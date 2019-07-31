package com.localgenie.promocode;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.pojo.PromoCodeResponse;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;
import com.utility.RefreshToken;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import adapters.PromoCodeAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class PromoCodeActivity extends DaggerAppCompatActivity implements PromoCodeContract.PromoPresent{

    @BindView(R.id.recyclerViewPromoCode)RecyclerView recyclerViewPromoCode;
    @BindView(R.id.etPromoCode)EditText etPromoCode;
    @BindView(R.id.tvApply)TextView tvApply;
    @Inject AppTypeface appTypeface;
    @Inject
    SessionManagerImpl manager;
    @BindView(R.id.toolBarPromo)Toolbar toolbar;
    @BindView(R.id.tv_center)TextView tvCenter;

    @Inject
    LSPServices lspServices;
    @Inject
    AlertProgress alertProgress;
    @Inject
    Gson gson;
    private PromoCodeAdapter promoCodeAdapter;


    private ArrayList<PromoCodeResponse.PromoCodeData>promoCodeData = new ArrayList<>();

    private final String TAG = PromoCodeActivity.class.getSimpleName();

    private double lat,lng;
    private int paymentType;
    private String cartId;
    AlertDialog alertDialog;



 //   private BottomSheetBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_code);
        ButterKnife.bind(this);
        getIntentValue();
        initializeToolBar();
        setTypeFace();
        promocodeBottom();

    }

    private void promocodeBottom() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //  recyclerViewDetails.setLayoutManager(layoutManager);
      //  sheetBehavior = BottomSheetBehavior.from(llPromoDetails);
    }

    private void initializeToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvCenter.setText(getString(R.string.promocode));
        tvCenter.setTypeface(appTypeface.getHind_semiBold());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void getIntentValue() {

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            lat = bundle.getDouble("BookingLat");
            lng = bundle.getDouble("BookingLng");
            cartId = bundle.getString("cartId");
            paymentType = bundle.getInt("PaymentMethod");
        }
    }

    private void setTypeFace() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        tvApply.setTypeface(appTypeface.getHind_semiBold());
        etPromoCode.setTypeface(appTypeface.getHind_medium());
        recyclerViewPromoCode.setLayoutManager(linearLayoutManager);
        promoCodeAdapter =new PromoCodeAdapter(this,promoCodeData,this);
        recyclerViewPromoCode.setAdapter(promoCodeAdapter);

        /*    tvPromoCodeHowToDetail.setTypeface(appTypeface.getHind_regular());
            tvPromoCodeDetails.setTypeface(appTypeface.getHind_semiBold());
            tvPromoDetailsApply.setTypeface(appTypeface.getHind_semiBold());
            tvAddedDescription.setTypeface(appTypeface.getHind_medium());*/

        getPromoCode();
    }

    private void getPromoCode()
    {
        alertDialog = alertProgress.getProgressDialog(this,getString(R.string.wait));

        onShowProgress();
        Observable<Response<ResponseBody>>responseObservable = lspServices.getPromoCode(manager.getAUTH(),Constants.selLang,
              lat,lng);

      responseObservable.subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                  @Override
                  public void onNext(Response<ResponseBody> responseBodyResponse) {

                      int code = responseBodyResponse.code();
                      Log.d(TAG, "onNext: "+code);
                      String response;
                      try
                      {
                          switch (code)
                          {
                              case Constants.SUCCESS_RESPONSE:
                                  promoCodeData.clear();
                                  response = responseBodyResponse.body().string();
                                  Log.d(TAG, "onNextResponse: "+response);
                                  PromoCodeResponse promoCodeResponse = gson.fromJson(response,PromoCodeResponse.class);
                                  promoCodeData.addAll(promoCodeResponse.getData());
                                  Log.d(TAG, "onNext: "+promoCodeData.size());
                                  promoCodeAdapter.notifyDataSetChanged();
                                  onHideProgress();
                                  break;
                              case Constants.SESSION_LOGOUT:
                                  onHideProgress();
                                  Utility.setMAnagerWithBID(PromoCodeActivity.this,manager);
                                  break;
                              case Constants.SESSION_EXPIRED:
                                  response = responseBodyResponse.errorBody().string();
                                  RefreshToken.onRefreshToken(new JSONObject(response).getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                      @Override
                                      public void onSuccessRefreshToken(String newToken) {
                                          getPromoCode();
                                      }

                                      @Override
                                      public void onFailureRefreshToken() {

                                      }

                                      @Override
                                      public void sessionExpired(String msg)
                                      {
                                          onHideProgress();
                                          Utility.setMAnagerWithBID(PromoCodeActivity.this,manager);

                                      }
                                  });
                                  break;
                          }
                      }catch (IOException e)
                      {
                          e.printStackTrace();
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }

                  }

                  @Override
                  public void onError(Throwable e)
                  {
                      onHideProgress();
                      alertProgress.tryAgain(PromoCodeActivity.this,  getString(R.string.pleaseCheckInternet), getString(R.string.system_error), new DialogInterfaceListner() {
                          @Override
                          public void dialogClick(boolean isClicked) {
                              if(isClicked)
                                  getPromoCode();
                          }
                      });
                  }

                  @Override
                  public void onComplete() {
                      onHideProgress();
                  }
              });

    }

    @OnClick(R.id.tvApply)
    public void applyClick()
    {
        String code = etPromoCode.getText().toString().trim();
        if(!code.isEmpty())
        {
            etPromoCode.setText("");
            Utility.hideKeyboard(this);

            callApiService(code);
        }
    }

    private void callApiService(String proCode)
    {
        alertDialog = alertProgress.getProgressDialog(this,getString(R.string.wait_validating_promo));
        onShowProgress();
        Log.d(TAG, "callApiService: "+lat+" lng "+lng+" cartId "+cartId+" payment "+paymentType+" code "+proCode);
        Observable<Response<ResponseBody>> responseObservable = lspServices.postPromoCodeValidation(manager.getAUTH(),Constants.selLang
        ,lat,lng,cartId,paymentType,proCode);
        responseObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();

                        Log.d(TAG, "onNextCode: "+code);
                        try
                        {
                            String response;
                            switch (code)
                            {
                                case  Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();
                                    Log.d(TAG, "onNextPROMOCODE: "+response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject jsonData = jsonObject.getJSONObject("data");
                                    double amount = jsonData.getDouble("discountAmount");
                                    onHideProgress();
                                    alertProgress.alertPositiveOnclick(PromoCodeActivity.this, jsonObject.getString("message"), getString(R.string.promocode),getString(R.string.ok), new DialogInterfaceListner() {
                                        @Override
                                        public void dialogClick(boolean isClicked) {
                                            callOnResultActivity(amount,proCode);
                                        }
                                    });

                                    break;
                                case Constants.SESSION_LOGOUT:
                                    onHideProgress();
                                    Utility.setMAnagerWithBID(PromoCodeActivity.this,manager);

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    response = responseBodyResponse.errorBody().string();
                                    RefreshToken.onRefreshToken(new JSONObject(response).getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {
                                            callApiService(proCode);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg)
                                        {
                                            onHideProgress();
                                            Utility.setMAnagerWithBID(PromoCodeActivity.this,manager);

                                        }
                                    });
                                    break;
                                    default:

                                        response = responseBodyResponse.errorBody().string();
                                        onHideProgress();
                                        alertProgress.alertinfo(PromoCodeActivity.this,new JSONObject(response).getString("message"));

                                        break;
                            }

                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        onHideProgress();
                        alertProgress.tryAgain(PromoCodeActivity.this, getString(R.string.pleaseCheckInternet), getString(R.string.system_error), new DialogInterfaceListner() {
                            @Override
                            public void dialogClick(boolean isClicked) {
                                if(isClicked)
                                    callApiService(proCode);
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        onHideProgress();
                    }
                });
    }

    private void callOnResultActivity(double amount, String code)
    {
        Intent intent = new Intent();
        intent.putExtra("DISCOUNTAMOUNT",amount);
        intent.putExtra("PROMOCODE",code);
        setResult(RESULT_OK,intent);
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
    }

    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message)
    {
        onShowProgress();
        callApiService(message);
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onShowProgress() {

        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    @Override
    public void onHideProgress() {

        if (!isFinishing())
            alertDialog.dismiss();
    }

    @Override
    public void onViewMoreClicked(PromoCodeResponse.PromoCodeData promoCodeData) {

        Bundle bundle = new Bundle();
        BottomSheetDialogFrag dialogFragment=new BottomSheetDialogFrag();

        dialogFragment.initializePresenter(this);
        bundle.putSerializable("PromoCodeData",promoCodeData);

        dialogFragment.setArguments(bundle);
        dialogFragment.show(getSupportFragmentManager(),"BottomSHEET");
        /*if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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

        }*/
    }

    @Override
    public void onPromoCodeSelected(String promoCode) {
        Log.d("TAG", "onClick: "+promoCode);
        onLogout(promoCode);
    }

    private View.OnClickListener applyPromoCode(String code)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("TAG", "onClick: "+code);
                onLogout(code);
            }
        };
    }
}
