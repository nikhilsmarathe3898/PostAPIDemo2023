package com.localgenie.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.localgenie.R;
import com.localgenie.model.Category;
import com.localgenie.utilities.AppTypeface;

import java.util.ArrayList;

import adapters.ServicesAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ali on 4/4/2018.
 */
public class SearchFilter extends AppCompatActivity
{
    @BindView(R.id.recyclerView)RecyclerView recyclerView;
    @BindView(R.id.ivSearch)ImageView ivSearch;
    @BindView(R.id.etSearch)EditText etSearch;
    @BindView(R.id.ivSearchClear)ImageView ivSearchClear;
    @BindView(R.id.progressBarShow)ProgressBar progressBarShow;
    private ArrayList<Category> categoryList;
    private ArrayList<Category> categoryListFilter = new ArrayList<>();
    private ServicesAdapter servicesAdapter;
    AppTypeface appTypeface;
    private boolean isEditStop;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list_adapter);
        ButterKnife.bind(this);
        appTypeface = AppTypeface.getInstance(this);
        getIntentValue();
        setTypeFaceValue();
    }

    private void getIntentValue() {

        categoryList = (ArrayList<Category>) getIntent().getSerializableExtra("CategoryList");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        servicesAdapter = new ServicesAdapter(categoryListFilter,this,true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(servicesAdapter);
    }

    private void setTypeFaceValue() {
        etSearch.setTypeface(appTypeface.getHind_medium());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEditStop = false;
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                Log.d("TAG", "afterTextChanged: "+editable.toString() +" isEdit "+isEditStop);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        isEditStop = true;
                        Log.d("TAG", "afterTextChanged: "+editable.toString()
                                +" isEditable "+isEditStop);
                        filterMethod(editable.toString());
                        Log.d("TAG", "afterTextChanged: "+editable.toString());

                    }
                },1000);

            }
        });
    }

    private void filterMethod(String s) 
    {
        if(isEditStop)
        {
            showProgress();
            filter(s);
        }
    }

    private void showProgress()
    {
        progressBarShow.setVisibility(View.VISIBLE);
    }


    private void filter(String s)
    {
       // ArrayList<String> catName = new ArrayList<>();

        categoryListFilter.clear();
        servicesAdapter.notifyDataSetChanged();

        for(int i=0;i<categoryList.size();i++)
        {
          /* String catNam = categoryList.get(i).getCatName().toLowerCase();
           catName.add(catNam);*/
           // str1.toLowerCase().contains(str2.toLowerCase())
            if(categoryList.get(i).getCatName().toLowerCase().contains(s.toLowerCase()))
            {
                categoryListFilter.add(categoryList.get(i));
                servicesAdapter.notifyDataSetChanged();
            }

        }
        progressBarShow.setVisibility(View.GONE);
    }

    @OnClick({R.id.ivSearch,R.id.ivSearchClear})
    public void clicked(View v)
    {
        if(v.getId()==R.id.ivSearch)
        {
            onBackPressed();
        }else{
            categoryListFilter.clear();
            servicesAdapter.notifyDataSetChanged();
            etSearch.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
