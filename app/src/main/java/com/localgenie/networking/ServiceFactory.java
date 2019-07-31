package com.localgenie.networking;

import com.localgenie.utilities.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Pramod on 15/12/17.
 */

public class ServiceFactory
{
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(80, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();
    /**
     * Creates a retrofit service from an arbitrary class (clazz)
     * @param clazz Java interface of the retrofit service
     * @return retrofit service with defined endpoint
     */
    public static <T> T createRetrofitService(final Class<T> clazz)
    {

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(clazz);
    }

    public static <T> T createRetrofitChatService(final Class<T> clazz)
    {

        return new Retrofit.Builder()
                .baseUrl(Constants.CHAT_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(clazz);
    }

    public static <T> T createRetrofitChatUploadImage(final Class<T> clazz)
    {

        return new Retrofit.Builder()
                .baseUrl(Constants.CHAT_DATA_UPLOAD)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(clazz);
    }
}
