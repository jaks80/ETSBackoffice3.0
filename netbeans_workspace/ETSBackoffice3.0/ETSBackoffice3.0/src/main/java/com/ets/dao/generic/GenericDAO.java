package com.ets.dao.generic;

import java.io.Serializable;
import java.util.List;
import org.hibernate.Query;

/**
 *
 * @author Yusuf
 * @param <T>
 * @param <Long>
 */
public interface GenericDAO<T, Long extends Serializable> {
 
    public void save(T entity);
 
    public void merge(T entity);
 
    public void delete(T entity);
 
    public List findAll(Class clazz);
 
    public T findByID(Class clazz, Long id);
    
    public List<T> findMany(Query query);
 
    public T findOne(Query query);
}