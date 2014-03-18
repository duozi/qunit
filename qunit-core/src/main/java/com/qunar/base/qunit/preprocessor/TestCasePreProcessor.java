/*
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */

package com.qunar.base.qunit.preprocessor;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class TestCasePreProcessor {
    private final static Map<String, Preprocessor> PREPROCESSORS = new HashMap<String, Preprocessor>();

    static {
        PREPROCESSORS.put("include", new IncludePreprocessor());
        PREPROCESSORS.put("step", new StepPreProcessor());
        PREPROCESSORS.put("data", new DataDrivenPreprocessor());
    }

    public Document prepare(String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = getCaseInputStream(fileName);
            Document document = getDocument(inputStream);
            if (!isValid(document)) return null;
            prepare(document.getRootElement());
            return document;
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private boolean isValid(Document document) {
        String rootName = document.getRootElement().getName();
        return rootName.equalsIgnoreCase("testcase");
    }

    private void prepare(Element parent) {
        Iterator iterator = parent.elementIterator();
        while (iterator.hasNext()) {
            Element currentNode = (Element) iterator.next();
            Preprocessor preprocessor = PREPROCESSORS.get(currentNode.getName());
            if (preprocessor != null && "data-case".equals(parent.getName())) {
                List<Node> newNodes = preprocessor.prepare(parent.getDocument(), currentNode);
                replace(iterator, parent, newNodes);
                prepare(parent);
            } else {
                prepare(currentNode);
            }
        }
    }

    private void replace(Iterator iterator, Element parent, List<Node> newNodes) {
        removeCurrent(iterator);
        List<Node> tail = removeTail(iterator);
        appendChildren(parent, newNodes);
        appendChildren(parent, tail);
    }

    private void removeCurrent(Iterator iterator) {
        iterator.remove();
    }

    private List<Node> removeTail(Iterator iterator) {
        List<Node> result = new ArrayList<Node>();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            iterator.remove();
            result.add(element);
        }
        return result;
    }

    private void appendChildren(Element parent, List<Node> children) {
        for (Node child : children) {
            parent.add(child);
        }
    }

    private InputStream getCaseInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(fileName);
    }

    private Document getDocument(InputStream inputStream) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(inputStream);
    }
}
