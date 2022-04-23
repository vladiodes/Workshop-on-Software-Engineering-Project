package main.Supplying;


import main.Stores.Product;

import java.time.LocalDate;
import java.util.List;

public class SupplyingAdapter implements ISupplying {

    @Override
    public boolean bookDelivery(String location, LocalDate dateToSupply) {
        return false;
    }

    @Override
    public boolean supply(String location, List<Product> productToSupply) {
        return false;
    }

}
