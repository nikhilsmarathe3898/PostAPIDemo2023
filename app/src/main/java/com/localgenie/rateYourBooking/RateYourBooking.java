package com.localgenie.rateYourBooking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.home.MainActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.RatingAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;
import com.pojo.AdditionalService;
import com.pojo.BookingAccounting;
import com.pojo.CartInfo;
import com.pojo.InvoiceDetails;
import com.utility.AlertProgress;
import com.utility.CalendarEventHelper;
import com.utility.DialogInterfaceListner;
import com.utility.NotificationUtils;

public class RateYourBooking extends DaggerAppCompatActivity implements RateYourProviderContract.ViewContract
        ,RatingAdapter.ViewChange
{//,RatingBar.OnRatingBarChangeListener
    @BindView(R.id.tbLayout)Toolbar tbLayout;
    @BindView(R.id.tb_service_header)TextView tb_service_header;
    @BindView(R.id.tbServiceAvailable)TextView tbServiceAvailable;
    @BindView(R.id.tvTotalBillAmtTitle)TextView tvTotalBillAmtTitle;
    @BindView(R.id.tvTotalBillAmt)TextView tvTotalBillAmt;
    @BindView(R.id.tvReceipt)TextView tvReceipt;
    @BindView(R.id.tvAddToFav)TextView tvAddToFav;
    @BindView(R.id.tvRateProviderTitle)TextView tvRateProviderTitle;
    @BindView(R.id.tvRateProviderName)TextView tvRateProviderName;
    @BindView(R.id.ivProfilePic)ImageView ivProfilePic;
    @BindView(R.id.btnSave)Button btnSave;
    @BindView(R.id.tvComments)EditText tvComments;
    @BindView(R.id.rvGrid)RecyclerView rvGrid;
    @BindView(R.id.progressBarRateYourPro)ProgressBar progressBarRateYourPro;

    // @BindView(R.id.rbProvider)RatingBar rbProvider;

    @Inject AppTypeface appTypeface;

    @Inject RateYourProviderContract.Presenter presenter;
    @Inject
    SessionManagerImpl manager;

    private RatingAdapter adapter;
    private ArrayList<InvoiceDetails.CustomerRating> stringList;
    private ArrayList<AdditionalService>additionalServices = new ArrayList<>();
    private CartInfo cartInfo;
    private BookingAccounting accounting;
    private String signURL,addressLine,providerId,catId,currencySymbol,reminderId,categoryName;
    private long bId;
    @Inject
    MQTTManager mqttManager;
    @Inject
    AlertProgress alertProgress;

    private int bookingModel;
    private ArrayList<String> pickUpList;
    private ArrayList<String> dropImageList;
    private int calltype;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_your_booking);
        ButterKnife.bind(this);
        getIntentValue();
        setToolBarValue();
        initializeTypeFace();
    }

    private void getIntentValue() {

        if (getIntent().getExtras() != null) {

            bId = getIntent().getLongExtra("BID", 0);
            // isImageDrawn = true;
        }
    }

    private void initializeTypeFace()
    {
        tb_service_header.setTypeface(appTypeface.getHind_medium());
        tbServiceAvailable.setTypeface(appTypeface.getHind_regular());
        tvTotalBillAmtTitle.setTypeface(appTypeface.getHind_regular());
        tvTotalBillAmt.setTypeface(appTypeface.getHind_semiBold());
        tvReceipt.setTypeface(appTypeface.getHind_regular());
        tvAddToFav.setTypeface(appTypeface.getHind_regular());
        tvRateProviderTitle.setTypeface(appTypeface.getHind_medium());
        tvRateProviderName.setTypeface(appTypeface.getHind_medium());
        btnSave.setTypeface(appTypeface.getHind_semiBold());
        tvComments.setTypeface(appTypeface.getHind_regular());
        tb_service_header.setText("Your Last Booking on");

    }

    private void setToolBarValue()
    {
        setSupportActionBar(tbLayout);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
       // tbLayout.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        tbLayout.setNavigationOnClickListener(view -> onBackPressed());
        presenter.onInvoiceDetailsCalled(bId);
        stringList=new ArrayList<>();

        setAdapters();
    }

    private void setAdapters() {
        adapter=new RatingAdapter(stringList,this,this);
        //  rvGrid.setLayoutManager(new GridLayoutManager(this,3));
        rvGrid.setLayoutManager(new LinearLayoutManager(this));
        rvGrid.setAdapter(adapter);
        rvGrid.setNestedScrollingEnabled(false);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.dimen_4dp);
        RatingAdapter.SpacesItemDecoration itemDecoration=new RatingAdapter.SpacesItemDecoration(spacingInPixels);
        rvGrid.addItemDecoration(itemDecoration);
    }

    @OnClick({R.id.btnSave,R.id.tvReceipt,R.id.tvAddToFav})
    public void onRateReviewClicked(View v)
    {
        switch (v.getId())
        {
            case R.id.tvAddToFav:
                onShowProgress();
                if(tvAddToFav.getText().toString().equals(getString(R.string.removeFromFav)))
                    presenter.removeFromFav(providerId,catId);
                else
                    presenter.onAddToFav(providerId,catId);
                break;
            case R.id.tvReceipt:
                presenter.openDialog(RateYourBooking.this,calltype,  appTypeface, cartInfo, signURL, accounting,addressLine,additionalServices,bookingModel,currencySymbol,
                        categoryName,pickUpList,dropImageList,this);
                break;
            case R.id.btnSave:
                onShowProgress();
                presenter.onUpdateReview(bId,stringList,tvComments.getText().toString());
                if(!"".equals(reminderId) && reminderId!=null)
                {
                    CalendarEventHelper calendarEventHelper = new CalendarEventHelper(this);
                    calendarEventHelper.deleteEvent(Long.parseLong(reminderId));
                }
                break;
        }
    }

    @Override
    public void onSessionExpired()
    {

    }

    @Override
    public void onLogout(String message)
    {
        alertProgress.alertPositiveOnclick(this, message, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(RateYourBooking.this,manager);
            }
        });
    }

    @Override
    public void onError(String error) {

        alertProgress.alertinfo(this,error);
    }

    @Override
    public void onShowProgress() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBarRateYourPro.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgress() {
        progressBarRateYourPro.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onGetInvoiceDetails(InvoiceDetails.InvoiceData data)
    {
        additionalServices.clear();
        String name = data.getProviderData().getFirstName()+" "+data.getProviderData().getLastName();
        tvRateProviderName.setText(name);
        calltype=data.getCart().getCallType();
        currencySymbol = data.getCurrencySymbol();
        pickUpList=data.getPickupImages();
        dropImageList=data.getDropImages();
        Utility.setAmtOnRecept(data.getAccounting().getTotal(),tvTotalBillAmt,currencySymbol);
        bookingModel =  data.getBookingModel();
        additionalServices.addAll(data.getAdditionalService());
        stringList.addAll(data.getCustomerRating());
        adapter.notifyDataSetChanged();
        providerId = data.getProviderData().getProviderId();
        catId = data.getCategoryId();
        presenter.timeMethod(tbServiceAvailable, data.getBookingRequestedFor());
        signURL = data.getSignURL();
        addressLine = data.getAddLine1();
        cartInfo = data.getCart();
        Log.d("TAG", "onGetInvoiceDetailsCART: "+cartInfo);
        accounting = data.getAccounting();
        categoryName = data.getCategoryName();
        if (data.getProviderData().getProfilePic() != null && !data.getProviderData().getProfilePic().equals("")) {
            Glide.with(this)
                    .load(data.getProviderData().getProfilePic())
                    .apply(Utility.createGlideOption(this))
                    .into(ivProfilePic);
        }
        reminderId = data.getReminderId();


    }

    @Override
    public void onRateProviderSuccess() {

        if(!Constants.isHomeFragment)
        {
            Constants.isHomeFragment = false;
            Constants.isConfirmBook = false;
            Constants.isJobDetailsOpen = false;
            Intent intent = new Intent(RateYourBooking.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if(mqttManager.isMQTTConnected())
                mqttManager.unSubscribeToTopic(MqttEvents.JobStatus.value + "/" + manager.getSID());
            startActivity(intent);
        }
        NotificationUtils.clearNotifications(this);
        finish();
    }

    @Override
    public void onGetStarList(ArrayList<String> stringList)
    {
        //this.stringList=stringList;
    }

    @Override
    public void onFavAdded(String message) {
        alertProgress.alertinfo(this,message);
        tvAddToFav.setText(getString(R.string.removeFromFav));
    }

    @Override
    public void removeFromFav(String message) {
        alertProgress.alertinfo(this,message);
        tvAddToFav.setText(getString(R.string.addToFav));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }



    @Override
    public void onTextSelected(float rating, int position)
    {
        stringList.get(position).setRatings(rating);

    }
}
