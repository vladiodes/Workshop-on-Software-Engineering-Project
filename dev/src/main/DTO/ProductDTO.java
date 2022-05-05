package main.DTO;

import main.Stores.Product;
import org.mockito.internal.matchers.Null;

public class ProductDTO {
    private String productName;
    private String description;
    private DiscountDTO discount;

    public ProductDTO(Product product) {
        this.productName = product.getName();
        this.description = product.getDescription();
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
}
