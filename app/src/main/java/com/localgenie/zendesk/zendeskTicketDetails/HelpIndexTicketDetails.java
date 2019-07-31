package com.localgenie.zendesk.zendeskTicketDetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.zendesk.zendeskadapter.HelpIndexRecyclerAdapter;
import com.localgenie.zendesk.zendeskadapter.SpinnerAdapter;
import com.localgenie.zendesk.zendeskpojo.SpinnerRowItem;
import com.localgenie.zendesk.zendeskpojo.ZendeskDataEvent;
import com.utility.AlertProgress;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * <h>HelpIndexTicketDetails</h>
 * Created by Ali on 2/26/2018.
 */

public class HelpIndexTicketDetails extends DaggerAppCompatActivity implements HelpIndexContract.HelpView
{
    public static final Integer[] priorityColor = { R.color.green_continue ,
            R.color.livemblue3498,R.color.red_login_dark,
            R.color.saffron,};
    //private String[] priorityTitles;
    @Inject AppTypeface appTypeface;
    @Inject SessionManagerImpl sharedPrefs;
    @Inject AlertProgress alertProgress;
    @Inject HelpIndexContract.presenter  presenter;
    @Inject HelpIndexRecyclerAdapter helpIndexRecyclerAdapter;

    @BindView(R.id.tool_helpindex_ticket)Toolbar toolBarLayout;
    @BindView(R.id.tv_center)TextView tv_center;
    @BindArray(R.array.ticketPriority)String[] priorityTitles;
    @BindView(R.id.etHelpIndexSubjectPre)EditText etHelpIndexSubjectPre;
    @BindView(R.id.etWriteMsg)EditText etWriteMsg;
    @BindView(R.id.etHelpIndexSubject)EditText etHelpIndexSubject;
    @BindView(R.id.tvHelpIndexDateNTimePre)TextView tvHelpIndexDateNTimePre;
    @BindView(R.id.spinnerHelpIndexPre)TextView spinnerHelpIndexPre;
    @BindView(R.id.tvHelpIndexImageText)TextView tvHelpIndexImageText;
    @BindView(R.id.tvHelpIndexCustName)TextView tvHelpIndexCustName;
    @BindView(R.id.tvHelpIndexDateNTime)TextView tvHelpIndexDateNTime;
    @BindView(R.id.ivHelpIndexImage)ImageView ivHelpIndexImage;
    @BindView(R.id.cardHelpIndexTicket) CardView cardHelpIndexTicket;
    @BindView(R.id.ivHelpCenterPriority) ImageView ivHelpCenterPriority;
    @BindView(R.id.spinnerHelpIndex) Spinner spinnerHelpIndex;

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.cardHelpIndexTicketPre) CardView cardHelpIndexTicketPre;
    @BindView(R.id.tvHelpIndexImageTextPre) TextView tvHelpIndexImageTextPre;
    @BindView(R.id.ivHelpIndexImagePre) ImageView ivHelpIndexImagePre;
    @BindView(R.id.ivHelpCenterPriorityPre) ImageView ivHelpCenterPriorityPre;
    @BindView(R.id.tvHelpIndexCustNamePre) TextView tvHelpIndexCustNamePre;
    @BindView(R.id.tvHelpIndexSend) TextView tvHelpIndexSend;
    @BindView(R.id.rlTextInput) RelativeLayout rlTextInput;



    private String subject,priority;
    private int zenId;
    private ArrayList<SpinnerRowItem> rowItems;
    private boolean isToAddTicket;
    private ArrayList<ZendeskDataEvent> zendeskDataEvents = new ArrayList<>();
    private long bid = 0;
    private String helpSubject = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_help_index_ticket);
        ButterKnife.bind(this);
        isToAddTicket = getIntent().getBooleanExtra("ISTOAddTICKET",false);
        bid = getIntent().getLongExtra("BID",0);
        helpSubject = getIntent().getStringExtra("helpSubJect");
        if(getIntent().getExtras()!=null)
        {
            zenId = getIntent().getIntExtra("ZendeskId",0);
        }
        initializeArrayList();
        initializeToolBar();
        initializeView();

    }

    private void initializeArrayList() {
        rowItems = new ArrayList<>();
        rowItems.add(new SpinnerRowItem(priorityColor[0],priorityTitles[0]));
        rowItems.add(new SpinnerRowItem(priorityColor[1],priorityTitles[1]));
        rowItems.add(new SpinnerRowItem(priorityColor[2],priorityTitles[2]));
        rowItems.add(new SpinnerRowItem(priorityColor[3],priorityTitles[3]));
    }

    private void initializeView()
    {
        if(isToAddTicket)
        {
            etHelpIndexSubject.setTypeface(appTypeface.getHind_regular());
            tvHelpIndexDateNTime.setTypeface(appTypeface.getHind_regular());
            tvHelpIndexCustName.setTypeface(appTypeface.getHind_medium());
            tvHelpIndexImageText.setTypeface(appTypeface.getHind_semiBold());
            priority = rowItems.get(0).getPriority();
            SpinnerAdapter adapter = new SpinnerAdapter(this,R.layout.spinner_adapter,R.id.tvSpinnerPriority, rowItems);
            spinnerHelpIndex.setAdapter(adapter);

            spinnerHelpIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("TAG", "onItemSelected: "+i);
                    priority = rowItems.get(i).getPriority();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            cardHelpIndexTicket.setVisibility(View.VISIBLE);
            String name = sharedPrefs.getFirstName()+" "+sharedPrefs.getLastName();
            tvHelpIndexCustName.setText(name);
            char c = name.charAt(0);
            tvHelpIndexImageText.setText(c+"");
            Date date = new Date(System.currentTimeMillis());
            String dateTime[] = Utility.getFormattedDate(date).split(",");
            String timeToSet =  dateTime[0]+" | "+dateTime[1];
            tvHelpIndexDateNTime.setText(timeToSet);
            if(helpSubject!=null)
            {
                String subject = helpSubject +" - Booking Id: "+bid;
                etHelpIndexSubject.setText(subject);
            }

        }else
        {
            etHelpIndexSubjectPre.setEnabled(false);
            spinnerHelpIndexPre.setTypeface(appTypeface.getHind_regular());
            tvHelpIndexCustNamePre.setTypeface(appTypeface.getHind_medium());
            tvHelpIndexImageTextPre.setTypeface(appTypeface.getHind_semiBold());
            etHelpIndexSubjectPre.setTypeface(appTypeface.getHind_regular());
            tvHelpIndexDateNTimePre.setTypeface(appTypeface.getHind_regular());
            cardHelpIndexTicketPre.setVisibility(View.VISIBLE);
            String name = sharedPrefs.getFirstName()+" "+sharedPrefs.getLastName();
            tvHelpIndexCustNamePre.setText(name);
            char c = name.charAt(0);
            tvHelpIndexImageTextPre.setText(c+"");
            onShowProgress();
            presenter.callApiToGetTicketInfo(zenId);
        }

        helpIndexRecyclerAdapter.onHelpIndexRecyclerAdapter(this,zendeskDataEvents);
        RecyclerView recyclerViewHelpIndex = findViewById(R.id.recyclerViewHelpIndex);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewHelpIndex.setLayoutManager(linearLayoutManager);
        recyclerViewHelpIndex.setAdapter(helpIndexRecyclerAdapter);
        etWriteMsg = findViewById(R.id.etWriteMsg);

        etWriteMsg.setTypeface(appTypeface.getHind_regular());
        tvHelpIndexSend.setTypeface(appTypeface.getHind_medium());
        recyclerViewHelpIndex.setNestedScrollingEnabled(false);

    }

    @OnClick(R.id.tvHelpIndexSend)
    public void msgSendText()
    {
        String trim = etWriteMsg.getText().toString().trim();
        if(!trim.isEmpty())
        {
            if(isToAddTicket)
            {
                subject = etHelpIndexSubject.getText().toString().trim();
                if(!subject.isEmpty())
                {
                    presenter.callApiToCreateTicket(trim,subject,priority);
                    setAndNotifyAdapter(sharedPrefs.getFirstName()+" "+sharedPrefs.getLastName(),trim);
                    etHelpIndexSubject.setEnabled(false);
                    isToAddTicket = false;
                    Utility.hideKeyboard(HelpIndexTicketDetails.this);
                }
                else
                    Toast.makeText(HelpIndexTicketDetails.this,"Please add subject",Toast.LENGTH_SHORT).show();
                etWriteMsg.setText("");

            }
            else
            {
                presenter.callApiToCommentOnTicket(trim,zenId);
                etWriteMsg.setText("");
                setAndNotifyAdapter(sharedPrefs.getFirstName()+" "+sharedPrefs.getLastName(),trim);
                Utility.hideKeyboard(HelpIndexTicketDetails.this);
            }

        }
    }


    private void setAndNotifyAdapter(String name, String trim)
    {
        long timeStsmp = System.currentTimeMillis()/1000;
        ZendeskDataEvent dataEvent = new ZendeskDataEvent();
        dataEvent.setBody(trim);
        dataEvent.setName(name);
        dataEvent.setTimeStamp(timeStsmp);
        zendeskDataEvents.add(dataEvent);
        helpIndexRecyclerAdapter.notifyDataSetChanged();
    }


    /*
    initialize toolBar
     */
    private void initializeToolBar()
    {
        setSupportActionBar(toolBarLayout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tv_center.setTypeface(appTypeface.getHind_semiBold());
        toolBarLayout.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolBarLayout.setNavigationOnClickListener(v -> onBackPressed());
        if(isToAddTicket)
            tv_center.setText(R.string.newTicket);
        else
            tv_center.setText(R.string.ticket);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.mainfadein, R.anim.slide_down_acvtivity);

    }

    @Override
    public void onShowProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgress() {
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onZendeskTicketAdded(String msg)
    {
        onBackPressed();
    }

    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message) {

    }

    @Override
    public void onError(String errMsg) {
        onHideProgress();
        alertProgress.alertinfo(this,errMsg);
    }

    @Override
    public void onTicketInfoSuccess(ArrayList<ZendeskDataEvent> events, String timeToSet, String subject, String priority, String type) {

        zendeskDataEvents.addAll(events);
        helpIndexRecyclerAdapter.notifyDataSetChanged();
        etHelpIndexSubjectPre.setText(subject);
        tvHelpIndexDateNTimePre.setText(timeToSet);
        this.subject = subject;
        this.priority = priority;
        spinnerHelpIndexPre.setText(priority);
        presenter.onPriorityImage(this,priority,ivHelpCenterPriorityPre);
        if(!"open".equalsIgnoreCase(type))
        {
            rlTextInput.setVisibility(View.GONE);
        }
    }
}
