package com.localgenie.faq_detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;

/**
 * <h>WebViewActivity</h>
 * @author Pramod
 * @since 10-03-2018.
 */

public class WebViewActivity extends AppCompatActivity {
    FrameLayout backArrow;
    private WebView webView;
    private ProgressBar progress;
    private String title, url;

    /**
     * <p>Setting content view</p>
     *
     * @param savedInstanceState view saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.web_view);
      //  overridePendingTransition(R.anim.side_slide_out, R.anim.side_slide_in);
        TextView tvSupportTittle = findViewById(R.id.tvSupportTittle);
        AppTypeface typeFace = AppTypeface.getInstance(this);
        String cominFrom = "";
        Intent intent = getIntent();
        /*
        * checking if not null fetching the link and title*/
        if (intent != null) {

            url = getIntent().getStringExtra("Link");
            title = getIntent().getStringExtra("Title");
            cominFrom = getIntent().getStringExtra("COMINFROM");

        }
        Toolbar toolbar = findViewById(R.id.app_toobar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        TextView bar_tittle = findViewById(R.id.tv_center);
        if (getString(R.string.faq).equals(cominFrom)) {
            bar_tittle.setText(getString(R.string.faq));
            tvSupportTittle.setText(title);
            tvSupportTittle.setVisibility(View.VISIBLE);
            tvSupportTittle.setTypeface(typeFace.getHind_semiBold());
        } else {
          //  bar_tittle.setText(title);
            tvSupportTittle.setVisibility(View.GONE);
            bar_tittle.setText(title);
          //  tvSupportTittle.setText(title);
        }



        bar_tittle.setTypeface(typeFace.getHind_semiBold());
      //  toolbar.setNavigationIcon(R.drawable.ic_login_back_icon_off);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        webView = findViewById(R.id.webView1);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setSaveFromParentEnabled(true);

        webView.getSettings().setJavaScriptEnabled(true);
        progress = findViewById(R.id.progressBar_splsh);
        progress.setVisibility(View.GONE);
        webView.loadUrl(url);


        if (validateUrl(url)) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);
        }

    }

    //on back pressed with slide animation
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);
    }

    private boolean validateUrl(String url) {
        return true;
    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progress.setVisibility(View.GONE);
            WebViewActivity.this.progress.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progress.setVisibility(View.VISIBLE);
            WebViewActivity.this.progress.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }
    }

}
