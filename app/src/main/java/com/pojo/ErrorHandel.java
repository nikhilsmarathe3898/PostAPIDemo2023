package com.pojo;

import java.io.Serializable;

/**
 * Created by Ali on 2/7/2018.
 */

public class ErrorHandel implements Serializable {
    private String message, data;

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}
