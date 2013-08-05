/*
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */

package com.qunar.base.qunit.casereader;

import com.qunar.base.qunit.exception.DuplicateIdException;
import com.qunar.base.qunit.reporter.QJSONReporter;
import com.qunar.base.qunit.transport.command.ServiceFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.URL;

@Ignore
public class Dom4jCaseReaderTest {

    @BeforeClass
    public static void initService() throws FileNotFoundException {
        ServiceFactory.getInstance().init(new String[]{"service.xml"}, new QJSONReporter(System.out));
    }

    @Before
    public void cleanCase() {
        Dom4jCaseReader.CASE_ID_CACHE.clear();
        Dom4jCaseReader.SUITE_ID_CACHE.clear();
    }

    @Test
    public void should_not_throw_exception_when_case_id_duplicated() throws FileNotFoundException {
        String fileName1 = "duplicateid/duplicate-caseid-test1.xml";
        String fileName2 = "duplicateid/duplicate-caseid-test2.xml";
        new Dom4jCaseReader().readTestCase(getFilePath(fileName1));
        new Dom4jCaseReader().readTestCase(getFilePath(fileName2));
    }

    @Test(expected = DuplicateIdException.class)
    public void should_throw_exception_when_suite_id_duplicated() throws FileNotFoundException {
        String fileName1 = "duplicateid/duplicate-suiteid-test1.xml";
        String fileName2 = "duplicateid/duplicate-suiteid-test2.xml";
        new Dom4jCaseReader().readTestCase(getFilePath(fileName1));
        new Dom4jCaseReader().readTestCase(getFilePath(fileName2));
    }

    @Test
    public void should_no_exception_when_id_not_duplicated() throws FileNotFoundException {
        String fileName1 = "duplicateid/duplicate-suiteid-test1.xml";
        String fileName2 = "duplicateid/duplicate-caseid-test1.xml";
        new Dom4jCaseReader().readTestCase(getFilePath(fileName1));
        new Dom4jCaseReader().readTestCase(getFilePath(fileName2));
    }

    private static String getFilePath(String fileName) throws FileNotFoundException {
        URL resource = Dom4jCaseReaderTest.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new FileNotFoundException(fileName);
        }
        return resource.getPath();
    }

}
