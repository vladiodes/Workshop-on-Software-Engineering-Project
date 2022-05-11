package main.DTO;

import main.Stores.SingleProductDiscounts.Discount;

public class DiscountDTO {
    String Description;
    public DiscountDTO(Discount directDiscount){
        this.Description = directDiscount.toString();
    }
}
