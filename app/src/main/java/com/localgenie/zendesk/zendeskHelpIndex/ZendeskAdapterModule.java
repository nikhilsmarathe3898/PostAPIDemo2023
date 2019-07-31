package com.localgenie.zendesk.zendeskHelpIndex;

import com.localgenie.Dagger2.ActivityScoped;

import com.localgenie.zendesk.zendeskadapter.HelpIndexAdapter;

import dagger.Module;
import dagger.Provides;

/**
 * <h>ZendeskAdapterModule</h>
 * Created by Ali on 2/26/2018.
 */

@Module
public class ZendeskAdapterModule
{
    @ActivityScoped
    @Provides
    HelpIndexAdapter provideHelpIndexAdapter()
    {
        return  new HelpIndexAdapter();
    }
}
