package com.qunar.base.qunit.dsl;

import com.qunar.base.qunit.util.XMLUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: zonghuang
 * Date: 9/4/13
 */
public class DSLParamParse {

    private final static Logger logger = LoggerFactory.getLogger(DSLParamParse.class);

    private static final Pattern pattern = Pattern.compile("\\$\\{?([a-zA-Z0-9_\\.]*)\\}?");

    private Map<String, Set<String>> paramMap = new HashMap<String, Set<String>>();

    public Map<String, Set<String>> read(List<String> fileNames) {
        if (CollectionUtils.isEmpty(fileNames)){
            return Collections.emptyMap();
        }
        for (String fileName : fileNames){
            if (StringUtils.isBlank(fileName)) continue;
            try {
                Document document = load(fileName);
                processDsl(document);
            } catch (FileNotFoundException e) {
                logger.error("指定的DSL命令配置文件不存在", fileName, e);
            } catch (DocumentException e) {
                logger.error("DSL命令定义文件格式错误，是非法的xml文档,file={}", fileName, e);
            }
        }

        return paramMap;
    }

    private void processDsl(Document document){
        Iterator elementIterator = document.getRootElement().elementIterator();
        while (elementIterator.hasNext()){
            Element defElement = (Element) elementIterator.next();
            processDef(defElement);
        }

    }

    private void processDef(Element document){
        Iterator iterator = document.elementIterator();
        while (iterator.hasNext()){
            Element element = (Element) iterator.next();
            Map<String, String> attributeMap = XMLUtils.getAttributeMap(element);
            Collection<String> values = attributeMap.values();
            processParams(values);
            if (StringUtils.isNotBlank(element.getTextTrim())){
                processParam(element.getTextTrim());
            } else{
                processDef(element);
            }
        }
    }

    private void processParams(Collection<String> values){
        if (CollectionUtils.isEmpty(values)){
            return;
        }
        for (String value : values){
            processParam(value);
        }
    }

    private void processParam(String value){
        if (StringUtils.isBlank(value)){
            return;
        }
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()){
            String matchResult = matcher.group(1);
            String[] array = StringUtils.split(matchResult, ".");
            if (array.length == 2){
                save(array[0], array[1]);
            }
        }
    }

    private void save(String key, String value){
        Set<String> valueSet = paramMap.get(key);
        if (valueSet == null){
            valueSet = new HashSet<String>();
        }
        valueSet.add(value);
        paramMap.put(key, valueSet);
    }

    private Document load(String fileName) throws FileNotFoundException, DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(new FileInputStream(fileName));
    }
}
