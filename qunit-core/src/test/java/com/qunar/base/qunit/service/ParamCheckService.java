/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 * Created by JarnTang at 12-8-6 下午12:02
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class ParamCheckService {

    String message = " param invalided.";

    public Map<String, String> check(String param1, String param2, String param3) {
        Map<String, String> result = new HashMap<String, String>();
        String invalidedMessage = "invalided";
        String normalMessage = param1 + " " + param2 + " " + param3 + " all right.";
        if (param1==null || !param1.contains("value")) {
            invalidedMessage = param1 + this.message;
            normalMessage = param2 + " " + param3 + " all right.";
        } else if (param2==null || !param2.contains("value")) {
            invalidedMessage = param2 + this.message;
            normalMessage = param1 + " " + param3 + " all right.";
        } else if (param3==null || !param3.contains("value")) {
            invalidedMessage = param3 + this.message;
            normalMessage = param1 + " " + param2 + " all right.";
        }
        result.put("invalidedMessage", invalidedMessage);
        result.put("normalMessage", normalMessage);
        return result;
    }

}
