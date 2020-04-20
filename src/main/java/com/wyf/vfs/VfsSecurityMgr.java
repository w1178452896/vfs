package com.wyf.vfs;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/6/5
 */
public interface VfsSecurityMgr {

	/**
	 * 判断operator能否读取文件f，如果不能，那么抛出异常VfsException
	 * 
	 * @throws VfsException  检查未通过时抛出
	 */
	public void checkCanRead(VfsFile f);

	/**
	 * 检查能否改写文件f
	 * 
	 * @throws VfsException  检查未通过时抛出
	 */
	public void checkCanWrite(VfsFile f);

	/**
	 * 检查能否创建文件f
	 * 
	 * @throws VfsException 检查未通过时抛出
	 */
	public void checkCanCreate(VfsFile f);

	/**
	 * 检查能否删除文件f
	 * 
	 * @throws VfsException 检查未通过时抛出
	 */
	public void checkCanRemove(VfsFile f);

}
