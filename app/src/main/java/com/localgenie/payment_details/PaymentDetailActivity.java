package com.localgenie.payment_details;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * @author Pramod
 * @since 16-01-2018.
 */

public class PaymentDetailActivity extends DaggerAppCompatActivity implements PaymentDetailView {

    @Inject
    SessionManagerImpl sessionManager;

    @Inject
    PaymentDetailPresenter paymentDetailPresenter;

    @Inject
    AlertProgress alertProgress;

    @BindView(R.id.card_input_widget)
    CardMultilineWidget cardInputWidget;

    @BindView(R.id.btn_scan_card)
    Button btn_scan_card;

    @BindView(R.id.btnDone)
    Button btnDone;

    String auth;

    @BindString(R.string.wait_add_card) String wait_add_card;

    @BindString(R.string.your_card_details) String your_card_details;

    AlertDialog alertDialog;


    private CreditCard scanResult;
    private boolean scanflag = false;

    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1100;

    Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_payment);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize()
    {
        alertDialog = alertProgress.getProgressDialog(this,wait_add_card);
        //Setting toolbar
        Toolbar toolbar= findViewById(R.id.toolbarLayout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        TextView tvTbTitle=toolbar.findViewById(R.id.tv_center);
        tvTbTitle.setTypeface(AppTypeface.getInstance(this).getHind_semiBold());
        tvTbTitle.setText(R.string.enter_card_details);

        auth = sessionManager.getAUTH();

    }

    @OnClick(R.id.btnDone)
    void setBtnDone() {
        // Remember that the card object will be null if the user inputs invalid data.

        card = cardInputWidget.getCard();
        if (card != null) {
            btnDone.setEnabled(false);
            showProgress();
            Log.d("TAG", "setBtnDone: "+Constants.stripeKeys);
            Stripe stripe = new Stripe(this, Constants.stripeKeys);
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            //Send token to your server
                            String card_token = token.getId();
                            Log.e("Card_details","Card token :  "+card_token);
                            paymentDetailPresenter.addCard(auth,sessionManager.getEmail(),card_token);
                            btnDone.setEnabled(true);
                            //btnDone.setTextColor(ResourcesCompat.getColor(getResources(), R.color.parrotGreen, null));
                        }
                        public void onError(Exception error) {
                            Toast.makeText(PaymentDetailActivity.this,
                                    ""+error, Toast.LENGTH_LONG).show();
                            hideProgress();
                        }
                    }
            );
        } else {
            // Do not continue token creation.
            Log.e("STRIPE","Can't create token");
        }
    }


    @OnClick(R.id.btn_scan_card)
    void setBtnScanCard() {
        card = cardInputWidget.getCard();
        Log.e("STRIPE", "setBtnScanCard:");
        if(Build.VERSION.SDK_INT>=23) {
            if (ContextCompat.checkSelfPermission(PaymentDetailActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                onScanPress();
            } else {
                requestCameraPermission();
            }
        }else
            onScanPress();
    }

    private void onScanPress() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: true
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true);
        int MY_SCAN_REQUEST_CODE = 100;
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    private void scanCardResult() {
        if (card != null) {
            EditText etcard = cardInputWidget.findViewById(com.stripe.android.R.id.et_card_number);
            EditText etExpiryDate = cardInputWidget.findViewById(com.stripe.android.R.id.et_expiry_date);
            EditText etcardcvv = cardInputWidget.findViewById(com.stripe.android.R.id.et_cvc_number);
            etcard.setText(scanResult.cardNumber);
            String expiryyear = String.valueOf(scanResult.expiryYear);
            String scancardmonth = String.valueOf(scanResult.expiryMonth);
            int expiryMonth = scanResult.expiryMonth;
            int expiryYear = scanResult.expiryYear;
            if (scanResult.expiryMonth < 10) {
                scancardmonth = "0" + scancardmonth;
                etExpiryDate.setText(scancardmonth + "/" + expiryyear);
            } else {
                etExpiryDate.setText(scancardmonth + "/" + expiryyear);
            }
            etcardcvv.setText(scanResult.cvv);
            etcard.requestFocus();
        }
    }

    //Request for Camera Permissions for scanning card and fetching details from the card via STRIPE
    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            int cameraPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);

            List<String> listPermissionsNeeded = new ArrayList<>();

            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_CODE_CAMERA_PERMISSION);
            }
        } else
        {
        }
    }


    @Override
    public void showProgress() {
        if (!isFinishing()) {
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
    public void navToPaymentScreen() {
        /*Intent intent = new Intent(PaymentDetailActivity.this, PaymentMethodActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
        finish();
    }

    /**
     * predefined method to check run time permissions list call back
     *
     * @param requestCode   request code
     * @param permissions:  contains the list of requested permissions
     * @param grantResults: contains granted and un granted permissions result list
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isDenine = false;
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_PERMISSION:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isDenine = true;
                    }
                }
                if (isDenine) {
                    Toast.makeText(this, "Camera Permission denied by the user", Toast.LENGTH_SHORT).show();
                } else {
                    onScanPress();
                    //TODO: if permissions granted by user, move forward
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
            scanflag = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scanflag) {
            scanCardResult();
        }/*else if(etcard.getText().toString().equals("")){
            etcard.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }*/

    }

    @Inject SessionManagerImpl manager;

    @Override
    public void logout(String message) {
        alertProgress.alertPositiveOnclick(this, message, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                // Utility.setMAnagerWithBID(AddAddressActivity.this,sessionManager);
                Utility.setMAnagerWithBID(PaymentDetailActivity.this,manager);
            }
        });
    }
}
