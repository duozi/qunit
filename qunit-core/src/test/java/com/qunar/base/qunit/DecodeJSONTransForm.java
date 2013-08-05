package com.qunar.base.qunit;

import com.qunar.base.qunit.command.Transform;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * 解析返回请求
 * User: luning.sun
 * Date: 12-7-2  上午10:55
 */
public class DecodeJSONTransForm extends Transform<String> {

    @Override
    public Object transport(String transformBody) throws Throwable {
        return getJson(transformBody);
    }

    private String getJson(String body) throws UnsupportedEncodingException {

        char[] chars = body.toCharArray();
        byte[] bytes = getBytes(chars);
        CharBuffer a = CharBuffer.wrap(chars);

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.nativeOrder());

        byte[] tag = new byte[4];
        byte[] serviceType = new byte[4];
        buffer.get(tag).get(serviceType);

        int errorCode = buffer.getInt();
        byte[] cp = new byte[1];
       buffer.get(cp);

//        byte[] dataLength = new byte[4];
//        buffer.get(dataLength);
        int dataLength = buffer.getInt();

        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);

        System.out.println("返回结果  tag=  serviceType= " + getInt(serviceType, 0) + " errorCode= " + errorCode + " cp= " + cp + " dataLength=  result= " + new String(result));

        return new String(result);
    }

//    private char[] sub(char[] )


    public static int getInt(byte[] b, int startIndex) {
        int ch1 = convertbytetoint(b[3 + startIndex]);
        int ch2 = convertbytetoint(b[2 + startIndex]);
        int ch3 = convertbytetoint(b[1 + startIndex]);
        int ch4 = convertbytetoint(b[0 + startIndex]);
        int r = (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
        return r;
    }

    public static int convertbytetoint(byte b) {
        return b & 0xff;
    }

    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);

        return bb.array();
    }

}
