package com.localgenie.chatting;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;
//import com.localgenie.networking.ChatApiService;
import com.pojo.ChatData;

import java.util.ArrayList;

/**
 * Created by Ali on 4/19/2018.
 */
public interface ChattingPresenter
{
    public interface Presenter extends BasePresenter
    {
        void onHistoryApi(long bid, String proId, int pageIndex);

        void onPostMsg(int msgType, long msgId, String msg, String cId, long bid, String proId);

        void getIntentValue();

        void loadImage(String path, long bid);
    }
    public interface ViewChatting extends BaseView
    {

        void onMoreAvailable(boolean isMoreAvailable);

        void onChatHistoryResponse(ArrayList<ChatData> data);

        void onRefreshing(boolean b);

        void setIntentValue(long chatBookingID, String chatProId, String proName, String sid);

        void sendImageMessage(String image, int typeMsg, long msgid);
    }
}
