package main.Persistence;

import main.Users.User;

import javax.persistence.*;
import java.util.List;

public class DAO {
    private static DAO instance;
    public static DAO getInstance(){
        if(instance==null)
            instance=new DAO();
        return instance;
    }
    private final EntityManager entityManager;
    private String persistence_unit = "Market";
    private DAO(){
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

    public <T> void merge(T obj){
        EntityTransaction et = null;
        synchronized (entityManager) {
            try{
                et = entityManager.getTransaction();
                et.begin();
                entityManager.merge(obj);
                et.commit();
            }
            catch (Exception e) {
                e.printStackTrace();
                if(et != null)
                    et.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    public <T> List<T> getEntities(String query,Class<T> cls) {
        TypedQuery<T> tq = entityManager.createQuery(query, cls);
        List<T> list;
        try{
            list = tq.getResultList();
            return list;
        }
        catch(NoResultException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getUsers(){
        return getEntities("select u from User u where u.user_id is not null",User.class);
    }

}
