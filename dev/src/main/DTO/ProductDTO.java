package main.DTO;

import main.Stores.Product;

public class ProductDTO {
    private String productName;
    private String description;

    private String storeName;

    private Double price;

    public ProductDTO(Product product) {
        this.productName = product.getName();
        this.description = product.getDescription();
        this.storeName=product.getStore().getName();
        this.price=product.getPrice();
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public String getStoreName() {
        return storeName;
    }

    public Double getPrice() {
        return price;
    }
}
