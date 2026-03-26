package com.jmxspec.spec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jmxspec.model.JmxModel;

public class SpecBuilder {

    private static final String FILE_PATH = "spec.md";

    public static void toMarkdown(
        final String testPlanName, final List<JmxModel.Argument> arguments, final List<JmxModel.ThreadGroup> groups) {
        final StringBuilder sb = new StringBuilder();
        sb.append("# Test Plan: ").append(testPlanName).append("\n\n");
        sb.append(buildArguments(arguments));
        sb.append(buildThreadGroups(groups));   
        outPutFile(sb.toString());
    }

    private static String buildArguments(final List<JmxModel.Argument> arguments) {
        final StringBuilder sb = new StringBuilder();
        if (arguments.isEmpty()) {
            return "";
        }
        sb.append("## User Defined Variables\n\n");
        for (final JmxModel.Argument arg : arguments) {
            sb.append("* ").append(arg.name).append(" : ").append(arg.value).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    private static String buildThreadGroups(final List<JmxModel.ThreadGroup> groups) {
        final StringBuilder sb = new StringBuilder();
        for (final JmxModel.ThreadGroup tg : groups) {
            sb.append("## ThreadGroup: ").append(tg.name).append("\n\n");
            appendLineIfNeeded(sb, "* comment : ", tg.comment);
            sb.append("* users : ").append(tg.users).append("\n");
            sb.append("* ramp-up : ").append(tg.rampUp).append(" sec\n");
            final String loop = tg.loop < 0 ? "Infinite" : Integer.toString(tg.loop);
            sb.append("* loop : ").append(loop).append("\n");
            if (tg.scheduler) {
                sb.append("* duration : ").append(tg.duration).append(" sec\n");
                sb.append("* start delay : ").append(tg.delay).append(" sec\n");
            }
            if (tg.delayedStart) {
                sb.append("* start : delayed\n");
            }
            if (tg.itr) {
                sb.append("* session : reuse\n");
            }
            
            sb.append("* on error : ");
            switch (tg.sampleError) {
                case "continue":
                    sb.append("continue\n");
                    break;
                case "startnextloop":
                    sb.append("start next loop\n");
                    break;
                case "stoptestnow":
                    sb.append("stop test now\n");
                    break;
                case "stoptest":
                    sb.append("stop test\n");
                    break;
                case "stopthread":
                    sb.append("stop thread\n");
                    break;
                default:
                    break;
            }
            sb.append("\n");
            sb.append(buildHttpRequestHeaderManeger(tg));
            sb.append(buildHttpRequests(tg)).append("\n");
            sb.append(buildConstantTimers(tg)).append("\n");
        }
        return sb.toString();
    }

    private static String buildHttpRequestHeaderManeger(final JmxModel.ThreadGroup group) {
        final StringBuilder sb = new StringBuilder();
        for (final JmxModel.HeaderManager headerManager : group.headerManagers) {
            sb.append("### Header Manager: ").append(headerManager.name).append("\n\n");
            sb.append("* comment : ").append(headerManager.comment).append("\n");
            sb.append("* headers : \n");
            for (final JmxModel.Header header : headerManager.headers) {
                sb.append("  * ").append(header.name).append(" : ").append(header.value).append("\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    private static String buildHttpRequests(final JmxModel.ThreadGroup group) {
        final StringBuilder sb = new StringBuilder();
        for (final JmxModel.HttpRequest req : group.requests) {
            sb.append("### ");
            sb.append(req.method).append(" ");
            sb.append(req.protocol).append("://");
            sb.append(req.domain);
            if (req.port != null && !req.port.isEmpty()) {
                sb.append(":").append(req.port);
            }
            sb.append("/").append(req.path);
            if (!req.arguments.isEmpty()) {
                if (req.postBodyRaw) {
                    sb.append("\n");
                    if (req.arguments.size() == 1) {
                        sb.append("#### body");
                        try {
                            sb.append("\n\n```json\n").append(prettyJson(req.arguments.get(0).value)).append("\n```\n");
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    sb.append("?");
                    final StringJoiner sj = new StringJoiner("&");
                    req.arguments.stream()
                        .filter(arg -> arg.metaData != null && arg.metaData.equals("="))
                        .forEach(arg -> sj.add(arg.name + "=" + arg.value));
                    sb.append(sj.toString()).append("\n");
                }
            }
            sb.append("\n");
            appendLineIfNeeded(sb, "* name : ", req.name);
            appendLineIfNeeded(sb, "* comment : ", req.comment);
            appendLineIfNeeded(sb, "* encoding : ", req.encoding);
            if (req.redirects) {
                sb.append("* follow Redirects : yes\n");
            } else {
                sb.append("* follow Redirects : no\n");
            }
            if (req.keepAlive) {
                sb.append("* keep Alive : enabled\n");
            } else {
                sb.append("* keep Alive : disabled\n");
            }
            if (req.files != null && !req.files.isEmpty()) {
                sb.append("\n");
                sb.append("#### files\n");
                for (final JmxModel.HTTPsamplerFiles file : req.files) {
                    sb.append("* ").append(file.paramName).append(" : ").append(file.path).append(" (").append(file.mimeType).append(")");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String appendLineIfNeeded(final StringBuilder sb, final String prefix, final String value) {
        if (value != null && !value.isEmpty()) {
            sb.append(prefix).append(value).append("\n");
        }
        return sb.toString();
    }

    private static String buildConstantTimers(final JmxModel.ThreadGroup group) {
        final StringBuilder sb = new StringBuilder();
        for (final JmxModel.ConstantTimer timer : group.constantTimers) {
            sb.append("### Constant Timer: ").append(timer.name).append("\n");
            sb.append("* comment : ").append(timer.comment).append("\n");
            sb.append("* think time : ").append(timer.delay).append(" ms\n");
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String prettyJson(final String rawJson) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final Object json = mapper.readValue(rawJson, Object.class);
        return mapper
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(json);
    }

    private static void outPutFile(final String markdownStr) {
        try {
            System.out.println(markdownStr);
            Files.write(Paths.get(FILE_PATH), markdownStr.getBytes(StandardCharsets.UTF_8));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
