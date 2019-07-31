package com.localgenie.home;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.add_address.SearchAddressLocation;
import com.localgenie.invoice.InvoiceActivity;
import com.localgenie.lspapplication.LocationDialogFragment;
import com.localgenie.model.CatDataArray;
import com.localgenie.model.Category;
import com.localgenie.model.CategoryResponse;
import com.localgenie.model.CityData;
import com.localgenie.rateYourBooking.RateYourBooking;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;
import com.utility.LocationUtil;
import com.utility.PermissionsListener;
import com.utility.PermissionsManager;
import com.utility.ShimmerLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import adapters.NewHomeServiceAdapter;
import adapters.RecommendedTrendingAdapter;
import adapters.ServicesAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServicesFrag extends DaggerFragment implements ServicesAdapter.OnServiceClicked, PermissionsListener, LocationUtil.LocationNotifier, ServiceFragContract.ServiceView {

    public static CityData.PaymentMode paymentMode;
    private final int SEARCH_RESULT = 101;
    @Inject
    PermissionsManager permissionsManager;
    @Inject
    AppTypeface appTypeface;
    @Inject
    ServiceFragContract.ServicePresenter servicePresenter;
    @Inject
    SessionManagerImpl manager;
    @BindView(R.id.rvHomeServiceCatagories)
    RecyclerView rvHomeServiceCatagories;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvYourLocation)
    TextView tvYourLocation;
    @BindView(R.id.rlSearchLayout)
    RelativeLayout rlSearchLayout;
    // @BindView(R.id.toolBarHome)Toolbar toolBarHome;
    @BindView(R.id.llHOmeAddress)
    LinearLayout llHOmeAddress;
    @BindView(R.id.nestedScrollViewHome)
    NestedScrollView nestedScrollViewHome;
    @BindView(R.id.rvRecommendedService)
    RecyclerView rvRecommendedService;
    //  @BindView(R.id.rvTrendingService)RecyclerView rvTrendingService;
    @BindView(R.id.tvRecommended)
    TextView tvRecommended;
    @BindView(R.id.shimmer)
    ShimmerLayout shimmerLayout;
    @BindView(R.id.tvNotOperational)
    TextView tvNotOperational;
    @BindView(R.id.rlNotOperational)
    RelativeLayout rlNotOperational;
    @Inject
    MQTTManager mqttManager;
    @Inject
    AlertProgress alertProgress;
    Context mContext;
    ArrayList<LatLng> latLngs = new ArrayList<>();
    SnappingLinearLayoutManager llOutManager;
    private int position = 0;
    private int firstVisibleInListview;
    private String TAG = ServicesFrag.class.getSimpleName();
    private String auth;
    private View view;
    private ArrayList<CatDataArray> dataList;
    private double currentLatLng[] = new double[2];
    private LocationUtil networkUtilObj;
    private Animation downMoment, upMoment;
    private boolean isReturnedFromLocation = false;
    private RecommendedTrendingAdapter recommendedAdapter, trendingAdapter;
    private ArrayList<Category> recommendedList = new ArrayList<>();
    //  private ArrayList<Category> trendingList = new ArrayList<>();
    private ArrayList<Category> categoryList = new ArrayList<>();


    @Inject
    public ServicesFrag() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_services, container, false);

        mContext = getActivity();
        ButterKnife.bind(this, view);
        // shimmerLayout.startShimmerAnimation();
        upMoment = AnimationUtils.loadAnimation(mContext, R.anim.anim_homepage_up_movement);
        downMoment = AnimationUtils.loadAnimation(mContext, R.anim.anim_homepage_down_movement);
        permissionsManager.setListener(this);
        auth = manager.getAUTH();

        if (!mqttManager.isMQTTConnected()) {
            mqttManager.createMQttConnection(manager.getSID(),false);
        }
        servicePresenter.attachView(this);
        initialize();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callReviewPendingBooking();
            }
        }, 4000);
        char a = 'a';
        char b = 'b';
        Log.d(TAG, "onCreateView: Shijen a>b " + (a > b) + " (int)a:" + ((int) a) + " ((int)b):" + ((int) b));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (manager != null && !"".equals(manager.getHomeScreenData())) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @OnClick(R.id.llHOmeAddress)
    public void onAddressChenge() {
        Intent intent = new Intent(mContext, SearchAddressLocation.class);//SearchAddressLocation
        intent.putExtra("CominFROM", "HOMEFRAG");
        startActivityForResult(intent, SEARCH_RESULT);
    }

    private void callReviewPendingBooking() {
        if (alertProgress.isNetworkAvailable(mContext)){
            servicePresenter.onPendingBooking();
         servicePresenter.onPendingInvocieBooking();}
        else
            alertProgress.showNetworkAlert(mContext);
    }

    /**
     * initialize the view
     */
    private void initialize() {
        dataList = new ArrayList<>();
       /* LinearLayoutManager llOutManager = new LinearLayoutManager(getActivity());
        rvServiceCategories.setLayoutManager(llOutManager);*/
        llOutManager = new SnappingLinearLayoutManager(mContext);
        rvHomeServiceCatagories.setLayoutManager(llOutManager);

        LinearLayoutManager managerRecommended = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recommendedAdapter = new RecommendedTrendingAdapter(recommendedList, mContext, false);
        rvRecommendedService.setLayoutManager(managerRecommended);
        rvRecommendedService.setAdapter(recommendedAdapter);


        try {
            if (manager.getHomeScreenData() != null && !"".equals(manager.getHomeScreenData())) {
                Gson gson = new Gson();
                CategoryResponse response = gson.fromJson(manager.getHomeScreenData(), CategoryResponse.class);
                if (response != null) {
                    if (response.getData() != null) {
                        CityData cityData = response.getData().getCityData();
                        if (cityData != null && cityData.getPolygons().getCoordinates().length > 0) {
                            Constants.currencySymbol = cityData.getCurrencySymbol();
                            servicePresenter.categorySuccess(manager.getHomeScreenData(), 0, 0, null);
                        } else
                            callCategoryService();
                    } else
                        callCategoryService();
                } else
                    callCategoryService();
            } else  //i am doing
                callCategoryService();
        } catch (Exception e) {
            e.printStackTrace();
            callCategoryService();
        }


        TextView tvSearch = view.findViewById(R.id.tvSearch);

        if ((manager.getLatitude() != null && !"".equals(manager.getLatitude()))) {
            currentLatLng[0] = Double.parseDouble(manager.getLatitude());
            currentLatLng[1] = Double.parseDouble(manager.getLongitude());
            servicePresenter.onAddress(mContext, currentLatLng[0], currentLatLng[1]);
        }

        tvSearch.setOnClickListener(view -> {
            if (categoryList.size() > 0) {
                Intent intent = new Intent(mContext, SearchFilter.class);
                intent.putExtra("CategoryList", categoryList);
                startActivity(intent);
            }

        });

        tvAddress.setTypeface(appTypeface.getHind_semiBold());
        tvYourLocation.setTypeface(appTypeface.getHind_regular());
        tvRecommended.setTypeface(appTypeface.getHind_semiBold());
        tvNotOperational.setTypeface(appTypeface.getHind_medium());

        ///////////////////////////////////////////////////////////////////////

        nestedScrollViewHome.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > 540) {
                    servicePresenter.showStatusBar(llHOmeAddress, false);
                }

                if (scrollY == 0) {

                    servicePresenter.showStatusBar(llHOmeAddress, true);
                }

            }
        });

        ////////////////////////////////////////////////////////////

        //  rvServiceCategories.setNestedScrollingEnabled(false);
        rvHomeServiceCatagories.setNestedScrollingEnabled(false);
        rvRecommendedService.setNestedScrollingEnabled(false);
        //  rvTrendingService.setNestedScrollingEnabled(false);

    }

    private void callCategoryService() {

        if (manager.getIpAddress().equals("0")) {
            new Handler().postDelayed(this::onCategoryService, 2000);
        } else
            onCategoryService();
    }


    private void onCategoryService() {
        /*if(alertProgress.isNetworkAvailable(mContext))
        {*/
        onShowProgress();
        servicePresenter.onGetCategory(currentLatLng[0], currentLatLng[1], latLngs);
        /*}else
            alertProgress.showNetworkAlert(mContext);*/

    }

    @Override
    public void onResume() {
        super.onResume();

        alertProgress.IPAddress((ipAddress, lat, lng) -> manager.setIpAddress(ipAddress));

        if (permissionsManager.areRuntimePermissionsRequired()) {
            if (permissionsManager.areLocationPermissionsGranted(mContext))
                showDialog();
            else
                createLocationObj();
        } else
            createLocationObj();

        Constants.isJobDetailsOpen = false;

        if (mqttManager.isMQTTConnected())
            mqttManager.subscribeToTopic(MqttEvents.Provider.value + "/" + manager.getSID(), 1);

        servicePresenter.onTogetServerTime();
    }

    private void showDialog() {
        LocationDialogFragment locationDialogFragment = new LocationDialogFragment();
        locationDialogFragment.initializePresenter(this);
        locationDialogFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onLocationListener(boolean isAllowed) {
        if (isAllowed) {
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    private void createLocationObj() {

        /*if(Constants.isMenuActivityCalled)
        {*/
        //   Constants.isMenuActivityCalled = false;
        if (!isReturnedFromLocation) {
            networkUtilObj = new LocationUtil(getActivity());
            networkUtilObj.setListener(this);
            if (!networkUtilObj.isGoogleApiConnected()) {
                networkUtilObj.checkLocationSettings();
            }
        }

        //  }

    }


    @Override
    public void onServiceClicked(String serviceName) {

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted)
            createLocationObj();

    }

    @Override
    public void updateLocation(Location location) {
        currentLatLng[0] = location.getLatitude();
        currentLatLng[1] = location.getLongitude();

        manager.setLatitude(currentLatLng[0] + "");
        manager.setLongitude(currentLatLng[1] + "");

        Constants.latitude = currentLatLng[0];
        Constants.longitude = currentLatLng[1];
        Constants.currentLat = currentLatLng[0];
        Constants.currentLng = currentLatLng[1];

        servicePresenter.onGetCategory(currentLatLng[0], currentLatLng[1], latLngs);
        servicePresenter.onAddress(mContext, currentLatLng[0], currentLatLng[1]);

        if (networkUtilObj != null && networkUtilObj.isGoogleApiConnected()) {
            networkUtilObj.stoppingLocationUpdate();
        }
    }

    @Override
    public void locationMsg(String error) {

    }

    @Override
    public void onSuccess(ArrayList<CatDataArray> data, ArrayList<LatLng> latLngs) {
        dataList.clear();
        dataList.addAll(data);
        categoryList.clear();
        for (int i = 0; i < data.size(); i++) {
            categoryList.addAll(data.get(i).getCategory());
        }
        rlNotOperational.setVisibility(View.GONE);
        //  rvServiceCategories.setVisibility(View.VISIBLE);
        rvHomeServiceCatagories.setVisibility(View.VISIBLE);
        if (dataList.size() > 0) {
            /*ServicesCategoryAdapter servicesCategoryAdapter = new ServicesCategoryAdapter(dataList, getActivity());
            rvServiceCategories.setAdapter(servicesCategoryAdapter);
            servicesCategoryAdapter.notifyDataSetChanged();*/
            NewHomeServiceAdapter servicesCategoryAdapter = new NewHomeServiceAdapter(mContext, dataList, this);
            rvHomeServiceCatagories.setAdapter(servicesCategoryAdapter);
            servicesCategoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPositionSelected(int adapterPosition) {
        //     llOutManager.scrollToPositionWithOffset(adapterPosition,0);
        rvHomeServiceCatagories.smoothScrollToPosition(adapterPosition);

    }

    @Override
    public void onLessData() {

    }

    @Override
    public void onAddressTOShow(String featureAddressName) {
        tvAddress.setText(featureAddressName);
    }

    @Override
    public void onPendingBooking(long bid) {
        if (bid != 0) {
            Constants.isHomeFragment = true;
            Intent intent = new Intent(mContext, RateYourBooking.class);
            intent.putExtra("BID", bid);
            startActivity(intent);
        }
    }

    @Override
    public void onPendingInvocieBooking(long bookingId) {
        if (bookingId != 0) {
            Constants.isHomeFragment = true;
            Intent intent = new Intent(mContext, InvoiceActivity.class);
            intent.putExtra("BID", bookingId);
            startActivity(intent);
        }

    }


    @Override
    public void onRecommendedService(ArrayList<Category> recommendedArr) {
        if (recommendedArr.size() > 0) {
            recommendedList.clear();
            recommendedList.addAll(recommendedArr);
            recommendedAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNoRecommendedService() {
        recommendedList.clear();
        recommendedAdapter.notifyDataSetChanged();
        tvRecommended.setVisibility(View.GONE);
    }


    @Override
    public void onTrendingService(ArrayList<Category> trendingArr) {
       /* if(trendingArr.size()>0)
        {
            trendingList.clear();
            trendingList.addAll(trendingArr);
            trendingAdapter.notifyDataSetChanged();
        }*/
    }

    @Override
    public void onNoTrendingService() {
      /*  trendingList.clear();
        trendingAdapter.notifyDataSetChanged();*/
    }

    @Override
    public void onNotOperational(String message) {
        Constants.isMenuActivityCalled = true;
        //   trendingList.clear();
        //   trendingAdapter.notifyDataSetChanged();
        recommendedList.clear();
        recommendedAdapter.notifyDataSetChanged();
        rlNotOperational.setVisibility(View.VISIBLE);
        tvNotOperational.setText(message);
        alertProgress.alertinfo(mContext, message);
        dataList.clear();
        categoryList.clear();
       /* ServicesCategoryAdapter servicesCategoryAdapter = new ServicesCategoryAdapter(dataList, getActivity());
        rvServiceCategories.setAdapter(servicesCategoryAdapter);
        servicesCategoryAdapter.notifyDataSetChanged();*/

        NewHomeServiceAdapter servicesCategoryAdapter = new NewHomeServiceAdapter(mContext, dataList, this);
        rvHomeServiceCatagories.setAdapter(servicesCategoryAdapter);
        servicesCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionError(String message, boolean isPendingBooking) {
        if (isPendingBooking)
            callReviewPendingBooking();
        else
            onCategoryService();

    }

    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message) {

        alertProgress.alertPositiveOnclick(mContext, message, getString(R.string.logout), getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(mContext, manager);
            }
        });
    }

    @Override
    public void onError(String error) {
        Constants.isMenuActivityCalled = true;
        // rvServiceCategories.setVisibility(View.GONE);
        rvHomeServiceCatagories.setVisibility(View.GONE);
        alertProgress.alertinfo(mContext, error);

        dataList.clear();
        categoryList.clear();
       /* ServicesCategoryAdapter servicesCategoryAdapter = new ServicesCategoryAdapter(dataList, getActivity());
        rvServiceCategories.setAdapter(servicesCategoryAdapter);
        servicesCategoryAdapter.notifyDataSetChanged();*/
        NewHomeServiceAdapter servicesCategoryAdapter = new NewHomeServiceAdapter(mContext, dataList, this);
        rvHomeServiceCatagories.setAdapter(servicesCategoryAdapter);
        servicesCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShowProgress() {
        // progressBar.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();
    }

    @Override
    public void onHideProgress() {
        //  progressBar.setVisibility(View.GONE);
        shimmerLayout.stopShimmerAnimation();
        shimmerLayout.setVisibility(View.GONE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_RESULT) {
            if (data != null) {
                String addrssname = data.getStringExtra("placename");
                String featureAddressName = data.getStringExtra("formatedaddes");
                currentLatLng[0] = data.getDoubleExtra("LATITUDE", 0);
                currentLatLng[1] = data.getDoubleExtra("LONGITUDE", 0);


                shimmerLayout.setVisibility(View.VISIBLE);
                rlNotOperational.setVisibility(View.GONE);
               /* Constants.latitude = currentLatLng[0];
                Constants.longitude = currentLatLng[1];*/
                Constants.isMenuActivityCalled = true;
                onShowProgress();
                if ("NO".equals(addrssname))
                    isReturnedFromLocation = false;
                else {
                    isReturnedFromLocation = true;
                    tvAddress.setText(addrssname);
                    manager.setAddress(featureAddressName);
                    manager.setLatitude(currentLatLng[0] + "");
                    manager.setLongitude(currentLatLng[1] + "");
                    Constants.latitude = currentLatLng[0];
                    Constants.longitude = currentLatLng[1];
                    servicePresenter.onGetCategory(currentLatLng[0], currentLatLng[1], latLngs);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        servicePresenter.detachView();

    }

    @Override
    public void onPause() {
        super.onPause();
        //   servicePresenter.detachView();
    }
}
