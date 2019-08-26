package com.localgenie.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.localgenie.R;

/**
 * @author Pramod
 * @since 16-Dec 2017.
 */

public class PercentageButton extends View {

    private Paint paintDefault;
    private Paint paintFilled;
    private RectF rectDefault;
    private RectF rectFilled;
    private int mWidth;
    private int mHeight;
    private int offset = 50;
    private Context context;
    private float percentValue =0;

    public PercentageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {

        paintDefault = new Paint();
        paintDefault.setColor(Color.rgb(154,217,0));

        paintFilled = new Paint();
        paintFilled.setColor(ContextCompat.getColor(context,R.color.parrotGreen));


        rectDefault = new RectF();
        rectFilled = new RectF();

        /*customBtn = new Button(mContext);
        customBtn.setText("Button");
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);*/
        //addView(customBtn, buttonParams);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getWidth();
        mHeight = getHeight();

        rectDefault = new RectF(offset,
                offset,
                mWidth - offset,
                mHeight - offset);

        rectFilled = new RectF(offset,
                offset,
                mWidth - offset,
                mHeight - offset);



        rectDefault.set(offset,offset,mWidth - offset,mHeight - offset);
        rectFilled.set(offset, offset, percentValue, mHeight - offset);

        int cornersRadius = 25;


        canvas.drawRoundRect(
                rectDefault,
                cornersRadius,
                cornersRadius,
                paintDefault
        );

        canvas.drawRoundRect(
                rectFilled,
                cornersRadius,
                cornersRadius,
                paintFilled
        );

        //canvas.drawRect(rectDefault, paintDefault);
        //canvas.drawRect(rectFilled, paintFilled);


    }

    public void setPercentValue(int percentValue) {
        this.percentValue = percentValue;
        invalidate();
    }

}