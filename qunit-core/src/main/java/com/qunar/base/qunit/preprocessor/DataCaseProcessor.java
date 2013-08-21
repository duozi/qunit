package com.qunar.base.qunit.preprocessor;

import com.qunar.base.qunit.model.KeyValueStore;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.*;

/**
 * User: zonghuang
 * Date: 8/15/13
 */
public class DataCaseProcessor {

    private final static String DEFAULT = "default";

    public final static Map<String, Map<String, Map<String, String>>> dataCasesMap = new HashMap<String, Map<String, Map<String, String>>>();

    public static void parseDataCases(Element element) {
        Map<String, String> attributeMap = getAttributeMap(element);
        Iterator iterator = element.elementIterator();
        Map<String, String> defaultMap = null;
        Map<String, String> caseDataMap = null;
        while(iterator.hasNext()){
            Element row = (Element)iterator.next();
            if (DEFAULT.equals(row.getName())){
                defaultMap = getData(row);
            }else {
                caseDataMap = getData(row);
                caseDataMap.putAll(defaultMap);

                Map<String, String> dataCaseAttributeMap = getAttributeMap(row);
                String id = dataCaseAttributeMap.get("id");
                String executor = attributeMap.get("executor");
                Map<String, Map<String, String>> caseMap = new HashMap<String, Map<String, String>>();
                caseMap.put(id, caseDataMap);

                Map<String, Map<String, String>> checkMap = dataCasesMap.get(executor);
                if (checkMap == null){
                    dataCasesMap.put(executor, caseMap);
                } else{
                    checkMap.putAll(caseMap);
                    dataCasesMap.put(executor, checkMap);
                }
            }
        }
    }

    private static Map<String, String> getData(Element element){
        Iterator iterator = element.elementIterator();
        Map<String, String> defaultMap = new HashMap<String, String>();
        while (iterator.hasNext()){
            Element row = (Element)iterator.next();
            defaultMap.putAll(processRow(row));
        }

        return defaultMap;
    }

    private static Map<String, String> processRow(Element trRow) {
        Map<String, String> trMap = new HashMap<String, String>();
        String name = trRow.getName();
        if (StringUtils.isNotBlank(trRow.getText())){
            for (Iterator it = trRow.elementIterator(); it.hasNext();) {
                Element element = (Element)it.next();
                trMap.put(name + "." + element.getName(), element.getText());
            }
        }
        Map<String, String> attributeMap = getAttributeMap(trRow);
        trMap.putAll(addPrefix(attributeMap, trRow.getName()));

        return trMap;
    }

    private static Map<String, String> addPrefix(Map<String, String> original, String name){
        Iterator iterator = original.entrySet().iterator();
        Map<String, String> afterMap = new HashMap<String, String>();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry<String, String>)iterator.next();
            afterMap.put(name + "." + entry.getKey(), entry.getValue());
        }

        return afterMap;
    }

    private static List<KeyValueStore> getAttribute(Element element) {
        List<KeyValueStore> attributes = new ArrayList<KeyValueStore>();
        Iterator iterator = element.attributeIterator();
        while (iterator.hasNext()) {
            Attribute attribute = (Attribute) iterator.next();
            String attributeName = attribute.getName();
            String attributeValue = attribute.getValue();
            attributes.add(new KeyValueStore(attributeName, attributeValue));
        }
        return attributes;
    }

    public static Map<String, String> getAttributeMap(Element element) {
        List<KeyValueStore> attribute = getAttribute(element);
        return convertListKeyValueToMap(attribute);
    }

    private static Map<String, String> convertListKeyValueToMap(List<KeyValueStore> list) {
        Map<String, String> map = new HashMap<String, String>();
        for (KeyValueStore kvs : list) {
            map.put(kvs.getName(), (String) kvs.getValue());
        }
        return map;
    }
}
