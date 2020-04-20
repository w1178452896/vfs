package com.wyf.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.wyf.utils.FileFunc;
import com.wyf.vfs.VfsException;
import com.wyf.vfs.VfsFile;
import com.wyf.vfs.impl.VfsMimeType;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/6/13 14:52
 */
@RestController
@RequestMapping("")
@Slf4j
public class VfsFileController {

    @Autowired
    VfsFile vfs;

    /**
     *  获取文件
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @GetMapping("/vfs/**")
    public void load(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = request.getServletPath().replaceFirst("/vfs", "");
        VfsFile vfsFile = this.vfs.getVfsFile(path);
        if (!vfsFile.isExist()) {
//            errorPage(404,response,request,vfsFile,new RuntimeException("文件不存在！"));
            response.setStatus(404);
            response.sendRedirect(request.getContextPath()+"/vfsPage/404.html");
            return;
        }
        if(vfsFile.isDir()){
            response.sendRedirect("../vfsPage/index.html");
        }else{
            InputStream in = null;
            try {
                String ae = request.getHeader("Accept-Encoding");
                String userAgent = request.getHeader("user-agent");
                if (ae != null && ae.indexOf("gzip") != -1
                        && (!Strings.isNullOrEmpty(userAgent) && userAgent.indexOf("MSIE 6.0") == -1 || Strings.isNullOrEmpty(userAgent))) {//浏览器支持gzip传送
                    response.setHeader("Content-Encoding", "gzip");
                    in = vfsFile.getGzipInputStream();//直接返回GZIP流
                    if (in == null) {
                        return;
                    }
                    int len = in.available();
                    if (len > 0) {
                        response.setContentLength(len);
                    }
                }
                else {
                    //res.setHeader("Content-Encoding", ""); 会在http头上增加一个空的头，这样在ISA代理服务器上会有问题。
                    if (response.containsHeader("Content-Encoding")) {
                        response.setHeader("P3P", null);
                        response.setHeader("Pragma", null);
                        response.setHeader("Cache-Control", null);
                        response.setHeader("Expires", null);
                        response.setHeader("Content-Type", null);
                        response.setHeader("Date", null);
                    }
                    response.setContentLength((int) vfsFile.length());
                    in = vfsFile.getInputStream();
                    if (in == null) {
                        return;
                    }
                }
                String contenttype = vfsFile.guessMimeType();//获得文件的类型,默认为application/octet-stream
                /**
                 * 如果contenttype为空, 则在websphere中不能调用response.setContentType(contenttype);
                 * 否则会出现异常:
                 * java.lang.NullPointerException at com.ibm.ws.webcontainer.srt.SRTServletResponse.setContentType(SRTServletResponse.java:1102)
                 */
                if (!Strings.isNullOrEmpty(contenttype)) {
                    String charset = vfsFile.getCharset();
                    if (!Strings.isNullOrEmpty(charset)) {//如果编码不为null,加入编码
                        contenttype += "; charset=" + charset;
                    }
                    response.setContentType(contenttype);
                }
                //response.setCharacterEncoding(charset);//此方法不是每个服务器都支持的，webloigc就不支持，最好用response.setContentType(contenttype+"; charset="+charset);
                response.resetBuffer();
                int n=0;
                byte[] buf=new byte[1024];
                ServletOutputStream out = response.getOutputStream();
                while ( (n=in.read(buf) ) > 0 ){
                    out.write(buf,0,n);
                }
//                out.close();
            } catch (IOException e) {
                log.error("",e);
            } finally {
                if (in!=null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.error("",e);
                    }
                }
            }
        }
    }

    /**
     * 获取文件信息，查找与下载。
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/file/**", method = RequestMethod.GET)
    public String getVfsFile2(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getServletPath().replaceFirst("/file", "");
        String cmd = request.getParameter("cmd");
        if (Strings.isNullOrEmpty(cmd)){
           return getFiles(path);
        }
        switch (cmd){
            case "search" :  return search(path,request);
            case "download" : return download(path,request,response);
            default: return null ;
        }
    }

    /**
     * 返回前台属性节点所需要的信息(只包含文件夹)
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/file/treeInfo4Move/**", method = RequestMethod.GET)
    public String getInfo(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getServletPath().replaceFirst("/file/treeInfo4Move", "");
        VfsFile vfsFile = this.vfs.getVfsFile(path);
        if (!vfsFile.isExist()) {
            throw new VfsException("文件不存在！");
        }
        boolean childs = path.endsWith("/");
        if (!childs){
            throw new RuntimeException("参数必须为文件夹!");
        }else{
            return  this.VfsFile2TreeInfo(vfsFile.getChilds());
        }
    }

    /**
     *  下载
     * @param path
     * @param request
     * @param response
     * @return
     */
    private String download(String path, HttpServletRequest request, HttpServletResponse response) {
        if (Strings.isNullOrEmpty(path)) {
            return null;
        }
        String[] names = path.split(",");
        ArrayList<VfsFile> files = new ArrayList<>();
        for (String name : names) {
            VfsFile vfsFile = vfs.getVfsFile(name);
            if(!vfsFile.isExist()) {
                throw new RuntimeException(vfsFile.getAbsolutePath()+"不存在请检查！");
            }
            files.add(vfsFile);
        }
        ServletOutputStream outputStream = null;
        try {
            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(  "VFSFiles.zip"    , "UTF-8"));
             outputStream = response.getOutputStream();
            vfs.exportZipPackage(files.toArray(new VfsFile[files.size()]),outputStream);
        } catch (IOException e) {
            throw new RuntimeException("下载错误："+e.getMessage(),e);
        }finally {
            IOUtils.close(outputStream);
        }
        return null;
    }

    /**
     * 查找文件
     * @param path
     * @param request
     */
    private String search(String path,HttpServletRequest request) {
        VfsFile vfsFile = this.vfs.getVfsFile(path);
        if (!vfsFile.isExist()) {
            throw new VfsException("文件不存在！");
        }
        String keyword = request.getParameter("keyword");
        VfsFile[] vfsFiles = vfsFile.listFiles(keyword, true);
        return VfsFile2Json(vfsFiles);
    }

    /**
     * 获取文件信息
     * @param path
     * @return
     */
    private String getFiles(String path) {
        VfsFile vfsFile = this.vfs.getVfsFile(path);
        if (!vfsFile.isExist()) {
            throw new VfsException("文件不存在！");
        }
        boolean childs = path.endsWith("/");
        if (!childs){
            return this.VfsFile2Json(new VfsFile[]{vfsFile});
        }else{
            return  this.VfsFile2Json(vfsFile.getChilds());
        }
    }

    /**
     * 添加文件
     * @param params
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/file/**" , method = RequestMethod.POST)
    public String addFile(@RequestBody JSONObject params, HttpServletRequest request, HttpServletResponse response){
        String path = request.getServletPath().replaceFirst("/file", "");

        boolean isFile = params.getBoolean("isFile");
        String name = params.getString("name");

        VfsFile newFile = this.vfs.getVfsFile(path + name);

        if (newFile.isExist()) {
            throw new RuntimeException("当前目录下已存在同名文件！");
        }

        if (isFile){
            newFile.create();
        }else{
            newFile.mkdirs();
        }
        return "success";

    }

    /**
     * 批量删除文件
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/file/**" , method = RequestMethod.DELETE)
    public String deleteFiles(HttpServletRequest request, HttpServletResponse response){
        String path = request.getServletPath().replaceFirst("/file", "");
        if (Strings.isNullOrEmpty(path)) {
            return null;
        }

        vfs.startTransaction();
        try {
            String[] names = path.split(",");
            for (String name : names) {
                if (FileFunc.separator.equals(name)) {
                    throw new VfsException("不能删除根目录!");
                }
                VfsFile file = this.vfs.getVfsFile(name);
                file.delete();
            }
            vfs.commitTransaction();
        }finally {
            vfs.endTransaction();
        }
        return "success";
    }

    /**
     *  上传文件
     *  参数：1：parentDir 父文件路径
     *      2：file 上传的文件
     * @param files
     * @param request
     */
    @PostMapping("/file/upload")
    public void upload(@RequestParam("file") MultipartFile[] files,HttpServletRequest request){
        String parentDir = request.getParameter("parentDir");
        for (MultipartFile file : files) {
            VfsFile vfsFile = vfs.getVfsFile(parentDir+file.getOriginalFilename());
            if (vfsFile.isExist()) {
                throw new RuntimeException("当前目录下已存在同名文件:"+vfsFile.getAbsolutePath());
            }
            try {
                byte[] content = file.getBytes();
                vfsFile.setAsBytesNoTransaction(content);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(),e);
            }
        }
    }

    /**
     * 获取文件内容，返回string，仅支持mimetype以text开头的文件
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/file/content/**" , method = RequestMethod.GET)
    public String getContent(HttpServletRequest request, HttpServletResponse response){
        String path = request.getServletPath().replaceFirst("/file/content", "");
        VfsFile vfsFile = this.vfs.getVfsFile(path);
        if (!vfsFile.isExist()) {
            throw new VfsException("文件不存在！");
        }
        String mimetype = vfsFile.guessMimeType();
        if (Strings.isNullOrEmpty(mimetype)) {
            throw new VfsException("文件类型无法识别,没有设置文件类型");
        }
        if (!VfsMimeType.isTextContentType(mimetype)) {// 判断是否文本文件
            //ESENBI-636 稍微增强一下提示信息  yizhl 20150301
            throw new VfsException("仅支持在线打开文本文件，其他类型文件请下载到本地打开");

        }
        return vfsFile.getContentAsString();
    }


    /**
     * 把前台编辑的文件内容保存起来。
     * @param params
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/file/content/**",method = RequestMethod.PUT)
    public String setContent(@RequestBody JSONObject params, HttpServletRequest request, HttpServletResponse response){
        String path = request.getServletPath().replaceFirst("/file/content", "");
        String content = params.getString("content");
        VfsFile vfsFile = this.vfs.getVfsFile(path);
        if (!vfsFile.isExist()) {
            throw new VfsException("文件不存在！");
        }

        vfsFile.saveAsString(content);
        return "success";
    }

    /**
     * 重命名
     * @param params
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/file/name/**",method = RequestMethod.PUT)
    public String rename(@RequestBody JSONObject params, HttpServletRequest request, HttpServletResponse response){
        String path = request.getServletPath().replaceFirst("/file/name", "");
        String newName = params.getString("newName");
        VfsFile file = this.vfs.getVfsFile(path);
        if (!file.isExist()) {
            throw new VfsException("文件不存在！");
        }
        file.renameTo(newName);
        return "success";
    }

    /**
     * 移动文件
     * @param params
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/file/parentDir/**",method = RequestMethod.PUT)
    public String move(@RequestBody JSONObject params, HttpServletRequest request, HttpServletResponse response){
        String pathsStr = request.getServletPath().replaceFirst("/file/parentDir", "");
        String destDir = params.getString("destDir");
        if(Strings.isNullOrEmpty(pathsStr)){
            return null;
        }
        vfs.startTransaction();
        try {
            String[] paths = pathsStr.split(",");
            VfsFile destDirFile = this.vfs.getVfsFile(destDir);
            for (String path : paths) {
                VfsFile file = this.vfs.getVfsFile(path);
                if (!file.isExist()) {
                    throw new VfsException("文件不存在！");
                }
                file.moveTo(destDirFile);
            }
            vfs.commitTransaction();
        } finally {
            vfs.endTransaction();
        }
        return "success";
    }

    /**
     * 编辑接口
     * @param params
     * @param request
     * @param response
     * @return
     */
    @PutMapping(value="/file/**")
    public String update(@RequestBody JSONObject params, HttpServletRequest request, HttpServletResponse response){
        String path = request.getServletPath().replaceFirst("/file", "");
        VfsFile file = this.vfs.getVfsFile(path);
        if (!file.isExist()) {
            throw new VfsException("文件不存在！");
        }
        JSONObject ext = params.getJSONObject("ext");
        for (String key : ext.keySet()) {
            file.setExtValue(key,ext.get(key));
        }
        file.create();
        return "success";
    }

    /**
     *  把vfsFile封装为前台树行控件所需要的json数据
     * @param vfsFiles
     * @return
     */
    private String VfsFile2TreeInfo(VfsFile[] vfsFiles){
        JSONArray array = new JSONArray();
        if(vfsFiles != null && vfsFiles.length!=0){

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (VfsFile vfsFile : vfsFiles) {
                if(vfsFile.isDir()){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("key",vfsFile.getAbsolutePath());
                    jsonObject.put("name",vfsFile.getName());
                    array.add(jsonObject);
                }
            }
        }
        return array.toJSONString();
    }
    /**
     * 封装vfsFile为json数据（手动封装是因为fastjson用在此处会报错）
     * @param vfsFiles
     * @return
     */
    private String VfsFile2Json(VfsFile[] vfsFiles){
        JSONArray array = new JSONArray();
        if(vfsFiles != null && vfsFiles.length!=0){

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (VfsFile vfsFile : vfsFiles) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",vfsFile.getName());
                jsonObject.put("parentDir", vfsFile.getPath());
                jsonObject.put("size",vfsFile.getContentSize());
                jsonObject.put("updateDate",dateFormat.format(vfsFile.getLastModified()));
                jsonObject.put("charset",vfsFile.getCharset());
                jsonObject.put("type",vfsFile.getMimeType());
                jsonObject.put("ext",JSONObject.toJSON(vfsFile.getAllExtValue()));
                jsonObject.put("dir",vfsFile.isDir());
                array.add(jsonObject);
            }
        }
        return array.toJSONString();
    }

    /**
     * 当此servlet出现异常，需要向客户端发送一个错误页面时调用，子类可以重载此方法
     * @param errorcode
     * @param response
     * @param request
     * @param vf 表示对应的文件对象，可能为null
     * @param ex
     * @throws IOException
     * @throws ServletException
     */
        protected void errorPage(int errorcode, HttpServletResponse response, HttpServletRequest request, VfsFile vf,
                             Exception ex)  throws IOException, ServletException {
        response.setContentType("text/html; charset=UTF-8"); //如果使用res.setCharacterEncoding("UTF-8")，那么weblogic 8.1 sunos 5.9 上这句话有异常
        response.setStatus(errorcode);
            PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
            out.print("<title>");
        //out.print(errorcode + "错误！");
        /**

         com.esen.platform.action.actionvfsabs.4=文件没找到或者已经被删除！
         com.esen.platform.action.actionvfsabs.5=服务器内部出现错误：
         */
        out.print(errorcode + "错误！");

        out.println("</title>");
        out.println("</head>");
        out.println("<body>");
        if (vf != null) {
            //out.println("<p style=\"font-size:18px;\">" + vf.getName() + "访问出错！" + "</p>");
            out.println("<p style=\"font-size:18px;\">" + vf.getName() + "访问出错！" + "</p>");
            //out.println("<p style=\"font-size:12px;\">" + "文件没找到或者已经被删除！" + "</p>");
            out.println("<p style=\"font-size:12px;\">文件没找到或者已经被删除！"
                    + "</p>");
        }
        if (ex != null) {
            //out.println("<p style=\"font-size:12px;\">" + "服务器内部出现错误：" + StrFunc.exceptionMsg2str(ex) + "</p>");
            out.println("<p style=\"font-size:12px;\">"
                    + "服务器内部出现错误："
                    + ex.getMessage() + "</p>");
            out.println("<p style=\"font-size:12px;line-height:18px;\">" + ex.getMessage() + "</p>");
        }
        out.println("</body>");
        out.println("</html>");
    }

}
