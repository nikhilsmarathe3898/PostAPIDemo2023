package com.localgenie.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;


/**
 * Created by embed on 15/9/16.
 *
 */
public class BitmapCustomMarker
{
    static int i=1;
    Bitmap bitmap;
    Context context;
    private String etaTime;
    public BitmapCustomMarker(Context context, String etaTime)
    {
        this.context=context;
        this.etaTime=etaTime;
    }
    public Bitmap createBitmap()
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout=inflater.inflate(R.layout.markerlayoutlivetracking, null);
        RelativeLayout lyBitmap= layout.findViewById(R.id.lyBitmap);
        TextView tveta= layout.findViewById(R.id.tvEta);
        TextView tvMin= layout.findViewById(R.id.tvMin);
        if(!etaTime.equals(""))
        {
            int i= etaTime.indexOf(" ");
            tveta.setText(etaTime.substring(0,i));
            tvMin.setText(etaTime.substring(i));
        }
        lyBitmap.setDrawingCacheEnabled(true);
        lyBitmap.buildDrawingCache();
        lyBitmap.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        lyBitmap.layout(0, 0, lyBitmap.getMeasuredWidth(), lyBitmap.getMeasuredHeight());
        bitmap =lyBitmap.getDrawingCache();
        return bitmap;
    }

}
