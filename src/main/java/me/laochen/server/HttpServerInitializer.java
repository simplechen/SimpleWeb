/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package me.laochen.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("httpServerInitializer")
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
	private final static Logger logger = LoggerFactory.getLogger(HttpServerInitializer.class);
	
	@Override
	public void initChannel(SocketChannel ch) {
		logger.debug("InitChannel...");
		
		ChannelPipeline channelPipeline = ch.pipeline();		
		channelPipeline.addLast("decoder", new HttpRequestDecoder());
    	channelPipeline.addLast("encoder", new HttpResponseEncoder());    	
    	channelPipeline.addLast("aggregator", new HttpObjectAggregator(1048576));//1M  netty.http.upload.bytes.size 		
//		channelPipeline.addLast("deflater", new HttpContentCompressor());//打开压缩		
    	channelPipeline.addLast(new HttpServerHandler());
	}
}
