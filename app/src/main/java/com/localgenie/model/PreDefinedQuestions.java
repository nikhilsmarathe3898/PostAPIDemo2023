package com.localgenie.model;

import java.io.Serializable;

/**
 * Created by Ali on 6/8/2018.
 */
public class PreDefinedQuestions implements Serializable
{
   /* "_id":"5b0d6db361596a224660c6c1",
            "name":"Male"*/


   private String _id,name;

    public PreDefinedQuestions(String _id, String name) {
        this._id = _id;
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }
}
