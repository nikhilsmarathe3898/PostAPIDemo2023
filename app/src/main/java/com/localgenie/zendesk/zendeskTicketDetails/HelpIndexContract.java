package com.localgenie.zendesk.zendeskTicketDetails;

import android.content.Context;
import android.widget.ImageView;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;
import com.localgenie.zendesk.zendeskpojo.ZendeskDataEvent;

import java.util.ArrayList;

/**
 * <h>HelpIndexContract</h>
 * Created by Ali on 2/26/2018.
 */

public interface HelpIndexContract
{
    interface presenter extends BasePresenter
    {

        void onPriorityImage(Context helpIndexTicketDetails, String priority, ImageView ivHelpCenterPriorityPre);

        void callApiToCommentOnTicket(String trim, int zenId);

        void callApiToCreateTicket(String trim, String subject, String priority);

        void callApiToGetTicketInfo(int zenId);
    }
    interface HelpView extends BaseView
    {

        void onTicketInfoSuccess(ArrayList<ZendeskDataEvent> events, String timeToSet, String subject, String priority, String type);

        void onZendeskTicketAdded(String response);
    }
}
