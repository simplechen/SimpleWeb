package me.laochen;

import me.laochen.server.HttpServer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		HttpServer httpServer = (HttpServer) ctx.getBean("httpServer");
		httpServer.run();		
	}
}