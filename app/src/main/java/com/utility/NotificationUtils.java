package com.utility;

/**
 * Created by embed on 29/11/16.
 *
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.localgenie.R;
import com.localgenie.jobDetailsStatus.JobDetailsActivity;
import com.localgenie.utilities.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//  import android.content.ContentResolver;

/**
 * Created by Ravi on 31/03/15.
 *
 */
public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;
    private NotificationChannel mChannel,notificationChannel;
    NotificationManager notificationManager;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
         notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

    }



    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent,boolean isChatting, TaskStackBuilder stackBuilder) {
        showNotificationMessage(title, message, timeStamp, intent, null,isChatting,stackBuilder);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl, boolean isChatting, TaskStackBuilder stackBuilder) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;
        // notification icon
        final int icon = R.mipmap.ic_launcher_round;
        final NotificationCompat.Builder mBuilder;

        Log.d(TAG, "showNotificationMessage: "+mContext+" notificationManager "+notificationManager);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (notificationChannel == null) {


                notificationChannel = new NotificationChannel
                        (mContext.getResources().getString(R.string.app_name)+"1id", title+"", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription(message);
                notificationChannel.enableVibration(true);
              //  notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            mBuilder = new NotificationCompat.Builder(mContext,notificationChannel.getId());
        }else
            mBuilder = new NotificationCompat.Builder(mContext);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent;
        if(isChatting)
        {
            resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else
        {
            resultPendingIntent =
                    PendingIntent.getActivity(
                            mContext,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );
        }




       /* final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");*/

        final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Ringtone r = RingtoneManager.getRingtone(mContext, notification);

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound,notificationManager);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound,notificationManager);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound,notificationManager);
            // playNotificationSound();
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound, NotificationManager notificationManager) {

        Notification notification;
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);
        inboxStyle.setBigContentTitle(mContext.getResources().getString(R.string.app_name));
        inboxStyle.setSummaryText(mContext.getResources().getString(R.string.livemAppLink));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            notification = mBuilder.setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_notification_logo) // required
                    .setContentText(message)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .setStyle(inboxStyle)
                    .setChannelId(notificationChannel.getId())
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setBadgeIconType(R.mipmap.ic_launcher_round)
                    .setSound(alarmSound)
                    .build();
            notification.flags=Notification.FLAG_AUTO_CANCEL;
            // Notification notification = builder.build();
            notificationManager.notify(Config.NOTIFICATION_ID, notification);
        }else
        {
            notification = mBuilder.setTicker(title).setWhen(0)
                    .setSmallIcon(R.drawable.ic_notification_logo)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    //.setWhen(getTimeMilliSec(timeStamp))
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .build();
            // NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Config.NOTIFICATION_ID, notification);
        }

    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound, NotificationManager notificationManager) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            notification = mBuilder.setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(bigPictureStyle)
                    .setDefaults(Notification.DEFAULT_ALL)
                    //.setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.ic_notification_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .setBadgeIconType(R.mipmap.ic_launcher_round)
                    .setChannelId(notificationChannel.getId())
                    //.setPriority(NotificationCompat.PRIORITY_HIGH)
                   // .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .build();
            notification.flags=Notification.FLAG_AUTO_CANCEL;
            // Notification notification = builder.build();
            notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);
        }else
        {
            notification = mBuilder.setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(bigPictureStyle)
                    //.setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.ic_notification_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .build();

            //  NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);
        }

        /***********************************/

/*
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        NotificationCompat.Builder builder=mBuilder.setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                // .setStyle(inboxStyle)

                .setStyle(bigPictureStyle)
                //.setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.notary_icon)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER);

        Notification notification;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            builder.setChannelId(mChannel.getId());
        }
        notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);*/

        /////

    }


    public void showJustNotification(String livestatus, String title, String message, Intent intent) {
        final int icon = R.mipmap.ic_launcher_round;
      //  NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent contentIntent;
        Notification notification;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("Lsp0", title, importance);
                mChannel.setDescription(message);
                mChannel.enableVibration(true);
             //   mChannel.setImportance(importance);
                notificationManager.createNotificationChannel(mChannel);
            }
        }
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);
        inboxStyle.setBigContentTitle(mContext.getResources().getString(R.string.app_name));
        inboxStyle.setSummaryText(mContext.getResources().getString(R.string.livemAppLink));
        TaskStackBuilder taskStackBuilder=TaskStackBuilder.create(mContext);
       // taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addParentStack(JobDetailsActivity.class);
        taskStackBuilder.addNextIntent(intent);

        if(!Constants.isJobDetailsOpen){

            contentIntent = PendingIntent.getActivity(mContext,
                    0,
                    intent, // add this
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            contentIntent = PendingIntent.getActivity(mContext,
                    0,
                    new Intent(), // add this
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,mChannel.getId());
            notification = mBuilder.setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_notification_logo) // required
                    .setContentText(message)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setStyle(inboxStyle)
                    .setChannelId(mChannel.getId())
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setBadgeIconType(R.mipmap.ic_launcher_round)
                    .setContentIntent(contentIntent)
                    .setSound(alarmSound)
                    .build();
            notification.flags=Notification.FLAG_AUTO_CANCEL;
           // Notification notification = builder.build();
            notificationManager.notify(Config.NOTIFICATION_ID, notification);
        }else
        {
            //  Notification notification;
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,"Livebokin");
            notification = mBuilder.setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setContentIntent(contentIntent )
                    //.setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.ic_notification_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentText(message)
                    //.setVisibility(NotificationCompat.)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .build();
            notification.flags=Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(Config.NOTIFICATION_ID, notification);
        }



    }


    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            /*Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();*/

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
