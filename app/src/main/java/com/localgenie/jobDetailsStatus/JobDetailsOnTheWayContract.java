package com.localgenie.jobDetailsStatus;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * <h>JobDetailsOnTheWayContract</h>
 * Created by Ali on 2/21/2018.
 */

public interface JobDetailsOnTheWayContract
{
     interface JobDetailsOnTheWayPresenter
    {
        Marker plotMarker(Context mContext, LatLng proLatLng, LatLng customerLatLng, GoogleMap mMap);
    }
    interface CancelBooking
    {
        void onToCancelBooking(long bid, Context mContext, String reminderId);

        void onSelectedReason(int res_id);
    }
}
