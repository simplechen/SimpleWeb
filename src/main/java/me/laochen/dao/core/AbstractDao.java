package me.laochen.dao.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.laochen.annotation.Table;
import me.laochen.utils.CamelCaseUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * @author simple
 *
 * @param <T>
 * @param <Y>
 */
public abstract class AbstractDao<T> {
	
	final Logger logger = LoggerFactory.getLogger(AbstractDao.class);
	
	@Autowired
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	public void setNamedParameterJdbcTemplate(
			NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}


	@SuppressWarnings({ "unchecked" })
	private Class<T> getGenericPOObject(){
		ParameterizedType parameterizedType = (ParameterizedType)this.getClass().getGenericSuperclass();
		logger.debug("ParameterizedType is "+parameterizedType);
		
		Class<T> cls = (Class<T>) parameterizedType.getActualTypeArguments()[0];
		logger.debug("getActualTypeArguments[0] is "+ cls.getCanonicalName());
		return cls;
	}
	
	
	private Map<String,String> getTableFields()
	{
		Class<T> cls = getGenericPOObject();
		return getTableFields(cls);
	}
	
	private String getTablePkField(Class<T> cls)
	{
		Table table_cls = cls.getAnnotation(Table.class);
		if(table_cls!=null){
			return cls.getAnnotation(Table.class).pK();
		} else {
			return "id";
		}
	}
	
	private Map<String,String> getTableFields(Class<T> cls)
	{
		Map<String,String> fields = new HashMap<String, String>();
		Field[] fs = cls.getDeclaredFields();
		
		for (Field field : fs) {
			String field_name = field.getName();
			if(field_name.equals("serialVersionUID")) continue;
			try {
				me.laochen.annotation.Field filed_define =  FieldUtils.getDeclaredField(cls, field_name,true).getAnnotation(me.laochen.annotation.Field.class);
				if(filed_define==null){
					fields.put(field_name,CamelCaseUtils.toUnderlineName(field_name));
				} else {
					fields.put(field_name,filed_define.value());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fields;
	}
	
	private String getTableName()
	{
		Class<T> cls = getGenericPOObject();
		return getTableName(cls);
	}
	
	private String getTableName(Class<T> cls){
		Table table = cls.getAnnotation(Table.class);
		if(table==null){
			String cls_name = cls.getSimpleName();
			return CamelCaseUtils.toUnderlineName(cls_name);
		} else {
			return table.value();
		}
	}
	
	//TODO 优化，防止SQL注入
	public Long add(T t)
	{
		String po_cls_name = t.getClass().getSimpleName();
		logger.debug(String.format("[execute.function]-Long add(%s %s)",po_cls_name,po_cls_name.toLowerCase()));
		
		Class<T> cls = getGenericPOObject();
		String table_name = getTableName(cls);
				
		Map<String,String> fields = getTableFields(cls);
		Map<String,String> filterFields = new HashMap<String, String>();
		for(String clsProperty:fields.keySet()){
			try {
				if(FieldUtils.readField(t, clsProperty,true)!=null){
					filterFields.put(clsProperty, fields.get(clsProperty));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(String.format("INSERT INTO `%s`(`%s`)",table_name,StringUtils.join(filterFields.values(), "`,`")));
		stringBuffer.append(String.format(" VALUES(:%s)",StringUtils.join(filterFields.keySet(), ",:")));
		
		String insertSQL = stringBuffer.toString();
		logger.debug("[inserSQL]"+insertSQL);
		
		SqlParameterSource ps = new BeanPropertySqlParameterSource(t);
		KeyHolder keyholder = new GeneratedKeyHolder();
		try {			
			namedParameterJdbcTemplate.update(insertSQL, ps, keyholder);
			return keyholder.getKey().longValue();
		} catch (Exception e) {
			logger.warn("[insertSQL->Error]"+insertSQL+"; Reason:"+e.getMessage());
		}
		return 0L;
	}
	
	public T update(T t)
	{
		String po_cls_name = t.getClass().getSimpleName();
		logger.debug(String.format("[execute.function]-%s update(%s %s)",po_cls_name,po_cls_name,po_cls_name.toLowerCase()));
		
		Class<T> cls = getGenericPOObject();
		String table_pk_name = getTablePkField(cls);
				
		T result = null;
		try {
			T et = (T) findByPk((Long) FieldUtils.readField(t, table_pk_name,true));
			if(et!=null){
				String table_name = getTableName(cls);
				
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(String.format("UPDATE `%s` SET ",table_name));
				
				Map<String,String> fields = getTableFields(cls);
				for(String clsProperty:fields.keySet()){
					try {
						if(FieldUtils.readField(t, clsProperty,true)!=null){
							stringBuffer.append(String.format("`%s`=:%s,",fields.get(clsProperty),clsProperty));
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				
				String updateSQL = stringBuffer.toString();
				if(updateSQL.endsWith(",")){
					updateSQL = updateSQL.substring(0,updateSQL.length()-1);
				}
				
				updateSQL+=String.format(" WHERE `%s`=:%s",table_pk_name,CamelCaseUtils.toCamelCase(table_pk_name));
				try {
					SqlParameterSource ps = new BeanPropertySqlParameterSource(t);
					int affectedRows = namedParameterJdbcTemplate.update(updateSQL,ps);
					logger.debug("affected rows "+affectedRows);
				} catch (Exception e) {
					logger.warn("[updateSQL->Error]"+updateSQL+"; Reason:"+e.getMessage());
					return null;
				}
			} else {
				logger.warn(String.format("The object of %s's pk is not null", t.getClass().getSimpleName()));
			}
		} catch (IllegalAccessException e) {
			logger.warn("can not access the "+t.getClass().getSimpleName()+" pk "+table_pk_name+"; reason is "+e.getMessage());
		}
		return result;
	}
	
	public int execute(String execSQL)
	{
		return namedParameterJdbcTemplate.update(execSQL, new HashMap<String, Object>());
	}
	
	public int execute(String execSQL,Map<String,Object> params)
	{
		return namedParameterJdbcTemplate.update(execSQL,params);
	}
	
	
	/**
	 * ------------------------------------------------------------------------------------------------------------------------------------------------
	 * 				Integer count(...) 
	 * ------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	public Integer count(String condition)
	{
		String table_name = getTableName();
		String querySQL = String.format("SELECT COUNT(*) FROM `%s` where %s", table_name,condition);
		logger.debug("[querySQL]"+querySQL);
		try {			
			return namedParameterJdbcTemplate.queryForInt(querySQL, new HashMap<String, Object>());
		} catch (Exception e) {
			logger.warn("[querySQL->Error]"+querySQL+"; Reason:"+e.getMessage());
			return null;
		}
	}
	
	public Integer count()
	{
		return count("1");
	}
	
	public Integer count(T t)
	{
		Class<T> cls = getGenericPOObject();
		String table_name = getTableName(cls);
		String pk_name = getTablePkField(cls);
		
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(String.format("SELECT count(`%s`) FROM `%s` WHERE 1",pk_name,table_name));

		Map<String,String> fields = getTableFields(cls);
		for(String clsProperty:fields.keySet()){
			String tableField = fields.get(clsProperty);
			try {
				if(FieldUtils.readField(t, clsProperty,true)!=null){
					stringBuffer.append(String.format(" AND `%s`=:%s", tableField,clsProperty));
				}
			} catch (IllegalAccessException e) {
				logger.warn("can not access the "+t.getClass().getSimpleName()+" function "+clsProperty);
			}
		}
		String querySQL = stringBuffer.toString();
		logger.debug("[querySQL]"+querySQL);
		
		try {			
			SqlParameterSource paramSource = new BeanPropertySqlParameterSource(t);
			return namedParameterJdbcTemplate.queryForObject(querySQL, paramSource, Integer.class);
		} catch (Exception e) {
			logger.warn("[querySQL->Error]"+querySQL+"; Reason:"+e.getMessage());
		}
		return 0;
	}
	
	public Integer count(Criteria criteria){
		return null;
	}
	
	
	
	/**
	 * ------------------------------------------------------------------------------------------------------------------------------------------------
	 * 				T findOne()
	 * ------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	
	public T findByPk(Long id) {
		logger.debug("[execute.function]-findByPk");
		Class<T> cls = getGenericPOObject();
		
		Table table = cls.getAnnotation(Table.class);
		if(table!=null){
			logger.debug("[po.annotation]"+cls.getSimpleName()+"=>"+table.toString());
			String querySQL = String.format("select * from %s where %s='%d'",table.value(),table.pK(),id);
			logger.debug("[querySQL]"+querySQL);
			
			SqlParameterSource ps = new BeanPropertySqlParameterSource(cls);
			return (T) namedParameterJdbcTemplate.queryForObject(querySQL, ps,new BeanPropertyRowMapper<T>(cls));
		} else {
			logger.warn(cls.getSimpleName()+" can not load");
		}
		return null;
	}
	
	public T findOne(T t){
		Class<T> cls = getGenericPOObject();
		logger.debug(String.format("[execute.function]-findOne(findOne(%s %s))",cls.getSimpleName(),cls.getSimpleName().toLowerCase()));
		
		System.err.println("device:"+t.toString());
		Field[] fs = t.getClass().getDeclaredFields();
		for (Field f : fs) {
			System.err.println("field is:"+f.getName()+":"+f);
		}
		return t;
	}	
	
	@SuppressWarnings("unchecked")
	public T findOne(String condition)
	{
		logger.debug("[execute.function]-findOne(String condition)");
		if(StringUtils.isNotEmpty(condition)){
			ParameterizedType parameterizedType = (ParameterizedType)this.getClass().getGenericSuperclass();
			logger.debug("ParameterizedType is "+parameterizedType);
			
			Class<T> cls = (Class<T>) parameterizedType.getActualTypeArguments()[0];
			logger.debug("getActualTypeArguments[0] is "+ cls.getCanonicalName());
			
			Table table = cls.getAnnotation(Table.class);
			if(table!=null){
				logger.debug("[po.annotation]"+cls.getSimpleName()+"=>"+table.toString());
				
				if(condition == null){
					condition = "1";
				} 
				String querySQL = String.format("SELECT * FROM %s WHERE %s limit 1",table.value(),condition);
				logger.debug("[querySQL]"+querySQL);
				
				Integer nums = count(condition);
				if(nums != null && nums>0){					
					try {
						SqlParameterSource ps = new BeanPropertySqlParameterSource(cls);					
						return (T) namedParameterJdbcTemplate.queryForObject(querySQL, ps,new BeanPropertyRowMapper<T>(cls));
					} catch (Exception e) {
						logger.warn("[querySQL->Error]"+querySQL+"; Reason:"+e.getMessage());
						return null;
					}
				} else {
					logger.warn("[querySQL->warn] no records "+querySQL);
					return null;
				}
			} else {
				logger.warn(cls.getSimpleName()+" can not load");
			}
		} else {
			logger.warn("This method is need one param and must not empty!");
		}
		return null;
	}
	
	public T findOneBySQL(String querySQL){
		final Class<T> cls = getGenericPOObject();
		logger.debug("[querySQL]"+querySQL);
		return (T) namedParameterJdbcTemplate.queryForObject(querySQL, new HashMap<String, Object>(),new RowMapper<T>(){
			public T mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				T object = null;
				try {
					object = (T) cls.newInstance();
					Field[] fs = cls.getDeclaredFields();
					for (Field field_name : fs) {
						if(field_name.getName().equals("serialVersionUID")) continue;						
						Object obj = rs.getObject(CamelCaseUtils.toUnderlineName(field_name.getName()));						
						if(obj!=null){
							try {
								MethodUtils.invokeMethod(object, String.format("set%s", StringUtils.capitalize(field_name.getName()))
										,obj);
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (InstantiationException e) {
					logger.warn("class can not instantiation "+cls.getSimpleName());
				} catch (IllegalAccessException e) {
					logger.warn("class can not access "+cls.getSimpleName());
				}
				return object;
			}
		});	
	}
	
	public T findOne(Criteria criteria){
		return null;	
	}
	
	
	/**
	 * ------------------------------------------------------------------------------------------------------------------------------------------------
	 * 				List<T> find[All] 
	 * ------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	
	public List<T> find(T t)
	{
		String po_cls_name = t.getClass().getSimpleName();
		logger.debug(String.format("[execute.function]-List<%s> find(%s %s)",po_cls_name,po_cls_name,po_cls_name.toLowerCase()));
		
		Class<T> cls = getGenericPOObject();
		
		String table_name = getTableName(cls);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("SELECT * FROM `"+table_name+"` WHERE 1");
	
		Map<String,String> fields = getTableFields(cls);
		for(String clsProperty:fields.keySet()){			
			String tableField = fields.get(clsProperty);
			try {
				if(FieldUtils.readField(t, clsProperty,true)!=null){
					stringBuffer.append(String.format(" AND `%s`=:%s", tableField,clsProperty));
				}
			} catch (IllegalAccessException e) {
				logger.warn("can not access the " +t.getClass().getSimpleName()+"->function "+clsProperty);
			}
		}
		
		String querySQL = stringBuffer.toString();
		logger.debug("[querySQL]"+querySQL);
		
		try {			
			SqlParameterSource ps = new BeanPropertySqlParameterSource(t);
			return namedParameterJdbcTemplate.query(querySQL, ps,new BeanPropertyRowMapper<T>(cls));
		} catch (Exception e) {
			logger.warn("[querySQL->Error]"+querySQL+"; Reason:"+e.getMessage());
			return null;
		}
	}
	
	public List<T> findAll()
	{
		Class<T> cls = getGenericPOObject();
		String table_name = getTableName(cls);
		
		String querySQL = String.format("SELECT * FROM `%s` WHERE 1", table_name);
		logger.debug("[querySQL]"+querySQL);
		try {
			SqlParameterSource ps = new BeanPropertySqlParameterSource(cls);
			return namedParameterJdbcTemplate.query(querySQL, ps,new BeanPropertyRowMapper<T>(cls));			
		} catch (Exception e) {
			logger.warn("[querySQL->Error]"+querySQL+"; Reason:"+e.getMessage());
			return null;
		}
	}
	
	public List<T> findBySQL(String querySQL)
	{
		final Class<T> cls = getGenericPOObject();
		logger.debug("[querySQL]"+querySQL);
		SqlParameterSource ps = new BeanPropertySqlParameterSource(cls);
		return namedParameterJdbcTemplate.query(querySQL, ps,new BeanPropertyRowMapper<T>(cls));
	}
	
	public List<T> find(String condition)
	{
		Class<T> cls = getGenericPOObject();
		String querySQL = String.format("SELECT * FROM `%s` WHERE 1 AND %s", getTableName(cls),condition);		
		logger.debug("[querySQL]"+querySQL);
		return findBySQL(querySQL);
	}
	
	public List<T> find(Criteria criteria)
	{
		return null;
	}
}
