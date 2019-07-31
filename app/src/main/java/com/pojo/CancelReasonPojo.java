package com.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <h>CancelReasonPojo</h>
 * Created by Ali on 11/15/2017.
 */

public class CancelReasonPojo implements Serializable
{
    /*"message":"You will be charged a cancellation fee of $ 0 . Are you sure you want to continue to cancel this booking?",
            "data":{}*/
    private String message;
    private CancelData data;


    public String getMessage() {
        return message;
    }

    public CancelData getData() {
        return data;
    }

    public class CancelReasonData implements Serializable {
        /* "res_id": 4,
                 "reason": "Artist denied the duty"*/
        private int res_id;
        private String reason;

        public int getRes_id() {
            return res_id;
        }

        public String getReason() {
            return reason;
        }
    }

    public class CancelData implements Serializable
    {
        /*"cancellationFeeApplied":false,
"cancellationFee":0,
"reason":[]*/
       private boolean cancellationFeeApplied;
       private double cancellationFee;
        private ArrayList<CancelReasonData> reason;

        public boolean isCancellationFeeApplied() {
            return cancellationFeeApplied;
        }

        public double getCancellationFee() {
            return cancellationFee;
        }

        public ArrayList<CancelReasonData> getReason() {
            return reason;
        }
    }
}
