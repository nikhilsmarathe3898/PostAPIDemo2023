package com.localgenie.changepassword;

import android.util.Log;

import com.localgenie.model.ServerResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;

import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * <h>ChangePwdPresenterImpl</h>
 * Created by Pramod on 19/12/17.
 */

public class ChangePwdPresenterImpl implements ChangePwdPresenter {

    private ChangePwdView changePwdView;

    @Inject
    LSPServices lspServices;

    @Inject
    CompositeDisposable compositeDisposable;

    CompositeDisposable verDisposable;
    //private ChangePwdModel changePwdModel;


    @Inject
    ChangePwdPresenterImpl(ChangePwdView changePwdView) {
        this.changePwdView = changePwdView;
        this.verDisposable = new CompositeDisposable();
        //this.changePwdModel = new ChangePwdModel();
    }


    @Override
    public void profChangePassword(String auth, String old_password, String new_password) {
        Observable<Response<ServerResponse>> response = lspServices.profChangePwd(auth,Constants.selLang,old_password,new_password);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ServerResponse>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        verDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ServerResponse> value)
                    {
                        Log.e("ChangePWD","code :: "+value.code() + " msg "+value.message());
                        if (200 == value.code()) {
                            if (value.body()!=null) {
                                //Log.e("ChangePWD","Success msg :: "+value.body().getGuestLoginMessage());
                                changePwdView.setError(value.body().getMessage());
                                changePwdView.navToProfile();
                            } /*else {
                                changePwdView.setError(value.message());
                            }*/
                        } else {
                            //changePwdView.hideProgress();
                            try {
                                if (value.errorBody()!=null) {
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    changePwdView.setError(errJson.getString("message"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    @Override
    public void changePassword(String sid,String new_password) {
        //LSPServices service = ServiceFactory.createRetrofitService(LSPServices.class);
        Observable<Response<ServerResponse>> response = lspServices.changePassword(Constants.selLang,new_password,sid,1);

        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ServerResponse>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ServerResponse> value)
                    {
                        Log.e("ChangePWD","code :: "+value.code() + " msg "+value.message());
                        if (200 == value.code()) {
                            if (value.body()!=null) {
                                //Log.e("ChangePWD","Success msg :: "+value.body().getGuestLoginMessage());
                                changePwdView.setError(value.body().getMessage());
                                changePwdView.navtoLogin();
                            } /*else {
                                changePwdView.setError(value.message());
                            }*/
                        } else {
                            //changePwdView.hideProgress();
                            try {
                                if (value.errorBody()!=null) {
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    changePwdView.setError(errJson.getString("message"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
}
