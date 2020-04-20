package com.wyf.vfs;

import com.alibaba.fastjson.JSONObject;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/6/27 17:28
 */
public interface PropertyService {

    /**
     * 获取所有扩展属性
     * @return  json字符串
     */
    public String getAll();

    /**
     *  根据名称获取扩展属性
     * @param name
     * @return   返回josn字符串
     */
    public String getByName(String name);

    /**
     *  根据名称删除一个扩展属性
     * @param name
     * @return
     */
    public boolean deleteByName(String name);

    /**
     * 添加数据
     * @param vfsExtendProperty   参数类型为 jsonobject，可转化为VfsExtendProperty
     * @return
     */
    public boolean insertOne(JSONObject vfsExtendProperty);

    /**
     * 更新数据
     * @param vfsExtendProperty   参数类型为 jsonobject，可转化为VfsExtendProperty
     * @return
     */
    public boolean update(JSONObject vfsExtendProperty);


}
