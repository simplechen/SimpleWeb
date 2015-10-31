package me.laochen.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.annotation.Resource;

import me.laochen.config.DefaultConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("httpServer")
public class HttpServer {
	protected static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
	private Integer port = 2080;

	@Resource
	private HttpServerInitializer httpServerInitializer;
	
	@Resource
	private DefaultConfig defaultConfig;	

	public void run(){
      EventLoopGroup bossGroup = new NioEventLoopGroup(1);
      EventLoopGroup workerGroup = new NioEventLoopGroup();//默认work线程数你的cpu的核心个数
      try { 
    	  if(defaultConfig.contains("kckp.weixin.http.port")){
    		  port = defaultConfig.getInt("kckp.weixin.http.port");
    	  }    	  
          ServerBootstrap b = new ServerBootstrap();
          b.option(ChannelOption.SO_BACKLOG, 1024); 		  
          b.group(bossGroup, workerGroup)
           .channel(NioServerSocketChannel.class)
           .handler(new LoggingHandler(LogLevel.INFO))
           .childHandler(httpServerInitializer);

          Channel ch = b.bind(port).sync().channel();           
          ch.closeFuture().sync();
          
          logger.debug("the http server is started. port="+port);
      } catch (Exception e){
    	  logger.error("the servier start fail, error is ",e);
      }  finally {
          bossGroup.shutdownGracefully();
          workerGroup.shutdownGracefully();
      }
	}
}