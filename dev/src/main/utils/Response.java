package main.utils;

public class Response<T> {
    private T result;
    private boolean error_occured;
    private String error_message;
    private boolean was_expected_error;

    public T getResult() {
        return result;
    }

    public boolean isError_occured() {
        return error_occured;
    }

    public String getError_message() {
        return error_message;
    }

    public boolean isWas_expected_error() {
        return was_expected_error;
    }

    public Response(T _res, String error_message){
        error_occured = false;
        this.result = _res;
        if (error_message != null) {
            this.error_occured = true;
            this.error_message = error_message;
        }
    }

    public Response(T result){
        error_occured=false;
        this.result=result;
    }

    public Response(Exception e,boolean was_expected_error){
        this.error_occured=true;
        this.error_message=e.getMessage();
        this.was_expected_error=was_expected_error;
    }
}
