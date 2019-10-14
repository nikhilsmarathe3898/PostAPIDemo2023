package com.localgenie.addTocart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.Login.LoginActivity;
import com.localgenie.R;
import com.localgenie.confirmbookactivity.ConfirmBookActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.CategoryAndServiceAdapter;
import adapters.SubServicesAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.pojo.CartModifiedData;
import com.pojo.ServiceResponse;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

public class AddToCart extends DaggerAppCompatActivity implements AddToCartContractor.ContractView{


    @BindView(R.id.toolProvider)Toolbar toolProvider;
    @BindView(R.id.numOfService)TextView numOfService;
    @BindView(R.id.tvOr)TextView tvOr;
    @BindView(R.id.tv_price)TextView tv_price;
    @BindView(R.id.tvAdditionalFee)TextView tvAdditionalFee;
    @BindView(R.id.tvCheckout)TextView tvCheckout;
    @BindView(R.id.toolBarTitle)TextView toolBarTitle;

    @BindView(R.id.glHireNow)GridLayout glHireNow;
    @BindView(R.id.tvHireHourly)TextView tvHireHourly;
    @BindView(R.id.llAddToCartHr)LinearLayout llAddToCartHr;
    @BindView(R.id.tvCartRemoveHr)ImageView tvCartRemoveHr;
    @BindView(R.id.tvCartNoHr)TextView tvCartNoHr;
    @BindView(R.id.tvCartAddHr)ImageView tvCartAddHr;
    @BindView(R.id.tvAddtoCartHourPrice)TextView tvAddtoCartHourPrice;
    @BindView(R.id.tvAddToCartHourlyInfo)TextView tvAddToCartHourlyInfo;

    @BindView(R.id.rv_services)RecyclerView rvServices;


    @BindView(R.id.progressBar)ProgressBar progressBar;

    @BindView(R.id.checkOutLayout)RelativeLayout checkOutLayout;


    ///////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.llAddToCartService)LinearLayout llAddToCartService;
    @BindView(R.id.nestedScrollAddToCart)NestedScrollView nestedScrollAddToCart;
    private SubServicesAdapter adapter;

    ArrayList<ServiceResponse.ServiceData> subServicesList = new ArrayList<>();
    ////////////////////////////////////////////////////////////////////////////////////

    @Inject
    AppTypeface appTypeface;
    @Inject
    SessionManagerImpl manager;

   /* @Inject
    LinearLayoutManager linearLayoutManager;*/

    @Inject
    AlertProgress alertProgress;

    @Inject
    AddToCartContractor.presenter presenter;

    private CategoryAndServiceAdapter subServiceAdapter;
    private  ArrayList<ServiceResponse.ServiceDataResponse> servicesList = new ArrayList<>();

    public static String messageCart;
    private boolean isQuantityAdded = false;
    private int toTalQuantity;
    public static boolean isCartPresent = false;
    public static boolean isGuestLogin = false;
    public boolean isAdapterCalled = true;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);
        ButterKnife.bind(this);
        //rv_services.setNestedScrollingEnabled(false);
        isCartPresent = false;
        getIntentValue();
        linearLayoutManager = new LinearLayoutManager(this);
        ToolBarSetUp();
        recyclerViewSet();
        typeFace();
    }

    private void getIntentValue()
    {
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            // services = (ArrayList<ProviderDetailsResponse.ProviderResponseDetails.ServiceCategory>) bundle.getSerializable("PROSERVICELIST");
            String name = bundle.getString("NAME");
        }

        if(Constants.bookingModel == 2 || Constants.bookingModel == 6)
        {
            tvCartRemoveHr.setVisibility(View.INVISIBLE);
            tvCartAddHr.setVisibility(View.INVISIBLE);
        }

    }

    private void recyclerViewSet() {

        onShowProgress();
        presenter.onSubServiceApiCalled(Constants.catId,Constants.proId);

    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(() -> presenter.getCartServiceCall(Constants.catId),1000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isAdapterCalled = true;
        isGuestLogin = manager.getGuestLogin();
        Log.d("TAG", "onResume: "+isGuestLogin +" "+manager.getGuestLogin());

    }


    private void ToolBarSetUp() {

        setSupportActionBar(toolProvider);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolProvider.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolProvider.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

    }

    private void typeFace() {

        numOfService.setTypeface(appTypeface.getHind_regular());
        tvOr.setTypeface(appTypeface.getHind_regular());
        tv_price.setTypeface(appTypeface.getHind_semiBold());
        tvAdditionalFee.setTypeface(appTypeface.getHind_regular());
        tvCheckout.setTypeface(appTypeface.getHind_bold());
        toolBarTitle.setTypeface(appTypeface.getHind_semiBold());
        tvHireHourly.setTypeface(appTypeface.getHind_regular());
        tvCartNoHr.setTypeface(appTypeface.getHind_semiBold());
        tvAddtoCartHourPrice.setTypeface(appTypeface.getHind_semiBold());
        tvAddToCartHourlyInfo.setTypeface(appTypeface.getHind_regular());

        if(Constants.serviceType==2 || Constants.serviceType==1)
        {
            String hourPrice = getString(R.string.hireByTheHour)+" @ "+Constants.pricePerHour+" /hr";
            tvAddtoCartHourPrice.setText(hourPrice);

            if(Constants.bookingModel!=1 && Constants.bookingModel!=5)
                glHireNow.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.checkOutLayout,R.id.tvHireHourly,R.id.tvCartRemoveHr,R.id.tvCartAddHr})
    public void onContinueClicked(View v)
    {
        switch (v.getId())
        {
            case R.id.checkOutLayout:
                if(isQuantityAdded)
                {
                    Intent intent = new Intent(this, ConfirmBookActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
                }else
                    Toast.makeText(AddToCart.this,"Please add service to proceed",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvHireHourly:
                if(isGuestLogin)
                {
                    onGuestToLogin();
                }else
                {
                    if(!isCartPresent)
                    {
                        hourlyServiceCalled();
                    }else
                    {
                        alertProgress.alertPositiveNegativeOnclick(this, messageCart, getString(R.string.itemsAlreadyInCart),getResources().getString(R.string.ok), getResources().getString(R.string.cancel),false, isClicked -> {
                            if(isClicked)
                            {
                                isCartPresent = true;

                                hourlyServiceCalled();
                            }

                        });
                    }

                }

                break;
            case R.id.tvCartRemoveHr:
                if(toTalQuantity==1)
                {
                    llAddToCartHr.setVisibility(View.GONE);
                    tvHireHourly.setVisibility(View.VISIBLE);
                }
                onCartModified("",2,2);
                break;
            case R.id.tvCartAddHr:
                onCartModified("",1,2);
                break;
        }


    }

    private void hourlyServiceCalled()
    {
        isQuantityAdded = true;
        llAddToCartHr.setVisibility(View.VISIBLE);
        tvHireHourly.setVisibility(View.GONE);
        layoutVisibility(true);
        //   checkOutLayout.setVisibility(View.VISIBLE);
        onCartModified("",1,2);
    }

    private void layoutVisibility(boolean b) {

        float alpha;
        int height;
        if(b)
        {
            alpha =1.0f;
            height = 0;
        }
        else
        {
            height =  checkOutLayout.getHeight();
            alpha =0.0f;
        }

        checkOutLayout.animate()
                .translationY(height)
                .alpha(alpha)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if(b)
                            checkOutLayout.setVisibility(View.VISIBLE);
                        else
                            checkOutLayout.setVisibility(View.GONE);

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);
        // overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
    }

    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message)
    {
        alertProgress.alertPositiveOnclick(this, message, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(AddToCart.this,manager);
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
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onViewMore(double unit, String name, String desc) {
        showServiceDesc(unit,name,desc);
    }

    private void showServiceDesc(double unit, String name, String desc)
    {
        Bundle bundle = new Bundle();
        DescDialogFragment dialogFragment=new DescDialogFragment();
        bundle.putDouble("Price",unit);
        bundle.putString("ServiceName",name);
        bundle.putString("Description",desc);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getSupportFragmentManager(),"SERVICEDESCRIPTION");
    }

    @Override
    public void onCartModified(String serviceId, int action, int serviceType) {

        onShowProgress();
        presenter.addSubCartData(Constants.catId,serviceId,action,serviceType);
    }

    @Override
    public void onCartModifiedSuccess(CartModifiedData.DataSelected data)
    {

        if(data != null) {
            onDataSetToTextView(data, true);
        }
    }

    private void onDataSetToTextView(CartModifiedData.DataSelected data,boolean isService)
    {
        String quantityService;
        if(isService)
            quantityService = data.getTotalQuntity() +" "+getString(R.string.services);
        else
        {
            quantityService = data.getTotalQuntity() +" "+getString(R.string.per_hour);
            toTalQuantity = data.getTotalQuntity();
            if(data.getTotalQuntity()==0)
            {
                llAddToCartHr.setVisibility(View.GONE);
                tvHireHourly.setVisibility(View.VISIBLE);
            }
            tvCartNoHr.setText(""+data.getTotalQuntity()+"");

        }
        numOfService.setText(quantityService);
        double totalAmount = data.getTotalAmount();
        // @SuppressLint("DefaultLocale") String amountWithCurrency = data.getCurrencySymbol()+" "+ String.format("%.2f",(float)totalAmount);
        Utility.setAmtOnRecept(totalAmount,tv_price,Constants.currencySymbol);
        //  tv_price.setText(amountWithCurrency);
        if(data.getTotalQuntity()>0) {
            layoutVisibility(true);
            // checkOutLayout.setVisibility(View.VISIBLE);

            isQuantityAdded = true;
        }
        else {
            isQuantityAdded = false;
            // checkOutLayout.setVisibility(View.INVISIBLE);
            layoutVisibility(false);
            //  nestedScrollAddToCart.scrollTo(0, 0);
        }
    }
    @Override
    public void onAlreadyAddedCart(CartModifiedData.DataSelected data, ArrayList<ServiceResponse.ServiceDataResponse> serviceResponse, boolean isFixed)
    {
        if(isFixed)
        {
            onDataSetToTextView(data,true);
            Log.d("TAG", "onAlreadyAddedCart: "+data.getTotalQuntity());
            if(data.getTotalQuntity()>0){
                isQuantityAdded = true;
                layoutVisibility(true);
                //  checkOutLayout.setVisibility(View.VISIBLE);

                for(int i =0;i<serviceResponse.size();i++)
                {

                    for(ServiceResponse.ServiceData service : serviceResponse.get(i).getService())
                    {
                        for(CartModifiedData.ItemSelected selectedItem : data.getItem())
                        {
                            if(service.get_id().equals(selectedItem.getServiceId()))
                            {
                                service.setTempQuant(selectedItem.getQuntity());
                            }
                        }
                    }

                }
                //  subServiceAdapter.notifyDataSetChanged();
                //  if(subServiceAdapter.adapter!=null)
                //  if(adapter!=null)
                if(isAdapterCalled)
                {
                    // adapter.subServiceList(subServicesList);
                    isAdapterCalled = false;
                    inflateAtRunTime(serviceResponse);

                    //  adapter.notifyDataSetChanged();

                }

                //  subServiceAdapter.adapter.notifyDataSetChanged();
            }else
            {
                isQuantityAdded = false;
                //  checkOutLayout.setVisibility(View.INVISIBLE);
                layoutVisibility(false);
                if(isAdapterCalled)
                {
                    isAdapterCalled = false;
                    inflateAtRunTime(serviceResponse);
                }
                //  nestedScrollAddToCart.scrollTo(0, 0);
            }

        }else
        {
            toTalQuantity = data.getTotalQuntity();
            if(toTalQuantity>0)
            {
                onDataSetToTextView(data,false);
                llAddToCartHr.setVisibility(View.VISIBLE);
                tvHireHourly.setVisibility(View.GONE);
                //  findViewById({your NestedScrollView resource id}).scrollTo(0, 0);
            }

        }

    }

    @Override
    public void removeHourly() {

        tvHireHourly.setVisibility(View.VISIBLE);
        // CardCartAddHr.setVisibility(View.GONE);
        llAddToCartHr.setVisibility(View.GONE);
    }

    @Override
    public void addHourly(CartModifiedData.DataSelected data) {

        if(data != null)
        onDataSetToTextView(data,false);
        for(int i = 0;i<servicesList.size();i++)
        {
            servicesList.get(i).getService();
            for(ServiceResponse.ServiceData serviceData : servicesList.get(i).getService())
            {
                serviceData.setTempQuant(0);
            }

        }
       // if(adapter!=null)
            adapter.notifyDataSetChanged();
        // subServiceAdapter.adapter.notifyDataSetChanged();
    }

    @Override
    public void removeFixed()
    {
        // tvHireHourly.setVisibility(View.GONE);
        // llAddToCartHr.setVisibility(View.VISIBLE);
    }
    @Override
    public void onAlreadyCartPresent(String message, boolean isCartPre)
    {
        isCartPresent = isCartPre;
        messageCart = message;

    }

    @Override
    public void onGuestToLogin() {
        Intent intent = new Intent(AddToCart.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void showAlert(final int position, final int quantity)
    {
       /* alertProgress.alertPositiveNegativeOnclick(AddToCart.this, messageCart, getString(R.string.itemsAlreadyInCart),getResources().getString(R.string.ok), getResources().getString(R.string.cancel),false , new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {

                if(isClicked)
                {
                    isCartPresent = false;
                    subServiceAdapter.callCategory(position,quantity);
                }

            }
        });*/
    }


    @Override
    public void onSuccessSubService(ArrayList<ServiceResponse.ServiceDataResponse> response)
    {
        if(response.size()>0)
        {
            servicesList.addAll(response);
            //  subServiceAdapter.notifyDataSetChanged();
            inflateAtRunTime(servicesList);
            //  nestedScrollAddToCart.smoothScrollTo(0,0);
        }
    }

    private void inflateAtRunTime(ArrayList<ServiceResponse.ServiceDataResponse> servicesList)
    {
        llAddToCartService.removeAllViews();
        subServicesList.clear();
        if(servicesList.size()>0)
        {

            for(int position = 0 ; position<servicesList.size(); position++)
            {
                TextView categoryHeader,categoryDesc;
                RelativeLayout rl_heading_subCat;
                View view = LayoutInflater.from(AddToCart.this).inflate(R.layout.category_header,nestedScrollAddToCart,false);
                llAddToCartService.addView(view);
                linearLayoutManager = new LinearLayoutManager(AddToCart.this);
                categoryHeader=view.findViewById(R.id.categoryHeader);
                categoryDesc=view.findViewById(R.id.categoryDesc);
                rl_heading_subCat=view.findViewById(R.id.rl_heading_subCat);
                rvServices=view.findViewById(R.id.rv_services);
                categoryHeader.setTypeface(appTypeface.getHind_semiBold());
                categoryDesc.setTypeface(appTypeface.getHind_regular());
                rvServices.setNestedScrollingEnabled(false);
                if(servicesList.get(position).getSub_cat_id().isEmpty()){
                    rl_heading_subCat.setVisibility(View.GONE);
                }
                rvServices.setLayoutManager(linearLayoutManager);
                subServicesList.addAll(servicesList.get(position).getService());
                adapter = new SubServicesAdapter(AddToCart.this, servicesList.get(position).getService(),this);
                rvServices.setAdapter(adapter);
                categoryHeader.setText(servicesList.get(position).getSub_cat_name());
                categoryDesc.setText(servicesList.get(position).getSub_cat_desc());

            }
        }
    }

}
