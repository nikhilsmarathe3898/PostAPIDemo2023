package com.localgenie.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.localgenie.model.Category;
import com.localgenie.model.CategoryResponse;
import com.localgenie.model.CityData;
import com.localgenie.model.CatDataArray;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.utility.RefreshToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import com.pojo.ErrorHandel;
import retrofit2.Response;

/**
 * <h>ServicesFragPresenter</h>
 * Created by Ali on 1/29/2018.
 */

public class ServicesFragPresenter implements ServiceFragContract.ServicePresenter
{
    private String TAG = ServiceFragContract.class.getSimpleName();

    @Inject LSPServices lspServices;
    @Inject
    SessionManagerImpl manager;
    @Nullable
    private ServiceFragContract.ServiceView serviceView;
    // @Inject ServiceFragContract.ServiceView serviceView;

    @Inject Gson gson;
    @Inject
    ServicesFragPresenter()
    {

    }

    @Override
    public void onGetCategory(final double lat, final double lng, final ArrayList<LatLng> latLngs)
    {

        //  LSPServices service = ServiceFactory.createRetrofitService(LSPServices.class);

        Log.d(TAG, "sessionOnGetCategory: "+manager.getAUTH() +" ipAddress "+manager.getIpAddress()
                +" lat "+lat +" lang "+lng);

        Observable<Response<ResponseBody>> bad=lspServices.getCategories(manager.getAUTH(),Constants.selLang,lat,lng, manager.getIpAddress());
        bad.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //    compositeDisposable.add(d);
                    }
                    @Override
                    public void onNext(Response<ResponseBody> result)
                    {

                        String resultBody;
                        int code = result.code();
                        Log.d(TAG, "onNextCode: "+code);
                        JSONObject jsonObject;
                        try {


                            switch (result.code())
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    resultBody = result.body().string();
                                    Log.d(TAG, "onNextResponse: "+resultBody);
                                    if(resultBody!=null) {
                                        manager.setHomeScreenData(resultBody);
                                        categorySuccess(resultBody,lat,lng,latLngs);
                                    }
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    resultBody = result.errorBody().string();
                                    jsonObject = new JSONObject(resultBody);
                                    if(serviceView!=null) {
                                        serviceView.onHideProgress();
                                        serviceView.onLogout(jsonObject.getString("message"));
                                    }
                                    break;
                                case 400:

                                    resultBody = result.errorBody().string();
                                    jsonObject = new JSONObject(resultBody);

                                    if(serviceView!=null) {
                                        serviceView.onHideProgress();
                                        //  serviceView.onNotOperational(jsonObject.getString("message");
                                        serviceView.onError(jsonObject.getString("message"));
                                    }
                                    break;
                                case 404:
                                    resultBody = result.errorBody().string();
                                    jsonObject = new JSONObject(resultBody);

                                    if(serviceView!=null) {
                                        serviceView.onHideProgress();
                                        serviceView.onNotOperational(jsonObject.getString("message"));
                                    }
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    resultBody = result.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(resultBody, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onGetCategory(lat,lng, latLngs);

                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg)
                                        {
                                            if(serviceView!=null) {


                                                serviceView.onHideProgress();
                                                serviceView.onLogout(msg);
                                            }
                                        }
                                    });
                                    break;

                            }
                        } catch (IOException e)
                        {
                            if(serviceView!=null) {


                                serviceView.onError(e.getMessage());
                                serviceView.onHideProgress();
                            }
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                        if(serviceView!=null)
                        {
                            serviceView.onConnectionError(e.getMessage(),false);
                        }
                        /*if(serviceView!=null)
                            serviceView.onError(e.getMessage());*/
                    }
                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void categorySuccess(String resultBody, double lat, double lng, ArrayList<LatLng> latLngs) {
        CategoryResponse response = gson.fromJson(resultBody,CategoryResponse.class);
        if(response!=null && response.getData()!=null)
        {
            CityData cityData = response.getData().getCityData();
            try
            {
                if(response.getData()!=null && cityData!=null)
                {
                    Constants.currencySymbol = response.getData().getCityData().getCurrencySymbol();
                    if(cityData.getDistanceMatrix()==0)
                        Constants.distanceUnit = "Kms";
                    else
                        Constants.distanceUnit = "Miles";
                    if(Constants.isMenuActivityCalled) {
                        Constants.isMenuActivityCalled = false;
                        callOnDataSet(cityData.getPolygons().getCoordinates()[0]
                                ,response.getData().getCatArr(),latLngs);
                        recommendedTrendingService(response.getData());

                    }else {
                        if(latLngs!=null) {
                            if(!Utility.containsLocation(lat,lng,latLngs,false)) {
                                callOnDataSet(cityData.getPolygons().getCoordinates()[0]
                                        ,response.getData().getCatArr(),latLngs);
                                recommendedTrendingService(response.getData());
                            }
                        }
                    }

                    Constants.paymentAbbr = cityData.getCurrencyAbbr();
                    if(cityData.getPaymentMode() != null) {
                        ServicesFrag.paymentMode = cityData.getPaymentMode();
                    }
                  //  ServicesFrag.paymentMode.setWallet(cityData.getPaymentMode().isWallet());
                    Constants.customerHomePageInterval = cityData.getCustomerFrequency().getCustomerHomePageInterval();
                    Constants.stripeKeys = cityData.getStripeKeys();
                    Constants.GOOGLEKEY = cityData.getCustGoogleMapKeys();
                    Constants.SERVER_KEY = cityData.getGoogleMapKey();
                    manager.setFcmTopicCity(cityData.getPushTopics().getCity());
                    manager.setFcmTopicAllCustomer(cityData.getPushTopics().getAllCustomers());
                    manager.setFcmTopicAllCity(cityData.getPushTopics().getAllCitiesCustomers());

                    if(!"".equals(cityData.getPushTopics().getCity()))
                    {
                        //FirebaseMessaging.getInstance().unsubscribeFromTopic(cityData.getPushTopics().getCity());
                        FirebaseMessaging.getInstance().subscribeToTopic(cityData.getPushTopics().getCity());
                    }
                    if(!"".equals(cityData.getPushTopics().getAllCustomers()))
                    {
                        //FirebaseMessaging.getInstance().unsubscribeFromTopic(cityData.getPushTopics().getAllCustomers());
                        FirebaseMessaging.getInstance().subscribeToTopic(cityData.getPushTopics().getAllCustomers());
                    }
                    if(!"".equals(cityData.getPushTopics().getAllCitiesCustomers()))
                    {
                        //FirebaseMessaging.getInstance().unsubscribeFromTopic(cityData.getPushTopics().getAllCitiesCustomers());
                        FirebaseMessaging.getInstance().subscribeToTopic(cityData.getPushTopics().getAllCitiesCustomers());
                    }

                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

    private void recommendedTrendingService(CategoryResponse.CategoryData data)
    {
        ArrayList<Category>trending = new ArrayList<>();
        ArrayList<Category>recommended = new ArrayList<>();
        if(serviceView!=null)
        {

            for(int i = 0;i<data.getCatArr().size();i++)
            {

                for(Category categoryService : data.getCatArr().get(i).getCategory())
                {
                    for(Category categoryTrend : data.getTrendingArr())
                    {
                        if(categoryService.getId().equals(categoryTrend.getCategoryId()))
                        {
                            categoryService.setIconApp(categoryTrend.getIconApp());
                            trending.add(categoryService);
                        }
                    }

                    for(Category categoryRec :data.getRecommendedArr())
                    {
                        if(categoryService.getId().equals(categoryRec.getCategoryId()))
                        {
                            categoryService.setRecommendedBannerImageApp(categoryRec.getRecommendedBannerImageApp());
                            recommended.add(categoryService);
                        }
                    }

                }

            }
            if(recommended.size()>0)
            {
                serviceView.onRecommendedService(recommended);
            }else
                serviceView.onNoRecommendedService();
            if(trending.size()>0)
            {
                serviceView.onTrendingService(trending);
            }else
                serviceView.onNoTrendingService();
        }
    }

    private void callOnDataSet(double[][] doubles, ArrayList<CatDataArray> catArr, ArrayList<LatLng> latLngs)
    {
        if(latLngs!=null)
        {
            for (double[] aDouble : doubles) {
                double[] strings;

                strings = aDouble;
                latLngs.add(new LatLng(strings[1], strings[0]));

            }
            if(serviceView!=null)
            {
                setDataOn(catArr,latLngs);
            }
        }

    }

    private void setDataOn(ArrayList<CatDataArray> catArr, ArrayList<LatLng> latLngs)
    {
        if(catArr.size()>0)
        {

            assert serviceView != null;
            serviceView.onSuccess(catArr,latLngs);

            serviceView.onHideProgress();
            //   new Handler().postDelayed((Runnable) () -> serviceView.onHideProgress(), 1000);
        }
        else
        {
            assert serviceView != null;
            serviceView.onLessData();
            serviceView.onHideProgress();
        }
    }


    Context mcontext;
    @SuppressLint("StaticFieldLeak")
    private class BackgroundGetAddress extends AsyncTask<String, Void, String>
    {
        List<Address> address;
        String lat,lng;
        @Override
        protected String doInBackground(String... params) {


            try {
                lat = params[0];
                lng = params[1];

                if(lat!=null && lng!=null)
                {
                    if(mcontext!=null)
                    {

                            Geocoder geocoder = new Geocoder(mcontext);
                            address = geocoder.getFromLocation(Double.parseDouble(params[0]), Double.parseDouble(params[1]), 1);

                    }
                }
            } catch (IOException e)
            {

                e.printStackTrace();

                ((Activity)mcontext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callmethod(lat,lng);
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if(address!=null && address.size()>0)
            {
                Address obj = address.get(0);//.getAddressLine(0);//+", "+address.get(0).getAddressLine(1)
              /*  homejobaddress.setText(fuladdress);
                youraddress.setText(fuladdress);
                manager.setSavedAddress(fuladdress);*/

                if(obj.getFeatureName()!=null && obj.getFeatureName().length()>5)
                {
                    if(serviceView!=null)
                        serviceView.onAddressTOShow(obj.getFeatureName());
                }else if(obj.getSubLocality()!=null)
                {
                    if(serviceView!=null)
                        serviceView.onAddressTOShow(obj.getSubLocality());
                }else if(obj.getSubAdminArea()!=null)
                {
                    if(serviceView!=null)
                        serviceView.onAddressTOShow(obj.getSubAdminArea());
                }

                manager.setAddress(obj.getAddressLine(0));
            }
        }
    }
    String currentLatitude,currentLongitude;

    private void callmethod(String lat, String lng) {
        //  BackgroundGeocodingTask()
        currentLatitude = lat;
        currentLongitude = lng;
        //  new BackgroundGeocodingTask().execute();
    }
    /*private class BackgroundGeocodingTask extends AsyncTask<String, Void, String>
    {
        GeocodingResponse response;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String url="https://maps.googleapis.com/maps/api/geocode/json?latlng="+currentLatitude+","+currentLongitude+"&sensor=false&key="+ Variableconstant.SERVERKEY;
            Utility.printLog("Geocoding url: " + url);

            String stringResponse= Utility.callhttpRequest(url);

            if(stringResponse!=null)
            {
                response=gson.fromJson(stringResponse, GeocodingResponse.class);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if(response!=null)
            {
                if(response.getStatus().equals("OK"))
                {

                    String short_address[]=response.getResults().get(0).getFormatted_address().split(",");
                    String temp_address=null;
                    if(short_address.length==1)
                    {

                        if(short_address[0]!=null)
                        {
                            temp_address=short_address[0];
                            if(short_address.length==2)
                            {

                                if(short_address[1]!=null)
                                {
                                    temp_address=temp_address+", "+short_address[1];


                                    if(short_address.length==3)
                                    {

                                        if(short_address[2]!=null)
                                        {
                                            temp_address=temp_address+", "+short_address[2];
                                        }
                                    }

                                }
                            }
                        }
                        Utility.printLog("VariableConstants.area_name: " + temp_address);
                    }
                }
            }
        }
    }*/

    @Override
    public void onAddress(Context mContext,double latitude, double longitude)
    {
        //Get address from the provided latitude and longitude
        mcontext = mContext;
        String params[] = {""+latitude,""+longitude};



        try {
            Geocoder geocoder = new Geocoder(mContext);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
            StringBuilder builder = new StringBuilder();
            //  String add = obj.getAddressLine(0);

            builder.append(obj.getAddressLine(0));
            builder.append(",SAD ").append(obj.getSubAdminArea());
            builder.append(",SL ").append(obj.getSubLocality());
            builder.append(",FN ").append(obj.getFeatureName());
            builder.append(",Ad ").append(obj.getAdminArea());
            builder.append(",CN ").append(obj.getCountryName());

            Log.e(TAG, "onAddressSub: "+obj.getSubAdminArea()+" at 0 "+
                    obj.getAddressLine(0)+" subLoc "+obj.getSubLocality()
                    +" feature "+obj.getFeatureName() +" area "+obj.getAdminArea()+" country "+obj.getCountryName());

            if(obj.getFeatureName()!=null && obj.getFeatureName().length()>5)
            {
                if(serviceView!=null)
                    serviceView.onAddressTOShow(obj.getFeatureName());
            }else if(obj.getSubLocality()!=null)
            {
                if(serviceView!=null)
                    serviceView.onAddressTOShow(obj.getSubLocality());
            }else if(obj.getSubAdminArea()!=null)
            {
                if(serviceView!=null)
                    serviceView.onAddressTOShow(obj.getSubAdminArea());
            }

            manager.setAddress(obj.getAddressLine(0));

            Log.e(TAG,"Address => "+builder);
            // return add;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            new BackgroundGetAddress().execute(params);
            //   Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            //  return "Dragged location";
        }

    }


    @Override
    public void onPendingBooking()
    {
        Observable<Response<ResponseBody>> observable = lspServices.onTOGetPendingBooking(manager.getAUTH(),
                Constants.selLang);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        String response;
                        try {

                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextPendingBooking: "+response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray arry = jsonObject.getJSONArray("data");
                                    if(arry!=null && serviceView!=null && arry.length()>0)
                                        serviceView.onPendingBooking(arry.getLong(0));

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response,ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onPendingBooking();
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg)
                                        {
                                            if(serviceView!=null)
                                                serviceView.onLogout(msg);
                                        }
                                    });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    if(serviceView!=null)
                                        serviceView.onLogout(jsonObject.getString("message"));
                                    break;

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                        if(serviceView!=null)
                        {
                            serviceView.onConnectionError(e.getMessage(),true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }





    @Override
    public void onPendingInvocieBooking()
    {
        Observable<Response<ResponseBody>> observable = lspServices.onTOGetPendingInvoiceBooking(manager.getAUTH(),
                Constants.selLang);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        String response;
                        try {

                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextPendingBooking2: "+response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray arry = jsonObject.getJSONArray("data");
                                    if(arry!=null && serviceView!=null && arry.length()>0) {
                                        for (int j = 0; j < arry.length() ; j++ ) {
                                            JSONObject jItem = arry.getJSONObject(j);
                                            long bookingId = jItem.getLong("bookingId");
                                            serviceView.onPendingInvocieBooking(bookingId);
                                        }
                                    }
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response,ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            onPendingInvocieBooking();
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg)
                                        {
                                            if(serviceView!=null)
                                                serviceView.onLogout(msg);
                                        }
                                    });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    response = responseBodyResponse.errorBody().string();
                                    jsonObject = new JSONObject(response);
                                    if(serviceView!=null)
                                        serviceView.onLogout(jsonObject.getString("message"));
                                    break;

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                        if(serviceView!=null)
                        {
                            serviceView.onConnectionError(e.getMessage(),true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void onTogetServerTime() {

        Observable<Response<ResponseBody>>observable = lspServices.onTogetServerTime(Constants.selLang);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        try
                        {
                            if(code == Constants.SUCCESS_RESPONSE)
                                Constants.serverTime  = new JSONObject(responseBodyResponse.body().string()).getLong("data");

                            Constants.diffServerTime  =  System.currentTimeMillis()-(Constants.serverTime*1000);


                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(serviceView!=null)
                        {
                            serviceView.onConnectionError(e.getMessage(),true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void attachView(Object view) {
        serviceView = (ServiceFragContract.ServiceView) view;
    }

    @Override
    public void detachView()
    {
        serviceView = null;
    }


    @Override
    public void showStatusBar(LinearLayout llHOmeAddress, boolean b) {

        float alpha;
        int height;
        long duration;
        if(b)
        {
            alpha =1.0f;
            height = 0;
            duration = 50;
        }
        else
        {
            height =  llHOmeAddress.getHeight();
            alpha =0.0f;
            duration = 300;
        }

        llHOmeAddress.animate()
                .translationY(height)
                .alpha(alpha)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if(b)
                            llHOmeAddress.setVisibility(View.VISIBLE);
                        else
                            llHOmeAddress.setVisibility(View.GONE);

                    }
                });
    }
}

