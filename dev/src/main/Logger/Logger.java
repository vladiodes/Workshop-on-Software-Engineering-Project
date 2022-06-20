package main.Logger;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

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
    private String userTokens = "C:\\Users\\banan\\Desktop\\Studies\\semester F\\sadna\\apache-jmeter-5.4.3\\bin\\TOKENS.csv";

    private Logger() {
        DeleteFile(userTokens);
        DeleteFile(logFileName);
        DeleteFile(bugFileName);
        appendStrToFile(String.format("%s\n","userToken"),userTokens);
    }

    private void DeleteFile(String path){
        File myObj = new File(path);
        if (myObj.delete()) {
            System.out.println("Deleted the file: " + myObj.getName());
        } else {
            System.out.println("Failed to delete the file.");
        }
    }

    public static Logger getInstance() {
        return LoggerHolder.instance;
    }

    public synchronized void logEvent(String className, String msg) {
        appendStrToFile(String.format("[EVENT][%s] - %s in class: %s\n", LocalDateTime.now(),msg,className),logFileName);
    }

    public synchronized void logBug(String className, String msg) {
        appendStrToFile(String.format("[BUG][%s] - %s in class: %s\n", LocalDateTime.now(),msg,className),bugFileName);
    }

    public synchronized void logGuest(String msg) {
        appendStrToFile(String.format("%s\n",msg),userTokens);
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

