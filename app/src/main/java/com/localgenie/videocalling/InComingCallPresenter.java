package com.localgenie.videocalling;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.localgenie.networking.ChatApiService;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Ali on 10/29/2018.
 */
public class InComingCallPresenter implements IncomingCallPresenterImpl.IncomingCallPresenters
{
    private String TAG = InComingCallPresenter.class.getSimpleName();

    @Inject
    SessionManagerImpl manager;
    @Inject
    Gson gson;
    @Inject
    IncomingCallPresenterImpl.IncomingCallView view;

    private CompositeDisposable compositeDisposable;

    @Inject
    public InComingCallPresenter() {
        compositeDisposable = new CompositeDisposable();
    }


    @Override
    public void ansDecline(String call_id, int i) {

//        Observable<Response<ResponseBody>> observable = apiService.telCallAnsDec(manager.getAUTH(), Constants.selLang
//        ,call_id,i);
//
//        observable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
//                    @Override
//                    public void onNext(Response<ResponseBody> responseBodyResponse) {
//
//                        int code = responseBodyResponse.code();
//                        String response;
//                        try
//                        {
//
//
//
//                        if(code == 200)
//                        {
//                            response = responseBodyResponse.body().string();
//                            if(i ==2)
//                                view.onSuccessAns();
//                            else
//                                view.onSuccessDec();
//                        }else
//                        {
//                            response = responseBodyResponse.errorBody().string();
//
//                            view.onError(new JSONObject(response).getString("message"));
//                        }
//                        }catch (Exception e)
//                        {
//
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    @Override
    public void getCallDetails() {

//        Observable<Response<ResponseBody>> observable = apiService.getCallDetails(manager.getAUTH(),Constants.selLang);
//
//        observable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
//                    @Override
//                    public void onNext(Response<ResponseBody> responseBodyResponse) {
//
//                        int code = responseBodyResponse.code();
//
//                        try
//                        {
//
//
//                        switch (code)
//                        {
//                            case 200:
//
//                                break;
//                                default:
//                                    view.onError("Call Over");
//                                    break;
//                        }
//                        }catch (Exception e)
//                        {
//
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    @Override
    public void setWindow(Activity  mActivity) {


        Window window = mActivity.getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.getDecorView()
                    .setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                            | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            window.getDecorView()
                    .setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    );

        }


        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public void checkIsCallerStillWaiting(String callID, ChatApiService chatApiService) {
        chatApiService.checkIsAvailable(manager.getCallToken(), Constants.selLang,callID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ResponseBody> value) {
                        try {
                            Log.d(TAG, "onNext: checkCallerAvailableCode: "+value.code());
                            if(value.code() == 200){
                                String response = value.body().string();
                                Log.d(TAG, "onNext: checkCallerAvailable: "+response);
                                //ok let user answer
                            }
                            else{
                                //caller ended the call
                                if(view != null)
                                    view.onSuccessDec();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(Throwable e) {
                        if(view != null)
                            view.onSuccessDec();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void dispose() {
        compositeDisposable.clear();
    }


    @Override
    public void endCall(String callID, String callFrom,ChatApiService chatApiService) {
        chatApiService.endCall(manager.getCallToken(),Constants.selLang,callID,callFrom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ResponseBody> value) {
                        try {
                            String response = value.body().string();
                            Log.d(TAG, "onNext: endCallResponse: "+response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(view != null)
                            view.onSuccessDec();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(view != null)
                            view.onSuccessDec();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void answerCall(String call_id,ChatApiService chatApiService) {
        chatApiService.callAnswer(manager.getCallToken(),Constants.selLang,call_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ResponseBody> value) {
                        Log.d(TAG, "onNext: "+value.code());
                        if(value.code() == 200){
                            if(view != null)
                                view.onSuccessAns();
                        }
                        else{
                            if(view != null)
                                view.onSuccessDec();
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        if(view != null)
                            view.onSuccessDec();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }
}
