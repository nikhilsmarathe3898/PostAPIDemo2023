package com.localgenie.lspapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * @author Pramod
 * @since  23/12/17.
 */

public abstract class MySMSBroadcastReceiver extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();
    final String SENDER = "IM-bytwoo";

    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");


                for (Object aPdusObj : pdusObj) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);

                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    Log.d("log24",message);


                    if (message != null) {
                        String splitted[] = message.split(" ");
                        message = splitted[splitted.length - 1];
                        //  if (senderNum.equals(SENDER)) {
                        onSmsReceived(message);
                        //}
                        abortBroadcast();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    protected abstract void onSmsReceived(String s);
}
