package main.Persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class DAO {
    private EntityManager entityManager;
    private String persistence_unit = "Market";
    public DAO(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistence_unit);
        entityManager= emf.createEntityManager();
    }

    public <T> void persist(T obj){
        EntityTransaction et = null;
        synchronized (entityManager) {
            try{
                et = entityManager.getTransaction();
                et.begin();
                entityManager.persist(obj);
                et.commit();
            }
            catch (Exception e) {
                if(et != null)
                    et.rollback();
                throw new RuntimeException(e);
            }
        }
    }

}
