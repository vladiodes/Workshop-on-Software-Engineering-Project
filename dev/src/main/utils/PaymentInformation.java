package main.utils;

import java.time.LocalDate;

public class PaymentInformation {
    private final String cardNumber;
    private final LocalDate expDate;
    private final int cvv;
    private final String name;
    private final String email;

    public PaymentInformation(String cardNumber, LocalDate expDate, int cvv, String name, String email) {
        this.cardNumber = cardNumber;
        this.expDate = expDate;
        this.cvv = cvv;
        this.name = name;
        this.email = email;
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
