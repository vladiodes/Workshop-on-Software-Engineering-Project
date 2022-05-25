package main.Service.CommandExecutor.Commands;

import main.Service.CommandExecutor.utils.UserTokens;
import main.utils.Response;

import java.util.List;

public class addProductToStoreCommand extends Command<Boolean>{
    private String name;
    private String prodName;
    private String category;
    private List<String> keyWords;
    private String description;
    private String storeName;
    private int quantity;
    private double price;


    public addProductToStoreCommand(UserTokens ut, String Token, String prodName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        this.name = ut.getName(Token);
        this.prodName = prodName;
        this.category = category;
        this.keyWords = keyWords;
        this.description = description;
        this.storeName = storeName;
        this.quantity = quantity;
        this.price = price;
    }

    public addProductToStoreCommand() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(List<String> keyWords) {
        this.keyWords = keyWords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public Response<Boolean> execute() {
        return this.service.addProductToStore(this.userTokens.getToken(name), prodName, category, keyWords, description, storeName,quantity, price);
    }
}
