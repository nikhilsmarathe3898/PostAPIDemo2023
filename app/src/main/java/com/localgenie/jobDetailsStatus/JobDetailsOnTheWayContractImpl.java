package com.localgenie.jobDetailsStatus;

import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.localgenie.R;
import com.localgenie.utilities.BitmapCustomMarker;

import javax.inject.Inject;

/**
 * <h>JobDetailsOnTheWayContractImpl</h>
 * Created by Ali on 2/21/2018.
 */

public class JobDetailsOnTheWayContractImpl implements JobDetailsOnTheWayContract.JobDetailsOnTheWayPresenter
{

    private Marker proMarker = null, ownMArker = null;
    private String etaTime = "";

    @Inject
    public JobDetailsOnTheWayContractImpl() {
    }



    @Override
    public Marker plotMarker(final Context mContext, LatLng proLatLng, LatLng customerLatLng, GoogleMap mMap)
    {

        String url = GoogleRoute.makeURL(customerLatLng.latitude, customerLatLng.longitude,
                proLatLng.latitude, proLatLng.longitude);
        GoogleRoute.startPlotting(mMap, url, new TextView(mContext), (timeEta, distance) -> {
            etaTime = timeEta;
          //  liveBookingStatusInt.onDistanceTime(distance,timeEta);
            if(proMarker!=null)
            {
                BitmapCustomMarker bitmapCustomMarker = new BitmapCustomMarker(mContext, etaTime);
                proMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapCustomMarker.createBitmap()));
            }
        });

        if (proMarker != null) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(proLatLng);
            mMap.moveCamera(center);
            return proMarker;
        } else {
            BitmapCustomMarker bitmapCustomMarker = new BitmapCustomMarker(mContext, etaTime);
            proMarker = mMap
                    .addMarker(new MarkerOptions()
                            .position(proLatLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapCustomMarker.createBitmap()))
                    );
            CameraUpdate center = CameraUpdateFactory.newLatLng(customerLatLng);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(13.0f);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

        }
        ownMArker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_new_address_red_pin_icon))
                .position(customerLatLng));
        ownMArker.setFlat(false);
        return proMarker;
    }
}
