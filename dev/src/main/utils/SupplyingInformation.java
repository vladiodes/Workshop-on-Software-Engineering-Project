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
    private int id;
    private  String address;
    private  LocalDate date;

    public SupplyingInformation(String address, LocalDate date) {
        this.address = address;
        this.date = date;
    }

    public SupplyingInformation() {

    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDate() {
        return date;
    }
}
