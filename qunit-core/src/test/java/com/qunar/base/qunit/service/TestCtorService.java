package com.qunar.base.qunit.service;

import com.qunar.base.qunit.model.MultipleCtor;
import com.qunar.base.qunit.model.MultipleCtorWithDiffType;
import com.qunar.base.qunit.model.UserCookies;

/**
 * User: zhaohuiyu
 * Date: 7/13/12
 * Time: 10:30 AM
 */
public class TestCtorService {
    public String singleParamCtor(UserCookies userCookies) {
        return String.format("{\"pCookie\":\"%s\",\"vCookie\":\"%s\",\"qCookie\":\"%s\"}", userCookies.getpCookie(), userCookies.getvCookie(), userCookies.getqCookie());
    }

    public String multipleCtor(MultipleCtor multipleCtor) {
        return String.format("{\"a\":\"%s\",\"b\":\"%s\",\"c\":\"%s\"}", multipleCtor.getA(), multipleCtor.getB(), multipleCtor.getC());
    }

    public String multipleCtorWithDiffType(MultipleCtorWithDiffType multipleCtorWithDiffType){
        return String.format("{\"a\":\"%s\",\"b\":%s,\"c\":\"%s\"}",multipleCtorWithDiffType.getA(),multipleCtorWithDiffType.getB(),multipleCtorWithDiffType.getC());
    }
}
