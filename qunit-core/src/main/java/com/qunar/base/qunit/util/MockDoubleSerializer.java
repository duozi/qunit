package com.qunar.base.qunit.util;

import com.alibaba.fastjson.serializer.*;
import com.qunar.autotest.mock.Mock;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;

/**
 * Created by zhaohui.yu
 * 6/15/15
 */
public class MockDoubleSerializer implements ObjectSerializer {
    public final static MockDoubleSerializer instance = new MockDoubleSerializer();

    private DecimalFormat decimalFormat = null;

    public MockDoubleSerializer() {

    }

    public MockDoubleSerializer(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }

    public MockDoubleSerializer(String decimalFormat) {
        this(new DecimalFormat(decimalFormat));
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            if (serializer.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                out.write('0');
            } else {
                out.writeNull();
            }
            return;
        }

        double doubleValue = ((Double) object).doubleValue();

        if (Double.isNaN(doubleValue)) {
            out.writeNull();
        } else if (Double.isInfinite(doubleValue)) {
            out.writeNull();
        } else {
            String doubleText;
            if (decimalFormat == null) {
                doubleText = Double.toString(doubleValue);
                if (doubleText.endsWith(".0")) {
                    doubleText = doubleText.substring(0, doubleText.length() - 2);
                }
            } else {
                doubleText = decimalFormat.format(doubleValue);
            }
            out.append(doubleText);
        }
    }
}
