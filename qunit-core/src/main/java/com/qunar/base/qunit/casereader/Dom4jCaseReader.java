/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.casereader;

import com.alibaba.fastjson.JSONObject;
import com.qunar.base.qunit.command.CallStepCommand;
import com.qunar.base.qunit.command.CommandFactory;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.command.TearDownStepCommand;
import com.qunar.base.qunit.exception.DuplicateIdException;
import com.qunar.base.qunit.model.DataDrivenTestCase;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.model.TestCase;
import com.qunar.base.qunit.model.TestSuite;
import com.qunar.base.qunit.preprocessor.TestCasePreProcessor;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.*;

import static com.qunar.base.qunit.util.CloneUtil.cloneStepCommand;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * 通过dom4j读取case文件
 * <p/>
 * Created by JarnTang at 12-6-4 下午3:36
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class Dom4jCaseReader implements TestCaseReader {
    private final static Logger logger = LoggerFactory.getLogger(Dom4jCaseReader.class);

    protected final static ThreadLocal<String> threadLocal = new ThreadLocal<String>();
    protected final static Map<String, List<Object>> SUITE_ID_CACHE = new HashMap<String, List<Object>>();
    protected final static Map<String, List<Object>> CASE_ID_CACHE = new HashMap<String, List<Object>>();

    @Override
    public TestSuite readTestCase(String fileName) throws FileNotFoundException {
        threadLocal.set(fileName);
        TestCasePreProcessor preProcessor = new TestCasePreProcessor();
        Document document = preProcessor.prepare(fileName);
        if (document == null) {
            logger.info(fileName + "不是testcase文件");
            return null;
        }
        TestSuite testSuite = getTestCase(document);
        setCaseFileName(testSuite, fileName);
        return testSuite;
    }

    private void setCaseFileName(TestSuite testSuite, String fileName) {
        String searchStr = "test-classes";
        int index = StringUtils.indexOf(fileName, searchStr);
        if (index != -1) {
            fileName = StringUtils.substring(fileName, index + searchStr.length());
            testSuite.setCaseFileName(fileName);
        } else {
            testSuite.setCaseFileName(fileName);
        }
    }

    private TestSuite getTestCase(Document document) {
        TestSuite testSuite = new TestSuite();
        Element rootElement = document.getRootElement();
        Map<String, String> attributeMap = getAttributeMap(rootElement);
        String suiteId = getId(attributeMap.get("id"));
        checkDuplicateId(suiteId, SUITE_ID_CACHE);
        testSuite.setId(suiteId);
        String tags = attributeMap.get("tag");
        testSuite.setTag(getTagList(tags));
        testSuite.setDesc(attributeMap.get("desc"));
        List<TestCase> backGrounds = getBackGrounds(rootElement, testSuite);

        List<TestCase> testCases = getCases(rootElement, testSuite);
        testSuite.setBeforeSuite(getBeforeSuite(rootElement, testSuite));
        testSuite.setAfterSuite(getAfterSuite(rootElement, testSuite));
        testSuite.setBeforeCase(getBeforeCase(rootElement, testSuite));
        testSuite.setAfterCase(getAfterCase(rootElement, testSuite));
        testSuite.setBackGrounds(backGrounds);
        testSuite.setTestCases(testCases);
        return testSuite;
    }

    private String getId(String id) {
        return StringUtils.replace(StringUtils.trim(id), " ", "_");
    }

    private String getSuiteDesc(TestSuite testSuite) {
        return isBlank(testSuite.getDesc()) ? testSuite.getId() : testSuite.getDesc();
    }

    private String getSuiteId(TestSuite testSuite) {
        return isBlank(testSuite.getId()) ? testSuite.getDesc() : testSuite.getId();
    }

    private List<KeyValueStore> getAttribute(Element element) {
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

    private Map<String, String> getAttributeMap(Element element) {
        List<KeyValueStore> attribute = getAttribute(element);
        return convertListKeyValueToMap(attribute);
    }

    private Map<String, String> convertListKeyValueToMap(List<KeyValueStore> list) {
        Map<String, String> map = new HashMap<String, String>();
        for (KeyValueStore kvs : list) {
            map.put(kvs.getName(), (String) kvs.getValue());
        }
        return map;
    }

    private List<TestCase> getCases(Element document, TestSuite testSuite) {
        List<TestCase> testCases = new ArrayList<TestCase>();
        testCases.addAll(getCase(document, "case", testSuite));
        testCases.addAll(getCase(document, "data-case", testSuite));
        checkDuplicateId(testCases, CASE_ID_CACHE);
        return testCases;
    }

    private void checkDuplicateId(List<TestCase> cases, Map<String, List<Object>> cache) {
        for (TestCase testCase : cases) {
            checkDuplicateId(testCase.getId(), cache);
        }
    }

    private void checkDuplicateId(String id, Map<String, List<Object>> cache) {
        for (Map.Entry<String, List<Object>> entry : cache.entrySet()) {
            String file = entry.getKey();
            List<Object> ids = entry.getValue();
            if (ids != null && ids.contains(id)) {
                String message;
                if (file.equals(threadLocal.get())) {
                    message = "文件<" + file + ">里的有重复的ID[" + id + "]";
                } else {
                    message = "文件<" + file + ">与文件<" + threadLocal.get() + ">里的ID[" + id + "] 重复";
                }
                throw new DuplicateIdException(message);
            }
        }
        List<Object> list = cache.get(threadLocal.get());
        if (list == null) {
            list = new ArrayList<Object>();
            cache.put(threadLocal.get(), list);
        }
        list.add(id);
    }

    private CallStepCommand getAutoParamCheckCallCommand(List<StepCommand> commands) {
        if (commands == null) return null;
        for (StepCommand sc : commands) {
            if (sc instanceof CallStepCommand) {
                return (CallStepCommand) sc;
            }
        }
        return null;
    }

    private List<TestCase> getBeforeSuite(Element element, TestSuite testSuite) {
        List<TestCase> beforeSuit = getCase(element, "beforeSuit", testSuite);
        for (TestCase tc : beforeSuit) {
            tc.setDesc(getSuiteDesc(testSuite) + "->BeforeSuit");
        }
        return beforeSuit;
    }

    private List<TestCase> getAfterSuite(Element element, TestSuite testSuite) {
        List<TestCase> afterSuit = getCase(element, "afterSuit", testSuite);
        for (TestCase tc : afterSuit) {
            tc.setDesc(getSuiteDesc(testSuite) + "->AfterSuit");
        }
        return afterSuit;
    }

    private List<TestCase> getBeforeCase(Element element, TestSuite testSuite) {
        return getCase(element, "beforeCase", testSuite);
    }

    private List<TestCase> getAfterCase(Element element, TestSuite testSuite) {
        return getCase(element, "afterCase", testSuite);
    }

    private List<TestCase> getBackGrounds(Element document, TestSuite testSuite) {
        Element backgrounds = document.element("backgrounds");
        return getCase(backgrounds, "background", testSuite);
    }

    private List<TestCase> getCase(Element document, String elementName, TestSuite testSuite) {
        List<TestCase> testCases = new ArrayList<TestCase>();
        if (document == null) {
            return testCases;
        }
        List caseElements = document.elements(elementName);
        if (caseElements != null) {
            for (Object caseElement : caseElements) {
                Element next = (Element) caseElement;
                testCases.addAll(createTestCase(next, testSuite));
            }
        }
        return testCases;
    }

    private List<TestCase> createTestCase(Element next, TestSuite testSuite) {
        TestCase testCase = newTestCase(next, testSuite);
        if (isAutoParamTestCase(next)) {
            checkParamCheckElementCount(next.elements("paramcheck"));
            return createAutoParamCheckTestCase(testCase, next, next.element("paramcheck"));
        } else {
            List<StepCommand> commands = CommandFactory.getInstance().getCommands(next);
            testCase.setTearDownCommand(getTearDownCommand(commands));
            testCase.setBodyCommand(getBodyCommand(filterBodyCommand(commands)));
            return Arrays.asList(testCase);
        }
    }

    private boolean isAutoParamTestCase(Element next) {
        List paramCheckElements = next.elements("paramcheck");
        return paramCheckElements != null && paramCheckElements.size() > 0;
    }

    private List<TestCase> createAutoParamCheckTestCase(TestCase testCase, Element parentElement, Element paramElement) {
        List<TestCase> result = new ArrayList<TestCase>();

        int index = getParamCheckElementIndex(parentElement, paramElement);
        List<StepCommand> checkCommands = getAutoParamCheckCommands(paramElement);
        removeAutoCheckElement(parentElement, paramElement);

        List<StepCommand> commands = CommandFactory.getInstance().getCommands(parentElement);

        List<String> excludes = getAutoParamCommandAttributeValue(paramElement, "exclude");
        List<String> includes = getAutoParamCommandAttributeValue(paramElement, "include");
        List<String> values = getAutoParamCommandAttributeValue(paramElement, "value");

        if (checkCommands != null && checkCommands.size() > 0) {
            CallStepCommand callCommand = getAutoParamCheckCallCommand(checkCommands);
            int commandIndex = checkCommands.indexOf(callCommand);
            Set<String> needReplaceNames = new HashSet<String>();

            collectionParameters(callCommand.getParams(), needReplaceNames);

            Integer count = 0;

            for (String value : values) {
                for (String needReplaceName : needReplaceNames) {
                    if (notNeedReplace(needReplaceName, excludes, includes)) continue;

                    CallStepCommand newCallStepCommand = (CallStepCommand) callCommand.cloneCommand();
                    replaceParameter(newCallStepCommand.getParams(), value, needReplaceName);

                    String suffix = needReplaceName + "=" + value;
                    TestCase tc = new TestCase(concat(testCase.getId(), suffix, count.toString()), testCase.getTag(), concat(testCase.getDesc(), suffix));

                    List<StepCommand> newCommands = cloneStepCommand(commands);
                    List<StepCommand> stepCommands = cloneStepCommand(checkCommands);
                    stepCommands.set(commandIndex, newCallStepCommand);

                    newCommands.addAll(index, stepCommands);
                    tc.setTearDownCommand(getTearDownCommand(newCommands));
                    StepCommand bodyCommand = getBodyCommand(filterBodyCommand(newCommands));
                    tc.setBodyCommand(bodyCommand);
                    result.add(tc);
                    ++count;
                }
            }
        }
        return result;
    }

    private boolean notNeedReplace(String name, List<String> excludes, List<String> includes) {
        if (excludes.contains(name)) {
            return true;
        }

        if (includes.size() != 0 && !includes.contains(name)) {
            return true;
        }
        return false;
    }

    private void collectionParameters(List<KeyValueStore> parameters, Set<String> parameterNames) {
        for (KeyValueStore parameter : parameters) {
            Object value = parameter.getValue();
            if (isJson(value)) {
                parameterNames.add(parameter.getName());
                collectionJsonParameter(parameter.getName(), (String) value, parameterNames);
            } else if (value instanceof String) {
                parameterNames.add(parameter.getName());
            } else if (value instanceof Map) {
                collectParameterInternal((Map) value, parameterNames);
            }
        }
    }

    private void collectionJsonParameter(String key, String json, Set<String> parameterNames) {
        Object object = JSONObject.parse(json);
        Iterator iterator = ((Map)object).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getKey() instanceof String) {
                parameterNames.add(key + "." + entry.getKey().toString().trim());
            }
        }
    }

    private void collectParameterInternal(Map parameters, Set<String> parameterNames) {
        for (Object o : parameters.keySet()) {
            Object value = parameters.get(o);
            if (value instanceof String) {
                parameterNames.add(o.toString());
            } else if (value instanceof Map) {
                collectParameterInternal((Map) value, parameterNames);
            }
        }
    }

    private void replaceParameter(List<KeyValueStore> parameters, String value, String replaceName) {
        for (KeyValueStore parameter : parameters) {
            Object parameterValue = parameter.getValue();
            if (isJson(parameterValue)) {
                if (replaceName.equals(parameter.getName())) {
                    parameter.setValue(value);
                    return;
                }
                String json = replaceJsonParameter(parameter.getName(), (String) parameterValue, value, replaceName);
                parameter.setValue(json);
            } else if (parameterValue instanceof Map) {
                replaceParameterInternal((Map) parameterValue, value, replaceName);
            } else if (parameterValue instanceof String) {
                String key = parameter.getName();

                if (!key.equals(replaceName)) continue;

                parameter.setValue(value);
            }
        }
    }

    private String replaceJsonParameter(String key, String json, String value, String replaceName) {
        Map map = (Map) JSONObject.parse(json);
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getKey() instanceof String) {
                if (replaceName.equals(key + "." + entry.getKey().toString().trim())) {
                    map.put(entry.getKey(), value);
                }
            }
        }
        return JSONObject.toJSONString(map);
    }

    private void replaceParameterInternal(Map parameters, String value, String replaceName) {
        for (Object o : parameters.keySet()) {
            Object parameterValue = parameters.get(o);

            if (parameterValue instanceof Map) {
                replaceParameterInternal((Map) parameterValue, value, replaceName);
            } else if (parameterValue instanceof String) {
                String key = o.toString();

                if (!key.equals(replaceName)) continue;

                parameters.put(key, value);
            }
        }
    }

    private TestCase newTestCase(Element element, TestSuite testSuite) {
        Map<String, String> attributeMap = getAttributeMap(element);
        TestCase testCase;
        if (element.getName().equals("case")) {
            testCase = new TestCase();
        } else {
            testCase = new DataDrivenTestCase();
        }
        String id = getId(attributeMap.get("id"));
        String desc = attributeMap.get("desc");

        testCase.setId("[" + getSuiteId(testSuite) + "]" + (id == null ? desc : id));
        testCase.setDesc(desc == null ? id : desc);
        String tags = attributeMap.get("tag");
        testCase.setTag(getTagList(tags));
        return testCase;
    }

    private String concat(String... items) {
        return StringUtils.join(items, "-");
    }

    private List<String> getAutoParamCommandAttributeValue(Element element, String attributeName) {
        Attribute exclude = element.attribute(attributeName);
        if (exclude == null) {
            return Collections.emptyList();
        }
        String value = exclude.getValue();
        return value == null ? new ArrayList<String>() : Arrays.asList(value.split(","));
    }

    private List<StepCommand> getAutoParamCheckCommands(Element paramElement) {
        return CommandFactory.getInstance().getCommands(paramElement);
    }

    private int getParamCheckElementIndex(Element parent, Element element) {
        Iterator iterator = parent.elementIterator();
        int index = 0;
        while (iterator.hasNext()) {
            Element e = (Element) iterator.next();
            if (!e.getName().equals(element.getName())) {
                index++;
            } else {
                break;
            }
        }
        return index;
    }

    private void checkParamCheckElementCount(List paramCheckElements) {
        if (paramCheckElements != null && paramCheckElements.size() > 1) {
            throw new RuntimeException("param check command only allowed 1 times, but find " + paramCheckElements.size() + " times");
        }
    }

    private void removeAutoCheckElement(Element element, Element removeElement) {
        element.remove(removeElement);
    }

    private List<String> getTagList(String tags) {
        if (tags == null) {
            return null;
        }
        return Arrays.asList(tags.split(","));
    }

    private StepCommand getBodyCommand(List<StepCommand> commands) {
        if (commands.size() > 1) {
            for (int index = 0; index < commands.size() - 1; index++) {
                commands.get(index).setNextCommand(commands.get(index + 1));
            }
        }
        return commands.size() == 0 ? null : commands.get(0);
    }

    private List<StepCommand> filterBodyCommand(List<StepCommand> commands) {
        List<StepCommand> result = new ArrayList<StepCommand>(commands.size());
        for (StepCommand command : commands) {
            if (isBodyCommand(command)) {
                result.add(command);
            }
        }
        return result;
    }

    private boolean isBodyCommand(StepCommand command) {
        return command == null || !(command instanceof TearDownStepCommand);
    }

    private StepCommand getTearDownCommand(List<StepCommand> commands) {
        for (StepCommand command : commands) {
            if (command instanceof TearDownStepCommand) {
                return command;
            }
        }
        return null;
    }

    private boolean isJson(Object value) {
        if (!(value instanceof String)) return false;
        String json = value.toString();
        return json.startsWith("{") && json.endsWith("}");
    }
}
