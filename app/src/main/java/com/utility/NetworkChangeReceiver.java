package com.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.localgenie.R;
import com.localgenie.SplashScreen.SplashActivity;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.Utility;



public class NetworkChangeReceiver extends BroadcastReceiver
{
	public static MyNetworkChangeListener myNetworkChangeListner=new MyNetworkChangeListener() {
		@Override
		public void onNetworkStateChanges(boolean nwStatus) {

		}
	};
	private Notification notification;

	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		Log.d(""," onRecieve  "+intent.getAction());
		String status = NetworkUtil.getConnectivityStatusString(context);
		
		String[] networkStatus = status.split(",");
		SessionManager sessionManager = new SessionManager(context);
		//Toast.makeText(context, networkStatus[0], Toast.LENGTH_LONG).show();
		
		Intent homeIntent=new Intent("com.localserviceprovider.internetStatus");
		homeIntent.putExtra("STATUS", networkStatus[1]);
		context.sendBroadcast(homeIntent);
		Log.d("","Network Status"+status);
		if(myNetworkChangeListner!=null)
		{
			myNetworkChangeListner.onNetworkStateChanges("1".equals(networkStatus[1].trim()));
		}

		if(!"1".equals(networkStatus[1]))
		{
			sendNotification(context,networkStatus[1]);
		}

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm !=null)
		{
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

			Log.d("Ali", "onReceive: "+isConnected);

			if(!isConnected && !Constants.IS_NETWORK_ERROR_SHOWED && !Utility.isAppIsInBackground(context))
			{
				Intent intentNetworkError = new Intent(context,NetworkErrorActivity.class);
				intentNetworkError.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intentNetworkError);
			}

		}
	}

	public void setMyNetworkChangeListner(MyNetworkChangeListener myNetworkChangeListner)
	{
		NetworkChangeReceiver.myNetworkChangeListner =myNetworkChangeListner;
	}
	private void sendNotification(Context context, String staus)
	{
		int icon = R.mipmap.ic_launcher_round;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		String title = context.getString(R.string.app_name);
		//SharedPrefs sessionManager = new SharedPrefs(context);
		Intent notificationIntent;
		notificationIntent = new Intent(context, SplashActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

		Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(),
				R.mipmap.ic_launcher_round);

		//Assign inbox style notification
		NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
		bigText.bigText("Alert");
		bigText.setBigContentTitle(title);
		//bigText.setSummaryText("Alert");

		//build notification
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
						.setSmallIcon(R.mipmap.ic_launcher_round)
						.setContentIntent(intent)
						.setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
						.setPriority(NotificationCompat.PRIORITY_MAX) //must give priority to High, Max which will considered as heads-up notification)
						.setContentTitle(title)
						.setAutoCancel(true)
						.setContentText("")
						.setLargeIcon(icon1)
						.setStyle(bigText);


		if ("1".equals(staus))
		{
			mBuilder.setContentText("Internet connected.");
			/*notification = new Notification(icon, "Internet connected", when);
			notification.setLatestEventInfo(context, title, "Internet connected", intent);*/
		}
		else{
			mBuilder.setContentText("No network connection found.");
			/*notification = new Notification(icon, "No network connection found.", when);
			notification.setLatestEventInfo(context, title, "No network connection found.", intent);*/
		}


		// Gets an instance of the NotificationManager service
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


	}

}
