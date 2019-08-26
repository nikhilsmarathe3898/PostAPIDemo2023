package com.localgenie.promocode;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.pojo.PromoCodeResponse;

import butterknife.BindView;

/**
 * Created by Ali on 6/11/2018.
 */
public class BottomSheetDialogFrag extends BottomSheetDialogFragment
{

    @BindView(R.id.llPromoDetails)LinearLayout llPromoDetails;
    @BindView(R.id.recyclerViewDetails)RecyclerView recyclerViewDetails;
    @BindView(R.id.tvAddedDescription)TextView tvAddedDescription;
    @BindView(R.id.tvPromoCodeDetails)TextView tvPromoCodeDetails;
    @BindView(R.id.tvPromoCodeHowToDetail)TextView tvPromoCodeHowToDetail;
    @BindView(R.id.tvPromoDetailsApply)TextView tvPromoDetailsApply;
    private View view;
    private PromoCodeResponse.PromoCodeData promoCodeData;
    private AppTypeface appTypeface;
    private PromoCodeContract.PromoPresent promoPresent;
    public BottomSheetDialogFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.bottom_sheet_promo, container, false);
        appTypeface = AppTypeface.getInstance(getActivity());
        initializeView();
        return view;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        promoCodeData= (PromoCodeResponse.PromoCodeData) args.getSerializable("PromoCodeData");
        super.setArguments(args);
    }

    private void initializeView() {

        llPromoDetails = view.findViewById(R.id.llPromoDetails);
        recyclerViewDetails = view.findViewById(R.id.recyclerViewDetails);
        tvAddedDescription = view.findViewById(R.id.tvAddedDescription);
        tvPromoCodeDetails = view.findViewById(R.id.tvPromoCodeDetails);
        tvPromoCodeHowToDetail = view.findViewById(R.id.tvPromoCodeHowToDetail);
        tvPromoDetailsApply = view.findViewById(R.id.tvPromoDetailsApply);


        tvPromoCodeHowToDetail.setTypeface(appTypeface.getHind_regular());
        tvPromoCodeDetails.setTypeface(appTypeface.getHind_semiBold());
        tvPromoDetailsApply.setTypeface(appTypeface.getHind_semiBold());
        tvAddedDescription.setTypeface(appTypeface.getHind_medium());
        setValue();
    }

    private void setValue() {

        tvPromoCodeDetails.setText(promoCodeData.getCode());
        tvPromoDetailsApply.setOnClickListener(applyPromoCode(promoCodeData.getCode()));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            tvPromoCodeHowToDetail.setText(Html.fromHtml(promoCodeData.getDescription(), Html.FROM_HTML_MODE_COMPACT));
            tvAddedDescription.setText(Html.fromHtml(promoCodeData.getTermsAndConditions(), Html.FROM_HTML_MODE_COMPACT));
        }
        else
        {
            tvAddedDescription.setText(Html.fromHtml(promoCodeData.getTermsAndConditions()));
            tvPromoCodeHowToDetail.setText(Html.fromHtml(promoCodeData.getDescription()));
        }
    }

    private View.OnClickListener applyPromoCode(String code) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promoPresent.onPromoCodeSelected(code);
            }
        };
    }

    public void initializePresenter(PromoCodeContract.PromoPresent promoPresent)
    {
        this.promoPresent = promoPresent;
    }
}
