package com.localgenie.providerList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.ProviderSearch.ProviderSearchActivity;
import com.localgenie.R;
import com.localgenie.filter.FilterActivity;
import com.localgenie.model.SubCategory;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.FilterAdapter;
import adapters.SingleProviderAdapter;
import adapters.SubCategoryAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import com.pojo.FilteredResponse;
import com.pojo.ProviderData;
import com.pojo.ProviderObservable;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;
import com.utility.ShimmerLayout;

/**
 * <h>ProviderList</h>
 * Created by Ali on 1/29/2018.
 */

public class ProviderList extends DaggerAppCompatActivity implements ProviderListContract.providerView,Runnable
{
    private String TAG = ProviderList.class.getSimpleName();
    @BindView(R.id.cardSubCategory)RelativeLayout cardSubCategory;
    @BindView(R.id.recyclerViewSubCat)RecyclerView recyclerViewSubCat;
    @BindView(R.id.recyclerViewFiltered)RecyclerView recyclerViewFiltered;
    @BindView(R.id.recyclerViewProviderList)RecyclerView recyclerViewProviderList;
    @BindView(R.id.tv_center)TextView tv_center;
    @BindView(R.id.ivFilter) ImageView ivFilter;
    @BindView(R.id.rlToolImage)LinearLayout rlToolImage;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.shimmerProvider)ShimmerLayout shimmerProvider;
    @BindView(R.id.nestedScrollViewProList)NestedScrollView nestedScrollViewProList;
    @BindView(R.id.llNoProviderAvailable)LinearLayout llNoProviderAvailable;
    @BindView(R.id.tvProviderNotAvailable)TextView tvProviderNotAvailable;
    @BindView(R.id.rlSearchLayout)RelativeLayout rlSearchLayout;
    @BindView(R.id.tvSearch)TextView tvSearch;
    @Inject AppTypeface appTypeface;
    @Inject ProviderListContract.providerPresenter providerPresenter;
    @Inject SessionManagerImpl manager;
    @Inject CompositeDisposable disposable;
    private Observer<ArrayList<ProviderData>> observe;
    @Inject ProviderObservable providerObservable;
    @Inject MQTTManager mqttManager;
    @Inject
    AlertProgress alertProgress;

    private String catId;
    private double minAmount,maxAmount;
    private ArrayList<SubCategory>subCategoryArrayList;
    private Handler handler = new Handler();
    private Runnable runnableCode;
    private SingleProviderAdapter singleProviderAdapter;
    private ArrayList<ProviderData>providerDataList;
    private ArrayList<FilteredResponse> filteredResponseArray;
    private FilterAdapter filterAdapter;
    public  static final String PROVIDERLIST="ProviderList";
    private boolean isFirstTrue = true;
    private boolean isAlertTrue = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_list);
        Constants.mqttErrorMsg = "";
        ButterKnife.bind(this);
        providerPresenter.attachView(this);
        runnableCode = this;
        providerDataList = new ArrayList<>();
        filteredResponseArray = new ArrayList<>();
        initializeRecyclerView();
        getIntentValue();
        initializeConstants();
        initializeToolBar();
        callProviderApi();
        initializeRxJava();
        if(mqttManager.isMQTTConnected())
        {
            mqttManager.subscribeToTopic(MqttEvents.Provider.value + "/" + manager.getSID(), 1);
        }
        Log.d(TAG, "onCreateIsMqttConnected: "+mqttManager.isMQTTConnected());
    }

    private void initializeRecyclerView() {
        LinearLayoutManager llManager = new LinearLayoutManager(this);
        singleProviderAdapter = new SingleProviderAdapter(this,providerDataList);
        recyclerViewProviderList.setLayoutManager(llManager);
        recyclerViewProviderList.setAdapter(singleProviderAdapter);
        recyclerViewProviderList.setNestedScrollingEnabled(false);
    }

    private void initializeConstants() {
        try
        {
            Constants.lat = Double.parseDouble(manager.getLatitude());
            Constants.lng= Double.parseDouble(manager.getLongitude());
            Constants.minPrice = minAmount;
            Constants.maxPrice = maxAmount;
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
        if(singleProviderAdapter!=null)
            singleProviderAdapter.splitIsFalse();
        if(filteredResponseArray.size()>0)
        {
            recyclerViewFiltered.setVisibility(View.VISIBLE);
            recyclerViewSubCat.setVisibility(View.GONE);
        }else {
            recyclerViewFiltered.setVisibility(View.GONE);
            recyclerViewSubCat.setVisibility(View.VISIBLE);
        }
    }

    private void startTimer()
    {
        handler.postDelayed(runnableCode,4000);
    }

    private void initializeRxJava()
    {

        observe = new Observer<ArrayList<ProviderData>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ArrayList<ProviderData> providerData) {

                providerDataList.clear();
                Log.d(TAG, "onNextProviderNxt: "+providerData.size());
                providerDataList.addAll(providerData);
                singleProviderAdapter.splitData();
                singleProviderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        //ProviderObservable.getInstance().subscribe(observe);
        providerObservable.subscribe(observe);

       /* ProviderObservable.getInstance().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ProviderData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<ProviderData> providerData) {

                        providerDataList.clear();
                        Log.d(TAG, "onNextProviderNxt: "+providerData.size());
                        providerDataList.addAll(providerData);
                        singleProviderAdapter.splitData();
                        singleProviderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        ProviderObservable.getInstance().subscribe(observe);*/

    }

    private void getIntentValue()
    {
        if(getIntent().getExtras()!=null)
        {
            subCategoryArrayList = (ArrayList<SubCategory>) getIntent().getSerializableExtra("SubCat");
            catId = getIntent().getStringExtra("CatId");
            minAmount = getIntent().getDoubleExtra("MinAmount",0);
            maxAmount = getIntent().getDoubleExtra("MaxAmount",0);
            Log.d(TAG, "onClickGrid: CatId:"+catId+" subcat:"+subCategoryArrayList+ " MinAmount:"+minAmount+
                    " maxAmount: "+maxAmount);
            Log.d(TAG, "getIntentValue: "+catId+" subCat "
                    +subCategoryArrayList+ " MinFee "+minAmount);
        }

        if(subCategoryArrayList!=null)
            showSubCategory();

    }

    private void showSubCategory()
    {
        if(subCategoryArrayList.size()>0)
        {
            SubCategoryAdapter subCategoryAdapter = new SubCategoryAdapter(this,subCategoryArrayList,true,this);
            cardSubCategory.setVisibility(View.VISIBLE);
            recyclerViewSubCat.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            recyclerViewSubCat.setAdapter(subCategoryAdapter);
            subCategoryAdapter.notifyDataSetChanged();
        }
        recyclerViewFiltered.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
    }

    private void initializeToolBar()
    {
        ivFilter.setVisibility(View.VISIBLE);
        rlToolImage.setVisibility(View.VISIBLE);
        tvProviderNotAvailable.setTypeface(appTypeface.getHind_semiBold());
        Toolbar providerToolBar = findViewById(R.id.providerToolBar);
        setSupportActionBar(providerToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv_center.setText(Constants.catName);//getString(R.string.bookingType)
        ivFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter));
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        // providerToolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        providerToolBar.setNavigationOnClickListener(view -> onBackPressed());
    }
    @OnClick(R.id.ivFilter)
    public void filterClicked()
    {

        Intent intent = new Intent(this, FilterActivity.class);
        intent.putExtra("SubCat",subCategoryArrayList);
        intent.putExtra("MinAmount",minAmount);
        intent.putExtra("MaxAmount",maxAmount);
        intent.putExtra("FilteredResp",filteredResponseArray);
        startActivityForResult(intent,Constants.FILTER_RESULT_CODE);
        overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
    }

    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message) {
        alertProgress.alertPositiveOnclick(this, message, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(ProviderList.this,manager);
            }
        });
    }

    @Override
    public void onError(String error)
    {
        alertProgress.alertinfo(this,error);
    }

    @Override
    public void onShowProgress() {

        //   progressBar.setVisibility(View.VISIBLE);
        shimmerProvider.startShimmerAnimation();
        //  shimmerProvider.sta
    }

    @Override
    public void onHideProgress() {
        //   progressBar.setVisibility(View.GONE);
        shimmerProvider.stopShimmerAnimation();
        shimmerProvider.setVisibility(View.GONE);
    }

    @Override
    public void onSuccessData(ArrayList<ProviderData> data)
    {
        llNoProviderAvailable.setVisibility(View.GONE);
        recyclerViewProviderList.setVisibility(View.VISIBLE);
        providerDataList.clear();
        Log.d(TAG, "onSuccessData: "+data);
        providerDataList.addAll(data);
        Log.d(TAG, "onSuccessDataName: "+data.get(0).getFirstName());
        singleProviderAdapter.splitData();
        singleProviderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFilterRemoveArray(ArrayList<FilteredResponse> filteredResponsed)
    {
        if(filteredResponsed.size()>0)
        {
            recyclerViewSubCat.setVisibility(View.GONE);
            recyclerViewFiltered.setVisibility(View.VISIBLE);
        }else
        {
            recyclerViewSubCat.setVisibility(View.VISIBLE);
            recyclerViewFiltered.setVisibility(View.GONE);
        }
        filterAdapter.notifyDataSetChanged();
    }

    @Override
    public void setLatLng()
    {
        Constants.lat = Double.parseDouble(manager.getLatitude());
        Constants.lng = Double.parseDouble(manager.getLongitude());
        Constants.filteredAddress = "";
        Constants.mqttErrorMsg = "";
        startTimer();

    }

    @Override
    public void onNoConnectionAvailable(String message, boolean isLocation)
    {
        if(isLocation)
            calLocationApi();
        else
            callProviderApi();

    }

    @Override
    public void setOnItemSelected(String subCatId, String subCatName) {
        FilteredResponse responseSubCat = new FilteredResponse(Constants.lat,Constants.lng,subCatId,subCatName,3,0,0);
        filteredResponseArray.add(responseSubCat);
        Constants.subCatId = subCatId;
        Log.i(TAG, "onActivityResult: "+filteredResponseArray.size());
        filteredAdapterSet(filteredResponseArray);
    }

    private void calLocationApi()
    {
        if(alertProgress.isNetworkAvailable(this))
            providerPresenter.onGetProviderLocation(catId);
        else
            alertProgress.showNetworkAlert(this);
    }


    private void callProviderApi()
    {
        if(alertProgress.isNetworkAvailable(this))
        {
            onShowProgress();
            providerPresenter.onGetProviderService(catId);
        }else
            alertProgress.showNetworkAlert(this);
    }

    @OnClick({R.id.tvSearch,R.id.rlSearchLayout})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.tvSearch:
            case R.id.rlSearchLayout:
                Intent intent =new Intent(this, ProviderSearchActivity.class);
                Bundle bundle =new Bundle();
                bundle.putSerializable(PROVIDERLIST,providerDataList);
                intent.putExtra("BUNDLE",bundle);
                startActivity(intent);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        providerPresenter.detachView();
        disposable.clear();

    }

    @Override
    public void run() {

        //  if(alertProgress.isNetworkAvailable(this))
        if("".equals(Constants.mqttErrorMsg))
        {
            providerPresenter.onGetProviderLocation(catId);
      /*  else
            alertProgress.showNetworkAlert(this);*/
            isAlertTrue = true;
            handler.postDelayed(runnableCode,1000*Constants.customerHomePageInterval);
            Log.d(TAG, "running: ");
        }else
        {
            Log.d(TAG, "runningNot: ");
            if(isAlertTrue)

                alertProgress.alertPositiveOnclick(this, Constants.mqttErrorMsg, getString(R.string.system_error), getString(R.string.ok), new DialogInterfaceListner() {
                    @Override
                    public void dialogClick(boolean isClicked) {
                        noProviderAvailable(Constants.mqttErrorMsg);

                    }
                });
            isAlertTrue = false;
            // handler.removeCallbacks(runnableCode);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        handler.removeCallbacks(runnableCode);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mqttManager.unSubscribeToTopic(MqttEvents.Provider.value + "/" + manager.getSID());
        mqttManager.onModelResponseSet();
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            nestedScrollViewProList.fullScroll(View.FOCUS_UP);
            nestedScrollViewProList.smoothScrollTo(0,0);
            nestedScrollViewProList.scrollTo(0,0);
            if(requestCode == Constants.FILTER_RESULT_CODE)
            {

                filteredResponseArray = (ArrayList<FilteredResponse>) data.getSerializableExtra("FilteredArray");
                Log.i(TAG, "onActivityResult: "+filteredResponseArray.size());
                filteredAdapterSet(filteredResponseArray);

            }
        }else if(resultCode == RESULT_FIRST_USER)
        {

            if(filteredResponseArray!=null && filterAdapter!=null)
            {
                filteredResponseArray.clear();
                filterAdapter.notifyDataSetChanged();
                recyclerViewFiltered.setVisibility(View.GONE);
                recyclerViewSubCat.setVisibility(View.VISIBLE);
                nestedScrollViewProList.fullScroll(View.FOCUS_UP);
                nestedScrollViewProList.smoothScrollTo(0,0);
                nestedScrollViewProList.scrollTo(0,0);

            }
        }
    }

    private void filteredAdapterSet(ArrayList<FilteredResponse> filteredResponseArray)
    {

        filterAdapter  = new FilterAdapter(ProviderList.this,filteredResponseArray,this);
        recyclerViewFiltered.setAdapter(filterAdapter);
        recyclerViewFiltered.setVisibility(View.VISIBLE);
        recyclerViewSubCat.setVisibility(View.GONE);
        nestedScrollViewProList.fullScroll(View.FOCUS_UP);
        nestedScrollViewProList.smoothScrollTo(0,0);
        nestedScrollViewProList.scrollTo(0,0);

        if(isFirstTrue)
        {
            new Handler().postDelayed(() -> {
                nestedScrollViewProList.fullScroll(View.FOCUS_UP);
                nestedScrollViewProList.smoothScrollTo(0,0);
                nestedScrollViewProList.scrollTo(0,0);
            },500);
            isFirstTrue = false;
        }



    }

    @Override
    public void noProviderAvailable(String providerNotAvailable) {
        llNoProviderAvailable.setVisibility(View.VISIBLE);
        recyclerViewProviderList.setVisibility(View.GONE);
        tvProviderNotAvailable.setText(providerNotAvailable);
    }
}
