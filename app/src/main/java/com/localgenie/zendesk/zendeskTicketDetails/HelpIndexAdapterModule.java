package com.localgenie.zendesk.zendeskTicketDetails;

import com.localgenie.Dagger2.ActivityScoped;
import com.localgenie.zendesk.zendeskadapter.HelpIndexRecyclerAdapter;

import dagger.Module;
import dagger.Provides;

/**
 * <h>HelpIndexAdapterModule</h>
 * Created by Ali on 2/26/2018.
 */
@Module
public class HelpIndexAdapterModule
{
    @ActivityScoped
    @Provides
    HelpIndexRecyclerAdapter provideHelpAdapter()
    {
     return new  HelpIndexRecyclerAdapter();
    }
}
