package com.localgenie.youraddress;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.model.ServerResponse;
import com.localgenie.model.youraddress.YourAddrData;
import com.localgenie.model.youraddress.YourAddressResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.pojo.ErrorHandel;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;
import com.utility.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author Pramod
 * @since  19-01-2018.
 */

public class YourAddressPresenterImpl implements YourAddressPresenter {

    @Inject
    LSPServices lspServices;

    @Inject
    AlertProgress alertProgress;

    private Context mContext;

    private YourAddressView yourAddressView;

    @Inject
    Gson gson;

    @Inject
    SessionManagerImpl manager;


    @Inject
    YourAddressPresenterImpl(YourAddressView yourAddressView) {
        this.yourAddressView = yourAddressView;
    }

    @Override
    public void getAddress(final String auth, Context yourAddressActivity) {
       /* if (yourAddressView!=null) {
            yourAddressView.showProgress();
        }*/
        mContext = yourAddressActivity;
        Observable<Response<ResponseBody>> response = lspServices.getAddress(auth, Constants.selLang);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {


                    @Override
                    public void onNext(Response<ResponseBody> value)
                    {

                        int code  = value.code();
                        String response;
                        try {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:

                                     response =  value.body().string();
                                    Log.d("TAG", "onNextGEYTADDRES: "+response);
                                    YourAddressResponse yourAddrResponse = gson.fromJson(response,YourAddressResponse.class);
                                    if (yourAddrResponse!=null) {
                                        ArrayList<YourAddrData> yourAddrDataList = yourAddrResponse.getData();
                                        if (yourAddrDataList!=null) {
                                            if (yourAddrDataList.size()>0) {
                                                yourAddressView.addItems(yourAddrDataList);
                                                yourAddressView.hideProgress();
                                            } else {
                                                yourAddressView.setNoAddressAvailable();
                                                yourAddressView.hideProgress();
                                            }
                                        }
                                    }
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    yourAddressView.hideProgress();
                                    yourAddressView.onLogout(errJson.getString("message"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = value.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response, ErrorHandel.class);
                                    RefreshToken.onRefreshToken(errorHandel.getData(),lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            manager.setAUTH(newToken);
                                            getAddress(newToken, yourAddressActivity);

                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            assert yourAddressView != null;
                                            yourAddressView.hideProgress();
                                            yourAddressView.onLogout(msg);
                                        }
                                    });
                                    break;
                                default:
                                    try {
                                        if (value.errorBody().string()!=null) {
                                            JSONObject errJsonD = new JSONObject(value.errorBody().string());
                                            yourAddressView.hideProgress();
                                            yourAddressView.setError(errJsonD.getString("message"));
                                            //yourAddressView.hideProgress();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //yourAddressView.hideProgress();
                                    }
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        //yourAddressView.hideProgress();
                    }
                });

    }

    @Override
    public void deleteAddress(final String auth, final String cardId, final YourAddrData rowItem, int adapterPosition)
    {

        alertProgress.alertPositiveNegativeOnclick(mContext, mContext.getResources().getString(R.string.areYouSureYouWantOTDelete), mContext.getResources().getString(R.string.system_error),mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.cancel),false , (DialogInterfaceListner) isClicked -> {
            if(isClicked)
            {
                Observable<Response<ServerResponse>> response = lspServices.deleteAddress(auth,Constants.selLang,cardId);
                response.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<Response<ServerResponse>>() {


                            @Override
                            public void onNext(Response<ServerResponse> value)
                            {
                                Log.e("DelAddr","Delete Server Req URL :: "+value.raw().request().url()+" code : "+value.code());
                                ServerResponse serverResponse;
                                try {
                                switch (value.code())
                                {
                                    case Constants.SUCCESS_RESPONSE:
                                        serverResponse = value.body();
                                        if (serverResponse!=null) {
                                            String message = serverResponse.getMessage();
                                            Log.e("DEL_ADDDR",message);
                                            yourAddressView.refreshItems(rowItem,adapterPosition);
                                            yourAddressView.hideProgress();
                                        }
                                        break;
                                    case Constants.SESSION_LOGOUT:
                                        JSONObject errJson = new JSONObject(value.errorBody().string());
                                        yourAddressView.hideProgress();
                                        yourAddressView.onLogout(errJson.getString("message"));
                                        break;
                                    case Constants.SESSION_EXPIRED:
                                        JSONObject errJsonExp = new JSONObject(value.errorBody().string());
                                        RefreshToken.onRefreshToken(errJsonExp.getString("data"),lspServices, new RefreshToken.RefreshTokenImple() {
                                            @Override
                                            public void onSuccessRefreshToken(String newToken) {

                                                manager.setAUTH(newToken);
                                                deleteAddress(newToken, cardId, rowItem, adapterPosition);

                                            }

                                            @Override
                                            public void onFailureRefreshToken() {

                                            }

                                            @Override
                                            public void sessionExpired(String msg) {
                                                assert yourAddressView != null;
                                                yourAddressView.hideProgress();
                                                yourAddressView.onLogout(msg);
                                            }
                                        });
                                        break;
                                    default:
                                        JSONObject errJsonD = new JSONObject(value.errorBody().string());
                                        yourAddressView.hideProgress();
                                        yourAddressView.onError(errJsonD.getString("message"));
                                        break;
                                }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public void onError(Throwable e)
                            {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });


    }

    @Override
    public void onItemClicked(int adapterPosition) {
        yourAddressView.onAddressSelected(adapterPosition);
    }
}
