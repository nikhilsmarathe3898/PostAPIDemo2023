package com.localgenie.addTocart;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManager;


public class DescDialogFragment extends DialogFragment implements View.OnClickListener {

    private double price;
    private String serviceName,description;


    SessionManager prefs;

    AppTypeface appTypeface;
    //SharedPrefs prefs;
    public DescDialogFragment(){

    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        price=args.getDouble("Price");
        serviceName=args.getString("ServiceName");
        description=args.getString("Description");
        super.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTransparent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_desc_dialog, container, false);
        appTypeface = AppTypeface.getInstance(getActivity());
        prefs = new SessionManager(getActivity());
        initializeViews(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            getDialog().getWindow().setElevation(4);
        }
        return view;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @SuppressLint("DefaultLocale")
    private void initializeViews(View view) {
        ImageView iv_closebtn=view.findViewById(R.id.iv_closebtn);
        TextView tv_serviceName,tv_servicePrice,tv_serviceDesc;
        tv_serviceDesc=view.findViewById(R.id.tv_serviceDesc);
        tv_servicePrice=view.findViewById(R.id.tv_servicePrice);
        tv_serviceName=view.findViewById(R.id.tv_serviceName);
        tv_serviceName.setTypeface(appTypeface.getHind_semiBold());
        tv_servicePrice.setTypeface(appTypeface.getHind_semiBold());
        tv_serviceDesc.setTypeface(appTypeface.getHind_regular());
        iv_closebtn.setOnClickListener(this);
        tv_serviceDesc.setText(description);
        tv_serviceName.setText(serviceName);
        tv_servicePrice.setText(Constants.currencySymbol+" "+String.format("%.2f",price));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_closebtn:
                dismiss();
                break;
        }
    }
}
