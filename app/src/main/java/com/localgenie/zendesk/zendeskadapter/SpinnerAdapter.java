package com.localgenie.zendesk.zendeskadapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Utility;
import com.localgenie.zendesk.zendeskpojo.SpinnerRowItem;


import java.util.ArrayList;

/**
 * <h>SpinnerAdapter</h>
 * Created by Ali on 12/29/2017.
 */

public class SpinnerAdapter extends ArrayAdapter<SpinnerRowItem>
{

    private Context mContext;
    private ArrayList<SpinnerRowItem> spinnerRowItems;
    public SpinnerAdapter(Context mContext,int resouceId,int textviewId, ArrayList<SpinnerRowItem> spinnerRowItems) {
        super(mContext,resouceId,textviewId,spinnerRowItems);
        this.mContext = mContext;
        this.spinnerRowItems = spinnerRowItems;
    }


    @Override
    public int getCount() {
        return spinnerRowItems.size();
    }

    @Override
    public long getItemId(int i) {
        return spinnerRowItems.get(i).getColorId();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);

    }

    private View getCustomView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_adapter, parent,false);
            holder = new ViewHolder();
            holder.ivSpinnerPriority = convertView.findViewById(R.id.ivSpinnerPriority);
            holder.tvSpinnerPriority = convertView.findViewById(R.id.tvSpinnerPriority);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        holder.tvSpinnerPriority.setText(spinnerRowItems.get(position).getPriority());
        holder.ivSpinnerPriority.setBackgroundColor(Utility.getColor(mContext,spinnerRowItems.get(position).getColorId()));
        holder.tvSpinnerPriority.setTypeface(holder.appTypeface.getHind_regular());
        return convertView;
    }

    private class ViewHolder {
        private TextView ivSpinnerPriority;
        private TextView tvSpinnerPriority;
        private AppTypeface appTypeface = AppTypeface.getInstance(mContext);
    }
}
