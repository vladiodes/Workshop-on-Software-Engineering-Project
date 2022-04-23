package main.Stores;

import main.Users.User;

public class StoreReview {
    private User user;
    private Store store;
    private String desc;
    private double points;

    public StoreReview(User user, Store store, String desc, double points) throws Exception
    {
        //TODO: Insert unique check
        this.user = user;
        this.store = store;

        if (desc.isBlank())
            throw new Exception("Review description cant be empty or blank");
        this.desc = desc;

        if(points<0)
            throw new Exception("Review points cant be negative");
        this.points = points;
    }

    public double getPoints() {
        return points;
    }
    public String getDescription()
    {
        return desc;
    }
}
