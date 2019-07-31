package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>CartInfo</h>
 * Created by Ali on 3/13/2018.
 */

public class CartInfo implements Serializable
{
    private ArrayList<CheckOutItem>checkOutItem;

    private String currencySymbol;
    private String categoryName,categoryId;
    private double totalAmount;
    private int callType;

    private ArrayList<CheckOutItem> item;

    public int getCallType() {
        return callType;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public ArrayList<CheckOutItem> getCheckOutItem() {
        return checkOutItem;
    }

    public ArrayList<CheckOutItem> getItem() {
        return item;
    }

    public class CheckOutItem implements Serializable
    {
        /*"serviceId":"5a716a0461596a014a04e79c",
"serviceName":"Loan Document",
"unit":175,
"unitPrice":175,
"plusOneCost":"1",
"additionalPrice":75,
"maxquantity":3,
"quntity":1,
"amount":175*/

        private String serviceId,serviceName;
        private int quntity;
        private double amount;

        public String getServiceId() {
            return serviceId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public int getQuntity() {
            return quntity;
        }

        public double getAmount() {
            return amount;
        }
    }
}
