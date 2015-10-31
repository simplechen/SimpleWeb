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
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import me.laochen.annotation.RequestBody;
import me.laochen.annotation.RequestMapper;
import me.laochen.annotation.RequestParam;
import me.laochen.controller.StaticController;
import me.laochen.controller.core.Controller;
import me.laochen.utils.ServiceFactory;
import me.laochen.vo.http.ResourceResponseVO;
import me.laochen.vo.http.ResponseVO;
import me.laochen.vo.http.StaticResponseVO;
import me.laochen.vo.http.TplResponseVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
//@Scope("prototype")
//@Component("httpServerHandler")
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private final static Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk	
	
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    	FullHttpRequest httpRequest = (FullHttpRequest) msg;    	
    	ResponseVO responseVO = null;
    	String uri = httpRequest.getUri();
    	logger.debug("Request.Uri="+uri);
    	
    	QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);    	
		String path = queryStringDecoder.path();		
		if(path.equals("/favicon.ico")){
			StaticResponseVO staticResponseVO = new StaticResponseVO();
			staticResponseVO.setContentType("text/html");
			staticResponseVO.setContent("");
			render(staticResponseVO, ctx,httpRequest);	
		} else if(uri.contains(".") && (path.startsWith("/static/") 
				|| path.startsWith("/js/") 
				|| path.startsWith("/bootstrap/")
				|| path.startsWith("/css/")
				|| path.startsWith("/images/") )){
			StaticController staticController = (StaticController) ServiceFactory.getBean("staticController");
			responseVO = staticController.execute(httpRequest);
			render(responseVO, ctx,httpRequest);
		}  else {
			Map<String,Object> httpRequestParams = getRequestParams(httpRequest);
			String controllerName = "/"+path.split("\\/",3)[1];
			String realPath = "";
			String[] paths = path.split("\\/",3);
			if(paths.length>2){
				realPath = "/"+paths[2];
			}
			
			Controller controller = (Controller) ServiceFactory.getBean(controllerName);
			if(realPath.equals("") || realPath.equals("/")){//调用默认				
				if(httpRequest.getMethod().equals(HttpMethod.GET)){
					responseVO = controller.execute(httpRequest);
				} else if(httpRequest.getMethod().equals(HttpMethod.POST)){
					responseVO = controller.execute(httpRequest.content().toString(CharsetUtil.UTF_8),httpRequest);
				}
				if(responseVO==null){
					StaticResponseVO staticResponseVO = new StaticResponseVO();
					staticResponseVO.setContent("Not found the request path "+path);
					render(staticResponseVO,ctx,httpRequest);
				} else {
					render(responseVO,ctx,httpRequest);		
				}				
			} else {
				Class<?> classz = controller.getClass();
				ClassPool pool = ClassPool.getDefault();
				CtClass ctClass = pool.get(classz.getName());
				CtMethod[] methods = ctClass.getMethods();	
				boolean notFoundPath = true;
				
				//TODO 缓存起来
				for (CtMethod ctMethod : methods) {
					RequestMapper requestMapper = (RequestMapper) ctMethod.getAnnotation(RequestMapper.class);
					if (requestMapper != null) {
						logger.debug("method.name="+ctMethod.getName());	
						
						if(!ArrayUtils.contains(requestMapper.value(),realPath))continue;
						//TODO 在此处可以根据 RequestMapper中的配置决定是否调用						
						notFoundPath = false;//决定是否404						
						CtClass[] parameterTypes = ctMethod.getParameterTypes();	
						MethodInfo methodInfo = ctMethod.getMethodInfo();
						CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
						LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
						
						Class<?>[] classzs = new Class<?>[parameterTypes.length];
						Object[] realMethodParams = new Object[parameterTypes.length];
						int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
						for (int i = 0; i < parameterTypes.length; i++){
							String paramTypeName = parameterTypes[i].getName();
							String paramName = attr.variableName(i + pos);//使用 javassist 获取参数名称
							
							logger.debug("method param: "+paramTypeName+" "+paramName);
	
							classzs[i] = Class.forName(paramTypeName);//用于方法参数类							
							if("FullHttpRequest".equals(parameterTypes[i].getSimpleName())){
								realMethodParams[i] = httpRequest;
							} else if(parameterTypes[i].getSimpleName().equals("Map")){//将所有获取的参数以map的方式传递过去
								realMethodParams[i] = httpRequestParams;
							} else if(httpRequestParams.containsKey(paramName)){
								realMethodParams[i] = typeCast(classzs[i], httpRequestParams.get(paramName));
							}
						}
						//检查当前方法是否有对应的注解，如果有注解则按注解进行参数构建
						Object[][] parameterAnnotations = ctMethod.getParameterAnnotations();
						for (int i = 0; i < parameterAnnotations.length; i++) {
							if(parameterAnnotations[i].length==0) break;
							Object annotationObject = parameterAnnotations[i][0];//TODO 目前一个参数只支持一个注解
							if(annotationObject instanceof RequestParam){
								RequestParam requestParam = (RequestParam) annotationObject;								
								String requestParamName = requestParam.value();						
								realMethodParams[i] = typeCast(classzs[i],httpRequestParams.get(requestParamName));						
							} else if(annotationObject instanceof RequestBody) {
								String postBody = httpRequest.content().toString(CharsetUtil.UTF_8);//TODO 根据请求时的编码进行对应的编码
								realMethodParams[i] = postBody;
							}
						}
						//检查是否有空参数，如果有空参数 报错提示   realMethodParams
						if(ArrayUtils.contains(realMethodParams, null)){
							StaticResponseVO staticResponseVO = new StaticResponseVO();
							staticResponseVO.setContentType("text/html");
							staticResponseVO.setContent("<h3>HTTP Status 500</h3><h5>Please check you rquest the params.</h5>");
							render(staticResponseVO,ctx,httpRequest);
						} else {
							try {
								Object returnObject = classz.getMethod(ctMethod.getName(), classzs).invoke(controller, realMethodParams);//在此处进行回调							
								render(returnObject,ctx,httpRequest);
							} catch (Exception e) {							
								logger.error("invoke the method "+path+" is error, reason ",e);
							}
						}
					}
				}
				if(notFoundPath){//GO 404
					logger.warn("not found the path "+ path);
					TplResponseVO tplResponseVO = new TplResponseVO();
					tplResponseVO.setTplFileName("404.html");			
					render(tplResponseVO,ctx,httpRequest);
				}				
			}
		}
		ctx.close();
    }
    
    //强制类型转换
    private Object typeCast(Class<?> classz, Object originalObject)
    {
    	String  paramTypeSimpleName = classz.getSimpleName();
		Object paramVal = null;
		if(paramTypeSimpleName.equals("Integer")){
			paramVal = Integer.valueOf(""+originalObject);
		} else if(paramTypeSimpleName.equals("Long")){
			paramVal = Long.valueOf(""+originalObject);
		} else if(paramTypeSimpleName.equals("Double")){
			paramVal = Double.valueOf(""+originalObject);
		} else if(paramTypeSimpleName.equals("Float")){
			paramVal = Float.valueOf(""+originalObject);
		} else {
			paramVal = String.valueOf(""+originalObject);
		}
		return paramVal;
    }
    
	private Map<String,String> getRequestHeaders(FullHttpRequest request) {
    	Map<String, String> requestHeaders = new HashMap<String, String>();
	    for (Entry<String, String> entry : request.headers()) {
	    	requestHeaders.put(entry.getKey(), entry.getValue());	    	
	    	if(logger.isDebugEnabled()) {
	    		logger.debug("Request.Header:"+entry.getKey()+"="+entry.getValue());
	    	}
        }
        return requestHeaders;
    }
	
	private Map<String,Object> getRequestParams(FullHttpRequest request) throws IOException {
    	Map<String, Object> requestParams = null;
    	if (request.getMethod().equals(HttpMethod.GET)) {//get请求    		
    		if(requestParams == null) {
        		requestParams = new HashMap<String, Object>();//uri参数
        	}
            QueryStringDecoder decoderQuery = new QueryStringDecoder(request.getUri());
            Map<String, List<String>> uriAttributes = decoderQuery.parameters();
            for (Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                for (String attrVal : attr.getValue()) {
                	String val = attrVal;
                	if(val != null) {
                		val = val.trim();
                	}
                	requestParams.put(attr.getKey(), val);
                }
            }
        } else if (request.getMethod().equals(HttpMethod.POST)) {//post请求        	
        	if(requestParams == null) {
        		requestParams = new HashMap<String, Object>();//uri参数
        	}
        	HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
            try {
				while (decoder.hasNext()) {
				    InterfaceHttpData data = decoder.next();
				    if (data != null) {
				        try {
				            writeHttpData(data,requestParams);
				        } finally {
				            data.release();
				        }
				    }
				}
			} catch (EndOfDataDecoderException e) {
				logger.debug("End of data decoder.");
			}
        } else {
        	return null;
        }
    	return requestParams;
    }
	
	
	private void writeHttpData(InterfaceHttpData data,Map<String, Object> requestParams) throws IOException {		  
        /**
         * HttpDataType有三种类型
         * Attribute, FileUpload, InternalAttribute
         */
        if (data.getHttpDataType() == HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            String value = attribute.getValue();
        	if(value != null) {
        		value = value.trim();
        	}
            requestParams.put(attribute.getName(), value);
        }
        if(data.getHttpDataType() == HttpDataType.FileUpload) {
        	throw new RuntimeException("file upload is not supported!");
        }
    }
	
	private void render(Object responseVO,ChannelHandlerContext ctx,FullHttpRequest fullHttpRequest){
		byte[] responseBytes = null;
		String charset = "utf-8";
		String contentType = "";
		Map<String,String> headers = getRequestHeaders(fullHttpRequest);
		if(headers.containsKey("Accept")){
			String accept = headers.get("Accept");
			if(accept.contains("text/html")){
				contentType = "text/html; charset="+charset;
			} else if(accept.contains("application/json")){
				contentType = "application/json; charset="+charset;
			} else if(accept.contains("application/xml")){
				contentType = "application/xml; charset="+charset;
			}
		}
		
		if(responseVO instanceof String){
			try {
				responseBytes = ((String) responseVO).getBytes(charset);
			} catch (UnsupportedEncodingException e) {
				logger.error("encode "+ responseVO +" error, reason ", e);
			}
		} else if(responseVO instanceof ResourceResponseVO){
			ResourceResponseVO resourceResponseVO = (ResourceResponseVO) responseVO;
			responseBytes = resourceResponseVO.getData();
			contentType = resourceResponseVO.getContentType();
		} else if(responseVO instanceof TplResponseVO){
			TplResponseVO tplResponseVO = (TplResponseVO) responseVO;
			if(StringUtils.isNotBlank(tplResponseVO.getCharset())){
				charset = tplResponseVO.getCharset();
			}
			contentType = "text/html; charset="+charset;
			Configuration cfg = new Configuration();
			try {
				cfg.setDirectoryForTemplateLoading(new File("./resources"));
				Template template = cfg.getTemplate(tplResponseVO.getTplFileName());				
				StringWriter out = new StringWriter();				
				template.process(tplResponseVO.getData(),out);				
				StringBuffer sbuffer = out.getBuffer();
				responseBytes = String.valueOf(sbuffer).getBytes(charset);	
			} catch (IOException e) {				
				e.printStackTrace();
				logger.equals(e);
			} catch (TemplateException e) {				
				e.printStackTrace();
				logger.equals(e);
			}
		} else if(responseVO instanceof StaticResponseVO){
			StaticResponseVO staticResponseVO = (StaticResponseVO) responseVO;
			contentType = staticResponseVO.getContentType();
			logger.debug("Response Content:"+staticResponseVO.getContent());			
			try {
				responseBytes = staticResponseVO.getContent().getBytes(staticResponseVO.getCharset());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.error("read the content is error, reason is ",e);
			}
		}
		
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,Unpooled.wrappedBuffer(responseBytes));
		response.headers().set(CONTENT_LENGTH,response.content().readableBytes());		
		if(contentType.length()>0){
			response.headers().set(CONTENT_TYPE, contentType);
		}
		try {
			boolean keepAlive = HttpHeaders.isKeepAlive(fullHttpRequest);
			if (!keepAlive) {
				ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
			} else {
				response.headers().set(CONNECTION, Values.KEEP_ALIVE);
				ctx.writeAndFlush(response);//TODO 注意此处需要主动刷新，否则客户端得不到结果
			}
		} catch (Exception e) {
			e.printStackTrace();
			ctx.close();
		}
	}
	
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
		logger.debug("channelReadComplete ip"+getAddress(ctx));
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
	
	private String getAddress(ChannelHandlerContext ctx)
	{
		InetSocketAddress isa = (InetSocketAddress)ctx.channel().remoteAddress();
		return isa.getHostName()+":"+isa.getPort();
	}
}