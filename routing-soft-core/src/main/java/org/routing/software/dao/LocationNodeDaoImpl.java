package org.routing.software.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.routing.software.jpos.LocationNodeJpo;

@ApplicationScoped
public class LocationNodeDaoImpl extends AbstractRoutingEntityDao<LocationNodeJpo> implements ILocationNodeDao{

    public LocationNodeDaoImpl() {
        super(LocationNodeJpo.class);
    }

}
