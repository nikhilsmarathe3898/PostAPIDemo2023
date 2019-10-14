package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubCategory implements Serializable{

    @SerializedName("_id")
    @Expose
    private String id;
    /*@SerializedName("city_id")
    @Expose
    private String cityId;*/
    @SerializedName("cat_id")
    @Expose
    private String catId;
    @SerializedName("sub_cat_name")
    @Expose
    private String subCatName;

    @SerializedName("sub_cat_desc")
    @Expose
    private String subCatDesc;
    @SerializedName("App_image_url")
    @Expose
    private String appImageUrl;
    /*
    @SerializedName("Website_banner_image_url")
    @Expose
    private String websiteBannerImageUrl;
*/
    @SerializedName("UnselBannerImageApp")
    @Expose
    private String UnSelAppImageUrl;

    public String getUnSelAppImageUrl() {
        return UnSelAppImageUrl;
    }

    private boolean isCategorySelected;

    public boolean isCategorySelected() {
        return isCategorySelected;
    }

    public void setCategorySelected(boolean categorySelected) {
        isCategorySelected = categorySelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

   /* public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
*/
    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getSubCatName() {
        return subCatName;
    }

/*    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public String getSubCatDesc() {
        return subCatDesc;
    }

    public void setSubCatDesc(String subCatDesc) {
        this.subCatDesc = subCatDesc;
    }*/

    public String getAppImageUrl() {
        return appImageUrl;
    }

   /* public void setAppImageUrl(String appImageUrl) {
        this.appImageUrl = appImageUrl;
    }

    public String getWebsiteBannerImageUrl() {
        return websiteBannerImageUrl;
    }

    public void setWebsiteBannerImageUrl(String websiteBannerImageUrl) {
        this.websiteBannerImageUrl = websiteBannerImageUrl;
    }*/

}