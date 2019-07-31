package com.localgenie.ProviderSearch;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.localgenie.R;
import com.localgenie.providerList.ProviderList;
import com.pojo.ProviderData;

import java.util.ArrayList;

import adapters.SingleProviderAdapter;

public class ProviderSearchActivity extends AppCompatActivity {

    ArrayList<ProviderData> providerData;
    SingleProviderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_search);
        Toolbar toolbar=findViewById(R.id.tb_toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIntentValue();
        setUpRecyclerView();
    }

    private void getIntentValue() {
        providerData = (ArrayList<ProviderData>) getIntent().getBundleExtra("BUNDLE").getSerializable(ProviderList.PROVIDERLIST);
    }

    public void setUpRecyclerView(){
        RecyclerView rv_searchProviders=findViewById(R.id.rv_searchProviders);
        adapter = new SingleProviderAdapter(this,providerData);
        LinearLayoutManager manager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv_searchProviders.setLayoutManager(manager);
        rv_searchProviders.setAdapter(adapter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

}
