package com.wyf.vfs.impl;

import com.wyf.vfs.VfsException;
import com.google.common.base.Strings;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Properties;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:  一些默认的文件后缀的MimeType
 * @date 2019/6/17 15:43
 */
@Component
public class VfsMimeType {
    public static final String TEXTTAG = "text/";//如果是以text/开始的MimeType,则认为是文本文件

    private static Properties mimeType = new Properties();

    @PostConstruct
    public void init(){
        InputStream in =null;
        try {
             in = VfsMimeType.class.getClassLoader().getResourceAsStream("conf/mimetype.properties");
             mimeType.load(in);
        } catch (Exception e) {
            throw new VfsException("加载minetype.properties文件失败！",e);
        }finally {
            IOUtils.closeQuietly(in);
        }

    }

    /**
     * 根据后缀返回其对于的ContentType，后缀可以点号开头也可不，例如：<br>
     * getContentType(".txt") 返回 text/plain
     * getContentType("txt") 返回 text/plain
     */
    public static  String getContentType(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        int i = name.lastIndexOf('.');
        if (i >= 0) {
            name = name.substring(i + 1);
        }
        name = name.toLowerCase();
        String contentType = mimeType.getProperty(name);
        if (Strings.isNullOrEmpty(contentType)){
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    public static void main(String[] args) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor("aaa.xml");
        System.out.println(contentTypeFor);
    }

    public  static boolean isTextContentType(String mimetype) {
        //toLowerCase效率好些，因为mimetype通常都是小写的
        return !Strings.isNullOrEmpty(mimetype) && mimetype.toLowerCase().startsWith(TEXTTAG);
    }

}
