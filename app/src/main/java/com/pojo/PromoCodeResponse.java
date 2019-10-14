package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ali on 5/18/2018.
 */
public class PromoCodeResponse implements Serializable
{
    String message;
    ArrayList<PromoCodeData>data;

    public String getMessage() {
        return message;
    }

    public ArrayList<PromoCodeData> getData() {
        return data;
    }

    public class PromoCodeData implements Serializable
    {
/*"code":"LSPDIS",
"minimumPurchaseValue":50,
"discount":{},
"startTime":"2018-05-18T06:30:00.000Z",
"endTime":"2018-05-31T06:30:00.000Z",
"termsAndConditions":"<p> </p>\n\n<ul>\n\t<li>Offer valid for a single restaurant bill value of Rs.99 and above only</li>\n\t<li>Offer valid on payments made via Standard Chartered Debit and Credit Cards</li>\n\t<li>Offer can be availed multiple times during the campaign duration</li>\n\t<li>Offer can not be clubbed with any other offer</li>\n\t<li>Offer not applicable on Domino's Pizza restaurants</li>\n\t<li>Offer valid till Jun 10, 2018 23:59 PM</li>\n</ul>\n",
"description":"<p>Get Huge Discount</p>\n",
"howItWorks":"<p>Get free delivery on all orders placed on Swiggy using Standard Chartered Debit or Credit cards (TCA*)</p>\n",
"ab":true*/
private String code,termsAndConditions,description,howItWorks;
/*private double minimumPurchaseValue;
private DiscountAmount discount;*/

        public String getCode() {
            return code;
        }

        public String getTermsAndConditions() {
            return termsAndConditions;
        }

        public String getDescription() {
            return description;
        }

        public String getHowItWorks() {
            return howItWorks;
        }

      /*  public double getMinimumPurchaseValue() {
            return minimumPurchaseValue;
        }

        public DiscountAmount getDiscount() {
            return discount;
        }*/
    }

/*
    private class DiscountAmount implements Serializable
    {
        */
/*"typeId":2,
"typeName":"percentage",
"value":30,
"maximumDiscountValue":60*//*

        private int typeId;
        private String typeName;
        private double value,maximumDiscountValue;

        public int getTypeId() {
            return typeId;
        }

        public String getTypeName() {
            return typeName;
        }

        public double getValue() {
            return value;
        }

        public double getMaximumDiscountValue() {
            return maximumDiscountValue;
        }
    }
*/
}
