package test.Stress_Load_tests;

import main.Communication.Main;
import main.Service.IService;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static test.Stress_Load_tests.Paths.configJmeter;

public class buyDifferentItems {


    StandardJMeterEngine jmeterClient;
    @Before
    public void setUp() {
        jmeterClient = configJmeter(Paths.buyDifferentItemJMX);
    }


    @Test
    public void execute(){
        Assume.assumeNotNull(jmeterClient);
        String[] args = new String[2];
        args[0] = "StressStartup.json";
        args[1] = "DBTestingConfig.json";
        Main.main(args);
        jmeterClient.run();
        IService service = Main.getService();
        String token = service.guestConnect().getResult();
        service.login(token,"admin","admin");
        int expected = 0;
        for(int i = 0; i < 100; i ++) {
            List<String> purchases = service.getStorePurchaseHistory(token, "s"+i).getResult();
            expected+= purchases.size();
        }
        System.out.println(String.format("Actual: %d", expected));
        Assert.assertTrue(0.95<(double)(expected / 100));   //SLA=95%
    }
}
