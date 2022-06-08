package main.ExternalServices;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestController {
    private URL url;
    private HttpURLConnection connection;


    public HttpRequestController() throws Exception
    {
        url = new URL("https://cs-bgu-wsep.herokuapp.com/");
        configureConnection();
    }

    public HttpRequestController(boolean flag)
    {

    }

    private void configureConnection() throws Exception
    {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
    }

    private byte[] configureRequest(Map<String, String> params) throws Exception
    {
        // Instantiate a requestData object to store our data
        StringBuilder requestData = new StringBuilder();

        for (Map.Entry<String, String> param : params.entrySet()) {
            if (requestData.length() != 0) {
                requestData.append('&');
            }
            // Encode the parameter based on the parameter map we've defined
            // and append the values from the map to form a single parameter
            requestData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            requestData.append('=');
            requestData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }

        //Set timeouts - 10 seconds
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);


        // Convert the requestData into bytes
        byte[] requestDataBytes = requestData.toString().getBytes("UTF-8");
        return requestDataBytes;
    }

    public String sendRequest(Map<String, String> requestParams) throws Exception
    {
        byte[] requestDataBytes = configureRequest(requestParams);
        connection.setDoOutput(true);
        try(DataOutputStream writer = new DataOutputStream(connection.getOutputStream()))
        {
            //Send Request
            writer.write(requestDataBytes);
            //Read Response
            StringBuilder content;
            try(BufferedReader in =  new BufferedReader(new InputStreamReader(connection.getInputStream())))
            {
                String line;
                content = new StringBuilder();
                while((line = in.readLine()) != null)
                {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            String beforeRemove = content.toString();
            //Remove \n and \r from string
            String afterRemove = beforeRemove.replaceAll("\n", "");
            afterRemove = afterRemove.replaceAll("\r", "");
           return afterRemove;
        }
        finally
        {
            connection.disconnect();
        }
    }

    public boolean handshake() throws Exception
    {
        Map<String,String> params = new HashMap<>();
        params.put("action_type", "handshake");
        String response = sendRequest(params);
        return response.equals("OK");
    }
}
