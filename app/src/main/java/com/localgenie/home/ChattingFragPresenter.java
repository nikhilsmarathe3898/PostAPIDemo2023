package com.localgenie.home;

import com.pojo.BookingChatPojo;

/**
 * Created by Ali on 5/28/2018.
 */
public interface ChattingFragPresenter
{
    interface Presenter extends BasePresenter
    {
        void onChattingActiveNonActive();
    }
    interface ViewPresent extends BaseView
    {
       void onSuccess(BookingChatPojo.BookingChatData data);

        void onErrorNotConnected(String message);
    }
}
