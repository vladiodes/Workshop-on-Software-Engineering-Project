package test.Stress_Load_tests;

import main.Communication.Main;
import main.DTO.ProductDTO;
import main.Service.IService;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.List;

import static test.Stress_Load_tests.Paths.configJmeter;

public class storeManagments {


    StandardJMeterEngine jmeterClient;
    @Before
    public void setUp() throws Exception {
        jmeterClient = configJmeter(Paths.StoreManagementsJMX);
    }
    @Test
    public void execute(){
        Assume.assumeNotNull(jmeterClient);
        String[] args = new String[2];
        args[0] = "StoreManagementStressStartup.json";
        args[1] = "DBTestingConfig.json";
        Main.main(args);
        jmeterClient.run();
        IService service = Main.getService();
        String token = service.guestConnect().getResult();
        service.login(token,"admin","admin");
        int actual = 0;
        int expected_operations = 200; // 100 appointments + 100 product updates
        List<ProductDTO> prods=service.getProductsByInfo("p1", null, null, null, null, null, null).getResult();
        for(ProductDTO prod : prods)
            if (Double.parseDouble(prod.getPrice()) == 420)
                actual ++;
        for(int i = 0; i < 100; i ++) {
            List<String> staff = service.getStoreStaff(token, "s" + i).getResult();
            if(staff.size()>1)
                actual++;
        }
        System.out.println(String.format("actual: %d",actual));
        Assertions.assertTrue((double)actual / expected_operations > 0.95);
    }
}
