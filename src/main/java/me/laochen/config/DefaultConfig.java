package me.laochen.config;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("defaultConfig")
public class DefaultConfig {

	protected static final Logger logger = LoggerFactory.getLogger(DefaultConfig.class); 
	
	private String appId;
	private Map<String, String> configMap = new ConcurrentHashMap<String, String>();
	private Properties properties;
	private String confiName;
	
	public void setConfiName(String confiName) {
		this.confiName = confiName;
	}
	
	private void showConf() {
		for (Map.Entry<String, String> en:configMap.entrySet()) {
			logger.info(">>>>>>"+en.getKey()+"="+en.getValue());
		}
	}
	
	public DefaultConfig () {}
	
	public DefaultConfig (String appId) {
		this.appId = appId;
		init();
	}
	
	public void init() {
		if(appId == null) {
			throw new RuntimeException("no appId found!");
		}
		try {
			properties = PropUtil.getProperties(confiName);
			for (Map.Entry<Object, Object> en:properties.entrySet()) {
				String key = (String)en.getKey();
				String val = (String)en.getValue();
				configMap.put(key, val);
			}
			logger.info(">>>>>>>>>>>>>get configuration from local.[server.properties]>>>>>>>>>>>>>");
			showConf();
		} catch (Exception e) {
			logger.error("init",e);
			System.exit(1);
		}
	}
	
	public void put(String key,Object value) {
		properties.put(key, value);
	}
	
	public Map<String, String> getVal() {
		return configMap;
	}
	
	public boolean contains(String key){
		return configMap.containsKey(key);
	}
		
		
	public String getVal(String key) {
		String value = configMap.get(key);	
		if(value == null) {
			throw new RuntimeException("no propertie ["+key+"] found in configuration.");
		}
		return value;
	}
	
	public Integer getInt(String key) {
		String value = getVal(key);
		return Integer.parseInt(value);
	}
	
	public Long getLong(String key) {
		String value = getVal(key);
		return Long.parseLong(value);
	}

	public Float getFloat(String key) {
		String value = getVal(key);
		return Float.parseFloat(value);
	}
	
	public Double getDouble(String key) {
		String value = getVal(key);
		return Double.parseDouble(value);
	}
	
	public Boolean getBoolean(String key) {
		String value = getVal(key);		
		return Boolean.valueOf(value);
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
}
