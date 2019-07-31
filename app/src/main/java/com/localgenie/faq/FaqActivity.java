package com.localgenie.faq;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.faq_detail.FaqDetailActivity;
import com.localgenie.faq_detail.WebViewActivity;
import com.localgenie.model.faq.FaqData;
import com.localgenie.utilities.AppTypeface;

import java.util.List;

import javax.inject.Inject;

import adapters.FaqDetailAdapter;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;


public class FaqActivity extends DaggerAppCompatActivity implements FaqView {

    public final String TAG = "FaqActivity";

    @BindView(R.id.faq_list)
    ListView faq_list;
    @BindView(R.id.tv_center)
    TextView tv_center;

    @BindString(R.string.wait_faq) String wait_faq;

    @Inject
    FaqPresenter presenter;

    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;

    private FaqDetailAdapter faqAdapter;
    @Inject
    AppTypeface appTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);
        initialize();

    }

    private void initialize() {
        Toolbar toolbar = findViewById(R.id.toolbarLayout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv_center.setText(R.string.faq);
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        presenter.getFAQ(1);

    }

    @Override
    public void addItems(List<FaqData> list) {
      //  faqAdapter = new FaqListAdapter(this,list);
        faqAdapter = new FaqDetailAdapter(this);
        faqAdapter.faqDetails(false,null,list);
        faq_list.setAdapter(faqAdapter);

        faq_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FaqData faqData = (FaqData) adapterView.getItemAtPosition(position);
                Log.e("TAG","FaQData :  "+faqData.getSubcat().size());
                if (faqData.getSubcat().size()>0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("faqList", faqData);
                    Intent intent = new Intent(FaqActivity.this, FaqDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
                }
                else if (!faqData.getLink().equals("") && faqData.getLink() != null) {
                    Intent intent = new Intent(FaqActivity.this, WebViewActivity.class);
                    intent.putExtra("Link", faqData.getLink());
                    intent.putExtra("Title", faqData.getName());
                    intent.putExtra("COMINFROM", getResources().getString(R.string.faq));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void showProgress() {
        //progressBar.setVisibility(View.VISIBLE);
        if (!isFinishing()) {
            dialogBuilder = new AlertDialog.Builder(FaqActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
            TextView tv_progress = dialogView.findViewById(R.id.tv_progress);
            tv_progress.setText(wait_faq);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        //progressBar.setVisibility(View.GONE);
        alertDialog.dismiss();
    }


    @Override
    public void setError(String message) {
        Toast.makeText(FaqActivity.this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);
    }
}

