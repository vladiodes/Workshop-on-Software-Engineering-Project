package main.Service.CommandExecutor.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UserTokens {
    private HashMap<String,String> name_Token;
    private String defaultName = "User";
    private AtomicInteger nameCounter;

    public UserTokens() {
        name_Token = new HashMap<>();
        nameCounter = new AtomicInteger(0);
    }

    public void addNameToken(String name, String token){
        name_Token.put(name, token);
    }
    public String addNameToken(String token){
        String output = defaultName + nameCounter.getAndIncrement();
        name_Token.put(output, token);
        return output;
    }

    public String getToken(String name){
        return name_Token.get(name);
    }

    public String getName(String token){
        for(Map.Entry<String,String> ent : name_Token.entrySet())
            if(ent.getValue().equals(token))
                return ent.getKey();
        return null;
    }
}
