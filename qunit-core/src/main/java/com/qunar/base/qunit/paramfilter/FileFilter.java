package com.qunar.base.qunit.paramfilter;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class FileFilter extends ParamFilter {

    //file:a.jpg|content-type:image/jpeg
    private static final String PREFIX = "file:";


    @Override
    protected Object doHandle(String param) {
        String parameters[] = param.split("\\|");
        String fileName = getFileName(parameters);
        String contentType = getContentType(parameters);
        byte[] data = readFile(fileName);
        return new FileUpload(contentType, fileName, data);
    }

    private String getContentType(String[] parameters) {
        return get(parameters, "content-type");
    }

    private String getFileName(String[] parameters) {
        return get(parameters, "file");
    }

    private String get(String[] parameters, String name) {
        for (String parameter : parameters) {
            if (parameter.startsWith(name))
                return parameter.substring(parameter.indexOf(":") + 1);
        }
        return null;
    }

    private byte[] readFile(String fileName) {
        InputStream is = null;
        try {
            is = readInputStream(fileName);
            if (is == null) {
                throw new RuntimeException("读取上传文件失败");
            }
            return toByte((new BufferedInputStream(is)));
        } catch (Exception e) {
            logger.error("读取上传文件失败" + fileName, e);
            throw new RuntimeException("读取上传文件失败" + fileName, e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private InputStream readInputStream(String fileName) throws FileNotFoundException {
        InputStream stream = FileFilter.class.getClassLoader().getResourceAsStream(fileName);
        if(stream == null){
            return new FileInputStream(fileName);
        }
        return stream;
    }

    @Override
    protected boolean support(String param) {
        return param.startsWith(PREFIX);
    }

    public static byte[] toByte(InputStream is) {
        byte[] result = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                os.write(buffer, 0, len);
            }
            result = os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(os);
        }
        return result;
    }


}

