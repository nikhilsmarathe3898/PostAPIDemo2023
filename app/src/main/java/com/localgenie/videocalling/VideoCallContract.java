package com.localgenie.videocalling;

import com.localgenie.networking.ChatApiService;

public interface VideoCallContract {
    interface Presenter{
        void initCall();
        void endCall(String callID, String callFrom, ChatApiService chatApiService);
        void dropView();
        void dispose();
    }

    interface View{
        void onAnswerSuccess();
        void onRejectSuccess();
    }
}
