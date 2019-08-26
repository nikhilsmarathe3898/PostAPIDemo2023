package com.localgenie.walletTransaction;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.utility.AlertProgress;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.ViewPagerAdapter;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * <h1>WalletFragment</h1>
 * <p> Class to load WalletTransActivity and show all transactions list </p>
 */
public class WalletTransActivity extends DaggerAppCompatActivity
        implements WalletTransactionContract.WalletTrasactionView
{
    private ArrayList<CreditDebitTransctions> allTransactionsAL;
    private ArrayList<CreditDebitTransctions> debitTransactionsAL;
    private ArrayList<CreditDebitTransctions> creditTransactionsAL;
    private ArrayList<CreditDebitTransctions> paymentTransactionsAL;
    @Inject WalletTransactionsFragment allTransactionsFrag,debitsFrag,creditsFrag,paymentFrag;
   /* @Inject  WalletTransactionsFragment debitsFrag;
    @Inject  WalletTransactionsFragment creditsFrag;*/
    @Inject
    AlertProgress alerts;
    private ProgressDialog pDialog;

    @BindView(R.id.toolBarWalletTransaction)Toolbar toolBar;
    @BindView(R.id.tv_center)
    TextView tvAbarTitle;
    @BindView(R.id.pager)
    ViewPager viewPager;

    @Inject
    AppTypeface appTypeface;
    @Inject WalletTransactionContract.WalletTransactionPresenter walletTransPresenter;

    @BindString(R.string.recentTransactions) String recentTransactions;
    @BindString(R.string.all) String all;
    @BindString(R.string.debit) String debit;
    @BindString(R.string.credit) String credit;
    @BindString(R.string.payment) String payment;
    @BindString(R.string.wait) String wait;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_transactions);
        ButterKnife.bind(this);
        initToolBar();
        initViews();
    }

    /* <h2>initToolBar</h2>
     * <p> method to initialize customer toolbar </p>
     */
    private void initToolBar()
    {
        if(getActionBar() != null)
        {
            getActionBar().hide();
        }
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");


        /*if(Utility.isRTL())
        {
            ImageView ivBackBtn =  findViewById(R.id.iv_custom_toolBarBackBtn);
            ivBackBtn.setRotation((float) 180.0);
        }*/

        toolBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));

        toolBar.setNavigationOnClickListener(view -> onBackPressed());


        tvAbarTitle.setTypeface(appTypeface.getHind_semiBold());
        tvAbarTitle.setText(recentTransactions);
    }

    public void setAllTransactionsAL(ArrayList<CreditDebitTransctions> allTransactionsAL) {
        this.allTransactionsAL = allTransactionsAL;
    //    allTransactionsAL.addAll(allTransactionsALL);
    }

    public void setDebitTransactionsAL(ArrayList<CreditDebitTransctions> debitTransactionsAL) {
        this.debitTransactionsAL = debitTransactionsAL;
      //  this.debitTransactionsAL.addAll(debitTransactionsAL);
    }

    public void setCreditTransactionsAL(ArrayList<CreditDebitTransctions> creditTransactionsAL) {
        this.creditTransactionsAL = creditTransactionsAL;
        //this.creditTransactionsAL.addAll(creditTransactionsAL);
    }

    public void setPaymentTransactionsAL(ArrayList<CreditDebitTransctions> paymentTransactionsAL) {
        this.paymentTransactionsAL = paymentTransactionsAL;
        //this.paymentTransactionsAL.addAll(paymentTransactionsAL);
    }
    /**
     *<h2>initViews</h2>
     * <P> custom method to initializes all the views of the screen </P>
     */
    private void initViews()
    {
        viewPager.setOffscreenPageLimit(4);
        TabLayout tabLayout =  findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        String tabTitles[]  = new String[]{all, debit, credit,payment};
       // WalletViewPagerAdapter viewPagerAdapter = new WalletViewPagerAdapter(getSupportFragmentManager());

        ViewPagerAdapter viewPagerAdapter1 = new ViewPagerAdapter(getSupportFragmentManager());

        this.allTransactionsFrag = WalletTransactionsFragment.getNewInstance();
        viewPagerAdapter1.addFragment(allTransactionsFrag, tabTitles[0]);

        this.debitsFrag = WalletTransactionsFragment.getNewInstance();
        viewPagerAdapter1.addFragment(debitsFrag, tabTitles[1]);

        this.creditsFrag = WalletTransactionsFragment.getNewInstance();
        viewPagerAdapter1.addFragment(creditsFrag, tabTitles[2]);

        this.paymentFrag = WalletTransactionsFragment.getNewInstance();
        viewPagerAdapter1.addFragment(paymentFrag, tabTitles[3]);
        viewPagerAdapter1.notifyDataSetChanged();

        viewPager.setAdapter(viewPagerAdapter1);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        loadTransactions(false);
    }


    /**
     * <h2>loadTransactions</h2>
     * <p> method to init getTransactionsList api </p>
     */
    public void loadTransactions(boolean isFromOnRefresh)
    {
        walletTransPresenter.initLoadTransactions(false, isFromOnRefresh);
    }



    @Override
    public void hideProgressDialog()
    {
        if(pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }



    @Override
    public void showProgressDialog(String msg)
    {
        if(pDialog == null)
        {
            pDialog = new ProgressDialog(this);
        }

        if(!pDialog.isShowing())
        {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage(wait);
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }


    @Override
    public void showToast(String msg, int duration)
    {
        hideProgressDialog();
        Toast.makeText(this, msg, duration).show();
    }


    @Override
    public void showAlert(String title, String msg)
    {
        hideProgressDialog();
    }


    /**
     *<h2>showAlert</h2>
     * <p> method to show alert that these is no internet connectivity there </p>
     */
    @Override
    public void noInternetAlert()
    {
        alerts.showNetworkAlert(this);
    }



    /**
     *<h2>walletTransactionsApiSuccessViewNotifier</h2>
     * <p> method to update fields data on the success response of api </p>
     */
    @Override
    public void walletTransactionsApiSuccessViewNotifier()
    {
        hideProgressDialog();
        String TAG = "WalletTransactionAct";
        Log.d(TAG, "walletTransactionsApiSuccessViewNotifier onSuccess: "+allTransactionsAL.size());

        if(this.allTransactionsFrag != null)
        {
            this.allTransactionsFrag.hideRefreshingLayout();
            this.allTransactionsFrag.notifyDataSetChangedCustom(allTransactionsAL);
        }

        if(this.debitsFrag != null)
        {
            this.debitsFrag.hideRefreshingLayout();
            this.debitsFrag.notifyDataSetChangedCustom(debitTransactionsAL);
        }

        if(this.creditsFrag != null)
        {
            this.creditsFrag.hideRefreshingLayout();
            this.creditsFrag.notifyDataSetChangedCustom(creditTransactionsAL);
        }


        if(this.paymentFrag != null)
        {
            this.paymentFrag.hideRefreshingLayout();
            this.paymentFrag.notifyDataSetChangedCustom(paymentTransactionsAL);
        }
    }


    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.mainfadein, R.anim.slide_down_acvtivity);
    }

}

