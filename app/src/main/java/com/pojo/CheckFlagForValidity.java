package com.pojo;

import java.io.Serializable;

/**
 * Created by Ali on 9/18/2018.
 */
public class CheckFlagForValidity
        implements Serializable {
    private boolean nFlg, eFlg, pFlg, mFlg, bNFlg;

    public boolean isnFlg() {
        return nFlg;
    }

    public void setnFlg(boolean nFlg) {
        this.nFlg = nFlg;
    }

    public boolean iseFlg() {
        return eFlg;
    }

    public void seteFlg(boolean eFlg) {
        this.eFlg = eFlg;
    }

    public boolean ispFlg() {
        return pFlg;
    }

    public void setpFlg(boolean pFlg) {
        this.pFlg = pFlg;
    }

    public boolean ismFlg() {
        return mFlg;
    }

    public void setmFlg(boolean mFlg) {
        this.mFlg = mFlg;
    }

    public boolean isbNFlg() {
        return bNFlg;
    }

    public void setbNFlg(boolean bNFlg) {
        this.bNFlg = bNFlg;
    }
}
