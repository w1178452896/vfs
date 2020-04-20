package com.wyf.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/8/16 15:14
 */
@Component(BeansCont.SPRINGUTIL)
public class SpringUtil implements ApplicationContextAware {

    // Spring应用上下文环境
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法。设置上下文环境
     *
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }
    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    /**
     * 获取对象
     *
     * @param name
     * @return Object
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }
    /**
     * 获取对象
     *
     * @param cla
     * @return Object
     * @throws BeansException
     */
    public static <T extends Object>  T getBean(Class<T> cla) throws BeansException {
        return applicationContext.getBean(cla);
    }
}
