package com.qunar.base.qunit.preprocessor;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StepPreProcessor extends Preprocessor {
    private final static Logger logger = LoggerFactory.getLogger(StepPreProcessor.class);

    @Override
    public List<Node> prepare(Document document, Element instruction) {
        Map<String, Element> templates = getTemplates(document);
        String id = instruction.attributeValue("ref");
        Element template = templates.get(id);
        if (template == null) {
            String message = String.format("id为: %s的模版不存在", id);
            logger.error(message);
            throw new RuntimeException(message);
        }
        return detach(template);
    }

    private Map<String, Element> getTemplates(Document document) {
        Element root = document.getRootElement();
        Iterator iterator = root.elementIterator();
        Map<String, Element> result = new HashMap<String, Element>();
        while (iterator.hasNext()) {
            Element next = (Element) iterator.next();
            if (next.getName().equals("template")) {
                result.put(next.attributeValue("id"), next);
            }
        }
        return result;
    }
}
