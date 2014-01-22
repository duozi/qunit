package com.qunar.base.qunit.reporter;

import com.qunar.base.qunit.Statistics;

import java.util.List;
import java.util.Map;

/**
 * User: zonghuang
 * Date: 12/27/13
 */
public class ParseCase implements Runnable{

    private QJSONReporter reporter;

    private Map<Object, Object> element;

    public ParseCase(QJSONReporter reporter, Map<Object, Object> element){
        this.reporter = reporter;
        this.element = element;
    }
    @Override
    public void run() {
        String name = (String) element.get("name");
        boolean result = parse();
        if (result) {
            reporter.addSuccess();
        } else {
            reporter.addFailed(name);
        }
        Statistics.start(reporter.getCaseStatistics());

    }

    private boolean parse() {
        List<Object> steps = (List<Object>) element.get("steps");
        for (Object object :  steps) {
            Map<Object, Object> result = (Map<Object, Object>) ((Map<Object, Object>)object).get("result");
            if ("failed".equalsIgnoreCase((String) result.get("status"))) {
                return false;
            }
        }
        return true;
    }
}
