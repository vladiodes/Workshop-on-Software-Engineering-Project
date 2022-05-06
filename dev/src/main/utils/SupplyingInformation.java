package main.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SupplyingInformation {
    private final String address;
    private final LocalDate date;

    public SupplyingInformation(String address, LocalDate date) {
        this.address = address;
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDate() {
        return date;
    }
}
