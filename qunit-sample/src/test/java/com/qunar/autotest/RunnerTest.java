package com.qunar.autotest;

import com.qunar.base.qunit.Qunit;
import org.junit.runner.RunWith;

/**
 * User: zhaohuiyu
 * Date: 5/8/13
 * Time: 11:25 PM
 */
@RunWith(Qunit.class)
/*
before 在所有case执行之前执行
after 在所有case执行之后执行
files xml case所在位置
tags 需要执行哪些类型的case
service 被测接口配置文件
dsl 提取的业务抽象
 */
@Qunit.Options(before = "before.xml",
        after = "after.xml",
        files = "cases/*.xml", tags = "automated",
        service = "service/service.xml", dsl = "dsl.xml")
public class RunnerTest {
}
