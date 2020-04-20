package com.wyf.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/7/19 15:58
 */
@Slf4j
public class IOUtil {

    private static final Logger log = LoggerFactory.getLogger(IOUtil.class);

    private static void close(Closeable close) {

        if (close != null) {
            try {
                close.close();
            } catch (IOException e) {
                log.error("",e);
            }
        }
    }

    /**
     * 返回一个压缩过的流,由调用处关闭连接,必须关闭,否则可能会出现异常
     * @param in
     * @return
     * @throws IOException
     */
    public static final InputStream getGZIPStm(InputStream in) throws IOException {
        if (in == null) {
            return null;
        }
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);//支持mark操作
        }
        int GZIP_MAGIC = 0x8b1f;//gizp
        in.mark(10);
        int ch1 = in.read();//如果流中没有数据了，会返回-1不会抛出异常
        int ch2 = in.read();
        in.reset();
        if ((ch2 << 8) + (ch1 << 0) == GZIP_MAGIC) {
            return in;//已经是gzip
        }
        return new GzipInStm(in);
    }


    /**
     * 从一个流中复制指定的长度的类容到另一个流中,如果从源流中不能再读入数据则返回复制了的数据的字节数
     */
    static private final int BUF_SIZE = 1024 * 8;

    static public final long stmCopyFrom(InputStream in, OutputStream out, long sz)
            throws IOException {
        byte[] buf = new byte[BUF_SIZE];
        long rst = 0;
        int r;
        while (sz > 0) {
            r = (int) (sz > BUF_SIZE ? BUF_SIZE : sz);
            r = in.read(buf, 0, r);
            if (r < 1) {
                break;
            }
            sz -= r;
            out.write(buf, 0, r);
            rst += r;
        }
        return rst;
    }

    public static int stmTryRead(InputStream in, byte[] bb, int off, int len)
            throws IOException {
        int r;
        int l = 0;
        int t = off;
        while ((r = in.read(bb, t, len - l)) >= 0) {
            t += r;
            l += r;
            if (l == len) {
                break;
            }
        }
        return l;
    }
}
