package com.localgenie.zendesk.zendeskHelpIndex;

import com.localgenie.Dagger2.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * <h>ZendeskModule</h>
 * Created by Ali on 2/26/2018.
 */
@Module
public interface ZendeskModule
{
    @ActivityScoped
    @Binds
    ZendeskHelpIndexContract.Presenter providePresenter(ZendeskHelpIndexImpl zendeskHelpIndex);

    @ActivityScoped
    @Binds
    ZendeskHelpIndexContract.ZendeskView provideView(ZendeskHelpIndex zendeskHelpIndex);
}
