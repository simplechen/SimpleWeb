package me.laochen.vo;

public class StaticCacheVO {
	private String contentType;
	private byte[] data;
	private String charset="utf-8";
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	
	public StaticCacheVO( byte[] data, String contentType, String charset) {
		super();
		this.contentType = contentType;
		this.data = data;
		this.charset = charset;
	}
	
	public StaticCacheVO(byte[] data,String contentType) {
		super();
		this.contentType = contentType;
		this.data = data;
	}
}
