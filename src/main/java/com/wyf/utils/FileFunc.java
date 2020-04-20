package com.wyf.utils;

import com.google.common.base.Strings;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/6/5
 */
public final class FileFunc {
    //定义文件分隔符常量
    public static final char separatorChar = '/';

    public static final String separator = "" + separatorChar;

    /**
     * 文件名内不能包含的字符
     */
    public final static String invalidFnChar = "/\\:*?\"\'<>|\r\n\t\b\f";//增加了一些不能包含特殊字符

    /**
     * 文件名不能包含的字符提示,增加了转义,报错时使用于前台显示
     * ISSUE:BI-8584 add by wandj 2013.6.18
     */
    private final static String invalidFnCharthrow = "/\\:*?\"\'<>|\\r\\n\\t\\b\\f";


    public static String[] sepPath(String path) {
        String format = formatPath(path);
        int index = format.lastIndexOf(FileFunc.separator);
        String parentDir = format.substring(0, index + 1);
        String fileName = format.substring(index + 1);
        if(path.equals(separator)){
            fileName = "/";
            parentDir="";
        }
        return new String[]{parentDir, fileName};
    }

    /**
     * 对传入的路径格式化为特定的字符串,返回形式以/开头,不以/结尾,如"a\\b/////c\\\\\\\\d//\\"返回"/a/b/c/d"
     */
    public static String formatPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return FileFunc.separator;
        }
        if (!FileFunc.isUnixDir(path, 0, path.length() - 1)) {
            return FileFunc.formatUnixDir(path);
        }
        return path;
    }

    /**
     * 格式化成unix目录格式。如果目录为空或者""，都返回/，其目录必须为下列格式：
     * /ab\aa
     * bb\cc
     *
     * @param dir
     * @return
     */
    public static String formatUnixDir(String dir) {
        if (dir == null || dir.length() == 0) {
            return "/";
        }
        if (!isUnixDir(dir, 0, -1)) {
            return formatUnixDir(dir, 0, -1);
        }
        return dir;
    }

    /**
     * 判断字符串的某一段是否是UNIX格式目录，要注意 "/" 的判断，end 可以为-1，表示off开始的所有字符串。有可能
     * 字符串包含连续的// 、\\\\ 、/\ 等，但只保留一个
     *
     * @param dir
     * @param off
     * @param end
     * @return
     */
    public static boolean isUnixDir(String dir, int off, int end) {
        if (end == -1) {
            end = dir.length() - 1;
            if (end == -1) {
                return false;
            }
        }

        char startChar = dir.charAt(off);
        if (off == end) {
            return startChar == '/';
        }

        if (startChar != '/') {
            return false;
        }
        char ch = dir.charAt(end);
        if (ch == '/' || ch == '\\') {
            return false;
        }

        /**
         * 判断off+1 到  end 之间，有没有连续的/\，或者有\
         */
        char lastChar = startChar;
        for (int i = off + 1; i < end; i++) {
            ch = dir.charAt(i);
            if (ch == '\\') {
                return false;
            }

            if (ch == '/' && lastChar == '/') {
                return false;
            }
            lastChar = ch;
        }

        return true;
    }

    /**
     * 把字符串中的某一段格式化成unix文件目录，例如：
     * ""          "/"
     * ab/ac       /ab/ac
     * //          /
     * \\          /
     * /ab/ab/     /ab/ab  ?
     * /ab\ac\     /ab/ac  ?
     *
     * @param dir
     * @param start
     * @param end
     * @return
     */
    public static String formatUnixDir(String dir, int start, int end) {
        if (dir == null || dir.length() == 0 || (dir.length() == 1 && (dir.charAt(0) == '/' || dir.charAt(0) == '\\'))) {
            return "/";
        }

        int len = dir.length();
        if (end == -1) {
            end = len - 1;
        }

        if (start > end || (start == end && dir.charAt(start) == '/')) {
            return dir;
        }

        StringBuffer sb = new StringBuffer(len + 2).append(dir);
        char ch = sb.charAt(end);
        if ((end > start) && ch == '/' || ch == '\\') {
            sb.deleteCharAt(end);
        }

        char lastChar = ch;

        for (int i = end - 1; i >= start; i--) {
            ch = sb.charAt(i);
            if ((ch == '/' || ch == '\\') && (lastChar == '/' || lastChar == '\\')) {
                sb.deleteCharAt(i);
                continue;
            }
            if (ch == '\\') {
                sb.setCharAt(i, '/');
            }
            lastChar = ch;
        }

        if ((lastChar != '/' && lastChar != '\\') || sb.length() == 0) {
            sb.insert(start, '/');
        }

        return sb.toString();
    }

    /**
     * 将path中的\\转换成/
     *
     * @param path
     * @return
     */
    public static String replaceSeparatorChar(String path) {
        String reg = "((\\\\+|/+)(\\\\*/*)*)+";
        return path.replaceAll(reg, "/");
    }

    /**
     * 判断fn是否是一个合法的文件名，不能为null或空，不能包含字符 "/\\:*?\"<>|"，长度不能超过256，
     * 文件名是可以以.开头的，但不能是.和..也不能全部是.或空格组成的
     * 如果参数throwiffail为true且fn不是一个合法的文件名，那么触发异常
     */
    public static final void checkValidFileName(String fn) {
        boolean r = isValidFileName(fn);
        if (!r) {
            /*
             * 增加提示信息，让用户知道是哪里出错了
             * 此信息不用加入日志
             *
             * edit by zhushy 2014.11.10 ISSUE:[资源管理器]ESENFACE-738 修改文件名不合法的提示内容。
             */
            throw new IllegalArgumentException("文件名不合法：" + fn + "\r\n不能包含这些字符{" + invalidFnCharthrow + "},也不能只由空格和.组成,并且长度不能超过256个字符。");
        }
    }

    public static final boolean isValidFileName(String fn) {
        if (fn == null || fn.length() == 0 || fn.length() > 256) {//fix me fn.length()是unicode长度
            return false;
        }
        for (int i = 0; i < fn.length(); i++) {
            if (invalidFnChar.indexOf(fn.charAt(i)) != -1) {
                return false;
            }
        }

        //文件名可以以.开头，但不能只是.或者..，因为这两个字符串有特殊意义
        if (isAll_Dot_Blank(fn)) {
            return false;
        }

        return true;
    }

    /**
     * 字符s是否都是由空格和点号组成的，如果是这样，那不是一个合法的字符画
     */
    private static final boolean isAll_Dot_Blank(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '.' && c != '\n' && c != '\r' && c != '\t' && c != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取文件编码类型
     *
     * @param bytes 文件bytes数组
     * @return      编码类型
     */
    public static String getEncoding(byte[] bytes) {
        String defaultEncoding = "UTF-8";
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        //log.info("字符编码是：{}", encoding);
        if (encoding == null) {
            encoding = defaultEncoding;
        }
        return encoding;
    }


}
