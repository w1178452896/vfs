package com.wyf.vfs.impl;

import com.wyf.vfs.VfsFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/6/5
 */
public class VfsOutPutStream extends ByteArrayOutputStream {

    private VfsFile vfsFile;

    public VfsOutPutStream(VfsFile vfsFile){
        this.vfsFile = vfsFile;
    }

    @Override
    public void close() throws IOException {
        super.flush();
        vfsFile.setAsBytes(toByteArray());
        super.close();
    }
}
