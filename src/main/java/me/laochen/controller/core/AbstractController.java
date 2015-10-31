package me.laochen.controller.core;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import me.laochen.vo.http.ResponseVO;

public abstract class AbstractController implements Controller {

	public void response(String responseContentType,byte[] responseBytes,HttpRequest request,ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,Unpooled.wrappedBuffer(responseBytes));
		response.headers().set(CONTENT_LENGTH,response.content().readableBytes());
		if (responseContentType.toLowerCase().equals("html")) {
			response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
		} else if (responseContentType.toLowerCase().equals("jpg")) {
			response.headers().set(CONTENT_TYPE, "image/jpeg");
		} else if (responseContentType.toLowerCase().equals("png")) {
			response.headers().set(CONTENT_TYPE, "image/png");
		} else {
			response.headers().set(CONTENT_TYPE, "text/plain;charset=utf-8");
		}
		boolean keepAlive = HttpHeaders.isKeepAlive(request);
		if (!keepAlive) {
			ctx.write(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set(CONNECTION, Values.KEEP_ALIVE);
			ctx.writeAndFlush(response);
		}
	}
	
	@Override
	public ResponseVO execute(FullHttpRequest request) {
		return null;
	}

	@Override
	public ResponseVO execute(String postBody, FullHttpRequest request) {
		return null;
	}
}
