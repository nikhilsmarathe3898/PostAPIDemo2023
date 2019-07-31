package com.localgenie.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.model.Category;
import com.localgenie.utilities.AppTypeface;

import java.util.ArrayList;

import adapters.ServicesAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewAllCategory extends AppCompatActivity {

    @BindView(R.id.recyclerViewAll)RecyclerView recyclerViewAll;
    @BindView(R.id.toolBarViewAll)Toolbar toolBarViewAll;
    @BindView(R.id.tv_center)TextView tv_center;
    private AppTypeface appTypeface;
    private ArrayList<Category> category;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_category);
        ButterKnife.bind(this);
        appTypeface = AppTypeface.getInstance(this);
        category = (ArrayList<Category>) getIntent().getSerializableExtra("CATEGORY");
        title = getIntent().getStringExtra("CATEGORYTITLE");
        initializeToolBar();
    }

    private void initializeToolBar()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        ServicesAdapter servicesAdapter= new ServicesAdapter(category,this, true); // Using same adapter for two places
        recyclerViewAll.setLayoutManager(linearLayoutManager);
        recyclerViewAll.setAdapter(servicesAdapter);
        servicesAdapter.notifyDataSetChanged();
        setSupportActionBar(toolBarViewAll);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv_center.setText(title);
        tv_center.setTypeface(appTypeface.getHind_medium());
        toolBarViewAll.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolBarViewAll.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
    }
}
