package main.utils;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.Product;
import main.Users.User;

public class Bid implements Comparable {
    private Product product;
    private User user;
    private Double costumePrice;
    private PaymentInformation paymentInformation;
    private IPayment payment;
    private SupplyingInformation supplyingInformation;
    private ISupplying supplying;

    public Bid (Product product, User user, Double costumePrice, PaymentInformation paymentInformation, IPayment payment, SupplyingInformation supplyingInformation, ISupplying supplying) {
        this.product = product;
        this.user = user;
        this.costumePrice = costumePrice;
        this.paymentInformation = paymentInformation;
        this.payment = payment;
        this.supplyingInformation = supplyingInformation;
        this.supplying = supplying;
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

    public IPayment getPayment() {
        return payment;
    }

    public SupplyingInformation getSupplyingInformation() {
        return supplyingInformation;
    }

    public ISupplying getSupplying() {
        return supplying;
    }


    @Override
    public int compareTo(Object o) {
        if (o == null)
            return 1;
        Bid other = (Bid) o ;
        return other.getCostumePrice().compareTo(((Bid) o).getCostumePrice());
    }
}
