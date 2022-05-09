package main.DTO;

import main.utils.Bid;

public class BidDTO {
    String biddingUser;
    Double costumePrice;
    String productName;

    public BidDTO (Bid bid){
        biddingUser = bid.getUser().getUserName();
        costumePrice = bid.getCostumePrice();
        productName = bid.getProduct().getName();
    }

    public String getBiddingUser() {
        return biddingUser;
    }

    public Double getCostumePrice() {
        return costumePrice;
    }

    public String getProductName(){
        return productName;
    }
}
