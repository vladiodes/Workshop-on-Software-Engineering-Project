package main.Persistence;

import javax.persistence.*;

public class Persistence {

    public void set() {
        EntityManagerFactory factory = javax.persistence.Persistence.createEntityManagerFactory("market");
        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();

        manager.getTransaction().commit();
        manager.close();
        factory.close();
    }
}
