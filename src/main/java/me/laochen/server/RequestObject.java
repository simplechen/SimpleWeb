package me.laochen.server;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.util.Map;

public class RequestObject {
	private HttpPostRequestDecoder decoder;
	private HttpRequest httpRequest;
	private String postBody;	
	private Map<String,Object> postParams;
	
	
	public RequestObject(HttpRequest httpRequest) {
		super();
		this.httpRequest = httpRequest;
	}
	public HttpPostRequestDecoder getDecoder() {
		return decoder;
	}
	public void setDecoder(HttpPostRequestDecoder decoder) {
		this.decoder = decoder;
	}
	public HttpRequest getHttpRequest() {
		return httpRequest;
	}
	public void setHttpRequest(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}
	public String getPostBody() {
		return postBody;
	}
	public void setPostBody(String postBody) {
		this.postBody = postBody;
	}
	public Map<String, Object> getPostParams() {
		return postParams;
	}
	public void setPostParams(Map<String, Object> postParams) {
		this.postParams = postParams;
	}
	@Override
	public String toString() {
		return "RequestObject [decoder=" + decoder + ", httpRequest="
				+ httpRequest + ", postBody=" + postBody + ", postParams="
				+ postParams + "]";
	}
	
}
