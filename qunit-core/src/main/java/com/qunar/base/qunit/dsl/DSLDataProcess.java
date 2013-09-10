package com.qunar.base.qunit.dsl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class DSLDataProcess {
	
	public final static Map<String, Map<String, Map<String, Object>>> dataMap = new HashMap<String, Map<String, Map<String, Object>>>();

	public final static String PARAM = "param";
	
	public final static String TR = "tr";

	public void processData(String defId, Element instruction) {

		Iterator iterator = instruction.elementIterator();
		Map<String, Map<String, Object>> trMap = new HashMap<String, Map<String, Object>>();
		while (iterator.hasNext()) {
			Element row = (Element) iterator.next();
			if (TR.equals(row.getName())) {
				String id = getId(row);
				Map<String, Object> rowMap = processRow(row);
				trMap.put(id, rowMap);
			}
		}
		
		dataMap.put(defId, trMap);
	}


	private Map<String, Object> processRow(Element trRow) {
		Iterator lineIterator = trRow.elementIterator();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		while (lineIterator.hasNext()) {
			Element row = (Element) lineIterator.next();
			if (!PARAM.equals(row.getName())) {
				return null;
			}
			if (StringUtils.isBlank(row.getText())) {
				for (Iterator it = row.attributeIterator(); it.hasNext();) {
					Attribute attribute = (Attribute) it.next();
					paramMap.put(attribute.getName(), attribute.getValue());
				}
			} else {
				Iterator it = row.attributeIterator();
				if (it.hasNext()) {
					Attribute attribute = (Attribute) it.next();
					paramMap.put(attribute.getValue(), row.getTextTrim());
				}
			}
		}
		return paramMap;
	}

	private String getId(Element row){
		for (Iterator it = row.attributeIterator(); it.hasNext();) {
			Attribute attribute = (Attribute) it.next();
			if ("id".equals(attribute.getName())){
				return attribute.getValue();
			}
		}
		
		return null;
	}

}
