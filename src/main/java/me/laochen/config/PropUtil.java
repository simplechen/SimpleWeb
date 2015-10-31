package me.laochen.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 属性文件加载类
 * @author ecxiaodx
 *
 */
public class PropUtil {
	
	private static Map<String,Properties> propMap = new ConcurrentHashMap<String, Properties>();

	/**
	 * 加载属性文件
	 * @param prop
	 * @param propFile 属性文件路径（相对classes根目录）
	 * @return
	 */
	public static final Properties loadProperty(String propFile) {
		InputStream input = null;
		try {
			input = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(input == null) {
			input = PropUtil.class.getResourceAsStream(propFile);
		}
		if(input == null) {
			try {
				input = new FileInputStream(new File(getAppPath(),propFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			Properties prop = new Properties();
			prop.load(input);
			return prop;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {}
		}
	}
	
	public static String getAppPath(){
		Class cls = DefaultConfig.class;
	    //检查用户传入的参数是否为空  
	    if(cls==null)   
	     throw new java.lang.IllegalArgumentException("参数不能为空！");  
	    ClassLoader loader=cls.getClassLoader();  
	    //获得类的全名，包括包名  
	    String clsName=cls.getName()+".class";  
	    //获得传入参数所在的包  
	    Package pack=cls.getPackage();  
	    String path="";  
	    //如果不是匿名包，将包名转化为路径  
	    if(pack!=null){  
	        String packName=pack.getName();  
	       //此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库  
	       if(packName.startsWith("java.")||packName.startsWith("javax."))   
	          throw new java.lang.IllegalArgumentException("不要传送系统类！");  
	        //在类的名称中，去掉包名的部分，获得类的文件名  
	        clsName=clsName.substring(packName.length()+1);  
	        //判定包名是否是简单包名，如果是，则直接将包名转换为路径，  
	        if(packName.indexOf(".")<0) path=packName+"/";  
	        else{//否则按照包名的组成部分，将包名转换为路径  
	            int start=0,end=0;  
	            end=packName.indexOf(".");  
	            while(end!=-1){  
	                path=path+packName.substring(start,end)+"/";  
	                start=end+1;  
	                end=packName.indexOf(".",start);  
	            }  
	            path=path+packName.substring(start)+"/";  
	        }  
	    }  
	    //调用ClassLoader的getResource方法，传入包含路径信息的类文件名  
	    java.net.URL url =loader.getResource(path+clsName);  
	    //从URL对象中获取路径信息  
	    String realPath=url.getPath();  
	    //去掉路径信息中的协议名"file:"  
	    int pos=realPath.indexOf("file:");  
	    if(pos>-1) realPath=realPath.substring(pos+5);  
	    //去掉路径信息最后包含类文件信息的部分，得到类所在的路径  
	    pos=realPath.indexOf(path+clsName);  
	    realPath=realPath.substring(0,pos-1);  
	    //如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名  
	    if(realPath.endsWith("!"))  
	        realPath=realPath.substring(0,realPath.lastIndexOf("/"));  
	  /*------------------------------------------------------------ 
	   ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径 
	    中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要 
	    的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的 
	    中文及空格路径 
	  -------------------------------------------------------------*/  
	  try{  
	    realPath=java.net.URLDecoder.decode(realPath,"utf-8");  
	   }catch(Exception e){throw new RuntimeException(e);}  
	   return realPath;  
	}
	
	public static final Properties loadProperty(InputStream inputStream) {
		try {
			Properties prop = new Properties();
			prop.load(inputStream);
			return prop;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {}
		}
	}
	
	
	/**
	 * 根据文件名，返回属性配置对象
	 * @param propFile 属性文件路径（相对classes根目录）
	 * @return
	 */
	public static final Properties getProperties(String propFile) {
		Properties prop = propMap.get(propFile);
		if(prop == null) {
			prop = loadProperty(propFile);
			propMap.put(propFile, prop);
		}
		return prop;
	}
	
	/**
	 * 
	 * @param propFile 属性文件路径（相对classes根目录）
	 * @param key	属性KEY
	 * @param reloadFile 是否重新读取文件（<b>reloadFile=false</b>时，会利用缓存，只加载一次配置文件,<b>reloadFile=true</b>时，每次都重新打开配置文件）
	 * @return
	 */
	public static final String getProp(String propFile,String key,boolean ...reloadFile) {
		Properties prop = null;
		if(reloadFile.length > 0 && reloadFile[0] ) {
			prop = loadProperty(propFile);
			return prop.getProperty(key);
		}
		prop = propMap.get(propFile);
		if(prop == null) {
			prop = loadProperty(propFile);
			propMap.put(propFile, prop);
		}
		return prop.getProperty(key);
	}
	
	public static final String getProp(InputStream inputStream,String key,boolean ...reloadFile) {
		Properties prop = new Properties();
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return prop.getProperty(key);
	}
	
}
