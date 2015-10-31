package me.laochen.vo.http;

/**
 * 静态响应 支持xml,json,txt等
 * @author laochen
 *
 */
public class StaticResponseVO implements ResponseVO {
	private String contentType;
	private String content;
	private String charset="UTF-8";
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	@Override
	public String toString() {
		return "StaticResponseVO [contentType=" + contentType + ", content="
				+ content + ", charset=" + charset + "]";
	}
	
		
}
