package com.localgenie.youraddress;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.add_address.AddAddressActivity;
import com.localgenie.model.youraddress.YourAddrData;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.AddressListAdapter;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;


public class YourAddressActivity extends DaggerAppCompatActivity implements YourAddressView {

    public final String TAG = "YourAddressActivity";

    /*@BindView(R.id.address_list)
    ListView address_list;*/

    @BindView(R.id.recyclerViewAddress) RecyclerView recyclerViewAddress;
    @BindView(R.id.noadressadded) TextView noadressadded;
    @BindView(R.id.rlListAddressEmpty) RelativeLayout rlListAddressEmpty;
    @BindView(R.id.rlListAddress) RelativeLayout rlListAddress;
    @BindView(R.id.tvSavedAddress) TextView tvSavedAddress;
    @BindView(R.id.btn_add_address) TextView btn_add_address;

    @BindString(R.string.noaddressavailable) String no_address_available;

    @BindString(R.string.wait_fetch_addr) String wait_fetch_address;

    @Inject AppTypeface appTypeface;

    @Inject
    YourAddressPresenter presenter;

    @Inject
    SessionManager sessionManager;

    String auth;

    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    @Inject
    AlertProgress alertProgress;
    private AddressListAdapter addressListAdapter;
    private boolean isNotFromAddress = true;
    private ArrayList<YourAddrData> yourAddressData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youraddress);
        ButterKnife.bind(this);
        if(getIntent().getExtras()!=null)
            isNotFromAddress = getIntent().getBooleanExtra("isNotFromAddress",true);

        Log.d(TAG, "onCreateYourAddress: "+isNotFromAddress);
        initialize();

    }

    private void initialize() {
        Toolbar toolbar =  findViewById(R.id.toolbarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textViewCenter =  findViewById(R.id.tv_center);
        // ((TextView) toolbar.findViewById(R.id.tv_center)).setText(R.string.your_addresses);
        textViewCenter.setText(R.string.your_addresses);
        //   ((TextView) toolbar.findViewById(R.id.tv_tb_rightbtn)).setVisibility(View.GONE);

        textViewCenter.setTypeface(appTypeface.getHind_semiBold());
        tvSavedAddress.setTypeface(appTypeface.getHind_medium());
        btn_add_address.setTypeface(appTypeface.getHind_semiBold());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        auth = sessionManager.getAUTH();



    }


    @Override
    public void onAddressSelected(int position) {
        if(!isNotFromAddress)
        {
            Intent intent = new Intent();
            intent.putExtra("AddressLine1",yourAddressData.get(position).getAddLine1());
            intent.putExtra("lat",yourAddressData.get(position).getLatitude());
            intent.putExtra("lng",yourAddressData.get(position).getLongitude());
            intent.putExtra("AddressLine2",yourAddressData.get(position).getAddLine2());
            intent.putExtra("TAGAS",yourAddressData.get(position).getTaggedAs());
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
        }
    }

    @OnClick(R.id.btn_add_address)
    void btn_add_address() {
        Intent intent=new Intent(YourAddressActivity.this, AddAddressActivity.class);
        startActivity(intent);
    }

    /**
     * <h2>addItems</h2>
     * <p>This method is used to Add the address items to the adapter</p>
     * @param list List of items to add
     */
    @Override
    public void addItems(ArrayList<YourAddrData> list)
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        yourAddressData = new ArrayList<>();
        yourAddressData.addAll(list);
        if(yourAddressData.size()>0)
        {
            rlListAddress.setVisibility(View.VISIBLE);
            rlListAddressEmpty.setVisibility(View.GONE);
        }else
        {
            rlListAddress.setVisibility(View.GONE);
            rlListAddressEmpty.setVisibility(View.VISIBLE);
        }
        //    noadressadded.setVisibility(View.GONE);
        addressListAdapter = new AddressListAdapter(this,list,presenter,auth,false);
        recyclerViewAddress.setLayoutManager(linearLayoutManager);
        recyclerViewAddress.setAdapter(addressListAdapter);
    }

    /**
     * <h2>refereshtems</h2>
     * <p>This method is used to refresh the address items from the adapter</p>
     * @param rowItem Address Item for deleting from the adapter
     * @param adapterPosition
     */
    @Override
    public void refreshItems(YourAddrData rowItem, int adapterPosition)
    {
        if (addressListAdapter!=null) {
            addressListAdapter.deleteItem(adapterPosition);
            Log.d(TAG, "refreshItems: "+rowItem.getTaggedAs() +" itemSize "+addressListAdapter.getItemCount());
            if (addressListAdapter.getItemCount()==0) {
                setNoAddressAvailable();
            }
        }
    }

    @Override
    public void showProgress() {
        //progressBar.setVisibility(View.VISIBLE);
        if (!isFinishing()) {
            dialogBuilder = new AlertDialog.Builder(YourAddressActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
            TextView tv_progress = dialogView.findViewById(R.id.tv_progress);
            tv_progress.setText(wait_fetch_address);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            alertDialog = dialogBuilder.create();
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
    public void setNoAddressAvailable() {
        rlListAddress.setVisibility(View.GONE);
        rlListAddressEmpty.setVisibility(View.VISIBLE);
        //    noadressadded.setVisibility(View.VISIBLE);
    }

    @Override
    public void setError(String message) {
        Toast.makeText(YourAddressActivity.this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String message)
    {
        alertProgress.alertinfo(this,message);
    }

    @Override
    public void onLogout(String msg) {

        alertProgress.alertPositiveOnclick(this, msg, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(YourAddressActivity.this,sessionManager);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String auth = sessionManager.getAUTH();
        showProgress();
        presenter.getAddress(auth, YourAddressActivity.this);
    }
}

