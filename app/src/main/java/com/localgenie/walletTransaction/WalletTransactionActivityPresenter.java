package com.localgenie.walletTransaction;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import com.localgenie.R;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class WalletTransactionActivityPresenter implements WalletTransactionContract.WalletTransactionPresenter
{

    private final String TAG = "WalletTransProvider";

    @Inject
    SessionManagerImpl preferenceHelperDataSource;
    @Inject Context mContext;
    @Inject WalletTransactionContract.WalletTrasactionView trasactionView;
    @Inject
    LSPServices networkService;
   // @Inject NetworkStateHolder networkStateHolder;
    @Inject Gson gson;


    @Inject
    WalletTransactionActivityPresenter()
    {
    }

    /**
     * <h2>initLoadTransactions</h2>
     * <p> method to init the getTransactionsHistory() api call if network connectivity is there </p>
     * @param isToLoadMore: true if is from load more option
     * @param isFromOnRefresh: true if it is to refresh
     */
    public void initLoadTransactions(boolean isToLoadMore, boolean isFromOnRefresh)
    {
//        if( networkStateHolder.isConnected())
//        {
            if(!isFromOnRefresh)
            {
//                trasactionView.showProgressDialog(mContext.getString(R.string.pleaseWait));
                getTransactionHistory();
            }else
                getTransactionHistory();

//        }
//        else
//        {
//            trasactionView.noInternetAlert();
//        }
    }


    @Override
    public void showToastNotifier(String msg, int duration)
    {
        trasactionView.showToast(msg, duration);
    }


    /**
     * <h>get Wallet History</h>
     * <p>this method is using to get the Wallet history data</p>
     */
    private void getTransactionHistory()
    {

        trasactionView.showProgressDialog(mContext.getString(R.string.pleaseWait));
        Observable<Response<ResponseBody>> request = networkService.getWalletTransaction(
                preferenceHelperDataSource.getAUTH(),
                Constants.selLang,0);

        request.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>()
                {

                    @Override
                    public void onNext(Response<ResponseBody> value)
                    {
                        Log.d(TAG, " getWalletTrans onNext: "+value.code());
                        try
                        {
                            switch (value.code())
                            {
                                case 200:
                                    //   String response= DataParser.fetchSuccessResponse(value);
                                    String response =value.body().string();
                                    Log.d(TAG, " getWalletTrans onNext: "+response);
                                    handleResponse(response);
                                    break;
                                default:
                                    break;
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(Throwable e) {
                       Log.d(TAG, "getWalletTrans error: "+e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        trasactionView.hideProgressDialog();

                    }
                });
    }


    /**
     * <h>Response Handler</h>
     * <p>this method is using to  handle the Server Response</p>
     * @param response server response
     */
    private void handleResponse(String response)
    {
        try
        {
            OldWalletTransPojo oldWalletTransPojo = gson.fromJson(response, OldWalletTransPojo.class);
            trasactionView.setAllTransactionsAL(oldWalletTransPojo.getData().getCreditDebitArr());
            trasactionView.setCreditTransactionsAL(oldWalletTransPojo.getData().getCreditArr());
            trasactionView.setDebitTransactionsAL(oldWalletTransPojo.getData().getDebitArr());
            trasactionView.setPaymentTransactionsAL(oldWalletTransPojo.getData().getPaymentArr());
            trasactionView.walletTransactionsApiSuccessViewNotifier();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}