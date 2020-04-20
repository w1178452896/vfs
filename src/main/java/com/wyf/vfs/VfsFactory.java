package com.wyf.vfs;

import com.wyf.utils.SpringUtil;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/6/20 19:59
 */
public class VfsFactory {

    /**
     * 创建vfs对象
     * @return
     */
    public static VfsFile getVfs(){
        return SpringUtil.getBean(VfsFile.class);
    }

    /**
     * 扩展属性service
     * @return
     */
    public static PropertyService getPropertyService(){
        return SpringUtil.getBean(PropertyService.class);
    }

}
