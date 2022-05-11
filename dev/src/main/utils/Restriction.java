package main.utils;

import main.Stores.IStore;
import main.Stores.Product;

import java.util.HashMap;
import java.util.Map;

public class Restriction extends HashMap<Product, Integer>{

    public static Restriction getRestriction(HashMap<String, Integer> restr, IStore store){
        Restriction output = new Restriction();
        for(Map.Entry<String , Integer> entry : restr.entrySet())
            output.put(store.getProduct(entry.getKey()), entry.getValue());
        return output;
    }

}
