package com.wyf.vfs;

import com.wyf.vfs.impl.VfsExtendPropertyBean;

import java.util.Arrays;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/6/27 17:42
 */
public enum SystemProperty {
    //名称
    NAME(VfsExtendPropertyBean.EDITOR_INPUT,VfsExtendPropertyBean.DISPLAY_SHOW,true,VfsExtendProperty.OWNER_SYSTEM,"","","name","name",VfsExtendPropertyBean.FIELD_TYPE_STR,0,0,false,false,"文件名"),
    PARENTDIR(VfsExtendPropertyBean.EDITOR_INPUT,VfsExtendPropertyBean.DISPLAY_SHOW,true,VfsExtendProperty.OWNER_SYSTEM,"","","parentDir","parentDir",VfsExtendPropertyBean.FIELD_TYPE_STR,0,0,false,false,"父目录"),
    FILE(VfsExtendPropertyBean.EDITOR_RADIO,VfsExtendPropertyBean.DISPLAY_SHOW,true,VfsExtendProperty.OWNER_SYSTEM ,"","","file","file",VfsExtendPropertyBean.FIELD_TYPE_STR,0,0,false,false,"是否是文件");

    private String editor;
    private String display;
    private boolean editNullable;
    private String owner;
    private String source;
    private String hint;
    private String name;
    private String fieldName;
    private char type;
    private int length;
    private int scale;
    private boolean nullable;
    private boolean unique;
    private String caption;

    public static boolean containsName(String name){
        SystemProperty[] values = SystemProperty.values();
        return Arrays.stream(values).anyMatch(x -> x.getName().equals(name));
    }

    SystemProperty(String editor, String display, boolean editNullable, String owner, String source, String hint, String name, String fieldName, char type, int length, int scale, boolean nullable, boolean unique, String caption) {
        this.editor = editor;
        this.display = display;
        this.editNullable = editNullable;
        this.owner = owner;
        this.source = source;
        this.hint = hint;
        this.name = name;
        this.fieldName = fieldName;
        this.type = type;
        this.length = length;
        this.scale = scale;
        this.nullable = nullable;
        this.unique = unique;
        this.caption = caption;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public boolean isEditNullable() {
        return editNullable;
    }

    public void setEditNullable(boolean editNullable) {
        this.editNullable = editNullable;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
