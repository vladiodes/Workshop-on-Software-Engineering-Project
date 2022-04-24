package main.utils;

public class PaymentInformation {
    String cardNumber;
    int year;
    int month;
    int day;
    int cvv;
    String name;
    String email;

    public PaymentInformation(String cardNumber, int year, int month, int day, int cvv, String name, String email) {
        this.cardNumber = cardNumber;
        this.year = year;
        this.month = month;
        this.day = day;
        this.cvv = cvv;
        this.name = name;
        this.email = email;
    }
}
