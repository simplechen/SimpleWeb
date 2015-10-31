package me.laochen.vo.http;

import java.util.Map;

/**
 * 模板类响应
 * @author laochen
 *
 */
public class TplResponseVO implements ResponseVO {
	private String charset="utf-8";
	private String tplFileName;
	private Map<String,Object> data;
	
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getTplFileName() {
		return tplFileName;
	}
	public void setTplFileName(String tplFileName) {
		this.tplFileName = tplFileName;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
}
