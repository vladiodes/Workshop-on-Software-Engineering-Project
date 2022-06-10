package main.ExternalServices.Supplying;

import main.Stores.Product;
import main.utils.SupplyingInformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ISupplying {
    boolean supply(SupplyingInformation si, Map<Product, Integer> productToSupply);
    void abort (SupplyingInformation si) throws Exception;
}
