package org.routing.software.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.routing.software.jpos.AbstractEntity;
import org.routing.software.jpos.IdentifiableEntity;
import org.routing.software.service.JpaHelper;

import java.util.List;
import java.util.Optional;

/**
 * This is a generic dao used for most class persistence
 * @param <T>
 */
public abstract class AbstractDao<T extends IdentifiableEntity> implements IGenericDao<T> {

    private Class<T> persistenceClass;

    public AbstractDao() {}

    public Class<T> getPersistenceClass() {
        return persistenceClass;
    }

    public void setPersistenceClass(Class<T> persistenceClass) {
        this.persistenceClass = persistenceClass;
    }

    @Override
    public Optional<T> insert(T t) {
        EntityManager em = getEntityManager();
        em.persist(t);
        return Optional.of(t);
    }

    @Override
    public Optional<T> update(T t) {
        EntityManager em = getEntityManager();
        em.merge(t);
        return Optional.of(t);
    }

    @Override
    public void delete(Object id) {
        Optional<T> t = getById(id);
        if (Optional.of(t).isPresent()) {
            T entity = t.get();
            if (entity instanceof AbstractEntity) {
                ((AbstractEntity) entity).setSoftDeleted(true);
                update(entity);
            } else {
                //TODO LOGGER
                throw new IllegalStateException("Entity does not extend AbstractEntity, cannot soft delete");
            }
        }
    }

    @Override
    public Optional<T> getById(Object id) {
        return Optional.ofNullable(getEntityManager().find(persistenceClass, id));
    }


    @Override
    public List<T> getAll() {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> selectQuery = cb.createQuery(getPersistenceClass());
        Root<T> root = selectQuery.from(getPersistenceClass());

        selectQuery.select(root);
        List<T> result = em.createQuery(selectQuery).getResultList();
        return result;

    }

    //TODO create the getByCriteria based on
//    @Override
//    public List<? extends T> getByCriteria(Map<String, Object> criteria) {
//        return List.of();
//    }
//
//    @Override
//    public List<T> getByCriteria(Class<T> clazz, Map<String, Object> criteria) {
//        return List.of();
//    }

    public EntityManager getEntityManager() {
        return JpaHelper.getEntityManager();
    }

}
