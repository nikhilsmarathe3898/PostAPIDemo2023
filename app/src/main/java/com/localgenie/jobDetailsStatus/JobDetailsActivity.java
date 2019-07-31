package com.localgenie.jobDetailsStatus;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.localgenie.R;
import com.localgenie.chatting.ChattingActivity;
import com.localgenie.home.MainActivity;
import com.localgenie.home.MyBookingsFrag;
import com.localgenie.invoice.InvoiceActivity;
import com.localgenie.jobDetailsStatus.ProviderHiredFragmentIn.OnFragmentInteractionListener;
import com.localgenie.lspapplication.MessagesFragment;
import com.localgenie.networking.ChatApiService;
import com.localgenie.networking.ServiceFactory;
import com.localgenie.rateYourBooking.RateYourBooking;
import com.localgenie.utilities.AppPermissionsRunTime;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import adapters.QuestionAdapterGrid;
import adapters.SelectedService;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;
import com.pojo.BidDispatchLog;
import com.pojo.BidQuestionAnswer;
import com.pojo.BookingAccounting;
import com.pojo.BookingTimer;
import com.pojo.BookingTimerLatLngObservable;
import com.pojo.CartInfo;
import com.pojo.MyBookingObservable;
import com.pojo.MyBookingStatus;
import com.pojo.ProviderDetailsBooking;
import com.pojo.QuestionImage;
import com.utility.AlertProgress;
import com.utility.CalendarEventHelper;
import com.utility.DialogInterfaceListner;
import com.utility.NotificationHandler;
import com.utility.NotificationUtils;
import com.webRtc.CallingApis;

public class JobDetailsActivity extends DaggerAppCompatActivity implements View.OnClickListener,JobDetailsContract.JobView,JobProviderInfo.JobBidCalling,OnFragmentInteractionListener {

    //  private TextView tvJobDetails;;
    private static final String TAG = "JobDetailsActivity";
    @Inject
    MQTTManager mqttManager;
    private static final int HIRE_PROVIDER = 101;
    private FragmentManager supportFragmentManger=getSupportFragmentManager();
    @BindView(R.id.toolbarJobDetails)Toolbar toolbar;
    @BindView(R.id.tv_center)TextView toolbarTitle;
    @BindView(R.id.ivFilter)ImageView ivFilter;
    @BindView(R.id.ivCancel)ImageView ivCancel;
    @BindView(R.id.rlToolImage)LinearLayout rlToolImage;
    @BindView(R.id.tv_skip)TextView btnCancel;
    @BindView(R.id.progressBarJobDetails)ProgressBar progressBarJobDetails;

    /************************Job Details***************************/

    @BindView(R.id.llchecks)LinearLayout llchecks;
    @BindView(R.id.llJobContactInfo)LinearLayout llJobContactInfo;
    @BindView(R.id.tvCallPro)TextView tvCallPro;
    @BindView(R.id.tvChatPro)TextView tvChatPro;
    @BindView(R.id.tvCancel)TextView tvCancel;
    @BindView(R.id.tvDetails)TextView tvDetails;
    @BindView(R.id.cardJobViewContactInfo)CardView cardJobViewContactInfo;
    @BindView(R.id.viewInfo0)View viewInfo0;
    @BindView(R.id.viewInfo1)View viewInfo1;
    @BindView(R.id.viewInfo2)View viewInfo2;

    /*************************************************************/

    @BindView(R.id.tvProJobPosted)TextView tvProJobPosted;
    @BindView(R.id.rlWaitingForResponse)RelativeLayout rlWaitingForResponse;
    @BindView(R.id.vLine2)View vLine2;
    @BindView(R.id.vCirle2)View vCirle2;
    @BindView(R.id.vLine2nd2)View vLine2nd2;
    @BindView(R.id.tvWaitForResponse)TextView tvWaitForResponse;

    @BindView(R.id.vLine3)View vLine3;
    @BindView(R.id.vCirle3)View vCirle3;
    @BindView(R.id.vLine2nd3)View vLine2nd3;
    @BindView(R.id.tvProHired)TextView tvProHired;

    @BindView(R.id.vLine4)View vLine4;
    @BindView(R.id.vCirle4)View vCirle4;
    @BindView(R.id.vLine2nd4)View vLine2nd4;
    @BindView(R.id.tvProOnTheWay)TextView tvProOnTheWay;

    @BindView(R.id.vLine5)View vLine5;
    @BindView(R.id.vCirle5)View vCirle5;
    @BindView(R.id.vLine2nd5)View vLine2nd5;
    @BindView(R.id.tvProJobArrived)TextView tvProJobArrived;

    @BindView(R.id.vLine6)View vLine6;
    @BindView(R.id.vCirle6)View vCirle6;
    @BindView(R.id.tvProJobStarted)TextView tvProJobStarted;

    // initialize JobDetails

    @BindView(R.id.tvJobDetailsInfo)TextView tvJobDetailsInfo;
    @BindView(R.id.tvJobDetailsInfoDesc)TextView tvJobDetailsInfoDesc;
    @BindView(R.id.tvJobDetailsInfoFor)TextView tvJobDetailsInfoFor;
    @BindView(R.id.tvConfirmBookingType)TextView tvConfirmBookingType;
    @BindView(R.id.tvJobDetailsAddress)TextView tvJobDetailsAddress;
    @BindView(R.id.tvJobDetailsLocation)TextView tvJobDetailsLocation;
    @BindView(R.id.tvJobDetailsPayment)TextView tvJobDetailsPayment;
    @BindView(R.id.tvJobDetailsPaymentMethod)TextView tvJobDetailsPaymentMethod;
    @BindView(R.id.tvJobDetailsFeeEstimate)TextView tvJobDetailsFeeEstimate;
    @BindView(R.id.tvJobTotalInfoAmount)TextView tvJobTotalInfoAmount;
    @BindView(R.id.tvJobTotalInfoPerShift)TextView tvJobTotalInfoPerShift;
    @BindView(R.id.tvJobTotalInfo)TextView tvJobTotalInfo;
    @BindView(R.id.tvConfirmDiscountFeeAmt)TextView tvConfirmDiscountFeeAmt;
    @BindView(R.id.tvConfirmDiscountFee)TextView tvConfirmDiscountFee;
    @BindView(R.id.tvConfirmVisitFee)TextView tvConfirmVisitFee;
    @BindView(R.id.tvConfirmVisitFeeAmt)TextView tvConfirmVisitFeeAmt;
    @BindView(R.id.recyclerViewService)RecyclerView recyclerViewService;
    @BindView(R.id.rlReceiptDetails)RelativeLayout rlReceiptDetails;

    @BindView(R.id.llJobQuesAns)LinearLayout llJobQuesAns;
    @BindView(R.id.llJobDetailsInfo)LinearLayout llJobDetailsInfo;
    @BindView(R.id.rlFeeJobDet)RelativeLayout rlFeeJobDet;
    @BindView(R.id.viewFeeJob)View viewFeeJob;

    @BindView(R.id.viewIsVisible)View viewIsVisible;
    @BindView(R.id.viewPayment)View viewPayment;
    @BindView(R.id.viewAddress)View viewAddress;
    @BindView(R.id.llBookingFor)LinearLayout llBookingFor;
    @BindView(R.id.lljobPaymentInfo)LinearLayout lljobPaymentInfo;


    /*******************************MultiShift********************************/

    @BindView(R.id.offInclude)LinearLayout offInclude;
    @BindView(R.id.tvTotalInfo)TextView tvTotalInfo;
    @BindView(R.id.rlOfferBidderMAin)RelativeLayout rlOfferBidderMAin;
    @BindView(R.id.NoOfBiddingShift)TextView NoOfBiddingShift;
    @BindView(R.id.NoOfBiddingShifts)TextView NoOfBiddingShifts;
    @BindView(R.id.tvPricePerShifts)TextView tvPricePerShifts;
    @BindView(R.id.tvPricePerShiftsAmt)TextView tvPricePerShiftsAmt;
    @BindView(R.id.tvBiddingDuration)TextView tvBiddingDuration;
    @BindView(R.id.tvBiddingDurationHR)TextView tvBiddingDurationHR;


    /*******************************MultiShift********************************/


    /**********************************In-Call********************************/
    @BindView(R.id.llMAinBookingOutCall)LinearLayout llMAinBookingOutCall;
    @BindView(R.id.llMAinBookingInCall)LinearLayout llMAinBookingInCall;

    @BindView(R.id.vCirleIn2)View vCirleIn2;
    @BindView(R.id.vLineIn2nd2)View vLineIn2nd2;
    @BindView(R.id.vLineIn3)View vLineIn3;
    @BindView(R.id.vCirleIn3)View vCirleIn3;

    /**********************************In-Call********************************/

    // JobPosted


    @Inject JobPostedFrag jobPostedFrag;
    @Inject MessagesFragment messageFragment;
    @Inject ProviderHiredFragment providerHiredFrag;
    @Inject ProviderHiredFragmentIn providerHiredIn;
    @Inject ProviderOnTheWayFrag providerOnTheWayFrag;
    @Inject ProviderArrivedFrag providerArrivedFrag;
    @Inject JobStartedFrag jobStartedFrag;
    @Inject JobDetailsContract.Presenter presenter;
    @Inject AppTypeface appTypeface;
    @Inject JobDetailsOnTheWayContract.CancelBooking cancelBooking;
    @Inject
    AlertProgress alertProgress;
    @Inject
    SessionManagerImpl manager;
    private CompositeDisposable disposable;

    // @Inject MyBookingObservable myBookingObservable;
    private Observer<MyBookingStatus> observer;
    private long bid;
    private long bookingRequestedFor;
    private int statusCode;
    private int callType = 0;

    private  JobProviderInfo jobProviderInfo;
    private String proName;
    private String proPic;
    private String currencySymbol;
    public String catJobName,reminderId;
    private boolean isStarTimerFrag = false;
    private boolean isOnPauseCalled = false;
    private Timer myTimer_publish;
    public static boolean isBidding = false;
    private Animation visible,gone;

    private int bookingType;; // 1 now , 2 for schedule , 3 for repeat booking
    // 1 in call 2 out call
    public static String phoneNumber; // 1 in call 2 out call
    // 1 in call 2 out call

    private NotificationUtils notificationUtils;
    private boolean isVisible = false;
    private ChatApiService chatApiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        ButterKnife.bind(this);

        disposable = new CompositeDisposable();
        getIntentValue();
        initialize();
        initializeToolBar();
        onTypeFaceSet();

        splitAmountForRepeatBooking();

        notificationUtils = new NotificationUtils(this);
    }

    private void splitAmountForRepeatBooking() {
        NoOfBiddingShift.setTypeface(appTypeface.getHind_regular());
        NoOfBiddingShifts.setTypeface(appTypeface.getHind_semiBold());

        tvPricePerShifts.setTypeface(appTypeface.getHind_regular());
        tvPricePerShiftsAmt.setTypeface(appTypeface.getHind_semiBold());

        tvBiddingDuration.setTypeface(appTypeface.getHind_regular());
        tvBiddingDurationHR.setTypeface(appTypeface.getHind_semiBold());
        tvTotalInfo.setVisibility(View.GONE);
        rlOfferBidderMAin.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String SUBMIT_PRESSED = "SUBMIT_PRESSED";
        outState.putBoolean(SUBMIT_PRESSED, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.isJobDetailsOpen = true;
        myTimer_publish = null;
        startPublishingWithTimer();

        if(observer!=null)
        {
            MyBookingObservable.getInstance().removeObserver(observer);
            observer = null;
            disposable.clear();
            disposable.dispose();
        }
        initializeRxJava();
    }

    private void getIntentValue() {
        if(getIntent().getExtras()!=null)
        {
            bid = getIntent().getLongExtra("BID",0);
            statusCode = getIntent().getIntExtra("STATUS",0);
            callType = getIntent().getIntExtra("CallType",0);
            Constants.bookingModelJobDetails = getIntent().getIntExtra("BookingModel",0);
            Log.d(TAG, " getIntentValue:bid:"+bid+" statusCode:"+statusCode+" callType:"+callType+" Constants.bookingModelJobDetails:"+Constants.bookingModelJobDetails);
        }

        if(Constants.bookingModelJobDetails ==3)
        {
            if(statusCode ==1 || statusCode ==2)
            {
                statusCode = 17;
            }
        }
        /*if(statusCode ==17 || statusCode == 1 || statusCode == 2 || statusCode == 3 || statusCode ==6)
            ivCancel.setVisibility(View.GONE);*/
        loadFragmentTransition(statusCode);
        onUiStatusChange(statusCode);
        serviceMethod();
        callTypeVisible();
    }

    private void callTypeVisible() {

        if(callType != 2)
        {
            llMAinBookingInCall.setVisibility(View.VISIBLE);
            llMAinBookingOutCall.setVisibility(View.GONE);
            //  cardJobViewContactInfo.setVisibility(View.GONE);

        }else
        {
            llMAinBookingOutCall.setVisibility(View.VISIBLE);
            llMAinBookingInCall.setVisibility(View.GONE);
            //   cardJobViewContactInfo.setVisibility(View.VISIBLE);
        }
    }

    private void serviceMethod()
    {
        if(alertProgress.isNetworkAvailable(this))
        {
            onShowProgress();
            presenter.onGetBookingDetails(bid);
        }else
            alertProgress.showNetworkAlert(this);
    }

    public void jobDetailProInfo(JobProviderInfo providerInfo)
    {
        jobProviderInfo = providerInfo;
    }

    private void initializeToolBar()
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnCancel.setText(R.string.cancel);
        btnCancel.setOnClickListener(this);
        toolbarTitle.setText(getString(R.string.jobId)+": "+bid);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void onTypeFaceSet()
    {
        visible = AnimationUtils.loadAnimation(this,R.anim.slide_view_up);//;slide_view_down
        gone  = AnimationUtils.loadAnimation(this,R.anim.slide_view_down);
        toolbarTitle.setTypeface(appTypeface.getHind_semiBold());
        btnCancel.setTypeface(appTypeface.getHind_light());
        tvProJobPosted.setTypeface(appTypeface.getHind_medium());
        tvWaitForResponse.setTypeface(appTypeface.getHind_medium());
        tvProHired.setTypeface(appTypeface.getHind_medium());
        tvProOnTheWay.setTypeface(appTypeface.getHind_medium());
        tvProJobStarted.setTypeface(appTypeface.getHind_medium());
        tvProJobArrived.setTypeface(appTypeface.getHind_medium());
        // tvProJobFinished.setTypeface(appTypeface.getHind_medium());

        tvJobTotalInfo.setTypeface(appTypeface.getHind_bold());
        tvJobTotalInfoAmount.setTypeface(appTypeface.getHind_bold());
        tvJobTotalInfoPerShift.setTypeface(appTypeface.getHind_light());
        tvJobDetailsFeeEstimate.setTypeface(appTypeface.getHind_semiBold());
        tvJobDetailsPaymentMethod.setTypeface(appTypeface.getHind_regular());
        tvJobDetailsInfo.setTypeface(appTypeface.getHind_semiBold());
        tvJobDetailsInfoFor.setTypeface(appTypeface.getHind_semiBold());
        //  tvJobDetailsCardInfo.setTypeface(appTypeface.getHind_regular());
        tvJobDetailsInfoDesc.setTypeface(appTypeface.getHind_regular());
        tvConfirmBookingType.setTypeface(appTypeface.getHind_regular());
        tvJobDetailsAddress.setTypeface(appTypeface.getHind_semiBold());
        tvJobDetailsPayment.setTypeface(appTypeface.getHind_semiBold());
        //   tvInConfirmBookingTypeDesc.setTypeface(appTypeface.getHind_regular());
        tvJobDetailsLocation.setTypeface(appTypeface.getHind_regular());
        tvConfirmDiscountFeeAmt.setTypeface(appTypeface.getHind_light());
        tvConfirmDiscountFee.setTypeface(appTypeface.getHind_light());
        tvConfirmVisitFee.setTypeface(appTypeface.getHind_light());
        tvConfirmVisitFeeAmt.setTypeface(appTypeface.getHind_light());
        tvCallPro.setTypeface(appTypeface.getHind_regular());
        tvChatPro.setTypeface(appTypeface.getHind_regular());
        tvCancel.setTypeface(appTypeface.getHind_regular());
        tvDetails.setTypeface(appTypeface.getHind_regular());
        tvJobDetailsFeeEstimate.setText(getString(R.string.feeBreakDown));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isOnPauseCalled)
        {
            loadFragmentTransition(statusCode);
            isOnPauseCalled = false;
            presenter.onGetBookingDetails(bid);
        }

    }

    /**
     * publishing provider topic for every 4 second
     */
    public void startPublishingWithTimer() {

        TimerTask myTimerTask_publish;
        if (myTimer_publish != null)
            return;
        myTimer_publish = new Timer();
        myTimerTask_publish = new TimerTask() {
            @Override
            public void run() {

                if(mqttManager.isMQTTConnected())
                {
                    mqttManager.subscribeToTopic(MqttEvents.JobStatus.value + "/" + manager.getSID(),1);
                    myTimer_publish.purge();
                    myTimer_publish.cancel();
                }else
                {
                    mqttManager.createMQttConnection(manager.getSID(),false);
                }
            }

        };
        myTimer_publish.schedule(myTimerTask_publish, 0, 1000);
    }

    private void initializeRxJava()
    {
        observer = new Observer<MyBookingStatus>() {
            @Override
            public void onSubscribe(Disposable d)
            {
                disposable.add(d);
            }

            @Override
            public void onNext(MyBookingStatus myBookingStatus)
            {

                Log.d("TAG", "onNextMQTTRESPONSEJOBDETAILS: "+statusCode+" isTrue "+Constants.isJobDetailsOpen
                        +" myBookingStatus "+myBookingStatus.getData().getStatus()+" isPause "+isOnPauseCalled);
                if(Constants.isJobDetailsOpen)
                {
                    onUiStatusChange(myBookingStatus.getData().getStatus());

                    if(!isOnPauseCalled)
                        loadFragmentTransition(myBookingStatus.getData().getStatus());

                    switch (myBookingStatus.getData().getStatus()) {
                        case 3:
                            //bookingType == 3 && (Constants.bookingModelJobDetails == 1 || Constants.bookingModelJobDetails == 2)
                            if (myBookingStatus.getData().getBookingType() == 3) {
                                MyBookingsFrag.onMyGetBooking();
                                onDestroyCalled();

                            } else
                            {
                                presenter.onGetBookingDetails(bid);
                                //  ivCancel.setVisibility(View.VISIBLE);

                                if (manager.getBookingStatus(myBookingStatus.getData().getBookingId()) < myBookingStatus.getData().getStatus()) {
                                    manager.setBookingStatus(myBookingStatus.getData().getBookingId(), myBookingStatus.getData().getStatus());
                                    notificationUtils.showJustNotification
                                            ("LIVESTATUS", myBookingStatus.getData().getStatusMsg(), myBookingStatus.getData().getMsg(), new Intent());
                                }
                            }
                            phoneNumber = myBookingStatus.getData().getPhone().get(0).getCountryCode()+myBookingStatus.getData().getPhone().get(0).getPhone();

                            // checkAndAddEvent(JobDetailsActivity.this,myBookingStatus.getData().getBookingId(),bookingTime,manager);
                            break;
                        case 4:
                            // Toast.makeText(JobDetailsActivity.this,myBookingStatus.getData().getStatusMsg(),Toast.LENGTH_SHORT).show();
                            break;
                        case 5:
                            //  Toast.makeText(JobDetailsActivity.this,myBookingStatus.getData().getStatusMsg(),Toast.LENGTH_SHORT).show();
                            if(manager.getBookingStatus(myBookingStatus.getData().getBookingId())<myBookingStatus.getData().getStatus()){
                                manager.setBookingStatus(myBookingStatus.getData().getBookingId(),myBookingStatus.getData().getStatus());
                                notificationUtils.showJustNotification
                                        ("LIVESTATUS", myBookingStatus.getData().getStatusMsg(),myBookingStatus.getData().getMsg(), new Intent());
                            }
                            onDestroyCalled();
                            break;
                        case 6:
                        case 7:
                            presenter.onGetBookingDetails(bid);
                            if(manager.getBookingStatus(myBookingStatus.getData().getBookingId())<myBookingStatus.getData().getStatus()){
                                manager.setBookingStatus(myBookingStatus.getData().getBookingId(),myBookingStatus.getData().getStatus());
                                notificationUtils.showJustNotification
                                        ("LIVESTATUS", myBookingStatus.getData().getStatusMsg(),myBookingStatus.getData().getMsg(), new Intent());
                            }
                            break;
                        case 8:
                        case 9:
                            //  ivCancel.setVisibility(View.GONE);
                            if(manager.getBookingStatus(myBookingStatus.getData().getBookingId())<myBookingStatus.getData().getStatus()){
                                manager.setBookingStatus(myBookingStatus.getData().getBookingId(),myBookingStatus.getData().getStatus());
                                notificationUtils.showJustNotification
                                        ("LIVESTATUS", myBookingStatus.getData().getStatusMsg(),myBookingStatus.getData().getMsg(), new Intent());
                            }
                            if(!isStarTimerFrag)
                            {
                                presenter.onGetBookingDetails(bid);
                                isStarTimerFrag = true;
                            }
                            BookingTimerLatLngObservable.getInstance().emitTimerLatLng(myBookingStatus.getData().getBookingTimer(),myBookingStatus.getData().getStatusMsg()
                                    ,myBookingStatus.getData().getStatus());
                            //====================Added for new rating screen============================
                            if(callType==3){
                                Intent intent = new Intent(JobDetailsActivity.this, InvoiceActivity.class);
                                intent.putExtra("BID", myBookingStatus.getData().getBookingId());
                                intent.putExtra("PROIMAGE", myBookingStatus.getData().getProProfilePic());
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            //===========================================================================
                            break;
                        case 10:
                            // ivCancel.setVisibility(View.GONE);
                            disposable.clear();
                            disposable.dispose();
                            Intent intent = new Intent(JobDetailsActivity.this, RateYourBooking.class);
                            intent.putExtra("BID", myBookingStatus.getData().getBookingId());
                            intent.putExtra("PROIMAGE", myBookingStatus.getData().getProProfilePic());
                            startActivity(intent);
                            finish();
                            break;
                        case 11:
                            if(!isOnPauseCalled)
                            {
                                alertProgress.alertPositiveOnclick(JobDetailsActivity.this, myBookingStatus.getData().getStatusMsg(),
                                        getString(R.string.system_error), getString(R.string.ok), isClicked -> {
                                            Constants.isConfirmBook = false;

                                            Intent intent1 = new Intent(JobDetailsActivity.this, MainActivity.class);
                                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent1);
                                            onDestroyCalled();
                                        });
                            }
                            if(!"".equals(myBookingStatus.getData().getReminderId()) && myBookingStatus.getData().getReminderId()!=null)
                            {
                                CalendarEventHelper calendarEventHelper = new CalendarEventHelper(JobDetailsActivity.this);
                                calendarEventHelper.deleteEvent(Long.parseLong(myBookingStatus.getData().getReminderId()));
                            }
                            break;
                        case 15:
                            //       removeLocationHandler();
                            if(!isOnPauseCalled) {
                                alertProgress.alertPositiveOnclick(JobDetailsActivity.this, myBookingStatus.getData().getStatusMsg(),
                                        getString(R.string.system_error),getString(R.string.ok), isClicked -> {
                                            Constants.isConfirmBook = true;
                                            Intent intent1 = new Intent(JobDetailsActivity.this, MainActivity.class);
                                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent1);
                                            // finish(); i am doing
                                            onDestroyCalled();
                                        });
                            }
                            break;
                        case 17:
                            //  ivCancel.setVisibility(View.VISIBLE);

                            onBidDispatchLog(myBookingStatus.getData().getBidProvider(),myBookingStatus.getData().getStatus());
                            break;
                    }
                    statusCode = myBookingStatus.getData().getStatus();

                }
            }

            @Override
            public void onError(Throwable e)
            {

            }

            @Override
            public void onComplete() {


            }
        };
        MyBookingObservable.getInstance().subscribe(observer);
    }

    private void changeContactInfoView(int statusCode, boolean isVisible)
    {
        if(statusCode == 9)
        {
            tvProJobStarted.setText(getString(R.string.jobCompleted));
        }
        if(callType==2)
        {
            presenter.changeWaitSum(this,statusCode,llJobContactInfo,tvCallPro,tvChatPro,tvCancel,tvDetails);
            presenter.changeViews(this,statusCode,viewInfo0,viewInfo1,viewInfo2);

        }else
        {
            presenter.changeWaitSumInCall(this,statusCode,isVisible,llJobContactInfo,tvCallPro,tvChatPro,tvCancel,tvDetails);
            presenter.changeViewsInCall(this,statusCode,isVisible,viewInfo0,viewInfo1,viewInfo2);
        }

    }

    private void onUiStatusChange(int statusCode)
    {
        changeContactInfoView(statusCode,true); // i am doing
        if(Constants.bookingModelJobDetails ==3)
        {
            rlWaitingForResponse.setVisibility(View.GONE);
        }
        if(callType == 2)
            presenter.onUiStatusChange(JobDetailsActivity.this,statusCode,vLine3,vCirle3,vLine2nd3,vLine4,vCirle4,vLine2nd4,
                    vLine5,vCirle5,vLine2nd5,vLine6,vCirle6);//vCirle2,vLine2nd2,
        else
            presenter.onUiInCallCahnge(JobDetailsActivity.this,statusCode,vCirleIn2,vLineIn2nd2,vLineIn3,vCirleIn3);
    }

    private void loadFragmentTransition(int statusCode)
    {
        Log.d(TAG, " getIntentValue:bid:"+bid+" statusCode:"+statusCode+" callType:"+callType+" Constants.bookingModelJobDetails:"+Constants.bookingModelJobDetails);
        if(statusCode ==17)
            isBidding = true;
        else if(statusCode == 1 || statusCode == 2 )
            isBidding = false;

        if(callType ==2)
        {

            if(!isFinishing())
                presenter.onFragmentTransition(statusCode,supportFragmentManger,providerHiredFrag
                        ,providerOnTheWayFrag,providerArrivedFrag,jobStartedFrag,messageFragment);//jobPostedFrag
        }else //if(callType == 1)
        {
            Log.d(TAG, "loadFragmentTransition: providerHiredIn:"+providerHiredIn);
            if(providerHiredIn!=null)
                providerHiredIn.callType(callType);

            if(!isFinishing())
                presenter.onFragmentTransitionIn(statusCode,supportFragmentManger,messageFragment,providerHiredIn);
        }/*else
        {

            if(!isFinishing())
                presenter.onFragmentTransitionIn(statusCode,supportFragmentManger,messageFragment,providerHiredIn);
        }*/


    }

    private void initialize() {
        chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);
        isOnPauseCalled = false;
        ivFilter.setVisibility(View.GONE);
        rlToolImage.setVisibility(View.GONE);
        ivFilter.setImageResource(R.drawable.ic_info_outline_black_24dp);
        tvCallPro.setOnClickListener(this);
        tvChatPro.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvDetails.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvDetails:
                if(!tvDetails.isSelected())
                {
                    tvDetails.setSelected(true);
                    toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
                    presenter.showAnimationCardVied(false,cardJobViewContactInfo);
                    rlReceiptDetails.setVisibility(View.VISIBLE);
                    llchecks.setVisibility(View.GONE);
                    rlReceiptDetails.startAnimation(visible);
                    isVisible = true;
                }
                break;
            case R.id.ivFilter:
                if(rlReceiptDetails.getVisibility()==v.VISIBLE)
                {

                    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                    rlReceiptDetails.setVisibility(View.GONE);
                    isVisible = false;

                }else
                {
                    toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
                    ivFilter.setImageResource(0);
                    rlReceiptDetails.setVisibility(View.VISIBLE);
                    rlReceiptDetails.startAnimation(visible);
                    isVisible = true;
                }
                break;
            case R.id.ivCancel:
                cancelBooking.onToCancelBooking(bid,this,reminderId);
                break;
            case R.id.tvCancel:
                cancelBooking.onToCancelBooking(bid,this,reminderId);
                break;
            case R.id.tvCallPro:
                if(callType == 2)
                {
                    Intent intentCall = new Intent(Intent.ACTION_DIAL);
                    intentCall.setData(Uri.parse("tel:" +phoneNumber));
                    startActivity(intentCall);
                }else
                    callButtonClicked();
                 //onFragmentInteraction(true);
                break;
            case R.id.tvChatPro:
                if(!manager.getChatProId().equals(""))
                {
                    Intent intent = new Intent(this, ChattingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
                }
                break;
        }
    }

    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message)
    {
        Utility.setMAnagerWithBID(this,manager);

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onShowProgress() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBarJobDetails.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgress()
    {
        progressBarJobDetails.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onSuccessBooking()
    {

    }

    @Override
    public void onBookingTimer(BookingTimer bookingTimer, int status, long serverTime, String statusMsg, long bookingRequestedAt, long totalJobTime)
    {
       /* if(status==8 ||status == 7)
        {*/
        jobProviderInfo.onJobTimer(bookingTimer,serverTime,statusMsg,bid,status,totalJobTime);
        // }

    }

    @Override
    public void onProviderDtls(ProviderDetailsBooking providerDetail, final double amount, final String currencySymbol, int status, String reminderId) {

        this.currencySymbol = currencySymbol;
        Constants.bookingcurrencySymbol = currencySymbol;
        Constants.currencySymbol = currencySymbol;
        phoneNumber = providerDetail.getPhone();
        proName = providerDetail.getFirstName()+" "+providerDetail.getLastName();
        proPic = providerDetail.getProfilePic();
        callProId = providerDetail.getProviderId();
        statusCode = status;
        this.reminderId = reminderId;


        if(jobProviderInfo!=null)
            jobProviderInfo.providerInfo(providerDetail.getProviderId(),proName,proPic,
                    providerDetail.getReviewCount(), providerDetail.getAverageRating()
                    ,amount,currencySymbol,status);
    }

    @Override
    public void onBookingSuccessInfo(final long bookingExpireTime, final long serverTime, final LatLng customerLatLng, final LatLng proLatLng, int status) {


        if(jobProviderInfo!=null)
            jobProviderInfo.proExpiry(proName,proPic,bookingExpireTime,serverTime,customerLatLng,proLatLng);

    }

    @Override
    public void JobDetailsAccounting(String jobDescription, int bookingType, long bookingRequestedFor, String addLine1, BookingAccounting accounting, CartInfo cart)
    {
        Date date = new Date(bookingRequestedFor * 1000L);
        if(jobProviderInfo!=null)
            jobProviderInfo.onJobAddress(addLine1,date);
        LinearLayoutManager llManager = new LinearLayoutManager(JobDetailsActivity.this);
        recyclerViewService.setLayoutManager(llManager);
        if(!"".equals(jobDescription) && jobDescription!=null)
            tvJobDetailsInfoDesc.setText(jobDescription);
        else
            tvJobDetailsInfoDesc.setText(getString(R.string.noDescriptionForJob));

        if(bookingType==2)
        {
            tvConfirmBookingType.setText(getString(R.string.schedule));
            tvConfirmBookingType.setText(Utility.getFormattedDate(date));
        }else if(bookingType==3)
        {

            tvConfirmBookingType.setText(getString(R.string.repeat)); //else for repeat

            if(statusCode == 1 || statusCode ==  2 || statusCode == 17)
            {
                offInclude.setVisibility(View.VISIBLE);
                NoOfBiddingShifts.setText(accounting.getTotalShiftBooking()+"");
                Utility.setAmtOnRecept(accounting.getTotal(),tvPricePerShiftsAmt,currencySymbol);
               /* String amountPerShift = tvPricePerShiftsAmt.getText().toString()+"/shift";
                tvPricePerShiftsAmt.setText(amountPerShift);*/
                tvBiddingDuration.setText(getString(R.string.total));
                double total = accounting.getTotal()*accounting.getTotalShiftBooking();
                Utility.setAmtOnRecept(total,tvBiddingDurationHR,currencySymbol);
            }
        }

        this.bookingRequestedFor = bookingRequestedFor;
        this.bookingType = bookingType;
        if(accounting.getPaidByWallet()==1)
            tvJobDetailsPaymentMethod.setText(getString(R.string.wallet));
        else
            tvJobDetailsPaymentMethod.setText(accounting.getPaymentMethodText());

        tvJobDetailsLocation.setText(addLine1);
        if(accounting.getDiscount()<=0)
        {
            tvConfirmDiscountFee.setVisibility(View.GONE);
            tvConfirmDiscountFeeAmt.setVisibility(View.GONE);
        }else
            Utility.setAmtOnRecept(accounting.getDiscount(),tvConfirmDiscountFeeAmt,currencySymbol);
        if(accounting.getVisitFee()<=0)
        {
            tvConfirmVisitFee.setVisibility(View.GONE);
            tvConfirmVisitFeeAmt.setVisibility(View.GONE);
        }else
            Utility.setAmtOnRecept(accounting.getVisitFee(),tvConfirmVisitFeeAmt,currencySymbol);

        Utility.setAmtOnRecept(accounting.getTotal(),tvJobTotalInfoAmount,currencySymbol);
        tvJobTotalInfoPerShift.setText("");
        if(bookingType == 3 && (statusCode == 1 || statusCode == 2 || statusCode == 17))
        {
            String amount = tvJobTotalInfoAmount.getText().toString().trim();
            tvJobTotalInfoAmount.setText(amount);
            tvJobTotalInfoPerShift.setText(getString(R.string.perShifts));
        }

        if(Constants.bookingModelJobDetails!=3)
        {
            SelectedService selectedService = new SelectedService(this,false);
            selectedService.onCheckOutItem(cart.getCheckOutItem());
            recyclerViewService.setAdapter(selectedService);
        }else
        {
            tvJobDetailsFeeEstimate.setVisibility(View.GONE);
            viewFeeJob.setVisibility(View.GONE);
            rlFeeJobDet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onJobInfo(int bookingType, BookingAccounting accounting, CartInfo cart, String categoryName) {

        if(jobProviderInfo!=null)
            jobProviderInfo.onJobInfo(bookingType,accounting,cart,categoryName);

    }

    @Override
    public void loadFragment() {
        if(!isOnPauseCalled)
            loadFragmentTransition(statusCode);
    }

    @Override
    public void setCatDesc(String categoryName, String categoryId, int callType) {
        this.callType = callType;

        if(Constants.catId==null || "".equals(Constants.catId))
            Constants.catId = categoryId;
        catJobName = categoryName;

    }

    @Override
    public void onBidQuestionAnswer(ArrayList<BidQuestionAnswer> questionAndAnswer, int bookingModel)
    {
       /* if(bookingModel == 3)
        {*/
        if(questionAndAnswer.size()>0)
        {

            // }
            tvJobDetailsInfoDesc.setVisibility(View.GONE);
            llJobQuesAns.setVisibility(View.VISIBLE);
            tvJobDetailsAddress.setVisibility(View.GONE);
            tvJobDetailsLocation.setVisibility(View.GONE);
            llBookingFor.setVisibility(View.GONE);
            viewIsVisible.setVisibility(View.GONE);
            viewPayment.setVisibility(View.GONE);
            lljobPaymentInfo.setVisibility(View.GONE);
            viewAddress.setVisibility(View.GONE);
            llJobQuesAns.removeAllViews();
            TextView tvJobQuestion,tvJobQuestions,tvJobAnswer,tvJobAnswers;
            LinearLayout llJobDetailsQA;
            RecyclerView recyclerViewQuestionImage;
            RelativeLayout rlQuestion;
            View viewJobQA;
            int colour = 0;
            for(int position = 0;position<questionAndAnswer.size();position++)
            {

                View itemView = LayoutInflater.from(JobDetailsActivity.this).inflate(R.layout.job_question_answer,llJobDetailsInfo,false);
                tvJobQuestions = itemView.findViewById(R.id.tvJobQuestions);
                tvJobAnswers = itemView.findViewById(R.id.tvJobAnswers);
                viewJobQA = itemView.findViewById(R.id.viewJobQA);
                tvJobQuestion = itemView.findViewById(R.id.tvJobQuestion);
                tvJobAnswer = itemView.findViewById(R.id.tvJobAnswer);
                llJobDetailsQA = itemView.findViewById(R.id.llJobDetailsQA);
                recyclerViewQuestionImage = itemView.findViewById(R.id.recyclerViewQuestionImage);
                rlQuestion = itemView.findViewById(R.id.rlQuestion);
                colour = Utility.getColor(this,R.color.search_background);
                rlQuestion.setBackgroundColor(colour);
                llJobQuesAns.addView(itemView);
                if(position == questionAndAnswer.size()-1)
                    viewJobQA.setVisibility(View.GONE);

                tvJobQuestions.setTypeface(appTypeface.getHind_semiBold());
                tvJobAnswers.setTypeface(appTypeface.getHind_regular());
                tvJobQuestion.setTypeface(appTypeface.getHind_bold());
                tvJobAnswer.setTypeface(appTypeface.getHind_bold());

                int qType = questionAndAnswer.get(position).getQuestionType();

                switch (qType)
                {
                    case 10:
                        recyclerViewQuestionImage.setVisibility(View.VISIBLE);
                        tvJobAnswers.setVisibility(View.GONE);
                        GridLayoutManager gridLayoutManager =  new GridLayoutManager(this,3);
                        tvJobQuestions.setText(questionAndAnswer.get(position).getQuestion());
                        String splitImage[] = questionAndAnswer.get(position).getAnswer().split(",");
                        if(splitImage.length>0)
                        {
                            ArrayList<QuestionImage>questionImages = new ArrayList<>();
                            for(int i =0; i<splitImage.length;i++)
                            {
                                questionImages.add(new QuestionImage(splitImage[i],true,i));
                            }
                            QuestionAdapterGrid questionAdapterGrid = new QuestionAdapterGrid(this,questionImages,null);
                            questionAdapterGrid.onIsQuestionInfo(true);
                            recyclerViewQuestionImage.setLayoutManager(gridLayoutManager);
                            recyclerViewQuestionImage.setAdapter(questionAdapterGrid);
                        }

                        break;
                    default:
                        recyclerViewQuestionImage.setVisibility(View.GONE);
                        tvJobAnswers.setVisibility(View.VISIBLE);
                        tvJobQuestions.setText(questionAndAnswer.get(position).getQuestion());
                        if(questionAndAnswer.get(position).getAnswer().matches("[0-9]{9,}"))
                        {
                            try {
                                Date date = new Date(Long.parseLong(questionAndAnswer.get(position).getAnswer()) * 1000L);
                                tvJobAnswers.setText(Utility.getFormattedDate(date));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else
                        {
                            tvJobAnswers.setText(questionAndAnswer.get(position).getAnswer());
                        }
                        if(qType != 4 || qType != 2 || qType != 3)
                        {
                            llBookingFor.setVisibility(View.VISIBLE);
                            viewIsVisible.setVisibility(View.VISIBLE);
                            viewPayment.setVisibility(View.VISIBLE);
                            lljobPaymentInfo.setVisibility(View.VISIBLE);
                            viewAddress.setVisibility(View.VISIBLE);
                        }
                        break;
                }


            }

        }
    }

    @Override
    public void onBidDispatchLog(ArrayList<BidDispatchLog> bidDispatchLog, int status) {

        for(int i = 0 ; i< bidDispatchLog.size(); i++)
        {
            bidDispatchLog.get(i).setCatName(catJobName);
            bidDispatchLog.get(i).setBid(bid);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if(statusCode==17)
                {
                    if(jobProviderInfo!=null)
                    {
                        jobProviderInfo.onBidJobInfoDetails(bidDispatchLog,statusCode);
                    }
                }
                if(messageFragment!=null && messageFragment.isAdded() && messageFragment.isVisible())
                {
                    if(status ==17)
                    {
                        messageFragment.notifyData(bidDispatchLog);
                    }

                }
            }
        }, 500);

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        isOnPauseCalled = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.isJobDetailsOpen = false;

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        if(isVisible)
        {

            ivFilter.setImageResource(R.drawable.ic_info_outline_black_24dp);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            presenter.showAnimationCardVied(true,cardJobViewContactInfo);
            rlReceiptDetails.setVisibility(View.GONE);
            llchecks.setVisibility(View.VISIBLE);
            isVisible = false;
            tvDetails.setSelected(false);
            //  rlReceiptDetails.startAnimation(gone);

        }else
        {

            onDestroyCalled();
            overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
        }


    }

    private void onDestroyCalled()
    {
        Constants.isJobDetailsOpen = false;
        MyBookingObservable.getInstance().removeObserver(observer);
        // myBookingObservable.removeObserver(observer);
        disposable.clear();
        disposable.dispose();
        finish();
    }



    @Override
    public void onIntentCall(Intent intent) {

        startActivityForResult(intent,HIRE_PROVIDER);
    }


    private boolean isBiddingAccepted = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == HIRE_PROVIDER)
        {

            if(resultCode == RESULT_OK)
            {
                MyBookingsFrag.onMyGetBooking();
                if(bookingType!=3)
                {
                    statusCode = 3;
                    isBiddingAccepted = true;
                    loadFragmentTransition(statusCode);
                    onUiStatusChange(statusCode);
                    serviceMethod();
                    if(bookingType == 2)
                    {
                        int eventId  = checkAndAddEvent(JobDetailsActivity.this,bid
                                ,bookingRequestedFor,manager);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                NotificationHandler notificationHandler = new NotificationHandler();
                                if(eventId!=0)
                                    notificationHandler.addReminderEventId(eventId,bid,manager);
                            }
                        }, 1000);
                    }

                }else
                    onDestroyCalled();

            }

        }
    }

    /**
     * <h>checkAndAddEvent</h>
     * Add the event in to the google calendar
     * @param mContext Context of the activity
     * @param bid booking id of the booked service
     * @param bookingRequestedFor booking time of the booked service
     * @param manager session manager object
     */

    private int checkAndAddEvent(Context mContext, long bid, long bookingRequestedFor, SessionManagerImpl manager) {
        CalendarEventHelper calendarEventHelper=new CalendarEventHelper(mContext);
        int eventId = 0;
        if(manager.getBookingStatus(bid)==3){
            eventId = calendarEventHelper.addEvent(bookingRequestedFor,bid);
        }
        return eventId;
    }

    String callProId,callName,callProPic;
    @Override
    public void onFragmentInteraction(boolean isVisible) {

        callName = proName;
        callProPic = proPic;
        changeContactInfoView(statusCode,isVisible);

    }

    private void callButtonClicked()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.System.canWrite(this) || !Settings.canDrawOverlays(this)) {
                if (!Settings.System.canWrite(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
                //If the draw over permission is not available open the settings screen
                //to grant the permission.

                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }else
                checkForPermission();

        } else
            checkForPermission();
    }

    private static final int REQUEST_CODE_PERMISSION_MULTIPLE = 144;
    private void checkForPermission() {



        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            ArrayList<AppPermissionsRunTime.MyPermissionConstants> myPermissionConstantsArrayList = new ArrayList<>();
            myPermissionConstantsArrayList.clear();
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_CAMERA);
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_RECORD_AUDIO);
            if (AppPermissionsRunTime.checkPermission(this, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE)) {

                selectStart();
            }
        } else
            selectStart();
    }

    /**
     * predefined method to check run time permissions list call back
     *
     * @param requestCode   request code
     * @param permissions:  contains the list of requested permissions
     * @param grantResults: contains granted and un granted permissions result list
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isDenine = false;
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_MULTIPLE:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isDenine = true;
                    }
                }
                if (isDenine) {
                    Toast.makeText(this, "Permission denied by the user", Toast.LENGTH_SHORT).show();
                } else {
                    selectStart();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    int callCallType;
    String callTypeValue;
    private void selectStart()
    {
        alertProgress.alertPositiveNegativeOnclick(this, getString(R.string.CallType), getString(R.string.call),
                getString(R.string.VideoCall), getString(R.string.AudioCall), true, new DialogInterfaceListner() {

                    @Override
                    public void dialogClick(boolean isClicked) {
                        int callType;
                        if(isClicked)
                        {
                            callType = 1;
                            callTypeValue = "video";
                        }

                        else
                        {
                            callType = 0;
                            callTypeValue = "audio";
                        }


                        callCallType = callType;
                        presenter.callInitCallApi(callProId, String.valueOf(System.currentTimeMillis()),chatApiService,bid+"",callTypeValue);

                    }
                });

    }

    @Override
    public void launchCallsScreen(String callId, String randomString) {
        CallingApis.initiateCall(JobDetailsActivity.this,callProId+"",callName,callProPic
                ,callCallType+"",phoneNumber,randomString,callId,bid+"",false);
        /*CallingApis.initiateCall(this, callProId, callName, callProPic,callType+"",
                phoneNumber, randomString ,callId,"",false);*/

    }

    private String randomString() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        sb.append("PnPLabs3Embed");
        return sb.toString();
    }

}

