package com.qunar.base.qunit.casefilter;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.trim;

/**
 * User: zhaohuiyu
 * Date: 11/2/12
 */
public class TagExpression {
    Or or = new Or();

    public TagExpression(List<String> expected) {
        for (String tag : expected) {
            or.add(add(tag));
        }
    }

    public boolean eval(List<String> actual) {
        return or.eval(actual);
    }

    private Expression add(String tagExp) {
        tagExp = trim(tagExp);
        if (tagExp.contains("&")) {
            String[] subTags = StringUtils.split(tagExp, "&");
            And and = new And();
            for (String subTag : subTags) {
                and.add(add(subTag));
            }
            return and;
        }
        if (tagExp.contains("|")) {
            String[] subTags = StringUtils.split(tagExp, "|");
            Or or = new Or();
            for (String subTag : subTags) {
                or.add(add(subTag));
            }
            return or;
        }
        if (tagExp.startsWith("!")) {
            return new Not(add(tagExp.substring(1)));
        } else {
            return new TagExp(tagExp);
        }
    }

    private interface Expression {
        boolean eval(List<String> tags);
    }

    private abstract class CompositeExpression implements Expression {
        protected List<Expression> expressions = new ArrayList<Expression>();

        public void add(Expression expression) {
            this.expressions.add(expression);
        }
    }

    private class TagExp implements Expression {
        private String expected;

        public TagExp(String expected) {
            this.expected = expected;
        }

        public boolean eval(List<String> tags) {
            if (tags == null || tags.size() == 0) return false;
            for (String actual : tags) {
                if (expected.equals(actual)) return true;
            }
            return false;
        }
    }

    private class Or extends CompositeExpression {
        public boolean eval(List<String> tags) {
            for (Expression expression : expressions) {
                if (expression.eval(tags)) return true;
            }
            return false;
        }
    }

    private class And extends CompositeExpression {
        public boolean eval(List<String> tags) {
            for (Expression expression : expressions) {
                if (!expression.eval(tags)) {
                    return false;
                }
            }
            return true;
        }
    }

    private class Not implements Expression {
        private Expression expression;

        public Not(Expression expression) {
            this.expression = expression;
        }

        public boolean eval(List<String> tags) {
            return !expression.eval(tags);
        }
    }
}
