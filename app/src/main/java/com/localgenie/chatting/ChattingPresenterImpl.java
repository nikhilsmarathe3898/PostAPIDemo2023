package com.localgenie.chatting;


import android.util.Log;

import com.google.gson.Gson;
//import com.localgenie.networking.ChatApiService;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.pojo.ChatResponseHistory;
import com.pojo.ErrorHandel;
import com.utility.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Ali on 4/19/2018.
 */
public class ChattingPresenterImpl implements ChattingPresenter.Presenter
{


    @Inject ChattingPresenter.ViewChatting viewChatting;

    @Inject
    SessionManagerImpl manager;
    @Inject
    Gson gson;

    @Inject
    LSPServices lspServices;

    private CompositeDisposable disposable;

    @Inject
    public ChattingPresenterImpl()
    {
        disposable = new CompositeDisposable();
    }




    @Override
    public void attachView(Object view) {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void onHistoryApi(final long bid, String proId, final int pageIndex)
    {
        Observable<Response<ResponseBody>> observable = lspServices.getChatHistory(manager.getAUTH()
                , Constants.selLang,bid,proId,pageIndex);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>()
                {

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        disposable.clear();
                        disposable.dispose();
                        int code =  responseBodyResponse.code();
                        String response;

                        try {


                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:

                                    response = responseBodyResponse.body().string();
                                    Log.d("TAG", "onNextOnSuccess: "+response);
                                    ChatResponseHistory chatReponce = gson.fromJson(response,ChatResponseHistory.class);
                                    if(chatReponce.getData()!=null)
                                    {
                                        if(chatReponce.getData().size() == 0)
                                        {
                                            viewChatting.onMoreAvailable(false);
                                        }
                                        viewChatting.onChatHistoryResponse(chatReponce.getData());
                                    }

                                    viewChatting.onRefreshing(false);

                                    break;
                                case Constants.SESSION_LOGOUT:
                                    viewChatting.onLogout(new JSONObject(responseBodyResponse.errorBody().string()).getString("data"));
                                    break;
                                case Constants.SESSION_EXPIRED:

                                    RefreshToken.onRefreshToken(new JSONObject(responseBodyResponse.errorBody().string()).getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            onHistoryApi(bid, proId, pageIndex);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                        }
                                    });
                                    break;
                                default:
                                    response = responseBodyResponse.errorBody().string();
                                    ErrorHandel errorHandel = gson.fromJson(response,ErrorHandel.class);
                                    viewChatting.onError(errorHandel.getMessage());
                                    viewChatting.onMoreAvailable(false);
                                    viewChatting.onRefreshing(false);
                                    break;
                            }

                        }catch (IOException e)
                        {
                            e.printStackTrace();
                            viewChatting.onMoreAvailable(false);
                            viewChatting.onRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        disposable.clear();
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {

                        disposable.clear();
                        disposable.dispose();
                    }
                });
    }

    @Override
    public void onPostMsg(final int msgType, final long msgId, final String msg, final String cId, final long bid, final String proId)
    {

        Observable<Response<ResponseBody>> observable = lspServices.postMessage(manager.getAUTH(),
                Constants.selLang,msgType,msgId,msg,cId,bid+"",proId);

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse)
                    {

                        int code = responseBodyResponse.code();
                        Log.w("TAG", "onNextSendMsg: "+code);
                        String response;
                        try {
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    viewChatting.onLogout(new JSONObject(responseBodyResponse.errorBody().string()).getString("data"));
                                    break;
                                case Constants.SESSION_EXPIRED:
                                    RefreshToken.onRefreshToken(new JSONObject(responseBodyResponse.errorBody().string()).getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            onPostMsg( msgType, msgId, msg, cId, bid, proId);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {

                                        }
                                    });
                                    break;
                                default:

                                    response = responseBodyResponse.errorBody().string();
                                    try{
                                        ErrorHandel errorHandel = gson.fromJson(response,ErrorHandel.class);
                                        viewChatting.onError(errorHandel.getMessage());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (JSONException e) {
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

    @Override
    public void loadImage(String path, long bid)
    {

        File file = new File(path);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), reqFile);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), bid+"");

        Log.d("TAG", "launchUploadActivity: "+file.getName() +" reqfile "+reqFile.toString()+" body "+body.toString()+" descrip "+description.toString());

        // finally, execute the request
        Call<ResponseBody> call = lspServices.upload(body);//description
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response)
            {

                int code = response.code();
                Log.v("Upload", "success "+code);
                long msgid = (System.currentTimeMillis());///1000;
               int typeMsg = 2;
                try {

                    if (response.isSuccessful()) {

                        String responseBody = response.body().string();
                        Log.d("TAG", "onResponse: "+responseBody);
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String image = jsonObject.getString("data");
                        viewChatting.sendImageMessage(image,typeMsg,msgid);
                    }else

                    {
                        String errorBody = response.errorBody().string();
                        Log.d("TAG", "onResponse: "+errorBody);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    @Override
    public void getIntentValue() {

        viewChatting.setIntentValue(manager.getChatBookingID(), manager.getChatProId(), manager.getProName(), manager.getSID());
    }
}
