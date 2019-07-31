package com.localgenie.zendesk.zendeskHelpIndex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.zendesk.zendeskTicketDetails.HelpIndexTicketDetails;
import com.localgenie.zendesk.zendeskpojo.OpenClose;
import com.utility.AlertProgress;
import java.util.ArrayList;
import javax.inject.Inject;
import com.localgenie.zendesk.zendeskadapter.HelpIndexAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class ZendeskHelpIndex extends DaggerAppCompatActivity implements ZendeskHelpIndexContract.ZendeskView,SwipeRefreshLayout.OnRefreshListener {

    @Inject AppTypeface appTypeface;
    @Inject SessionManagerImpl manager;
    @Inject AlertProgress alertProgress;
    @Inject HelpIndexAdapter helpIndexAdapter;
    @Inject ZendeskHelpIndexContract.Presenter presenter;

    @BindView(R.id.tool_helpIndex)Toolbar toolHelpIndex;
    @BindView(R.id.rlHelpIndex)RelativeLayout rlHelpIndex;
    @BindView(R.id.recyclerHelpIndex)RecyclerView recyclerHelpIndex;
    @BindView(R.id.progressbarHelpIndex)ProgressBar progressbarHelpIndex;
    @BindView(R.id.srlZenDesk)SwipeRefreshLayout srlZenDesk;

    private ArrayList<OpenClose> openCloses = new ArrayList<>();
    private int openSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_index);
        ButterKnife.bind(this);
        initializeToolBar();
        initializeView();
    }

    private void initializeView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerHelpIndex.setLayoutManager(layoutManager);
        helpIndexAdapter.onCreateIndex(this,openCloses);
        recyclerHelpIndex.setAdapter(helpIndexAdapter);
        onShowProgress();
        presenter.onToGetZendeskTicket();
        onRefreshing(false);
        srlZenDesk.setOnRefreshListener(this);
    }

    /*
    initialize toolBar
     */
    private void initializeToolBar()
    {

        setSupportActionBar(toolHelpIndex);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tvHelpCenter = findViewById(R.id.tv_center);
        TextView tvAddNewTicket = findViewById(R.id.tv_skip);

        getSupportActionBar().setTitle("");
        tvHelpCenter.setText(R.string.helpcenter);
        tvAddNewTicket.setVisibility(View.VISIBLE);
        tvAddNewTicket.setText("+");
        tvAddNewTicket.setTextSize(20);
        tvAddNewTicket.setTextColor(Utility.getColor(this,R.color.parrotGreen));
        tvHelpCenter.setTypeface(appTypeface.getHind_semiBold());
        tvAddNewTicket.setTypeface(appTypeface.getHind_semiBold());
        toolHelpIndex.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolHelpIndex.setNavigationOnClickListener(v -> onBackPressed());

        tvAddNewTicket.setOnClickListener(view -> {
            Intent intent = new Intent(ZendeskHelpIndex.this,HelpIndexTicketDetails.class);
            intent.putExtra("ISTOAddTICKET",true);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toolHelpIndex.setVisibility(View.INVISIBLE);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message) {

    }

    @Override
    public void onError(String error)
    {
        alertProgress.alertPositiveOnclick(this,error, getString(R.string.system_error),getString(R.string.ok),
                isClicked -> Utility.setMAnagerWithBID(ZendeskHelpIndex.this,manager));
    }

    @Override
    public void onShowProgress()
    {
        progressbarHelpIndex.setVisibility(View.VISIBLE);

    }

    @Override
    public void onHideProgress() {
        progressbarHelpIndex.setVisibility(View.GONE);

    }

    @Override
    public void onGetTicketSuccess() {

    }

    @Override
    public void onEmptyTicket()
    {
        rlHelpIndex.setVisibility(View.VISIBLE);
        recyclerHelpIndex.setVisibility(View.GONE);
    }

    @Override
    public void onTicketStatus(OpenClose openClose, int openCloseSize, boolean isOpenClose)
    {
        openCloses.clear();

        if(isOpenClose)
        {
            openSize = 0;
            openSize = openCloseSize;
        }

       /* else
            closeSize = openCloseSize;*/

        helpIndexAdapter.openCloseSize(openSize);
        openCloses.add(openClose);
    }

    @Override
    public void onNotifyData(ArrayList<OpenClose> alOpenClose)
    {
        openCloses = alOpenClose;
        rlHelpIndex.setVisibility(View.GONE);
        recyclerHelpIndex.setVisibility(View.VISIBLE);
        helpIndexAdapter.onCreateIndex(this,openCloses);
        recyclerHelpIndex.setAdapter(helpIndexAdapter);
        helpIndexAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshing(boolean isRefreshing) {
        srlZenDesk.setRefreshing(isRefreshing);
    }

    @Override
    public void onRefresh()
    {
        Log.d("TAG", "onRefresh: "+srlZenDesk.isRefreshing());
       /* if(!srlZenDesk.isRefreshing())
        {*/
            onRefreshing(true);
            presenter.onToGetZendeskTicket();
       // }

    }
}
