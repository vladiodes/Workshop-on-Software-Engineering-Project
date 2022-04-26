package main.Stores;

import main.Users.User;

public class StoreReview {
    private User user;
    private IStore IStore;
    private String desc;
    private double points;

    public StoreReview(User user, IStore IStore, String desc, double points) throws Exception
    {
        this.user = user;
        this.IStore = IStore;

        if (desc.isBlank())
            throw new IllegalArgumentException("Review description cant be empty or blank");
        this.desc = desc;

        if(points<0)
            throw new IllegalArgumentException("Review points cant be negative");
        this.points = points;
    }

    public User getUser() {
        return user;
    }

    public double getPoints() {
        return points;
    }
    public String getDescription()
    {
        return desc;
    }
}
