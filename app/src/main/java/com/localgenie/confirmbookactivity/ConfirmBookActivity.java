package com.localgenie.confirmbookactivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.Login.LoginActivity;
import com.localgenie.R;
import com.localgenie.home.MainActivity;
import com.localgenie.home.ServicesFrag;
import com.localgenie.promocode.PromoCodeActivity;
import com.localgenie.selectPaymentMethod.SelectPayment;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.youraddress.YourAddressActivity;
import com.mqtt.MQTTManager;
import com.utility.AlertProgress;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import adapters.SelectedService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;
import com.pojo.CartModifiedData;
import com.utility.DialogInterfaceListner;
import com.utility.PermissionsManager;

public class ConfirmBookActivity extends DaggerAppCompatActivity implements
    ConfirmBookingContract.ContractView {

  private static final String TAG = ConfirmBookActivity.class.getSimpleName();
  private final int PROMOCODE = 11;
  private final int MY_PERMISSIONS_REQUEST_CALENDAR = 101;
  @BindView(R.id.toolBarConfirmBooking)
  Toolbar toolBarConfirmBooking;
  @BindView(R.id.tb_service_header)
  TextView tb_service_header;
  @BindView(R.id.tbServiceAvailable)
  TextView tbServiceAvailable;
  @BindView(R.id.tvWatIsJobLocation)
  TextView tvWatIsJobLocation;
  @BindView(R.id.tvConfirmLocation)
  TextView tvConfirmLocation;
  @BindView(R.id.tvAddressType)
  TextView tvAddressType;
  @BindView(R.id.tvAddressView)
  View tvAddressView;
  @BindView(R.id.tvConfirmFeeEstimate)
  TextView tvConfirmFeeEstimate;
  @BindView(R.id.tvConfirmVisitFee)
  TextView tvConfirmVisitFee;
  @BindView(R.id.tvConfirmVisitFeeAmt)
  TextView tvConfirmVisitFeeAmt;
  @BindView(R.id.tvConfirmTimeFee)
  TextView tvConfirmTimeFee;
  @BindView(R.id.tvConfirmTimeFeeAmt)
  TextView tvConfirmTimeFeeAmt;
  @BindView(R.id.tvConfirmDiscountFee)
  TextView tvConfirmDiscountFee;
  @BindView(R.id.tvConfirmDiscountFeeAmt)
  TextView tvConfirmDiscountFeeAmt;
  @BindView(R.id.tvConfirmTotalFee)
  TextView tvConfirmTotalFee;
  @BindView(R.id.tvConfirmTotalFeeAmt)
  TextView tvConfirmTotalFeeAmt;
  @BindView(R.id.tvConfirmPayment)
  TextView tvConfirmPayment;
  @BindView(R.id.tvConfirmJobDescription)
  TextView tvConfirmJobDescription;
  @BindView(R.id.etConfirmDescription)
  EditText etConfirmDescription;
  @BindView(R.id.tvConfirmBook)
  TextView tvConfirmBook;
  @BindView(R.id.rlMainConfirmBooking)
  RelativeLayout rlMainConfirmBooking;
  @BindView(R.id.rlMAin)
  RelativeLayout rlMAin;
  @BindView(R.id.progress_bar)
  ProgressBar progress_bar;
  @BindView(R.id.ivTickCheck)
  ImageView ivTickCheck;
  @BindView(R.id.tvYahBookingDone)
  TextView tvYahBookingDone;
  @BindView(R.id.tvWillLetUKnw)
  TextView tvWillLetUKnw;
  @BindView(R.id.tvTotalToPay)
  TextView tvTotalToPay;
  @BindView(R.id.tvTotalToPayAmt)
  TextView tvTotalToPayAmt;
  @BindView(R.id.tvConfirmViewBillDetails)
  TextView tvConfirmViewBillDetails;
  @BindView(R.id.tvConfirmBookFor)
  TextView tvConfirmBookFor;
  @BindView(R.id.tvConfirmBookingType)
  TextView tvConfirmBookingType;
  @BindView(R.id.tvInConfirmBookingTypeDesc)
  TextView tvInConfirmBookingTypeDesc;
  @BindView(R.id.tvConfirmPromocode)
  TextView tvConfirmPromocode;
  @BindView(R.id.tvSelectPaymentMethod)
  TextView tvSelectPaymentMethod;
  @BindView(R.id.llCardPayment)
  LinearLayout llCardPayment;
  @BindView(R.id.ivCardImageInfo)
  ImageView ivCardImageInfo;
  @BindView(R.id.tvcardInfo)
  TextView tvcardInfo;
  @BindView(R.id.recyclerViewService)
  RecyclerView recyclerViewService;
  @BindView(R.id.progressBarService)
  ProgressBar progressBarService;
  @BindView(R.id.scrollViewConfirm)
  NestedScrollView scrollViewConfirm;
  @BindView(R.id.llConfirmTotalDues)
  LinearLayout llConfirmTotalDues;
  @BindView(R.id.tvConfirmDuesPayInfo)
  TextView tvConfirmDuesPayInfo;
  @BindView(R.id.tvConfirmDuesAddress)
  TextView tvConfirmDuesAddress;
  @BindView(R.id.tvConfirmDuesDate)
  TextView tvConfirmDuesDate;
  @BindView(R.id.tvCancelDues)
  TextView tvCancelDues;
  @BindView(R.id.tvConfirmDues)
  TextView tvConfirmDues;
  @BindView(R.id.viewPromoCode)
  View viewPromoCode;
  /*******************************MultiShift********************************/

  @BindView(R.id.multipleShiftBooking)
  LinearLayout multipleShiftBooking;
  @BindView(R.id.tvConfirmMultiShift)
  TextView tvConfirmMultiShift;
  @BindView(R.id.tvConfirmStartTime)
  TextView tvConfirmStartTime;
  @BindView(R.id.tvConfirmStartDate)
  TextView tvConfirmStartDate;
  @BindView(R.id.tvConfirmEndTime)
  TextView tvConfirmEndTime;
  @BindView(R.id.tvConfirmEndDate)
  TextView tvConfirmEndDate;
  @BindView(R.id.tvToConfirm)
  TextView tvToConfirm;
  @BindView(R.id.tvConfirmNumberOfShift)
  TextView tvConfirmNumberOfShift;
  @BindView(R.id.tvConfirmShift)
  TextView tvConfirmShift;
  @BindView(R.id.tvConfirmAmountScheduleCharge)
  TextView tvConfirmAmountScheduleCharge;
  @BindView(R.id.llConfirmScheduledDays)
  LinearLayout llConfirmScheduledDays;
  @BindView(R.id.tvConfirmViewBillPerShift)
  TextView tvConfirmViewBillPerShift;
  @BindView(R.id.glInCall)
  GridLayout glInCall;
  @BindView(R.id.offInclude)
  LinearLayout offInclude;
  @BindView(R.id.tvTotalInfo)
  TextView tvTotalInfo;
  @BindView(R.id.rlOfferBidderMAin)
  RelativeLayout rlOfferBidderMAin;
  @BindView(R.id.NoOfBiddingShift)
  TextView NoOfBiddingShift;
  @BindView(R.id.NoOfBiddingShifts)
  TextView NoOfBiddingShifts;
  @BindView(R.id.tvPricePerShifts)
  TextView tvPricePerShifts;
  @BindView(R.id.tvPricePerShiftsAmt)
  TextView tvPricePerShiftsAmt;
  @BindView(R.id.tvBiddingDuration)
  TextView tvBiddingDuration;
  @BindView(R.id.tvBiddingDurationHR)
  TextView tvBiddingDurationHR;
  @BindView(R.id.rlOfferFee)
  RelativeLayout rlOfferFee;
  @BindView(R.id.tvConfirmOfferFee)
  TextView tvConfirmOfferFee;
  @BindView(R.id.tvConfirmOfferFe)
  TextView tvConfirmOfferFe;
  @BindView(R.id.tvConfirmOfferFeeAmt)
  TextView tvConfirmOfferFeeAmt;
  @BindView(R.id.llmultpleshift_day)
  LinearLayout llmultpleshift_day;
  @Inject
  MQTTManager mqttManager;
  @Inject
  AppTypeface appTypeface;
  @Inject
  SessionManagerImpl manager;
  @Inject
  ConfirmBookingContract.ContractPresenter presenter;
  @Inject
  CompositeDisposable disposable;
  @Inject
  AlertProgress alertProgress;
  @Inject
  PermissionsManager permissionsManager;
  int durationHour;
  /****************************End MultiShifts************************************/

  private BottomSheetBehavior sheetBehavior;
  private Animation hide, visible;
  private double bookingLat, bookingLng;
  private String bookingAddress;
  private int paymentType = 0;
  private String cardId = "";
  private String cartId = "";
  private double discountAmt, visitFee, serviceAmount;
  private ArrayList<CartModifiedData.ItemSelected> onSelectedCart = new ArrayList<>();
  private boolean isDueFound = false;
  private String itemSelected = "";
  private int i;
  private boolean isWalletSelected = false;
  private int hrNHalf, quantityInHr = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_confirm_book);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    ButterKnife.bind(this);
    hide = AnimationUtils.loadAnimation(this, R.anim.scaledown_progress_animation);
    visible = AnimationUtils.loadAnimation(this, R.anim.scaleup_progress_animation);
    multipleShiftTypeFace();
    toolSetValue();
    appTypeFace();
    work();
  }

  @Override
  protected void onResume() {
    super.onResume();
    // checkforCalendarPermission();
    //  checkCalendarPermissions();
  }

  private void multipleShiftTypeFace() {

    tvConfirmMultiShift.setTypeface(appTypeface.getHind_light());
    tvConfirmStartTime.setTypeface(appTypeface.getHind_light());
    tvConfirmStartDate.setTypeface(appTypeface.getHind_light());
    tvConfirmEndTime.setTypeface(appTypeface.getHind_light());
    tvConfirmEndDate.setTypeface(appTypeface.getHind_light());
    tvToConfirm.setTypeface(appTypeface.getHind_light());
    tvConfirmViewBillPerShift.setTypeface(appTypeface.getHind_light());
    tvConfirmShift.setTypeface(appTypeface.getHind_light());
    tvConfirmAmountScheduleCharge.setTypeface(appTypeface.getHind_light());
    tvConfirmNumberOfShift.setTypeface(appTypeface.getHind_semiBold());

    NoOfBiddingShift.setTypeface(appTypeface.getHind_regular());
    NoOfBiddingShifts.setTypeface(appTypeface.getHind_semiBold());

    tvPricePerShifts.setTypeface(appTypeface.getHind_regular());
    tvPricePerShiftsAmt.setTypeface(appTypeface.getHind_semiBold());

    tvBiddingDuration.setTypeface(appTypeface.getHind_regular());
    tvBiddingDurationHR.setTypeface(appTypeface.getHind_semiBold());


    tvConfirmOfferFee.setTypeface(appTypeface.getHind_light());
    tvConfirmOfferFe.setTypeface(appTypeface.getHind_light());
    tvConfirmOfferFeeAmt.setTypeface(appTypeface.getHind_light());

    if (Constants.bookingType == 3) {
      quantityInHr = Constants.scheduledTime / 60;
      hrNHalf = Constants.scheduledTime % 60;
      /**
       * setting minimum hour =1 if duration is less than an hour;
       */
      if (quantityInHr == 0) {
        quantityInHr = 1;
        hrNHalf = 0;
      }

      String duration = quantityInHr + " hr : " + hrNHalf + " mn";
      tvBiddingDurationHR.setText(duration);

      tvConfirmPromocode.setVisibility(View.GONE);
      viewPromoCode.setVisibility(View.GONE);
      glInCall.setVisibility(View.GONE);
      multipleShiftBooking.setVisibility(View.VISIBLE);
      tvConfirmViewBillPerShift.setText(getString(R.string.perShifts));

      tvConfirmStartTime.setText(Constants.repeatStartTime);
      tvConfirmStartDate.setText(Constants.repeatStartDate);
      tvConfirmEndTime.setText(Constants.repeatEndTime);
      tvConfirmEndDate.setText(Constants.repeatEndDate);

      String numberOfShift = Constants.repeatNumOfShift + "";
      tvConfirmNumberOfShift.setText(numberOfShift);
      NoOfBiddingShifts.setText(numberOfShift);

      for (int i = 0; i < Constants.repeatDays.size(); i++) {
        TextView tvWeekDays;
        View view = LayoutInflater.from(this).inflate(R.layout.booking_dates, multipleShiftBooking,
            false);
        llConfirmScheduledDays.addView(view);

        tvWeekDays = view.findViewById(R.id.tvWeekDays);

        tvWeekDays.setTypeface(appTypeface.getHind_light());
        String valueSelected = Constants.repeatDays.get(i).charAt(0) + "";
        tvWeekDays.setText(valueSelected);
      }
      tvTotalInfo.setVisibility(View.GONE);
      rlOfferBidderMAin.setVisibility(View.GONE);
      offInclude.setVisibility(View.VISIBLE);
      String off;
      if (Constants.bookingOffer != 0) {
        //  rlOfferFee.setVisibility(View.VISIBLE);
        tvConfirmOfferFe.setText(Constants.offerName);
        if (Constants.offerType == 1) {
          Utility.setAmtOnRecept(Constants.bookingOffer, tvConfirmOfferFeeAmt,
              Constants.currencySymbol);
        } else {
          off = Constants.bookingOffer + "%";
          tvConfirmOfferFeeAmt.setText(off);
        }
      }
    } else if (Constants.bookingType == 2
        && Constants.jobType == 2) { //If out call and single shift booking
      //Setting ui for single shift booking
      glInCall.setVisibility(View.GONE);
      multipleShiftBooking.setVisibility(View.VISIBLE);
      SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
      SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMMM-yyyy");
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(Long.parseLong(Constants.scheduledDate) * 1000);
      Calendar calendarEndTime = Calendar.getInstance();
      String startTime = timeFormat.format(calendar.getTime());
      String[] split = Constants.selectedDuration.split(":");

      llmultpleshift_day.setVisibility(View.GONE);
      tvConfirmAmountScheduleCharge.setVisibility(View.GONE);
      String[] hours = split[0].trim().split(" ");

      calendarEndTime.setTimeInMillis(
          (calendar.getTimeInMillis() + TimeUnit.HOURS.toMillis(Long.parseLong(hours[0]))));
      tvConfirmMultiShift.setText(R.string.scheduled_single_shift);
      tvConfirmNumberOfShift.setText(String.format("%02d", Integer.parseInt(hours[0])) + " : 00");
      durationHour = Integer.parseInt(hours[0]);
      quantityInHr = durationHour;
      tvConfirmShift.setText(R.string.hour);
      String endTime = timeFormat.format(calendarEndTime.getTime());
      String startDateFormatted = dateFormat.format(calendar.getTime());
      String endDateFormatted = dateFormat.format(calendarEndTime.getTime());
      tvConfirmStartTime.setText(startTime);
      tvConfirmEndTime.setText(endTime);
      tvConfirmStartDate.setText(startDateFormatted);
      tvConfirmEndDate.setText(endDateFormatted);
      //Setting ui for single shift booking
    } else if (Constants.bookingType == 2 && Constants.jobType == 3
        || Constants.jobType == 1) { //If out call and single shift booking
      //Setting ui for single shift booking
      glInCall.setVisibility(View.GONE);
      multipleShiftBooking.setVisibility(View.VISIBLE);
      SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
      SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMMM-yyyy");
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(Long.parseLong(Constants.scheduledDate) * 1000);
      Calendar calendarEndTime = Calendar.getInstance();
      String startTime = timeFormat.format(calendar.getTime());
      // String[] split = Constants.selectedDuration.split(":");
      llmultpleshift_day.setVisibility(View.GONE);
      tvConfirmAmountScheduleCharge.setVisibility(View.GONE);
      // String[] hours = split[0].trim().split(" ");
      String[] splitTime = Constants.selectedDuration.split(":");
      Log.d(TAG, "multipleShiftTypeFace: " + splitTime[0] + "  " + splitTime[1]);

      String[] hours = splitTime[0].trim().split(" ");
      String[] minutes = splitTime[1].trim().split(" ");
      Log.d(TAG, "multipleShiftTypeFace: " + hours[0] + "  " + minutes[1]);
      calendarEndTime.setTimeInMillis(
          (calendar.getTimeInMillis() + TimeUnit.HOURS.toMillis(Long.parseLong(hours[0]))
              + TimeUnit.MINUTES.toMillis(Long.parseLong(minutes[1]))));
      tvConfirmMultiShift.setText(R.string.scheduled_shift);
      tvConfirmNumberOfShift.setText(
          String.format("%02d", Integer.parseInt(hours[0])) + " : " + String.format("%02d",
              Integer.parseInt(minutes[1])));
      tvConfirmShift.setText(R.string.hour);
      String endTime = timeFormat.format(calendarEndTime.getTime());
      String startDateFormatted = dateFormat.format(calendar.getTime());
      String endDateFormatted = dateFormat.format(calendarEndTime.getTime());
      tvConfirmStartTime.setText(startTime);
      tvConfirmEndTime.setText(endTime);
      tvConfirmStartDate.setText(startDateFormatted);
      tvConfirmEndDate.setText(endDateFormatted);
      //Setting ui for single shift booking
    } else {
      glInCall.setVisibility(View.VISIBLE);
      multipleShiftBooking.setVisibility(View.GONE);
    }
  }

  private void toolSetValue() {
    sheetBehavior = BottomSheetBehavior.from(llConfirmTotalDues);
    setSupportActionBar(toolBarConfirmBooking);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    tb_service_header.setTextSize(18);
    tb_service_header.setText(Constants.catName);
    tb_service_header.setTypeface(appTypeface.getHind_semiBold());
    tbServiceAvailable.setTypeface(appTypeface.getHind_regular());
    toolBarConfirmBooking.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
    toolBarConfirmBooking.setNavigationOnClickListener(view -> onBackPressed());
  }

  private void appTypeFace() {
    tvYahBookingDone.setTypeface(appTypeface.getHind_semiBold());
    tvWillLetUKnw.setTypeface(appTypeface.getHind_regular());
    tvWatIsJobLocation.setTypeface(appTypeface.getHind_medium());
    tvConfirmPromocode.setTypeface(appTypeface.getHind_medium());
    tvConfirmLocation.setTypeface(appTypeface.getHind_light());
    tvInConfirmBookingTypeDesc.setTypeface(appTypeface.getHind_light());
    tvConfirmBookFor.setTypeface(appTypeface.getHind_medium());
    tvConfirmBookingType.setTypeface(appTypeface.getHind_medium());
    tvConfirmFeeEstimate.setTypeface(appTypeface.getHind_medium());
    tvConfirmVisitFee.setTypeface(appTypeface.getHind_light());
    tvConfirmVisitFeeAmt.setTypeface(appTypeface.getHind_light());
    tvConfirmTimeFee.setTypeface(appTypeface.getHind_light());
    tvConfirmTimeFeeAmt.setTypeface(appTypeface.getHind_light());
    tvConfirmDiscountFee.setTypeface(appTypeface.getHind_light());
    tvConfirmDiscountFeeAmt.setTypeface(appTypeface.getHind_light());
    tvConfirmBook.setSelected(false);
    // for now hide the visiblity of the discount amount
    tvConfirmDiscountFee.setVisibility(View.GONE);
    tvConfirmDiscountFeeAmt.setVisibility(View.GONE);
    tvConfirmTotalFee.setTypeface(appTypeface.getHind_semiBold());
    tvConfirmTotalFeeAmt.setTypeface(appTypeface.getHind_semiBold());
    tvConfirmPayment.setTypeface(appTypeface.getHind_medium());

    tvConfirmJobDescription.setTypeface(appTypeface.getHind_medium());
    etConfirmDescription.setTypeface(appTypeface.getHind_light());
    tvConfirmBook.setTypeface(appTypeface.getHind_semiBold());
    tvSelectPaymentMethod.setTypeface(appTypeface.getHind_regular());
    tvcardInfo.setTypeface(appTypeface.getHind_regular());
    tvConfirmViewBillDetails.setTypeface(appTypeface.getHind_regular());
    tvTotalToPayAmt.setTypeface(appTypeface.getHind_semiBold());

    tvConfirmDues.setTypeface(appTypeface.getHind_semiBold());
    tvCancelDues.setTypeface(appTypeface.getHind_semiBold());
    tvConfirmDuesPayInfo.setTypeface(appTypeface.getHind_semiBold());
    tvConfirmDuesAddress.setTypeface(appTypeface.getHind_medium());
    tvConfirmDuesDate.setTypeface(appTypeface.getHind_regular());

    /***********************MultiPleShift*******************/
  }

  private void work() {

    discountAmt = 0.00;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    recyclerViewService.setLayoutManager(linearLayoutManager);
    recyclerViewService.setNestedScrollingEnabled(false);

    if (Constants.jobType == 1 || Constants.jobType == 3) //incall , telecall
    {
      bookingLat = Constants.proLatitude;
      bookingLng = Constants.proLongitude;
      tvConfirmLocation.setText(Constants.proAddress);
      bookingAddress = Constants.proAddress;
      Log.d(TAG, "LATLNGwork: " + bookingLat);
      tvWatIsJobLocation.setText(getString(R.string.providerLocation));
      tvConfirmLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0);
      if (Constants.jobType == 3) {
        tvConfirmLocation.setVisibility(View.GONE);
        tvWatIsJobLocation.setVisibility(View.GONE);
        tvAddressView.setVisibility(View.GONE);
      }
    } else { //outcall
      tvConfirmLocation.setText(manager.getAddress());
      bookingAddress = manager.getAddress();
      if (Constants.serviceType == 1) {
        try {
          bookingLat = Double.parseDouble(manager.getLatitude());
          bookingLng = Double.parseDouble(manager.getLongitude());
        } catch (Exception e) {
          e.printStackTrace();
        }

      } else {
        if (Constants.lat != 0.0) {
          bookingLat = Constants.lat;
          bookingLng = Constants.lng;
        } else {
          bookingLat = Double.parseDouble(manager.getLatitude());
          bookingLng = Double.parseDouble(manager.getLongitude());
        }
      }
    }

    progressBarService.setVisibility(View.VISIBLE);
    visitFee = Constants.visitFee;
    if (visitFee > 0) {
      totalToPay(visitFee, discountAmt, serviceAmount);
    } else {
      tvConfirmVisitFee.setVisibility(View.GONE);
      tvConfirmVisitFeeAmt.setVisibility(View.GONE);
    }
    if (Constants.serviceType == 1 && Constants.bookingModel == 3) {
      Constants.serviceSelected = 2;
      if (Constants.jobType != 2) {
        tbServiceAvailable.setVisibility(View.GONE);
      } else {
        String hourly = "Hourly @ " + Constants.currencySymbol + Constants.pricePerHour + " /hr(s)";
        tbServiceAvailable.setText(hourly);
      }

      if (Constants.bookingType == 3) {
        String priceIs = Constants.currencySymbol + " " + (Constants.pricePerHour + visitFee);
        tvPricePerShiftsAmt.setText(priceIs);
      }
    }
    callGetCartId();

    if (ServicesFrag.paymentMode.isCard()) {
      if (!"".equals(manager.getDefaultCardNum())) {
        cardId = manager.getDefaultCardId();
        setCardInfo(manager.getDefaultCardName(), manager.getDefaultCardNum());
        tvConfirmBook.setSelected(true);
      } else {
        paymentType = 0;
      }
    } else {
      paymentType = 0;
    }


    if (Constants.bookingType == 2 && Constants.jobType == 2) {
      tvConfirmBookingType.setText(getString(R.string.schedule));
      tvConfirmBookFor.setText(
          getString(R.string.booking_for) + " (" + getString(R.string.schedule) + ")");
      Log.d(TAG, "work: " + Constants.scheduledDate);
      tvInConfirmBookingTypeDesc.setText(
          Constants.selectedDate + "\n" + "Duration " + Constants.selectedDuration + " hr(s)");
    } else if (Constants.bookingType == 2 && Constants.jobType == 3) {
      tvConfirmBookingType.setText(getString(R.string.scheduled_shift));
      tvConfirmBookFor.setText(
          getString(R.string.shift_summary) + " (" + getString(R.string.outTeleCall) + ")");
      Log.d(TAG, "work: " + Constants.scheduledDate);
      tvInConfirmBookingTypeDesc.setText(
          Constants.selectedDate + "\n" + "Duration " + Constants.selectedDuration + " hr(s)");
    }
    if (Constants.jobType == 1) {
      tvConfirmBookingType.setText(
          getString(R.string.booking_for) + " (" + getString(R.string.inCall) + ")");
      presenter.timeMethod(tvInConfirmBookingTypeDesc, Constants.fromTime);
    } else if (Constants.jobType == 3) {
      tvConfirmBookingType.setText(
          getString(R.string.booking_for) + " (" + getString(R.string.outTeleCall) + ")");
      presenter.timeMethod(tvInConfirmBookingTypeDesc, Constants.fromTime);
    }
  }

  private void callGetCartId() {
    if (alertProgress.isNetworkAvailable(this)) {
      presenter.onGetCartId();
    } else {
      alertProgress.showNetworkAlert(this);
    }
  }

  @OnClick({
      R.id.tvConfirmLocation, R.id.tvConfirmBook, R.id.tvcardInfo
      , R.id.tvConfirmViewBillDetails, R.id.tvCancelDues, R.id.tvConfirmDues,
      R.id.tvConfirmPromocode})
  public void onClickOfTheView(View v) {
    Intent intent;
    switch (v.getId()) {
      case R.id.tvConfirmPromocode:
        if (tvConfirmPromocode.getText().toString().trim().equals(getString(R.string.promocode))) {
          if (paymentType != 0) {
            Bundle bundle = new Bundle();
            intent = new Intent(this, PromoCodeActivity.class);
            bundle.putDouble("BookingLat", bookingLat);
            bundle.putDouble("BookingLng", bookingLng);
            bundle.putString("cartId", cartId);
            bundle.putInt("PaymentMethod", paymentType);
            intent.putExtras(bundle);
            startActivityForResult(intent, PROMOCODE);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay_still);
          } else {
            alertProgress.alertinfo(this, getString(R.string.pleaseChoosePayment));
          }
        } else {
          tvConfirmPromocode.setText(getString(R.string.promocode));
          tvConfirmDiscountFee.setVisibility(View.GONE);
          tvConfirmDiscountFeeAmt.setVisibility(View.GONE);
          discountAmt = 0;
          totalToPay(visitFee, discountAmt, serviceAmount);
          tvConfirmPromocode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_discount_icon, 0,
              R.drawable.ic_arrow, 0);
        }

        break;
      case R.id.tvConfirmViewBillDetails:
        scrollViewConfirm.post(() -> scrollViewConfirm.fullScroll(View.FOCUS_DOWN));
        break;
      case R.id.tvConfirmBook:
        if (manager.getGuestLogin()) {
          intent = new Intent(this, LoginActivity.class);
          startActivity(intent);
        } else {
          if (paymentType != 0) {
            if (!isDueFound) {
              onDuesApiCall();
            } else {
              onDuesFoundBehaviour();
            }
          } else {
            alertProgress.alertinfo(this, getString(R.string.pleaseChoosePayment));
          }
        }
        break;

      case R.id.tvcardInfo:
        if (manager.getGuestLogin()) {
          intent = new Intent(this, LoginActivity.class);
          startActivity(intent);
        } else {
          intent = new Intent(this, SelectPayment.class);
          intent.putExtra("isNotFormPayment", false);
          startActivityForResult(intent, Constants.PAYMENT_RESULT_CODE);
          overridePendingTransition(R.anim.slide_in_up, R.anim.stay_still);
        }

        break;
      case R.id.tvConfirmLocation:
        if (manager.getGuestLogin()) {
          intent = new Intent(this, LoginActivity.class);
          startActivity(intent);
        } else {
          if (Constants.jobType == 2) {
            intent = new Intent(this, YourAddressActivity.class);
            intent.putExtra("isNotFromAddress", false);
            startActivityForResult(intent, Constants.ADDRESS_RESULT_CODE);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay_still);
          }

        }
        break;
      case R.id.tvCancelDues:
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
          sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        break;
      case R.id.tvConfirmDues:
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
          sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        callOnCliveBooking();
        break;
    }
  }

  private void onDuesApiCall() {
    if (alertProgress.isNetworkAvailable(this)) {
      onShowProgress();
      presenter.lastDues();
    } else {
      alertProgress.showNetworkAlert(this);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case Constants.ADDRESS_RESULT_CODE:
          if (data != null) {
            bookingLat = data.getDoubleExtra("lat", 0.0);
            bookingLng = data.getDoubleExtra("lng", 0.0);
            bookingAddress = data.getStringExtra("AddressLine1");

            String tag = data.getStringExtra("TAGAS");
            String bookingAddress2 = data.getStringExtra("AddressLine2");
            Log.d(TAG, "onActivityResult: " + bookingLat + " lang " + bookingLng + " add "
                + bookingAddress + " add2 " + bookingAddress2);
            tvConfirmLocation.setText(bookingAddress);
            manager.setAddress(bookingAddress);
            tvAddressType.setVisibility(View.VISIBLE);
            // tvAddressType.setText(tag);
            callSetImageInTextView(tag, bookingAddress);


          }
          break;

        case Constants.PAYMENT_RESULT_CODE:
          if (data != null) {

            isWalletSelected = data.getBooleanExtra("ISWallet", false);
            paymentType = data.getIntExtra("PAYMENTTYPE", 1);
            if (isWalletSelected) {
              tvcardInfo.setText(getString(R.string.wallet));
              Bitmap bitmap = Utility.setCreditCardLogo("Wallet", this);
              cardId = data.getStringExtra("CARDID");
              ivCardImageInfo.setVisibility(View.VISIBLE);
              ivCardImageInfo.setImageResource(R.drawable.ic_account_balance_wallet_black_24dp);
            } else {
              if (paymentType == 1) {
                tvcardInfo.setText(getString(R.string.cash));
                ivCardImageInfo.setVisibility(View.VISIBLE);
                //  ivCardImageInfo.setImageResource(R.drawable.ic_cash_icon);
                Bitmap bitmap = Utility.setCreditCardLogo("Cash", this);
                ivCardImageInfo.setImageBitmap(bitmap);
              } else if (paymentType == 2) {
                // tvSelectPaymentMethod.setVisibility(View.GONE);

                cardId = data.getStringExtra("CARDID");
                String cardType = data.getStringExtra("CARDTYPE");
                String last4 = data.getStringExtra("LAST4");

                Log.d(TAG, "onActivityResultCARD: " + cardId + " CardType " + cardType
                    + " last4 " + last4);

                setCardInfo(cardType, last4);
                manager.setDefaultCardId(cardId);
                manager.setDefaultCardNum(last4);
                manager.setDefaultCardName(cardType);

              }
            }
            tvConfirmBook.setSelected(true);

          }
          break;
        case PROMOCODE:
          if (data != null) {
            discountAmt = data.getDoubleExtra("DISCOUNTAMOUNT", 0);
            String promoCode = data.getStringExtra("PROMOCODE");
            tvConfirmPromocode.setText(promoCode);
            tvConfirmDiscountFee.setVisibility(View.VISIBLE);
            tvConfirmDiscountFeeAmt.setVisibility(View.VISIBLE);
            totalToPay(visitFee, discountAmt, serviceAmount);
            tvConfirmPromocode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_discount_icon,
                0, R.drawable.ic_clear_black_24dp, 0);

          }
          break;
      }
    }

  }

  private void callSetImageInTextView(String tag, String bookingAddress) {
    String bookingAddres;
    switch (tag) {
      case "home":
        tvAddressType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvAddressType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home, 0, 0, 0);
        tvAddressType.setText(getString(R.string.home));
        presenter.setAddressWithImage(tvConfirmLocation, bookingAddress);
        break;

      case "office":
        tvAddressType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvAddressType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_work, 0, 0, 0);
        tvAddressType.setText(getString(R.string.work));
        presenter.setAddressWithImage(tvConfirmLocation, bookingAddress);
        break;

      default:
        tvAddressType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvAddressType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_other_addr, 0, 0, 0);
        tvAddressType.setText(tag);
        presenter.setAddressWithImage(tvConfirmLocation, bookingAddress);
        break;
    }
    tvAddressType.setCompoundDrawablePadding(50);
  }

  private void setCardInfo(String cardType, String last4) {
    paymentType = 2;
    ivCardImageInfo.setVisibility(View.VISIBLE);
    llCardPayment.setVisibility(View.VISIBLE);
    Bitmap bitmap = Utility.setCreditCardLogo(cardType, this);
    ivCardImageInfo.setImageBitmap(bitmap);
    String cardLast4 = getString(R.string.stars) + " " + last4;
    tvcardInfo.setText(cardLast4);

  }

  @Override
  public void onSessionExpired() {
    rlMainConfirmBooking.setVisibility(View.VISIBLE);
    rlMAin.setVisibility(View.GONE);
  }

  @Override
  public void onLogout(String message) {

    alertProgress.alertPositiveOnclick(this, message, getString(R.string.logout),
        getString(R.string.ok), new DialogInterfaceListner() {
          @Override
          public void dialogClick(boolean isClicked) {
            Utility.setMAnagerWithBID(ConfirmBookActivity.this, manager);
          }
        });

  }

  @Override
  public void onError(String error) {
    onSessionExpired();
    alertProgress.alertinfo(this, error);

  }

  @Override
  public void onShowProgress() {

    rlMainConfirmBooking.setVisibility(View.GONE);
    rlMAin.setVisibility(View.VISIBLE);
  }

  @Override
  public void onHideProgress() {

    progress_bar.setVisibility(View.GONE);
    progress_bar.startAnimation(hide);
    ivTickCheck.setVisibility(View.VISIBLE);
    ivTickCheck.startAnimation(visible);
    tvYahBookingDone.setVisibility(View.VISIBLE);
    tvWillLetUKnw.setVisibility(View.VISIBLE);
  }

  @Override
  public void onSuccessBooking() {

    new Handler().postDelayed(() -> {
      Constants.isConfirmBook = true;
      Constants.isJobDetailsOpen = false;
      Constants.bookingModel = 2;
      Constants.catId = "";
      Constants.subCatId = "";
      Constants.scheduledDate = "";
      Constants.proId = "0";
      Constants.scheduledTime = 0;
      disposable.clear();
      Intent intent = new Intent(ConfirmBookActivity.this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      finish();
    }, 1000);

  }

  @Override
  public void onSuccessCartId(String cartId) {
    this.cartId = cartId;
  }

  @Override
  public void onCartModification(CartModifiedData.DataSelected data) {
    if (data != null) {
      progressBarService.setVisibility(View.GONE);

      cartId = data.getCatId();

      serviceAmount = data.getTotalAmount();

      if (!tvConfirmPromocode.getText().toString().trim().equals(getString(R.string.promocode))) {
        presenter.callPromoCodeApi(bookingLat, bookingLng, paymentType, cartId,
            tvConfirmPromocode.getText().toString().trim());
      }

      if (Constants.serviceSelected == 2) {
        if (Constants.bookingType == 2 && Constants.jobType == 2) {
          tvConfirmBookingType.setText(getString(R.string.schedule));
          tvInConfirmBookingTypeDesc.setText(
              Constants.selectedDate + "\n" + "Duration " + data.getTotalQuntity() + " hr(s)");
        }
        String hourly;
        if (Constants.jobType != 2) {
          tbServiceAvailable.setVisibility(View.GONE);
        } else {
          hourly = "Hourly @ " + Constants.currencySymbol + data.getTotalAmount() + " for "
              + data.getTotalQuntity() + " hr(s)";
          tbServiceAvailable.setText(hourly);
        }

        if (Constants.bookingType == 3) {

          double totalPerShift = 0;
          if (Constants.bookingOffer != 0) {
            discountAmt = Constants.bookingOffer;
            if (Constants.offerType == 2) {
              discountAmt = (discountAmt / 100) * ((serviceAmount + visitFee));
              double OfferTotal = ((serviceAmount + visitFee)) - discountAmt;
              //totalPerShift = OfferTotal/Constants.repeatNumOfShift;
            } else {
              totalPerShift = ((serviceAmount + visitFee)) - (discountAmt);
            }
            Utility.setAmtOnRecept(discountAmt, tvConfirmDiscountFeeAmt,
                Constants.currencySymbol);
          } else {
            totalPerShift = (serviceAmount + visitFee) - discountAmt;
          }


          String priceIs = Constants.currencySymbol + " "
              + (totalPerShift);// Constants.pricePerHour;//data.getTotalAmount()+visitFee
          tvPricePerShiftsAmt.setText(priceIs);
          tvConfirmViewBillPerShift.setText(priceIs + getString(R.string.per_shift));
          tvTotalToPayAmt.setText(priceIs + getString(R.string.per_shift));
          String duration = data.getTotalQuntity() + " hr : " + hrNHalf + " mn";
          tvBiddingDurationHR.setText(duration);
          checkForHours(data.getTotalQuntity());

        }
      } else {
        String toolBarQuant = data.getTotalQuntity() + " service(s)   " + Constants.currencySymbol
            + data.getTotalAmount();
        tbServiceAvailable.setText(toolBarQuant);

        double totalPerShift = 0;
        if (Constants.bookingOffer != 0) {
          discountAmt = Constants.bookingOffer;
          if (Constants.offerType == 2) {
            discountAmt = (discountAmt / 100) * ((serviceAmount + visitFee));
            double OfferTotal = ((serviceAmount + visitFee)) - discountAmt;
            //totalPerShift = OfferTotal/Constants.repeatNumOfShift;
          } else {
            totalPerShift = ((serviceAmount + visitFee)) - (discountAmt);
          }
          Utility.setAmtOnRecept(discountAmt, tvConfirmDiscountFeeAmt,
              Constants.currencySymbol);
        } else {
          totalPerShift = (serviceAmount + visitFee) - discountAmt;
        }

        tvTotalToPayAmt.setText(
            Constants.currencySymbol + totalPerShift + getString(R.string.per_shift));
        tvConfirmViewBillPerShift.setText(
            Constants.currencySymbol + totalPerShift + getString(R.string.per_shift));

        Utility.setAmtOnRecept(totalPerShift, tvPricePerShiftsAmt, Constants.currencySymbol);
      }

      if (data.getItem().size() > 0) {
        onSelectedCart.clear();
        for (int i = 0; i < data.getItem().size(); i++) {
          if (data.getItem().get(i).getQuntity() > 0) {
            onSelectedCart.add(data.getItem().get(i));
          }
        }
      }
      totalToPay(visitFee, discountAmt, serviceAmount);

      if (onSelectedCart.size() > 0) {
        SelectedService selectedServiceAdapter = new SelectedService(this, true);
        selectedServiceAdapter.onSelectedInterFace(this, onSelectedCart);
        recyclerViewService.setAdapter(selectedServiceAdapter);
        selectedServiceAdapter.notifyDataSetChanged();

      } else {
        onBackPressed();
      }

    }
  }

  private void checkForHours(int totalQuntity) {

    String startTime = tvConfirmStartTime.getText().toString().trim();
    String[] splitTime = convertAmPm(startTime).split(":");
    try {
      int intStartTime = Integer.parseInt(splitTime[0]);
      Log.d(TAG, "checkForHours: " + intStartTime);

      int increasedHr = intStartTime + totalQuntity;
      if (increasedHr > 24) {
        increasedHr = increasedHr - 24;
      } else if (increasedHr == 24) {
        increasedHr = increasedHr - 24;
      }
      Log.d(TAG, "checkForHours: " + increasedHr);
      String date = increasedHr + ":" + splitTime[1];
      String dateEnd, timeEnd;
      if (intStartTime >= 12) {

        if (increasedHr < intStartTime) {
          timeEnd = convertTo24Hr(date);
          if (Constants.repeatEndDate.contains("(+1)")) {
            dateEnd = Constants.repeatEndDate;
          } else {
            dateEnd = Constants.repeatEndDate + "(+1)";
          }
          tvConfirmEndDate.setText(dateEnd);
          //Increase by a day
        } else {
          timeEnd = convertTo24Hr(date);
          tvConfirmEndDate.setText(Constants.repeatEndDate);
        }
      } else {
        if (increasedHr < intStartTime) {
          timeEnd = convertTo24Hr(date);
          if (Constants.repeatEndDate.contains("(+1)")) {
            dateEnd = Constants.repeatEndDate;
          } else {
            dateEnd = Constants.repeatEndDate + "(+1)";
          }

          tvConfirmEndDate.setText(dateEnd);
        } else {
          timeEnd = convertTo24Hr(date);
          tvConfirmEndDate.setText(Constants.repeatEndDate);
        }
      }
      tvConfirmEndTime.setText(timeEnd);

    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
  }

  private String convertTo24Hr(String datTime) {

    SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    SimpleDateFormat displayFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
    Date date = null;
    String dateFormat = "";
    try {
      date = parseFormat.parse(datTime);
      dateFormat = displayFormat.format(date);
      return dateFormat;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return dateFormat;
  }

  private String convertAmPm(String datTime) {

    SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    SimpleDateFormat parseFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
    Date date = null;
    String dateFormat = "";
    try {
      date = parseFormat.parse(datTime);
      dateFormat = displayFormat.format(date);
      return dateFormat;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return dateFormat;
  }

  @Override
  public void onCartModified(String itemSelected, int i) {
    this.itemSelected = itemSelected;
    this.i = i;

    if (alertProgress.isNetworkAvailable(this)) {
      progressBarService.setVisibility(View.VISIBLE);
      presenter.onAddSubCartModifyData(Constants.catId, itemSelected, i, quantityInHr);
    } else {
      alertProgress.showNetworkAlert(this);
    }

  }

  @Override
  public void onHidePro() {
    progressBarService.setVisibility(View.GONE);
  }

  @Override
  public void onHourly() {
    Constants.serviceSelected = 2;
    Log.d(TAG, "onHourly: " + Constants.selectedDuration);
    Log.d(TAG, "onHourly: " + Constants.selectedDuration);
    presenter.onAddSubCartModifyData(Constants.catId, "" + 1, 1, quantityInHr);
  }

  @Override
  public void onAlreadyCartPresent(String message, boolean b) {

    if (b) {
      ConfirmBookActivity.this.onHourly();
    } else {
      ConfirmBookActivity.this.onBackPressed();
    }
/*
        alertProgress.alertPositiveNegativeOnclick(ConfirmBookActivity.this, message, getString(R
        .string.itemsAlreadyInCart),getResources().getString(R.string.ok), getResources()
        .getString(R.string.cancel),false , new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                if (isClicked)
                    ConfirmBookActivity.this.onHourly();
                else
                    ConfirmBookActivity.this.onBackPressed();
            }
        });
*/
  }

  @Override
  public void noDuesFoundLiveBooking() {
    presenter.serverTime();
  }

  @Override
  public void CallLiveBook() {
    String promo = "";
    if (!tvConfirmPromocode.getText().toString().trim().equals(getString(R.string.promocode))) {
      promo = tvConfirmPromocode.getText().toString().trim();
    }
    presenter.onLiveBookingService(paymentType, isWalletSelected,
        etConfirmDescription.getText().toString(),
        promo, cardId, cartId, bookingLat, bookingLng, bookingAddress);
  }

  @Override
  public void onDuesFound(String msg, String addLine1, String formattedDate) {

    tvConfirmDuesPayInfo.setText(msg);
    tvConfirmDuesAddress.setText(addLine1);
    tvConfirmDuesDate.setText(formattedDate);
    onDuesFoundBehaviour();
  }

  @Override
  public void onConnectionError(String message, String cartId, String hourly) {
    alertProgress.tryAgain(this, getString(R.string.pleaseCheckInternet),
        getString(R.string.system_error), isClicked ->
        {
          if (isClicked) {
            if ("AddCart".equals(cartId)) {
              if ("hourly".equals(hourly)) {
                onHourly();
              } else {
                if (!"".equals(itemSelected)) {
                  onCartModified(itemSelected, i);
                }
              }

            } else if ("LastDues".equals(cartId)) {
              onDuesApiCall();
            } else if ("CartId".equals(cartId)) {
              callGetCartId();
            } else if ("LiveBooking".equals(cartId)) {
              callOnCliveBooking();
            }
          }
        });
  }

  @Override
  public void promoCode(double amount, String proCode) {

    discountAmt = amount;
    tvConfirmPromocode.setText(proCode);
    tvConfirmDiscountFee.setVisibility(View.VISIBLE);
    tvConfirmDiscountFeeAmt.setVisibility(View.VISIBLE);
    totalToPay(visitFee, discountAmt, serviceAmount);
  }

  private void callOnCliveBooking() {
    if (alertProgress.isNetworkAvailable(this)) {
      onShowProgress();
      noDuesFoundLiveBooking();
    } else {
      alertProgress.showNetworkAlert(this);
    }
  }

  private void onDuesFoundBehaviour() {
    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
      sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      //  btnBottomSheet.setText("Close sheet");
    }
    isDueFound = true;
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
    overridePendingTransition(R.anim.mainfadein, R.anim.side_slide_in);
  }

  public void totalToPay(double visitFee, double discountAmt, double serviceAmount) {
    Utility.setAmtOnRecept(visitFee, tvConfirmVisitFeeAmt, Constants.currencySymbol);
    Utility.setAmtOnRecept(discountAmt, tvConfirmDiscountFeeAmt, Constants.currencySymbol);
    double total;
    if (Constants.bookingType == 3) {
      if (Constants.bookingOffer != 0) {
        discountAmt = Constants.bookingOffer;
        if (Constants.offerType == 2) {
          discountAmt =
              (discountAmt / 100) * ((serviceAmount + visitFee)
                  * Constants.repeatNumOfShift);
          double OfferTotal =
              ((serviceAmount + visitFee) * Constants.repeatNumOfShift) - discountAmt;
          total = OfferTotal / Constants.repeatNumOfShift;
        } else {
          total = ((serviceAmount + visitFee) * Constants.repeatNumOfShift) - (discountAmt
              * Constants.repeatNumOfShift);
        }
        Utility.setAmtOnRecept(discountAmt, tvConfirmDiscountFeeAmt,
            Constants.currencySymbol);
      } else {
        total = ((serviceAmount + visitFee) - discountAmt) * Constants.repeatNumOfShift;
      }
    } else {
      total = serviceAmount + visitFee - discountAmt;
    }

    Utility.setAmtOnRecept(total, tvTotalToPayAmt, Constants.currencySymbol);
    //tvTotalToPayAmt.setText("");
  }

  private void checkCalendarPermissions() {
    if (permissionsManager.areRuntimePermissionsRequired()) {
      if (permissionsManager.isCalendarWritePermissionGranted(this)) {
        permissionsManager.requestCalendarPermission(this);
      }
    }
  }

  private void checkforCalendarPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
        != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.READ_CONTACTS)) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CALENDER PERMISSION");
        builder.setMessage("Need calendar permission to set remainder on later booking !");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            ActivityCompat.requestPermissions(ConfirmBookActivity.this,
                new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_CALENDAR);
          }
        });
        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        });
        builder.create().show();
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR},
            MY_PERMISSIONS_REQUEST_CALENDAR);
      }
    }
  }
}
