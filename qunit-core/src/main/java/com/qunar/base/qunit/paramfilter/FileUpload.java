package com.qunar.base.qunit.paramfilter;

import java.util.Arrays;

public class FileUpload {
    private final String fileName;
    private final byte[] data;
    private String contentType;

    public FileUpload(String contentType, String fileName, byte[] data) {
        this.contentType = contentType;
        this.fileName = fileName;
        this.data = Arrays.copyOf(data, data.length);
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }
}
