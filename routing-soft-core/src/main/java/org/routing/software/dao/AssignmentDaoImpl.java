package org.routing.software.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.routing.software.jpos.AssignmentJpo;


@ApplicationScoped
public class AssignmentDaoImpl  extends AbstractRoutingEntityDao<AssignmentJpo> implements IAssignmentDao {


    public AssignmentDaoImpl() {
        super(AssignmentJpo.class);
    }
}
