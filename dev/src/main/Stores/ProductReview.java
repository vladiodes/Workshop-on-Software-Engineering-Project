package main.Stores;

import main.Users.User;

public class ProductReview {
    private User user;
    private Product product;
    private String desc;
    private double points;

    public ProductReview(User user, Product product, String desc, double points) throws Exception
    {
        this.user = user;
        this.product = product;

        if (desc.isBlank())
            throw new Exception("Review description cant be empty or blank");
        if(desc.length()>501)
            throw new Exception("Review is longer than 500 characters");
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
