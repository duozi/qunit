package com.qunar.base.qunit.model;

import com.qunar.base.qunit.command.ExamplesCommand;
import com.qunar.base.qunit.command.StepCommand;

import java.util.List;

import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 10/15/12
 */
public class DataDrivenTestCase extends TestCase {
    public List<Map<String, String>> getExamples() {
        StepCommand current = getBodyCommand();
        StepCommand pre = null;
        while (current != null) {
            if (current instanceof ExamplesCommand) {
                removeExamples(current, pre);
                return ((ExamplesCommand) current).getExamples();
            }
            pre = current;
            current = current.getNextCommand();
        }
        throw new RuntimeException("数据驱动的Case必须有data节点");
    }

    private void removeExamples(StepCommand examples, StepCommand pre) {
        if (pre == null) {
            this.setBodyCommand(examples.getNextCommand());
        } else {
            pre.setNextCommand(examples.getNextCommand());
        }
    }
}
