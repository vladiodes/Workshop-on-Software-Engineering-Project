package main.Stores;

import main.Users.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;


@Entity
public class ProductReview implements Serializable {

    @Id
    @OneToOne
    private User user;

    @Id
    @OneToOne
    private Product product;
    private String description;
    private double points;

    public ProductReview(User user, Product product, String desc, double points)
    {
        this.user = user;
        this.product = product;

        if (desc.isBlank())
            throw new IllegalArgumentException("Review description cant be empty or blank");
        if(desc.length()>501)
            throw new IllegalArgumentException("Review is longer than 500 characters");
        this.description = desc;

        if(points<0)
            throw new IllegalArgumentException("Review points cant be negative");
        this.points = points;

    }

    public ProductReview() {

    }

    public double getPoints() {
        return points;
    }
    public String getDescription()
    {
        return description;
    }
}
