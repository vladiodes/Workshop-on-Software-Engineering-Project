package main.Stores.PurchasePolicy.ProductPolicy;

import main.Users.User;

import javax.persistence.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Entity
public class bidApprovedByUserList {
    @Id
    @GeneratedValue
    private int id;

    @OneToMany
    private List<User> approvedBy;

    public bidApprovedByUserList(){
        approvedBy= Collections.synchronizedList(new LinkedList<>());
    }

    public List<User> getApprovedBy() {
        return approvedBy;
    }

    public void add(User approvingUser) {
        if(approvedBy.contains(approvingUser))
            throw new IllegalArgumentException("You've already approved this bid!");
        approvedBy.add(approvingUser);
    }
}
