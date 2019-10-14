package com.localgenie.model;

import java.io.Serializable;


/**
 * Created by embed on 29/7/15.
 */
public class FacebookLoginPojo implements Serializable {
    private String id;
    private String email;
    private String first_name;
    private String last_name;
    private String name;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

/*
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
*/

    public String getLast_name() {
        return last_name;
    }

/*
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
*/



}
