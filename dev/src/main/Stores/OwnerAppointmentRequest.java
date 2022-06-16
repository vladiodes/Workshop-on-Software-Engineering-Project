package main.Stores;

import main.Persistence.DAO;
import main.Users.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

@Entity
public class OwnerAppointmentRequest implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    @OneToMany
    private Collection<User> approvedBy;
    @OneToOne
    private User requestedBy;
    @OneToOne
    private User userToAppoint;
    public OwnerAppointmentRequest(User requestedBy, User userToAppoint) {
        this.requestedBy = requestedBy;
        this.userToAppoint = userToAppoint;
        this.approvedBy = Collections.synchronizedList(new LinkedList<>());
        this.approvedBy.add(requestedBy);
    }

    public boolean addVote(User u) {
        // TODO: persist changes to database
        boolean res = approvedBy.add(u);
        DAO.getInstance().merge(this);
        return res;
    }
    public Collection<User> getApprovedBy() {
        return this.approvedBy;
    }
    public User getRequestedBy(){
        return this.requestedBy;
    }

    public User getUserToAppoint() {
        return userToAppoint;
    }
    public boolean didVote(User u) {
        return approvedBy.contains(u);
    }
}
