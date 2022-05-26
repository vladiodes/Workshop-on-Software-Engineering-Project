package main.Logger;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    /**
     * A logger class, implemented as a safe-thread singleton
     * Simply writes events or bugs into a file.
     */
    private static class LoggerHolder {
        private final static Logger instance = new Logger();
    }

    private String logFileName = "LOG.txt";
    private String bugFileName = "BUG.txt";

    private Logger() {

    }

    public static Logger getInstance() {
        return LoggerHolder.instance;
    }

    public synchronized void logEvent(String className, String msg) {
        appendStrToFile(String.format("[EVENT] - %s in class: %s\n",msg,className),logFileName);
    }

    public synchronized void logBug(String className, String msg) {
        appendStrToFile(String.format("[BUG] - %s in class: %s\n",msg,className),bugFileName);
    }

    private void appendStrToFile(String msg,String fileName) {

        try {
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(fileName, true));

            out.write(msg);
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occurred" + e);
        }
    }
}

