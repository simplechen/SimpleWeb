package me.laochen.utils;

import java.util.HashMap;

public class HttpContentTypeUtils {
	private static HashMap<String,String> data;
	
	//TODO 根据需要进一步完善
	//http://tool.oschina.net/commons
	static {
		data.put("htm", "text/html");
		data.put("html", "text/html");
		data.put("xhtml", "text/html");
		data.put("jpg", "image/jpeg");
		data.put("png", "image/png");
		data.put("js", "text/javascript");
		data.put("css", "text/css");
		data.put("doc","application/msword");		
	}
	public static String getContentType(String suufix){
		if(data.containsKey(suufix)){
			return data.get(suufix);
		} else  {
			return null;
		}
	}
}
