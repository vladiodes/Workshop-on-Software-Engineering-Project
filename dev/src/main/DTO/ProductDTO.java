package main.DTO;

import main.Stores.Product;

public class ProductDTO {
    private String productName;
    private String description;

    public ProductDTO(Product product) {

    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }
}
