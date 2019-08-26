package com.localgenie.invoice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.home.MyBookingsFrag;
import com.localgenie.rateYourBooking.RateYourBooking;
import com.localgenie.utilities.AppPermissionsRunTime;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.CreateOrClearDirectory;
import com.localgenie.utilities.HandlePictureEvents;
import com.localgenie.utilities.ImageUploadedAmazon;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.localgenie.R;
import com.pojo.BidQuestionAnswer;
import com.pojo.BookingAccounting;
import com.pojo.ProviderDetailsBooking;
import com.pojo.QuestionImage;
import com.utility.AlertProgress;
import com.utility.DecimalDigitsInputFilter;
import com.utility.DialogInterfaceListner;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import adapters.QuestionAdapterGrid;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by murashid on 29-Sep-17.
 * <h1>InvoiceActivity</h1>
 * InvoiceActivity for showing the invoice detatils and take signature from customer
 */
public class InvoiceActivity extends DaggerAppCompatActivity implements InvoiceModel.InvoiceView
{

    private static final String TAG = "InvoiceActivity";
    //private UpdateStatusPresenter presenter;


    @BindView(R.id.toolBarInvoice)Toolbar toolBarInvoice;
    @BindView(R.id.tv_center)TextView tv_center;

    /*business Info*/
    @BindView(R.id.ivCustomer)ImageView ivCustomer;
    @BindView(R.id.tvCustomerName)TextView tvCustomerName;
    @BindView(R.id.tvCategory)TextView tvCategory;

    /*Pay*/
    @BindView(R.id.tvTotalBillAmount)TextView tvTotalBillAmount;
    @BindView(R.id.tvDate)TextView tvDate;
    @BindView(R.id.tvTime)TextView tvTime;

    /*Services*/
    @BindView(R.id.tvSelectedServiceLabel)TextView tvSelectedServiceLabel;
    @BindView(R.id.llService)LinearLayout llService;
    @BindView(R.id.llMainServicePay)LinearLayout llMainServicePay;

    @BindView(R.id.rlTravelFee)RelativeLayout rlTravelFee;
    @BindView(R.id.tvTravelFeeLabel)TextView tvTravelFeeLabel;
    @BindView(R.id.tvTravelFee)TextView tvTravelFee;

    @BindView(R.id.rlVisitFee)RelativeLayout rlVisitFee;
    @BindView(R.id.tvVisitFeeLabel)TextView tvVisitFeeLabel;
    @BindView(R.id.tvVisitFee)TextView tvVisitFee;

    @BindView(R.id.rlLastDue)RelativeLayout rlLastDue;
    @BindView(R.id.tvLastDueLabel)TextView tvLastDueLabel;
    @BindView(R.id.tvLastDue)TextView tvLastDue;


    @BindView(R.id.rlDiscountFee)RelativeLayout rlDiscountFee;
    @BindView(R.id.tvDiscountLabel)TextView tvDiscountLabel;
    @BindView(R.id.tvDiscount)TextView tvDiscount;

    @BindView(R.id.rlServiceFee)RelativeLayout rlServiceFee;
    @BindView(R.id.tvServiceLabel)TextView tvServiceLabel;
    @BindView(R.id.tvServiceFee)TextView tvServiceFee;

    /*Add Extra*/
    @BindView(R.id.llAddExtraItem)LinearLayout llAddExtraItem;
    @BindView(R.id.tvAddNewItem)TextView tvAddNewItem;
    @BindView(R.id.tvTotalLabel)TextView tvTotalLabel;
    @BindView(R.id.tvTotal)TextView tvTotal;


    @BindView(R.id.tvPaymentMethodLabel)TextView tvPaymentMethodLabel;
    @BindView(R.id.tvPaymentMethod)TextView tvPaymentMethod;

    @BindView(R.id.llQuestionAnswer)LinearLayout llQuestionAnswer;
    @BindView(R.id.tvSignatureHeader)TextView tvSignatureHeader;
    @BindView(R.id.signaturePad)SignaturePad signaturePad;

    @BindView(R.id.tvRetake)TextView tvRetake;
    @BindView(R.id.seekBar)SeekBar seekBar;
    @BindView(R.id.tvSeekbarText)TextView tvSeekbarText;

    @BindView(R.id.nestedScrollViewInvoice)NestedScrollView nestedScrollViewInvoice;
    @BindView(R.id.llSignature)LinearLayout llSignature;
    @BindView(R.id.llPayment)LinearLayout llPayment;
    @BindView(R.id.tvConfirm)TextView tvConfirm;
    @BindView(R.id.tvEditItem)TextView tvEditItem;
    @BindView(R.id.tvPromoCode)TextView tvPromoCode;


    @BindView(R.id.tvJobFeeLabel)TextView tvJobFeeLabel;
    @BindView(R.id.tvJobFee)TextView tvJobFee;
    @BindView(R.id.tvTimeSpent)TextView tvTimeSpent;


    private ProgressDialog progressDialog;
    @Inject
    SessionManagerImpl sessionManager;

    @Inject InvoiceModel.InvoicePre presenter;
    //   private SeekBar seekBar;

    private  File signatureFile;
    private Bitmap signatureBitmap;
    private HandlePictureEvents handlePicEvent;

    //  private Booking booking;

    private LayoutInflater inflater;
    private ArrayList<EditText> etAmounts, etExtraFees;
    private ArrayList<EditText> etAmountsTemp, etExtraFeesTemp;
    private double total=0,discountAmount =0;
    private double serviceFee = 0;
    @Inject AppTypeface appTypeface;
    @Inject
    AlertProgress alertProgress;
    AlertDialog alertDialog;
    private long bId;
    private String currencySymbol,promoCode="";
    private final int REQUEST_CODE_PERMISSION_STORE = 101;
    private File dir;
    ArrayList<AppPermissionsRunTime.MyPermissionConstants> myPermissionConstantsArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        ButterKnife.bind(this);
        appTypeface = AppTypeface.getInstance(this);
        getIntentValue();
        typeFace();
        alertDialogMethod(getString(R.string.wait));
        setToolBar();
        callInvoiceService();
    }

    private void setToolBar() {

        setSupportActionBar(toolBarInvoice);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
       // toolBarInvoice.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolBarInvoice.setNavigationOnClickListener(view -> onBackPressed());
        tv_center.setText(getString(R.string.businessSignature));
    }

    private void getIntentValue() {

        if (getIntent().getExtras() != null) {

            bId = getIntent().getLongExtra("BID", 0);
            // isImageDrawn = true;
        }
    }


    @Override
    public void alertDialogMethod(String msg) {
        alertDialog = alertProgress.getProgressDialog(this,msg);
    }

    private void callInvoiceService()
    {
        onShowProgress();
        presenter.getBookingDetailsById(bId);
    }
    private void typeFace() {
        handlePicEvent = new HandlePictureEvents(this);
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        tvCustomerName.setTypeface(appTypeface.getHind_semiBold());
        tvCategory.setTypeface(appTypeface.getHind_regular());
        tvTotalBillAmount.setTypeface(appTypeface.getHind_semiBold());
        tvDate.setTypeface(appTypeface.getHind_regular());
        tvTime.setTypeface(appTypeface.getHind_regular());
        tvSelectedServiceLabel.setTypeface(appTypeface.getHind_medium());
        tvTravelFeeLabel.setTypeface(appTypeface.getHind_regular());
        tvTravelFee.setTypeface(appTypeface.getHind_regular());
        tvVisitFeeLabel.setTypeface(appTypeface.getHind_regular());
        tvVisitFee.setTypeface(appTypeface.getHind_regular());
        tvLastDueLabel.setTypeface(appTypeface.getHind_regular());
        tvLastDue.setTypeface(appTypeface.getHind_regular());
        tvDiscountLabel.setTypeface(appTypeface.getHind_regular());
        tvDiscount.setTypeface(appTypeface.getHind_regular());
        tvServiceLabel.setTypeface(appTypeface.getHind_regular());
        tvServiceFee.setTypeface(appTypeface.getHind_regular());
        tvAddNewItem.setTypeface(appTypeface.getHind_medium());
        tvEditItem.setTypeface(appTypeface.getHind_medium());
        tvTotalLabel.setTypeface(appTypeface.getHind_semiBold());
        tvTotal.setTypeface(appTypeface.getHind_semiBold());
        tvPaymentMethodLabel.setTypeface(appTypeface.getHind_medium());
        tvPaymentMethod.setTypeface(appTypeface.getHind_regular());
        tvSignatureHeader.setTypeface(appTypeface.getHind_medium());
        tvRetake.setTypeface(appTypeface.getHind_regular());
        tvSeekbarText.setTypeface(appTypeface.getHind_semiBold());
        tvConfirm.setTypeface(appTypeface.getHind_semiBold());
        tvJobFeeLabel.setTypeface(appTypeface.getHind_semiBold());
        tvJobFee.setTypeface(appTypeface.getHind_semiBold());
        tvPromoCode.setTypeface(appTypeface.getHind_regular());
        tvTimeSpent.setTypeface(appTypeface.getHind_regular());

        inflater = LayoutInflater.from(this);
        etAmounts = new ArrayList<>();
        etExtraFees = new ArrayList<>();
        etAmountsTemp = new ArrayList<>();
        etExtraFeesTemp = new ArrayList<>();
        checkForPerMission();

        onSeekBarScroll();

    }



    private void checkForPerMission() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            myPermissionConstantsArrayList.clear();
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_WRITE_EXTERNAL_STORAGE);
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_READ_EXTERNAL_STORAGE);
            if (AppPermissionsRunTime.checkPermission(this, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_STORE)) {
                pictureEvent();
            }
        } else {
            pictureEvent();
        }
    }

    private void pictureEvent() {

        CreateOrClearDirectory directory = CreateOrClearDirectory.getInstance();

        dir = directory.getAlbumStorageDir(this, Constants.SIGNATURE_PIC_DIR, true);
        signatureFile = new File(dir, bId+".jpg");



        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                signatureBitmap = signaturePad.getSignatureBitmap();
            }

            @Override
            public void onClear()
            {
                Toast.makeText(InvoiceActivity.this, getString(R.string.cleared), Toast.LENGTH_SHORT).show();
            }
        });
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
            case REQUEST_CODE_PERMISSION_STORE:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isDenine = true;
                    }
                }
                if (isDenine) {
                    Toast.makeText(this, "Permission denied by the user", Toast.LENGTH_SHORT).show();
                    //checkForPerMission();
                } else {
                    pictureEvent();
                }
                break;
        }
    }

            @OnClick({R.id.tvAddNewItem,R.id.tvConfirm,R.id.tvRetake,R.id.tvEditItem,R.id.tvPromoCode})
    public void onButtonClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tvPromoCode:
                presenter.showPromoCodeDialog(this,bId);
                break;
            case R.id.tvEditItem:
                showEditItem();
                tvEditItem.setVisibility(View.GONE);
                tvAddNewItem.setVisibility(View.VISIBLE);
                addEditView();
                break;
            case R.id.tvAddNewItem:
                showEditItem();
                addRemoveDynamicView();
                break;
            case R.id.tvConfirm:
                if(isValidExtraFees())
                {
                    tvConfirm.setVisibility(View.GONE);
                    llSignature.setVisibility(View.VISIBLE);
                    llPayment.setVisibility(View.VISIBLE);
                    tvAddNewItem.setVisibility(View.GONE);
                    tvEditItem.setVisibility(View.GONE);
                    if(llQuestionAnswer !=null)
                    {
                        llQuestionAnswer.setVisibility(View.VISIBLE);
                    }

                    addStaticView();
                }
                break;
            case R.id.tvRetake:
                signaturePad.clear();
                break;
        }
    }



    private void showEditItem() {
        llSignature.setVisibility(View.GONE);
        llPayment.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.VISIBLE);
        if(llQuestionAnswer !=null)
        {
            llQuestionAnswer.setVisibility(View.GONE);
        }
    }


    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message) {

    }

    @Override
    public void onError(String error) {

        alertProgress.alertinfo(this,error);
    }

    @Override
    public void onShowProgress() {
        if(alertDialog!=null)
            alertDialog.show();
    }

    @Override
    public void onHideProgress() {
        if(alertDialog!=null)
            alertDialog.hide();
    }

    @Override
    public void networkUnReachable(String message, boolean isGetDetails)
    {
        alertProgress.tryAgain(this, getString(R.string.pleaseCheckInternet), getString(R.string.alert), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                if(isGetDetails)
                    callInvoiceService();
                else
                {

                }
            }
        });

    }

    @Override
    public void onProviderDetails(String currencySymbol, ProviderDetailsBooking providerDetail,
                                  String categoryName, long bookingRequestedFor, ArrayList<BidQuestionAnswer> questionAndAnswer) {
        this.currencySymbol = currencySymbol;

        presenter.timeMethod(tvDate,tvTime,bookingRequestedFor);
        String name = providerDetail.getFirstName()+" "+providerDetail.getLastName();
        tvCustomerName.setText(name);
        tvCategory.setText(categoryName);
        if(!providerDetail.getProfilePic().equals(""))
        {
            Glide.with(this).
                    load(providerDetail.getProfilePic())
                    .apply(Utility.createGlideOptionCall(this))
                    .into(ivCustomer);
        }else
        {
            View view = Utility.getCustomeBackgroundTextProfilePic(inflater, providerDetail.getFirstName() , providerDetail.getLastName());
            ivCustomer.setBackground(new BitmapDrawable(getResources(), Utility.createDrawableFromView(this, view)));
        }

        bidding(questionAndAnswer);
    }

    private void bidding(ArrayList<BidQuestionAnswer> questionAndAnswer) {

       /* if(bookingModel == 3)
        {

        }*/
        llQuestionAnswer.removeAllViews();
        llQuestionAnswer.setVisibility(View.VISIBLE);

        TextView tvJobQuestion,tvJobQuestions,tvJobAnswer,tvJobAnswers;
        LinearLayout llJobDetailsQA;
        RecyclerView recyclerViewQuestionImage;
        RelativeLayout rlQuestion;
        View viewJobQA;
        int colour = 0;
        for(int position = 0;position<questionAndAnswer.size();position++)
        {
            View itemView = LayoutInflater.from(InvoiceActivity.this).inflate(R.layout.job_question_answer,nestedScrollViewInvoice,false);
            tvJobQuestions = itemView.findViewById(R.id.tvJobQuestions);
            tvJobAnswers = itemView.findViewById(R.id.tvJobAnswers);
            viewJobQA = itemView.findViewById(R.id.viewJobQA);
            tvJobQuestion = itemView.findViewById(R.id.tvJobQuestion);
            tvJobAnswer = itemView.findViewById(R.id.tvJobAnswer);
            llJobDetailsQA = itemView.findViewById(R.id.llJobDetailsQA);
            recyclerViewQuestionImage = itemView.findViewById(R.id.recyclerViewQuestionImage);
            rlQuestion = itemView.findViewById(R.id.rlQuestion);
            //  if(position%2==0)
            colour = Utility.getColor(this,R.color.search_background);
              /*  else
                    colour = Utility.getColor(this,R.color.white);*/

            rlQuestion.setBackgroundColor(colour);
            // llJobDetailsQA.setBackgroundColor(colour);
            llQuestionAnswer.addView(itemView);
            if(position == questionAndAnswer.size()-1)
                viewJobQA.setVisibility(View.GONE);


            tvJobQuestions.setTypeface(appTypeface.getHind_semiBold());
            tvJobAnswers.setTypeface(appTypeface.getHind_regular());
            tvJobQuestion.setTypeface(appTypeface.getHind_bold());
            tvJobAnswer.setTypeface(appTypeface.getHind_bold());
            tvJobQuestions.setText(questionAndAnswer.get(position).getQuestion());
            switch (questionAndAnswer.get(position).getQuestionType())
            {
                case 10:
                    recyclerViewQuestionImage.setVisibility(View.VISIBLE);
                    tvJobAnswers.setVisibility(View.GONE);
                    GridLayoutManager gridLayoutManager =  new GridLayoutManager(this,3);

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
                case 1:
                    recyclerViewQuestionImage.setVisibility(View.GONE);
                    tvJobAnswers.setVisibility(View.VISIBLE);
                    tvJobAnswers.setText(questionAndAnswer.get(position).getAnswer());


                    break;
                default:
                    recyclerViewQuestionImage.setVisibility(View.GONE);
                    tvJobAnswers.setVisibility(View.VISIBLE);
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
                    break;
            }


        }


    }

    @Override
    public void onAccounting(BookingAccounting accounting) {


        inflateJobPay(accounting.getBidPrice());
        totalTOPay(accounting.getBidPrice(),accounting.getTotal());

        if(accounting.getVisitFee()>0)
        {
            rlVisitFee.setVisibility(View.VISIBLE);
            Utility.setAmtOnRecept(accounting.getVisitFee(),tvVisitFee,currencySymbol);
        }
        if(accounting.getTravelFee()>0)
        {
            rlTravelFee.setVisibility(View.VISIBLE);
            Utility.setAmtOnRecept(accounting.getTravelFee(),tvTravelFee,currencySymbol);

        }

        if(accounting.getDiscount()>0)
        {
            rlDiscountFee.setVisibility(View.VISIBLE);
            Utility.setAmtOnRecept(accounting.getDiscount(),tvDiscount,currencySymbol);

        }

        if(accounting.getPaymentMethod()==1)
            tvPaymentMethod.setText(accounting.getPaymentMethodText());
        else if(accounting.getPaymentMethod() == 2)
        {
            String card = accounting.getPaymentMethodText()+" "+getString(R.string.cardFirst16Digit)+""+
                    accounting.getLast4();
            tvPaymentMethod.setText(card);
        }
        if(accounting.getPaidByWallet() !=0)
            tvPaymentMethod.setText(tvPaymentMethod.getText().toString() + " + "+ getString(R.string.wallet));

        serviceFee = accounting.getAmount();// shijen dev changed service fee to amount
        Utility.setAmtOnRecept(serviceFee,tvServiceFee,currencySymbol);

        String timeSpent = "Time Spent "+formatSeconds(accounting.getTotalJobTime());
        tvTimeSpent.setText(timeSpent);
    }

    private static String formatSeconds(long timeInSeconds)
    {
        int hours = (int)timeInSeconds / 3600;
        int secondsLeft = (int)timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
       /* if (hours < 10)
            formattedTime += "0";*/
        formattedTime += hours + "hr ";

       /* if (minutes < 10)
            formattedTime += "0";*/
        formattedTime += minutes + "mn ";

       /* if (seconds < 10)
            formattedTime += "0";*/
        formattedTime += seconds +"sec" ;

        return formattedTime;
    }

    private void totalTOPay( double bidPrice, double totalPr) {
        if(discountAmount>0)
            rlDiscountFee.setVisibility(View.VISIBLE);
        Utility.setAmtOnRecept(discountAmount,tvDiscount,currencySymbol);
        total = totalPr;
        Utility.setAmtOnRecept((total-discountAmount),tvTotal,currencySymbol);
        // Utility.setAmtOnRecept(total,tvTotalBillAmount,currencySymbol);
        tvTotalBillAmount.setText(tvTotal.getText().toString());

    }

    private void inflateJobPay(double bidPrice) {

        if(bidPrice > 0) {
            llService.setVisibility(View.VISIBLE);
            llService.removeAllViews();
            TextView tvServiceToPay, tvServiceToPayAmt;
            View v = LayoutInflater.from(this).inflate(R.layout.services_to_pay, llMainServicePay, false);
            llService.addView(v);
            tvServiceToPay = v.findViewById(R.id.tvServiceToPay);
            tvServiceToPayAmt = v.findViewById(R.id.tvServiceToPayAmt);
            tvServiceToPayAmt.setTypeface(appTypeface.getHind_regular());
            tvServiceToPay.setTypeface(appTypeface.getHind_regular());
            tvServiceToPay.setText(getString(R.string.serviceToPay));
            Utility.setAmtOnRecept(bidPrice, tvServiceToPayAmt, currencySymbol);
        }

    }


    private void addRemoveDynamicView() {
        final View view =inflater.inflate(R.layout.add_new_item_invoice_view,null);
        llAddExtraItem.addView(view);
        ImageView ivDelete = view.findViewById(R.id.ivDelete);
        final EditText etAmount = view.findViewById(R.id.etAmount);
        final EditText etExtraFee = view.findViewById(R.id.etExtraFee);
        final TextView tvCurrencySymbol = view.findViewById(R.id.tvCurrencySymbol);
        final TextView tvCurrencySymbolSuffix = view.findViewById(R.id.tvCurrencySymbolSuffix);
        etAmount.setTypeface(appTypeface.getHind_regular());
        etAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(15,2)});
        etExtraFee.setTypeface(appTypeface.getHind_regular());
        tvCurrencySymbol.setTypeface(appTypeface.getHind_regular());
        tvCurrencySymbolSuffix.setTypeface(appTypeface.getHind_regular());
        tvCurrencySymbol.setText(currencySymbol);
        tvCurrencySymbolSuffix.setText(currencySymbol);

        etAmounts.add(etAmount);
        etExtraFees.add(etExtraFee);

       /* if(sessionManager.getCurrencyAbbrevation().equals("2"))
        {
            tvCurrencySymbolSuffix.setVisibility(View.VISIBLE);
            tvCurrencySymbol.setVisibility(View.GONE);
        }*/

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llAddExtraItem.removeView(view);
                etAmounts.remove(etAmount);
                etExtraFees.remove(etExtraFee);

                if(etAmounts.size() == 0)
                {
                    tvConfirm.setVisibility(View.GONE);
                    llSignature.setVisibility(View.VISIBLE);
                    llPayment.setVisibility(View.VISIBLE);
                    if(llQuestionAnswer !=null)
                    {
                        llQuestionAnswer.setVisibility(View.VISIBLE);
                    }
                }

                calculateTotal();
            }
        });

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                {
                    double amount;

                    etAmount.setText(etAmount.getText().toString());

                }
                else
                {
                    etAmount.setCursorVisible(true);
                }
            }
        });

        etAmount.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    etAmount.setCursorVisible(false);
                    return true;
                }
                return false;
            }
        });

    }

    private void calculateTotal() {
        double serviceTotal = 0;
        for (EditText etAmount : etAmounts)
        {
            if(!etAmount.getText().toString().equals(""))
            {
                try {
                    serviceTotal += Double.parseDouble(etAmount.getText().toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        Utility.setAmtOnRecept((total+serviceTotal-discountAmount),tvTotal,currencySymbol);
        tvTotalBillAmount.setText(tvTotal.getText().toString());
    }

    private boolean isValidExtraFees()
    {
        for (int i=0 ; i < etAmounts.size() ; i++)
        {
            if(etExtraFees.get(i).getText().toString().equals(""))
            {
                // Toast.makeText(this,getString(R.string.plsEnterServiceName),Toast.LENGTH_SHORT).show();
                alertProgress.alertinfo(this,getString(R.string.plsEnterServiceName));
                return false;
            }
            else if(etAmounts.get(i).getText().toString().equals(""))
            {
                //   Toast.makeText(this,getString(R.string.plsEnterPrice),Toast.LENGTH_SHORT).show();
                alertProgress.alertinfo(this,getString(R.string.plsEnterPrice));

                return false;
            }
        }

        return true;
    }

    private void addEditView()
    {
        llAddExtraItem.removeAllViews();

        etAmountsTemp.clear();
        etExtraFeesTemp.clear();

        for (int i=0 ; i < etAmounts.size() ; i++)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.add_new_item_invoice_view,llMainServicePay,false);
            llAddExtraItem.addView(view);
            ImageView ivDelete = view.findViewById(R.id.ivDelete);
            final EditText etAmount = view.findViewById(R.id.etAmount);
            final EditText etExtraFee = view.findViewById(R.id.etExtraFee);
            final TextView tvCurrencySymbol = view.findViewById(R.id.tvCurrencySymbol);
            final TextView tvCurrencySymbolSuffix = view.findViewById(R.id.tvCurrencySymbolSuffix);
            etAmount.setTypeface(appTypeface.getHind_regular());
            etAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(15,2)});
            etExtraFee.setTypeface(appTypeface.getHind_regular());
            tvCurrencySymbol.setTypeface(appTypeface.getHind_regular());
            tvCurrencySymbolSuffix.setTypeface(appTypeface.getHind_regular());
            tvCurrencySymbol.setText(currencySymbol);
            etExtraFee.setText(etExtraFees.get(i).getText().toString());
            if(!"".equals(etAmounts.get(i).getText().toString()))
            {
                double amount = Double.parseDouble(etAmounts.get(i).getText().toString());
                Utility.doubleformate(amount);
                etAmount.setText(Utility.doubleformate(amount));
            }
            etAmountsTemp.add(etAmount);
            etExtraFeesTemp.add(etExtraFee);


            //   Utility.setAmtOnRecept();
            //  tvServiceFee.setText(Utility.getPrice(etAmounts.get(i).getText().toString(),sessionManager.getCurrencySymbol(), sessionManager.getCurrencyAbbrevation()));

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llAddExtraItem.removeView(view);
                    etAmounts.remove(etAmount);
                    etExtraFees.remove(etExtraFee);

                    if(etAmounts.size() == 0)
                    {
                        tvConfirm.setVisibility(View.GONE);
                        llSignature.setVisibility(View.VISIBLE);
                        llPayment.setVisibility(View.VISIBLE);
                        if(llQuestionAnswer !=null)
                        {
                            llQuestionAnswer.setVisibility(View.VISIBLE);
                        }
                    }

                    calculateTotal();
                }
            });

            etAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    calculateTotal();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b)
                    {
                        etAmount.setText(etAmount.getText().toString());
                    }
                    else
                    {
                        etAmount.setCursorVisible(true);
                    }
                }
            });

            etAmount.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        etAmount.setCursorVisible(false);
                        return true;
                    }
                    return false;
                }
            });

        }

        etAmounts.clear();
        etExtraFees.clear();
        etAmounts.addAll(etAmountsTemp);
        etExtraFees.addAll(etExtraFeesTemp);
    }
    private void addStaticView() {
        llAddExtraItem.removeAllViews();
        for (int i=0 ; i < etAmounts.size() ; i++)
        {
            View serviceView = LayoutInflater.from(this).inflate(R.layout.services_to_pay,llMainServicePay,false);
            TextView tvServiceFeeLabel = serviceView.findViewById(R.id.tvServiceToPay);
            llAddExtraItem.addView(serviceView);
            TextView tvServiceFee = serviceView.findViewById(R.id.tvServiceToPayAmt);
            tvServiceFeeLabel.setTypeface(appTypeface.getHind_regular());
            tvServiceFee.setTypeface(appTypeface.getHind_regular());
            tvServiceFeeLabel.setText(etExtraFees.get(i).getText().toString());
            if(!"".equals(etAmounts.get(i).getText().toString()))
            {
                double amount = Double.parseDouble(etAmounts.get(i).getText().toString());
                Utility.setAmtOnRecept(amount,tvServiceFee,currencySymbol);
            }
            //   Utility.setAmtOnRecept();
            //  tvServiceFee.setText(Utility.getPrice(etAmounts.get(i).getText().toString(),sessionManager.getCurrencySymbol(), sessionManager.getCurrencyAbbrevation()));

        }

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i=0 ; i < etAmounts.size() ; i++)
            {
                JSONObject jsonObject1 = new JSONObject();

                jsonObject1.put("serviceName",etExtraFees.get(i).getText().toString());
                jsonObject1.put("price",etAmounts.get(i).getText().toString());
                jsonArray.put(jsonObject1);
            }
            jsonObject.put("additionalService",jsonArray);
            Log.d(TAG, "addStaticView: "+jsonObject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void onSeekBarScroll() {

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (seekBar.getProgress() > 75)
                {
                    if (AppPermissionsRunTime.checkPermission(InvoiceActivity.this, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_STORE)) {

                        if (!signaturePad.isEmpty()) {
                            seekBar.setProgress(100);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                alertDialogMethod(getString(R.string.raisingInvoice));
                                String BUCKETSUBFOLDER = Constants.SIGNATURE_UPLOAD;

                                final String signatureUrl = Constants.AMAZON_BASE_URL + Constants.Amazonbucket + "/" + BUCKETSUBFOLDER + "/" + signatureFile.getName();
                                Utility.saveImage(signatureBitmap, signatureFile);

                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri contentUri = Uri.fromFile(signatureFile);
                                mediaScanIntent.setData(contentUri);
                                sendBroadcast(mediaScanIntent);
                                onShowProgress();
                                handlePicEvent.uploadToAmazon(Constants.Amazonbucket + "/" + Constants.SIGNATURE_UPLOAD, signatureFile, new ImageUploadedAmazon() {
                                    @Override
                                    public void onSuccessAdded(String image) {
                                        //signatureUrl[0] = image;
                                        dir.delete();
                                        signatureFile.deleteOnExit();
                                        //  if(dir!=null)

                                    }

                                    @Override
                                    public void onerror(String errormsg) {
                                    }

                                });

                          /*  jsonObject.put("bookingId",bId);
                            jsonObject.put("signatureUrl",signatureUrl);*/


                                JSONArray jsonArray = new JSONArray();
                                for (int i = 0; i < etAmounts.size(); i++) {
                                    JSONObject jsonObjectExtra = new JSONObject();
                                    //  jsonObjectExtra.put("serviceName",etExtraFees.get(i).getText().toString());
                                    //  jsonObjectExtra.put("price",etAmounts.get(i).getText().toString());
                                    jsonArray.put(jsonObjectExtra);
                                }

                                //   jsonObject.put("additionalService",jsonArray);


                                Log.d(TAG, "onStopTrackingTouch: " + jsonObject);

                                // UploadFileAmazonS3 amazonS3 = UploadFileAmazonS3.getInstance(InvoiceActivity.this);

                                // presenter.updateStatus(jsonArray, signatureUrl,handlePicEvent,signatureFile,bId);
                                presenter.updateStatus(jsonArray, signatureUrl, handlePicEvent, signatureFile, bId, promoCode);
                            } catch (Exception e) {
                                e.printStackTrace();
                                seekBar.setProgress(0);
                            }
                        }
                    }
                    else
                    {
                        seekBar.setProgress(0);
                        Toast.makeText(InvoiceActivity.this,getString(R.string.plsProvideSignature),Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    seekBar.setProgress(0);
                }

            }
        });

    }

    @Override
    public void viewRatingScreen() {
        Constants.isHomeFragment = false;
        Intent intent = new Intent(this, RateYourBooking.class);
        // Intent intent = new Intent(mContext, InvoiceActivity.class);
        intent.putExtra("BID", bId);
        if(dir!=null)
            dir.deleteOnExit();
        if(signatureFile!=null)
            signatureFile.delete();
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
       // MyBookingsFrag.onMyGetBooking();
    }

    @Override
    public void callOnResultActivity(double amount, String trim) {

        if(amount<=serviceFee)
        {
            tvDiscountLabel.setText(trim);
            Utility.setAmtOnRecept(amount,tvDiscount,currencySymbol);
            discountAmount = amount;
            String discount = "- "+tvDiscount.getText().toString();
            tvDiscount.setText(discount);
            totalTOPay(0,total);
            promoCode = trim;
        }else {
            alertProgress.alertinfo(this,getString(R.string.discountFeeLess));
        }
    }
}
