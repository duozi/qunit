package com.qunar.base.qunit.dsl;

import com.qunar.base.qunit.annotation.Element;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.config.StepConfig;
import com.qunar.base.qunit.model.KeyValueStore;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 2/18/13
 * Time: 7:28 PM
 */
public class DSLCommandConfig extends StepConfig {
    private static Map<String, DSLCommandDesc> DSLMAPPING = new HashMap<String, DSLCommandDesc>();

    public static void map(String commandName, DSLCommandDesc dslCommandDesc) {
        DSLMAPPING.put(commandName, dslCommandDesc);
    }

    @Element
    List<KeyValueStore> params;

    @Override
    public StepCommand createCommand() {
        DSLCommandDesc dslCommandDesc = DSLMAPPING.get(commandName);
        if (dslCommandDesc == null && !"data".equalsIgnoreCase(commandName)) {
            throw new RuntimeException("未定义的DSL命令: " + commandName);
        }
        List<StepCommand> commands = createCommands(dslCommandDesc.children(), false);
        return new DSLCommand(dslCommandDesc, params, commands);
    }

    public StepCommand createCommand(boolean followed){
        DSLCommandDesc dslCommandDesc = DSLMAPPING.get(commandName);
        if (dslCommandDesc == null && !"data".equalsIgnoreCase(commandName)) {
            throw new RuntimeException("未定义的DSL命令: " + commandName);
        }
        List<StepCommand> commands = createCommands(dslCommandDesc.children(), followed);
        return new DSLCommand(dslCommandDesc, params, commands);
    }

    private List<StepCommand> createCommands(List<StepConfig> stepConfigs, boolean followed) {
        List<StepCommand> commands = new ArrayList<StepCommand>();
        for (StepConfig config : stepConfigs) {
            if ("data".equalsIgnoreCase(config.getCommandName())){
                continue;
            }
            if ((config instanceof DSLCommandConfig) && followed && checkFollow(((DSLCommandConfig) config).params)){
                continue;
            }
            StepCommand command = null;
            if ((config instanceof DSLCommandConfig)){
                command = ((DSLCommandConfig) config).createCommand(followed).cloneCommand();
            } else {
                command = config.createCommand().cloneCommand();
            }
            commands.add(command);
        }
        return commands;
    }

    private boolean checkFollow(List<KeyValueStore> params){
        if (CollectionUtils.isEmpty(params)){
            return false;
        }
        for (KeyValueStore kvs : params){
            if ("notRunWhenFollowed".equalsIgnoreCase(kvs.getName()) && "true".equalsIgnoreCase((String) kvs.getValue())){
                return true;
            }
        }
        return false;
    }
}
