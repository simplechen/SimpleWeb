package me.laochen.po;

import me.laochen.annotation.Field;
import me.laochen.annotation.Table;

@Table("admin_info")
public class AdminInfo {
	
	//TODO  注意使用封装的 jdbctemplate 泛型dao 主键必须为long 名称必须为id 
	@Field("id")
	private Long id;
	
	@Field("display_name")
	private String displayName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "AdminInfo [id=" + id + ", displayName=" + displayName + "]";
	}	
}
