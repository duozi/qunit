package com.qunar.base.qunit.dsl;

import com.qunar.base.qunit.preprocessor.DataCaseProcessor;
import com.qunar.base.qunit.reporter.Reporter;
import com.qunar.base.qunit.util.ConfigUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 2/20/13
 * Time: 4:49 PM
 */
public class DSLCommandReader {
    private final static Logger logger = LoggerFactory.getLogger(DSLCommandReader.class);

    public void read(List<String> fileNames, Reporter reporter) {
        //readDslTemplate(reporter);
        if (CollectionUtils.isEmpty(fileNames)){
            return;
        }
        for (String fileName : fileNames){
            if (StringUtils.isBlank(fileName)) continue;
            init(fileName, reporter);
        }
    }

    private void init(String fileName, Reporter reporter) {
        try {
            Document document = load(fileName);
            initCommands(document, reporter);
        } catch (FileNotFoundException e) {
            logger.error("指定的DSL命令配置文件不存在", fileName, e);
        } catch (DocumentException e) {
            logger.error("DSL命令定义文件格式错误，是非法的xml文档,file={}", fileName, e);
        }
    }

    private void initCommands(Document document, Reporter reporter) {
        Iterator serviceElements = document.getRootElement().elementIterator();
        while (serviceElements.hasNext()) {
            Element element = (Element) serviceElements.next();
            DefineDSLCommand defineDSLCommand = ConfigUtils.init(DefineDSLCommand.class, element);
            defineDSLCommand.setData(DataCaseProcessor.dataCasesMap.get(defineDSLCommand.getId()));
            if (element.selectSingleNode("data") != null){
                Node dataNode = element.selectSingleNode("data");
                DSLDataProcess dslDataProcess = new DSLDataProcess();
                dslDataProcess.processData(defineDSLCommand.getId(), (Element)dataNode);
            }
            defineDSLCommand.define(reporter);
        }
    }

    private Document load(String fileName) throws FileNotFoundException, DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(new FileInputStream(fileName));
    }

    private void readDslTemplate(Reporter reporter){
        URL url = this.getClass().getClassLoader().getResource("dslInnerTemplate.xml");
        if (url == null) {
            logger.error(String.format("dslTemplate.xml文件不存在"));
            return;
        }
        String path = url.getPath();
        init(path, reporter);
    }
}
