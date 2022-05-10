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

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder(purchaseDate.toString() + ":\n");
        for(ProductDTO p: productsQuantities.keySet()){
            builder.append(String.format("%s X%d\n",p.getProductName(),productsQuantities.get(p)));
        }
        return builder.toString();
    }
}
