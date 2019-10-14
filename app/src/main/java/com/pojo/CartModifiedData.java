package com.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>CartModifiedData</h>
 * Created by Ali on 2/9/2018.
 */

public class CartModifiedData implements Serializable
{

    /*"message":"sucess",
"data":{}*/
    String message;
    private DataSelected data;

    public String getMessage() {
        return message;
    }

    public DataSelected getData() {
        return data;
    }

    public class DataSelected implements Serializable
    {
        /*"_id":"5a7d715718967605c327aab4",
"userId":"5a70292918967605c327a8f0",
"customerName":"akbar attar",
"providerId":"",
"createTimeStamp":1518170455,
"expiryTimeStamp":1518170655,
"createTs":"6520492453978439681",
"createDate":"2018-02-09T10:00:55.285Z",
"bookingType":1,
"userType":1,
"createdBy":"Customer",
"status":1,
"categoryId":"5a40f4d361596a7b787cde94",
"categoryName":"Artist",
"totalAmount":51,
"totalQuntity":1,
"cityId":"5a3f84a361596a7b787cde93",
"cityName":"Bengaluru",
"currencySymbol":"$",
"currency":"USD",
"serviceType":"2",
"item":[]*/
        @SerializedName("_id")
        @Expose

        private String CatId;
        private String categoryName,currencySymbol; //categoryId
        private double totalAmount;
        private int totalQuntity,serviceType;

        private ArrayList<ItemSelected>item;

        public String getCatId() {
            return CatId;
        }

        public int getServiceType()
        {
            return serviceType;
        }

/*
        public String getCategoryId() {
            return categoryId;
        }
*/

        public String getCategoryName() {
            return categoryName;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public int getTotalQuntity() {
            return totalQuntity;
        }

        public ArrayList<ItemSelected> getItem() {
            return item;
        }
    }

    public class ItemSelected implements Serializable
    {
        /*"serviceId":"5a4ddb6b61596a6dc0727fd5",
"serviceName":"30",
"unit":51,
"unitPrice":51,
"plusOneCost":"00",
"additionalPrice":0,
"maxquantity":0,
"quntity":1,
"amount":51,
"addedToCartOn":1518170455,
"status":1,
"action":[]*/
       private String serviceId,serviceName,quantityAction;//unit
       private double unitPrice,amount;
       private int maxquantity,quntity;
       private int tempQuantity;

        public String getQuantityAction() {
            return quantityAction;
        }

        public int getTempQuantity() {
            return tempQuantity;
        }

        public void setTempQuantity(int tempQuantity) {
            this.tempQuantity = tempQuantity;
        }

        private ArrayList<ActionSelected>action;

        public String getServiceId() {
            return serviceId;
        }

        public String getServiceName() {
            return serviceName;
        }

/*
        public String getUnit() {
            return unit;
        }
*/

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getAmount() {
            return amount;
        }

        public int getMaxquantity() {
            return maxquantity;
        }

        public int getQuntity() {
            return quntity;
        }

        public ArrayList<ActionSelected> getAction() {
            return action;
        }

        public class ActionSelected implements Serializable
        {
            /*"type":"added",
"actionBy":"Customer",
"timeStamp":1518170455*/
            String type,actionBy;

            public String getType() {
                return type;
            }
        }
    }
}
