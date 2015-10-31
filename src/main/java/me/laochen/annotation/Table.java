package me.laochen.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
	public String value();
	
	public String pK() default "id";
	
	public String name() default "";
	
	public String hasOne() default "";
	
	public String belongTo() default "";
	
	public String hasMany() default "";
}