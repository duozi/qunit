package com.qunar.base.qunit.util;

import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.config.StepConfig;
import com.qunar.base.qunit.model.KeyValueStore;

import java.util.*;

public class CloneUtil {

    public static List<KeyValueStore> cloneKeyValueStore(List<KeyValueStore> parameters) {
        if (parameters == null) {
            return null;
        }
        return (List<KeyValueStore>) clone(parameters);
    }

    private static Object clone(Object value) {
        if (value instanceof Map) {
            Map mapValue = (Map) value;

            Map newMap = new LinkedHashMap();
            for (Object o : mapValue.keySet()) {
                newMap.put(o, clone(mapValue.get(o)));
            }

            return newMap;
        } else if (value instanceof List) {
            List parameters = (List) value;
            List result = new ArrayList();
            for (Object item : parameters) {
                result.add(clone(item));
            }
            return result;
        } else if (value instanceof KeyValueStore) {
            KeyValueStore old = (KeyValueStore) value;
            return new KeyValueStore(old.getName(), clone(old.getValue()));
        }
        return value;
    }

    public static List<StepCommand> cloneStepCommand(List<StepCommand> children) {
        List<StepCommand> result = new ArrayList<StepCommand>();
        if (children != null) {
            for (StepCommand sc : children) {
                result.add(sc.cloneCommand());
            }
        }
        return result;
    }

    public static List<StepConfig> cloneStepConfig(List<StepConfig> children) {
        List<StepConfig> result = new ArrayList<StepConfig>();
        if (children != null){
            for (StepConfig sc : children) {
                result.add((StepConfig) sc.clone());
            }
        }
        return result;
    }

}
