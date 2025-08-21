package org.routing.software.dao;

import java.util.List;
import java.util.Optional;

public interface IGenericDao<T> {
    Optional<T> insert(T t);
    Optional<T> update(T t);
    void delete(Object id);
    Optional<T> getById(Object id);
    List<T> getAll();
//    List<? extends T> getByCriteria(Map<String, Object> criteria);
//    List<T> getByCriteria(Class<T> clazz, Map<String, Object> criteria);
}

