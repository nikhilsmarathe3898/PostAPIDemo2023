package com.localgenie.wallet;


import com.localgenie.home.BasePresenter;

public interface WalletActivityContract
{
     interface WalletView
    {
        /**
         * <h>Api Error</h>
         * <p>this method is using to display Api call Error</p>
         * @param error
         */
        void walletDetailsApiErrorViewNotifier(String error);

        /**
         * <h2>showToastNotifier</h2>
         * <p> method to trigger activity/fragment show progress dialog interfac </p>
         */
        void showProgressDialog();

        /**
         * <h2>showToastNotifier</h2>
         * <p> method to trigger activity/fragment showToast interface to show test </p>
         * @param msg: message to be shown in toast
         * @param duration: toast duration
         */
        void showToast(String msg, int duration);

        /**
         * <h2>showAlertNotifier</h2>
         * <p> method to trigger activity/fragment showAlertNotifier interface to show alert </p>
         * @param title: alert title to be setList
         * @param msg: alert message to be displayed
         */
        void showAlert(String title, String msg);


        /**
         * <h2>noInternetAlert</h2>
         * <p> method to trigger activity/fragment alert interface to show alert that there isnot internet connectivity </p>
         */
        void noInternetAlert();

        /**
         * <H>Hide Progress bar</H>
         * <p>This method is using to hide the progress bar</p>
         */
        void hideProgressDialog();

        /**
         * <H>Set Wallet Balance</H>
         * <p>Set the updated Wallet balance</p>
         * @param balance new balance
         * @param hardLimit new hard limit
         * @param softLimit new soft limit
         */
        void setBalanceValues(String balance, String hardLimit, String softLimit);

        /**
         * <h>Show recharge Confirmation</h>
         * <p>This method is using to  display Confirmation message to User</p>
         * @param amount amount to display
         */
        void showRechargeConfirmationAlert(String amount);

        /**
         * <H>Set Card type View</H>
         * <p>this method is using to set card details view</p>
         * @param cardNum card last 4 digits
         * @param cardType card brand
         */
        void setCard(String cardNum, String cardType);

        /**
         * <H>Set no Card View</H>
         * <p>this method is using to set no card view</p>
         */
        void setNoCard();

        void walletRecharged(boolean recharged, String message);

        void onLogout(String msg);

        void onError(String message);
    }

    interface WalletPresenter
    {
        /**
         * <h>Recharge Wallet</h>
         * <p>this method is uisng to recharge the Wallet</p>
         * @param amount amount to be rechrged
         * @param cardId
         */
        void rechargeWallet(String amount, String cardId);

        /**
         * <H>get Wallet limits</H>
         * <p>this method is using to get Wallet limit</p>
         */
        void getWalletLimits();

        /**
         * <h>Get Crad number</h>
         * <p>this method is using to get Card Last four digits</p>
         */
        void getLastCardNo();

        /**
         * <h>get Currency Symbol</h>
         * <p>this method is using to get the Currency Symbol</p>
         * @return currency  symbol
         */
        String getCurrencySymbol();

    }

    interface WalletPresenterBalance extends BasePresenter
    {
        /**
         * <H>get Wallet limits</H>
         * <p>this method is using to get Wallet limit</p>
         */
        void getWalletLimits();

    }
}

