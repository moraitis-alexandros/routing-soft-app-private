package org.routing.software.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.routing.software.jpos.TruckJpo;

@ApplicationScoped
public class TruckDaoImpl extends AbstractRoutingEntityDao<TruckJpo> implements ITruckDao {

    public TruckDaoImpl() {
        super(TruckJpo.class);
    }
}
