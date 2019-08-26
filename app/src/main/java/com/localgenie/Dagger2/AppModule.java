package com.localgenie.Dagger2;

import android.app.Application;
import android.content.Context;

import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.SessionManagerImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * Created by ${3Embed} on 4/11/17.
 */
@Module
public interface AppModule {
    //expose Application as an injectable context
    @Binds
    @Singleton
    Context bindContext(Application application);

    @Binds
    @Singleton
    SessionManagerImpl preferenceHelperDataSource(SessionManager preferencesHelper);


}
