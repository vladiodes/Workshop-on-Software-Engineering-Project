package main.Users;


import java.util.HashMap;

public enum StorePermission {
    UpdateAddProducts("Update and add products"),
    ViewStoreHistory("View store history"),
    AnswerAndTakeRequests("Answer and take requests"),
    PolicyPermission("Permision to add policy"),
    BargainPermission("Permission to bargain with buyers"),
    OwnerPermission("Owner permissions");//can do anything


    private String str;
    private static HashMap<StorePermission,String> toString_map = createMapping();

    private static HashMap<StorePermission, String> createMapping() {
        HashMap<StorePermission,String> map=new HashMap<>();
        map.put(UpdateAddProducts, UpdateAddProducts.str);
        map.put(ViewStoreHistory, ViewStoreHistory.str);
        map.put(AnswerAndTakeRequests, AnswerAndTakeRequests.str);
        map.put(OwnerPermission, OwnerPermission.str);
        return map;
    }

    StorePermission(String str){
        this.str=str;
    }

    public static String stringOf(StorePermission permission){
        return toString_map.get(permission);
    }
}
