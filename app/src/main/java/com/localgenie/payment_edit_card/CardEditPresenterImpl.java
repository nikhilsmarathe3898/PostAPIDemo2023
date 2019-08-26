package com.localgenie.payment_edit_card;

import android.util.Log;

import com.localgenie.model.ServerResponse;
import com.localgenie.model.card.DeleteCard;
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
 * @author Pramod
 * @since 31-01-2018.
 */
public class CardEditPresenterImpl implements CardEditPresenter {

    @Inject
    CardEditView cardEditView;

    @Inject
    LSPServices lspServices;

    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    CardEditPresenterImpl(CardEditView cardEditView) {
        this.cardEditView = cardEditView;
    }

    @Override
    public void deleteCard(String auth,String cardId) {
        if (cardEditView!=null) {
            cardEditView.showProgress();
        }
        DeleteCard deleteCard = new DeleteCard(cardId);
        Observable<Response<ServerResponse>> bad = lspServices.deleteCard(auth,Constants.selLang,deleteCard);
        bad.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ServerResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }
                    @Override
                    public void onNext(Response<ServerResponse> value)
                    {
                        if (200 == value.code()) {
                            cardEditView.hideProgress();
                            cardEditView.navToPayment();
                        } else {
                            try {
                                if (value.errorBody()!=null) {
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    cardEditView.setErrorMsg(errJson.getString("message"));
                                    cardEditView.hideProgress();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e("Error","error"+e.getMessage());
                        e.printStackTrace();
                        cardEditView.setErrorMsg(e.getMessage());
                        cardEditView.hideProgress();
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void makeDefault(String auth, String cardId) {
        if (cardEditView!=null) {
            cardEditView.showProgress();
        }
        Observable<Response<ServerResponse>> bad = lspServices.makeDefaultCard(auth, Constants.selLang,cardId);
        bad.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ServerResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }
                    @Override
                    public void onNext(Response<ServerResponse> value)
                    {
                        if (200 == value.code()) {
                            cardEditView.hideProgress();
                            cardEditView.navToPayment();
                        } else {
                            try {
                                if (value.errorBody()!=null) {
                                    JSONObject errJson = new JSONObject(value.errorBody().string());
                                    cardEditView.setErrorMsg(errJson.getString("message"));
                                    cardEditView.hideProgress();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e("Error","error"+e.getMessage());
                        e.printStackTrace();
                        cardEditView.setErrorMsg(e.getMessage());
                        cardEditView.hideProgress();
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }

}
