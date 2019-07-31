package com.localgenie.filter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.model.SubCategory;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.youraddress.YourAddressActivity;
import com.ma.bubbleseekmodule.BubbleSeekBar;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.SubCategoryAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import com.pojo.FilteredPriceResponse;
import com.pojo.FilteredResponse;



public class FilterActivity extends DaggerAppCompatActivity {

    private ArrayList<SubCategory> subCategoryArrayList;

    private String TAG = FilterActivity.class.getSimpleName();
    //Toolbar
    @BindView(R.id.toolFilter)Toolbar toolFilter;
    @BindView(R.id.tv_center)TextView tvCenterText;
    @BindView(R.id.tv_skip)TextView tvResetFilter;
    @BindView(R.id.ivFilter)ImageView ivFilter;
    @BindView(R.id.rlToolImage)LinearLayout rlToolImage;

    //initializeView
    @BindView(R.id.tvFilterLocation)TextView tvFilterLocation;
    @BindView(R.id.tvFilterLocationIcon)TextView tvFilterLocationIcon;
    @BindView(R.id.tvFilterDistanceIcon)TextView tvFilterDistanceIcon;
    @BindView(R.id.tvFilterSubCategory)TextView tvFilterSubCategory;
    @BindView(R.id.categoryView)View categoryView;

    // Price
    @BindView(R.id.tvFilterPriceIcon)TextView tvFilterPriceIcon;
    @BindView(R.id.tvFilterPrice)TextView tvFilterPrice;//from
    @BindView(R.id.tvFilterMinPrice)TextView tvFilterMinPrice;//min from
    @BindView(R.id.tvFilterMinPriceFrom)TextView tvFilterMinPriceFrom;//min from $
    @BindView(R.id.etFilterMinPriceFrom)EditText etFilterMinPriceFrom;//min from $
    @BindView(R.id.tvFilterPriceTo)TextView tvFilterPriceTo;//to
    @BindView(R.id.tvFilterMaxPrice)TextView tvFilterMaxPrice;//min from
    @BindView(R.id.tvFilterMaxPriceTo)TextView tvFilterMaxPriceTo;//min from $
    @BindView(R.id.etFilterMaxPriceTo)EditText etFilterMaxPriceTo;//min from $

    @BindView(R.id.tvFilterSave)TextView tvFilterSave;

    @BindView(R.id.recyclerViewSubCategory)RecyclerView recyclerViewSubCategory;

    @BindView(R.id.seekBarBubble)BubbleSeekBar bubbleSeekBar;

    @BindView(R.id.llFilteredPrice)LinearLayout llFilteredPrice;

    @BindView(R.id.llmain)LinearLayout llMain;

    @BindView(R.id.tvDistanceKm)TextView tvDistanceKm;

    @Inject SessionManagerImpl manager;
    @Inject
    AppTypeface appTypeface;

    private double lat,lng;
    private int distance = 30;
    private boolean isDistance,isLocation,isFilteredReset;
    @Inject FilteredPriceResponse filteredPriceResponse;
    @Inject FilterContract.FilterPresent presenter;
    @Inject AlertProgress alertProgress;
    private ArrayList<FilteredResponse> filteredResponseArray;
    private SubCategoryAdapter subCategoryAdapter;
    private  String tagAddress;



    private boolean isMinMaxSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        // filteredResponseArray = new ArrayList<>();
        getIntentValue();
        toolBarSetting();
        seekBarWork();
        typeFaceValue();

    }

    private void typeFaceValue()
    {
        etFilterMinPriceFrom.setTypeface(appTypeface.getHind_regular());
        etFilterMaxPriceTo.setTypeface(appTypeface.getHind_regular());
        tvFilterMinPrice.setTypeface(appTypeface.getHind_light());
        tvFilterMaxPrice.setTypeface(appTypeface.getHind_light());
        tvFilterPriceTo.setTypeface(appTypeface.getHind_regular());
        tvFilterPrice.setTypeface(appTypeface.getHind_regular());
        tvDistanceKm.setTypeface(appTypeface.getHind_regular());
        tvFilterLocationIcon.setTypeface(appTypeface.getHind_medium());
        tvFilterDistanceIcon.setTypeface(appTypeface.getHind_medium());
        tvFilterSubCategory.setTypeface(appTypeface.getHind_medium());
        tvFilterPriceIcon.setTypeface(appTypeface.getHind_medium());
        String distance = "30 "+Constants.distanceUnit;
        tvDistanceKm.setText(distance);

        //  if(Constants.serviceType==2 && Constants.bookingModel==4)
        if(Constants.bookingModel==2 || Constants.bookingModel==3 || Constants.bookingModel==4
                || Constants.bookingModel == 6)
        {
            llFilteredPrice.setVisibility(View.VISIBLE);
        }

        if(Constants.jobType != 3)
        {
            llMain.setVisibility(View.VISIBLE );
        }

    }
    private void getIntentValue() {
        subCategoryArrayList = (ArrayList<SubCategory>) getIntent().getSerializableExtra("SubCat");
        double minAmount = getIntent().getDoubleExtra("MinAmount",0);
        double maxAmount = getIntent().getDoubleExtra("MaxAmount",0);
        filteredResponseArray = (ArrayList<FilteredResponse>) getIntent().getSerializableExtra("FilteredResp");
        Log.d(TAG, "getIntentValueFiltered: "+filteredResponseArray);
        filteredPriceResponse.setMinAmount(minAmount);
        filteredPriceResponse.setMaxAmount(maxAmount);
        if(subCategoryArrayList!=null)
            subCategoryAdapter = new SubCategoryAdapter(this,subCategoryArrayList,false,null);


        if(filteredResponseArray.size()>0)
        {
            for(int i =0;i< filteredResponseArray.size();i++)
            {
                if(filteredResponseArray.get(i).getType()==1)
                {
                   // tvFilterLocation.setText(filteredResponseArray.get(i).getGeneralName());
                    tagAddress = filteredResponseArray.get(i).getGeneralName();
                    isLocation = true;
                    lat = filteredResponseArray.get(i).getLat();
                    lng = filteredResponseArray.get(i).getLng();

                }

                if(filteredResponseArray.get(i).getSelectedPriceMin()>0)
                {
                    String minPr = filteredResponseArray.get(i).getSelectedPriceMin()+"";
                    etFilterMinPriceFrom.setText(minPr);
                    filteredPriceResponse.setMin(true);
                    filteredPriceResponse.setMinPrice(filteredResponseArray.get(i).getSelectedPriceMin());

                }
                if(filteredResponseArray.get(i).getSelectedPriceMax()>0) {
                    String maxPr = filteredResponseArray.get(i).getSelectedPriceMax()+"";
                    filteredPriceResponse.setMax(true);
                    filteredPriceResponse.setMaxPrice(filteredResponseArray.get(i).getSelectedPriceMax());
                    etFilterMaxPriceTo.setText(maxPr);

                }



                if(filteredResponseArray.get(i).getType()==3)
                {
                    for(int j = 0;j<subCategoryArrayList.size();j++)
                    {
                        if(subCategoryArrayList.get(j).getId().equals(filteredResponseArray.get(i).getSubCatId()))
                        {
                            subCategoryArrayList.get(j).setCategorySelected(true);

                            break;
                        }
                    }
                }
                if(filteredResponseArray.get(i).getType()==2)
                {

                    String[] distanceKm = filteredResponseArray.get(i).getGeneralName().split(" ");
                    String[] splitDistance = distanceKm[0].split("-");
                    distance = Integer.parseInt(splitDistance[1]);
                    //Constants.distance = distance;
                    isDistance = true;
                    bubbleSeekBar.setProgress(Float.parseFloat(splitDistance[1]));
                }

            }
        }
        if(!Constants.filteredAddress.equals(""))
        {
            tvFilterLocation.setText(Constants.filteredAddress);
            lat = Constants.filteredLat;
            lng = Constants.filteredLng;

        }else
        {
            lat = Double.parseDouble(manager.getLatitude());
            lng = Double.parseDouble(manager.getLongitude());
            tvFilterLocation.setText(manager.getAddress());
        }

        setAdapterGrid();
    }

    private void setAdapterGrid() {
        if(subCategoryArrayList!=null)

        {
            if(subCategoryArrayList.size()>0)
            {
                recyclerViewSubCategory.setVisibility(View.VISIBLE);
                tvFilterSubCategory.setVisibility(View.VISIBLE);
                categoryView.setVisibility(View.VISIBLE);
                recyclerViewSubCategory.setLayoutManager(new GridLayoutManager(this,2));
                recyclerViewSubCategory.setAdapter(subCategoryAdapter);
                subCategoryAdapter.notifyDataSetChanged();
            }
        }

        String minPrice = getString(R.string.min)+" "+Constants.currencySymbol+filteredPriceResponse.getMinAmount();
        String maxPrice = getString(R.string.max)+" "+Constants.currencySymbol+filteredPriceResponse.getMaxAmount();
        tvFilterMinPrice.setText(minPrice);
        tvFilterMaxPrice.setText(maxPrice);
        etFilterMaxPriceTo.addTextChangedListener(editTextMaxTo());
        etFilterMinPriceFrom.addTextChangedListener(editTextMinFrom());
    }


    private void seekBarWork() {
        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

                if(progress<30)
                {
                    distance = progress;
                    isDistance = true;
                }

                Log.w(TAG, "getProgressOnFinally: "+distance);
            }
        });
    }
    //chandrakanta771950@gmail.com
    private void toolBarSetting()
    {
        ivFilter.setVisibility(View.GONE);
        rlToolImage.setVisibility(View.GONE);
        tvResetFilter.setVisibility(View.VISIBLE);
        tvResetFilter.setText(getString(R.string.reset));
        tvCenterText.setText(getString(R.string.filter));
        setSupportActionBar(toolFilter);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvCenterText.setTypeface(appTypeface.getHind_semiBold());
        toolFilter.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolFilter.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    @OnClick({R.id.tvFilterLocation,R.id.tv_skip})
    public void address(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_skip:
                Log.d(TAG, "addressSkip: ");
                resetFilter();
                break;
            case R.id.tvFilterLocation:
                Intent intent = new Intent(this, YourAddressActivity.class);
                intent.putExtra("isNotFromAddress",false);
                startActivityForResult(intent,Constants.ADDRESS_RESULT_CODE);
                overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
                break;
        }

    }

    private void resetFilter() {
        isFilteredReset = true;
        isLocation = false;
        isDistance = false;
        subCategoryAdapter.subCatName = null;
        filteredPriceResponse.setMin(false);
        filteredPriceResponse.setMax(false);
        filteredPriceResponse.setMinPrice(0);
        filteredPriceResponse.setMaxPrice(0);

        etFilterMinPriceFrom.setText("");
        etFilterMaxPriceTo.setText("");
        subCategoryAdapter.resetAdapter();
        tagAddress = "";

        for(int j = 0;j<subCategoryArrayList.size();j++)
        {
            subCategoryArrayList.get(j).setCategorySelected(false);
        }
        Constants.lat = Double.parseDouble(manager.getLatitude());
        Constants.lng = Double.parseDouble(manager.getLongitude());
        Constants.distance = 30;
        Constants.subCatId = "";
        Constants.maxPrice = filteredPriceResponse.getMaxAmount();
        Constants.minPrice = filteredPriceResponse.getMinAmount();

        subCategoryAdapter.notifyDataSetChanged();

        bubbleSeekBar.setProgress(30);

    }

    @OnClick(R.id.tvFilterSave)
    public void saveClicked()
    {
        String amountName;
        /* */

        filteredResponseArray.clear();
        if(isLocation)
        {
            FilteredResponse responseAdd = new FilteredResponse(lat,lng,"",tagAddress,1,0,0);
            filteredResponseArray.add(responseAdd);
            Constants.lat = lat;
            Constants.lng = lng;
        }
        if(isDistance)
        {
            String distanceKm = "0-"+distance+" "+Constants.distanceUnit;
            FilteredResponse responseDis = new FilteredResponse(lat,lng,"",distanceKm,2,0,0);
            filteredResponseArray.add(responseDis);
            Constants.distance = distance;
        }

        if(subCategoryAdapter.subCatName!=null)
        {
            FilteredResponse responseSubCat = new FilteredResponse(lat,lng,subCategoryAdapter.subCatId,subCategoryAdapter.subCatName,3,0,0);
            filteredResponseArray.add(responseSubCat);
            Constants.subCatId = subCategoryAdapter.subCatId;
        }
        boolean isMin = filteredPriceResponse.isMin();
        boolean isMax = filteredPriceResponse.isMax();
        double minPrice = 0;
        if(filteredPriceResponse.getMinPrice()>0)
            minPrice = Utility.round(filteredPriceResponse.getMinPrice(),2);
        double maxPrice = 0;
        if(filteredPriceResponse.getMaxPrice()>0)
            maxPrice = Utility.round(filteredPriceResponse.getMaxPrice(),2);

        Log.w(TAG, "saveClickedmin: "+isMin+" isMax "+isMax+" min "+minPrice+" max "+maxPrice);
        /*if(minPrice>maxPrice){
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage("Max price should be greater than min price")

                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            return;
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }*/
        if(isMin && isMax)
        {
            amountName = Constants.currencySymbol+minPrice+" - "+Constants.currencySymbol+maxPrice;
            FilteredResponse responseMinMax = new FilteredResponse(lat,lng,"",amountName,4,
                    minPrice,maxPrice);
            filteredResponseArray.add(responseMinMax);
            Constants.minPrice = minPrice;
            Constants.maxPrice = maxPrice;
        }
        else if (isMin)
        {
            amountName = "From "+Constants.currencySymbol+minPrice;
            FilteredResponse responseMinMax = new FilteredResponse(lat,lng,"",amountName,4
                    ,minPrice,0);
            filteredResponseArray.add(responseMinMax);
            Constants.minPrice = minPrice;
            Constants.maxPrice = filteredPriceResponse.getMaxAmount();
        }else if (isMax)
        {
            amountName = "To "+Constants.currencySymbol+maxPrice;
            FilteredResponse responseMinMax = new FilteredResponse(lat,lng,"",amountName,4
                    ,0,maxPrice);
            filteredResponseArray.add(responseMinMax);
            Constants.maxPrice = maxPrice;
            Constants.minPrice = filteredPriceResponse.getMinAmount();
        }else
        {

            if(!etFilterMinPriceFrom.getText().toString().equals("") && !etFilterMaxPriceTo.getText().toString().equals(""))
                isMinMaxSelected = false;
            Constants.maxPrice = filteredPriceResponse.getMaxAmount();
            Constants.minPrice = filteredPriceResponse.getMinAmount();
        }

        final Intent intent = new Intent();
        if(filteredResponseArray.size()>0)
        {
            isFilteredReset = false;
            if(isMinMaxSelected){
                intent.putExtra("FilteredArray",filteredResponseArray);
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
            }else
            {
                alertProgress.alertPositiveOnclick(FilterActivity.this,getString(R.string.filterWillNotForWork), getString(R.string.system_error),getString(R.string.ok), new DialogInterfaceListner() {
                    @Override
                    public void dialogClick(boolean isClicked) {
                        intent.putExtra("FilteredArray",filteredResponseArray);
                        setResult(RESULT_OK,intent);
                        finish();
                        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
                    }
                });
            }

        }else
        {
            setResult(RESULT_FIRST_USER,intent);
            finish();
        }

        //  onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        //  super.onBackPressed();
        if(isFilteredReset)
        {
            Intent intent = new Intent();
            setResult(RESULT_FIRST_USER,intent);
        }
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);

    }
    private TextWatcher editTextMinFrom()
    {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String min = etFilterMinPriceFrom.getText().toString();

                if(etFilterMaxPriceTo.getText().toString().isEmpty())
                {
                    filteredPriceResponse.setMaxPrice(0.0);
                }
                presenter.onMinPriceSet(filteredPriceResponse,min);

            }
        };
    }


    private TextWatcher editTextMaxTo()
    {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                String max = etFilterMaxPriceTo.getText().toString();
                if(etFilterMinPriceFrom.getText().toString().isEmpty())
                {
                    filteredPriceResponse.setMinPrice(0.0);
                }
                presenter.onMaxPriceSet(filteredPriceResponse,max);

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == Constants.ADDRESS_RESULT_CODE)
            {
                if(data!=null)
                {
                    lat = data.getDoubleExtra("lat",0.0);
                    lng = data.getDoubleExtra("lng",0.0);
                    String bookingAddress = data.getStringExtra("AddressLine1");
                    String bookingAddress2 = data.getStringExtra("AddressLine2");
                    tagAddress = data.getStringExtra("TAGAS");
                    Log.d(TAG, "onActivityResult: "+lat+" lang "+lng+" add "
                            +bookingAddress +" add2 "+bookingAddress2);
                    tvFilterLocation.setText(bookingAddress);
                    Constants.filteredAddress = bookingAddress;
                    // manager.setAddress(bookingAddress);
                    isLocation = true;
                }
            }
        }
    }
}

