package com.qunar.base.qunit.casereader;

import com.qunar.base.qunit.preprocessor.XMLProcessor;
import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * User: zonghuang
 * Date: 3/24/14
 */
public class GlobalVariablesReader {

    //public final static String spliter = "$$@@$$";
    public final static String spliter = "@@$$@@";

    private final static Logger logger = LoggerFactory.getLogger(GlobalVariablesReader.class);

    public Map<String, Object> parse(String file) {
        Document document = loadDocument(file);
        if (document == null) {
            logger.info(file + "不是global文件");
            return Collections.EMPTY_MAP;
        }
        Element rootElement = document.getRootElement();
        List<Element> dataElements = rootElement.elements("data");
        List<Map<String, Object>> dataList = getData(dataElements);

        List setElements = rootElement.elements("set");
        Map<String, Object> setMap = getGlobalData(setElements);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", dataList);
        map.put("set", setMap);
        return map;
    }

    private Map<String, Object> getGlobalData (List<Element> setElements) {
        if (setElements == null) return Collections.EMPTY_MAP;
        Map<String, Object> globalParameters = new HashMap<String, Object>();
        for (Element aElement : setElements) {
            globalParameters.putAll(getParameter(aElement));
        }
        return globalParameters;
    }

    private Map<String, Object> getParameter(Element rootElement) {
        Iterator iterator = rootElement.elementIterator();
        Map<String, Object> parameters = new HashMap<String, Object>();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            if (element.isTextOnly()) {
                parameters.putAll(getParameterWithText(element));
            } else {
                parameters.putAll(getAttribute(element));
            }
        }
        return parameters;
    }

    private Map<String, Object> getParameterWithText(Element element) {
        Map<String, Object> attributes = getAttribute(element);
        Iterator iterator = attributes.entrySet().iterator();
        String key = "";
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if ("name".equals(entry.getKey())) {
                key = (String) entry.getValue();
                iterator.remove();
                break;
            }
        }
        if (StringUtils.isNotBlank(key)) {
            attributes.put(key, element.getTextTrim());
        }
        return attributes;
    }

    private Map<String, Object> getAttribute(Element element) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        Iterator iterator = element.attributeIterator();
        while (iterator.hasNext()) {
            Attribute attribute = (Attribute) iterator.next();
            String attributeName = attribute.getName();
            String attributeValue = attribute.getValue();
            attributes.put(attributeName, attributeValue);
        }
        return attributes;
    }

    private List<Map<String, Object>> getData(List<Element> dataElements) {
        if (dataElements == null) return Collections.EMPTY_LIST;
        String data = null;

        for (Element aElement : dataElements) {
            if (aElement.isTextOnly()) {
                data = processText(aElement.getText());
            } else if (aElement.selectSingleNode("tr/param") == null) {
                StringBuilder sb = new StringBuilder();
                processXmlData(aElement, sb);
                data = sb.toString();
            } else {
                StringBuilder sb = new StringBuilder();
                new XMLProcessor().processKeyValueXML(aElement, sb);
                data = sb.toString();
            }
        }
        return processData(data);
    }

    private List<Map<String, Object>> processData(String data) {
        if (StringUtils.isBlank(data)) return Collections.EMPTY_LIST;
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        String[] arrays = StringUtils.split(data.trim(), LineSeparator.Web);
        String[] titleArrays = StringUtils.split(arrays[0], spliter);
        for (int i = 1; i < arrays.length; i++) {
            if (StringUtils.isBlank(arrays[i])) continue;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            String[] valueArrays = StringUtils.split(arrays[i].trim(), spliter);
            for (int j = 0; j < valueArrays.length; j++) {
                valueMap.put(titleArrays[j], valueArrays[j]);
            }
            dataList.add(valueMap);
        }
        return dataList;
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

    private Document loadDocument(String fileName) {
        InputStream inputStream = null;
        try {
            URL url = this.getClass().getClassLoader().getResource(fileName);
            if (url == null) {
                throw new RuntimeException(String.format("全局配置文件不存在,file=<%s>", fileName));
            }
            String path = url.getPath();
            inputStream = getCaseInputStream(path);
            Document document = getDocument(inputStream);
            if (!isValid(document)) return null;
            return document;
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private InputStream getCaseInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(fileName);
    }

    private Document getDocument(InputStream inputStream) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(inputStream);
    }

    private boolean isValid(Document document) {
        String rootName = document.getRootElement().getName();
        return rootName.equalsIgnoreCase("global");
    }
}
