package main.utils;

public class PaymentInformation {
    private String cardNumber;
    private int year;
    private int month;
    private int day;
    private int cvv;
    private String name;
    private String email;
    private Boolean output;

    public PaymentInformation(String cardNumber, int year, int month, int day, int cvv, String name, String email) {
        this.cardNumber = cardNumber;
        this.year = year;
        this.month = month;
        this.day = day;
        this.cvv = cvv;
        this.name = name;
        this.email = email;
        this.output = null;
    }

    /***
     * used for testing only!
     */
    public PaymentInformation(Boolean output) {
        this.output = output;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
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

    public Boolean getOutput() {
        return output;
    }
}
