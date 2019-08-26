package com.localgenie.model;

/**
 * Created by Pramod on 14/12/17.
 */

public class ValidationReq {
    private String email;

    public ValidationReq(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
