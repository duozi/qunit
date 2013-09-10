package com.qunar.base.qunit.preprocessor;

import com.qunar.base.qunit.dsl.DSLParamParse;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Element;

import java.util.*;

import static com.qunar.base.qunit.util.XMLUtils.getAttributeMap;

/**
 * User: zonghuang
 * Date: 8/15/13
 */
public class DataCaseProcessor {

    private final static String DEFAULT = "default";

    public final static Map<String, Map<String, Map<String, Object>>> dataCasesMap = new HashMap<String, Map<String, Map<String, Object>>>();

    public static void parseDataCases(Element element, Map<String, String> keyMap, List<String> dslFiles) {
        Map<String, String> attributeMap = getAttributeMap(element);
        Iterator iterator = element.elementIterator();
        Map<String, List<Map<String,String>>> defaultMap = null;
        Map<String, List<Map<String,String>>> orginCaseDataMap = null;
        Map<String, Object> caseDataMap = null;
        DSLParamParse dslParamParse = new DSLParamParse();
        while(iterator.hasNext()){
            Element row = (Element)iterator.next();
            if (DEFAULT.equals(row.getName())){
                defaultMap = getData(row);
            }else {
                orginCaseDataMap = getData(row);
                mergeMap(orginCaseDataMap, defaultMap, keyMap);

                Map<String, String> dataCaseAttributeMap = getAttributeMap(row);
                String id = dataCaseAttributeMap.get("id");
                String executor = attributeMap.get("executor");

                Map<String, Set<String>> dslParamMap = dslParamParse.getParamMap(executor, dslParamParse.read(dslFiles));
                caseDataMap = processData(orginCaseDataMap, dslParamMap);

                Map<String, Map<String, Object>> caseMap = new HashMap<String, Map<String, Object>>();
                caseMap.put(id, caseDataMap);

                Map<String, Map<String, Object>> checkMap = dataCasesMap.get(executor);
                if (checkMap == null){
                    dataCasesMap.put(executor, caseMap);
                } else{
                    checkMap.putAll(caseMap);
                    dataCasesMap.put(executor, checkMap);
                }
            }
        }
    }

    private static Map<String, List<Map<String, String>>> mergeMap(Map<String, List<Map<String, String>>> caseMap, Map<String, List<Map<String, String>>> defaultMap, Map<String, String> keyMap){
        Iterator iterator = defaultMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<Map<String, String>>> entry = (Map.Entry<String, List<Map<String, String>>>) iterator.next();
            String key = null;
            if (keyMap != null){
                key = keyMap.get(entry.getKey());
            }
            if (key == null){
                mergeNoKeyList(caseMap, entry);
            } else {
                checkDuplicateKey(key, entry.getValue());
                mergeKeyList(caseMap, entry, key);
            }
        }

        return caseMap;
    }

    private static void mergeNoKeyList(Map<String, List<Map<String, String>>> caseMap, Map.Entry<String, List<Map<String, String>>> entry){
        List<Map<String, String>> caseList = caseMap.get(entry.getKey());
        if (caseList == null){
            caseMap.put(entry.getKey(), entry.getValue());
        } else {
            List<Map<String, String>> defaultList = entry.getValue();
            caseMap.put(entry.getKey(), replaceList(caseList, defaultList));

        }
    }

    private static List<Map<String, String>> replaceList(List<Map<String, String>> caseList, List<Map<String, String>> defaultList){
        List<Map<String, String>> newCaseList = new ArrayList<Map<String, String>>();
        for (Map<String, String> cMap : caseList){
            for (Map<String, String> dMap : defaultList){
                Map<String, String> newMap = copyMap(dMap);
                replaceMap(newMap, cMap);
                newCaseList.add(newMap);
            }
        }
        return newCaseList;
    }

    private static void replaceMap(Map<String, String> newMap, Map<String, String> cMap){
        Iterator iterator = cMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            newMap.put(entry.getKey(), entry.getValue());
        }
    }

    private static void mergeKeyList(Map<String, List<Map<String, String>>> caseMap, Map.Entry<String, List<Map<String, String>>> entry, String key) {
        List<Map<String, String>> caseList = caseMap.get(entry.getKey());
        List<Map<String, String>> newCaseList = new ArrayList<Map<String, String>>();
        if (caseList == null) {
            caseMap.put(entry.getKey(), entry.getValue());
        } else {
            List<Map<String, String>> defaultList = entry.getValue();
            for (Map<String, String> dMap : defaultList) {
                boolean equal = false;
                for (Map<String, String> cMap : caseList) {
                    if (dMap.get(key) != null && dMap.get(key).equalsIgnoreCase(cMap.get(key))) {
                        Map<String, String> tempMap = copyMap(dMap);
                        replaceMap(tempMap, cMap);
                        newCaseList.add(tempMap);
                        equal = true;
                        break;
                    }
                }
                if (equal) continue;
                newCaseList.add(dMap);
            }
            newCaseList.addAll(getDiff(caseList, defaultList, key));
            caseMap.put(entry.getKey(), newCaseList);
        }
    }

    private static List<Map<String, String>> getDiff(List<Map<String, String>> caseMap, List<Map<String, String>> defaultMap, String key){
        List<Map<String, String>> diffList = new ArrayList<Map<String, String>>();
        for (Map<String, String> cMap : caseMap){
            boolean equal = false;
            for (Map<String, String> dMap : defaultMap){
                if (cMap.get(key) != null && cMap.get(key).equals(dMap.get(key))){
                    equal = true;
                    break;
                }
            }
            if (equal) continue;
            diffList.add(cMap);
        }
        return diffList;
    }

    private static void checkDuplicateKey(String key, List<Map<String, String>> dataList){
        if (CollectionUtils.isEmpty(dataList)){
            return ;
        }
        List<String> keyList = new ArrayList<String>();
        for (Map<String, String> aMap : dataList){
            String value = aMap.get(key);
            if (value == null) continue;
            if (keyList.contains(value)){
                throw new RuntimeException("存在相同的key:{}");
            } else {
                keyList.add(value);
            }
        }
    }

    private static Map<String, List<Map<String,String>>> getData(Element element){
        Iterator iterator = element.elementIterator();
        Map<String, List<Map<String, String>>> dataCaseMap = new HashMap<String, List<Map<String, String>>>();
        while (iterator.hasNext()){
            Element row = (Element)iterator.next();
            Map<String, String> trMap = processRow(row);
            List<Map<String, String>> listMap = dataCaseMap.get(row.getName());
            if (CollectionUtils.isEmpty(listMap)){
                listMap = new ArrayList<Map<String, String>>();
            }
            listMap.add(trMap);
            dataCaseMap.put(row.getName(), listMap);
        }

        return dataCaseMap;
    }

    private static Map<String, Object> processData(Map<String, List<Map<String, String>>> dataCaseMap, Map<String, Set<String>> dslParamMap){
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator iterator = dataCaseMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<Map<String, String>>> entry = (Map.Entry<String, List<Map<String, String>>>)iterator.next();
            int count = entry.getValue() == null ? 0 : entry.getValue().size();
            String name = entry.getKey();
            map.putAll(parseList(entry.getKey(), entry.getValue()));

            processOtherParam(map, count, name, dslParamMap.get(name));
        }

        return convertOneListToString(map);
    }

    private static Map<String, Object> convertOneListToString(final Map<String, Object> map){
        if (map == null){
            return Collections.EMPTY_MAP;
        }
        Map<String, Object> newMap = new HashMap<String, Object>();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
            List<String> list = (ArrayList)entry.getValue();
            if (list.size() == 1){
                newMap.put(entry.getKey(), list.get(0));
            } else {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }

        return newMap;
    }

    private static void processOtherParam(Map<String, Object> map, int count, String name, Set<String> dslParamSet){
        if (dslParamSet == null){
            return;
        }
        Iterator iterator = dslParamSet.iterator();
        while (iterator.hasNext()){
            String param = (String) iterator.next();
            String key = name + "." + param;
            if (map.get(key) == null){
                List<String> valueList = generateValue(count);
                map.put(key, valueList);
            }
        }

    }

    private static List<String> generateValue(int count){
        List<String> valueList = new ArrayList<String>();
        for (int i = 0; i < count; i++){
            valueList.add("[null]");
        }
        return valueList;
    }

    private static Map<String, List<String>> parseList(String name, List<Map<String, String>> mapList){
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        if (CollectionUtils.isEmpty(mapList)){
            return null;
        }
        for (int i = 0; i < mapList.size(); i++){
            Map<String, String> orginMap = mapList.get(i);
            parseMap(orginMap, map, i, name);
        }
        checkNumber(map, mapList.size());

        return map;
    }

    private static void checkNumber(Map<String, List<String>> map, int count){
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) iterator.next();
            List<String> valueList = entry.getValue();
            if (valueList.size() != count){
                addList(valueList, count);
            }
            map.put(entry.getKey(), valueList);
        }
    }

    private static void parseMap(Map<String, String> orginMap, Map<String, List<String>> map, int index, String name){
        Iterator iterator = orginMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            String key = name + "." + entry.getKey();
            List<String> valueList = map.get(key);
            if (valueList == null){
                valueList = new ArrayList<String>();
                addList(valueList, index);
            } else if (valueList.size() != index){
                addList(valueList, index);
            }
            valueList.add(entry.getValue());
            map.put(key, valueList);
        }
    }

    private static void addList(List<String> valueList, int index){
        while (index - valueList.size() > 0){
            valueList.add("[null]");
        }
    }

    private static Map<String, String> processRow(Element trRow) {
        Map<String, String> trMap = new HashMap<String, String>();
        Iterator iterator = trRow.elementIterator();
        while (iterator.hasNext()){
            Element element = (Element)iterator.next();
            trMap.put(element.getName(), element.getText());
        }
        trMap.putAll(getAttributeMap(trRow));

        return trMap;
    }

    private static Map<String, String> copyMap(Map<String, String> srcMap){
        Map<String, String> destMap = new HashMap<String, String>();
        if (srcMap == null){
            return destMap;
        }
        Iterator iterator = srcMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            destMap.put(entry.getKey(), entry.getValue());
        }

        return  destMap;
    }
}
