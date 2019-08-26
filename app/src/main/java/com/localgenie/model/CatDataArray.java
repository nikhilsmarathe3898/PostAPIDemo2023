package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CatDataArray implements Serializable {


    /*"groupId":"5a65ca0d61596a053775ab02",
"groupName":"Home Services",
"num":2,
"selectImage":"https://s3.amazonaws.com/localserviceprocustomer/6826542321060.png",
"description":"Cleaner, Plumber, Electrician, Interior Designer, Pest Control, Carpenter, Packers & Movers, Painter",*/

    @SerializedName("groupId")
    @Expose
    private String groupId;

    @SerializedName("groupName")
    @Expose
    private String groupName;

    @SerializedName("selectImage")
    @Expose
    private String selectImage;

    @SerializedName("description")
    @Expose
    private String description;

    private boolean expanded;

    @SerializedName("category")
    @Expose
    private ArrayList<Category> category = null;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSelectImage() {
        return selectImage;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Category> getCategory() {
        return category;
    }
}