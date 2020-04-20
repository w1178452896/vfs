package com.wyf.vfs;


import org.w3c.dom.Document;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;

/**
 * 文件接口
 * 定义了一些标准的方法
 * @author wangyaofeng
 */
public interface VfsFile extends Serializable, Comparable<VfsFile>{

	/**
	 * 根据虚拟目录路径文件（目录）名构造并返回一个VfsFile对象.
	 *
	 * @param filename 必须传递且不能为空串或null
	 */
	public VfsFile getVfsFile(String filename);
	/**
	 * 根据目录，跟文件名获取文件
	 * @param parentDir
	 * @param filename
	 * @throws VfsException
	 */
	public VfsFile getVfsFile(String parentDir,String filename) throws VfsException;
	/**
	 * 等于getVfsFile("/",operator)
	 */
	public VfsFile getRoot();

	/**
	 * 以字符串形式返回文件的内容，这是一个获取文件内容的简便函数
	 * @return 如果文件不存在返回null，如果文件存在但内容为空那么返回""，如果文件文件不是文本文件那么返回的内容可能包含有乱码
	 */
	public String getFileAsString(String filename) throws VfsException;


	/**
	 * 将指定的字符串保存到指定的文件中覆盖文件的内容
	 * @throws VfsException 无法写入文件或其他原因造成无法保存时抛出
	 */
	public void setFileAsString(String filename, String content ) throws VfsException;

	/**
	 * 以byte数组形式返回文件的内容，这是一个获取文件内容的简便函数
	 * @return 如果文件不存在返回null，如果文件存在但内容为空那么返回new byte[0]
	 */
	public byte[] getFileAsBytes( String filename) throws VfsException;

	/**
	 * 将指定的byte数组保存到指定的文件中覆盖文件的内容
	 * @throws VfsException 无法写入文件或其他原因造成无法保存时抛出
	 */
	public void setFileAsBytes(String filename, byte[] buf) throws VfsException;

	/**
	 * 删除指定的文件
	 * 如果文件存在，并且成功删除,返回true，
	 * 如果不存在，则直接返回true
	 * @throws VfsException 当文件存在且无法完成删除时抛出
	 */
	public void removeFile(String filename) throws VfsException;

	/**
	 * 根据后缀返回其对于的ContentType，后缀可以点号开头也可不，例如：<br>
	 * getContentType(".txt") 返回 text/plain
	 * <br>
	 * getContentType("txt") 返回 text/plain
	 */
	public String getContentType(String ext);


	/**
	 * 设置此vfs的外部权限实现模块，以前的vfs是内置权限的，现在的实现不内置权限了，所有的权限判断都委托给接口VfsSecurityMgr2
	 * 默认的vfs是没有SecurityMgr的，即不进行权限判断
	 */
	public void setVfsSecurityMgr(VfsSecurityMgr smgr);

	/**
	 * 将文件导出为zip包,zip包的内容会输出到OutputStream中
	 * 如果文件不存在或没有读权限,则会抛出异常
	 */
	public void exportZipPackage(VfsFile file, OutputStream out) throws VfsException;

	/**
	 * 将多个文件导出为一个zip包,zip包的内容会输出到OutputStream中
	 * 如果文件不存在或没有读权限,则会抛出异常
	 * 如果这多个文件中有两个文件重名,则会抛出异常,文件名忽略大小写
	 *
	 * @param files
	 * @param out
	 * @throws VfsException
	 */
	public void exportZipPackage(VfsFile[] files, OutputStream out) throws VfsException;

	/**
	 * 导入文件时会限制文件的最大大小限制的属性名
	 */
	public static final String VFS_OPTION_UPLOAD_MAXFILESIZE = "upload.maxfilesize";

	/**
	 * 获取扩展属性的值
	 * @param name
	 * @return
	 */
	public Object getExtValue(String name);

	/**
	 * 设置扩展属性
	 * @param key
	 * @param value
	 * @throws VfsException
	 */
	public void setExtValue(String key,Object value) ;

	/**
	 *  获取所有的扩展属性,以map的形式返回
	 * @throws VfsException
	 */
	public Map<String,Object> getAllExtValue() ;
	
	/**
	 * 是否是文件夹
	 * @return
	 * @throws VfsException
	 */
	public boolean isDir() throws VfsException;
	/**
	 * 获取内容作为字符串
	 * @return
	 * @throws VfsException
	 */
	public String getContentAsString() throws VfsException;
	/**
	 * 获取内容作为字节数组
	 * @return
	 * @throws VfsException
	 */
	public byte[] getContentAsBytes() throws VfsException;
	/**
	 * 返回文件内容对应的XML的Document
	 * @return 文件不存在或内容为空,返回null,否则构建Document并返回
	 * @throws VfsException 没有权限或此文件是目录
	 */
	public Document getContentAsXml() throws VfsException;
	/**
	 * 如果是一个文件获得一个文件的写入流，如果文件不存在则创建它,如果文件所在目录不存在,也创建之
	 * 调用者要负责关闭返回的流。
	 * 
	 * @throws Exception 没有权限或此文件是一个目录
	 */
	public OutputStream getOutPutStream() throws VfsException;
	/**
	 * 如果是一个文件则获得一个文件的读出流，调用者要负责关闭返回的流。
	 * 
	 * @return 就算文件的内容是空的,返回的InputStream内容为空,不会为null.
	 * @throws VfsException 没有权限,文件不存在或此文件是一个目录
	 */
	public InputStream getInputStream() throws VfsException;
	/**
	 * 获取文件大小
	 * @return
	 * @throws VfsException
	 */
	public int getContentSize() throws VfsException;
	/**
	 *  保存文件
	 * @param v   
	 * @param charset  字符集
	 * @throws VfsException
	 */
	public void saveAsString(String v, String charset) throws VfsException;
	/**
	 * 保存文件
	 * @param bytes
	 * @throws VfsException
	 */
	public void setAsBytes(byte[] bytes) throws VfsException;
	/**
	 * 保存文件,不用事务
	 * 在mongodb实现中如果一次事务中操作的数据大于16M便会报错，因此出现此方法
	 * @param bytes
	 * @throws VfsException
	 */
	public void setAsBytesNoTransaction(byte[] bytes) throws VfsException;
	/**
	 * 保存文件
	 * @param v
	 * @throws VfsException
	 */
	public void saveAsString(String v) throws VfsException;
	/**
	 * 保存文件
	 * @param doc
	 * @param encoding
	 * @throws VfsException
	 */
	public void saveAsXml(Document doc, String encoding) throws VfsException;

	/**
	 * 获取文件路径，不包括文件名
	 * @return
	 * @throws VfsException
	 */
	public String getPath() throws VfsException;
	/**
	 * 获取文件名
	 * @return
	 * @throws VfsException
	 */
	public String getName() throws VfsException;
	/**
	 * 获取文件夹的子文件
	 * @return
	 * @throws VfsException
	 */
	public VfsFile[] getChilds() throws VfsException;
	/**
	 * 获取父文件的路径
	 * @return
	 * @throws VfsException
	 */
	public String getParent() throws VfsException;
	/**
	 * 获取父文件
	 * @return
	 * @throws VfsException
	 */
	public VfsFile getParentFile() throws VfsException;
	/**
	 * 是否是文件
	 * @throws VfsException
	 */
	public boolean isFile() throws VfsException;

	/**
	 * 重命名文件为指定名称,如果重命名成功,返回true
	 * <br>
	 * 目标目录不存在,会自动创建
	 * @param fnOrDir 如果指定名称中带有/或\\,则该名称是绝对路径,否则就是新的文件名
	 * @return 重命名成功,返回true
	 * @throws VfsException 以下情况会抛出异常:
	 * <ol>
	 *   <li>文件名不合法
	 *   <li>文件不存在
	 *   <li>没有写权限
	 *   <li>指定名称为空
	 *   <li>重命名后的文件已经存在
	 *   <li>重命名后的文件是当前文件的子文件
	 *   <li>目标目录没有写权限
	 * </ol>
	 */
	public boolean renameTo(String fnOrDir) throws VfsException;

	/**
	 * 注释见boolean renameTo(String fnOrDir)方法
	 * renameTo(fnOrDir) = boolean renameTo(fnOrDir,false)
	 *
	 * @param isoverwrite 当isoverwrite=false,只能重命名为一个不存在的文件，如果isoverwrite=true,则会覆盖同名文件
	 */
	public boolean renameTo(String fnOrDir, boolean isoverwrite) throws VfsException;

	/**
	 * 将文件或目录移到目标目录。移动成功,返回true
	 * @param destDir 目标目录不存在会自动创建
	 * @return
	 * @throws VfsException 以下情况会抛出异常:
	 * <ol>
	 *   <li>文件不存在
	 *   <li>没有写权限
	 *   <li>目标目录destDir不是目录
	 *   <li>文件与目标目录相同
	 *   <li>目标目录是本文件的子目录
	 *   <li>目标目录下存在与本文件名相同的文件或目录
	 *   <li>目标目录没有写权限
	 * </ol>
	 */
	public boolean moveTo(VfsFile destDir) throws VfsException;

	/**
	 * 将文件或目录复制目标目录,并以name作为新的文件或文件夹的名称
	 * @param destDir 目标目录,不存在,会自动创建
	 * @param name 新的文件或目录名,如果为空,仍然使用原名称
	 * @param isoverwrite 如果目的目录已经存在同名的文件,isoverwrite=true时会先删除,再复制,如果isoverwrite=false,则抛出异常
	 * @return 复制成功返回true
	 * @throws VfsException 以下情况会抛出异常:
	 * <ol>
	 *   <li>文件不存在
	 *   <li>没有写权限
	 *   <li>目标目录没有写权限
	 *   <li>目标目录destDir不是目录
	 *   <li>文件与目标目录相同
	 *   <li>目标目录是本文件的子目录
	 *   <li>isoverwrite=false并且目标目录下存在与本文件名相同的文件或目录
	 *   <li>本文件已在目标目录下
	 * </ol>
	 */
	public boolean copyTo(VfsFile destDir, String name, boolean isoverwrite) throws VfsException;
	/**
	 *  是否存在
	 * @throws VfsException
	 */
	public boolean isExist() throws VfsException;
	/**
	 * 是否隐藏
	 * @throws VfsException
	 */
	public boolean isHidden() throws VfsException;
	/**
	 * 获得文件内容编码方式
	 * @return 目录没有编码,如果是目录,返回null
	 */
	public String getCharset();
	/**
	 * 创建文件,如果文件存在，则保存修改
	 * @throws VfsException
	 */
	public void create() throws VfsException;
	/**
	 * 创建文件夹
	 * @throws VfsException
	 */
	public void mkdirs() throws VfsException;
	/**
	 * 删除文件
	 * @throws VfsException
	 */
	public void delete() throws VfsException;

	/**
	 * 等于importFile(file,true)
	 */
	public void importFile(File file) throws VfsException;

	/**
	 * 导入文件,可以导入文件和目录
	 * <br>如果此VfsFile不存在,会自动创建为一个目录,再将file导入到此VfsFile下
	 * @param file
	 *     <br>如果此VfsFile是目录,file是文件,会将此文件导入到此目录下,如果存在同名的文件会覆盖
	 *     <br>如果此VfsFile是目录,file是目录,则会将file目录下的所有文件(不包括file本身)导入到VfsFile下,如果导入时出现同名的文件,则会根据参数deleteFirst的值来处理.
	 *         deleteFirst=true,会先删除同名的文件和目录,再导入.deleteFirst=false,会直接覆盖同名的文件和目录
	 *     <br>如果此VfsFile是文件,并且file也是文件,会用file的内容覆盖此VfsFile的内容
	 *     <br>如果此VfsFile是文件,file是目录,抛出异常
	 * @param deleteFirst 是否先删除文件,为true与为false时的区别在于:
	 *     <br>如果VfsFile下存在一个同名目录a,并且a下还有文件b,那么为true时,会删除a和a下的所有文件(包括b)
	 *     <br>当file下的a目录中没有b文件时,那么导入后就没有b这个文件了
	 *     <br>当deleteFirst=false时,只会删除a目录本身,a下的所有文件仍然存在,所以导入后b仍然存在
	 * @throws VfsException 没有权限或此文件是文件,但是导入的file是目录
	 */
	public void importFile(File file, boolean deleteFirst) throws VfsException;

	/**
	 * 在此目录下创建一个文件,文件名为name参数指定,内容为InputStream的内容
	 * <br>
	 * 如果此目录下不存在名称name的文件,会自动创建,如果已经存在,会用InputStream覆盖原来的内容
	 * <br>
	 * 如果此目录不存在,也会自动创建
	 * @param in 文件内容,如果为空,只会创建一个内容为空的文件
	 * @param name 文件名,不能为空
	 * @throws VfsException 没有权限或此文件不是目录,或在此目录下存在名称为name的目录
	 */
	public void importStm(InputStream in, String name) throws VfsException;

	/**
	 * 导入一个压缩的zip包,包的内容在InputStream中.
	 * <br>
	 * 如果此目录不存在,会自动创建
	 * @param in 在导入时会先用new ZipInputStream(InputStream in)解压zip包,再导入到此目录下
	 * @throws VfsException 没有权限或此文件是文件
	 */
	public void importZip(InputStream in) throws VfsException;

	/**
	 * 将文件和子文件导出为zip到流中
	 * @param out 可以是ZipOutputStream也可以不是,如果是ZipOutputStream必须已经设置好了编码
	 * @throws VfsException 没有权限或文件不存在
	 */
	public void exportZipPackage(OutputStream out) throws VfsException;

	/**
	 * 如果此文件是文件,则将文件内容写入到流中,如果不存在或是目录,不作任何处理
	 * @param out
	 * @param zip 写入的内容是否压缩.如果zip为true,则将内容压缩后写入到out中,为false,则将解压后的内容写入到out中
	 * @throws VfsException 没有权限或传入的流为空,如果文件不存在或是目录,传入的流为空也不会抛出异常
	 */
	public void writeContentTo(OutputStream out, boolean zip) throws VfsException;

	/**
	 * 设置内容类型 mime type
	 * @param mimetype
	 * @throws VfsException 没有权限或此文件是目录,或文件不存在
	 */
	public void setMimeType(String mimetype);

	/**
	 * 返回内容类型 mime type
	 * @return 目录没有MimeType,如果是目录返回null
	 *
	 */
	public String getMimeType();

	/**
	 * 获得文件类型方法，优先级从上到下
	 * 1.根据字段MimeType的值获得
	 * 2.使用文件名后缀获得
	 * 3.从bi系统定义的ContentType中获得
	 *
	 * @return 如果是目录,返回null,如果是文件,根据文件后缀获得对应的MimeType
	 * @throws VfsException 没有权限
	 */
	public String guessMimeType();
	/**
	 * 设置文件内容编码方式
	 * @param charset 编码方式
	 * @throws VfsException 没有权限或此文件是目录,或文件不存在
	 */
	public void setCharset(String charset) throws VfsException;

	/**
	 * 返回文件大小,单位字节,如果是目录,返回0
	 */
	public long length();
	/**
	 * 获取最后修改时间
	 */
	public long getLastModified();
	/**
	 * 获得绝对路径(相对与VFS的路径)
	 */
	public String getAbsolutePath();

	public static final int FILTERFILE = 0x01;
	public static final int FILTERFOLDER = 0x02;
	public static final int RESERVEFILE = 0x04;
	public static final int RESERVEFOLDER = 0x08;
	public static final int WITHCONTENT = 0x10;
	/**
	 * 在查询范围中筛选出符合条件的文件或目录并返回.
	 * <br>
	 * 筛选条件根据参数filterwildcard和filterType确定,具体规则见两个参数的说明
	 * <pre>
	 * @param filterwildcard  过滤条件,可以包含通配符.通配符*匹配0或多个任意字符,通配符?匹配单个任意字符.而,号用来分隔多个过滤条件,相当于或运算,会把多个过滤条件的结果合并返回
	 *                        例如: w*d.do?   可以匹配word.doc, world.dot等等。
	 *                              *.jsp,*.java  可以匹配以jsp或java为扩展名的文件。
	 *        此参数是否有效决定于filterType参数.
	 *        与filterwildcard匹配:满足过滤条件就是与filterwildcard匹配,word.doc,world.dot都满足过滤条件,它们都与filterwildcard匹配
	 * @param filterType  过滤方式分别为:
	 *                    常量                       描述                   值
	 *                    ------------------------------------------------------
	 *                    VfsFile2.FILTERFILE    |  在查询范围中,返回与filterwildcard匹配的文件              | 0x01
	 *                    VfsFile2.FILTERFOLDER  |  在查询范围中,返回与filterwildcard匹配的目录               | 0x02
	 *                    VfsFile2.RESERVEFILE   |  在查询范围中,只要是文件都返回 | 0x04
	 *                    VfsFile2.RESERVEFOLDER |  在查询范围中,只要是目录都返回 | 0x08
	 *                    VfsFile2.WITHCONTENT   |  在数据库实现中,查询符合条件的子文件时,把符合条件的文件的内容一起缓存,在后面使用时不再需要再次查询,这样可以提高访问的效率     | 0x10
	 *
	 *                    此参数可以为上述多个常量的和,如VfsFile.FILTERFILE + VfsFile.RESERVEFOLDER  表示保留目录的同时过滤文件
	 *                    RESERVEFILE与FILTERFILE同时存在时,FILTERFILE不会起作用
	 *                    RESERVEFOLDER与FILTERFOLDER同时存在时,FILTERFOLDER不会起作用
	 *
	 * @param recur 是否递归.为true时表示递归,会列出此文件的所有直接和间接子文件.为false时表示不递归,只会列出此文件的直接子文件
	 * @return 如果文件不存在或不是目录,返回null
	 * @throws SQLException  没有权限
	 *
	 * 用例:
	 * 列出所有子目录，但不要文件:listFiles(null,RESERVEFOLDER,recur);
	 * 列出所有*.gif文件，但不要目录:listFiles("*.gif",FILTERFILE,recur);
	 * 列出所有子目录和*.gif文件:listFiles("*.gif",FILTERFILE | RESERVEFOLDER,recur);
	 * 列出所有win开头的目录，但不包括文件:listFiles("win*",FILTERFOLDER,recur);
	 * 列出所有sys开头的目录和文件:listFiles("sys*",FILTERFILE | FILTERFOLDER,recur);
	 *
	 */
	public VfsFile[] listFiles(String filterwildcard, int filterType, boolean recur) throws VfsException;

	/**
	 *  根据关键字查找，范围是当前目录的子节点
	 * @param keyword
	 * @param recur   是否递归查找
	 * @return
	 * @throws VfsException
	 */
	public VfsFile[] listFiles(String keyword, boolean recur) throws VfsException;


	/**
	 * 返回一个压缩的内容流，和上面的函数getInputStream类似，只是返回的流是压缩的。
	 * 调用者要负责关闭返回的流
	 *
	 * @throws VfsException 没有权限,文件不存在或此文件是一个目录
	 */
	public InputStream getGzipInputStream() throws VfsException;

	/**
	 * 开始事务，此函数可以多个对此vfs的修改操作当做一个整体的事物提交，如果有一个失败那么整个操作都会回滚
	 * 此函数不是必须调用的，如果调用者只是进行读取操作或者调用者只是对vfs进行一次修改，那么不必调用。
	 * 如果调用者会连续的对vfs进行多次修改，且要保证这些修改是作为一个整体的事物提交的，那么必须手工调用此函数
	 * 调用此函数之后必须调用commitTransaction()才能提交事物，最后还必须调用endTransaction()来进行清理工作
	 * <p/>
	 * 例如:<br><pre>
	 * startTransaction();
	 * try{
	 *   removeFile("/a/b/c");
	 *   setFileAsBytes(....);
	 *   ...
	 *
	 *   commitTransaction();
	 * }finally{
	 *   endTransaction();
	 * }
	 * </pre>
	 * <p>
	 * startTransaction(),commitTransaction(),endTransaction()是线程安全的,两个线程间的事务不会相互影响
	 * <p>
	 * 需要注意的是在startTransaction()和commitTransaction()中间不要查询数据<br>
	 * 因为数据库实现是用ibatis实现的,如果在提交前执行了查询数据的操作,获得的可能是当前未提交的数据,并将这个结果缓存下来<br>
	 * 如果在提交前其它线程再执行相同的查询,会把缓存的结果返回,导致返回的结果不正确.
	 * <p>
	 * 数据库的实现中调用startTransaction()时会创建Connection con对象并持有该对象,同时设置con.setAutoCommit(false)<br>
	 * 调用commitTransaction()时执行con.commit();<br>
	 * 执行完成后endTransaction()会释放con对象
	 * <p>
	 */
	public void startTransaction() throws VfsException;

	/**
	 * 提交事物
	 * @throws VfsException 当事务无法完成时
	 */
	public void commitTransaction() throws VfsException;

	/**
	 * 执行startTransaction之后必须执行此函数，参考startTransaction的说明
	 */
	public void endTransaction() throws VfsException;

	/**
	 * 判断operator能否读取文件f，如果不能，那么抛出异常VfsException
	 */
	public boolean canRead();

	/**
	 * 检查能否改写文件f
	 */
	public boolean canWrite();

	/**
	 * 检查能否创建文件f
	 */
	public boolean canCreate();

	/**
	 * 检查能否删除文件f
	 */
	public boolean canRemove();

	/**
	 * 判断文件或目录是否存在，如果不存在则触发异常，否则直接返回
	 */
	public void checkExists(boolean isdir);

	/**
	 * 判断文件或目录是否存在，如果不存在则创建它.父目录不存在,也会自动创建
	 * @param isdir 当isdir=true时,确保此文件是一个目录,并且存在
	 *          <br>当isdir=false时,确保此文件是一个文件,并且存在
	 * @throws VfsException 没有权限,创建没有成功,或节点已经存在,但是也isdir指定的类型不一致
	 */
	public void ensureExists(boolean isdir) throws VfsException;

}
