package main.ExternalServices.Supplying;


import main.Stores.Product;
import main.utils.SupplyingInformation;

import java.util.HashMap;

public class SupplyingAdapter implements ISupplying {

    @Override
    public boolean bookDelivery(SupplyingInformation si) {
        return true;
    }

    @Override
    public boolean supply(SupplyingInformation si, HashMap<Product, Integer> productToSupply) {
        return true;
    }

    @Override
    public void abort(SupplyingInformation si) {

    }

}
