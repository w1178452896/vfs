package com.wyf.vfs;

import com.alibaba.fastjson.util.IOUtils;
import com.wyf.utils.*;
import com.wyf.vfs.impl.VfsMimeType;
import com.google.common.base.Strings;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.data.annotation.Transient;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * vfsfile的抽象类,定义公共属性和方法
 * @author wangyaofeng
 */

public abstract class AbstractVfsFile implements VfsFile {

    private static final Logger log = LoggerFactory.getLogger(AbstractVfsFile.class);

    public static final String DEFAULT_ENCODING = "UTF-8";//文件默认的编码


    /**
     * 文件名
     */
    protected String name;
    /**
     * 上级目录路径
     */
    protected String parentDir;
    /**
     * 是否是文件，默认true
     */
    protected boolean file=true;
    /**
     * 创建时间
     */
    protected Date createTime;
    /**
     * 修改时间
     */
    protected Date modifyTime;
    /**
     * 拥有者
     */
    protected String owner ;
    /**
     * 修改者
     */
    protected String mender;
    /**
     * 字符集
     */
    protected String charset="UTF-8";
    /**
     *
     */
    protected String mimeType;
    /**
     * 文件大小
     */
    protected int size;
    /**
     * 内容
     */
    @Transient
    protected Object content;

    /**
     * 扩展属性
     */
    protected Map<String,Object> extendPro=new HashMap();
    /**
     * 是否存在
     */
    @Transient
    protected boolean exist=false;

    private static VfsSecurityMgr vfsSecurityMgr;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentDir() {
        return parentDir;
    }

    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }

    @Override
    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMender() {
        return mender;
    }

    public void setMender(String mender) {
        mender = mender;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }



    /**
     * 获取VfsFile
     *
     * @return Object
     * @throws BeansException
     */
    protected   VfsFile getNullVfsFile() {
        try {
            return (VfsFile) SpringUtil.getBean("vfsFile");
        }catch (BeansException e){
            throw new VfsException("从spring容器中获取vfsFile失败："+e.getMessage());
        }
    }

    @Override
    public Object getExtValue(String name) {
        return extendPro.get(name);
    }
    @Override
    public void setExtValue(String key, Object value)  {
        extendPro.put(key,value);
    }
    @Override
    public Map<String, Object> getAllExtValue()  {
        return extendPro;
    }

    /**
     * 判断operator能否读取文件f，如果不能，那么抛出异常VfsException
     */
    protected void checkCanRead(VfsFile f){
        if(vfsSecurityMgr!=null){
            vfsSecurityMgr.checkCanRead(f);
        }
    }

    /**
     * 检查能否改写文件f
     */
    protected void checkCanWrite(VfsFile f){
        if(vfsSecurityMgr!=null){
            vfsSecurityMgr.checkCanWrite(f);
        }
    }

    /**
     * 检查能否创建文件f
     */
    protected void checkCanCreate(VfsFile f){
        if(!f.isExist()) {
            throw new VfsException(f.getAbsolutePath()+"不存在！");
        }
        if(vfsSecurityMgr!=null){
            vfsSecurityMgr.checkCanCreate(f);
        }
    }

    /**
     * 检查能否删除文件f
     */
    protected void checkCanRemove(VfsFile f){
        if(!f.isExist()) {
            throw new VfsException(f.getAbsolutePath()+"不存在！");
        }
        if(vfsSecurityMgr!=null){
            vfsSecurityMgr.checkCanRemove(f);
        }
    }

    @Override
    public boolean isDir() throws VfsException {
        return !isFile();
    }

    @Override
    public void setVfsSecurityMgr(VfsSecurityMgr vfsSecurityMgr) {
        AbstractVfsFile.vfsSecurityMgr = vfsSecurityMgr;
    }

    @Override
    public String getAbsolutePath() {
        if(FileFunc.separator.equals(this.getParentDir()) && FileFunc.separator.equals(this.getName())){
            return FileFunc.separator;
        }
        return this.getParentDir()+this.getName();
    }

    @Override
    public int compareTo(VfsFile o) {
        if(this.isFile() && o.isDir()){
            return 1;
        }else if(this.isDir() && o.isFile()){
            return -1;
        }else{
            return this.getName().compareTo(o.getName());
        }
    }

    @Override
    public long getLastModified() {
        return this.modifyTime.getTime();
    }

    @Override
    public int getContentSize() throws VfsException {
        return this.getSize();
    }

    @Override
    public long length() {
        return this.getSize();
    }

    @Override
    public void exportZipPackage(VfsFile file, OutputStream out) throws VfsException {
        this.exportZipPackage(new VfsFile[]{file},out);
    }

    @Override
    public void exportZipPackage(VfsFile[] files, OutputStream out) throws VfsException {
        ZipUtils.exportZip(files,out);
    }

    @Override
    public void exportZipPackage(OutputStream out) throws VfsException {
        this.exportZipPackage(this,out);
    }

    @Override
    public InputStream getGzipInputStream() throws VfsException {
        try {
            return IOUtil.getGZIPStm(this.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean canRead() {
        try {
            checkCanRead(this);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canWrite() {
        try {
            checkCanRead(this);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canCreate() {
        try {
            checkCanRead(this);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canRemove() {
        try {
            checkCanRead(this);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public String getContentType(String ext) {
        return VfsMimeType.getContentType(ext);
    }

    @Override
    public String guessMimeType() {
        if (this.isDir()) {
            return null;
        }
        //获得文件类型方法，优先级从上到下
        //根据字段MimeType的值获得
        //根据文件名后缀获得
        //从bi系统定义的ContentType中获得
        String mt = this.getMimeType();
        //根据文件后缀名获得
        if (Strings.isNullOrEmpty(mt) ) {
            mt = VfsMimeType.getContentType(this.getName());
        }
        //如何还未null则返回默认类型
        if (Strings.isNullOrEmpty(mt)) {
            mt = "application/octet-stream";
        }
        return mt;
    }

    @Override
    public String getParent() throws VfsException {
        String parentDir = this.getParentDir();
        return getParentByParentDir(parentDir);
    }

    /**
     * 跟据传入的parentDir获取parent
     * @param parentDir
     * @return
     */
    protected String getParentByParentDir(String parentDir){
        if(Strings.isNullOrEmpty(parentDir)){
            return null;
        }
        if(FileFunc.separator.equals(parentDir)){
            return FileFunc.separator;
        }
        return parentDir.substring(0,parentDir.length()-1);
    }

    @Override
    public String getPath() throws VfsException {
        return this.getParentDir();
    }

    @Override
    public void saveAsXml(org.w3c.dom.Document doc, String encoding) throws VfsException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(0);
        try {
            XMLWriter xmlWriter = new XMLWriter(byteOutputStream);
            xmlWriter.write(XmlUtil.parse(doc));
            this.setCharset(encoding);
            this.setAsBytes(byteOutputStream.toByteArray());
        }catch (Exception e) {
            throw new VfsException(e);
        } finally {
            IOUtils.close(byteOutputStream);
        }
    }

    @Override
    public Document getContentAsXml() throws VfsException {
        checkCanRead(this);
        SAXReader saxReader = new SAXReader();
        InputStream inputStream =null;
        try {
            inputStream = this.getInputStream();
            saxReader.setEncoding(this.getCharset());
            return XmlUtil.parse(saxReader.read(inputStream));
        }catch (Exception e) {
            throw new VfsException(e);
        } finally {
            IOUtils.close(inputStream);
        }
    }
    /**
     * 检测文件是否存在
     */
    public void checkExists() {
        if (!this.isExist()) {
            throw new VfsException("文件不存在:"+this.getAbsolutePath());
        }
    }
    @Override
    public void checkExists(boolean isdir) {
        checkExists();
        if (this.isFile() && isdir) {
            throw new VfsException("已经存在同名文件:" + this.getAbsolutePath());
        }
        if (this.isDir() && !isdir) {
            throw new VfsException("已经存在同名文件夹:" + this.getAbsolutePath());
        }
    }



    @Override
    public void ensureExists(boolean isdir) throws VfsException {
        if (!this.isExist()) {
            this.checkCanCreate(this);
            if (isdir) {
                try {
                    this.mkdirs();
                } catch (VfsException e) {
                    throw new VfsException("创建文件夹没有成功:" + this.getAbsolutePath(),e);
                }

            } else  {
                try {
                    this.create();
                } catch (VfsException e) {
                    throw new VfsException("创建文件没有成功:" + this.getAbsolutePath(),e);
                }
            }
        }
        else {
            if (this.isFile() && isdir) {
                throw new VfsException("已经存在同名文件:" + this.getAbsolutePath());
            }
            else if (this.isDir() && !isdir) {
                throw new VfsException("已经存在同名文件夹:" + this.getAbsolutePath());
            }
        }
    }


}
