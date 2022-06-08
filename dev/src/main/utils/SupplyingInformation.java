package main.utils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class SupplyingInformation {
    @Id
    @GeneratedValue
    private String name;
    private String address;

    private String city;

    private String country;

    private String zip;

    private int transactionId;
    public SupplyingInformation(String name, String address, String city, String country, String zip) {
        this.address = address;
        this.name = name;
        this.city = city;
        this.country = country;
        this.zip = zip;
        this.transactionId = 0;
    }

    public SupplyingInformation() {

    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public String getZip() {
        return zip;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
}
