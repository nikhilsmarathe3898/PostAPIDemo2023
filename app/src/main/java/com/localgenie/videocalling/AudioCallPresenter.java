package com.localgenie.videocalling;

import android.util.Log;

import com.localgenie.networking.ChatApiService;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Ali on 3/20/2019.
 */
public class AudioCallPresenter implements VideoCallContract.Presenter {

    private String TAG = VideoCallPresenter.class.getSimpleName();

    @Inject
    VideoCallContract.View view;
    @Inject
    LSPServices apiServices;
    @Inject
    SessionManagerImpl sessionManager;
    private CompositeDisposable compositeDisposable;

    @Inject
    AudioCallPresenter(){
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void initCall() {

    }

    @Override
    public void endCall(String callID, String callFrom, ChatApiService chatApiService) {
        chatApiService.endCall(sessionManager.getCallToken(), Constants.selLang,callID,callFrom)
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
                            view.onRejectSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void dropView() {
        this.view = null;
    }

    @Override
    public void dispose() {
        compositeDisposable.clear();
    }
}
