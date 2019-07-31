package com.pojo;

import java.io.Serializable;

/**
 * <h>BookingAccounting</h>
 * Created by Ali on 2/20/2018.
 */

public class BookingAccounting implements Serializable
{
    /*"amount":0,
"cancellationFee":10,
"discount":0,
"total":0,
"appEarning":0,
"providerEarning":0,
"pgCommissionApp":0,
"pgCommissionProvider":0,
"totalPgCommission":0,
"appEarningPgComm":0,
"paymentMethod":1,
"paymentMethodText":"cash",
"pgName":null,
"chargeId":0,
"last4":"",
"appCommission":20*/
    private double amount,cancellationFee,discount,total,appEarning,providerEarning,pgCommissionApp
            ,pgCommissionProvider,visitFee,travelFee,bidPrice
            ,appEarningPgComm,paymentMethod,appCommission,captureAmount,remainingAmount;
    private String last4,paymentMethodText;
    private int paidByWallet,totalActualJobTimeMinutes,serviceType,totalShiftBooking;
    private long totalJobTime;

    //chargeId
    public int getTotalShiftBooking() {
        return totalShiftBooking;
    }

    public long getTotalJobTime() {
        return totalJobTime;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public int getServiceType() {
        return serviceType;
    }

    public double getTravelFee() {
        return travelFee;
    }

    public int getTotalActualJobTimeMinutes() {
        return totalActualJobTimeMinutes;
    }

    public double getVisitFee() {
        return visitFee;
    }

    public String getPaymentMethodText() {
        return paymentMethodText;
    }

    public double getCaptureAmount() {
        return captureAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public int getPaidByWallet() {
        return paidByWallet;
    }

    public double getAmount() {
        return amount;
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotal() {
        return total;
    }

    public double getAppEarning() {
        return appEarning;
    }

    public double getProviderEarning() {
        return providerEarning;
    }

    public double getPgCommissionApp() {
        return pgCommissionApp;
    }

    public double getPgCommissionProvider() {
        return pgCommissionProvider;
    }

    public double getAppEarningPgComm() {
        return appEarningPgComm;
    }

    public double getPaymentMethod() {
        return paymentMethod;
    }

/*
    public double getChargeId() {
        return chargeId;
    }
*/


    public double getAppCommission() {
        return appCommission;
    }

    public String getLast4() {
        return last4;
    }
}
