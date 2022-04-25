package main.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SupplyingInformation {
    String address;
    LocalDateTime date;

    public SupplyingInformation(String address, LocalDateTime date) {
        this.address = address;
        this.date = date;
    }
}
