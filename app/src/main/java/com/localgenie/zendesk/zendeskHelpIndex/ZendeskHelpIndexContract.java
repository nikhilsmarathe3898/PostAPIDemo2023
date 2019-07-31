package com.localgenie.zendesk.zendeskHelpIndex;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;
import com.localgenie.zendesk.zendeskpojo.OpenClose;

import java.util.ArrayList;

/**
 * <h>ZendeskHelpIndexContract</h>
 * Created by Ali on 2/26/2018.
 */

public interface ZendeskHelpIndexContract
{
    interface Presenter extends BasePresenter
    {
        void onToGetZendeskTicket();
    }
    interface  ZendeskView extends BaseView
    {
        void onGetTicketSuccess();

        void onEmptyTicket();

        void onTicketStatus(OpenClose openClose, int openCloseSize, boolean isOpenClose);

        void onNotifyData(ArrayList<OpenClose> alOpenClose);
        void onRefreshing(boolean isRefreshing);
    }
}
