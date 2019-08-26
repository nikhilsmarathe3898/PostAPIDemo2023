package com.localgenie.faq_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.model.faq.FaqData;
import com.localgenie.model.faq.Subcat;
import com.localgenie.utilities.AppTypeface;

import java.util.List;

import adapters.FaqDetailAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Pramod
 * @since 26-02-2018.
 */

public class FaqDetailActivity extends AppCompatActivity {

    public final String TAG = "FaqDetailActivity";

    @BindView(R.id.faq_list)
    ListView faq_list;

    List<Subcat> subcatList;

    private AppTypeface appTypeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);
        appTypeface = AppTypeface.getInstance(this);
        initialize();


    }

    private void initialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLayout);
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
        tvTbTitle.setText(R.string.faq);
        tvTbTitle.setTypeface(appTypeface.getHind_semiBold());
        Bundle bundle = this.getIntent().getExtras();
        if (bundle!=null) {
            FaqData faqData = (FaqData) bundle.getSerializable("faqList");
            if (faqData!=null) {
                subcatList = faqData.getSubcat();
            }
        }

        if (subcatList.size()>0) {
            FaqDetailAdapter faqAdapter = new FaqDetailAdapter(this);
            faqAdapter.faqDetails(true,subcatList,null);
            faq_list.setAdapter(faqAdapter);
            faqAdapter.notifyDataSetChanged();
        }

        faq_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (subcatList.size() > 0) {
                    Intent intent = new Intent(FaqDetailActivity.this, WebViewActivity.class);
                    intent.putExtra("Link", subcatList.get(position).getLink());
                    intent.putExtra("Title", subcatList.get(position).getName());
                    intent.putExtra("COMINFROM", getResources().getString(R.string.faq));
                    startActivity(intent);
                } else if (!subcatList.get(position).getLink().equals("") && subcatList.get(position).getLink() != null) {
                    Intent intent = new Intent(FaqDetailActivity.this, WebViewActivity.class);
                    intent.putExtra("Link", subcatList.get(position).getLink());
                    intent.putExtra("Title", subcatList.get(position).getName());
                    intent.putExtra("COMINFROM", getResources().getString(R.string.faq));
                    startActivity(intent);

                }
                overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);
    }
}