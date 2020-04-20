package com.wyf.vfs;

import java.io.Serializable;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:  扩展属性元数据实体类
 * @date 2019/6/5 15:14
 */
public interface VfsExtendProperty extends Comparable<VfsExtendProperty>, Serializable {
	public char FIELD_TYPE_INT = 73;
	public char FIELD_TYPE_STR = 67;
	public char FIELD_TYPE_DATE = 80;
	public char FIELD_TYPE_CLOB = 77;
	public char FIELD_TYPE_FLOAT = 78;
	public char FIELD_TYPE_LOGIC = 76;
	public char FIELD_TYPE_BINARY = 88;
	public String OWNER_SYSTEM = "system";
	public String OWNER_EXT = "ext";
	public String EDITOR_INPUT = "input";
	public String EDITOR_TEXTAREA = "textarea";
	public String EDITOR_SELECT = "select";
	public String EDITOR_MULTISELECT = "multiselect";
	public String EDITOR_RADIO = "radio";
	public String EDITOR_CHECKBOX = "checkbox";
	public String EDITOR_DATE = "date";
	public String EDITOR_NONE = "none";
	public String DISPLAY_HIDDEN = "hidden";
	public String DISPLAY_EDITABLE = "editable";
	public String DISPLAY_SHOW = "show";

	public String getEditor() ;

	public void setEditor(String editor) ;

	public String getDisplay() ;

	public void setDisplay(String display) ;

	public String getOwner() ;

	public void setOwner(String owner) ;

	public String getSource() ;

	public void setSource(String source) ;

	public String getHint() ;

	public void setHint(String hint) ;


	public String getName() ;

	public void setName(String name) ;

	public String getFieldName() ;

	public void setFieldName(String fieldName) ;

	public char getType() ;

	public void setType(char type) ;

	public int getLength() ;

	public void setLength(int length) ;

	public int getScale() ;

	public void setScale(int scale) ;

	public boolean isNullable() ;

	public void setNullable(boolean nullable) ;

	public boolean isUnique() ;

	public void setUnique(boolean unique) ;


	public String getCaption() ;

	public void setCaption(String caption) ;


	
}
