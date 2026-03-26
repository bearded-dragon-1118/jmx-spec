package com.jmxspec.model;

import java.util.List;

public class JmxModel {

    public static class Argument {
        public final String name;
        public final String value;
        public final String metaData;

        public Argument(
            final String name,
            final String value,
            final String metaData) {

            this.name = name;
            this.value = value;
            this.metaData = metaData;
        }
    }

    public static class ThreadGroup {

        public final String name;
        public final String comment;
        public final boolean delayedStart;
        public final int users;
        public final int rampUp;
        public final int duration;
        public final int delay;
        public final boolean itr;
        public final boolean scheduler;
        public final String sampleError;
        public final int loop;
        public final boolean continueForever;
        public final List<HttpRequest> requests;
        public final List<ConstantTimer> constantTimers;
        public final List<HeaderManager> headerManagers;

        public ThreadGroup(
            final String name,
            final String comment,
            final boolean delayedStart,
            final int users,
            final int rampUp,
            final int duration,
            final int delay,
            final boolean itr,
            final boolean scheduler,
            final String sampleError,
            final int loop,
            final boolean continueForever,
            final List<HttpRequest> requests,
            final List<ConstantTimer> constantTimers,
            final List<HeaderManager> headerManagers) {

            this.name = name;
            this.comment = comment;
            this.delayedStart = delayedStart;
            this.users = users;
            this.rampUp = rampUp;
            this.duration = duration;
            this.delay = delay;
            this.itr = itr;
            this.scheduler = scheduler;
            this.sampleError = sampleError;
            this.loop = loop;
            this.continueForever = continueForever;
            this.requests = requests;
            this.constantTimers = constantTimers;
            this.headerManagers = headerManagers;
        }
    }

    public static class HTTPsamplerArgument {
        public final String name;
        public final String value;
        public final boolean awaitsEncoding;
        public final String metaData;
        public final boolean useEquals;
        
        public HTTPsamplerArgument(
            final String name,
            final String value,
            final boolean awaitsEncoding,
            final String metaData,
            final boolean useEquals) {

            this.name = name;
            this.value = value;
            this.awaitsEncoding = awaitsEncoding;
            this.metaData = metaData;
            this.useEquals = useEquals;
        }
    }

    public static class HTTPsamplerFiles {

        public final String paramName;
        public final String mimeType;
        public final String path;

        public HTTPsamplerFiles(
            final String paramName,
            final String mimeType,
            final String path) {

            this.paramName = paramName;
            this.mimeType = mimeType;
            this.path = path;
        }
    }

    public static class HttpRequest {

        public final String name;
        public final String comment;
        public final String domain;
        public final String port;
        public final String protocol;
        public final String encoding;
        public final String method;
        public final String path;
        public final boolean redirects;
        public final boolean keepAlive;
        public final boolean postBodyRaw;
        public final List<HTTPsamplerArgument> arguments;
        public final List<HTTPsamplerFiles> files;

        public HttpRequest(
            final String name,
            final String comment,
            final String domain,
            final String port,
            final String protocol,
            final String encoding,
            final String method,
            final String path,
            final boolean redirects,
            final boolean keepAlive,
            final boolean postBodyRaw,
            final List<HTTPsamplerArgument> arguments,
            final List<HTTPsamplerFiles> files) {

            this.name = name;
            this.comment = comment;
            this.domain = domain;
            this.port = port;
            this.protocol = protocol;
            this.encoding = encoding;
            this.method = method;
            this.path = path;
            this.redirects = redirects;
            this.keepAlive = keepAlive;
            this.postBodyRaw = postBodyRaw;
            this.arguments = arguments;
            this.files = files; 
        }
    }

    public static class ConstantTimer {
        public final String name;
        public final String comment;
        public final String delay;

        public ConstantTimer(
            final String name,
            final String comment,
            final String delay) {

            this.name = name;
            this.comment = comment;
            this.delay = delay;
        }
    }

    public static class Header {
        public final String name;
        public final String value;

        public Header(
            final String name,
            final String value) {

            this.name = name;
            this.value = value;
        }
    }

    public static class HeaderManager {
        public final String name;
        public final String comment;
        public final List<Header> headers;

        public HeaderManager(
            final String name,
            final String comment,
            final List<Header> headers) {

            this.name = name;
            this.comment = comment;
            this.headers = headers;
        }
    }
}
