package com.qunar.base.qunit.casereader;

import com.qunar.base.qunit.model.TestSuite;
import org.dom4j.DocumentException;

import java.io.FileNotFoundException;

/**
 * 基于文件的TestCase读取器，如果需要支持其他方式，实现该接口注入到ReaderFactory即可正常使用。
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 * @version V1.0
 */
public interface TestCaseReader {

    /**
     * 解析指定文件里的所有测试用例
     * @param file 测试用例文件
     * @return 所有测试用例
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public TestSuite readTestCase(String file) throws FileNotFoundException, DocumentException;

}
