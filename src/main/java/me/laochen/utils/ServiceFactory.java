package me.laochen.utils;

import me.laochen.config.DefaultConfig;

import org.springframework.context.ApplicationContext;

/**
 * TODO 通过该 方法可以在整个spring环境中拿到对应的服务
 * @author laochen
 *
 */
public class ServiceFactory {
	public static Object getBean(String beanName) {
		return ServiceFactory.getApplicationContext().getBean(beanName);
	}
	
	public static ApplicationContext getApplicationContext(){
		SpringUtils util = SpringUtils.getInsantce();
		ApplicationContext ctx = util.getCtx();
		return ctx;
	}
	
	public static DefaultConfig getDefaultConfig(){
		return (DefaultConfig) getBean("defaultConfig");
	}
}