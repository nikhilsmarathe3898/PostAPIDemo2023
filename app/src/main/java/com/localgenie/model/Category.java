package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {

@SerializedName("_id")
@Expose
private String id;
/*@SerializedName("city_id")
@Expose
private String cityId;
@SerializedName("city")
@Expose
private String city;
@SerializedName("prepTime")
@Expose
private String prepTime;
@SerializedName("packupTime")
@Expose
private String packupTime;*/
@SerializedName("bookingType")
@Expose
private String bookingType;
/*@SerializedName("bussinessgp")
@Expose
private String bussinessgp;*/
@SerializedName("service_type")
@Expose
private int serviceType;

@SerializedName("billing_model")
@Expose
private int billingModel;
@SerializedName("cat_name")
@Expose
private String catName;
/*@SerializedName("cat_desc")
@Expose
private String catDesc;
@SerializedName("can_fees")
@Expose
private Integer canFees;
@SerializedName("bannerImageWeb")
@Expose
private String bannerImageWeb;*/
@SerializedName("bannerImageApp")
@Expose
private String bannerImageApp;
/*@SerializedName("sel_img")
@Expose
private String selImg;
@SerializedName("unsel_img")
@Expose
private String unselImg;*/
@SerializedName("minimum_fees")
@Expose
private double minimumFees;
@SerializedName("miximum_fees")
@Expose
private double maximumFees;

@SerializedName("subCategory")
@Expose
private ArrayList<SubCategory> subCategory = null;
/*@SerializedName("bussinessGroup")
@Expose
private String bussinessGroup;*/

    private BookingTypeAction bookingTypeAction;
    private CallType callType;


    @SerializedName("price_per_fees")
    @Expose
    private double pricePerHour;

    @SerializedName("visit_fees")
    @Expose
    private double visitFee;
    @SerializedName("recomdedBannerImageApp")
    @Expose
    private String recommendedBannerImageApp;

    private ArrayList<Offers>offers;


    private ArrayList<QuestionList>questionArr;

    private String iconApp;

    private String categoryId;

    public CallType getCallType() {
        return callType;
    }

    public ArrayList<QuestionList> getQuestionArr() {
        return questionArr;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public ArrayList<Offers> getOffers() {
        return offers;
    }

/*
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
*/

    public String getRecommendedBannerImageApp() {
        return recommendedBannerImageApp;
    }

    public void setRecommendedBannerImageApp(String recommendedBannerImageApp) {
        this.recommendedBannerImageApp = recommendedBannerImageApp;
    }

    public String getIconApp() {
        return iconApp;
    }

    public void setIconApp(String iconApp) {
        this.iconApp = iconApp;
    }

    public double getVisitFee() {
        return visitFee;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }



    public BookingTypeAction getBookingTypeAction() {
        return bookingTypeAction;
    }

    public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

/*public String getCityId() {
return cityId;
}


public String getCity() {
return city;
}


public String getPrepTime() {
return prepTime;
}


public String getPackupTime() {
return packupTime;
}*/


public String getBookingType() {
return bookingType;
}


/*public String getBussinessgp() {
return bussinessgp;
}*/


public int getServiceType() {
return serviceType;
}

/*public int getBillingModel() {
return billingModel;
}*/

    public int getBillingModel() {
        return billingModel;
    }

    public String getCatName() {
return catName;
}


/*public String getCatDesc() {
return catDesc;
}


public Integer getCanFees() {
return canFees;
}


public String getBannerImageWeb() {
return bannerImageWeb;
}*/


public String getBannerImageApp() {
return bannerImageApp;
}

/*
public String getSelImg() {
return selImg;
}


public String getUnselImg() {
return unselImg;
}*/


public double getMinimumFees() {
return minimumFees;
}


public double getMaximumFees() {
return maximumFees;
}


public ArrayList<SubCategory> getSubCategory() {
return subCategory;
}


/*
public String getBussinessGroup() {
return bussinessGroup;
}
*/


    public class BookingTypeAction implements Serializable
    {
        /*"now":true,
"schedule":false,
"repeate":false*/

        /*@SerializedName("bussinessGroup")
@Expose*/
        boolean now,schedule,repeat;

        public boolean isNow() {
            return now;
        }

        public boolean isSchedule() {
            return schedule;
        }

        public boolean isRepeat() {
            return repeat;
        }
    }
}