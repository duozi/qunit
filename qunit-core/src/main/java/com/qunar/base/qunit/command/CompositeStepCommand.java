package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.intercept.InterceptorFactory;
import com.qunar.base.qunit.response.Response;

import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 6/12/12
 * Time: 11:36 AM
 */
public abstract class CompositeStepCommand extends StepCommand {
    protected List<StepCommand> children;

    InterceptorFactory interceptor = InterceptorFactory.getInstance();

    public CompositeStepCommand(List<StepCommand> children) {
        this.children = children;
    }

    @Override
    public Response doExecute(Response param, Context context) throws Throwable {
        Response response = param;
        for (StepCommand child : children) {
            interceptor.doBefore(child, response, context);
            response = child.doExecute(response, context);
            interceptor.doAfter(child, response, context);
        }
        return response;
    }

    public List<StepCommand> getChildren() {
        return children;
    }
}
