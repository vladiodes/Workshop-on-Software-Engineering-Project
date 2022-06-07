package main.Stores;

import main.Users.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
public class StoreReview implements Serializable {
    @Id
    @OneToOne
    private User user;

    @Id
    @OneToOne
    private Store IStore;
    private String description;
    private double points;

    public StoreReview(User user, Store IStore, String desc, double points) throws Exception
    {
        this.user = user;
        this.IStore = IStore;

        if (desc.isBlank())
            throw new IllegalArgumentException("Review description cant be empty or blank");
        this.description = desc;

        if(points<0)
            throw new IllegalArgumentException("Review points cant be negative");
        this.points = points;
    }

    public StoreReview() {

    }

    public User getUser() {
        return user;
    }

    public double getPoints() {
        return points;
    }
    public String getDescription()
    {
        return description;
    }
}
