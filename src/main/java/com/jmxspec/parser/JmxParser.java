package com.jmxspec.parser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jmxspec.model.JmxModel;

public class JmxParser {

    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private final Path path;
    private String testPlanName;
    private List<JmxModel.ThreadGroup> threadGroupList;
    private List<JmxModel.Argument> argumentList;

    public JmxParser(final Path path) {
        this.path = path;
    }

    public boolean parse() {
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(this.path.toFile());
            doc.getDocumentElement().normalize();

            this.testPlanName = parseTestPlanName(doc);
            this.threadGroupList = parseTestPlan(doc);
            this.argumentList = parseArguments(doc);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }        
    }

    public List<JmxModel.ThreadGroup> getThreadGroupList() {
        return this.threadGroupList;
    }

    public List<JmxModel.Argument> getArgumentList() {
        return this.argumentList;
    }

    public String getTestPlanName() {
        return this.testPlanName;
    }

    private String parseTestPlanName(final Document doc) {
        final NodeList testPlanList = doc.getElementsByTagName("TestPlan");
        final Element tgNode = (Element) testPlanList.item(0);
        return tgNode.getAttribute("testname");
    }

    private List<JmxModel.Argument> parseArguments(final Document doc) {
        final List<JmxModel.Argument> result = new ArrayList<>();
        if (doc == null) {
            return result;
        }

        final Element el = (Element) doc.getDocumentElement();

        final List<Element> elements = getCollectionProp(el, "Arguments.arguments");
        for (final Element element : elements) {
            final String name = getProp(element, "Argument.name");
            final String value = getProp(element, "Argument.value");
            final String metadata = getProp(element, "Argument.metadata");
            result.add(new JmxModel.Argument(name, value, metadata));
        }
        return result;
    }

    private List<JmxModel.ThreadGroup> parseTestPlan(final Document doc) {

        final List<JmxModel.ThreadGroup> result = new ArrayList<>();
        final NodeList threadGroupList = doc.getElementsByTagName("ThreadGroup");

        for (int i = 0; i < threadGroupList.getLength(); i++) {
            final Element tgNode = (Element) threadGroupList.item(i);
            final String name = tgNode.getAttribute("testname");
            final String comment = getProp(tgNode, "TestPlan.comments");
            final boolean delayedStart = parseBool(getProp(tgNode, "ThreadGroup.delayedStart"));
            final int users = parseInt(getProp(tgNode, "ThreadGroup.num_threads"));
            final int rampUp = parseInt(getProp(tgNode, "ThreadGroup.ramp_time"));
            final int duration = parseInt(getProp(tgNode, "ThreadGroup.duration"));
            final int delay = parseInt(getProp(tgNode, "ThreadGroup.delay"));
            final boolean itr = parseBool(getProp(tgNode, "ThreadGroup.same_user_on_next_iteration"));
            final boolean scheduler = parseBool(getProp(tgNode, "ThreadGroup.scheduler"));
            final String sampleError = getProp(tgNode, "ThreadGroup.on_sample_error");
            final int loop = parseInt(getProp(tgNode, "LoopController.loops"));
            final boolean continueForever = parseBool(getProp(tgNode, "LoopController.continue_forever"));
            final Node httpSamplerHashTree = getNextHashTree(tgNode);
            final List<JmxModel.HttpRequest> samplers = parseHttpSamplers(httpSamplerHashTree);
            final Node constantTimerHashTree = getNextHashTree(tgNode);
            final List<JmxModel.ConstantTimer> constantTimers = parseConstantTimers(constantTimerHashTree);
            final Node headerManagerHashTree = getNextHashTree(tgNode);
            final List<JmxModel.HeaderManager> headerManagers = parseHeaderManagers(headerManagerHashTree);
            result.add(new JmxModel.ThreadGroup(
                name, comment, delayedStart, users, rampUp, duration, delay, itr, scheduler, sampleError, loop, continueForever, samplers, constantTimers, headerManagers));
        }

        return result;
    }

    private List<JmxModel.HttpRequest> parseHttpSamplers(final Node hashTree) {

        final List<JmxModel.HttpRequest> result = new ArrayList<>();
        if (hashTree == null) {
            return result;
        }

       final  NodeList children = hashTree.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {

            final Node node = children.item(i);

            if (node instanceof Element) {
                final Element el = (Element) node;
                if ("HTTPSamplerProxy".equals(el.getNodeName())) {
                    final String name = el.getAttribute("testname");
                    final String comment = getProp(el, "TestPlan.comments");
                    final String domain = getProp(el, "HTTPSampler.domain");
                    final String port = getProp(el, "HTTPSampler.port");
                    final String protocol = getProp(el, "HTTPSampler.protocol");
                    final String encoding = getProp(el, "HTTPSampler.contentEncoding");
                    final String method = getProp(el, "HTTPSampler.method");
                    final String path = getProp(el, "HTTPSampler.path");
                    final boolean redirects =   parseBool(getProp(el, "HTTPSampler.follow_redirects"));
                    final boolean keepalive = parseBool(getProp(el, "HTTPSampler.use_keepalive"));
                    final boolean postBodyRaw = parseBool(getProp(el, "HTTPSampler.postBodyRaw"));
                    final List<JmxModel.HTTPsamplerArgument> arguments = parseHTTPsamplerArgument(el);
                    final List<JmxModel.HTTPsamplerFiles> files = parseFileStrings(el);
                    result.add(new JmxModel.HttpRequest(
                        name, comment, domain, port, protocol, encoding, method, path, redirects, keepalive, postBodyRaw, arguments, files));
                }
            }
        }
        return result;
    }

    private List<JmxModel.ConstantTimer> parseConstantTimers(final Node hashTree) {

        final List<JmxModel.ConstantTimer> result = new ArrayList<>();
        if (hashTree == null) {
            return result;
        }

       final  NodeList children = hashTree.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {

            final Node node = children.item(i);

            if (node instanceof Element) {
                final Element el = (Element) node;
                if ("ConstantTimer".equals(el.getNodeName())) {
                    final String name = el.getAttribute("testname");
                    final String comment = getProp(el, "TestPlan.comments");
                    final int delay = parseInt(getProp(el, "ConstantTimer.delay"));
                    result.add(new JmxModel.ConstantTimer(name, comment, String.valueOf(delay)));
                }
            }
        }

        return result;
    }

    private List<JmxModel.HTTPsamplerArgument> parseHTTPsamplerArgument(final Node hashTree) {
        final List<JmxModel.HTTPsamplerArgument> result = new ArrayList<>();
        if (hashTree == null || !(hashTree instanceof Element)) {
            return result;
        }

        final Element el = (Element) hashTree;

        final List<Element> elements = getCollectionProp(el, "Arguments.arguments");
        for (final Element element : elements) {
            final String name = getProp(element, "Argument.name");
            final String value = getProp(element, "Argument.value");
            final String metadata = getProp(element, "Argument.metadata");
            final boolean alwaysEncode = parseBool(getProp(element, "Argument.always_encode"));
            final boolean useEquals = parseBool(getProp(element, "Argument.use_equals"));
            result.add(new JmxModel.HTTPsamplerArgument(name, value, alwaysEncode, metadata, useEquals));
        }
        return result;
    }

    private List<JmxModel.HTTPsamplerFiles> parseFileStrings(final Node hashTree) {
        final List<JmxModel.HTTPsamplerFiles> result = new ArrayList<>();
        if (hashTree == null) {
            return result;
        }

       final  NodeList children = hashTree.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {

            final Node node = children.item(i);

            if (node instanceof Element) {
                final Element el = (Element) node;
                if ("elementProp".equals(el.getNodeName()) && "HTTPFileArgs".equals(el.getAttribute("elementType"))) {
                    final String paramName = getProp(el, "File.paramname");
                    final String path = getProp(el, "File.path");
                    final String mimeType = getProp(el, "File.mimetype");
                    result.add(new JmxModel.HTTPsamplerFiles(paramName, mimeType, path));
                }
            }
        }

        return result;
    }

    private List<JmxModel.HeaderManager> parseHeaderManagers(final Node hashTree) {
        final List<JmxModel.HeaderManager> result = new ArrayList<>();
        if (hashTree == null) {
            return result;
        }

       final  NodeList children = hashTree.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {

            final Node node = children.item(i);

            if (node instanceof Element) {
                final Element el = (Element) node;
                if ("HeaderManager".equals(el.getNodeName())) {
                    final String name = el.getAttribute("testname");
                    final String comment = getProp(el, "TestPlan.comments");
                    final List<JmxModel.Header> headers = parseHeaders(el);
                    result.add(new JmxModel.HeaderManager(name, comment, headers));
                }
            }
        }

        return result;
    }

    private List<JmxModel.Header> parseHeaders(final Node hashTree) {
        final List<JmxModel.Header> result = new ArrayList<>();
        if (hashTree == null || !(hashTree instanceof Element)) {
            return result;
        }

        final Element el = (Element) hashTree;

        final List<Element> elements = getCollectionProp(el, "HeaderManager.headers");
        for (final Element element : elements) {
            final String name = getProp(element, "Header.name");
            final String value = getProp(element, "Header.value");
            result.add(new JmxModel.Header(name, value));
        }
        return result;
    }

    private List<Element> getCollectionProp(final Node node, final String name) {
        final List<Element> elements = new ArrayList<>();
        if (node instanceof Element) {
            final Element element = (Element) node;
            if ("collectionProp".equals(node.getNodeName()) && name.equals(element.getAttribute("name"))) {
                for (int i = 0; i < element.getChildNodes().getLength(); i++) {
                    final Node child = element.getChildNodes().item(i);
                    if ("elementProp".equals(child.getNodeName())) {
                        elements.add((Element) child);
                    }
                }
                return elements;
            }
        }
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final List<Element> childElems = getCollectionProp(children.item(i), name);
            if (childElems.isEmpty()) {
                continue;
            }
            return childElems;
        }
        return elements; 
    }

    private String getProp(final Node node, final String name) {
        if (node instanceof Element) {
            final Element element = (Element) node;
            if (name.equals(element.getAttribute("name"))) {
                final String result = element.getTextContent().trim();
                return result;
            }
        }
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final String prop = getProp(children.item(i), name);
            if (prop == null) {
                continue;
            }
            return prop;
        }
        return null;
    }

    private Node getNextHashTree(final Node node) {
        Node next = node.getNextSibling();
        while (next != null) {
            if (next instanceof Element && "hashTree".equals(next.getNodeName())) {
                return next;
            }
            next = next.getNextSibling();
        }
        return null;
    }

    private int parseInt(final String str) {
        if (str == null) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    private boolean parseBool(final String str) {
        if (str == null) {
            return false;
        }
        return Boolean.parseBoolean(str);
    }
}
