package org.routing.software.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.routing.software.jpos.AssignmentJpo;
import org.routing.software.jpos.IdentifiableEntity;
import org.routing.software.jpos.PlanJpo;
import org.routing.software.service.JpaHelper;
import java.util.List;
import java.util.Optional;

/**
 * This is a generic dao used for most class persistence
 * @param <T>
 */
public abstract class AbstractRoutingEntityDao<T extends IdentifiableEntity> implements IGenericRoutingEntityDao<T> {

    private final Class<T> persistenceClass;

    protected AbstractRoutingEntityDao(Class<T> persistenceClass) {
        this.persistenceClass = persistenceClass;
    }

    public Class<T> getPersistenceClass() {
        return persistenceClass;
    }

    public Optional<T> insert(T t) {
        EntityManager em = getEntityManager();
        em.persist(t);
        return Optional.of(t);
    }

    @Override
    public List<T> getAllEntitiesByUserUUID(Object userUUID) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> selectQuery = cb.createQuery(getPersistenceClass());
        Root<T> root = selectQuery.from(getPersistenceClass());

        Predicate uuidPredicate = cb.equal(root.get("userJpo").get("uuid"), userUUID);
        Predicate isNotSoftDeleted = cb.isFalse(root.get("isSoftDeleted"));

        selectQuery.select(root).where(uuidPredicate, isNotSoftDeleted);
        List<T> result = em.createQuery(selectQuery).getResultList();

        return result;
    }

    @Override
    public Optional<T> getSingleEntityByIdByUserUUID(Object entityId, Object userUUID) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> selectQuery = cb.createQuery(getPersistenceClass());
        Root<T> root = selectQuery.from(getPersistenceClass());

        Predicate uuidPredicate = cb.equal(root.get("userJpo").get("uuid"), userUUID);
        Predicate entityIdPredicate = cb.equal(root.get("id"), entityId);
        Predicate isNotSoftDeleted = cb.isFalse(root.get("isSoftDeleted"));
        Predicate finalPredicate = cb.and(uuidPredicate, entityIdPredicate, isNotSoftDeleted);

        selectQuery.select(root).where(finalPredicate);
        List<T> result = em.createQuery(selectQuery).getResultList();

        return !result.isEmpty() ? Optional.of(result.get(0)) : Optional.empty();
    }

    @Override
    public List<T> getAllEntitiesByPlanIdAndByUserUUID(Object planId, Object userUUID) {

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> selectQuery = cb.createQuery(getPersistenceClass());
        Root<T> root = selectQuery.from(getPersistenceClass());

        Predicate uuidPredicate = cb.equal(root.get("userJpo").get("uuid"), userUUID);

        //I have one to many relationship so i have to join
        Join<T, AssignmentJpo> assignmentJoin = root.join("assignmentJpos"); //the name of the field in TruckJpo
        Predicate planPredicate = cb.equal(assignmentJoin.get("plan").get("id"), planId);
        Predicate isNotSoftDeleted = cb.isFalse(root.get("isSoftDeleted"));
        Predicate finalPredicate = cb.and(uuidPredicate, planPredicate, isNotSoftDeleted);

        selectQuery.select(root).distinct(true).where(finalPredicate);
        List<T> result = em.createQuery(selectQuery).getResultList();

        return result;
    }

    @Override
    public boolean isEntityExists(Object entityId, Object userUUID) {
        return getSingleEntityByIdByUserUUID(entityId, userUUID).isPresent();
    }

    @Override
    public boolean hasPlanAssigned(Object entityId, Object userUUID) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> selectQuery = cb.createQuery(getPersistenceClass());
        Root<T> root = selectQuery.from(getPersistenceClass());

        Predicate uuidPredicate = cb.equal(root.get("userJpo").get("uuid"), userUUID);
        Predicate entityIdPredicate = cb.equal(root.get("id"), entityId);

        Join<T, AssignmentJpo> assignmentJoin = root.join("assignmentJpos"); //the name of the field in TruckJpo
        Predicate planPredicate = cb.isNotNull(assignmentJoin.get("plan")); //for single attribute, isEmpty for collection
        Predicate isNotSoftDeleted = cb.isFalse(root.get("isSoftDeleted"));
        Join<AssignmentJpo, PlanJpo> planJoin = assignmentJoin.join("plan"); //the name of the field in planJpo
        Predicate planSoftDeletePredicate = cb.isFalse(planJoin.get("isSoftDeleted"));
        Predicate finalPredicate = cb.and(uuidPredicate,
                entityIdPredicate,
                planPredicate,
                isNotSoftDeleted,
                planSoftDeletePredicate);

        selectQuery.select(root).where(finalPredicate);
        List<T> result = em.createQuery(selectQuery).setMaxResults(1).getResultList(); //we put set max results for performance issue because
        //there are many assignments, we want at least one to validate the method

        return !result.isEmpty();
    }

    @Override
    public Optional<T> deleteEntity(Object entityId, Object userUUID) {

        Optional<T> t = getSingleEntityByIdByUserUUID(entityId, userUUID);

        if (t.isEmpty()) {
            return Optional.empty();
        }

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<T> softDeleteQuery = cb.createCriteriaUpdate(getPersistenceClass());
        Root<T> root = softDeleteQuery.from(getPersistenceClass());

        Predicate uuidPredicate = cb.equal(root.get("userJpo").get("uuid"), userUUID);
        Predicate entityIdPredicate = cb.equal(root.get("id"), entityId);
        Predicate finalPredicate = cb.and(uuidPredicate, entityIdPredicate);

        softDeleteQuery.set(root.get("isSoftDeleted"), true).where(finalPredicate);
        em.createQuery(softDeleteQuery).executeUpdate();
        return t;
    }

    public EntityManager getEntityManager() {
        return JpaHelper.getEntityManager();
    }

}
