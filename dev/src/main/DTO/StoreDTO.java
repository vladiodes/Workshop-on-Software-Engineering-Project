package main.DTO;

import main.Stores.IStore;
import main.Stores.Product;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class StoreDTO {
    private HashMap<String, ProductDTO> productsByName;
    private String storeName;
    private Boolean isActive;

    public StoreDTO(IStore st) {
        this.setStoreName(st.getName());
        this.setProductsByName(st.getProductsByName());
        this.setIsActive(st.getIsActive());
    }


    public String getStoreName() {
        return storeName;
    }

    public HashMap<String, ProductDTO> getProductsByName() {
        return productsByName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setProductsByName(ConcurrentHashMap<String, Product> productsByName) {
        this.productsByName = new HashMap<>();
        for (String key : productsByName.keySet())
            this.productsByName.put(key, new ProductDTO(productsByName.get(key)));
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
