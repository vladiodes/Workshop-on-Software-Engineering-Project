package test.Stress_Load_tests;

import main.Communication.Main;
import main.Service.IService;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.apache.jmeter.engine.StandardJMeterEngine;

import java.util.List;

import static test.Stress_Load_tests.Paths.configJmeter;

public class buySameItem {


    StandardJMeterEngine jmeterClient;
    @Before
    public void setUp() throws Exception {
        jmeterClient = configJmeter(Paths.buySameItemJMX);
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
        List<String> purchases = service.getStorePurchaseHistory(token, "s1").getResult();
        System.out.println(String.format("Actual: %d", purchases.size()));
        Assert.assertTrue(0.95<(double)(purchases.size() / 150));   //SLA=95%
    }
}
