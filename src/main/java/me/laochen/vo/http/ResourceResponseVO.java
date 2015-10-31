package me.laochen.vo.http;

import java.util.Arrays;

/**
 * 媒体资源类型
 * @author laochen
 *
 */
public class ResourceResponseVO implements ResponseVO {
	public String contentType;
	public byte[] data;
	
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
	
	@Override
	public String toString() {
		return "ResourceResponseVO [contentType=" + contentType + ", data="
				+ Arrays.toString(data) + "]";
	}	
}
