package test.Stress_Load_tests;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.io.IOException;

public class Paths {
    /***
     * @param pathToJemeter SET IT TO THE FOLDER WHERE YOU DOWNLOADED JMETER. OTHERWISE, TESTS WILL SKIP.
     *                      download from https://jmeter.apache.org/download_jmeter.cgi
     */
    static String pathToJmeter = "C:\\Users\\banan\\Desktop\\Studies\\semester F\\sadna\\apache-jmeter-5.4.3";
    static String buySameItemJMX = "src\\test\\Stress_Load_tests\\buySameItem.jmx";
    static String buyDifferentItemJMX = "src\\test\\Stress_Load_tests\\BuyDifferentItems.jmx";
    static String registerAndOpenStoreJMX = "src\\test\\Stress_Load_tests\\register many users and stores.jmx";
    static String StoreManagementsJMX = "src\\test\\Stress_Load_tests\\store managements.jmx";
    static String pathToProperties = "src\\test\\Stress_Load_tests\\jmeter.properties";

    public static StandardJMeterEngine configJmeter(String jmx_path) {
        try {
            StandardJMeterEngine jmeterClient = new StandardJMeterEngine();
            // Initialize Properties, logging, locale, etc.
            JMeterUtils.loadJMeterProperties(Paths.pathToProperties);
            JMeterUtils.setJMeterHome(Paths.pathToJmeter);
            JMeterUtils.initLocale();

            // Initialize JMeter SaveService
            SaveService.loadProperties();

            // Load existing .jmx Test Plan
            File in = new File(jmx_path);
            HashTree testPlanTree = SaveService.loadTree(in);
            jmeterClient.configure(testPlanTree);
            return jmeterClient;
        }
        catch (Exception e){
            return null;
        }
    }
}
