package com.qunar.base.qunit.dsl;

import com.qunar.base.qunit.annotation.ChildrenConfig;
import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.CommandFactory;
import com.qunar.base.qunit.config.StepConfig;
import com.qunar.base.qunit.reporter.Reporter;

import java.util.List;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 2/18/13
 * Time: 5:13 PM
 * <p/>
 * 这是一个特殊的命令，该命令的执行并不是执行测试的步骤，
 * 该命令执行时候会根据它的子命令生成一个新的命令，新的命令
 * 以id命名。
 * <p/>
 * 这个命令给你的主要作用是实现DSL。在各个team的测试中，往往有一些
 * 公共切通用的部分，开发人员可以利用这个命令定义自己的通用命令：
 * <p/>
 * 定义:
 * <def id="reload" desc="刷新内存" >
 * <call service="reload" />
 * <call service="flushMemcache" />
 * <call service="clearDB" />
 * </def>
 * <p/>
 * 使用:
 * <reload />
 * <p/>
 * 该命令与<template id="reload" /> & <step ref="reload" />相比有什么优势呢？
 * 1 使用该命令无需在使用的case文件里定义template或使用<include />导入外部文件，
 * 我们在一个集中的地方定义系统的所有DSL
 * 2 使用该命令以更友好的方式: <reload />，而不是<step ref="reload" />这种方式
 * 3 使用这种方式定义的命令还可以接收参数：
 * <reload>
 * <param server="127.0.0.1" />
 * </reload>
 * 与系统内置提供的命令别无二致。
 * 4 使用该命令时，测试报告里输出的只是该命令上的描述，其子命令内容不会输出，这将提高测试的抽象层次。
 * 5 生成的测试报告中会输出已经存在的所有DSL风格的命令，这也是文档的一部分。
 * 6 该命令还有一个runOnce属性，当为true时，不管引入该命令多少次，只会在第一次引入时执行。
 * <def id="reload" desc="刷新内存" runOnce="true">
 * ...
 * </def>
 * <p/>
 * 这个的作用还是：文档化。
 * 有的命令只需要执行一次即可，但是如果我们只在第一个地方执行，其他的地方不执行，不引入。则离上下文太远，
 * 不利于case的维护。但是如果多个地方都引入，又可能对性能造成影响。runOnce的作用会是只引入，并在报告中输出
 * 描述(文档)，但只在第一次执行。
 */
public class DefineDSLCommand {
    /*
    定义的命令的名称，必须唯一
     */
    @Property(required = true)
    private String id;

    /*
    对自定义命令的描述，会显示在测试报告里
     */
    @Property(required = true)
    private String desc;

    @Property(defaultValue = "false")
    private String runOnce;

    @ChildrenConfig
    private List<StepConfig> children;

    private Map<String, Map<String, Object>> data;

    public void define(Reporter reporter) {
        DSLCommandDesc dslCommandDesc = new DSLCommandDesc(id, desc, Boolean.valueOf(runOnce), children, data);
        reporter.addDSLCommand(dslCommandDesc);

        DSLCommandConfig.map(id, dslCommandDesc);
        CommandFactory.getInstance().addConfig(id, DSLCommandConfig.class);
    }

    public String getId() {
        return id;
    }

    public Map<String, Map<String, Object>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, Object>> data) {
        this.data = data;
    }

    public void setId(String id) {
        this.id = id;
    }
}
