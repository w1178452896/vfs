package com.wyf.vfs.impl;

import com.wyf.vfs.VfsExtendProperty;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/6/5 15:14
 */
public class VfsExtendPropertyBean implements VfsExtendProperty {
    private static final long serialVersionUID = -6366140463077368662L;

    private String editor;
    private String display;
//    private boolean editNullable;
    private String owner;
    private String source;
//    protected boolean searchable;
    private String hint;
//    protected String defaultValue;
    private String name;
    private String fieldName;
    private char type;
    private int length;
    private int scale;
    private boolean nullable;
    private boolean unique;
//    protected boolean primaryKey;
//    protected boolean autoInc;
    private String caption;
//    protected boolean version;

    @Override
    public String getEditor() {
        return editor;
    }

    @Override
    public void setEditor(String editor) {
        this.editor = editor;
    }

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getHint() {
        return hint;
    }

    @Override
    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public char getType() {
        return type;
    }

    @Override
    public void setType(char type) {
        this.type = type;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public int getScale() {
        return scale;
    }

    @Override
    public void setScale(int scale) {
        this.scale = scale;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public void setUnique(boolean unique) {
        this.unique = unique;
    }


    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public int compareTo(VfsExtendProperty o) {
        return 0;
    }
}
