package me.laochen.controller.core;

import io.netty.handler.codec.http.FullHttpRequest;
import me.laochen.vo.http.ResponseVO;

public interface Controller {
	public ResponseVO execute(FullHttpRequest request);
	public ResponseVO execute(String postBody, FullHttpRequest request);
}