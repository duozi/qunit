package com.qunar.base.qunit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class TestCaseFileFinder {
    private final static Logger logger = LoggerFactory.getLogger(TestCaseFileFinder.class);

    public List<String> getTestCaseList(List<String> testDirs) {
        List<String> filterFilePath = new ArrayList<String>();

        for (String dirs : testDirs) {
            String dirPath = "";
            String matcher = dirs;
            Enumeration<URL> resources;
            int filePos = dirs.lastIndexOf("/");

            if (filePos >= 0) {
                dirPath = dirs.substring(0, filePos);
                matcher = dirs.substring(filePos + 1);
                matcher = matcher.replace("*", ".*");
            }

            try {
                resources = Thread.currentThread().getContextClassLoader().getResources(dirPath);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    String path = url.getPath();
                    for (String filePath : filterFile(new File(path), matcher)) {
                        filterFilePath.add(filePath);
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

        }
        return filterFilePath;
    }

    private boolean filePathMatch(String fileName, String matcher) {
        return fileName.matches(matcher);
    }

    private List<String> filterFile(File path, String matcher) {
        List<String> filterFilePath = new ArrayList<String>();
        if (path.isDirectory()) {
            File[] fileList = path.listFiles();
            for (File file : fileList) {
                filterFilePath.addAll(filterFile(file, matcher));
            }
        } else if (filePathMatch(path.getName(), matcher)) {
            filterFilePath.add(path.getAbsolutePath());
        }
        return filterFilePath;
    }
}
