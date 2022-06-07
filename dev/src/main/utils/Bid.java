package main.utils;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.Product;
import main.Users.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Bid implements Serializable,Comparable {
    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private Product product;
    @OneToOne
    private User user;
    private Double costumePrice;
    @OneToOne
    private PaymentInformation paymentInformation;
    @OneToOne
    private SupplyingInformation supplyingInformation;

    public Bid (Product product, User user, Double costumePrice, PaymentInformation paymentInformation, SupplyingInformation supplyingInformation) {
        this.product = product;
        this.user = user;
        this.costumePrice = costumePrice;
        this.paymentInformation = paymentInformation;
        this.supplyingInformation = supplyingInformation;
    }

    public Bid() {

    }

    public Product getProduct() {
        return product;
    }

    public User getUser() {
        return user;
    }

    public Double getCostumePrice() {
        return costumePrice;
    }

    public PaymentInformation getPaymentInformation() {
        return paymentInformation;
    }



    public SupplyingInformation getSupplyingInformation() {
        return supplyingInformation;
    }



    @Override
    public int compareTo(Object o) {
        if (o == null)
            return 1;
        Bid other = (Bid) o ;
        return other.getCostumePrice().compareTo(((Bid) o).getCostumePrice());
    }
}
