package com.localgenie.walletTransaction;

/**
 * @since 19/09/17.
 */

public class OldWalletTransPojo
{
        /*"errNum":200,
    "errMsg":"Got The Details",
    "errFlag":0,
    "data":{
    "debitArr":[],
    "creditArr":[],
    "creditDebitArr":[*/

    private int errNum, errFlag;
    private String errMsg;
    private WalletTransDataPojo data;

    public int getErrNum() {
        return errNum;
    }



    public int getErrFlag() {
        return errFlag;
    }


    public String getErrMsg() {
        return errMsg;
    }


    public WalletTransDataPojo getData() {
        return data;
    }



    @Override
    public String toString() {
        return "OldWalletTransPojo{" +
                "errNum=" + errNum +
                ", errFlag=" + errFlag +
                ", errMsg='" + errMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
