package com.localgenie.providerdetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.localgenie.Login.LoginActivity;
import com.localgenie.R;
import com.localgenie.addTocart.AddToCart;
import com.localgenie.chatting.ChattingActivity;
import com.localgenie.confirmbookactivity.ConfirmBookActivity;
import com.localgenie.inCallOutCall.TimeSlots;
import com.localgenie.model.youraddress.YourAddrData;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.CircleTransform;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.LangExpertiseAdapter;
import adapters.RatingReviewAdapter;
import adapters.SlotsTimingAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import com.pojo.ProviderDetailsResponse;
import com.pojo.ReviewPojo;
import com.pojo.Slots;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

public class ProviderDetails extends DaggerAppCompatActivity implements ProviderDetailsContract.ProviderView
        , OnMapReadyCallback
{

    @BindView(R.id.app_bar)AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_layout)CollapsingToolbarLayout collapsingToolbarLayout;
    // @BindView(R.id.youtubeThumbNail)ImageView youtubeThumbNail;
    @BindView(R.id.toolProvider)Toolbar toolProvider;
    @BindView(R.id.toolBarTitle)TextView toolBarTitle;
    @BindView(R.id.ivProDtlsPic)ImageView ivProDtlsPic;
    @BindView(R.id.rlBook)RelativeLayout rlBook;
    @BindView(R.id.tvProBook)TextView tvProBook;
    @BindView(R.id.progressBarPro)ProgressBar progressBarPro;
    @BindView(R.id.tvProviderConvinence)TextView tvProviderConvinence;

    /*BID*/

    @BindView(R.id.rlProDetBid)RelativeLayout rlProDetBid;
    @BindView(R.id.tvBidProHire)TextView tvBidProHire;
    @BindView(R.id.tvBidProAmount)TextView tvBidProAmount;
    @BindView(R.id.tvBidProAmountFee)TextView tvBidProAmountFee;
    @BindView(R.id.tvBidProViewMore)TextView tvBidProViewMore;
    @BindView(R.id.tvBidProDesc)TextView tvBidProDesc;


    @BindView(R.id.tvPrice)TextView tvPrice;
    @BindView(R.id.tvProviderPerHour)TextView tvPriceUnitPer;

    @BindView(R.id.llinflateAtRunTime)LinearLayout llinflateAtRunTime;

    @BindView(R.id.rlProRateReview)LinearLayout rlProRateReview;
    @BindView(R.id.llRatingLog)LinearLayout llRatingLog;
    @BindView(R.id.llReviewLog)LinearLayout llReviewLog;
    @BindView(R.id.tvProviderAge)TextView tvProviderAge;
    @BindView(R.id.tvjobProLocation)TextView tvjobProLocation;
    @BindView(R.id.tvProReviews)TextView tvProReviews;
    @BindView(R.id.viewReview)View viewReview;
    @BindView(R.id.veProDivider7)View veProDivider7;
    @BindView(R.id.tvRevwAvrgRtin)TextView tvRevwAvrgRtin;
    @BindView(R.id.tvavgrtnTotalRev)TextView tvavgrtnTotalRev;
    @BindView(R.id.rtReview)RatingBar rtReview;
    @BindView(R.id.tvRatings)TextView tvRatings;
    @BindView(R.id.tvReviews)TextView tvReviews;
    @BindView(R.id.recyclerViewRating)RecyclerView recyclerViewRating;
    @BindView(R.id.recyclerViewReview)RecyclerView recyclerViewReview;

    @BindView(R.id.tvReviewMe)TextView tvReviewMe;
    @BindView(R.id.tvAboutMe)TextView tvAboutMe;

    @BindView(R.id.nestedScrollView)NestedScrollView nestedScrollView;


    @BindView(R.id.tvProviderNoJob)TextView tvProviderNoJob;
    @BindView(R.id.tvProviderJob)TextView tvProviderJob;
    @BindView(R.id.tvProviderAvgRating)TextView tvProviderAvgRating;
    @BindView(R.id.tvProviderRating)TextView tvProviderRating;
    @BindView(R.id.tvProviderNoReviews)TextView tvProviderNoReviews;
    @BindView(R.id.tvProviderJobReviews)TextView tvProviderJobReviews;
    @BindView(R.id.tvProviderName)TextView tvProviderName;
    @BindView(R.id.llPerHour)LinearLayout llPerHour;
    @BindView(R.id.providerProgress)ProgressBar providerProgress;


    @BindView(R.id.rrljobimage)RelativeLayout rrljobimage;
    @BindView(R.id.veProDivider8)View veProDivider8;
    @BindView(R.id.tvjobimage)TextView tvjobimage;
    @BindView(R.id.nojobimage)TextView nojobimage;
    @BindView(R.id.horizontaljobimageView)HorizontalScrollView horizontaljobimageView;
    @BindView(R.id.MainContainerjob)LinearLayout MainContainerjob;


    @BindView(R.id.llConsultation)LinearLayout llConsultation;
    @BindView(R.id.tvInCallAddress1)TextView tvInCallAddress1;
    @BindView(R.id.tvInCallAddress2)TextView tvInCallAddress2;
    // @BindView(R.id.ivInCallLocation)ImageView ivInCallLocation;
    @BindView(R.id.mRecyclerViewSlot)RecyclerView mRecyclerViewSlot;
    @BindView(R.id.llConfirmSlot)LinearLayout llConfirmSlot;


    @BindView(R.id.tvSlotsProviders)TextView tvSlotsProviders;


    @Inject
    AppTypeface appTypeface;
    @Inject ProviderDetailsContract.ProviderPresenter presenter;
    @Inject
    SessionManagerImpl manager;
    @Inject
    AlertProgress alertProgress;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int pageCount = 0;
    private double bidAmount = 0;
    private boolean loading = true;
    private  LinearLayoutManager linearLayoutManagerReview;
    private LangExpertiseAdapter langAdapter,expertiseAdapter;
    private String proId;
    private Bundle intentBundle;
    private String[] lanArrayExpertise;
    private String name;
    private boolean isProfileView = false;
    private boolean isBidding = false;
    private long bid;
    private int bidStatus = 0;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;


    private RatingReviewAdapter ratinReviewAdaptr;


    private ArrayList<ProviderDetailsResponse.ProviderResponseDetails.ReviewList>rateReview = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_details);
        ButterKnife.bind(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        getIntentValue();
        typefaceValue();
        valueForAdapter();
        setToolBar();
        if(isBidding && bidStatus == 17)
            fabIcon();
        callType();


    }

    private void callType() {

        if(Constants.jobType == 1)
        {
            llConsultation.setVisibility(View.VISIBLE);
            llConfirmSlot.setVisibility(View.GONE);
            mRecyclerViewSlot.setNestedScrollingEnabled(false);
            tvProviderConvinence.setVisibility(View.VISIBLE);
        }else if(Constants.jobType == 3 || Constants.callType == 3)
            tvProviderConvinence.setVisibility(View.VISIBLE);
        else
            tvProviderConvinence.setVisibility(View.GONE);

        onLoadMore();
    }

    private void fabIcon() {
        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChattingActivity.class);
            if(bidStatus == 1 || bidStatus == 2
                    || bidStatus == 3 || bidStatus == 6 || bidStatus == 7
                    || bidStatus == 8 || bidStatus == 9 || bidStatus == 17)
            {
                intent.putExtra("isChating",true);
            }
            intent.putExtra("STATUSCODE",bidStatus);
            intent.putExtra("CurrencySymbol",Constants.currencySymbol);
            intent.putExtra("AMOUNT",bidAmount);
            intent.putExtra("CallType",Constants.jobType);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
        });
    }

    private void onLoadMore() {

        recyclerViewReview.setNestedScrollingEnabled(false);
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)(v,scrollX,scrollY,oldScrollX,oldScrollY)-> {
            if(v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    visibleItemCount = linearLayoutManagerReview.getChildCount();
                    totalItemCount = linearLayoutManagerReview.getItemCount();
                    pastVisiblesItems = linearLayoutManagerReview.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            providerProgress.setVisibility(View.VISIBLE);
                            pageCount++;
                            presenter.callReviewApi(pageCount, proId);
                        }
                    }
                }
            }
        });

    }

    private void setToolBar() {


        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTypeface(appTypeface.getHind_semiBold());
        setSupportActionBar(toolProvider);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolProvider.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolProvider.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void valueForAdapter()
    {
        linearLayoutManagerReview = new LinearLayoutManager(this);
        recyclerViewReview.setLayoutManager(linearLayoutManagerReview);

        ratinReviewAdaptr = new RatingReviewAdapter(this, rateReview, true);
        recyclerViewReview.setAdapter(ratinReviewAdaptr);

        tvProBook.setOnClickListener(view -> {
            Intent intent;
            if(Constants.bookingModel==4)
            {
                if(manager.getGuestLogin())
                    intent = new Intent(this, LoginActivity.class);
                else
                {
                    if(Constants.jobType !=2) //if telecall and incall
                    {
                        intent = new Intent(ProviderDetails.this, TimeSlots.class);

                        intent.putExtras(intentBundle);

                    }
                    else
                        intent = new Intent(ProviderDetails.this, ConfirmBookActivity.class);
                }
            }else
            {
                if(Constants.jobType !=2)
                {
                    intent = new Intent(ProviderDetails.this, TimeSlots.class);
                    intent.putExtras(intentBundle);
                }else
                {
                    intent = new Intent(ProviderDetails.this, AddToCart.class);
                    intent.putExtras(intentBundle);
                }

            }
            startActivity(intent);
            overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);

        });

    }

    private void getIntentValue() {
        intentBundle = new Bundle();
        if(Constants.jobType==2)
        {
            if(Constants.bookingModel == 2 || Constants.bookingModel == 3|| Constants.bookingModel == 4
                    || Constants.bookingModel == 6)
            {
                llPerHour.setVisibility(View.VISIBLE);
                Utility.setAmtOnRecept(Constants.pricePerHour,tvPrice,Constants.currencySymbol);
            }
            if(Constants.bookingTypeNowSchedule !=0 && Constants.bookingTypeNowSchedule == 3)
            {
                llPerHour.setVisibility(View.GONE);
            }
        }else
        {
            tvProviderAge.setVisibility(View.VISIBLE);
        }


        String bidDesc = "";

        if(getIntent().getExtras()!=null)
        {
            proId = getIntent().getStringExtra("ProId");
            isProfileView = getIntent().getBooleanExtra("isProFileView",false);
            isBidding = getIntent().getBooleanExtra("isBidding",false);
            bidDesc = getIntent().getStringExtra("bidDesc");
            bidAmount = getIntent().getDoubleExtra("bidAmount",0);
            bid = getIntent().getLongExtra("BID",0);
            bidStatus = getIntent().getIntExtra("BIDStatus",0);
            onShowProgress();
            callProvider();

        }


        if(!isProfileView)
        {
            rlBook.setVisibility(View.VISIBLE);
        }else
        {
            if (isBidding)
            {
                rlProDetBid.setVisibility(View.VISIBLE);
                tvBidProDesc.setText(bidDesc);

                if("".equals(bidDesc))
                {
                    tvBidProViewMore.setVisibility(View.GONE);
                    tvBidProDesc.setVisibility(View.GONE);
                }
            }
        }
    }

    private void callProvider() {

        if(alertProgress.isNetworkAvailable(this))
            presenter.onProviderDetailService(proId);
        else
            alertProgress.showNetworkAlert(this);
    }

    @OnClick({R.id.tvAboutMe,R.id.tvReviewMe,R.id.tvBidProHire})
    public void onAboutReviewClicked(View v)
    {
        switch (v.getId())
        {
            case R.id.tvAboutMe:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nestedScrollView.smoothScrollTo(0,llinflateAtRunTime.getTop());

                    }
                },100);
                tvAboutMe.setSelected(true);
                tvReviewMe.setSelected(false);
                break;
            case R.id.tvReviewMe:
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nestedScrollView.smoothScrollTo(0,rlProRateReview.getTop()+10);

                    }
                },100);*/
                tvAboutMe.setSelected(false);
                tvReviewMe.setSelected(true);
                break;
            case R.id.tvBidProHire:
                if(alertProgress.isNetworkAvailable(this))
                {
                    alertProgress.alertPositiveNegativeOnclick(ProviderDetails.this, getString(R.string.areYouSureYouWantOTHire) + " '" + name + "' " +
                                    getString(R.string.forThisJob), getString(R.string.hire), getString(R.string.yes), getString(R.string.no)
                            , true, isClicked -> {
                                if(isClicked)
                                {
                                    onShowProgress();
                                    presenter.hireProvider(bid,proId);
                                }
                            });

                }

                else
                    alertProgress.showNetworkAlert(this);
                break;
        }
    }

    private void typefaceValue()
    {
        tvSlotsProviders.setTypeface(appTypeface.getHind_medium());
        tvInCallAddress1.setTypeface(appTypeface.getHind_regular());
        tvInCallAddress2.setTypeface(appTypeface.getHind_regular());
        tvPriceUnitPer.setTypeface(appTypeface.getHind_light());
        tvPrice.setTypeface(appTypeface.getHind_bold());
        tvProBook.setTypeface(appTypeface.getHind_medium());
        toolBarTitle.setTypeface(appTypeface.getHind_semiBold());
        tvProReviews.setTypeface(appTypeface.getHind_medium());
        tvReviewMe.setTypeface(appTypeface.getHind_regular());
        tvAboutMe.setTypeface(appTypeface.getHind_regular());
        tvProviderJob.setTypeface(appTypeface.getHind_regular());
        tvProviderNoJob.setTypeface(appTypeface.getHind_semiBold());
        tvProviderAvgRating.setTypeface(appTypeface.getHind_semiBold());
        tvProviderRating.setTypeface(appTypeface.getHind_regular());
        tvProviderNoReviews.setTypeface(appTypeface.getHind_semiBold());
        tvProviderName.setTypeface(appTypeface.getHind_semiBold());
        tvProviderJobReviews.setTypeface(appTypeface.getHind_regular());
        tvjobimage.setTypeface(appTypeface.getHind_medium());
        tvRevwAvrgRtin.setTypeface(appTypeface.getHind_semiBold());
        tvRatings.setTypeface(appTypeface.getHind_medium());
        tvReviews.setTypeface(appTypeface.getHind_medium());
        tvavgrtnTotalRev.setTypeface(appTypeface.getHind_light());
        tvBidProHire.setTypeface(appTypeface.getHind_semiBold());
        tvBidProAmount.setTypeface(appTypeface.getHind_semiBold());
        tvBidProAmountFee.setTypeface(appTypeface.getHind_regular());
        tvBidProViewMore.setTypeface(appTypeface.getHind_regular());
        tvBidProDesc.setTypeface(appTypeface.getHind_regular());
        tvProviderAge.setTypeface(appTypeface.getHind_regular());
        tvjobProLocation.setTypeface(appTypeface.getHind_medium());
        tvProviderConvinence.setTypeface(appTypeface.getHind_regular());

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
                Utility.setMAnagerWithBID(ProviderDetails.this,manager);
            }
        });

    }

    @Override
    public void onError(String error) {
        alertProgress.alertinfo(this,error);

    }

    @Override
    public void onShowProgress()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        progressBarPro.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgress() {
        if (progressBarPro != null)
            progressBarPro.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    double latitude,longitude;
    @Override
    public void onSuccess(ProviderDetailsResponse.ProviderResponseDetails providerDataResp)
    {
        Log.d("Abc", "onSuccess: "+providerDataResp);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.profile_price_bg)
                .error(R.drawable.profile_price_bg)
                .transform(new CircleTransform(this))
                .priority(Priority.HIGH);
        Glide.with(ProviderDetails.this)
                .load(providerDataResp.getProfilePic())
                .apply(options)
                .into(ivProDtlsPic);

        intentBundle.putString("ImagePro",providerDataResp.getProfilePic());
        intentBundle.putString("ProviderId",proId);


        if(providerDataResp.getAddress().getLatitude()!=0)
        {
            latitude =  providerDataResp.getAddress().getLatitude();
            longitude =  providerDataResp.getAddress().getLongitude();
        }else
        {
            latitude =  providerDataResp.getLocation().getLatitude();
            longitude =  providerDataResp.getLocation().getLongitude();
        }

        if(Constants.jobType !=2)
        {

            showAddress(providerDataResp.getAddress());
            Constants.proLatitude = providerDataResp.getAddress().getLatitude();
            Constants.proLongitude = providerDataResp.getAddress().getLongitude();
            Constants.proAddress = providerDataResp.getAddress().getAddLine1();
        }

        mapFragment.getMapAsync(this);
        name = providerDataResp.getFirstName() + " " + providerDataResp.getLastName();
        appBarChangeListnr(name);
        Utility.setAmtOnRecept(providerDataResp.getAmount(),tvPrice,Constants.currencySymbol);
        //Utility.setAmtOnRecept(providerDataResp.getAmount(),tvProviderConvinence,Constants.currencySymbol);
        String consultancy = String.format("Consultation fee : %s %s", Constants.currencySymbol, String.format("%.2f", providerDataResp.getAmount()));
        tvProviderConvinence.setText(consultancy);
        if(providerDataResp.getAmount()!=0)
        {
            Constants.pricePerHour = providerDataResp.getAmount();
        }
        String totalBooking = providerDataResp.getTotalBooking()+" ";
        tvProviderNoJob.setText(totalBooking);
        String bookProName = getResources().getString(R.string.bookSmall) + " "+name;
        tvProBook.setText(bookProName);
        tvProviderName.setText(name);

        // ratingBar.setRating(providerDataResp.getRating());
        String avgRaating = providerDataResp.getRating()+"";
        tvProviderAvgRating.setText(avgRaating);
        String reviews = providerDataResp.getNoOfReview() + " ";// + getString(R.string.reviews);
        tvReviewMe.setText(reviews);
        tvProviderNoReviews.setText(reviews);
        // proServiceList.addAll(providerDataResp.getServices());
        rateReview.addAll(providerDataResp.getReview());
        if(providerDataResp.getRatingLog().size()>0)
        {
            rlProRateReview.setVisibility(View.VISIBLE);
            llRatingLog.setVisibility(View.VISIBLE);

        }
        if(providerDataResp.getReview().size()>0)
        {
            llReviewLog.setVisibility(View.VISIBLE);
            viewReview.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager lloutMAnager = new LinearLayoutManager(this);

        RatingAdap ratingAdap = new RatingAdap(this,providerDataResp.getRatingLog());
        recyclerViewRating.setLayoutManager(lloutMAnager);
        recyclerViewRating.setAdapter(ratingAdap);
        recyclerViewRating.setNestedScrollingEnabled(false);

        String reviewsNumber = getString(R.string.reviews)+" ("+providerDataResp.getNoOfReview()+")";
        // tvProReviews.setText(reviewsNumber);
        tvReviews.setText(reviewsNumber);
        String nnumberRatinReview = providerDataResp.getRatingLog().size()+" "+getString(R.string.rating);
        // +"   "+providerDataResp.getNoOfReview()+" "+getString(R.string.reviews);
        tvavgrtnTotalRev.setText(nnumberRatinReview);
        String rating = String.valueOf(providerDataResp.getRating());
        tvRevwAvrgRtin.setText(rating);
        rtReview.setRating(providerDataResp.getRating());

        if(isBidding)
        {
            Utility.setAmtOnRecept(bidAmount,tvBidProAmountFee,Constants.currencySymbol);

            manager.setChatBookingID(bid);
            manager.setChatProId(proId);
            manager.setProName(providerDataResp.getFirstName()+" "+providerDataResp.getLastName());

        }

        ratinReviewAdaptr.notifyDataSetChanged();


        if (providerDataResp.getWorkImage().size()>0) {

            nojobimage.setVisibility(View.GONE);


            int imgcount = providerDataResp.getWorkImage().size();
            rrljobimage.setVisibility(View.VISIBLE);

            for (int j = 0; j < imgcount; j++) {

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.doctor_image,MainContainerjob,false);
                ImageView ivmainjob;
                ivmainjob = (ImageView) view.findViewById(R.id.ivmainjob);
                String rurl = providerDataResp.getWorkImage().get(j);
                // arrayList.add(rurl);
                Log.i("MasterDetails", "imageurlmas " + rurl);
                rurl = rurl.replace(" ", "%20");


                if (!rurl.equals("")) {

                    Glide.with(ProviderDetails.this)
                            .load(rurl)
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(30)))
                            .into(ivmainjob);
                }

                ivmainjob.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(ProviderDetails.this,Image_Gallery.class);
                        intent.putStringArrayListExtra("Imagelist",providerDataResp.getWorkImage());
                        startActivity(intent);
                    }
                });


                MainContainerjob.addView(view);

            }
        } else {
            nojobimage.setVisibility(View.VISIBLE);
            veProDivider7.setVisibility(View.GONE);
        }

        if(providerDataResp.getMetaDataArr().size()>0)
        {
            tvAboutMe.setSelected(true);
            llinflateAtRunTime.setVisibility(View.VISIBLE);
            for(int i =0;i<providerDataResp.getMetaDataArr().size();i++)
            {
                ProviderDetailsResponse.MetaDataArray metaDataArray = providerDataResp.getMetaDataArr().get(i);
                TextView tvProInstrument,tvProInstruments;
                RecyclerView recyclerViewproInstrument;
                View inflatedView = LayoutInflater.from(this).inflate(R.layout.provider_rules_info,llinflateAtRunTime,false);
                llinflateAtRunTime.addView(inflatedView);

                tvProInstrument = inflatedView.findViewById(R.id.tvProInstrument);
                tvProInstruments = inflatedView.findViewById(R.id.tvProInstruments);
                recyclerViewproInstrument = inflatedView.findViewById(R.id.recyclerViewproInstrument);

                tvProInstrument.setTypeface(appTypeface.getHind_medium());
                tvProInstruments.setTypeface(appTypeface.getHind_regular());

                if(metaDataArray.getFieldType()==1 || metaDataArray.getFieldType()==3
                        || metaDataArray.getFieldType()==4)
                {
                    tvProInstruments.setVisibility(View.VISIBLE);
                    tvProInstrument.setText(metaDataArray.getFieldName());
                    tvProInstruments.setText(metaDataArray.getData());
                    presenter.moreReadable(tvProInstruments);
                }
                else if(metaDataArray.getFieldType()==2 || metaDataArray.getFieldType()==6)
                {

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    String[] langArrya = metaDataArray.getData().split(",");
                    recyclerViewproInstrument.setVisibility(View.VISIBLE);
                    recyclerViewproInstrument.setLayoutManager(linearLayoutManager);
                    if(langArrya.length>0)
                    {
                        LangExpertiseAdapter  langAdapter = new LangExpertiseAdapter(this,langArrya,false, null);
                        recyclerViewproInstrument.setAdapter(langAdapter);
                        langAdapter.notifyDataSetChanged();

                    }
                    tvProInstrument.setText(metaDataArray.getFieldName());
                    if(Constants.jobType != 2)
                    {
                        if(metaDataArray.getFieldName().equals("Expertise"))
                            tvProviderAge.setText(metaDataArray.getData());
                    }
                }
                else if(metaDataArray.getFieldType()==5)
                {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    recyclerViewproInstrument.setVisibility(View.VISIBLE);
                    recyclerViewproInstrument.setLayoutManager(linearLayoutManager);
                    tvProInstrument.setText(metaDataArray.getFieldName());
                    LangExpertiseAdapter  langAdapter = new LangExpertiseAdapter(this,null,true,metaDataArray.getPreDefined());
                    recyclerViewproInstrument.setAdapter(langAdapter);
                    langAdapter.notifyDataSetChanged();
                }

            }
        }else if(providerDataResp.getReview().size()>0)
        {
            tvReviewMe.setSelected(true);
        }

        if(providerDataResp.getDistanceMatrix()==0)
            Constants.distanceUnit = getString(R.string.distanceKm);
        else
            Constants.distanceUnit = getString(R.string.distanceMiles);
        String distance = providerDataResp.getDistance()+" "+Constants.distanceUnit;

        Constants.proId = proId;
        intentBundle.putString("NAME", tvProviderName.getText().toString());
        intentBundle.putString("expertise",tvProviderAge.getText().toString());

        nestedScrollView.scrollTo(0,0);




    }

    private void showAddress(YourAddrData address) {
        if (latitude != 0) {

           /* LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());*/

            tvInCallAddress1.setText(address.getAddLine1());
        }
    }

    private void slots(ArrayList<Slots> slotData) {


        if(slotData.size()>0)
        {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            SlotsTimingAdapter adapter = new SlotsTimingAdapter(this,true,slotData);
            mRecyclerViewSlot.setLayoutManager(linearLayoutManager);
            mRecyclerViewSlot.setAdapter(adapter);

        }
    }

    @Override
    public void onReviewSuccess(ReviewPojo.SignUpDataSid reviewList) {

        providerProgress.setVisibility(View.GONE);
        if(reviewList.getReviews().size()>0)
        {

            loading = reviewList.getReviews().size() > 4;
            rateReview.addAll(reviewList.getReviews());
            ratinReviewAdaptr.notifyDataSetChanged();
        }
        else
        {
            loading = false;
        }
    }

    @Override
    public void onErrorNotConnected(String message) {

        alertProgress.tryAgain(this,  getString(R.string.pleaseCheckInternet), getString(R.string.system_error), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                if(isClicked)
                    callProvider();
            }
        });
    }

    @Override
    public void onBookingHired() {

        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();//finishing activityz
    }

    private void appBarChangeListnr(final String name) {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0)
                {
                    collapsingToolbarLayout.setTitle(name);
                    toolBarTitle.setText("Registereres");
                    ivProDtlsPic.setVisibility(View.GONE);
                    toolProvider.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    // toolBarTitle.setText("");
                    ivProDtlsPic.setVisibility(View.VISIBLE);
                    toolProvider.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(name+" Location"));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 14.0f);
        mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);



        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+latitude+","+longitude));
                startActivity(intent);
            }
        });

    }

    private class RatingAdap extends RecyclerView.Adapter {
        private Context mContext;
        private ArrayList<ProviderDetailsResponse.RatingLog> ratingLog;
        public RatingAdap(ProviderDetails providerDetails, ArrayList<ProviderDetailsResponse.RatingLog> ratingLog)
        {
            mContext = providerDetails;
            this.ratingLog = ratingLog;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.rating_log,parent,false);
            return new RecyclerHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            RecyclerHolder hold = (RecyclerHolder) holder;
            hold.tvRatingName.setText(ratingLog.get(position).getName());
            hold.rtRating.setRating(ratingLog.get(position).getRating());
        }

        @Override
        public int getItemCount() {
            return ratingLog.size();
        }

        private class RecyclerHolder extends RecyclerView.ViewHolder
        {
            TextView tvRatingName;
            private RatingBar rtRating;
            public RecyclerHolder(View itemView) {
                super(itemView);
                tvRatingName = itemView.findViewById(R.id.tvRatingName);
                rtRating = itemView.findViewById(R.id.rtRating);
                tvRatingName.setTypeface(AppTypeface.getInstance(mContext).getHind_regular());
            }
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            tvInCallAddress1.setText(locationAddress);

        }
    }
}
