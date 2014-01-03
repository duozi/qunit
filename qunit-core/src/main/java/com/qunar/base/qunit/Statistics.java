package com.qunar.base.qunit;

import com.qunar.base.qunit.model.CaseStatistics;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.transport.http.HttpService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * User: zonghuang
 * Date: 1/2/14
 */
public class Statistics {

    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);

    public static void start(CaseStatistics caseStatistics) {
        try {
            if (!isValid(caseStatistics)) return;
            HttpService.get("http://autotest.corp.qunar.com/api/set/statistics.do", buildParameters(caseStatistics));
        } catch (Exception e) {
            logger.error("Start set memcached error", e);
        }
    }

    private static List<KeyValueStore> buildParameters(CaseStatistics caseStatistics) {
        KeyValueStore job = new KeyValueStore("job", caseStatistics.getJob());
        KeyValueStore build = new KeyValueStore("build", caseStatistics.getBuild());
        KeyValueStore runNum = new KeyValueStore("runNum", String.valueOf(caseStatistics.getRunSum()));
        KeyValueStore successedNum = new KeyValueStore("successedNum", String.valueOf(caseStatistics.getSuccess()));
        KeyValueStore failedNum = new KeyValueStore("failedNum", String.valueOf(caseStatistics.getFailed()));
        KeyValueStore caseDescs = new KeyValueStore("caseDescs", StringUtils.join(caseStatistics.getFailedIdList(), ","));
        return Arrays.asList(job, build, runNum, failedNum, successedNum, caseDescs);
    }

    private static boolean isValid(CaseStatistics caseStatistics) {
        if (isBlank(caseStatistics.getJob())
                || isBlank(caseStatistics.getBuild())) {
            return false;
        }
        return true;
    }
}
