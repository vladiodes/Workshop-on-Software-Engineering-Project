package main.DTO;

import main.Stores.Product;
import main.Users.User;
import org.mockito.internal.matchers.Null;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ProductDTO {
    @Id
    @GeneratedValue
    private int id;
    private String productName;
    private String description;

    private String storeName;

    private String price;

    private int quantity;

    public ProductDTO(Product product) {
        this.productName = product.getName();
        this.description = product.getDescription();
        this.storeName=product.getStore().getName();
        this.price=product.getCleanPrice() + "";
        this.quantity=product.getQuantity();
    }

    public ProductDTO(Product product, User u) {
        this.productName = product.getName();
        this.description = product.getDescription();
        this.storeName=product.getStore().getName();
        try {
            this.price = product.getCurrentPrice(u) + "";
        }
        catch (Exception e) {
            this.price = e.getMessage();
        }
        this.quantity=product.getQuantity();
    }

    public ProductDTO() {

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

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
