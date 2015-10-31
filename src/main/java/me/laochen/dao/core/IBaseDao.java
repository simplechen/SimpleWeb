package me.laochen.dao.core;

import java.util.List;
import java.util.Map;

public interface IBaseDao<T> {
	public Long add(T t);
	public T update(T t);
	public int execute(String execSQL);
	public int execute(String execSQL,Map<String,Object> params);
	
	public T findByPk(Long id);
	
	public T findOne(String condition);
	public T findOne(T t);
	public T findOneBySQL(String querySQL);
	public T findOne(Criteria criteria);
	
	public Integer count();
	public Integer count(String condition);
	public Integer count(T t);
	public Integer count(Criteria criteria);

	
	public List<T> find(T t);
	public List<T> findAll();
	public List<T> findBySQL(String querySQL);
	public List<T> find(String condition);
	public List<T> find(Criteria criteria);
	
//	public List<T> find(Map<String,Object> criteria);
}
