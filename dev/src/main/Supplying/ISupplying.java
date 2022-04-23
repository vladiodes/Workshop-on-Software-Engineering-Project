package main.Supplying;

import main.Stores.Product;

import java.time.LocalDate;
import java.util.List;

public interface ISupplying {
    boolean bookDelivery(String location, LocalDate dateToSupply);
    boolean supply(String location, List<Product> productToSupply);
}
