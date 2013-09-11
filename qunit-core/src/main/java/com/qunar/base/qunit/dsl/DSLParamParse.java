package com.qunar.base.qunit.dsl;

import com.qunar.base.qunit.command.CommandFactory;
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

    public Map<String, Set<String>> read(List<String> fileNames) {
        if (CollectionUtils.isEmpty(fileNames)) {
            return Collections.emptyMap();
        }
        Map<String, Map<String, Set<String>>> paramMap = new HashMap<String, Map<String, Set<String>>>();
        for (String fileName : fileNames) {
            if (StringUtils.isBlank(fileName)) continue;
            try {
                Document document = load(fileName);
                paramMap.putAll(processDsl(document));
            } catch (FileNotFoundException e) {
                logger.error("指定的DSL命令配置文件不存在", fileName, e);
            } catch (DocumentException e) {
                logger.error("DSL命令定义文件格式错误，是非法的xml文档,file={}", fileName, e);
            }
        }

        return getFinallyMap(paramMap);
    }

    private Map<String, Set<String>> getFinallyMap(final Map<String, Map<String, Set<String>>> orginMap){
        if (orginMap == null){
            return Collections.EMPTY_MAP;
        }
        Map<String, Set<String>> paramMap = new HashMap<String, Set<String>>();
        Iterator iterator = orginMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Map<String, Set<String>>> entry = (Map.Entry<String, Map<String, Set<String>>>) iterator.next();
            paramMap.put(entry.getKey(), getParamByDef(entry.getValue(), orginMap));
        }
        return paramMap;
    }

    private Set<String> getParamByDef(final Map<String, Set<String>> childMap, final Map<String, Map<String, Set<String>>> orginMap){
        if (childMap == null){
            return Collections.EMPTY_SET;
        }
        Set<String> paramSet = new HashSet<String>();
        Iterator iterator = childMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Set<String>> entry = (Map.Entry<String, Set<String>>) iterator.next();
            if (CommandFactory.getInstance().getConfig(entry.getKey()) == null){
                paramSet.addAll(getParamByDef(orginMap.get(entry.getKey()), orginMap));
            }
            paramSet.addAll(entry.getValue());
        }
        return paramSet;
    }

    public Map<String, Set<String>> getParamMap(String executor, Map<String, Set<String>> wholeMap){
        if (StringUtils.isBlank(executor) || wholeMap == null) {
            return Collections.emptyMap();
        }
        Set<String> paramSet = wholeMap.get(executor);
        if (paramSet == null) {
            throw new RuntimeException("DSL " + executor + "不存在");
        }

        return processParams(paramSet);
    }

    private Map<String, Map<String, Set<String>>> processDsl(Document document) {
        Map<String, Map<String, Set<String>>> dslMap = new HashMap<String, Map<String, Set<String>>>();
        Iterator elementIterator = document.getRootElement().elementIterator();
        while (elementIterator.hasNext()) {
            Element defElement = (Element) elementIterator.next();
            Map<String, String> attributeMap = XMLUtils.getAttributeMap(defElement);
            String id = attributeMap.get("id");
            if (id == null){
                throw new RuntimeException("DSL id不存在");
            }
            dslMap.put(id, processDef(defElement));
        }

        return dslMap;
    }

    private Map<String, Set<String>> processDef(Element document) {
        Iterator iterator = document.elementIterator();
        Map<String, Set<String>> defMap = new HashMap<String, Set<String>>();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            defMap.put(element.getName(), processUnit(element));
        }

        return defMap;
    }

    private Set<String> processUnit(Element document){
        Set<String> defSet = new HashSet<String>();
        Map<String, String> attributeMap = XMLUtils.getAttributeMap(document);
        defSet.addAll(convertMapValueToSet(attributeMap));
        if (StringUtils.isNotBlank(document.getTextTrim())) {
            defSet.add(document.getTextTrim());
        } else {
            Iterator iterator = document.elementIterator();
            while (iterator.hasNext()) {
                Element element = (Element) iterator.next();
                defSet.addAll(processUnit(element));
            }
        }
        return defSet;
    }

    private Set<String> convertMapValueToSet(Map<String, String> map){
        if (map == null){
            return Collections.EMPTY_SET;
        }
        Set<String> paramSet = new HashSet<String>();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            paramSet.add(entry.getValue());

        }
        return paramSet;
    }

    private Map<String, Set<String>> processParams(Set<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }
        Map<String, Set<String>> paramMap = new HashMap<String, Set<String>>();
        for (String value : values) {
            processParam(value, paramMap);
        }

        return paramMap;
    }

    private void processParam(String value, Map<String, Set<String>> defMap) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String matchResult = matcher.group(1);
            String[] array = StringUtils.split(matchResult, ".");
            if (array.length == 2) {
                save(array[0], array[1], defMap);
            }
        }
    }

    private void save(String key, String value, Map<String, Set<String>> defMap) {
        Set<String> valueSet = defMap.get(key);
        if (valueSet == null) {
            valueSet = new HashSet<String>();
        }
        valueSet.add(value);
        defMap.put(key, valueSet);
    }

    private Document load(String fileName) throws FileNotFoundException, DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(new FileInputStream(fileName));
    }
}
