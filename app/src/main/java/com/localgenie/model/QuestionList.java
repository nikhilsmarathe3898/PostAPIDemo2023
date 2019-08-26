package com.localgenie.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ali on 6/8/2018.
 */
public class QuestionList implements Serializable
{
        /*"_id":"5b0d6da561596a562e63ae8c",
"question":"Gender",
"questionType":5,
"isManadatory":0,
"Description":"gender",*/
        private String _id,question,Description;
        private int questionType,isManadatory,num;
        private ArrayList<PreDefinedQuestions>preDefined;

    public String get_id() {
        return _id;
    }

    public String getQuestion() {
        return question;
    }

    public String getDescription() {
        return Description;
    }

    public int getQuestionType() {
        return questionType;
    }

    public int getIsManadatory() {
        return isManadatory;
    }

    public int getNum() {
        return num;
    }

    public ArrayList<PreDefinedQuestions> getPreDefined() {
        return preDefined;
    }

    public void setPreDefined(ArrayList<PreDefinedQuestions> preDefined) {
        this.preDefined = preDefined;
    }
}
