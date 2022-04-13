package main.Logger;

import jdk.jshell.spi.ExecutionControl;

public class Logger {
    /**
     * A logger class, implemented as a safe-thread singleton
     * Simply writes events or bugs into a file.
     */
    private static class LoggerHolder{
        private final static Logger instance = new Logger();
    }

    private String logFileName;
    private Logger(){

    }
    public static Logger getInstance(){
        return LoggerHolder.instance;
    }

    public void logEvent(String className,String msg){

    }

    public void logBug(String className,String msg){

    }
}
