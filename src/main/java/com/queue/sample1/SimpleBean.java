package com.queue.sample1;

import java.io.Serializable;

public class SimpleBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5628984365952121148L;
    private String message;
    private int StatusCode;
    
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public int getStatusCode() {
        return StatusCode;
    }
    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

}
