package com.qunar.base.qunit.preprocessor;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Preprocessor {
    public abstract List<Node> prepare(Document document, Element instruction);

    protected List<Node> detach(Element element) {
        Element temp = element.createCopy();
        Iterator iterator = temp.elementIterator();
        List<Node> result = new ArrayList<Node>();
        while (iterator.hasNext()) {
            Element next = (Element) iterator.next();
            Node node = next.detach();
            result.add(node);
        }
        return result;
    }
}
