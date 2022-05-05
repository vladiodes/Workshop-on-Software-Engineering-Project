package main.utils;

import main.Stores.IStore;
import main.Stores.Product;
import main.Stores.Store;

import java.util.HashMap;
import java.util.Map;

public class Restriction extends HashMap<Product, Integer>{

    private static Restriction getRestriction(HashMap<String, Integer> restr, IStore store){
        Restriction output = new Restriction();
        for(Map.Entry<String , Integer> entry : restr.entrySet())
            output.put(store.getProduct(entry.getKey()), entry.getValue());
        return output;
    }

    public static HashMap<Restriction, Double> getRestrictions(HashMap<HashMap<String, Integer>, Double> restrictions, IStore store){
        HashMap<Restriction, Double> output = new HashMap<>();
        for( Map.Entry<HashMap<String, Integer>, Double> entry: restrictions.entrySet())
            output.put(getRestriction(entry.getKey(), store),entry.getValue());
        return output;
    }
}
