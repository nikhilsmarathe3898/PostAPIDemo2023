package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>ServiceResponse</h>
 * Created by Ali on 2/7/2018.
 */

public class ServiceResponse implements Serializable
{
    private String message;
    private ArrayList<ServiceDataResponse>data;

    public String getMessage() {
        return message;
    }

    public ArrayList<ServiceDataResponse> getData() {
        return data;
    }

    public class ServiceDataResponse implements Serializable
    {

        /*"sub_cat_id":"",
"sub_cat_name":"",
"sub_cat_desc":"",
"service":[]*/
        String sub_cat_id,sub_cat_name,sub_cat_desc;
        ArrayList<ServiceData>service;

        public String getSub_cat_id() {
            return sub_cat_id;
        }

        public String getSub_cat_name() {
            return sub_cat_name;
        }

        public String getSub_cat_desc() {
            return sub_cat_desc;
        }

        public ArrayList<ServiceData> getService() {
            return service;
        }
    }


    public class ServiceData implements Serializable
    {
        /*"_id":"5a40fc0761596a7b7c1de952",
"city_id":"5a3f84a361596a7b787cde93",
"currency":"USD",
"currencySymbol":"$",
"cat_id":"5a40f4d361596a7b787cde94",
"sub_cat_id":"5a40fb4661596a7b787cde95",
"ser_name":"15",
"ser_desc":"Service for Fifteen mins.",
"unit":"MINS",
"is_unit":50,
"bannerImageApp":"http://138.197.78.50/iserve-v2/pics/def.png",
"bannerImageWeb":"http://138.197.78.50/iserve-v2/pics/def.png",
"quantity":"1",
"plusOneCost":"0",
"additionalPrice":0,
"maxquantity":10,
"minFees":10,
"maxFees":300*/
        private String _id,currency,currencySymbol,cat_id,sub_cat_id,ser_name,ser_desc,unit,bannerImageApp;
        private double is_unit,plusOneCost,additionalPrice,minFees,maxFees;
        private int  quantity,maxquantity,tempQuant;

         /*  private ArrayList<CartItem>item; //when get item called

        public ArrayList<CartItem> getItem() {
            return item;
        }*/

        public int getTempQuant() {
            return tempQuant;
        }

        public void setTempQuant(int tempQuant) {
            this.tempQuant = tempQuant;
        }

        public String get_id() {
            return _id;
        }

        public String getCurrency() {
            return currency;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public String getCat_id() {
            return cat_id;
        }

        public String getSub_cat_id() {
            return sub_cat_id;
        }

        public String getSer_name() {
            return ser_name;
        }

        public String getSer_desc() {
            return ser_desc;
        }

        public String getUnit() {
            return unit;
        }

        public String getBannerImageApp() {
            return bannerImageApp;
        }

        public double getIs_unit() {
            return is_unit;
        }

        public double getPlusOneCost() {
            return plusOneCost;
        }

        public double getAdditionalPrice() {
            return additionalPrice;
        }

        public double getMinFees() {
            return minFees;
        }

        public double getMaxFees() {
            return maxFees;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getMaxquantity() {
            return maxquantity;
        }
    }
}
