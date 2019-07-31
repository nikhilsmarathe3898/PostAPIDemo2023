package com.utility;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.chatting.ChattingActivity;
import com.localgenie.home.MainActivity;
import com.localgenie.invoice.InvoiceActivity;
import com.localgenie.jobDetailsStatus.JobDetailsActivity;
import com.localgenie.rateYourBooking.RateYourBooking;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.videocalling.UtilityVideoCall;
import com.localgenie.zendesk.zendeskHelpIndex.ZendeskHelpIndex;
import com.pojo.NotificationPojo;
import com.webRtc.CallingApis;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * <h>MyFirebaseMessagingService</h>
 * Created by 3Embed on 10/26/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    JSONObject data;
    private String TAG = "MyFirebaseMessagingService";
    private String message;
    private int action, bookingModel = 0, callType;
    private NotificationUtils notificationUtils;
    private Gson gson = new Gson();
    private long bid;
    private String picUrl = "";
    private String title = "";
    private TaskStackBuilder stackBuilder;
    private String timestamp;
    private SessionManager manager;

    /**
     * <h>checkAndAddEvent</h>
     * Add the event in to the google calendar
     *
     * @param context     Context of the activity
     * @param bookingId   booking id of the booked service
     * @param bookingTime booking time of the booked service
     * @param manager     session manager object
     */

    private static int checkAndAddEvent(Context context, long bookingId, long bookingTime, SessionManagerImpl manager) {


        CalendarEventHelper calendarEventHelper = new CalendarEventHelper(context);
        Log.d("TAG", "checkAndAddEvent: ");
        int eventId = 0;
        eventId = calendarEventHelper.addEvent(bookingTime, bookingId);
      /*  if(manager.getBookingStatus(bookingId)==3){

        }*/
        return eventId;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        manager = SessionManager.getInstance(this);
        timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());


        if (remoteMessage == null)
            return;
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());

        }
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Notification Data: " + remoteMessage.getData());
            stackBuilder = TaskStackBuilder.create(this);
            String title = getResources().getString(R.string.app_name);
            String s = remoteMessage.getData().get("action");
            action = Integer.parseInt(s);

            message = remoteMessage.getData().get("msg");
            String data = remoteMessage.getData().get("data");
            this.title = remoteMessage.getData().get("title");
            Log.d(TAG, "onMessageReceivedDATA: " + data);
            try {
                if (action == 23) {
                    handleDataMessage(action, picUrl, 12, null);
                } else if (action == 70) {
                    if (!Constants.isHelpIndexOpen)
                        sendNotification(message);
                } else if (action == 111) {
                    information();
                } else if (action == 120) {
                    Map<String, String> params = remoteMessage.getData();
                    calling(params);
                }else if(action == 201)
                {
                    information();
                } else {
                    int index = data.lastIndexOf("}");
                    String dataSubString = data.substring(0, index + 1);
                    NotificationPojo notificationPojo = gson.fromJson(dataSubString, NotificationPojo.class);
                    bid = notificationPojo.getBookingId();
                    picUrl = notificationPojo.getProfilePic();
                    bookingModel = notificationPojo.getBookingModel();
                    callType = notificationPojo.getCallType();
                    this.title = notificationPojo.getStatusMsg();
                    if (action == 112)
                        handleChatNotification(notificationPojo);
                    else
                        handleDataMessage(action, picUrl, bid, notificationPojo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void calling(Map<String, String> dataSubString) {

        Log.d(TAG, "calling: " + dataSubString);



        /*try {


         *//*
            if (!manager.isAppOpen()) {
                if (obj.getString("type").equals("0")) {
                    UtilityVideoCall.getInstance().setActiveOnACall(true, true);
                    manager.setChatBookingID(obj.getLong("bookingId"));
                    manager.setChatProId(obj.getString("callerId"));
                    manager.setProName(obj.getString("callerName"));
                    CallingApis.OpenIncomingCallScreen(obj, this);
                }
            }
*//*


            JSONObject tempObj = new JSONObject();
            tempObj.put("status", 0);

            Log.d(TAG, "Calling received: 2 ");


        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        data = new JSONObject(dataSubString);
        CallingApis.OpenIncomingCallScreen(data, this);
        //UtilityVideoCall.getInstance().setActiveOnACall(true, true);
        // JSONObject data = null;

        // UtilityVideoCall.getInstance().setActiveOnACall(true, true);
        // String isVideo = "0";

          /*  if( obj.getString("type").equals("video"))
            {
                isVideo = "1";
            }*/
        /*    Intent incomingScreen = new Intent(getApplicationContext(), IncomingCallScreen.class);
            stackBuilder.addParentStack(MainActivity.class);

            stackBuilder.addNextIntent(incomingScreen);
            incomingScreen.putExtra("callerName",  obj.getString("userName"));
            incomingScreen.putExtra("BookingId",  obj.getString("bookingId"));
            incomingScreen.putExtra("roomId",  obj.getString("room"));
            incomingScreen.putExtra("callId",  obj.getString("callId"));
            incomingScreen.putExtra("callerId", manager.getSID());
            incomingScreen.putExtra("callType",  obj.getString(isVideo));
            incomingScreen.putExtra("callerIdentifier",  obj.getString("userName"));*/
       /*     Intent incomingScreen = new Intent("com.localgenie.videocalling.IncomingCallScreen");
            incomingScreen.putExtra("callerImage", obj.getString("userImage"));
            incomingScreen.putExtra("callerName",  obj.getString("userName"));
            incomingScreen.putExtra("BookingId",  obj.getString("bookingId"));
            incomingScreen.putExtra("roomId",  obj.getString("room"));
            incomingScreen.putExtra("callId",  obj.getString("callId"));
            incomingScreen.putExtra("callerId", manager.getSID());
            incomingScreen.putExtra("callType",  obj.getString(isVideo));
            incomingScreen.putExtra("callerIdentifier",  obj.getString("userName"));

            incomingScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            UtilityVideoCall.getInstance().setActiveOnACall(true, true);
            CallingApis.OpenIncomingCallScreen(obj, this);*/



           /* sessionManager.setChatCount(obj.getString("bookingId"), sessionManager.getChatCount(obj.getString("bookingId")+1));
            sessionManager.setChatBookingID(obj.getString("bookingId"));
            sessionManager.setChatCustomerName(obj.getString("callerName"));
            sessionManager.setChatCustomerID(obj.getString("callerId"));*/

    }

    private void information() {

        if (!Utility.isAppIsInBackground(getApplicationContext())) {
            // app is in foreGround
            Intent pushNotification = new Intent(getApplicationContext(), MainActivity.class);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(pushNotification);
            showNotificationMessage(getApplicationContext(), title, message, timestamp, pushNotification, false);
        } else {
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);//MainActivity
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, false);
        }

    }

    // End onReceive

    private void handleChatNotification(NotificationPojo notificationPojo) {
        Log.d(TAG, "handleChatNotification: " + notificationPojo);

        this.title = notificationPojo.getName();
        timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());


        //  ChatData chatData = gson.fromJson(jsonRemoteMessage.getString("data"),ChatData.class);
        long timeStamp = /*Long.parseLong(String.valueOf(*/notificationPojo.getTimestamp();
        /*if(!Constants.IS_CHATTING_OPENED)
        {
            db.addNewChat(String.valueOf(chatData.getBid()),chatData.getContent(), sessionManager.getProviderId(),
                    chatData.getFromID(), String.valueOf(chatData.getTimestamp()),chatData.getType(),"1");
        }*/

        Log.d(TAG, "handleChatNotification: getlasttimestampmsg: timeStamp: " + timeStamp + "manager.get" + manager.getLastTimeStampMsg());
        if (timeStamp > manager.getLastTimeStampMsg()) {
            manager.setLastTimeStampMsg(timeStamp);

            /*if(chatListener!=null)
            {
                chatListener.onMessageReceived(chatData);
            }*/

            Log.d(TAG, "handleChatNotification: Constants.IS_CHATTING_RESUMED:" + Constants.IS_CHATTING_RESUMED);
            if (!Constants.IS_CHATTING_RESUMED) {
                Intent resultIntent = new Intent(getApplicationContext(), ChattingActivity.class);//MainActivity
                stackBuilder.addParentStack(ChattingActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                resultIntent.putExtra("BID", notificationPojo.getBid());
                resultIntent.putExtra("PROIMAGE", notificationPojo.getProfilePic());
                resultIntent.putExtra("PROID", notificationPojo.getFromID());
                resultIntent.putExtra("PRONAME", notificationPojo.getName());
                manager.setChatBookingID(notificationPojo.getBid());
                manager.setChatProId(notificationPojo.getFromID());
                manager.setProName(notificationPojo.getName());
                manager.setChatCount(notificationPojo.getBid(), manager.getChatCount(notificationPojo.getBid()) + 1);

          /*  if(!VariableConstant.isChattingToSave)
            {

                db.addNewChat(message,notificationPojo.getTargetId(),notificationPojo.getFromID()
                        ,notificationPojo.getTimestamp(),notificationPojo.getType(),2,notificationPojo.getBid());
            }*/


                // check for image attachment
                if (message.contains("https://s3.amazonaws.com"))
                    showNotificationMessageWithBigImage(getApplicationContext(), title, "LiveM", timestamp, resultIntent, message, true);
                else
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, true);

            }
        }


    }

    private void handleDataMessage(int action, String picUrl, long bid, NotificationPojo notificationPojo) {

       /* if(manager.getBookingStatus(bid)>=action){
            return;
        }*/
       /* if(notificationPojo!=null)
        {
            if(manager.getBookingStatus(notificationPojo.getBookingId())<action){
                manager.setBookingStatus(notificationPojo.getBookingId(),action);
            }
        }*/

        this.action = action;
        this.picUrl = picUrl;
        this.bid = bid;
        timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US).format(new Date());


        Log.e(TAG, "action " + this.action);
        Log.e(TAG, "message: " + message);
        Log.e(TAG, "bid: " + this.bid);
        Log.e(TAG, "timestamp: " + timestamp);


        if (action == 3 || action == 6 || action == 7 || action == 8 || action == 9 || action == 17) {
            assert notificationPojo != null;
            Log.d(TAG, "handleDataMessage: " + action + " bookingTypeNotification " + notificationPojo.getBookingType());
            if (action == 6) {
                //Constants.custLatLng = new LatLng(notificationPojo.get);
            }
            if (!Constants.isJobDetailsOpen) {
                Intent resultIntent;
                if (action == 3 && notificationPojo.getBookingType() == 3) {
                    Constants.isConfirmBook = true;
                    Log.d(TAG, "handleDataMessageInIf: " + action);

                    resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    stackBuilder.addParentStack(MainActivity.class);
                } else if (action == 9 && notificationPojo.getCallType() == 3) {
                    resultIntent = new Intent(getApplicationContext(), InvoiceActivity.class);
                    stackBuilder.addParentStack(InvoiceActivity.class);

                } else {
                    Log.d(TAG, "handleDataMessageInElse: " + action);

                    resultIntent = new Intent(getApplicationContext(), JobDetailsActivity.class);
                    stackBuilder.addParentStack(JobDetailsActivity.class);
                }
                stackBuilder.addNextIntent(resultIntent);
                resultIntent.putExtra("BID", this.bid);
                resultIntent.putExtra("STATUS", this.action);
                resultIntent.putExtra("BookingModel", bookingModel);
                resultIntent.putExtra("CallType", callType);
                // VariableConstant.isSplashCalled = true;
                Log.e(TAG, "initializehandleDataMessage: " + bid + " BID " + this.bid + " action " + this.action);
                // check for image attachment
                if (TextUtils.isEmpty(picUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, true);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, picUrl, true);
                }
            }

        } else if (action == 10) {
            Intent resultIntent = new Intent(getApplicationContext(), RateYourBooking.class);
            stackBuilder.addParentStack(RateYourBooking.class);
            stackBuilder.addNextIntent(resultIntent);
            resultIntent.putExtra("BID", this.bid);
            // VariableConstant.isSplashCalled = true;
            Log.e(TAG, "initializehandleDataMessage: " + bid + " BID " + this.bid + " action " + this.action);
            // check for image attachment
            if (TextUtils.isEmpty(picUrl)) {
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, true);
            } else {
                // image is present, show notification with image
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, picUrl, true);
            }
        } else {
            //Added April 25 2018
            if (Constants.isJobDetailsOpen) {
                return;
            }
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            resultIntent.putExtra("BID", this.bid);
            resultIntent.putExtra("STATUS", this.action);
            //    VariableConstant.isSplashCalled = true;
            Log.e(TAG, "initializehandleDataMessage: " + bid + " BID " + this.bid + " action " + this.action);
            // check for image attachment
            if (TextUtils.isEmpty(picUrl)) {
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, true);
            } else {
                // image is present, show notification with image
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, picUrl, true);
            }

        }


        if (action == 3 && notificationPojo != null && (notificationPojo.getBookingType() == 3 || notificationPojo.getBookingType() == 2)) {
            int eventId = checkAndAddEvent(MyFirebaseMessagingService.this, notificationPojo.getBookingId()
                    , notificationPojo.getBookingRequestedFor(), manager);
            Log.d(TAG, "handleDataMessageEvent: " + eventId + " event " + manager.getBookingStatus(notificationPojo.getBid()));


            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    NotificationHandler notificationHandler = new NotificationHandler();
                    if (eventId != 0)
                        notificationHandler.addReminderEventId(eventId, notificationPojo.getBookingId(), manager);
                }
            });


        }
        //}
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent, boolean isChatting) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, isChatting, stackBuilder);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl, boolean isChatting) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl, isChatting, stackBuilder);
    }

    //    /**
//     * Create and show a simple notification containing the received FCM message.
//     *
//     * @param messageBody FCM message body received.
//     */
    private void sendNotification(String messageBody) {
        Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher_round);
        int numMessages = 0;

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setSummaryText("" + getResources().getString(R.string.livemAppLink));
        inboxStyle.setBigContentTitle(messageBody);
        String[] events = new String[6];
        stackBuilder.addParentStack(ZendeskHelpIndex.class);
        // Moves events into the expanded layout
        // Moves events into the expanded layout
        for (int i = 0; i < events.length; i++) {

            inboxStyle.addLine(events[i]);
        }
        // mBuilder.setStyle(inboxStyle);

        Intent intent = new Intent(this, ZendeskHelpIndex.class);
        stackBuilder.addNextIntent(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        /*PendingIntent pendingIntent = PendingIntent.getActivity(this, 0  , intent,
                PendingIntent.FLAG_ONE_SHOT);*/

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_logo)//splash_live_m_logo
                .setLargeIcon(icon1)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(inboxStyle)
                .setNumber(++numMessages)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notificationBuilder.build());
    }

    public void genralmethod() {
        Intent pushNotification = new Intent(getApplicationContext(), NotificationHandler.class);

        pushNotification.putExtra("message", message);
        pushNotification.putExtra("statcode", this.action);
        pushNotification.putExtra("bid", this.bid);
        pushNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(pushNotification);
        notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();
    }

    private class callApi extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
    }
}
