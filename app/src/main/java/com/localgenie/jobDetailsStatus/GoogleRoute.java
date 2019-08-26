package com.localgenie.jobDetailsStatus;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.localgenie.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <h>GoogleRoute</h>
 * Created by murashid on 28-Sep-16.
 */
public class GoogleRoute {

    static TimeDuration  etaAtJobLoc;

    public static double tempSrcLat,tempSrcLon,tempDestLat,tempDestLong;
    public static String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        tempSrcLat=sourcelat;
        tempSrcLon=sourcelog;
        tempDestLat=destlat;
        tempDestLong=destlog;
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        if(Constants.GOOGLEKEY!=null && Constants.GOOGLEKEY.size()>0)
            urlString.append("&key=").append(Constants.GOOGLEKEY.get(0));
        else
            urlString.append("&key=" + Constants.SERVER_KEY);
        Log.d("mura", "makeURL: " + urlString.toString());
        return urlString.toString();
    }

    public static void startPlotting(GoogleMap googleMap, String urlPass, TextView tvETA, TimeDuration eta)
    {
        etaAtJobLoc = eta;
        GooglePath googlePath = new GooglePath(googleMap, urlPass, tvETA);
        googlePath.execute();
    }

    private static class GooglePath extends AsyncTask<Void, Void, String> {
        String url;
        GoogleMap googleMap;
        TextView textView;

        GooglePath(GoogleMap googleMap, String urlPass, TextView tvETA) {
            url = urlPass;
            this.googleMap = googleMap;
            textView = tvETA;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response;
            String jsonResponse = null;
            try {
                response = client.newCall(request).execute();
                jsonResponse = response.body().string();

            } catch (IOException e) {
                Log.d("Ali", "eror in Ok http" +
                        e.toString());
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                JSONObject json = null;
                Log.d("Ali", "onPostExecute: " + result);
                try {
                    json = new JSONObject(result);
                    if(json.getString("status").equals("OK"))
                    {
                        drawPath(googleMap, result, textView);
                    }else
                    {
                        if(Constants.GOOGLEKEY.size()>0){
                            Constants.GOOGLEKEY.remove(0);
                            String makeURL = makeURL(tempSrcLat, tempSrcLon, tempDestLat, tempDestLong);
                            //   new com.localgenie.Login.GoogleRoute.GetDistanceMatrixEta(makeURL,etaResultInterface).execute();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private static void drawPath(GoogleMap mMap, String result, TextView textView) {

        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");

            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            String duration = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
            String distance = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
            if(etaAtJobLoc!=null)
            {
                etaAtJobLoc.onDistanceTime(duration,distance);
            }
            // String durations = "ETA: " + duration;
            // textView.setText(durations);
            textView.setText(duration);
            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(9)
                    .color(Color.parseColor("#4377DE"))
                    .geodesic(true)
            );

        } catch (Exception e) {

            Log.d("ALi", "drawPath: " + e);
        }
    }

    private static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


}
