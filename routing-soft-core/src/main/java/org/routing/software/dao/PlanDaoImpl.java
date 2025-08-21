package org.routing.software.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.routing.software.jpos.PlanJpo;
import org.routing.software.jpos.TruckJpo;

import java.util.Optional;

@ApplicationScoped
public class PlanDaoImpl extends AbstractRoutingEntityDao<PlanJpo> implements IPlanDao {

    public PlanDaoImpl() {
        super(PlanJpo.class);
    }

    public Optional<PlanJpo> update(PlanJpo t) {
        EntityManager em = getEntityManager();
        em.merge(t);
        return Optional.of(t);
    }
}
