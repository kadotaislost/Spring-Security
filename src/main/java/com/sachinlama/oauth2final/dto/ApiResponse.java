package com.sachinlama.oauth2final.dto;

public class ApiResponse<T> {
    private int Status;
    private String message;
    private T data;

    public ApiResponse(int status, String message, T data) {
        Status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
