package com.qunar.base.qunit.command;

import com.qunar.base.qunit.config.*;
import com.qunar.base.qunit.dsl.DSLCommandConfig;
import com.qunar.base.qunit.exception.CommandNotFoundException;
import com.qunar.base.qunit.extension.ExtensionLoader;
import com.qunar.base.qunit.model.DataCase;
import com.qunar.base.qunit.util.ConfigUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CommandFactory {

    private static Logger logger = LoggerFactory.getLogger(CommandFactory.class);

    private static final Map<String, Class<? extends StepConfig>> CONFIG = new HashMap<String, Class<? extends StepConfig>>();

    static {
        CONFIG.put("prepareData", PreDataStepConfig.class);
        CONFIG.put("call", CallStepConfig.class);
        CONFIG.put("sql", SqlStepConfig.class);
        CONFIG.put("wait", WaitStepConfig.class);
        CONFIG.put("assert", AssertStepConfig.class);
        CONFIG.put("dbAssert", DbAssertStepConfig.class);
        CONFIG.put("dbassert", DbAssertStepConfig.class);
        CONFIG.put("transform", TransformStepConfig.class);
        CONFIG.put("waituntil", WaitUntilStepConfig.class);
        CONFIG.put("waitUntil", WaitUntilStepConfig.class);
        CONFIG.put("mock", MockStepConfig.class);
        CONFIG.put("loop", LoopStepConfig.class);
        CONFIG.put("print", PrintStepConfig.class);
        CONFIG.put("set", SetStepConfig.class);
        CONFIG.put("teardown", TearDownStepConfig.class);
        CONFIG.put("examples", ExamplesConfig.class);
        CONFIG.put("echo", EchoConfig.class);
        CONFIG.put("removeHeader", RemoveHeaderConfig.class);
        CONFIG.put("compare", CompareStepConfig.class);
        CONFIG.put(CompareDatabaseStepConfig.NAME, CompareDatabaseStepConfig.class);
        loadAllConfigs();
    }

    private static void loadAllConfigs() {
        try {
            Map<String, Class<? extends StepConfig>> map = ExtensionLoader.loadExtension(StepConfig.class);
            CONFIG.putAll(map);
        } catch (Exception e) {
            logger.error("can not load extensions for {}", StepConfig.class.getName());
        }
    }

    public List<StepCommand> getCommands(Element element) {
        List<StepCommand> commands = new ArrayList<StepCommand>();
        Iterator iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element nextElement = (Element) iterator.next();
            StepCommand command = getCommand(nextElement);
            commands.add(command);
        }
        return commands;
    }

    private StepCommand getCommand(Element element) {
        Class<? extends StepConfig> clazz = CONFIG.get(element.getName());
        if (clazz == null) {
            throw new CommandNotFoundException(element.getName());
        }
        try {
            StepConfig config = ConfigUtils.init(clazz, element);
            return config.createCommand();
        } catch (Exception e) {
            throw new RuntimeException(String.format("初始化Command<%s>失败,error message=%s", element.getName(),
                    e.getMessage()), e);
        }
    }

    public List<StepCommand> getDataCommands(List<DataCase> caseChain) {
        List<StepCommand> commands = new ArrayList<StepCommand>();
        int count = caseChain.size();
        for (int i = 0; i < count; i++){
            boolean beFollowed = false;
            boolean follow = false;
            if (i < count -1){
                beFollowed = true;
            }
            if (i != 0){
                follow = true;
            }
            StepCommand command = getDataCommand(caseChain.get(i).getExecutor(), caseChain.get(i).getId(), follow, beFollowed);
            commands.add(command);
        }

        return commands;
    }

    private StepCommand getDataCommand(String executor, String caseId, boolean follow, boolean beFollowed) {
        Class<? extends StepConfig> clazz = CONFIG.get(executor);
        if (clazz == null) {
            throw new CommandNotFoundException(executor);
        }
        try {
            StepConfig config = ConfigUtils.initDataCase(clazz, caseId, executor);
            if (config instanceof DSLCommandConfig){
                return ((DSLCommandConfig) config).createCommand(follow, beFollowed);
            } else {
                return config.createCommand();
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("初始化Command<%s>失败,error message=%s", executor,
                    e.getMessage()), e);
        }
    }

    public Class<? extends StepConfig> getConfig(String name) {
        return CONFIG.get(name);
    }

    public void addConfig(String name, Class<? extends StepConfig> config) {
        if (CONFIG.containsKey(name)) {
            throw new RuntimeException("该命令已经存在，请使用其他命令名称:" + name);
        }
        CONFIG.put(name, config);
    }

    static CommandFactory factory = new CommandFactory();

    public static CommandFactory getInstance() {
        return factory;
    }

}
