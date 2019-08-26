package com.localgenie.videocalling;

import android.app.Activity;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;
import com.localgenie.networking.ChatApiService;

/**
 * Created by Ali on 10/29/2018.
 */
public interface IncomingCallPresenterImpl
{
    interface IncomingCallPresenters extends BasePresenter
    {

        void ansDecline(String call_id, int i);

        void getCallDetails();

        void setWindow(Activity mActivity);

        void checkIsCallerStillWaiting(String callId, ChatApiService chatApiSzervice, IncomingCallScreen incomingCallScreen);

        void dispose();

        void endCall(String call_id, String request, ChatApiService chatApiService);

        void answerCall(String call_id,ChatApiService chatApiService);
    }
    interface IncomingCallView extends BaseView
    {

        void onSuccessAns();

        void onSuccessDec();
    }
}
