package test.RobustnessTests;

import main.ExternalServices.HttpRequestController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;


public class HttpRequestControllerTest {

    private HttpRequestController ctrl;
    private Map<String, String> payParams;
    private Map<String, String> supplyParams;
    private Map<String, String> badAPIParams;
    private Map<String, String> badActionTypeParams;
    private Map<String, String> badParamsGoodActionType;
    private Map<String, String> nullParams;
    private String goodURL = "https://cs-bgu-wsep.herokuapp.com/";
    private String badURL = "https://lksdf.ilsdf/";
    @BeforeEach
    void setUp() {

        try
        {
            ctrl = new HttpRequestController(goodURL);
        }
        catch (Exception e)
        {

        }

        //Prepare pay request
        payParams = new HashMap<>();
        payParams.put("action_type", "pay");
        payParams.put("card_number", "1111222233334444" );
        payParams.put("month", "4");
        payParams.put("year", "2023");
        payParams.put("holder", "Oded Gal");
        payParams.put("ccv", "555");
        payParams.put("id", "123123123");

        //Prepare supply request
        supplyParams = new HashMap<>();
        supplyParams.put("action_type", "supply");
        supplyParams.put("name", "Oded Gal");
        supplyParams.put("address", "Cool Address");
        supplyParams.put("city", "Haifa");
        supplyParams.put("country", "Israel");
        supplyParams.put("zip", "123123");

        //Prepare bad API request
        badAPIParams = new HashMap<>();
        badAPIParams.put("", "");

        //Prepare bad action type request
        badActionTypeParams = new HashMap<>();
        badActionTypeParams.put("action_type", "action_typo");

        //Prepare bad params but good action type request
        badParamsGoodActionType = new HashMap<>();
        badParamsGoodActionType.put("action_type", "pay");
        badParamsGoodActionType.put("i_am_bad", "me_too");

        //Prepare null params
        nullParams = new HashMap<>();
        nullParams.put(null, null);
    }

    @Test
    void goodHandshakeTest()
    {
        Assertions.assertDoesNotThrow(()->ctrl.handshake());
    }
    @Test
    void badHandshakeTest()
    {
        Assertions.assertDoesNotThrow(()->ctrl = new HttpRequestController(badURL));
        Assertions.assertFalse(ctrl.handshake());
    }

    @Test
    void goodPayRequestAndCancelTest()
    {
        //Payment
        String response =  ctrl.sendRequest(payParams);
        if(response == null)
            Assertions.fail();
        Assertions.assertDoesNotThrow(()-> Integer.parseInt(response));
        int id = Integer.parseInt(response);
        Assertions.assertTrue(id>=10000 && id <=100000);

        //Cancel payment
        Map<String,String> cancelPayParams = new HashMap<>();
        cancelPayParams.put("action_type", "cancel_pay");
        cancelPayParams.put("transaction_id", String.valueOf(id));
        try
        {
            ctrl = new HttpRequestController(goodURL);
        }
        catch (Exception e)
        {
            Assertions.fail();
        }
        String cancelResponse = ctrl.sendRequest(cancelPayParams);
        if(cancelResponse == null)
            Assertions.fail();
        int cancelRes = Integer.parseInt(cancelResponse);
        Assertions.assertEquals(1, cancelRes);
    }


    @Test
    void goodSupplyRequestAndCancelTest()
    {
        //Supply
        String response =  ctrl.sendRequest(supplyParams);
        if(response == null)
            Assertions.fail();
        Assertions.assertDoesNotThrow(()-> Integer.parseInt(response));
        int id = Integer.parseInt(response);
        Assertions.assertTrue(id>=10000 && id <=100000);

        //Cancel supply
        Map<String,String> cancelSupplyParams = new HashMap<>();
        cancelSupplyParams.put("action_type", "cancel_pay");
        cancelSupplyParams.put("transaction_id", String.valueOf(id));
        try
        {
            ctrl = new HttpRequestController(goodURL);
        }
        catch (Exception e)
        {
            Assertions.fail();
        }
        String cancelResponse = ctrl.sendRequest(cancelSupplyParams);
        if(cancelResponse == null)
            Assertions.fail();
        int cancelRes = Integer.parseInt(cancelResponse);
        Assertions.assertEquals(1, cancelRes);
    }

    @Test
    void badAPIRequestTest()
    {
        String response = ctrl.sendRequest(badAPIParams);
        Assertions.assertNull(response);
    }

    @Test
    void badActionTypeRequestTest()
    {
        String response = ctrl.sendRequest(badActionTypeParams);
        Assertions.assertNull(response);
    }

    @Test
    void badParamsGoodActionTypeTest()
    {
        String response = ctrl.sendRequest(badParamsGoodActionType);
        Assertions.assertEquals(response, "-1");
    }

    @Test
    void nullParamsTest()
    {
        String response = ctrl.sendRequest(nullParams);
        Assertions.assertNull(response);
    }







}
