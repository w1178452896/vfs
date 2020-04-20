package com.wyf.controller;

import com.alibaba.fastjson.JSONObject;
import com.wyf.vfs.PropertyService;
import com.wyf.vfs.SystemProperty;
import com.wyf.vfs.impl.VfsExtendPropertyBean;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/6/28 16:15
 */
@RestController
@RequestMapping("/vfsMetaData")
public class VfsPropertyController {

    @Autowired
    PropertyService propertyService;

    @GetMapping("/index")
    public String ma(){
        return "index";
    }

    @GetMapping("")
    public String getAll(){
        return propertyService.getAll();
    }

    @PostMapping("")
    public void insert(@RequestBody JSONObject object, HttpServletRequest request){
        checkPropertyBean(object);
        checkNameAndCaption(object);
        propertyService.insertOne(object);
    }
    @PutMapping("/{name}")
    public void edit(@PathVariable("name") String name,@RequestBody JSONObject object){
        checkPropertyBean(object);
        propertyService.update(object);
    }

    @DeleteMapping("/{name}")
    public void delete(@PathVariable("name") String name){
        propertyService.deleteByName(name);
    }

    /**
     * 检查名称是否已经存在，如果存在，则抛异常
     * @param object
     */
    private void checkNameAndCaption(JSONObject object) {
        String name = object.getString("name");

        if(SystemProperty.containsName(name)|| propertyService.getByName(name)!=null){
            throw new RuntimeException("已存在名称："+name);
        }
    }

    /**
     *  检查 属性是否为空，如果为空，设置为默认值
     * @param bean
     */
    private void checkPropertyBean(JSONObject bean) {
        if(Strings.isNullOrEmpty(bean.getString("editor"))){
            bean.put("editor", VfsExtendPropertyBean.EDITOR_INPUT);
        }
        if(Strings.isNullOrEmpty(bean.getString("type"))){
            bean.put("type", VfsExtendPropertyBean.FIELD_TYPE_STR);
        }
    }


}
