package main.utils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class PaymentInformation {
    @Id
    @GeneratedValue
    private  String cardNumber;
    private  LocalDate expDate;
    private  int cvv;
    private  String name;
    private String id;

    private int transactionId;

    public PaymentInformation(String cardNumber, LocalDate expDate, int cvv, String name, String id) {
        this.cardNumber = cardNumber;
        this.expDate = expDate;
        this.cvv = cvv;
        this.name = name;
        this.id = id;
        this.transactionId = 0;
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

    public String getId() {
        return id;
    }

    public int getTransactionId()
    {
        return this.transactionId;
    }

    public void setTransactionId(int newTrans)
    {
        this.transactionId = newTrans;
    }


}
