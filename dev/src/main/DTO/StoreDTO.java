package main.DTO;

import main.Stores.Product;
import main.Stores.Store;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class StoreDTO {
    private HashMap<String, ProductDTO> productsByName;
    private String storeName;
    public StoreDTO(Store st) {
        this.setStoreName(st.getName());
        this.setProductsByName(st.getProductsByName());
    }

    public HashMap<String, ProductDTO> getProductsByName() {
        return productsByName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setProductsByName(ConcurrentHashMap<String, Product> productsByName) {
        this.productsByName = new HashMap<>();
        for (String key : productsByName.keySet())
            this.productsByName.put(key, new ProductDTO(productsByName.get(key)));
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
