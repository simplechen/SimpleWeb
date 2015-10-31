package me.laochen.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
	private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	public static String get(String url,String encoding) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if(StringUtils.isEmpty(encoding)){
			encoding = "UTF-8";
		}
		String charset = encoding;
		String html=null;
		try {
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpget);
			
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				if(entity.getContentEncoding() != null) {
					charset = entity.getContentEncoding().getValue();			
				} else if (ContentType.get(entity) != null) {
			    	Charset cs = ContentType.get(entity).getCharset();
			    	if(cs != null) {
			    		charset = cs.name();
			    	}
				}
			    html = EntityUtils.toString(entity, charset);
			    logger.debug("Get Result="+html);
				EntityUtils.consume(entity);//关闭GET
			}			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			 if (httpClient != null) {  
                try {  
                	httpClient.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
		}
		return html;
	}
	
	/**
	 * post json body
	 * @param url
	 * @param params
	 * @param headers
	 * @param encoding
	 * @return
	 */
	public static String postJson(String url, String jsonString,Map<String,String> headers,String encoding){
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String html=null;
		if(StringUtils.isEmpty(encoding)){
			encoding = Charset.defaultCharset().name();
		}
		String charset = encoding;
		HttpPost httppost = new HttpPost(url);		
		httppost.setProtocolVersion(HttpVersion.HTTP_1_0);// TODO 设置通讯协议为1.0
		if(headers!=null && headers.size()>0){
			for (String key : headers.keySet()) {
				httppost.setHeader(key, headers.get(key));
			}
		}	
		httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");		
		HttpEntity entity = null;
		try {
			StringEntity params =new StringEntity(jsonString,encoding);//解决发送出去乱码的问题主要在此处设置编码
			params.setContentEncoding(encoding);
			params.setContentType("application/json");   
//			params.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	        
			httppost.setEntity(params);
			
			HttpResponse response = httpClient.execute(httppost);
			entity = response.getEntity();
			if (entity != null) {
				if(ContentType.getOrDefault(entity).getCharset()!=null){
					encoding = ContentType.getOrDefault(entity).getCharset().name();
				}
				if(entity.getContentEncoding() != null) {
					charset = entity.getContentEncoding().getValue();			
				} else if (ContentType.get(entity) != null) {
			    	Charset cs = ContentType.get(entity).getCharset();
			    	if(cs != null) {
			    		charset = cs.name();
			    	}
				}
			    html = EntityUtils.toString(entity, charset);
			    logger.debug("Post Result="+html);
				EntityUtils.consume(entity);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null) {  
                try {  
                	httpClient.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }
		}
		return html;
	}

	public static String post(String url, Map<String, String> params,Map<String,String> headers,String encoding) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String html=null;
		if(StringUtils.isEmpty(encoding)){
			encoding = Charset.defaultCharset().name();
		}		
		String charset = encoding;		
//		httpclient.getParams().setParameter("HttpMethodParams.HTTP_CONTENT_CHARSET", encoding);
		HttpPost httppost = new HttpPost(url);
		httppost.setProtocolVersion(HttpVersion.HTTP_1_0);// TODO 设置通讯协议为1.0
		if(headers!=null && headers.size()>0){
			for (String key : headers.keySet()) {
				httppost.setHeader(key, headers.get(key));
			}
		}
		UrlEncodedFormEntity uefEntity=null;
		HttpEntity entity=null;
		try {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			if(params.size()>0){
				for (String key : params.keySet()) {
					formparams.add(new BasicNameValuePair(key,params.get(key)));
				}
			}
			uefEntity = new UrlEncodedFormEntity(formparams, encoding);//编码参数
			
			httppost.setEntity(uefEntity);

			HttpResponse response = httpClient.execute(httppost);
			entity = response.getEntity();
			if (entity != null) {
				if(ContentType.getOrDefault(entity).getCharset()!=null){
					encoding = ContentType.getOrDefault(entity).getCharset().name();
				}
				if(entity.getContentEncoding() != null) {
					charset = entity.getContentEncoding().getValue();			
				} else if (ContentType.get(entity) != null) {
			    	Charset cs = ContentType.get(entity).getCharset();
			    	if(cs != null) {
			    		charset = cs.name();
			    	}
				}
			    html = EntityUtils.toString(entity, charset);
			    logger.debug("Post Result="+html);
				EntityUtils.consume(entity);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null) {  
                try {  
                	httpClient.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }
		}
		return html;
	}
}
