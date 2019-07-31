package com.localgenie.walletTransaction;


import java.util.ArrayList;

/**
 * @since 19/09/17.
 */

public class WalletTransDataPojo
{
   /* "data":{
    "debitArr":[],
    "creditArr":[],
    "creditDebitArr":[]}*/

   private ArrayList<CreditDebitTransctions> debitArr;
   private ArrayList<CreditDebitTransctions> creditArr;
   private ArrayList<CreditDebitTransctions> creditDebitArr;
   private ArrayList<CreditDebitTransctions> paymentArr;


    public ArrayList<CreditDebitTransctions> getPaymentArr() {
        return paymentArr;
    }

    public ArrayList<CreditDebitTransctions> getDebitArr() {
        return debitArr;
    }

    public ArrayList<CreditDebitTransctions> getCreditArr() {
        return creditArr;
    }

    public ArrayList<CreditDebitTransctions> getCreditDebitArr() {
        return creditDebitArr;
    }

    @Override
    public String toString() {
        return "WalletTransDataPojo{" +
                "debitArr=" + debitArr +
                ", creditArr=" + creditArr +
                ",paymentArr=" + paymentArr +
                ", creditDebitArr=" + creditDebitArr +
                '}';
    }
}
