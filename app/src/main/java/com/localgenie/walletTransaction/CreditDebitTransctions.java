package com.localgenie.walletTransaction;



public class CreditDebitTransctions
{

    /*"txnId":"WAL-1525329330-882.4883915169897",
"trigger":"WALLET RECHARGE",
"txnType":"CREDIT",
"comment":"Wallet recharge from provider app",
"currency":"USD",
"openingBal":0,
"amount":1000,
"closingBal":1000,
"paymentType":"WALLET",
"timestamp":1525329330,
"paymentTxnId":"ch_1CNb2MLsYTSIIkpUU5DZ6lQ8",
"intiatedBy":"Provider",
"tripId":"N/A"*/
    private String txnType;

    private String trigger;

    private String openingBal;

    private String tripId;

    private String paymentType;

    private String intiatedBy;

    private String txnId;

    private String currency;

    private double amount;

    private String timestamp;

    private String paymentTxnId;

    private String comment;

    private String closingBal;

    public String getTxnType ()
    {
        return txnType;
    }

    public void setTxnType (String txnType)
    {
        this.txnType = txnType;
    }

    public String getTrigger ()
    {
        return trigger;
    }

    public void setTrigger (String trigger)
    {
        this.trigger = trigger;
    }

    public String getOpeningBal ()
    {
        return openingBal;
    }

    public void setOpeningBal (String openingBal)
    {
        this.openingBal = openingBal;
    }

    public String getTripId ()
    {
        return tripId;
    }

    public void setTripId (String tripId)
    {
        this.tripId = tripId;
    }

    public String getPaymentType ()
    {
        return paymentType;
    }

    public void setPaymentType (String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getIntiatedBy ()
    {
        return intiatedBy;
    }

    public void setIntiatedBy (String intiatedBy)
    {
        this.intiatedBy = intiatedBy;
    }

    public String getTxnId ()
    {
        return txnId;
    }

    public void setTxnId (String txnId)
    {
        this.txnId = txnId;
    }

    public String getCurrency ()
    {
        return currency;
    }

    public void setCurrency (String currency)
    {
        this.currency = currency;
    }

    public double getAmount ()
    {
        return amount;
    }


    public String getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp (String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getPaymentTxnId ()
    {
        return paymentTxnId;
    }

    public void setPaymentTxnId (String paymentTxnId)
    {
        this.paymentTxnId = paymentTxnId;
    }

    public String getComment ()
    {
        return comment;
    }

    public void setComment (String comment)
    {
        this.comment = comment;
    }

    public String getClosingBal ()
    {
        return closingBal;
    }

    public void setClosingBal (String closingBal)
    {
        this.closingBal = closingBal;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [txnType = "+txnType+", trigger = "+trigger+", openingBal = "+openingBal+", tripId = "+tripId+", paymentType = "+paymentType+", intiatedBy = "+intiatedBy+", txnId = "+txnId+", currency = "+currency+", amount = "+amount+", timestamp = "+timestamp+", paymentTxnId = "+paymentTxnId+", comment = "+comment+", closingBal = "+closingBal+"]";
    }
}
