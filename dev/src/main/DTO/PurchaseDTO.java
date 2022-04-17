package main.DTO;

import java.time.LocalDateTime;
import java.util.HashMap;

public class PurchaseDTO {
    private HashMap<ProductDTO,Integer> productsQuantities;
    private LocalDateTime purchaseDate;

    public PurchaseDTO(HashMap<ProductDTO,Integer> productsQuantities, LocalDateTime purchaseDate){
        this.productsQuantities=productsQuantities;
        this.purchaseDate=purchaseDate;
    }
}
