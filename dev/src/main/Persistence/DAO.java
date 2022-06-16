package main.Persistence;

import main.Stores.Store;
import main.Users.User;
import main.utils.SystemStats;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DAO {
    private static DAO instance;
    private boolean shouldPersist;
    private Map<Long,EntityTransaction> thread_transactions_map;
    private static boolean enablePersist=false;
    private static String persistence_unit = "Market";
    public static DAO getInstance(){
        if(instance==null)
            instance=new DAO(enablePersist,persistence_unit);
        return instance;
    }
    public static void enablePersist(){
        enablePersist=true;
    }
    public static void setPersistence_unit(String unit){
        persistence_unit=unit;
    }
    private EntityManager entityManager=null;
    private DAO(boolean shouldPersist,String persistence_unit) {
        this.shouldPersist=shouldPersist;
        if (shouldPersist) {
            try {
                EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistence_unit);
                entityManager = emf.createEntityManager();
            }
            catch (Exception e){
                DAO.enablePersist=false;
                entityManager=null;
            }
            thread_transactions_map=new ConcurrentHashMap<>();
        }
    }

    public static void disablePersist() {
        if (instance != null) {
            instance.closeCon();
            instance = null;
        }
        enablePersist=false;
    }

    public void openTransaction(){
        if(shouldPersist){
            EntityTransaction et = null;
            try{
                et = entityManager.getTransaction();
                et.begin();
                thread_transactions_map.put(Thread.currentThread().getId(),et);
            }
            catch (Exception e){
                e.printStackTrace();
                throw new IllegalArgumentException("Something went wrong with the database...");
            }
        }
    }

    public void commitTransaction() {
        if (shouldPersist) {
            EntityTransaction et = thread_transactions_map.get(Thread.currentThread().getId());
            if (et == null)
                return;
            try {
                if(!et.getRollbackOnly() && et.isActive())
                    et.commit();
            } catch (Exception e) {
                et.rollback();
                //throw new RuntimeException(e);
            }
        }
    }

    public <T> void persist(T obj){
        if(shouldPersist) {
            EntityTransaction et = thread_transactions_map.get(Thread.currentThread().getId());
            synchronized (entityManager) {
                try {
                    entityManager.persist(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Something went wrong with the database...");
                }
            }
        }
    }

    public <T> void remove(T obj){
        if(shouldPersist) {
            EntityTransaction et = thread_transactions_map.get(Thread.currentThread().getId());
            synchronized (entityManager) {
                try {
                    entityManager.remove(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Something went wrong with the database...");
                }
            }
        }
    }

    public <T> void merge(T obj){
        if(shouldPersist) {
            EntityTransaction et = thread_transactions_map.get(Thread.currentThread().getId());
            synchronized (entityManager) {
                try {
                    entityManager.merge(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Something went wrong with the database...");
                }
            }
        }
    }

    public <T> List<T> getEntities(String query,Class<T> cls) {
        if(shouldPersist) {
            TypedQuery<T> tq = entityManager.createQuery(query, cls);
            List<T> list;
            try {
                list = tq.getResultList();
                return list;
            } catch (NoResultException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            return new LinkedList<>();
        }
    }

    private void closeCon(){
        if(entityManager != null)
            this.entityManager.close();
    }

    public List<User> getUsers(){
        return getEntities("select u from User u where u.user_id is not null",User.class);
    }
    public List<Store> getStores() {
        return getEntities("select s from Store s where s.store_id is not null",Store.class);
    }

    public List<SystemStats> getStats() {
        return getEntities("select s from SystemStats s where s.date is not null",SystemStats.class);
    }
}
