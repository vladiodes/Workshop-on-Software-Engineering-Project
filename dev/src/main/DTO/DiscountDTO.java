package main.DTO;

import main.Stores.Discounts.DirectDiscount;
import main.Stores.Discounts.Discount;

public class DiscountDTO {
    String Description;
    public DiscountDTO(Discount directDiscount){
        this.Description = directDiscount.toString();
    }
}
