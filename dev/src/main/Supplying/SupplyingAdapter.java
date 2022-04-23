package dev.src.main.Supplying;

import main.Supplying.ISupplying;

import java.time.LocalDate;

public class SupplyingAdapter implements ISupplying {

    @Override
    public boolean bookDelivery(String location, LocalDate dateToSupply) {
        return false;
    }

    @Override
    public boolean supply(String location) {
        return false;
    }
}
