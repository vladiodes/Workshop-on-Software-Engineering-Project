package main.utils;

public class Response<T> {
    private T result;
    private boolean error_occured;
    private String error_message;

    public T getResult() {
        return result;
    }

    public boolean isError_occured() {
        return error_occured;
    }

    public String getError_message() {
        return error_message;
    }

    public Response(T _res, String error_message){
        error_occured = false;
        this.result = _res;
        if (error_message != null) {
            this.error_occured = true;
            this.error_message = error_message;
        }
    }
}
