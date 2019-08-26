package com.localgenie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Pramod
 * @since 11/01/2018.
 */

public class PaymentMethods {

    @SerializedName("card")
    @Expose
    private Boolean card;
    @SerializedName("cash")
    @Expose
    private Boolean cash;
    @SerializedName("wallet")
    @Expose
    private Boolean wallet;

    public Boolean getCard() {
        return card;
    }

    public void setCard(Boolean card) {
        this.card = card;
    }

    public Boolean getCash() {
        return cash;
    }

    public void setCash(Boolean cash) {
        this.cash = cash;
    }

    public Boolean getWallet() {
        return wallet;
    }

    public void setWallet(Boolean wallet) {
        this.wallet = wallet;
    }

}
