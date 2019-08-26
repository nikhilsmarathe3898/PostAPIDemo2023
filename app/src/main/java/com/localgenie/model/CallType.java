package com.localgenie.model;

import java.io.Serializable;

/**
 * Created by Ali on 11/23/2018.
 */
public class CallType implements Serializable
{
    /*"incall":true,
"outcall":true,
"telecall":false*/

   private boolean incall,outcall,telecall;

    public boolean isIncall() {
        return incall;
    }

    public boolean isOutcall() {
        return outcall;
    }

    public boolean isTelecall() {
        return telecall;
    }
}
