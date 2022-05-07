package main.DTO;

import main.Stores.Product;
import org.mockito.internal.matchers.Null;

public class ProductDTO {
    private String productName;
    private String description;
    private DiscountDTO discount;

    private String storeName;

    private Double price;

    private int quantity;

    public ProductDTO(Product product) {
        this.productName = product.getName();
        this.description = product.getDescription();
        this.storeName=product.getStore().getName();
        this.price=product.getCleanPrice();
        this.quantity=product.getQuantity();
        if(product.getDiscount() != null)
            this.discount = new DiscountDTO(product.getDiscount());
        else this.discount = null ;
    }

    public DiscountDTO getDiscount() {
        return discount;
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

    public int getQuantity() {
        return quantity;
    }
}
