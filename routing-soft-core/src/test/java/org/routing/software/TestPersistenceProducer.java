package org.routing.software;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class TestPersistenceProducer {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("routingPU");

    @Produces
    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }
}
