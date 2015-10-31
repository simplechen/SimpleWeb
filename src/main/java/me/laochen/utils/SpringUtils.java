package me.laochen.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Holds the spring's <code>ApplicationContext</code> <br>
 * and provides bean finding method.
 */
public class SpringUtils {
	private static Logger log = LoggerFactory.getLogger(SpringUtils.class);
	private static SpringUtils instance = new SpringUtils();
	private ApplicationContext ctx;

	private SpringUtils() {
	}

	/**
	 * @return
	 */
	public static SpringUtils getInsantce() {
		return instance;
	}

	/**
	 * Holds application context
	 * 
	 * @param ctx
	 *            application context
	 */
	public void setCtx(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * @return global ApplicationContext instance
	 */
	public ApplicationContext getCtx() {
		if (ctx == null) {
			log.warn("spring context is null");
		}
		return ctx;
	}
	
	/*public PlatformTransactionManager getTxManager() {
		PlatformTransactionManager mgr = (PlatformTransactionManager) ctx.getBean("transactionManager");
		return mgr;
	}*/
	
	/*public void publishEvent(OnlineEvent event) {
		getCtx().publishEvent(event);
	}*/
}