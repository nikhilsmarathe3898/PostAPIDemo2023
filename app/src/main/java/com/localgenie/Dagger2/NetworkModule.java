package com.localgenie.Dagger2;

import android.app.Application;

import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Pramod
 * @since 15-12-2017
 */
@Module
public class NetworkModule {
    public static final String NAME_BASE_URL="NAME_BASE_URL";
    private static final long CACHE_SIZE = 10 * 1024 * 1024; //10 MB

    public static final String CONNECT_TIMEOUT = "60000";
    public static final String READ_TIMEOUT = "30000";
    public static final String WRITE_TIMEOUT = "15000";

    @Provides
    @Named(NAME_BASE_URL)
    String provideBaseUrl(){
        return Constants.BASE_URL;
    }

    @Provides
    @Singleton
    Converter.Factory provideGsonConverterFactory(){
        return GsonConverterFactory.create();
    }


    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        return new Cache(application.getCacheDir(), CACHE_SIZE);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(80, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false);
        return builder.build();
    }
    @Provides
    @Singleton
    Retrofit provideRetrofit(Converter.Factory converter,@Named(NAME_BASE_URL) String baseUrl, OkHttpClient okHttpClient){

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converter)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    LSPServices provideLSPService(Retrofit retrofit){
        return retrofit.create(LSPServices.class);
    }

}
