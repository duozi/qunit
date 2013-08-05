package com.qunar.base.qunit.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qunar.base.qunit.context.Context;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: zhaohuiyu
 * Date: 6/8/12
 * Time: 10:58 PM
 */
public class ExtractValueUtils {
    //TODO It's not very right.
    //$result[0].arr[1].property
    private final static Pattern pattern = Pattern.compile("(\\$result(\\[\\d+\\])?[\\_\\.a-zA-Z\\[\\d+\\]]*)");

    public static Object replaceVariables(String expression, Object result) {
        if (result == null) return expression;
        if (result instanceof Context) {
            return ((Context) result).replace(expression);
        }
        List<String> parameters = extractParameters(expression);
        for (String parameterName : parameters) {
            Object parameter = ExtractValueUtils.extract(parameterName, result);
            if (expression != null && parameter != null) {
                if (expression.equals("$result")) {
                    return parameter;
                }
                expression = expression.replace(parameterName, parameter.toString());
            }
        }
        return expression;
    }

    protected static List<String> extractParameters(String expression) {
        Matcher matcher = pattern.matcher(expression);
        ArrayList<String> result = new ArrayList<String>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    protected static Object extract(String accessRule, Object target) {
        if (StringUtils.isBlank(accessRule)) return accessRule;
        if (!accessRule.startsWith("$result")) return accessRule;
        if (accessRule.equals("$result")) return target;
        String[] accessRules = StringUtils.split(accessRule, ".");
        return extractByAccessRule(accessRules, target);
    }

    private static Object extractTarget(String accessRule, Object target) {
        if (isArrayAccessor(accessRule)) {
            target = extractProperty(accessRule, target);
            int index = extractIndex(accessRule);
            if (target instanceof Iterable) {
                return accessIterable(index, (Iterable) target);
            } else if (target.getClass().isArray()) {
                return Array.get(target, index);
            } else if (target instanceof String) {
                JSONArray jsonArray = tryParseArray(target.toString());
                if (jsonArray != null)
                    return jsonArray.get(index);
                else
                    return target.toString().charAt(index);
            }
            return target;
        } else {
            return extractProperty(accessRule, target);
        }
    }

    private static Object extractProperty(String accessRule, Object target) {
        String propertyName = extractPropertyName(accessRule);
        if (propertyName.equals("$result")) return target;
        if (target instanceof String) {
            JSONObject jsonObject = tryParseObject(target);
            if (jsonObject != null) {
                return jsonObject.get(propertyName);
            } else {
                throw new RuntimeException("ExtractValueUtils: given string is not a json:" + target.toString());
            }
        } else if (target instanceof Map) {
            return ((Map) target).get(propertyName);
        } else {
            return ReflectionUtils.getValue(target, propertyName);
        }
    }

    private static Object accessIterable(int index, Iterable target) {
        int i = 0;
        for (Object o : target) {
            if (i == index)
                return o;
            ++i;
        }
        return null;
    }

    private static boolean isArrayAccessor(String accessRule) {
        return accessRule.contains("[") && accessRule.endsWith("]");
    }

    private static int extractIndex(String accessRule) {
        int start = accessRule.indexOf("[") + 1;
        int end = accessRule.length() - 1;
        return Integer.parseInt(accessRule.substring(start, end));
    }

    private static String extractPropertyName(String accessRule) {
        if (isArrayAccessor(accessRule)) {
            int end = accessRule.indexOf("[");
            return accessRule.substring(0, end);
        } else
            return accessRule;
    }

    private static Object extractByAccessRule(String[] accessRules, Object target) {
        for (String accessRule : accessRules) {
            target = extractTarget(accessRule, target);
        }
        return target;
    }

    private static JSONObject tryParseObject(Object value) {
        try {
            return JSON.parseObject(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private static JSONArray tryParseArray(String text) {
        try {
            return JSON.parseArray(text);
        } catch (Exception e) {
            return null;
        }
    }
}
