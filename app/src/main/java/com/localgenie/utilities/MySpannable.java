package com.localgenie.utilities;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * <h>MySpannable</h>
 * customized spannable string
 * Created by Ali on 9/20/2017.
 */

public class MySpannable extends ClickableSpan {
    private boolean isUnderline = false;

    /**
     * Constructor
     */
    public MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {

        ds.setColor(Color.parseColor("#9AD900"));
        ds.setUnderlineText(isUnderline);
    }

    @Override
    public void onClick(View widget) {

    }
}
