package com.wyf.utils;

import com.wyf.vfs.VfsException;
import com.wyf.vfs.VfsFile;
import com.google.common.base.Strings;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/6/5
 */
public class ZipUtils {

    public static void doCompress(String srcFile, String zipFile) throws IOException {
        doCompress(new File(srcFile), new File(zipFile));
    }

    /**
     * 文件压缩
     * @param srcFile 目录或者单个文件
     * @param zipFile 压缩后的ZIP文件
     */
    public static void doCompress(File srcFile, File zipFile) throws IOException {
        ZipOutputStream out = null;
        FileOutputStream destOutPutStream = null;
        try {
            long begin = System.currentTimeMillis();
            destOutPutStream = new FileOutputStream(zipFile);
            out = new ZipOutputStream(destOutPutStream);
            doCompress(srcFile, out);
            long end = System.currentTimeMillis();
            System.out.println("耗时："+(end-begin)+"毫秒");
        } catch (Exception e) {
            throw e;
        } finally {
            out.flush();
            out.finish();
           // out.close();//记得关闭资源
//            out.flush();
//            out=null;
            destOutPutStream.close();
        }
    }

    public static void doCompress(String filelName, ZipOutputStream out) throws IOException{
        doCompress(new File(filelName), out);
    }

    public static void doCompress(File file, ZipOutputStream out) throws IOException{
        doCompress(file, out, "");
    }

    public static void doCompress(File inFile, ZipOutputStream out, String dir) throws IOException {
        if ( inFile.isDirectory() ) {
            File[] files = inFile.listFiles();
            if (files!=null && files.length>0) {
                for (File file : files) {
                    String name = inFile.getName();
                    if (!"".equals(dir)) {
                        name = dir + "/" + name;
                    }
                    ZipUtils.doCompress(file, out, name);
                }
            }
        } else {
            ZipUtils.doZip(inFile, out, dir);
        }
    }

    public static void doZip(File inFile, ZipOutputStream out, String dir) throws IOException {
        String entryName = null;
        if (!"".equals(dir)) {
            entryName = dir + "/" + inFile.getName();
        } else {
            entryName = inFile.getName();
        }
        ZipEntry entry = new ZipEntry(entryName);
        out.putNextEntry(entry);

        int len = 0 ;
        byte[] buffer = new byte[1024];
        FileInputStream fis = new FileInputStream(inFile);
        while ((len = fis.read(buffer)) > 0) {
            out.write(buffer, 0, len);
            out.flush();
        }
        out.closeEntry();
        fis.close();
    }

    /**
     * zip解压
     * @param srcFile        zip源文件
     * @param destDirPath     解压后的目标文件夹
     * @throws RuntimeException 解压失败会抛出运行时异常
     */
    public static void unZip(File srcFile, String destDirPath) throws RuntimeException {
        long start = System.currentTimeMillis();
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                System.out.println("解压" + entry.getName());
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if(!targetFile.getParentFile().exists()){
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("解压完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        } finally {
            if(zipFile != null){
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *  把文件以zip的形式放入输出流中
     * @param vfsFiles
     * @param outputStream
     */
    public static void  exportZip(VfsFile[] vfsFiles, OutputStream outputStream){
        ZipOutputStream zipOS = new ZipOutputStream(outputStream);
        try {
            doCompress(vfsFiles,zipOS,"");
        } finally {
            try {
                zipOS.flush();
                zipOS.finish();
            } catch (IOException e) {
                throw new VfsException(e);
            }
        }
    }

    /**
     * 压缩多个文件进入输出流中，文件可以是目录
     * @param vfsFiles
     * @param zipOS
     * @param path
     */
    public  static  void doCompress(VfsFile[] vfsFiles,ZipOutputStream zipOS,String path){
        if (vfsFiles==null || vfsFiles.length ==0) {
            return;
        }
        for (VfsFile vfsFile :
                vfsFiles) {
            if (vfsFile.isExist()){
                String newPath = Strings.isNullOrEmpty(path) ? vfsFile.getName() : path+"/"+vfsFile.getName();
                if(vfsFile.isDir()){
                    VfsFile[] childs = vfsFile.getChilds();
                    doCompress(childs,zipOS,newPath);
                }else {
                    doZip(vfsFile,zipOS,newPath);
                }
            }
        }
    }

    /**
     *  压缩文件进入压缩流，文件不能是目录
     * @param vfsFile
     * @param zipOS
     * @param entryName
     */
    public static void doZip(VfsFile vfsFile,ZipOutputStream zipOS,String entryName){
        try {
            if (vfsFile.isDir()) {
                throw new VfsException("目录不能压缩！");
            }
            byte[] content = vfsFile.getContentAsBytes();
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOS.putNextEntry(zipEntry);
            zipOS.write(content,0,vfsFile.getContentSize());
            zipOS.flush();
        } catch (IOException e) {
            throw new VfsException(e);
        }finally {
            try {
                zipOS.closeEntry();
            } catch (IOException e) {
                throw new VfsException(e);
            }
        }
    }

}
