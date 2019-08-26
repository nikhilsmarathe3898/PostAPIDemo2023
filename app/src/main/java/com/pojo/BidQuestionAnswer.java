package com.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ali on 7/9/2018.
 */
public class BidQuestionAnswer implements Serializable
{
    /*"answer":"Fresh Painting (For new houses without any paint on the walls)",
"name":"What do you want to get painted?",
"_id":"5b1f8d9261596a49207f0cfe"*/
    private String answer,_id;
    private int questionType;
    @SerializedName("name")
    @Expose
    private String question;

    public int getQuestionType() {
        return questionType;
    }

    public String getAnswer() {
        return answer;
    }

    public String get_id() {
        return _id;
    }

    public String getQuestion() {
        return question;
    }
}
