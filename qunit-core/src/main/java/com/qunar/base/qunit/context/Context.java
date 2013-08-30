/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.context;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：
 * Created by JarnTang at 12-7-24 上午11:18
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class Context {

    private Context parentContext;

    private Map<String, Object> context = new HashMap<String, Object>();

    public Context(Context parentContext) {
        this.parentContext = parentContext;
    }

    public void addContext(String key, Object value) {
        context.put(key, value);
    }

    public void addContext(Map<String, Object> context) {
        this.context.putAll(context);
    }

    public Object getContext(String key) {
        Object value = this.context.get(key);
        Context parent = getParent();
        if (value == null && parent != null) {
            value = parent.getContext(key);
        }
        return value;
    }

    public Object replace(String expression) {
        Object result = completeReplace(expression);
        return result != null ? result : partReplace(expression);
    }

    private Object completeReplace(String expression) {
        Object result = null;
        if (expression != null && expression.startsWith("$")) {
            result = getContext(expression.substring(1));
        }
        return result;
    }

    private Object partReplace(String expression) {
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String matchResult = matcher.group(1);
            Object o = getContext(matchResult);
            String replacedPart = String.format("${%s}", matchResult);
            if (expression == null) return expression;
            if (o == null) continue;
            if (expression.equals(replacedPart)) return o.toString();
            expression = StringUtils.replace(expression,replacedPart,o.toString());
        }
        if (expression != null && expression.startsWith("$")){
            expression = "[null]";
        }
        return expression;
    }

    //${jenkins.host}
    private static final Pattern pattern = Pattern.compile("\\$\\{?([a-zA-Z0-9_\\.]*)\\}?");

    public Context getParent() {
        return this.parentContext;
    }
}
