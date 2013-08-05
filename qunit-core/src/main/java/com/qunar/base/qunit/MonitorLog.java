package com.qunar.base.qunit;

import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.transport.http.HttpService;
import com.qunar.base.qunit.util.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * User: zhaohuiyu
 * Date: 2/28/13
 * Time: 2:01 PM
 */
public class MonitorLog {
    protected final static Logger logger = LoggerFactory.getLogger(MonitorLog.class);

    public static void start(QunitOptions options) {
        try {
            if (!isValid(options)) return;
            HttpService.get("http://autotest.corp.qunar.com/start", buildParameters(options));
        } catch (Exception e) {
            logger.error("Start log monitor error", e);
        }
    }

    private static List<KeyValueStore> buildParameters(QunitOptions options) {
        String autotestlog = PropertyUtils.getProperty("autotest-log.host");
        KeyValueStore job = new KeyValueStore("job", options.jobName());
        KeyValueStore build = new KeyValueStore("build", options.buildNumber());
        KeyValueStore list = new KeyValueStore("list", autotestlog);
        return Arrays.asList(job, build, list);
    }

    private static boolean isValid(QunitOptions options) {
        String autotestlog = PropertyUtils.getProperty("autotest-log.host");
        if (isBlank(autotestlog)
                || isBlank(options.jobName())
                || isBlank(options.buildNumber())) {
            return false;
        }
        return true;
    }
}
