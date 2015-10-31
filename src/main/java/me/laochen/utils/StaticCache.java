package me.laochen.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.laochen.vo.StaticCacheVO;

import org.springframework.stereotype.Component;

@Component("StaticCache")
public class StaticCache {
	private static Map<String, StaticCacheVO> caches = new ConcurrentHashMap<String, StaticCacheVO>();
	
	public StaticCacheVO get(String path){
		if(caches.containsKey(path)){
			return caches.get(path);
		}
		return null;
	}
	
	public void set(String path, StaticCacheVO staticCacheVO){
		caches.put(path, staticCacheVO);
	}
	
	public void set(String path, byte[] datas, String contentType){
		caches.put(path, new StaticCacheVO(datas, contentType));
	}
	
	public Boolean contains(String path){
		return caches.containsKey(path);
	}
}