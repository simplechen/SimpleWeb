package me.laochen.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Listens the spring application change.
 */
public class SpringContextListener implements ApplicationListener {
	private static Logger logger = LoggerFactory.getLogger(SpringContextListener.class);

	public void onApplicationEvent(ApplicationEvent event) {
		Object obj = event.getSource();	
		if (obj instanceof ApplicationContext) {
			SpringUtils util = SpringUtils.getInsantce();
			util.setCtx((ApplicationContext) obj);
			logger.info("Spring started...");
		}
	}

}
