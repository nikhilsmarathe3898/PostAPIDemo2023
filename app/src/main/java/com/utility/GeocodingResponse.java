package com.utility;

import java.util.ArrayList;

public class GeocodingResponse {
    String status;
    ArrayList<GeocodingAddressList> results;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<GeocodingAddressList> getResults() {
        return results;
    }


}
