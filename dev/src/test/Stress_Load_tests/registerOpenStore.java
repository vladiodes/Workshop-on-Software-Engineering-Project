package test.Stress_Load_tests;

import main.Communication.Main;
import main.Service.IService;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static test.Stress_Load_tests.Paths.configJmeter;

public class registerOpenStore {


    StandardJMeterEngine jmeterClient;
    @Before
    public void setUp() throws Exception {
        jmeterClient = configJmeter(Paths.registerAndOpenStoreJMX);
    }
    @Test
    public void execute(){
        Assume.assumeNotNull(jmeterClient);
        String[] args = new String[2];
        args[0] = "NONE";
        args[1] = "DBTestingConfig.json";
        Main.main(args);
        jmeterClient.run();
        IService service = Main.getService();
        String token = service.guestConnect().getResult();
        service.login(token,"admin","admin");
        int usersRegistered = Integer.parseInt(service.getNumberOfRegisteredUsersPerDate(token, LocalDate.now()).getResult());
        System.out.println(String.format("Actual is %d", usersRegistered));
        Assert.assertTrue(0.95<(double)usersRegistered / 100);   //SLA=95%
    }
}
