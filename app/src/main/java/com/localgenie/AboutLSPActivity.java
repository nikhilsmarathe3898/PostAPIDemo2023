package com.localgenie;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.utility.AlertProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Pramod
 * @since 31-01-2018.
 */

public class AboutLSPActivity extends AppCompatActivity {


    @BindView(R.id.tvAppVersion)TextView tvAppVersion;
    @BindView(R.id.tv_rate_store)TextView tv_rate_store;
    @BindView(R.id.tv_like_fb)TextView tv_like_fb;
    @BindView(R.id.website)TextView website;
    private AppTypeface appTypeface;
    private AlertProgress alertProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_lsp);
        ButterKnife.bind(this);
        appTypeface = AppTypeface.getInstance(this);
        alertProgress = new AlertProgress(this);
        initialize();
    }

    private void initialize() {
        Toolbar toolbar =  findViewById(R.id.toolbarLayout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        TextView tvTbTitle=toolbar.findViewById(R.id.tv_center);
        tvTbTitle.setText(R.string.about);
        tvTbTitle.setTypeface(appTypeface.getHind_semiBold());
        tvAppVersion.setText(BuildConfig.VERSION_NAME);

    }

    @OnClick({R.id.website,R.id.tv_rate_store,R.id.tv_like_fb})
    public void onButtonClicked(View v)
    {
        switch (v.getId())
        {
            case R.id.website:
                if (alertProgress.isNetworkAvailable(this))
                {
                    String url = "https://localgenie.online/"+getString(R.string.lsp_website);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }
                else
                {
                    alertProgress.showNetworkAlert(this);
                }
                break;
            case R.id.tv_rate_store:
                if (alertProgress.isNetworkAvailable(this))
                {

                    Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e)
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                    }

                }
                else
                {
                    alertProgress.showNetworkAlert(this);
                }
                break;
            case R.id.tv_like_fb:
                if (alertProgress.isNetworkAvailable(this))
                {
                    String url = "https://www.facebook.com/Service-Genie-418772702301080/";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }
                else
                {
                    alertProgress.showNetworkAlert(this);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}