package com.localgenie.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

/**
 * @author Pramod
 * @since  23/12/17.
 */

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    Log.d("TAG", "onReceive: "+message);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;
            }
        }
    }

   /* final SmsManager sms = SmsManager.getDefault();
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
                    Log.d("TAG",message);


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

    protected abstract void onSmsReceived(String s);*/
}
