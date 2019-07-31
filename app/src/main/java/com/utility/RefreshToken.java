package com.utility;

import android.util.Log;

import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by murashid on 16-Oct-17.
 * <h1>RefreshToken</h1>
 * wrapper class for refreshing token
 */

public class RefreshToken {
    private static String TAG = "RefreshToken";

    /**
     * method for calling api for  refresh token
     * @param token date token
     * @param imple RefreshTokenImple success , failure callback
     */
    private static RefreshTokenImple impl;
    private static String tokens ="";
    private static LSPServices lspService;

    public static void onRefreshToken(final String token,LSPServices lspServices,final RefreshTokenImple imple)
    {
        impl = imple;
        tokens = token;
        lspService = lspServices;
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.refreshToken();
    }

    private void refreshToken()
    {

        Observable<Response<ResponseBody>> observable = lspService.getAccessToken(tokens,Constants.selLang);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {
                        try {
                            int responseCode = responseBodyResponse.code();

                            JSONObject jsonObject;
                            switch (responseCode)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    String responseBodes = responseBodyResponse.body().string();
                                     jsonObject = new JSONObject(responseBodes);
                                    Log.d(TAG, "onNextaccessToken: "+responseBodes+" responseCode "+responseCode);

                                    impl.onSuccessRefreshToken(jsonObject.getString("data"));
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    String responseBode = responseBodyResponse.errorBody().string();
                                     jsonObject = new JSONObject(responseBode);
                                    impl.sessionExpired(jsonObject.getString("message"));
                                    break;
                                default:
                                    impl.onFailureRefreshToken();
                                    break;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * RefreshTokenImple callback for failure and succes refresh token
     */
    public interface RefreshTokenImple {
        void onSuccessRefreshToken(String newToken);
        void onFailureRefreshToken();
        void sessionExpired(String msg);
    }
}
