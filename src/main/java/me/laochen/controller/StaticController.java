package me.laochen.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import me.laochen.config.DefaultConfig;
import me.laochen.controller.core.AbstractController;
import me.laochen.utils.StaticCache;
import me.laochen.vo.StaticCacheVO;
import me.laochen.vo.http.ResourceResponseVO;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Scope("prototype")
@Repository("staticController")
public class StaticController extends AbstractController{
	private final static Logger logger = LoggerFactory.getLogger(StaticController.class); 
	
	@Resource
	private DefaultConfig defaultConfig;
	
	@Resource
	private StaticCache cache;

	@Override
	public ResourceResponseVO execute(FullHttpRequest request) {
		ResourceResponseVO responseVO = new ResourceResponseVO();		
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
		byte[] responseBytes=null;		
		String resourcesRoot="./resources";
		if(defaultConfig.contains("kckp.weixin.http.resources")){
			String tmpResourcesDir = defaultConfig.getVal("kckp.weixin.http.resources");
			if(tmpResourcesDir.endsWith("/")){
				tmpResourcesDir = tmpResourcesDir.substring(0,tmpResourcesDir.length()-1); 
			}
			resourcesRoot = tmpResourcesDir;
		}
		String resUri = "";
		if(queryStringDecoder.path().startsWith("/static/")){
			resUri = resourcesRoot+queryStringDecoder.path().substring(7);
		} else {
			resUri = resourcesRoot+queryStringDecoder.path();
		}
		
		String contentType ="text/plain;charset=utf-8";//TODO 资源文件编码的判断		
		if(cache.contains(resUri)){
			logger.debug("read the static file "+resUri+" from cache.");
			StaticCacheVO cacheVO = cache.get(resUri);
			responseVO.setContentType(cacheVO.getContentType());
			responseVO.setData(cacheVO.getData());
		} 
		else 
		{
			logger.debug("资源文件:"+resUri);
			File localFile = new File(resUri);
			if (localFile.isHidden() || !localFile.exists() || localFile.isDirectory()) {
				responseVO.setContentType("text/html; charset=utf-8");
				localFile = new File(resourcesRoot+"404.html");
			} else {
				if(resUri.toLowerCase().endsWith(".html")){
					contentType="text/html; charset=utf-8";
				} else if(resUri.toLowerCase().endsWith(".jpg")){
					contentType="image/jpeg";
				} else if(resUri.toLowerCase().endsWith(".png")){
					contentType="image/png";
				} else if(resUri.toLowerCase().endsWith(".js")){
					contentType = "application/javascript; charset=utf-8";
				} else if(resUri.toLowerCase().endsWith(".css")){
					contentType = "text/css";
				}
				responseVO.setContentType(contentType);
			}
			try {
				responseBytes = FileUtils.readFileToByteArray(localFile);
				responseVO.setData(responseBytes);
				cache.set(resUri, responseBytes, contentType);
			} catch (IOException e) {
				logger.error("Read the resource is error.",e);
			}
		}
    	return responseVO;
	}		
}
