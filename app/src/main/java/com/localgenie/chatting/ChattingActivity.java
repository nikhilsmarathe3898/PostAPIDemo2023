package com.localgenie.chatting;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.localgenie.R;
//import com.localgenie.networking.ChatApiService;
import com.localgenie.home.MainActivity;
import com.localgenie.jobDetailsStatus.JobDetailsActivity;
import com.localgenie.utilities.AppPermissionsRunTime;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.HandlePictureEvents;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;

import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;
import com.pojo.ChatData;
import com.pojo.ChatDataObervable;
import com.pojo.MyBookingObservable;
import com.pojo.MyBookingStatus;
import com.utility.AlertProgress;
//import com.utility.AndroidMultiPartEntity;
import com.utility.DialogInterfaceListner;
import com.utility.NotificationUtils;
import com.utility.PermissionsManager;

//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;

import org.json.JSONObject;
import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import adapters.ChattingAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import eu.janmuller.android.simplecropimage.CropImage;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


import javax.inject.Inject;


public class ChattingActivity extends DaggerAppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        ChattingPresenter.ViewChatting{

    private static final String TAG = "Chatting";

    @BindView(R.id.etMsg)EditText etMsg;
    private final int REQUEST_CODE_PERMISSION_MULTIPLE = 11;

    @BindView(R.id.rcvChatMsg)RecyclerView rcvChatMsg;
    @BindView(R.id.rlBookingDetails)RelativeLayout rlBookingDetails;
    @BindView(R.id.tvCategoryName)TextView tvCategoryName;
    @BindView(R.id.tvTotalPrice)TextView tvTotalPrice;
    @BindView(R.id.tvViewDetails)TextView tvViewDetails;
    @BindView(R.id.srlChat)SwipeRefreshLayout srlChat;
    @BindView(R.id.cardViewChat)CardView cardViewChat;
    private int typeMsg;

    @Inject
    PermissionsManager permissionsManager;

    @Inject ChattingPresenter.Presenter presenter;

    private HandlePictureEvents handlePicEvent;
    @Inject SessionManagerImpl sessionManager;
    @Inject
    AppTypeface appTypeface;
   // ChatApiService chatApiService;

    private long bid;
    private String proId,cId;
    private String proName;
    private ArrayList<ChatData> chatDataArry;
    private ChattingAdapter cAdapter;
    //   private DataBaseChat db;

    @Inject
    MQTTManager mqttManager;
    private File mFileTemp;
    private LayoutInflater inflater;
    private NotificationManager notificationManager;

    private Gson gson;
    private int pageIndex = 0;
    private boolean isMoreDataAvailable = true;
    private JSONObject jsonObj;
    @Inject
    AlertProgress alertProgress;
    private Observer<ChatData> observer;
    private long  responcecTimeStap = 0;
    private boolean isChatting = false;
    private int status = 0,bookingModel = 0;
    private int calltype;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Utility.checkAndShowNetworkError(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        ButterKnife.bind(this);
        if(!mqttManager.isMQTTConnected())
            mqttManager.createMQttConnection(sessionManager.getSID(),false);
      //  chatApiService = ServiceFactory.createRetrofitChatService(ChatApiService.class);
        jsonObj = new JSONObject();
        typeFace();
        init();
        initializeRxJavaStatus();
        sessionManager.clearChatCountPreference(bid);

    }

    private void initializeRxJavaStatus() {

        Observer observer = new Observer<MyBookingStatus>() {
                @Override
                public void onSubscribe(Disposable d)
                {

                }

                @Override
                public void onNext(MyBookingStatus myBookingStatus)
                {
                   int statusCode = myBookingStatus.getData().getStatus();
                   if(statusCode == 11 || statusCode == 12)
                   {
                       alertProgress.alertPositiveOnclick(ChattingActivity.this, myBookingStatus.getData().getStatusMsg(), getString(R.string.cancel),getString(R.string.ok), new DialogInterfaceListner() {
                           @Override
                           public void dialogClick(boolean isClicked) {
                               Intent intent = new Intent(ChattingActivity.this, MainActivity.class);
                               Constants.isJobDetailsOpen = false;
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               startActivity(intent);
                               finish();
                           }
                       });
                   }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };

            MyBookingObservable.getInstance().subscribe(observer);

    }

    private void typeFace() {
        tvCategoryName.setTypeface(appTypeface.getHind_semiBold());
        tvTotalPrice.setTypeface(appTypeface.getHind_regular());

        if(getIntent().getExtras()!=null)
        {
            isChatting = getIntent().getBooleanExtra("isChating",false);
            status = getIntent().getIntExtra("STATUSCODE",0);
            calltype = getIntent().getIntExtra("CallType",0);
            double amount = getIntent().getDoubleExtra("AMOUNT",0);
            String currency = getIntent().getStringExtra("CurrencySymbol");
            Utility.setAmtOnRecept(amount,tvTotalPrice,currency);
        }
    }

    private void init()
    {
        gson = new Gson();
        chatDataArry = new ArrayList<>();
        handlePicEvent = new HandlePictureEvents(this);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        inflater =  LayoutInflater.from(this);
        if(isChatting)
        {
            rlBookingDetails.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.setMargins(0, 0, 0, 0);
            srlChat.setLayoutParams(buttonLayoutParams);
        }
        presenter.getIntentValue();

        if(status == 4 || status == 5 || status == 10 || status == 11 || status == 12 )
        {
            cardViewChat.setVisibility(View.GONE);
        }

        Toolbar toolbarChatting = findViewById(R.id.toolbarChatting);
        setSupportActionBar(toolbarChatting);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarChatting.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbarChatting.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // db = new DataBaseChat(this);
        //chatDataArry = db.fetchData(bid);

        ImageView ivAddFiles = findViewById(R.id.ivAddFiles);
        TextView tvchatproname = findViewById(R.id.tvchatproname);
        TextView tvSend = findViewById(R.id.tvSend);
        TextView tvEventId = findViewById(R.id.tvEventId);


        tvEventId.setText(getResources().getString(R.string.jobId)+" "+bid);

        cAdapter = new ChattingAdapter(this,chatDataArry, cId);
        rcvChatMsg.setLayoutManager(new LinearLayoutManager(this));
        rcvChatMsg.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layoutanimation_from_bottom));
        rcvChatMsg.setAdapter(cAdapter);

        srlChat.setOnRefreshListener(this);

        tvchatproname.setText(proName);
        tvEventId.setTypeface(appTypeface.getHind_regular());
        etMsg.setTypeface(appTypeface.getHind_regular());
        tvSend.setTypeface(appTypeface.getHind_semiBold());

        ivAddFiles.setOnClickListener(this);
        tvSend.setOnClickListener(this);
        tvViewDetails.setOnClickListener(this);

        Constants.IS_CHATTING_OPENED = true;

        if(!mqttManager.isMQTTConnected())
            mqttManager.createMQttConnection(cId,false);
        else
            mqttManager.subscribeToTopic(MqttEvents.Message.value + "/" + cId, 2);

        etMsg.setOnFocusChangeListener((view, b) -> scrollToBottomSlowly());

        etMsg.setOnClickListener(this);


        if(getIntent().getBooleanExtra("isPastChat",false))
        {
            CardView cardViewChat = findViewById(R.id.cardViewChat);
            cardViewChat.setVisibility(View.GONE);
        }

        initializeRxJava();
        getMessage();
    }

    private void initializeRxJava()
    {

        observer = new Observer<ChatData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ChatData chatData) {

                Log.w(TAG, "onNextChatResponse: "+chatData.getType());
                if(chatData.getBid()==bid)
                {
                    if(responcecTimeStap!=chatData.getTimestamp())
                    {
                        responcecTimeStap = chatData.getTimestamp();
                        notifyAdapter(chatData.getTimestamp(),chatData.getType(),chatData.getContent(),proId,cId,2);
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();
                    }

                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        ChatDataObervable.getInstance().subscribe(observer);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvSend:
                if(!etMsg.getText().toString().trim().isEmpty())
                {
                    String contentMsg = etMsg.getText().toString().trim();
                    etMsg.setText("");
                    typeMsg = 1;

                    long msgId = (System.currentTimeMillis());///1000;
                    sendMessage(contentMsg,typeMsg, msgId);
                }
                break;

            case R.id.ivAddFiles:
                checkPermission();
                break;

            case R.id.etMsg:
                scrollToBottomSlowly();
                break;
            case R.id.tvViewDetails:
                Intent intent = new Intent(this, JobDetailsActivity.class);
                intent.putExtra("BID",bid);
                intent.putExtra("STATUS", status);
                intent.putExtra("BookingModel", bookingModel);
                intent.putExtra("CallType", calltype);
                startActivity(intent);

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.IS_CHATTING_RESUMED = true;
        notificationManager.cancelAll();
        sessionManager.setChatCount(bid,0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constants.IS_CHATTING_RESUMED = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

       // mqttManager.unSubscribeToTopic(MqttEvents.Message.value + "/" + cId);
        Constants.IS_CHATTING_OPENED = false;
        //   observer.onComplete();
    }


    @Override
    public void onRefresh() {
        pageIndex ++;
        if(isMoreDataAvailable)
        {
            getMessage();
        }
        else
        {
            srlChat.setRefreshing(false);
        }
    }

    private void getMessage()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.onHistoryApi(bid,proId,pageIndex);
            }
        },1000);

        srlChat.setRefreshing(true);
    }

    /**
     * <h2>sendmessage</h2>
     * <p>
     *     sending request to channel a "Message" of socket
     * @param msg message to be send
     * @param msgType message type, i.e what type of message is it. example text message type ,image message type, video message type
     *                for txt msgtype = 0, for video 2, for image 1;
     * @param msgId timeStamp in GMT
     */
    private void sendMessage(String msg, int msgType, long msgId)
    {
        presenter.onPostMsg(msgType,msgId,msg,cId,bid,proId);
        notifyAdapter(msgId,msgType,msg,cId,proId,1);
        scrollToBottom();
    }


    private void notifyAdapter(long msgId, int msgType, String msg, String customerSid, String proId, int custProType)
    {
        ChatData chatData = new ChatData();
        chatData.setTimestamp(msgId);
        chatData.setType(msgType);
        chatData.setTargetId(proId);
        chatData.setFromID(customerSid);
        chatData.setContent(msg);
        chatData.setCustProType(custProType);
        chatDataArry.add(chatData);
        cAdapter.notifyDataSetChanged();

        //   db.addNewChat(msg,customerSid,proId,msgid,msgtype,custProType,bid);

        scrollToBottom();

    }

    /*scrolling to the bottom of the recyclerview*/
    private void scrollToBottom() {
        rcvChatMsg.scrollToPosition(cAdapter.getItemCount() - 1);
    }

    /*scrolling to the bottom of the recyclerview*/
    private void scrollToBottomSlowly() {
        rcvChatMsg.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                rcvChatMsg.scrollToPosition(cAdapter.getItemCount()-1);
            }
        },200);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (handlePicEvent.newFile != null)
            outState.putString("cameraImage", handlePicEvent.newFile.getPath());
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("cameraImage")) {
                Uri uri = Uri.parse(savedInstanceState.getString("cameraImage"));
                handlePicEvent.newFile = new File(uri.getPath());
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * <h2>checkPermission</h2>
     * <p>checking for the permission for camera and file storage at run time for
     * build version more than 22
     */
    private void checkPermission()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<AppPermissionsRunTime.MyPermissionConstants> myPermissionConstantsArrayList = new ArrayList<>();
            myPermissionConstantsArrayList.clear();
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_CAMERA);
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_WRITE_EXTERNAL_STORAGE);
            myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_READ_EXTERNAL_STORAGE);

            if (AppPermissionsRunTime.checkPermission(this, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE))
                selectImage();
        }else
            selectImage();
    }

    /**
     * predefined method to check run time permissions list call back
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
                    alertProgress.alertinfo(this,getResources().getString(R.string.permissiondenied)+ " " + REQUEST_CODE_PERMISSION_MULTIPLE);
                } else {
                    selectImage();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void selectImage() {

        handlePicEvent.openDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode != RESULT_OK)    //result code to check is the result is ok or not
        {
            return;
        }
        switch (requestCode) {
            case Constants.CAMERA_PIC:
                handlePicEvent.startCropImage(handlePicEvent.newFile);
                break;
            case Constants.GALLERY_PIC:
                if(data!=null)
                    handlePicEvent.gallery(data.getData());
                break;
            case Constants.CROP_IMAGE:
                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path != null)
                {
                    try {
                        launchUploadActivity(path);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                break;
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

    }

    @Override
    public void onShowProgress() {

    }

    @Override
    public void onHideProgress() {

    }

    @Override
    public void onMoreAvailable(boolean isMoreAvailable) {
        isMoreDataAvailable = isMoreAvailable;

    }

    @Override
    public void onChatHistoryResponse(ArrayList<ChatData> data) {

        chatDataArry.addAll(data);

        Collections.sort(chatDataArry, new Comparator<ChatData>() {
            @Override
            public int compare(ChatData o1, ChatData o2) {
                return (int)(o1.getTimestamp() - o2.getTimestamp());
            }
        });
        cAdapter.notifyDataSetChanged();

        if(pageIndex == 0)
        {
            scrollToBottom();
        }
    }

    @Override
    public void onRefreshing(boolean b) {
        srlChat.setRefreshing(b);
    }

    @Override
    public void setIntentValue(long chatBookingID, String chatProId, String proName, String sid) {
        bid = chatBookingID;
        proId = chatProId;
        this.proName = proName;
        cId = sid;
        tvCategoryName.setText(proName);
    }

    @Override
    public void sendImageMessage(String image, int typeMsg, long msgid) {
        sendMessage(image,typeMsg,msgid);
    }

    private void launchUploadActivity(String path)
    {
        presenter.loadImage(path,bid);
    }

}
