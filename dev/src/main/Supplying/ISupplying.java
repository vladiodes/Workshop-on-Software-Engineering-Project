package dev.src.main.Supplying;

import java.time.LocalDate;

public interface ISupplying {
    boolean bookDelivery(String location, LocalDate dateToSupply);
    boolean supply(String location);
}
