package com.qunar.base.qunit.preprocessor;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.*;

public class XMLProcessor {

	public final static String splitter = DataDrivenPreprocessor.spliter;

	public final static String DEFAULT = "default";

	public final static String TR = "tr";

	public final static String PARAM = "param";

	public void processKeyValueXML(Element instruction, StringBuilder sb) {

		Set<String> titleset = getTitle(instruction);
		if (CollectionUtils.isEmpty(titleset)) {
			return;
		}
		sb.append(splitter);
		sb.append(StringUtils.join(titleset, splitter));
		sb.append(splitter);
		sb.append(LineSeparator.Web);

		Iterator iterator = instruction.elementIterator();
		Map<String, String> componentMap = new HashMap<String, String>();
		while (iterator.hasNext()) {
			Element row = (Element) iterator.next();
			if (DEFAULT.equals(row.getName())) {
				componentMap = processRow(row);
			} else if (TR.equals(row.getName())) {
				Map<String, String> trMap = processRow(row);
				Map<String, String> tempMap = copyMap(componentMap);
				tempMap.putAll(trMap);
				generateBuilder(tempMap, titleset, sb);
			}
		}
	}

	private void processRow(Element trRow, Set<String> titleSet) {
		Iterator lineIterator = trRow.elementIterator();
		while (lineIterator.hasNext()) {
			Element row = (Element) lineIterator.next();
			if (!PARAM.equals(row.getName())) {
				return;
			}
			if (StringUtils.isBlank(row.getText())) {
				for (Iterator it = row.attributeIterator(); it.hasNext();) {
					Attribute attribute = (Attribute) it.next();
					titleSet.add(attribute.getName());
				}
			} else {
				Iterator it = row.attributeIterator();
				if (it.hasNext()) {
					Attribute attribute = (Attribute) it.next();
					titleSet.add(attribute.getValue());
				}
			}
		}
	}

	private Map<String, String> processRow(Element trRow) {
		Iterator lineIterator = trRow.elementIterator();
		Map<String, String> rowMap = new HashMap<String, String>();
		while (lineIterator.hasNext()) {
			Element row = (Element) lineIterator.next();
			if (!PARAM.equals(row.getName())) {
				return null;
			}
			if (StringUtils.isBlank(row.getText())) {
				for (Iterator it = row.attributeIterator(); it.hasNext();) {
					Attribute attribute = (Attribute) it.next();
					rowMap.put(attribute.getName(), attribute.getValue());
				}
			} else {
				Iterator it = row.attributeIterator();
				if (it.hasNext()) {
					Attribute attribute = (Attribute) it.next();
					rowMap.put(attribute.getValue(), row.getTextTrim());
				}
			}
		}
		return rowMap;
	}

	private Set<String> getTitle(Element instruction) {
		Set<String> titleSet = new HashSet<String>();
		Iterator iterator = instruction.elementIterator();
		while (iterator.hasNext()) {
			Element row = (Element) iterator.next();
			if (DEFAULT.equals(row.getName())) {
				processRow(row, titleSet);
			}
			if (TR.equals(row.getName())) {
				processRow(row, titleSet);
				//break;
			}
		}
		return titleSet;
	}

	private void generateBuilder(Map<String, String> map, Set<String> titleset,
			StringBuilder sb) {
        Iterator iterator = titleset.iterator();
        sb.append(splitter);
        while (iterator.hasNext()) {
            String title = (String) iterator.next();
            if (map.containsKey(title)) {
			    sb.append(map.get(title));
            } else {
                sb.append("${").append(title).append("}");
            }
            sb.append(splitter);
        }
		sb.append(LineSeparator.Web);
	}

	private Map<String, String> copyMap(Map<String, String> srcMap) {
		Map<String, String> dstMap = new HashMap<String, String>();
		Iterator iterator = srcMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator
					.next();
			dstMap.put(entry.getKey(), entry.getValue());
		}
		return dstMap;
	}
}
