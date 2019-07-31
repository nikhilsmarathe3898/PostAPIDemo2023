package com.pojo;

import java.io.Serializable;

/**
 * <h>FilteredResponse</h>
 *
 * Created by Ali on 2/1/2018.
 */

public class FilteredResponse implements Serializable
{
    private double lat,lng,selectedPriceMin,selectedPriceMax;
    private String subCatId,generalName;
    private int type;


    public FilteredResponse(double lat, double lng, String subCatId, String generalName, int type,double
            selectedPriceMin,double selectedPriceMax) {
        this.lat = lat;
        this.lng = lng;
        this.subCatId = subCatId;
        this.generalName = generalName;
        this.type = type;
        this.selectedPriceMin = selectedPriceMin;
        this.selectedPriceMax  = selectedPriceMax;
    }


    public double getSelectedPriceMin() {
        return selectedPriceMin;
    }

    public void setSelectedPriceMin(double selectedPriceMin) {
        this.selectedPriceMin = selectedPriceMin;
    }

    public double getSelectedPriceMax() {
        return selectedPriceMax;
    }

    public void setSelectedPriceMax(double selectedPriceMax) {
        this.selectedPriceMax = selectedPriceMax;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }



    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

    public String getGeneralName() {
        return generalName;
    }

    public void setGeneralName(String generalName) {
        this.generalName = generalName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
