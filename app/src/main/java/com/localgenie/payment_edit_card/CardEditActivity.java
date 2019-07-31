package com.localgenie.payment_edit_card;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.model.payment_method.CardGetData;
import com.localgenie.payment_method.PaymentMethodActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * @author Pramod
 * @since 16-01-2018.
 */

public class CardEditActivity extends DaggerAppCompatActivity implements CardEditView {

    private static String TAG="CardEditActivity";
    @Inject
    SessionManagerImpl sessionManager;
    @Inject
    CardEditPresenter cardEditPresenter;
    @BindView(R.id.tv_cardNumber)
    TextView tv_cardNumber;

    @BindView(R.id.btnMarkDefault) Button btnMarkDefault;
    @BindView(R.id.btnDeleteCard) Button btnDeleteCard;

    @BindView(R.id.iv_card_logo)
    ImageView iv_card_logo;

    @Inject
    AlertProgress alertProgress;

    //@BindString(R.string.wait_make_def_card) String wait_card;
    @BindView(R.id.tv_expiryDate)
    TextView tv_expiryDate;
    AlertDialog alertDialog;
  //  AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    String cardId,auth;
    private boolean isDefault;

    @Inject
    AppTypeface appTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        alertDialog = alertProgress.getProgressDialog(this,getString(R.string.wait_card));
        //Setting toolbar
        Toolbar toolbar=  findViewById(R.id.toolbarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        TextView tvTbTitle=toolbar.findViewById(R.id.tv_center);
        tvTbTitle.setText(R.string.your_card_details);
        tvTbTitle.setTypeface(appTypeface.getHind_semiBold());

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            CardGetData data = (CardGetData) bundle.getSerializable("data");
            AccountManager accountManager = AccountManager.get(this);

         //   auth  = Utility.getAuthToken(accountManager,sessionManager.getUSER_NAME());
            auth  = sessionManager.getAUTH();
            if (data!=null) {
                cardId = data.getId();
                isDefault = data.getIsDefault();
                if (data.getLast4()!=null)
                    tv_cardNumber.setText("" + data.getLast4());
                if (data.getExpMonth()!=null && data.getExpYear()!=null)
                    tv_expiryDate.setText(" "+data.getExpMonth() +"/"+ data.getExpYear());
                if (data.getBrand()!=null)
                    iv_card_logo.setImageBitmap(Utility.setCreditCardLogo(data.getBrand(),this));

            }
        }

        if(!isDefault)
        {
            btnDeleteCard.setVisibility(View.VISIBLE);
            btnMarkDefault.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btnMarkDefault)
    void setBtnMarkDefault() {
        Log.e(TAG,"Make Default");
        if (cardId!=null && auth!=null)
        {
            alertDialog = alertProgress.getProgressDialog(this,getString(R.string.saving));
            showProgress();
            cardEditPresenter.makeDefault(auth,cardId);
        }
    }

    @OnClick(R.id.btnDeleteCard)
    void setBtnDeleteCard() {
        Log.e(TAG,"Delete");
        if (cardId!=null && auth!=null)
        {
            alertProgress.alertPositiveNegativeOnclick(CardEditActivity.this, getString(R.string.want_to_delete), getString(R.string.system_error),getResources().getString(R.string.ok), getResources().getString(R.string.cancel),false , isClicked -> {
                if(isClicked)
                {
                    alertDialog = alertProgress.getProgressDialog(CardEditActivity.this,getString(R.string.deleting));
                    showProgress();
                    cardEditPresenter.deleteCard(auth, cardId);
                }
            });

        }
    }

    @Override
    public void navToPayment() {
        Intent intent = new Intent(CardEditActivity.this, PaymentMethodActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void showProgress() {
        if (!isFinishing()) {
            alertDialog.show();
            /*dialogBuilder = new AlertDialog.Builder(CardEditActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
            TextView tv_progress = dialogView.findViewById(R.id.tv_progress);
            tv_progress.setText("");
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            alertDialog = dialogBuilder.create();
            alertDialog.show();*/
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

}
