/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.runner;

import com.qunar.base.qunit.Qunit;
import com.qunar.base.qunit.Qunit.Options;
import org.junit.runner.RunWith;

/**
 * Qunit Runner 测试入口
 *
 * Created by JarnTang at 12-5-21 下午1:30
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
@RunWith(Qunit.class)
@Options(files = {"runnertest/*.xml"}, service = "service.xml")
public class RunnerTest {

}