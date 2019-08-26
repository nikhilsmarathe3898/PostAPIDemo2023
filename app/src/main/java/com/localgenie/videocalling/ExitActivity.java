package com.localgenie.videocalling;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.localgenie.utilities.Constants;

import dagger.android.support.DaggerAppCompatActivity;

public class ExitActivity extends DaggerAppCompatActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(android.os.Build.VERSION.SDK_INT >= 21)
        {
            Constants.isFromNotification = false;
            finishAndRemoveTask();
        }
        else
        {
            Constants.isFromNotification = false;
            finish();
        }
    }

    public static void exitApplicationAndRemoveFromRecent(Context context)
    {
        Constants.isFromNotification = false;
        Intent intent = new Intent(context, ExitActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        context.startActivity(intent);
    }
}
