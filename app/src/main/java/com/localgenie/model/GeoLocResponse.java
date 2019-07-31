package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Pramod
 * @since 15/01/2018.
 */

public class GeoLocResponse {

    @SerializedName("geoplugin_request")
    @Expose
    private String geoPluginRequest;
    @SerializedName("geoplugin_status")
    @Expose
    private Integer geopluginStatus;
    @SerializedName("geoplugin_credit")
    @Expose
    private String geopluginCredit;
    @SerializedName("geoplugin_city")
    @Expose
    private String geopluginCity;
    @SerializedName("geoplugin_region")
    @Expose
    private String geopluginRegion;
    @SerializedName("geoplugin_areaCode")
    @Expose
    private String geopluginAreaCode;
    @SerializedName("geoplugin_dmaCode")
    @Expose
    private String geopluginDmaCode;
    @SerializedName("geoplugin_countryCode")
    @Expose
    private String geopluginCountryCode;
    @SerializedName("geoplugin_countryName")
    @Expose
    private String geopluginCountryName;
    @SerializedName("geoplugin_continentCode")
    @Expose
    private String geopluginContinentCode;
    @SerializedName("geoplugin_latitude")
    @Expose
    private double geoPluginLatitude;
    @SerializedName("geoplugin_longitude")
    @Expose
    private double geoPluginLongitude;
    @SerializedName("geoplugin_regionCode")
    @Expose
    private String geopluginRegionCode;
    @SerializedName("geoplugin_regionName")
    @Expose
    private String geopluginRegionName;

    public String getGeoPluginRequest() {
        return geoPluginRequest;
    }

    public double getGeoPluginLatitude() {
        return geoPluginLatitude;
    }

    public double getGeoPluginLongitude() {
        return geoPluginLongitude;
    }
}
