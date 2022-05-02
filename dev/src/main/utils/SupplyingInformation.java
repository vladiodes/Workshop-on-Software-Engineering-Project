package main.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SupplyingInformation {
    private String address;
    private LocalDate date;
    private Boolean output;

    /***
     * used for testing only.
     */
    public SupplyingInformation(Boolean output) {
        this.output = output;
    }

    public SupplyingInformation(String address, LocalDate date) {
        this.address = address;
        this.date = date;
        output = null;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDate() {
        return date;
    }

    public Boolean getOutput() {
        return output;
    }
}
