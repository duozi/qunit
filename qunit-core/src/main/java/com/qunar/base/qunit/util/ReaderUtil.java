/*
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */

package com.qunar.base.qunit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 输入流工具类
 *
 * Created by JarnTang at 12-8-13 下午3:32
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class ReaderUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderUtil.class);

    public static String readeAsString(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("read input stream has error.", e);
        }
        return sb.toString();
    }

}
