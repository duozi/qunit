package com.qunar.base.qunit.command;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.preprocessor.DataDrivenPreprocessor;
import com.qunar.base.qunit.response.Response;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * User: zhaohuiyu
 * Date: 10/16/12
 */
public class ExamplesCommand extends StepCommand {

    private List<Map<String, String>> examples;

    private static final Splitter SPLITTER = Splitter.on(DataDrivenPreprocessor.spliter).trimResults();

    public ExamplesCommand(String body) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(body));
        try {
            String line = null;
            List<String> keys = readKeys(reader);
            examples = new ArrayList<Map<String, String>>();
            while ((line = reader.readLine()) != null) {
                examples.add(readExample(keys, line));
            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private Map<String, String> readExample(List<String> keys, String line) {
        Iterable<String> result = SPLITTER.split(line);
        List<String> values = covertToList(result);
        if (values.size() != keys.size()) throw new RuntimeException(String.format("数据驱动的Case数据格式错误:表头有%s字段，而值有%s字段", keys.size(), values.size()));
        Map<String, String> examples = new HashMap<String, String>();
        for (int i = 0; i < keys.size(); ++i) {
            examples.put(keys.get(i), values.get(i));
        }
        return examples;
    }

    private List<String> covertToList(Iterable<String> values) {
        Iterator<String> iterator = values.iterator();
        ArrayList<String> result = new ArrayList<String>();
        while (iterator.hasNext()) {
            String value = iterator.next();
            result.add(value);
        }
        if (Strings.isNullOrEmpty(result.get(0))) {
            result.remove(0);
        }
        if (Strings.isNullOrEmpty(result.get(result.size() - 1))) {
            result.remove(result.size() - 1);
        }
        return result;
    }

    private List<String> readKeys(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return covertToList(SPLITTER.split(line));
    }

    @Override
    public Response doExecute(Response param, Context context) throws Throwable {
        return param;
    }

    @Override
    protected StepCommand doClone() {
        return new ExamplesCommand(examples);
    }

    public ExamplesCommand(List<Map<String, String>> examples){
        this.examples = examples;
    }


    @Override
    public Map<String, Object> toReport() {
        return new HashMap<String, Object>();
    }

    public List<Map<String, String>> getExamples() {
        return examples;
    }
}
