package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.paramfilter.FileUpload;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;

public class FileUploadWriter extends ByteArrayOutputStream {
    private final static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final String boundary = getMultipartDelimiter();

    public void write(List<KeyValueStore> parameters) {
        for (KeyValueStore parameter : parameters) {
            writeFormField(parameter);
            writeFileField(parameter);
        }
        writeEnd();
    }

    private void writeEnd() {
        writeBoundary(boundary + "--");
    }

    private void writeFormField(KeyValueStore parameter) {
        if (parameter.getValue() instanceof String) {
            writeBoundary(boundary);
            writeHeader(parameter.getName());
            writeEmptyLine();
            writeValue(parameter.getValue());
        }
    }

    private void writeFileField(KeyValueStore parameter) {
        Object value = parameter.getValue();
        if (value instanceof FileUpload) {
            writeBoundary(boundary);
            FileUpload fileUpload = (FileUpload) value;
            writeFileHeader(parameter, fileUpload);
            writeEmptyLine();
            write(fileUpload.getData(), 0, fileUpload.getData().length);
            writeEmptyLine();
        }
    }

    private void writeBoundary(String boundary) {
        byte[] buffer = ("--" + boundary).getBytes(DEFAULT_CHARSET);
        write(buffer, 0, buffer.length);
    }

    private void writeHeader(String name) {
        byte[] buffer = format("\r\nContent-Disposition: form-data; name=\"%s\"\r\n", encode(name, DEFAULT_CHARSET)).getBytes(DEFAULT_CHARSET);
        write(buffer, 0, buffer.length);
    }

    private void writeEmptyLine() {
        byte[] buffer = "\r\n".getBytes(DEFAULT_CHARSET);
        write(buffer, 0, buffer.length);
    }

    private void writeValue(Object value) {
        byte[] buffer = (value.toString() + "\r\n").getBytes(DEFAULT_CHARSET);
        write(buffer, 0, buffer.length);
    }

    private void writeFileHeader(KeyValueStore parameter, FileUpload fileUpload) {
        StringBuilder sb = new StringBuilder();
        sb.append(format("\r\nContent-Disposition: form-data; name=\"%s\"; filename=\"%s\"", encode(parameter.getName(), DEFAULT_CHARSET), fileUpload.getFileName()));
        sb.append(format("\r\nContent-Type: %s\r\n", fileUpload.getContentType()));
        byte[] bytes = sb.toString().getBytes(DEFAULT_CHARSET);
        write(bytes, 0, bytes.length);
    }

    private String getMultipartDelimiter() {
        Random random = new Random();
        return "Qunit" + Long.toHexString(random.nextLong()).toLowerCase();
    }

    private static String encode(String s, Charset charset) {
        if (StringUtils.isBlank(s)) return "";
        try {
            return URLEncoder.encode(s, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(charset.name(), e);
        }
    }

    public String getBoundary() {
        return boundary;
    }
}
