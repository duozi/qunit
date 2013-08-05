package com.qunar.base.qunit;

import com.qunar.base.qunit.command.Transform;

/**
 * User: zhaohuiyu
 * Date: 6/8/12
 * Time: 11:32 AM
 */
public class FlightJsonTransform extends Transform {
    @Override
    public Object transport(Object original) {
        String body = original.toString().trim();
        body = body.substring(1);
        body = body.substring(0, body.length() - 1);
        return body;
    }
}
