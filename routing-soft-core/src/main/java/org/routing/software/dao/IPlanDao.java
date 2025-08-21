package org.routing.software.dao;

import org.routing.software.jpos.PlanJpo;

import java.util.Optional;


public interface IPlanDao extends IGenericRoutingEntityDao<PlanJpo> {

    Optional<PlanJpo> update(PlanJpo t);
}
