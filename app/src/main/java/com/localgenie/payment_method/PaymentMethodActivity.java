package com.localgenie.payment_method;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.model.payment_method.CardGetData;
import com.localgenie.payment_details.PaymentDetailActivity;
import com.localgenie.payment_edit_card.CardEditActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.CardsListAdapter;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * @author Pramod
 * @since 16-01-2018.
 */

public class PaymentMethodActivity extends DaggerAppCompatActivity implements PaymentMethodView {

    @Inject
    SessionManager sessionManager;

    @Inject
    PaymentMethodPresenter paymentMethodPresenter;

    @Inject
    AlertProgress alertProgress;
    @Inject
    AppTypeface appTypeface;

    @BindView(R.id.rvPaymentList)
    ListView rvPaymentList;

    @BindView(R.id.rlMainCardList)RelativeLayout rlMainCardList;
    @BindView(R.id.rlListCardEmpty)RelativeLayout rlListCardEmpty;
    @BindView(R.id.tvCardNew)TextView tvCardNew;

    @BindView(R.id.tvAddCard)
    TextView tvAddCard;

    @BindString(R.string.wait_card) String wait_card;

    CardsListAdapter cardsListAdapter;

    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    String auth;
    private boolean isNotFromPayment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ButterKnife.bind(this);
        if(getIntent().getExtras()!=null)
            isNotFromPayment = getIntent().getBooleanExtra("isNotFormPayment",true);
        initialize();
    }

    private void initialize() {
        //Setting toolbar
        Toolbar toolbar= findViewById(R.id.toolbarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        TextView tvTbTitle=toolbar.findViewById(R.id.tv_center);
        tvTbTitle.setText(R.string.cards);
        tvTbTitle.setTypeface(appTypeface.getHind_semiBold());
        tvCardNew.setTypeface(appTypeface.getHind_medium());

        auth = sessionManager.getAUTH();
     //   paymentMethodPresenter.getCard(auth);

        rvPaymentList.setOnItemClickListener((adapterView, view, position, id) -> {
            if(isNotFromPayment)
            {
                CardGetData data = (CardGetData) adapterView.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data",data);
                Intent intent = new Intent(PaymentMethodActivity.this, CardEditActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }else
            {
                CardGetData data = (CardGetData) adapterView.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra("CARDID",data.getId());
                intent.putExtra("CARDTYPE",data.getBrand());
                intent.putExtra("LAST4",data.getLast4());
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
            }

            //paymentMethodPresenter.editCard();
        });

    }

    @OnClick({R.id.tvAddCard,R.id.tvCardNew})
    void setTvAddCard() {
        Intent intent = new Intent(PaymentMethodActivity.this, PaymentDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void addItems(ArrayList<CardGetData> list) {
        rlMainCardList.setVisibility(View.VISIBLE);
        rlListCardEmpty.setVisibility(View.GONE);
        cardsListAdapter = new CardsListAdapter(this,R.layout.item_card_list,list);
        rvPaymentList.setAdapter(cardsListAdapter);
    }

    @Override
    public void showProgress() {
        if (!isFinishing()) {
            dialogBuilder = new AlertDialog.Builder(PaymentMethodActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
            TextView tv_progress = dialogView.findViewById(R.id.tv_progress);
            tv_progress.setText(wait_card);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        //progressBar.setVisibility(View.GONE);
        if (!isFinishing())
            alertDialog.dismiss();
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        Toast.makeText(this,""+errorMsg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void cardNotFound() {

        rlMainCardList.setVisibility(View.GONE);
        rlListCardEmpty.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(Utility.isNetworkAvailable(this))
        {
          //  onShowProgress();
            paymentMethodPresenter.getCard(auth);
        }else
            alertProgress.showNetworkAlert(this);

    }

    @Override
    public void logout(String msg) {
        alertProgress.alertPositiveOnclick(PaymentMethodActivity.this, msg, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(PaymentMethodActivity.this,sessionManager);
            }
        });
    }
}
