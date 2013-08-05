/**
 *
 */
package com.qunar.base.qunit.event;

import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.TestCase;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ziqiang.deng
 */
public class StepNotifier {

    private Set<StepEventListener> listeners;

    public StepNotifier() {
        this.listeners = new HashSet<StepEventListener>();
    }

    public void addStepEventListener(StepEventListener sListener) {
        this.listeners.add(sListener);
    }

    public void fireCaseStarted(TestCase testCase, Context context) {
        for (StepEventListener listener : listeners) {
            listener.caseStarted(testCase, context);
        }
    }

    public void fireCaseFinished(TestCase testCase, Context context) {
        for (StepEventListener listener : listeners) {
            listener.caseFinished(testCase, context);
        }
    }

    public void fireStepStarted(StepCommand sc) {
        for (StepEventListener listener : this.listeners) {
            listener.stepStarted(sc);
        }
    }

    public void fireStepFailed(StepCommand sc, Throwable e) {
        for (StepEventListener listener : this.listeners) {
            listener.stepFailed(sc, e);
        }
    }

    public void fireStepFinished(StepCommand sc) {
        for (StepEventListener listener : this.listeners) {
            listener.stepFinished(sc);
        }
    }

}
