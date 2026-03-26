package com.jmxspec;

import java.nio.file.Path;
import java.util.List;

import com.jmxspec.model.JmxModel;
import com.jmxspec.parser.JmxParser;
import com.jmxspec.spec.SpecBuilder;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="jmx-spec")
public class App implements Runnable {

    @Parameters
    Path path;

    public void run() {
        System.out.println("Processing: " + path.getFileName());
        final JmxParser jmxParser =  new JmxParser(path);
        if (jmxParser.parse()) {
            final String testPlanName = jmxParser.getTestPlanName();
            final List<JmxModel.Argument> arguments = jmxParser.getArgumentList();
            final List<JmxModel.ThreadGroup> threadGroups = jmxParser.getThreadGroupList();
            SpecBuilder.toMarkdown(testPlanName, arguments, threadGroups);
        }
    }

    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: jmx-spec <file>");
            return;
        }
        new CommandLine(new App()).execute(args);
    }
}