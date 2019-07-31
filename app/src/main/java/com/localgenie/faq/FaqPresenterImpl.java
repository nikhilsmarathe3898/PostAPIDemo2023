package com.localgenie.faq;

import android.util.Log;

import com.localgenie.model.faq.FAQResponse;
import com.localgenie.model.faq.FaqData;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;

import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author Pramod.
 * @since 11/01/2018.
 */

public class FaqPresenterImpl implements FaqPresenter {
private static final String TAG="FaqPresenterImpl";
    @Inject
    FaqView faqView;

    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    LSPServices lspServices;

    @Inject
    FaqPresenterImpl(FaqView faqView) {
        this.faqView = faqView;
    }

    @Override
    public void getFAQ(int userType) {
        Log.i(TAG, "getFAQ: 1");
        if (faqView != null) {
            faqView.showProgress();
        }
        Log.i(TAG, "getFAQ: 2");

        Observable<Response<FAQResponse>> response = lspServices.getFAQ(Constants.selLang,userType);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<FAQResponse>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                        faqView.hideProgress();
                        Log.i(TAG, "onSubscribe: 3");
                    }

                    @Override
                    public void onNext(Response<FAQResponse> value)
                    {
                        Log.e("FAQ","Server Req URL "+value.raw().request().url() + " Code :: "+value.code());
                        if (200 == value.code()) {
                            FAQResponse faqResponse = value.body();
                            if (faqResponse != null) {
                                faqView.hideProgress();
                                List<FaqData> faqDataList = faqResponse.getData();
                                if (faqDataList != null) {
                                    if (faqDataList.size() > 0) {
                                        faqView.addItems(faqDataList);
                                    }
                                }
                            }
                        } else {
                            try {
                                if (value.errorBody()!=null) {
                                    faqView.hideProgress();
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    faqView.setError(errJson.getString("message"));
                                }
                            } catch (Exception e) {
                                faqView.hideProgress();
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Log.i(TAG, "onError: "+e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: hideProgress");
                        faqView.hideProgress();
                    }
                });
    }
}
