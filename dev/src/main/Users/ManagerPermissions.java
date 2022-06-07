package main.Users;

import main.Persistence.DAO;
import main.Stores.Store;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

@Entity
public class ManagerPermissions implements Serializable {

    @Id
    @OneToOne(cascade = CascadeType.ALL)
    private User appointedToManager;

    @Id
    @OneToOne(cascade = CascadeType.ALL)
    private User appointedBy;
    @Id
    @OneToOne(cascade = CascadeType.ALL)
    private Store IStore;
    @ElementCollection(targetClass = StorePermission.class)
    @CollectionTable
    @Enumerated(EnumType.STRING)
    private Collection<StorePermission> permissions;

    public ManagerPermissions(User appointedToManager, User appointedBy, Store IStore){
        permissions= Collections.synchronizedList(new LinkedList<>());
        this.appointedToManager=appointedToManager;
        this.IStore = IStore;
        this.appointedBy=appointedBy;
        addDefaultPermissions();
    }

    public ManagerPermissions() {

    }

    private void addDefaultPermissions() {
        permissions.add(StorePermission.AnswerAndTakeRequests);
        permissions.add(StorePermission.ViewStoreHistory);
    }

    public Store getStore() {
        return IStore;
    }


    public boolean hasPermission(StorePermission permission) {
        return permissions.contains(permission);
    }

    public User getAppointedBy() {
        return appointedBy;
    }

    public User getAppointedToManager() {
        return appointedToManager;
    }

    public void addPermission(StorePermission permission) {
        if(!permissions.contains(permission))
            permissions.add(permission);
        DAO.getInstance().merge(this);
    }

    public void removePermission(StorePermission permission) {
        permissions.remove(permission);
        DAO.getInstance().merge(this);
    }

    public String permissionsToString() {
        StringBuilder builder=new StringBuilder();
        for(StorePermission permission:permissions){
            builder.append(StorePermission.stringOf(permission));
            builder.append(", ");
        }
        return builder.toString();
    }

    public Collection<StorePermission> getPermissions() {
        return permissions;
    }
}
