package com.qunar.base.qunit.dsl;

import com.qunar.base.qunit.annotation.Element;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.config.StepConfig;
import com.qunar.base.qunit.model.KeyValueStore;

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
        List<StepCommand> commands = createCommands(dslCommandDesc.children());
        return new DSLCommand(dslCommandDesc, params, commands);
    }

    private List<StepCommand> createCommands(List<StepConfig> stepConfigs) {
        List<StepCommand> commands = new ArrayList<StepCommand>();
        for (StepConfig config : stepConfigs) {
            StepCommand command = config.createCommand().cloneCommand();
            commands.add(command);
        }
        return commands;
    }
}
