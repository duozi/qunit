package com.qunar.base.qunit.preprocessor;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.Iterator;
import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 10/16/12
 */
public class DataDrivenPreprocessor extends Preprocessor {
    //public final static String spliter = "$$@@$$";
    public final static String spliter = "@@$$@@";

    @Override
    public List<Node> prepare(Document document, Element instruction) {
        String data = null;
        if (instruction.isTextOnly()) {
            data = processText(instruction.getText());
        } else if(instruction.selectSingleNode("tr/param") == null){
            StringBuilder sb = new StringBuilder();
            processXmlData(instruction, sb);
            data = sb.toString();
        }else{
        	StringBuilder sb = new StringBuilder();
        	new XMLProcessor().processKeyValueXML(instruction, sb);
        	data = sb.toString();
        }
        instruction = clearChildren(instruction);
        Element element = instruction.addElement("examples");
        element.setText(data);
        return detach(instruction);
    }

    private String processText(String text) {
        if (StringUtils.isBlank(text)) return text;
        return StringUtils.replace(text, "|", spliter);
    }

    private void processXmlData(Element instruction, StringBuilder sb) {
        Iterator iterator = instruction.elementIterator();
        while (iterator.hasNext()) {
            Element row = (Element) iterator.next();
            processRow(row, sb);
        }
    }

    private void processRow(Element row, StringBuilder sb) {
        sb.append(spliter);
        Iterator lineIterator = row.elementIterator();
        while (lineIterator.hasNext()) {
            Element cell = (Element) lineIterator.next();
            sb.append(cell.getTextTrim());
            sb.append(spliter);
        }
        sb.append(LineSeparator.Web);
    }

    private Element clearChildren(Element instruction) {
        Iterator iterator = instruction.elementIterator();
        while (iterator.hasNext()) {
            Element next = (Element) iterator.next();
            next.detach();
        }
        return instruction;
    }
}
