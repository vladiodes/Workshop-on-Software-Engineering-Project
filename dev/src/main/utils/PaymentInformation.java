package main.utils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class PaymentInformation {
    @Id
    @GeneratedValue
    private int id;
    private  String cardNumber;
    private  LocalDate expDate;
    private  int cvv;
    private  String name;
    private  String email;

    public PaymentInformation(String cardNumber, LocalDate expDate, int cvv, String name, String email) {
        this.cardNumber = cardNumber;
        this.expDate = expDate;
        this.cvv = cvv;
        this.name = name;
        this.email = email;
    }

    public PaymentInformation() {

    }

    public LocalDate getExpDate() {
        return expDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getCvv() {
        return cvv;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

}
