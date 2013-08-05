package com.qunar.base.qunit.preprocessor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncludePreprocessor extends Preprocessor {
    private final static Logger logger = LoggerFactory.getLogger(IncludePreprocessor.class);

    private final static Map<String, Element> INCLUDED = new HashMap<String, Element>();

    public List<Node> prepare(Document document, Element instruction) {
        String fileName = instruction.attributeValue("file");
        Element element = loadXml(fileName);
        return detach(element);
    }

    private Element loadXml(String fileName) {
        Element element = INCLUDED.get(fileName);
        if (element != null) return element;
        String content = read(fileName);
        SAXReader reader = new SAXReader();
        StringReader stream = new StringReader(content);
        try {
            Element root = reader.read(stream).getRootElement();
            INCLUDED.put(fileName, root);
            return root;
        } catch (DocumentException e) {
            logger.error("读取include指令指定的文件失败", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String read(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("include的file不能为空");
        }

        InputStream inputStream = IncludePreprocessor.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            logger.error("无法读取include给定的文件，请检查文件路径");
            throw new RuntimeException("无法读取include给定的文件，请检查文件路径");
        }
        try {
            List<String> lines = IOUtils.readLines(inputStream);
            return merge(lines);
        } catch (IOException e) {
            logger.error("include 指令的file文件未找到", e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private String merge(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        sb.append("<include>");
        for (String line : lines) {
            sb.append(line);
        }
        sb.append("</include>");
        return sb.toString();
    }
}
