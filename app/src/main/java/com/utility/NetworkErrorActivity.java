package com.utility;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.localgenie.R;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;

/**
 * <h>NetworkErrorActivity</h>
 */


public class NetworkErrorActivity extends AppCompatActivity {

    private Handler handlerNetworkCheck;
    private Runnable runnableNetworkCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error);
        overridePendingTransition(R.anim.fade_open, R.anim.fade_close);

        Constants.IS_NETWORK_ERROR_SHOWED = true;
        handlerNetworkCheck = new Handler();
        runnableNetworkCheck = new Runnable() {
            @Override
            public void run() {
                if(!Utility.isNetworkAvailable(NetworkErrorActivity.this))
                {
                    handlerNetworkCheck.postDelayed(this,2000);
                }
                else
                {
                    finish();
                }
            }
        };
        handlerNetworkCheck.postDelayed(runnableNetworkCheck,3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerNetworkCheck.removeCallbacks(runnableNetworkCheck);
        Constants.IS_NETWORK_ERROR_SHOWED = false;
    }

    @Override
    public void onBackPressed() {

    }
}
