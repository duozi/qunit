package com.qunar.base.qunit.service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelService {
	
	public void generateExcel(LinkedHashMap<String, String> content){
		Iterator iterator = content.entrySet().iterator();
    	while(iterator.hasNext()){
    		Map.Entry<String, String> entry = (Map.Entry<String, String>)iterator.next();
    		System.out.println(entry.getKey());
    		System.out.println(entry.getValue());
    	}
	}

}
