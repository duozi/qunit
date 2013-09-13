package com.qunar.base.qunit.casereader;

import com.qunar.base.qunit.command.CommandFactory;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.command.TearDownStepCommand;
import com.qunar.base.qunit.exception.DuplicateIdException;
import com.qunar.base.qunit.model.DataCase;
import com.qunar.base.qunit.model.DataSuite;
import com.qunar.base.qunit.model.TestCase;
import com.qunar.base.qunit.model.TestSuite;
import com.qunar.base.qunit.preprocessor.DataCaseProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static com.qunar.base.qunit.util.XMLUtils.getAttributeMap;

public class DatacaseReader {
    private final static Logger logger = LoggerFactory.getLogger(DatacaseReader.class);
    protected final static ThreadLocal<String> threadLocal = new ThreadLocal<String>();
    protected final static Map<String, List<Object>> DATA_CASE_ID_CACHE = new HashMap<String, List<Object>>();
    protected final static Map<String, List<Object>> DATA_SUITE_ID_CACHE = new HashMap<String, List<Object>>();

    public List<TestSuite> convertDataSuiteToTestSuite(List<DataSuite> dataSuites, String ids){
        List<TestSuite> testSuites = new ArrayList<TestSuite>();
        List<String> idsList = null;
        if (StringUtils.isNotBlank(ids)){
            idsList = Arrays.asList(StringUtils.split(ids, ","));
        }
        for (DataSuite dataSuite : dataSuites) {
            TestSuite testSuite = new TestSuite();
            testSuite.setId(dataSuite.getId());
            testSuite.setCaseFileName(dataSuite.getCaseFileName());
            testSuite.setDesc(dataSuite.getDesc());
            List<TestCase> testCases = convertDataCaseToTestCase(dataSuite.getDataCases());
            if (idsList != null) {
                testSuite.setTestCases(filter(testCases, idsList));
            } else {
                testSuite.setTestCases(testCases);
            }
            if (!testSuite.getTestCases().isEmpty()) {
                testSuites.add(testSuite);
            }
        }
        return testSuites;
    }

    private List<TestCase> filter(List<TestCase> testCases, List<String> idsList) {
        List<TestCase> needRerun = new ArrayList<TestCase>();
        for (TestCase testCase : testCases) {
            if (idsList.contains(testCase.getId())) {
                needRerun.add(testCase);
            }
        }
        return needRerun;
    }

    private List<TestCase> convertDataCaseToTestCase(Map<String, DataCase> dataCases) {
        List<TestCase> testCases = new ArrayList<TestCase>();
        Iterator iterator = dataCases.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DataCase> entry = (Map.Entry<String, DataCase>) iterator.next();
            DataCase dataCase = entry.getValue();
            List<DataCase> caseChainList = dataCase.getCaseChain();
            if (caseChainList != null) {
                TestCase testCase = new TestCase();
                testCase.setId(dataCase.getId());
                testCase.setDesc(dataCase.getDesc());
                List<StepCommand> commands = CommandFactory.getInstance().getDataCommands(caseChainList);
                testCase.setBodyCommand(getBodyCommand(filterBodyCommand(commands)));
                testCases.add(testCase);
            }
        }
        return testCases;
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

    public List<DataSuite> getSuites(List<String> files, String keyFile, List<String> dslFiles) throws FileNotFoundException {
        List<DataSuite> suites = new ArrayList<DataSuite>(files.size());
        Map<String, String> keyMap = parseKeyFile(keyFile);
        for (String file : files) {
            DataSuite dataSuite = readDataCase(file, keyMap, dslFiles);
            if (dataSuite == null) continue;
            if (!dataSuite.getDataCases().isEmpty()){
                suites.add(dataSuite);
            }
        }

        return suites;
    }

    private DataSuite readDataCase(String fileName, Map<String, String> keyMap, List<String> dslFiles) throws FileNotFoundException {
        threadLocal.set(fileName);
        Document document = loadDocument(fileName);
        if (document == null) {
            logger.info(fileName + "不是testcase文件");
            return null;
        }

        DataSuite dataSuite = getDataCases(document, keyMap, dslFiles);
        setCaseFileName(dataSuite, fileName);
        return dataSuite;
    }

    public void processDataSuite(List<DataSuite> suites, List<String> levels, List<String> statuss){
        Map<String, DataCase> allDataCaseMap = getAllDataCaseMap(suites);

        for (DataSuite dataSuite : suites){
            addFollowCase(dataSuite, allDataCaseMap, levels, statuss);
        }
    }

    private Map<String, String> parseKeyFile(String fileName){
        try {
            URL url = this.getClass().getClassLoader().getResource(fileName);
            if (url == null) {
                logger.error(String.format("key文件不存在,file=<%s>", fileName));
                return null;
            }
            String path = url.getPath();

            Document document = loadKeyDocument(path);
            if (document == null){
                return null;
            } else {
                return getKeyMap(document);
            }
        } catch (FileNotFoundException e) {
            logger.error(String.format("key文件不存在,file=<%s>", fileName));
        } catch (DocumentException e) {
            logger.error(String.format("key文件格式错误，是非法的xml文档,file=<%s>", fileName));
        }
        return null;
    }

    private Map<String, String> getKeyMap(Document document){
        Map<String, String> keyMap = new HashMap<String, String>();
        Element rootElement = document.getRootElement();
        Iterator iterator = rootElement.elementIterator();
        while (iterator.hasNext()){
            Element row = (Element) iterator.next();
            String name = row.getName();
            String value = getValue(row);
            if (StringUtils.isBlank(value)){
                continue;
            } else {
                keyMap.put(name, value);
            }
        }

        return keyMap;
    }

    private String getValue(Element element){
        Iterator iterator = element.elementIterator();
        String value = "";
        while (iterator.hasNext()){
            Element keyElement = (Element) iterator.next();
            value = keyElement.getTextTrim();
        }

        return value;
    }

    private void setCaseFileName(DataSuite dataSuite, String fileName) {
        String searchStr = "test-classes";
        int index = StringUtils.indexOf(fileName, searchStr);
        if (index != -1) {
            fileName = StringUtils.substring(fileName, index + searchStr.length());
            dataSuite.setCaseFileName(fileName);
        } else {
            dataSuite.setCaseFileName(fileName);
        }
    }

    private void addFollowCase(DataSuite dataSuite, Map<String, DataCase> allDataCaseMap, List<String> levels, List<String> statuss){
        Map<String, DataCase> dataCases = dataSuite.getDataCases();
        Iterator iterator = dataCases.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, DataCase> entry = (Map.Entry<String, DataCase>)iterator.next();
            DataCase dataCase = entry.getValue();
            if (!checkLevelAndStatus(levels, statuss, dataCase.getLevel(), dataCase.getStatus())){
                continue;
            }
            List<String> idList = getFollowId(entry, allDataCaseMap);
            setCaseChain(entry, allDataCaseMap, idList);
        }
    }

    private void setCaseChain(Map.Entry<String, DataCase> entry, Map<String, DataCase> allDataCaseMap, List<String> idList){
        List<DataCase> caseChain = new ArrayList<DataCase>();
        for (int i = idList.size() - 1; i >= 0; i--){
            caseChain.add(allDataCaseMap.get(idList.get(i)));
        }
        entry.getValue().setCaseChain(caseChain);
    }

    private boolean checkLevelAndStatus(List<String> levels, List<String> statuss, String level, String status){
        if (levels.contains("*") && statuss.contains("*")){
            return true;
        } else if (levels.contains("*") && statuss.contains(status)){
            return true;
        } else if (levels.contains(level) && statuss.contains("*")){
            return true;
        } else if (levels.contains(level) && statuss.contains(status)){
            return true;
        }
        return false;
    }


    private List<String> getFollowId(Map.Entry<String, DataCase> entry, Map<String, DataCase> allDataCaseMap){
        List<String> idList = new ArrayList<String>();
        idList.add(entry.getKey());
        DataCase dataCase = entry.getValue();
        String follow = dataCase.getFollow();
        while (follow != null){
            idList.add(follow);
            dataCase = allDataCaseMap.get(follow);
            if (dataCase == null){
                throw new RuntimeException("case [" + entry.getKey() + "] 依赖的case [" + follow + "]未定义");
            }
            follow = dataCase.getFollow();
        }

        return idList;
    }

    private Map<String, DataCase> getAllDataCaseMap(List<DataSuite> dataSuites){
        Map<String, DataCase> dataCaseMap = new HashMap<String, DataCase>();
        if (CollectionUtils.isNotEmpty(dataSuites)){
            for (DataSuite dataSuite : dataSuites){
                dataCaseMap.putAll(dataSuite.getDataCases());
            }
        }

        return dataCaseMap;
    }

    private DataSuite getDataCases(Document document, Map<String, String> keyMap, List<String> dslFiles){
        DataSuite dataSuite = new DataSuite();
        Element rootElement = document.getRootElement();
        Map<String, String> attributeMap = getAttributeMap(rootElement);
        String suiteId = getId(attributeMap.get("id"));
        checkDuplicateId(suiteId, DATA_SUITE_ID_CACHE);
        dataSuite.setId(suiteId);
        dataSuite.setDesc(attributeMap.get("desc"));

        Map<String, DataCase> dataCasesMap = new LinkedHashMap<String, DataCase>();
        List caseElements = rootElement.elements("data-cases");
        if (caseElements != null){
            for (Object caseElement : caseElements) {
                Element next = (Element) caseElement;
                dataCasesMap.putAll(getDataCase(next));
                DataCaseProcessor.parseDataCases(next, keyMap, dslFiles);
            }
        }
        dataSuite.setDataCases(dataCasesMap);
        return dataSuite;
    }

    private String getId(String id) {
        return StringUtils.replace(StringUtils.trim(id), " ", "_");
    }

    private Map<String, DataCase> getDataCase(Element element) {
        Map<String, String> attributeMap = getAttributeMap(element);
        Iterator iterator = element.elementIterator();
        Map<String, DataCase> dataCaseMap = new LinkedHashMap<String, DataCase>();
        while(iterator.hasNext()){
            Element row = (Element)iterator.next();
            if (!"default".equals(row.getName())){
                Map<String, String> dataCaseAttributeMap = getAttributeMap(row);
                String id = dataCaseAttributeMap.get("id");
                checkDuplicateId(id, DATA_CASE_ID_CACHE);
                String executor = attributeMap.get("executor");
                DataCase dataCase = new DataCase(id, dataCaseAttributeMap.get("desc"), dataCaseAttributeMap.get("level"), dataCaseAttributeMap.get("status"), executor, dataCaseAttributeMap.get("follow"));
                dataCaseMap.put(id, dataCase);
            }
        }

        return dataCaseMap;
    }

    private Document loadKeyDocument(String fileName) throws FileNotFoundException, DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(new FileInputStream(fileName));
    }

    private Document loadDocument(String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = getCaseInputStream(fileName);
            Document document = getDocument(inputStream);
            if (!isValid(document)) return null;
            return document;
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private InputStream getCaseInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(fileName);
    }

    private Document getDocument(InputStream inputStream) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(inputStream);
    }

    private boolean isValid(Document document) {
        String rootName = document.getRootElement().getName();
        return rootName.equalsIgnoreCase("casesuit");
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
}
