package com.localgenie.zendesk.zendeskTicketDetails;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <h>HelpTicketDetailsModule</h>
 * Created by Ali on 2/26/2018.
 */
@Module
public interface HelpTicketDetailsModule
{
    @ActivityScoped
    @Binds
    HelpIndexContract.presenter providePresenter(HelpIndexContractImpl helpIndexContract);

    @ActivityScoped
    @Binds
    HelpIndexContract.HelpView provideView(HelpIndexTicketDetails helpIndexTicketDetails);
}
