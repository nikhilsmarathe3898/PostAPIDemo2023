package com.localgenie.biddingFlow;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.localgenie.Login.LoginActivity;
import com.localgenie.R;
import com.localgenie.addTocart.AddToCart;
import com.localgenie.add_address.AddAddressActivity;
import com.localgenie.add_address.SearchAddressLocation;
import com.localgenie.bookingtype.DialogTimeFragment;
import com.localgenie.confirmbookactivity.ConfirmBookActivity;
import com.localgenie.home.MainActivity;
import com.localgenie.home.ServicesFrag;
import com.localgenie.model.CallType;
import com.localgenie.model.Category;
import com.localgenie.model.Offers;
import com.localgenie.model.PreDefinedQuestions;
import com.localgenie.model.QuestionList;
import com.localgenie.model.SubCategory;
import com.localgenie.model.youraddress.YourAddrData;
import com.localgenie.providerList.ProviderList;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.HandlePictureEvents;
import com.localgenie.utilities.ImageUploadedAmazon;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.youraddress.YourAddressPresenter;
import com.pojo.QuestionImage;
import com.utility.AlertProgress;
import com.utility.PermissionsListener;
import com.utility.PermissionsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import adapters.AddressListAdapter;
import adapters.QuestionAdapterGrid;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import eu.janmuller.android.simplecropimage.CropImage;

public class BiddingQuestions extends DaggerAppCompatActivity implements MyBiddingQuestionFrag.OnFragmentInteractionListener,
        BiddingContractor.BiddingContractView, MyBiddingBookTypeFrag.OnFragmentInteractionListener,PermissionsListener,DialogTimeFragment.OnFragmentInteractionListener {

    private final String TAG = BiddingQuestions.class.getSimpleName();
    private final int SEARCH_RESULT = 101;
    @BindView(R.id.toolBarBidding)Toolbar toolBarBidding;
    @BindView(R.id.tv_center)TextView tv_center;
    @BindView(R.id.progressQuestion)ProgressBar progressQuestion;
    @BindView(R.id.tvBiddingQuestionInfo)TextView tvBiddingQuestionInfo;
    @BindView(R.id.frameContainer)LinearLayout frameContainer;

    @BindView(R.id.tvBookTypeSave)TextView tvBookTypeSave;

    @BindView(R.id.tvQuestionDesc)TextView tvQuestionDesc;
    @BindView(R.id.recyclerViewQuestions)RecyclerView recyclerViewQuestions;
    //  @BindView(R.id.viewCalendar)CalendarView viewCalendar;
    @BindView(R.id.timePicker)TimePicker timePicker;

    @BindView(R.id.linAddress)NestedScrollView linAddress;
    @BindView(R.id.recyclerViewAddress)RecyclerView recyclerViewAddress;
    // @BindView(R.id.addressListView)ListView addressListView;
    @BindView(R.id.etAddress)EditText etAddress;
    @BindView(R.id.tvChangeAdd)ImageView tvChangeAdd;
    @BindView(R.id.ivSearchAddress)ImageView ivSearchAddress;
    @BindView(R.id.tvAddNewAddress)TextView tvAddNewAddress;

    @BindView(R.id.rlBidAmount)RelativeLayout rlBidAmount;
    @BindView(R.id.tvBidCurrencySym)TextView tvBidCurrencySym;
    @BindView(R.id.etBiddingAmount)EditText etBiddingAmount;

    @BindView(R.id.llBidCardPayment)LinearLayout llBidCardPayment;
    // @BindView(R.id.ivCardImageInfo)ImageView ivCardImageInfo;
    //  @BindView(R.id.tvCardInfo)TextView tvCardInfo;


    @BindView(R.id.rlMAinBidding)RelativeLayout rlMAinBidding;
    @BindView(R.id.rlMAinBooking)RelativeLayout rlMAinBooking;
    @BindView(R.id.progress_bar)ProgressBar progress_bar;
    @BindView(R.id.ivTickCheck)ImageView ivTickCheck;
    @BindView(R.id.tvYahBookingDone)TextView tvYahBookingDone;
    @BindView(R.id.tvWillLetUKnw)TextView tvWillLetUKnw;


    @BindView(R.id.progressBarBid)ProgressBar progressBarBid;
    @BindView(R.id.rlPromoCode)RelativeLayout rlPromoCode;
    @BindView(R.id.etPromoCode)EditText etPromoCode;
    @BindView(R.id.tvApply)TextView tvApply;


    @BindView(R.id.tvOrAddress)TextView tvOrAddress;
    @BindView(R.id.viewOrAddress)View viewOrAddress;

    @BindView(R.id.llBiddingQuestion)LinearLayout llBiddingQuestion;

    @BindView(R.id.tvBidSkip)TextView tvBidSkip;

    @BindView(R.id.tvBookBidJobs)TextView tvBookBidJobs;


    @BindView(R.id.tilTextYourViews)TextInputLayout tilTextYourViews;
    @BindView(R.id.etTextYourViews)EditText etTextYourViews;

    @BindView(R.id.llConfirmTotalDues)LinearLayout llConfirmTotalDues;
    @BindView(R.id.tvConfirmDuesPayInfo)TextView tvConfirmDuesPayInfo;
    @BindView(R.id.tvConfirmDuesAddress)TextView tvConfirmDuesAddress;
    @BindView(R.id.tvConfirmDuesDate)TextView tvConfirmDuesDate;
    @BindView(R.id.tvCancelDues)TextView tvCancelDues;
    @BindView(R.id.tvConfirmDues)TextView tvConfirmDues;
    //  @BindView(R.id.viewPromoCode)View viewPromoCode;

    private BottomSheetBehavior sheetBehavior;
    private boolean isDueFound = false;

    @Inject
    AppTypeface appTypeface;
    @Inject BiddingContractor.BiddingContractPresent presenter;
    @Inject
    MyBiddingQuestionFrag myBiddingQuestionFrag;

    @Inject
    MyBiddingBookTypeFrag myBiddingBookingTypeFrag;

    @Inject
    AlertProgress alertProgress;
    @Inject
    HashMap<String,AnswerHashMap> answerHashMap;
    @Inject
    SessionManagerImpl manager;
    @Inject
    YourAddressPresenter yourAddressPresenter;
    @Inject
    PermissionsManager permissionsManager;

    ArrayList<YourAddrData> AddressList = new ArrayList<>();
    ArrayList<QuestionImage> questionImages;
    //  private ArrayList<String>imageArray = new ArrayList<>();
    private HashMap<Integer,String>imageArray = new HashMap<>();
    private int imagePositionTaken = 0;
    QuestionAdapterGrid questionAdapterGrid;
    AddressListAdapter addressListAdapter;
    private HandlePictureEvents handlePicEvent;

    private ArrayList<QuestionList>questionLists = new ArrayList<>();
    private Category.BookingTypeAction bookingTypeAction;
    private String catId;
    private ArrayList<SubCategory>subCategoryArrayList;
    private ArrayList<Offers>offers;
    private double minAmount,maxAmount;

    private int startQuestion = -1;

    private int fixedPosition = -1;
    private LatLng latLng = null;
    private double bidLatitude = 0;
    private double bidLongitude = 0;
    private long visitingTime = 0;
    private String bidAddress = "";
    private String bidPromoCode = "";
    private boolean isWallet;
    private int paymentType;//,imagePosition;
    private String cardId,selectedAnswer,visitSelected;
    private boolean isRepeatCalled = false;
    //private boolean isWalletSelected = false;
    private boolean flag_guest_login = false;
    private boolean isBidding = false;
    private boolean isInCallSelected = false;
    private Animation hide, visible;
    private String totalBiddingAmt = "";
    private CallType callType;
    //Mandatory question type
    private final int QT1_AMOUNT=1;
    private final int QT2_BOOKING_WHEN=2;//NOW LATER REPEAT
    private final int QT11_TYPE_OF_SERVICE=11;//INCALL OUTCALL TELECALL


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidding_questions);
        ButterKnife.bind(this);
        hide = AnimationUtils.loadAnimation(this, R.anim.scaledown_progress_animation);
        visible = AnimationUtils.loadAnimation(this, R.anim.scaleup_progress_animation);
        getIntentValues();
        progressBar();
        typeFace();
        toolBar();
        paymentValue();
        AddressSaved();
        questionImage();

    }

    private void questionImage() {
        questionImages = new ArrayList<>();
        questionImages.add(new QuestionImage("",false,imagePositionTaken));

        etBiddingAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                totalBiddingAmt = etBiddingAmount.getText().toString().trim();
            }
        });
    }

    private void AddressSaved()
    {
        String addAddress = "+ "+getString(R.string.add_new_address);
        tvAddNewAddress.setText(addAddress);
        bidAddress = manager.getAddress();
        etAddress.setFocusable(false);
        bidLatitude = Constants.latitude;
        bidLongitude = Constants.longitude;
        //  yourAddressPresenter.getAddress(manager.getAUTH(),this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        addressListAdapter = new AddressListAdapter(this,AddressList,yourAddressPresenter,manager.getAUTH(),true);
        recyclerViewAddress.setLayoutManager(linearLayoutManager);
        recyclerViewAddress.setAdapter(addressListAdapter);
        recyclerViewAddress.setNestedScrollingEnabled(false);
    }

    @Override
    public void onAddressSelected(int position) {

        bidLatitude = AddressList.get(position).getLatitude();
        bidLongitude = AddressList.get(position).getLongitude();
        bidAddress = AddressList.get(position).getAddLine1();
        tvChangeAdd.setSelected(true);
    }

    /**
     * <h2>refereshtems</h2>
     * <p>This method is used to refresh the address items from the adapter</p>
     * @param rowItem Address Item for deleting from the adapter
     * @param adapterPosition position of the deleted item in the adapter
     */
    @Override
    public void refreshItems(YourAddrData rowItem, int adapterPosition) {
        if (addressListAdapter!=null) {
            AddressList.remove(adapterPosition);
            addressListAdapter.notifyItemRemoved(adapterPosition);
            addressListAdapter.notifyItemRangeChanged(adapterPosition, AddressList.size());
            if(AddressList.size()>0)
            {
                tvOrAddress.setVisibility(View.VISIBLE);
                viewOrAddress.setVisibility(View.VISIBLE);
            }else
            {
                tvOrAddress.setVisibility(View.GONE);
                viewOrAddress.setVisibility(View.GONE);
            }
            if(bidAddress.equals(rowItem.getAddLine2()))
            {
                myCurrentLocation();
            }
            if (addressListAdapter.getItemCount()==0) {
                setNoAddressAvailable();
            }
        }
    }

    private void myCurrentLocation() {
        bidLatitude = Constants.latitude;
        bidLongitude = Constants.longitude;
        bidAddress = manager.getAddress();
        tvChangeAdd.setSelected(false);

    }

    private void paymentValue() {
        if(ServicesFrag.paymentMode!=null && ServicesFrag.paymentMode.isCard())
        {
            if(!"".equals(manager.getDefaultCardNum()))
            {
                cardId = manager.getDefaultCardId();
                setCardInfo(manager.getDefaultCardName(),manager.getDefaultCardNum());
                // tvConfirmBook.setSelected(true);
            }else
                paymentType = 0;
        }else
            paymentType = 0;
    }

    private void progressBar() {
        progressQuestion.setMax(questionLists.size());
        frameContainer.setVisibility(View.GONE);
        //  viewCalendar.setVisibility(View.GONE);
        recyclerViewQuestions.setVisibility(View.GONE);

    }

    private void toolBar()
    {
        sheetBehavior = BottomSheetBehavior.from(llConfirmTotalDues);
        setSupportActionBar(toolBarBidding);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv_center.setText(Constants.catName);//getString(R.string.bookingType)
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        toolBarBidding.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolBarBidding.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void typeFace()
    {
        handlePicEvent = new HandlePictureEvents(this);
        tvBiddingQuestionInfo.setTypeface(appTypeface.getHind_medium());
        String BiddingInfo = getString(R.string.biddingInfoQuestion)+" "+Constants.catName;
        tvBiddingQuestionInfo.setText(BiddingInfo);
        tvBookTypeSave.setTypeface(appTypeface.getHind_semiBold());
        tvBookBidJobs.setTypeface(appTypeface.getHind_semiBold());
        tvQuestionDesc.setTypeface(appTypeface.getHind_semiBold());
        //   tvCardInfo.setTypeface(appTypeface.getHind_regular());
        etAddress.setTypeface(appTypeface.getHind_regular());
        tvYahBookingDone.setTypeface(appTypeface.getHind_semiBold());
        tvAddNewAddress.setTypeface(appTypeface.getHind_semiBold());
        tvWillLetUKnw.setTypeface(appTypeface.getHind_regular());
        tvApply.setTypeface(appTypeface.getHind_semiBold());
        etPromoCode.setTypeface(appTypeface.getHind_medium());
        tvBidSkip.setTypeface(appTypeface.getHind_semiBold());
        tvBidCurrencySym.setTypeface(appTypeface.getHind_regular());
        etTextYourViews.setTypeface(appTypeface.getHind_regular());
        tvBiddingQuestionInfo.setVisibility(View.VISIBLE);
        progressQuestion.setVisibility(View.INVISIBLE);
        tvBidCurrencySym.setText(Constants.currencySymbol);


        tvConfirmDues.setTypeface(appTypeface.getHind_semiBold());
        tvCancelDues.setTypeface(appTypeface.getHind_semiBold());
        tvConfirmDuesPayInfo.setTypeface(appTypeface.getHind_semiBold());
        tvConfirmDuesAddress.setTypeface(appTypeface.getHind_medium());
        tvConfirmDuesDate.setTypeface(appTypeface.getHind_regular());
    }

    private void getIntentValues() {

        Bundle bundle;
        bundle = getIntent().getExtras();
        if(bundle!= null)
        {
            catId = bundle.getString("CatId");
            ArrayList<QuestionList>  questionList;
            questionList = (ArrayList<QuestionList>) bundle.getSerializable("QUESTIONS");
            bookingTypeAction = (Category.BookingTypeAction) bundle.getSerializable("BookingTypeAction");
            subCategoryArrayList = (ArrayList<SubCategory>)bundle.getSerializable("SubCat");
            offers = (ArrayList<Offers>) bundle.getSerializable("OFFERS");
            minAmount = bundle.getDouble("MinAmount",0);
            maxAmount = bundle.getDouble("MaxAmount",0);
            isBidding = bundle.getBoolean("IsBidding",false);
            callType = (CallType) bundle.getSerializable("CallType");
            Log.d(TAG, " getIntentValues: minAmount: "+minAmount+" maxAmount:"+maxAmount);
            Constants.catId = catId;

            if(!isBidding)
            {
                assert questionList != null;
                for(QuestionList queList : questionList)
                {
                    if(queList.getQuestionType()==QT11_TYPE_OF_SERVICE) //Which type of service (INCALL OUTCALL TELECALL)
                    {
                        if(setPredefinedList(queList))
                            questionLists.add(queList);
                        // Added else part to add job type as this was not set if there is only single call type
                        // Developer : shijen
                        else{
                            if(callType.isIncall()){
                                Constants.jobType=1;
                            }else if(callType.isOutcall()){
                                Constants.jobType=2;
                            }else if(callType.isTelecall()){
                                Constants.jobType=3;
                            }
                        }
                    }else {
                        if(queList.getQuestionType()!=3 && queList.getQuestionType()!=1 && queList.getQuestionType()!=4)
                            questionLists.add(queList);
                    }
                }
            }else {

                showNextQuestion(1);

              /*  assert questionList != null;
                for(QuestionList queList : questionList)
                {
                    if(queList.getQuestionType()==11)
                    {
                        if(setPredefinedList(queList))
                            questionLists.add(queList);
                    }else
                        questionLists.add(queList);
                }*/
            }
        }
    }

    private boolean setPredefinedList(QuestionList queList) {

        boolean isNotOnlyType;
        ArrayList<PreDefinedQuestions> preDefinedQuestions = new ArrayList<>();
        if(callType.isIncall() && callType.isOutcall() && callType.isTelecall())
        {
            preDefinedQuestions.add(new PreDefinedQuestions("inCallId",getString(R.string.inCall)));
            preDefinedQuestions.add(new PreDefinedQuestions("outCallId",getString(R.string.outCall)));
            preDefinedQuestions.add(new PreDefinedQuestions("teleCallId",getString(R.string.outTeleCall)));
            isNotOnlyType = true;

        }else if(callType.isIncall() && callType.isOutcall() && !callType.isTelecall())
        {
            preDefinedQuestions.add(new PreDefinedQuestions("inCallId",getString(R.string.inCall)));
            preDefinedQuestions.add(new PreDefinedQuestions("outCallId",getString(R.string.outCall)));
            isNotOnlyType = true;
        }else if(callType.isIncall() && !callType.isOutcall() && callType.isTelecall())
        {
            preDefinedQuestions.add(new PreDefinedQuestions("inCallId",getString(R.string.inCall)));
            preDefinedQuestions.add(new PreDefinedQuestions("teleCallId",getString(R.string.outTeleCall)));
            isNotOnlyType = true;
        }else if(!callType.isIncall() && callType.isOutcall() && callType.isTelecall())
        {
            preDefinedQuestions.add(new PreDefinedQuestions("outCallId",getString(R.string.outCall)));
            preDefinedQuestions.add(new PreDefinedQuestions("teleCallId",getString(R.string.outTeleCall)));
            isNotOnlyType = true;
        }else
            isNotOnlyType = false;

        if(isNotOnlyType)
            queList.setPreDefined(preDefinedQuestions);

        return isNotOnlyType;
    }


    @Override
    protected void onResume() {
        super.onResume();
        flag_guest_login = manager.getGuestLogin();
        yourAddressPresenter.getAddress(manager.getAUTH(), this);
        //  checkforCalendarPermission();
        // checkCalendarPermissions();

    }

    private void checkCalendarPermissions() {
        if(permissionsManager.areRuntimePermissionsRequired())
        {
            if(permissionsManager.isCalendarWritePermissionGranted(this))
                permissionsManager.requestCalendarPermission(this);

        }
    }

    private final int MY_PERMISSIONS_REQUEST_CALENDAR = 101;
    private void checkforCalendarPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("CALENDER PERMISSION");
                builder.setMessage("Need calendar permission to set remainder on later booking !");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(BiddingQuestions.this,new String[]{Manifest.permission.WRITE_CALENDAR},MY_PERMISSIONS_REQUEST_CALENDAR);
                    }
                });
                builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_CALENDAR},MY_PERMISSIONS_REQUEST_CALENDAR);
            }
        }
    }

    @OnClick({R.id.tvBookTypeSave,R.id.tvChangeAdd,R.id.etAddress,R.id.tvAddNewAddress,R.id.tvBidSkip,
            R.id.tvApply,R.id.ivSearchAddress,R.id.tvBookBidJobs,R.id.tvCancelDues,R.id.tvConfirmDues})//R.id.tvCardInfo,
    public void onClickedView(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            case R.id.tvAddNewAddress:
                intent=new Intent(this, AddAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.ivSearchAddress:
            case R.id.etAddress:
                intent = new Intent(this,SearchAddressLocation.class);
                intent.putExtra("CominFROM","HOMEFRAG");
                startActivityForResult(intent,SEARCH_RESULT);
                break;
            case R.id.tvBookTypeSave:
                saveNextQuestionClick(false);
                break;
            case R.id.tvBidSkip:
                saveNextQuestionClick(true);
                break;
            case R.id.tvChangeAdd:

                myCurrentLocation();
                if(AddressList.size()>0)
                    addressListAdapter.removeSelectedItem();

                break;
            case R.id.tvApply:
                applyPromoCode();
                break;
            case R.id.tvBookBidJobs:
                if(startQuestion == questionLists.size()-1)
                {
                    Utility.hideKeyboard(this);
                    setAnswerOnHashMap(startQuestion,true);
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



    private void saveNextQuestionClick(boolean isSkipped)
    {
        Utility.hideKeyboard(this);
        Intent intent;
        if (flag_guest_login) {
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            if(startQuestion==-1)
            {
                startQuestion ++;
                tvBookTypeSave.setText(R.string.next);
                showNextQuestion(startQuestion);
                tvBiddingQuestionInfo.setVisibility(View.GONE);
            }else {
                if(startQuestion<(questionLists.size()-1))
                {
                    if(questionLists.get(startQuestion).getQuestionType()==1)
                    {
                        if(!"".equals(totalBiddingAmt))
                            callNextIncrease(isSkipped);
                        else
                            alertProgress.alertinfo(this,getString(R.string.pleaseSetBiddingAmount));
                    }else
                        callNextIncrease(isSkipped);
                }
            }
            progressQuestion.setVisibility(View.VISIBLE);
            progressQuestion.setProgress(startQuestion);
        }
    }

    private void callNextIncrease(boolean isSkipped) {

        if(!isSkipped){
            //Developer shijen to fix crash in situation
            // incall selected
            // servicetype 2()
            // if last question is @QT2_BOOKING_WHEN
            // =============================================================
            if(isInCallSelected)
            {
                if(questionLists.get(startQuestion+1).getQuestionType()==QT2_BOOKING_WHEN){
                    if((startQuestion+1)==questionLists.size()-1){ //Question is last question
                        setAnswerOnHashMap(startQuestion,true);
                        return;
                    }
                }
            }
            //===========================================================
            setAnswerOnHashMap(startQuestion,false);
        }

        startQuestion++;
        if(isInCallSelected)
        {
            if(questionLists.get(startQuestion).getQuestionType()==2)
                startQuestion++;
        }
        showNextQuestion(startQuestion);
    }

    private void callBookingApi() {


        Utility.hideKeyboard(this);
        //onShowProgress();
        JSONArray  jsonArray = returnJsonArray();
        if(bidLatitude!=0){
            latLng = new LatLng(bidLatitude,bidLongitude);
        }
        Log.d(TAG, "callBookingApi: isWalletSelected: "+isWallet+" Paymenttype:"+paymentType);
        presenter.onLiveBookingCalled(paymentType,latLng,cardId,catId,isWallet,totalBiddingAmt,bidPromoCode,jsonArray,visitingTime,bidAddress);
    }

    private JSONArray returnJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for(int i = 0;i<questionLists.size();i++)
        {
            JSONObject jsonObject = new JSONObject();
            if(answerHashMap.containsKey(questionLists.get(i).get_id()))
            {
                try {
                    jsonObject.put("_id",answerHashMap.get(questionLists.get(i).get_id()).getQuestionId());
                    jsonObject.put("name",answerHashMap.get(questionLists.get(i).get_id()).getQuestion());
                    jsonObject.put("answer",answerHashMap.get(questionLists.get(i).get_id()).getAnswer());
                    jsonObject.put("questionType",answerHashMap.get(questionLists.get(i).get_id()).getQuestionType());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "callBookingApi: "+jsonObject);
                jsonArray.put(jsonObject);

                //  answerHashMap.get(questionLists.get(i).get_id()).get
            }
        }
        return jsonArray;
    }

    private void setAnswerOnHashMap(int questionPosition, boolean isToCallApi)
    {
        int questionType = questionLists.get(questionPosition).getQuestionType();
        String questionId = questionLists.get(questionPosition).get_id();
        String question = questionLists.get(questionPosition).getQuestion();

        switch (questionType)
        {
            case 0:
                if(!"".equals(bidPromoCode))
                {

                    answerHashMap.put(questionId,new AnswerHashMap(bidPromoCode,
                            question,questionId,0,"",false,bidPromoCode,null, questionType));
                }

                break;
            case 1:

                answerHashMap.put(questionId,new AnswerHashMap(Constants.currencySymbol+" "+totalBiddingAmt,
                        question,questionId,0,"",false,bidPromoCode,null, questionType));
                break;
            case 2:
                answerHashMap.put(questionId,new AnswerHashMap(visitSelected,
                        question,questionId,0,"",false,bidPromoCode,null, questionType));
                break;
            case 3:
                answerHashMap.put(questionId,new AnswerHashMap(bidAddress,
                        question,questionId,0,"",false,bidPromoCode,latLng, questionType));
                break;
            case 4:
                String payby = "";
                if(paymentType == 2 )
                    payby = getString(R.string.card);
                else
                    payby = getString(R.string.cash);
                answerHashMap.put(questionId,new AnswerHashMap(payby,
                        question,questionId,paymentType,cardId,isWallet,bidPromoCode,null, questionType));
                break;
            case 5:
                answerHashMap.put(questionId,new AnswerHashMap(etTextYourViews.getText().toString(),
                        question,questionId,paymentType,cardId,isWallet,bidPromoCode,null, questionType));
                break;
            case 6:
            case 7:
                answerHashMap.put(questionId,new AnswerHashMap(selectedAnswer,question,questionId,
                        0,"",false,bidPromoCode,null, questionType));
                break;
            case 10:
                String imagesIs = "";
                for (String tab : imageArray.values()) {
                    // do something with tab
                    if(!"".equals(tab))
                    {
                        if("".equals(imagesIs))
                            imagesIs = tab;
                        else
                            imagesIs = imagesIs +","+tab;
                    }
                }
                answerHashMap.put(questionId,new AnswerHashMap(imagesIs,question,questionId,
                        0,"",false,bidPromoCode,null, questionType));
                break;
        }

        if(isToCallApi)
        {
            if(isBidding)
            {
                Utility.hideKeyboard(this);
                if(!isDueFound)
                    onDuesApiCall();
                else
                    onDuesFoundBehaviour();
                // presenter.onLastDues();
            }else {
                Constants.jsonArray = returnJsonArray();
                //Developer : shijen
                //Solution : added
                //testing
                Intent intent;
                if(Constants.serviceType==2)
                {
                    intent = new Intent(this, ProviderList.class);
                    intent.putExtra("CatId",catId);
                    intent.putExtra("SubCat",subCategoryArrayList);
                    intent.putExtra("MinAmount",minAmount);
                    intent.putExtra("MaxAmount",maxAmount);
                    startActivity(intent);
                }else if(Constants.serviceType == 1)
                {

                    if(Constants.bookingModel==3)
                    {
                        if(manager.getGuestLogin())
                            intent = new Intent(this, LoginActivity.class);
                        else
                            intent = new Intent(this, ConfirmBookActivity.class);
                    }else
                        intent = new Intent(this, AddToCart.class);

                    startActivity(intent);
                }else
                    Toast.makeText(this,"Bidding ",Toast.LENGTH_SHORT).show();

                //testing


                //CallNewIntent();
            }
        }

    }

    private void onDuesApiCall() {
        onShowProgress();
        presenter.onLastDues();
    }

    private void CallNewIntent() {

        Intent intent = new Intent(this, ProviderList.class);
        intent.putExtra("CatId",catId);
        intent.putExtra("SubCat",subCategoryArrayList);
        intent.putExtra("MinAmount",minAmount);
        intent.putExtra("MaxAmount",maxAmount);
        startActivity(intent);
        overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
    }

    private void showNextQuestion(int startQuestion)
    {
        if(questionLists.size()>0)
        {
            Log.d(TAG, "showNextQuestion: "+startQuestion);
            int questionType = questionLists.get(startQuestion).getQuestionType();

            if(isBidding)
                tvBookBidJobs.setText(getString(R.string.postTheJob));
            else
                tvBookBidJobs.setText(getString(R.string.next));
            showVisibility();

            Utility.hideKeyboard(this);
            switch (questionType)
            {
                case 0:
                    whatIsTheQuestion(questionLists.get(startQuestion).getQuestion());
                    if(startQuestion!=questionLists.size()-1)
                        tvBookTypeSave.setVisibility(View.VISIBLE);
                    rlPromoCode.setVisibility(View.VISIBLE);
                    etPromoCode.setFocusable(true);
                    etPromoCode.setFocusable(true);
                    etPromoCode.requestFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    Utility.showKeyBoard(this);

                    break;
                case 1:
                    if(isRepeatCalled)
                    {
                        Intent intent = new Intent(this,BiddingOffers.class);
                        intent.putExtra("QUESTIONS",questionLists);
                        intent.putExtra("questionPosition",startQuestion);
                        startActivityForResult(intent,Constants.AMOUNT_RESULT_CODE);
                        overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);

                    }else
                    {
                        whatIsTheQuestion(questionLists.get(startQuestion).getQuestion());
                        tvBookTypeSave.setVisibility(View.VISIBLE);
                        rlBidAmount.setVisibility(View.VISIBLE);
                        etBiddingAmount.setFocusable(true);
                        etBiddingAmount.requestFocus();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        Utility.showKeyBoard(this);
                        biddingAmount();
                    }

                    break;
                case 2:
                    whatIsTheQuestion(questionLists.get(startQuestion).getQuestion());
                    tvBookTypeSave.setVisibility(View.GONE);
                    //  viewCalendar.setVisibility(View.VISIBLE);
                    llBidCardPayment.setVisibility(View.VISIBLE);

                    biddingCalendar();
                    break;
                case 3:
                    whatIsTheQuestion(questionLists.get(startQuestion).getQuestion());
                    tvBookTypeSave.setVisibility(View.VISIBLE);
                    linAddress.setVisibility(View.VISIBLE);
                    biddingAddress();
                    break;
                case 4:
                    whatIsTheQuestion(questionLists.get(startQuestion).getQuestion());
                    tvBookTypeSave.setVisibility(View.GONE);
                    llBidCardPayment.setVisibility(View.VISIBLE);
                    paymentMethod();
                    break;
                case 5:
                    etTextYourViews.setText("");
                    whatIsTheQuestion(questionLists.get(startQuestion).getQuestion());
                    Utility.showKeyBoard(this);
                    tvBookTypeSave.setVisibility(View.VISIBLE);
                    tilTextYourViews.setVisibility(View.VISIBLE);
                    etTextYourViews.setFocusable(true);
                    etTextYourViews.requestFocus();

                    break;
                case 7:
                case 6:
                case 11:
                    whatIsTheQuestion(questionLists.get(startQuestion).getQuestion());
                    tvBookTypeSave.setVisibility(View.VISIBLE);
                    recyclerViewQuestions.setVisibility(View.VISIBLE);
                    String answer = "";
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    QuestionAdapter questionAdapter = new QuestionAdapter(this,questionLists.get(startQuestion).getPreDefined(),questionType,answer,this);
                    recyclerViewQuestions.setLayoutManager(linearLayoutManager);
                    recyclerViewQuestions.setAdapter(questionAdapter);

                    break;
                case 10:
                    whatIsTheQuestion(questionLists.get(startQuestion).getQuestion());
                    tvBookTypeSave.setVisibility(View.VISIBLE);
                    recyclerViewQuestions.setVisibility(View.VISIBLE);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
                    questionAdapterGrid = new QuestionAdapterGrid(this,questionImages,this);
                    recyclerViewQuestions.setLayoutManager(gridLayoutManager);
                    recyclerViewQuestions.setAdapter(questionAdapterGrid);
                    break;
            }

            frameContainer.setVisibility(View.VISIBLE);

            if(startQuestion == questionLists.size()-1)
            {
                tvBidSkip.setVisibility(View.GONE);
                if(questionType!=2 && questionType!=4)
                    tvBookBidJobs.setVisibility(View.VISIBLE);
                tvBookTypeSave.setVisibility(View.GONE);
            }else {
                if(questionLists.get(startQuestion).getIsManadatory()==0)
                {
                    tvBidSkip.setVisibility(View.VISIBLE);
                }
                else
                    tvBidSkip.setVisibility(View.GONE);
            }

        }else
        {
            //showToast;
            alertProgress.alertinfo(this,"Sorry buddy Don't Have any question");
        }
    }

    private void applyPromoCode() {
        String code = etPromoCode.getText().toString().trim();
        bidPromoCode = code;
        if(!code.isEmpty())
        {
            etPromoCode.setText("");
            Utility.hideKeyboard(this);

            onShowProgressPromo();

            presenter.callPromoValidation(code,catId,bidLatitude,bidLongitude,paymentType,BiddingQuestions.this);

        }
    }


    private void biddingAddress() {

        etAddress.setText(bidAddress);
        if(!"".equals(manager.getLatitude()))
            latLng = new LatLng(bidLatitude,bidLongitude);
    }

    private void biddingCalendar() {

        fragmentTransaction(false);

    }

    private void biddingAmount() {

    }

    private void showVisibility() {
        tvBookTypeSave.setVisibility(View.GONE);
        recyclerViewQuestions.setVisibility(View.GONE);
        // viewCalendar.setVisibility(View.GONE);
        timePicker.setVisibility(View.GONE);
        linAddress.setVisibility(View.GONE);
        rlBidAmount.setVisibility(View.GONE);
        llBidCardPayment.setVisibility(View.GONE);
        rlPromoCode.setVisibility(View.GONE);
        tvBookBidJobs.setVisibility(View.GONE);
        tilTextYourViews.setVisibility(View.GONE);
    }

    private void paymentMethod()
    {
        fragmentTransaction(true);
    }

    private void fragmentTransaction(boolean isPayment) {
        if(questionLists.size()>0)
        {
            if(isPayment)
            {
                Bundle args = new Bundle();
                args.putInt(MyBiddingQuestionFrag.ARG_POSITION, fixedPosition);
                args.putInt(MyBiddingQuestionFrag.ARG_POSITION_QUE, startQuestion);
                args.putSerializable(MyBiddingQuestionFrag.ARG_QUESTION_LIST, questionLists);
                myBiddingQuestionFrag.setArguments(args);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fTransaction = fragmentManager.beginTransaction();
                fTransaction.replace(R.id.llBidCardPayment,myBiddingQuestionFrag);
                fTransaction.commit();
            }else
            {
                Bundle args = new Bundle();

                //  args.putInt(MyBiddingBookTypeFrag.ARG_POSITION_QUE, questionPosition);

                args.putSerializable(MyBiddingBookTypeFrag.ARG_BOOK_TYPE, bookingTypeAction);
                myBiddingBookingTypeFrag.setArguments(args);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fTransaction = fragmentManager.beginTransaction();
                fTransaction.replace(R.id.llBidCardPayment,myBiddingBookingTypeFrag);
                fTransaction.commit();
            }

            setBookTypeText(getString(R.string.next));
        }else
        {
            //showToast;
            alertProgress.alertinfo(this,"Sorry buddy Don't Have any question");
        }

    }

    private void setBookTypeText(String string) {
        tvBookTypeSave.setText(string);
    }

    private void whatIsTheQuestion(String question)
    {
        tvQuestionDesc.setText(question);
    }


    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        Log.d(TAG, "onBackPressed: "+startQuestion);
        switch (startQuestion)
        {
            case -1:
                finish();
                overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);
                break;
            case 0:
                startQuestion--;
                progressQuestion.setProgress(startQuestion);
                progressQuestion.setVisibility(View.INVISIBLE);
                frameContainer.setVisibility(View.GONE);
                tvBiddingQuestionInfo.setVisibility(View.VISIBLE);
                tvBookTypeSave.setVisibility(View.VISIBLE);
                String nameInfo = getString(R.string.biddingInfoQuestion)+" "+Constants.catName;
                tvBiddingQuestionInfo.setText(nameInfo);
                setBookTypeText(getString(R.string.getStarted));
                // are you stating
                break;
            default:
                showPreviousQuestion();
                break;
        }
    }

    private void showPreviousQuestion()
    {

        Utility.hideKeyboard(this);
        tvBookTypeSave.setText(R.string.next);
        startQuestion--;
        Log.d(TAG, "showPreviousQuestion: "+isInCallSelected+" questionList "+questionLists.get(startQuestion).getQuestionType());
        if(isInCallSelected)
        {
            if(questionLists.get(startQuestion).getQuestionType()==2)
                startQuestion--;
        }
        progressQuestion.setProgress(startQuestion);
        showNextQuestion(startQuestion);
        tvBiddingQuestionInfo.setVisibility(View.GONE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * For Wallet and Cash booking
     * This method is called after payment is set after payment selection in {#link {@link MyBiddingQuestionFrag} }
      */

    @Override
    public void onFragmentWalletInteraction(boolean isWallet, int paymentType, String cardId) {
        this.isWallet = isWallet;
        this.paymentType = paymentType;
        this.cardId = cardId;
        Log.d(TAG, "onFragmentWalletInteraction: this.iswallet: "+this.isWallet+" this.paymenttype: "+this.paymentType+" this.cardId:"+this.cardId);
        if(startQuestion == questionLists.size()-1)
        {
            Utility.hideKeyboard(this);
            setAnswerOnHashMap(startQuestion,true);
        }else {
            tvBookTypeSave.setVisibility(View.VISIBLE);
            llBidCardPayment.setVisibility(View.GONE);
            saveNextQuestionClick(false);
        }
    }

    /**
     * This methos is called if card is selected during payment in bidding of question flow
     * from {@link MyBiddingQuestionFrag#nextIntentForCar(int)}
     * @param paymentType
     * @param id
     * @param brand
     * @param last4
     */
    @Override
    public void onCardSelectedPayment(int paymentType, String id, String brand, String last4) {
        isWallet = false;
        this.paymentType = paymentType;
        cardId = id;
        Log.d(TAG, "onCardSelectedPayment: this.paymentType: "+this.paymentType+ " cardId: "+cardId);
        if(startQuestion == questionLists.size()-1)
        {
            Utility.hideKeyboard(this);
            setAnswerOnHashMap(startQuestion,true);
        }else
        {
            tvBookTypeSave.setVisibility(View.VISIBLE);
            llBidCardPayment.setVisibility(View.GONE);
            saveNextQuestionClick(false);
        }

    }

    @Override
    public void onAnswerSelected(String answer)
    {
        selectedAnswer = answer;
        if(!isBidding)
        {
            if(answer.equals(getString(R.string.inCall)))
            {
                Constants.jobType = 1;
                Constants.bookingType = 2;
                Constants.scheduledDate= ""+presenter.getStartOfDayInMillisToday()/1000;
                isInCallSelected = true;
            }else if(answer.equals(getString(R.string.outTeleCall)))
            {
                Constants.jobType = 3;
                Constants.bookingType = 2;
                Constants.scheduledDate= ""+presenter.getStartOfDayInMillisToday()/1000;
                isInCallSelected = true;
            }else if(answer.equals(getString(R.string.outCall))){
                Constants.jobType = 2;
                isInCallSelected = false;
            }
        }

    }

    @Override
    public void onPromoCodeError(String message) {
        alertProgress.alertinfo(BiddingQuestions.this,message);
        etPromoCode.setText("");
        bidPromoCode = "";

    }

    @Override
    public void onPromoCodeSuccess(double amount, String codes)
    {
        etPromoCode.setText(codes);
        bidPromoCode = codes;
        tvBookBidJobs.setText(getString(R.string.postTheJobs));
    }

    @Override
    public void onConnectionError(String message, String cartId, String s) {
        alertProgress.tryAgain(BiddingQuestions.this, getString(R.string.pleaseCheckInternet),
                getString(R.string.system_error), isClicked ->
                {
                    if(isClicked)
                    {
                        if("LiveBooking".equals(cartId))
                            callOnCliveBooking();
                            //
                        else if("LastDues".equals(cartId))
                            onDuesApiCall();
                    }
                });
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
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //VariableConstant.isSplashCalled = true;
            startActivity(intent);
            finish();
        },1000);
    }

    @Override
    public void addItems(ArrayList<YourAddrData> yourAddrDataList) {
        AddressList.clear();
        AddressList.addAll(yourAddrDataList);
        addressListAdapter.notifyDataSetChanged();
        if(AddressList.size()>0)
        {
            tvOrAddress.setVisibility(View.VISIBLE);
            viewOrAddress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setNoAddressAvailable() {

    }

    @Override
    public void setError(String message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            switch (requestCode) {

                case SEARCH_RESULT:
                    if(data!=null)
                    {
                        String addrssname = data.getStringExtra("placename");
                        String featureAddressName = data.getStringExtra("formatedaddes");
                        bidLatitude = data.getDoubleExtra("LATITUDE",0);
                        bidLongitude = data.getDoubleExtra("LONGITUDE",0);
                        if("NO".equals(addrssname))
                            myCurrentLocation();
                        else
                        {
                            etAddress.setText(addrssname);
                            bidAddress = featureAddressName;
                        }
                        latLng = new LatLng(bidLatitude,bidLongitude);

                        etAddress.setFocusable(false);
                    }
                    break;
                case Constants.ADDRESS_RESULT_CODE:
                    if(data!=null)
                    {
                        bidLatitude = data.getDoubleExtra("lat",0.0);
                        bidLongitude = data.getDoubleExtra("lng",0.0);
                        String bookingAddress = data.getStringExtra("AddressLine1");
                        latLng = new LatLng(bidLatitude,bidLongitude);
                        String tag = data.getStringExtra("TAGAS");
                        String bookingAddress2 = data.getStringExtra("AddressLine2");
                        etAddress.setText(bookingAddress);
                        bidAddress = bookingAddress;
                    }
                    break;
                case Constants.PAYMENT_RESULT_CODE:
                    if(data!=null) {
                        isWallet = data.getBooleanExtra("ISWallet",false);
                        paymentType = data.getIntExtra("PAYMENTTYPE",1);
                        if(isWallet){
                        }else {
                            if(paymentType == 1) {
                            }
                            else if(paymentType ==2 ) {
                                cardId = data.getStringExtra("CARDID");
                                String cardType = data.getStringExtra("CARDTYPE");
                                String last4 = data.getStringExtra("LAST4");
                                setCardInfo(cardType,last4);
                                manager.setDefaultCardId(cardId);
                                manager.setDefaultCardNum(last4);
                                manager.setDefaultCardName(cardType);
                            }
                        }
                    }
                    break;
                case Constants.CAMERA_PIC:
                    handlePicEvent.startCropImage(handlePicEvent.newFile);
                    break;
                case Constants.GALLERY_PIC:
                    if(data!=null)
                        handlePicEvent.gallery(data.getData());
                    break;
                case Constants.CROP_IMAGE:
                    onShowProgressPromo();
                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    imagePositionTaken ++;
                    if (path != null)
                    {
                        try {
                            File fileExist = new File(path);

                            //imageFlag = true;
                            handlePicEvent.uploadToAmazon(Constants.Amazonbucket + "/" + Constants.AmazonUploadJobImage, fileExist, new ImageUploadedAmazon() {
                                @Override
                                public void onSuccessAdded(String image) {
                                    imageArray.put(imagePositionTaken,image);
                                    onHideProgressPromo();
                                }

                                @Override
                                public void onerror(String errormsg) {
                                }

                            });
                            questionImages.add(questionImages.size()-1,new QuestionImage(path,true,imagePositionTaken));
                            questionAdapterGrid.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Constants.AMOUNT_RESULT_CODE:
                    if(data!=null)
                    {
                        double total = data.getDoubleExtra("TATALBIDDINGAMT",0);
                        totalBiddingAmt = String.valueOf(total);
                        saveNextQuestionClick(false);
                    }
                    break;
            }
        }else if(resultCode==RESULT_FIRST_USER)
        {
            showPreviousQuestion();
        }
    }

    private void setCardInfo(String cardType, String last4) {
        paymentType = 2;
    }

    @Override
    public void onSessionExpired() {
        rlMAinBidding.setVisibility(View.VISIBLE);
        rlMAinBooking.setVisibility(View.GONE);
        tvBookTypeSave.setVisibility(View.VISIBLE);
        llBiddingQuestion.setVisibility(View.VISIBLE);
        tvBookBidJobs.setVisibility(View.VISIBLE);
    }
    private void callOnCliveBooking() {
        onShowProgress();
        noDuesFoundLiveBooking();
    }

    @Override
    public void noDuesFoundLiveBooking() {

        callBookingApi();
    }



    @Override
    public void onDuesFound(String msg, String addLine1, String formattedDate) {

        tvConfirmDuesPayInfo.setText(msg);
        tvConfirmDuesAddress.setText(addLine1);
        tvConfirmDuesDate.setText(formattedDate);
        onDuesFoundBehaviour();
    }

    private void onDuesFoundBehaviour()
    {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            //  btnBottomSheet.setText("Close sheet");
        }
        isDueFound = true;
    }

    @Override
    public void onLogout(String message) {
        alertProgress.alertPositiveOnclick(this, message, getString(R.string.logout),getString(R.string.ok), isClicked -> Utility.setMAnagerWithBID(BiddingQuestions.this,manager));
    }

    @Override
    public void onSessionExpired(String errorMsg) {
        alertProgress.alertinfo(BiddingQuestions.this,errorMsg);
        rlMAinBidding.setVisibility(View.VISIBLE);
        rlMAinBooking.setVisibility(View.GONE);
        tvBookTypeSave.setVisibility(View.VISIBLE);
        tvBookBidJobs.setVisibility(View.VISIBLE);
        llBiddingQuestion.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(String error) {
        alertProgress.alertinfo(BiddingQuestions.this,error);


    }

    @Override
    public void onShowProgressPromo() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBarBid.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgressPromo() {
        progressBarBid.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onShowProgress() {
        rlMAinBidding.setVisibility(View.GONE);
        rlMAinBooking.setVisibility(View.VISIBLE);
        tvBookTypeSave.setVisibility(View.GONE);
        tvBookBidJobs.setVisibility(View.GONE);
        llBiddingQuestion.setVisibility(View.GONE);
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
    public void onFragmentNowLater(boolean isNow, long selectedScheduledDateTime, int selectedDuration) {

        presenter.onNowLaterSelected(isNow,selectedScheduledDateTime,selectedDuration);
        if(isNow)
        {
            visitingTime = 1;
            visitSelected = getString(R.string.now);
        }else
        {
            visitingTime = 2;
            visitSelected = getString(R.string.schedule);
        }
        isRepeatCalled = false;
        if(startQuestion == questionLists.size()-1)
        {
            Utility.hideKeyboard(this);
            setAnswerOnHashMap(startQuestion,true);
        }else{
            tvBookTypeSave.setVisibility(View.VISIBLE);
            llBidCardPayment.setVisibility(View.GONE);
            saveNextQuestionClick(false);
        }
    }

    @Override
    public void onFragmentRepeat(long selectedScheduledDateTime, long selectedEndDate, int selectedDuration, ArrayList<String> repeatBooking) {
        visitingTime = 3;
        visitSelected = getString(R.string.repeat);
        presenter.onRepeatSelected(selectedScheduledDateTime,selectedEndDate,selectedDuration,repeatBooking);
        isRepeatCalled = true;
        if(startQuestion == questionLists.size()-1)
        {
            Utility.hideKeyboard(this);
            setAnswerOnHashMap(startQuestion,true);
        }else{
            tvBookTypeSave.setVisibility(View.VISIBLE);
            llBidCardPayment.setVisibility(View.GONE);
            saveNextQuestionClick(false);
        }
    }


    @Override
    public void deletePhoto(int adapterPosition, int imagePostion) {

        alertProgress.alertPositiveNegativeOnclick(this, getString(R.string.areYouSureYouWantOTDeleteImage),
                getString(R.string.delete), getString(R.string.ok), getString(R.string.cancel), true, isClicked -> {
                    if(isClicked)
                    {
                        questionImages.remove(adapterPosition);
                        imageArray.remove(imagePostion);
                        imagePositionTaken--;
                        questionAdapterGrid.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public void onToTakeImage(int adapterPosition)
    {
        checkPermission();
    }

    private void checkPermission()
    {
        if(permissionsManager.areRuntimePermissionsRequired())
        {
            if(permissionsManager.areCameraFilePermissionGranted(this))
                permissionsManager.requestCameraPermissions(this);
            else
                selectImage();
        }else
            selectImage();
    }

    private void selectImage() {
        handlePicEvent.openDialog();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain)
    {
    }
    @Override
    public void onPermissionResult(boolean granted)
    {
        if(granted)
            handlePicEvent.openDialog();
    }

    BiddingContractor.BiddingDateTIme biddingDateTIme;
    @Override
    public void onFragmentInteraction(Date uri, boolean isSchedule) {

        if(biddingDateTIme!=null)
            biddingDateTIme.onDateTimeSel(uri,isSchedule);
    }

    public void biddingSelected(MyBiddingBookTypeFrag myBiddingBookTypeFrag)
    {
        biddingDateTIme = myBiddingBookTypeFrag;
    }
    private class QuestionAdapter extends RecyclerView.Adapter
    {

        Context mContext;
        ArrayList<PreDefinedQuestions> preDefined = new ArrayList<>();
        int questionType;
        private int selectedPosition = 0;
        private String answer;
        BiddingContractor.BiddingContractView biddingContractor;
        public QuestionAdapter(Context mContext, ArrayList<PreDefinedQuestions> preDefined,
                               int questionType, String answer,BiddingContractor.BiddingContractView biddingContractor)
        {
            this.mContext =mContext;
            this.preDefined =preDefined;
            this.questionType =questionType;
            this.answer = answer;
            this.biddingContractor = biddingContractor;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from(mContext).inflate(R.layout.bidding_question,parent,false);
            return new RecyclerViewHoldersView(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            RecyclerViewHoldersView hold = (RecyclerViewHoldersView) holder;
            hold.radioButtonBidding.setVisibility(View.VISIBLE);
            hold.radioButtonBidding.setText(preDefined.get(position).getName());
            if(questionType==6 || questionType == 11)
            {

                if(position == selectedPosition)
                {
                    hold.radioButtonBidding.setSelected(true);
                    answer =  preDefined.get(position).getName();
                    biddingContractor.onAnswerSelected(answer);
                }else {
                    hold.radioButtonBidding.setSelected(false);
                }

                if(questionType == 11)
                {
                    hold.descriptionDtls.setVisibility(View.VISIBLE);
                    if(preDefined.get(position).getName().equals(mContext.getString(R.string.inCall)))
                        hold.descriptionDtls.setText(mContext.getString(R.string.inCallDesc));
                    else if(preDefined.get(position).getName().equals(mContext.getString(R.string.outCall)))
                        hold.descriptionDtls.setText(mContext.getString(R.string.outCallDesc));
                    else
                        hold.descriptionDtls.setText(mContext.getString(R.string.outTeleCallDesc));
                }
            }else if(questionType==7)
            {
                if(position == selectedPosition)
                {
                    hold.radioButtonBidding.setSelected(true);
                    if("".equals(answer))
                    {
                        answer = preDefined.get(position).getName();
                    }else
                    {
                        answer  = answer +","+preDefined.get(position).getName();
                    }
                    biddingContractor.onAnswerSelected(answer);
                }

            }
        }

        @Override
        public int getItemCount() {
            return preDefined.size();
        }


        private class RecyclerViewHoldersView extends RecyclerView.ViewHolder
        {
            TextView radioButtonBidding,descriptionDtls;
            // CheckBox checkBoxBidding;

            public RecyclerViewHoldersView(View itemView) {
                super(itemView);
                radioButtonBidding = itemView.findViewById(R.id.radioButtonBidding);
                descriptionDtls = itemView.findViewById(R.id.descriptionDtls);
                // checkBoxBidding = itemView.findViewById(R.id.checkBoxBidding);
                descriptionDtls.setTypeface(appTypeface.getHind_light());
                itemView.setOnClickListener(view -> {
                    notifyItemChanged(selectedPosition);
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                });

            }
        }
    }
}
