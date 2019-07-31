package com.localgenie.selectPaymentMethod;

import android.content.Context;

import com.localgenie.home.BasePresenter;
import com.localgenie.home.BaseView;
import com.localgenie.model.payment_method.CardGetData;
import com.utility.AlertProgress;

import java.util.ArrayList;

/**
 * <h>SelectedCardInfoInterface</h>
 * Created by Ali on 3/12/2018.
 */

public interface SelectedCardInfoInterface
{
    interface SelectedPresenter extends BasePresenter
    {
        void onGetCards();

        void getWalletAmount();

        void setCashCardBookingView(int select, double balance, double softLimit, double hardLimit, Context mContext, AlertProgress alertProgress);
    }
    interface SelectedView extends BaseView
    {
        void onToCallIntent();

        void onToBackIntent(int adapterPosition);

        void addItems(ArrayList<CardGetData> cardsList);

        void onVisibilitySet();

        void showWalletAmount(String currencySymbol, double v, double softLimit, double hardLimit);

        void startActivity();

        void paymentSelection(int selectedCell);
    }
}
