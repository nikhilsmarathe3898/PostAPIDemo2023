package com.localgenie.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>CityData</h>
 * Created by Ali on 3/15/2018.
 */

public class CityData  implements Serializable
{
    /*"_id":"5a3f84a361596a7b787cde93",
"ci_csrf_token":"",
"country":"India",
"city":"Bengaluru",
"currencySymbol":"₹",
"currency":"USD",
"isDeleted":false,*/
    //@SerializedName("_id")
    private String _id,country,city,currencySymbol,currency,providerPushTopic,customerPushTopic
            ,stripeKeys,appVersion;
    private int distanceMatrix,currencyAbbr;
    private PolyGone polygons;
    private PaymentMode paymentMode;
    private CustomerFrequency customerFrequency;
    private PushTopics pushTopics;;
    private boolean mandatory;
    private ArrayList<String> custGoogleMapKeys;


    public ArrayList<String> getCustGoogleMapKeys() {
        return custGoogleMapKeys;
    }

    public String getStripeKeys() {
        return stripeKeys;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public CustomerFrequency getCustomerFrequency() {
        return customerFrequency;
    }

    public PushTopics getPushTopics() {
        return pushTopics;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public int getCurrencyAbbr() {
        return currencyAbbr;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public String get_id() {
        return _id;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getCurrency() {
        return currency;
    }

    public String getProviderPushTopic() {
        return providerPushTopic;
    }

    public String getCustomerPushTopic() {
        return customerPushTopic;
    }

    public int getDistanceMatrix() {
        return distanceMatrix;
    }

    public PolyGone getPolygons() {
        return polygons;
    }

    public class PolyGone implements Serializable
    {
       // ArrayList<PolyGoneCordinate>coordinates;
       // ArrayList<ArrayList<LatLng>> coordinates; //= new ArrayList<ArrayList<LatLng>>();


        private double[][][] coordinates;

        public double[][][] getCoordinates ()
        {
            return coordinates;
        }

       /* public ArrayList<PolyGoneCordinate> getCoordinates() {
            return coordinates;
        }*/

        public class PolyGoneCordinate {
        }
    }

    public class PaymentMode implements Serializable
    {
        /*"cash":true,
"card":true,
"wallet":true,*/
       private boolean cash,card,wallet;

        public boolean isCash() {
            return cash;
        }

        public boolean isCard() {
            return card;
        }

        public boolean isWallet() {
            return wallet;
        }
    }

    public class CustomerFrequency implements Serializable
    {
       private int customerHomePageInterval;

        public int getCustomerHomePageInterval() {
            return customerHomePageInterval;
        }
    }

    public class PushTopics implements Serializable
    {
        private String city,allCustomers,allCitiesCustomers,outZoneCustomers;

        public String getCity() {
            return city;
        }

        public String getAllCustomers() {
            return allCustomers;
        }

        public String getAllCitiesCustomers() {
            return allCitiesCustomers;
        }

        public String getOutZoneCustomers() {
            return outZoneCustomers;
        }
    }
}
