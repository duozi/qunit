package com.qunar.base.qunit.dsl;

import com.qunar.base.qunit.command.ParameterizedCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.preprocessor.DataCaseProcessor;
import com.qunar.base.qunit.response.Response;

import java.util.*;

/**
 * User: zhaohuiyu
 * Date: 2/20/13
 * Time: 4:08 PM
 */
public class DSLCommand extends ParameterizedCommand {

    private final static Set<String> COMMADND_RUNRECORD = new HashSet<String>();

    private DSLCommandDesc desc;
    private List<StepCommand> commands;
    private Map<String, String> commandParam;

    public DSLCommand(DSLCommandDesc desc, List<KeyValueStore> params, List<StepCommand> commands) {
        super(params);
        this.desc = desc;
        this.commands = commands;
    }

    @Override
    protected Response doExecuteInternal(Response preResult, List<KeyValueStore> processedParams, Context context) throws Throwable {
        Response response = preResult;
        Boolean run = COMMADND_RUNRECORD.contains(desc.id());
        if (desc.runOnce() && run) {
            return response;
        }
        COMMADND_RUNRECORD.add(desc.id());
        Context childContext = new Context(context);
        Map<String, Map<String, String>> dataMap = desc.data();
        for (KeyValueStore processedParam : processedParams) {
            childContext.addContext(processedParam.getName(), processedParam.getValue());
            if ("data".equals(processedParam.getName())){
                setCommandParam(dataMap.get(processedParam.getValue()));
            	addContext(dataMap, processedParam, childContext);
            }
        }
        for (StepCommand child : commands) {
            response = child.doExecute(response, childContext);
        }
        return response;
    }

    @Override
    protected StepCommand doClone() {
        List<StepCommand> cloneCommands = new ArrayList<StepCommand>();
        for (StepCommand command : commands) {
            cloneCommands.add(command.cloneCommand());
        }
        return new DSLCommand(this.desc, this.params, cloneCommands);
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "执行:");
        details.put("name", desc.desc());
        details.put("params", convertMapToList(getCommandParam()));
        return details;
    }
    
    private void addContext(Map<String, Map<String, String>> dataMap, KeyValueStore processedParam, Context childContext){
    	Map<String, String> keyValueMap = dataMap.get(processedParam.getValue());
    	Iterator iterator = keyValueMap.entrySet().iterator();
    	while(iterator.hasNext()){
    		Map.Entry<String, String> entry = (Map.Entry<String, String>)iterator.next();
    		childContext.addContext(entry.getKey(), entry.getValue());
    	}
    }

    public Map<String, String> getCommandParam() {
        return commandParam;
    }

    public void setCommandParam(Map<String, String> commandParam) {
        this.commandParam = commandParam;
    }

    private List<KeyValueStore> convertMapToList(Map<String, String> map){
        List<KeyValueStore> keyValueStores = new ArrayList<KeyValueStore>();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry<String, String>)iterator.next();
            keyValueStores.add(new KeyValueStore(entry.getKey(), entry.getValue()));
        }

        return keyValueStores;
    }
}
