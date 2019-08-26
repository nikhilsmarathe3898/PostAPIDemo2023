package com.localgenie.model.payment_method;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CardGetData implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("last4")
    @Expose
    private String last4;
    @SerializedName("expYear")
    @Expose
    private Integer expYear;
    @SerializedName("expMonth")
    @Expose
    private Integer expMonth;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("funding")
    @Expose
    private String funding;
    @SerializedName("isDefault")
    @Expose
    private Boolean isDefault;

    public String getName() {
        return name;
    }


    public String getLast4() {
        return last4;
    }


    public Integer getExpYear() {
        return expYear;
    }


    public Integer getExpMonth() {
        return expMonth;
    }


    public String getId() {
        return id;
    }


    public String getBrand() {
        return brand;
    }


    public String getFunding() {
        return funding;
    }


    public Boolean getIsDefault() {
        return isDefault;
    }


}

