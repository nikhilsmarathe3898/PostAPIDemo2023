package com.pojo;

import java.io.Serializable;

/**
 * Created by Ali on 5/30/2018.
 */
public class AdditionalService implements Serializable
{
    /*"serviceName":"pipe fitting",
"price":"250.00"*/
    private String serviceName;
    private double price;

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }
}
