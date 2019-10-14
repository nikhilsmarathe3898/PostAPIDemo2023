package com.localgenie.zendesk.zendeskadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Utility;
import com.localgenie.zendesk.zendeskTicketDetails.HelpIndexTicketDetails;
import com.localgenie.zendesk.zendeskpojo.OpenClose;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <h>HelpIndexAdapter</h>
 * Created by Ali on 2/26/2018.
 */

public class HelpIndexAdapter extends RecyclerView.Adapter
{

    private Context mContext;
    private ArrayList<OpenClose> openCloses;
    @Inject AppTypeface appTypeface;

    private int openSize;

    @Inject
    public HelpIndexAdapter()
    {

    }

    public void onCreateIndex(Activity zendeskHelpIndex, ArrayList<OpenClose> openCloses)
    {
        mContext = zendeskHelpIndex;
        this.openCloses = openCloses;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.help_index_adapter,parent,false);
        return new ViewHolders(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ViewHolders hldr  = (ViewHolders) holder;

        if(openCloses.get(position).isFirst())
        {
            hldr.tvOpenCloseStatus.setVisibility(View.VISIBLE);
            String status = mContext.getString(R.string.status)+" : "+openCloses.get(position).getStatus();
            hldr.tvOpenCloseStatus.setText(status);
        }
        else {
            hldr.tvOpenCloseStatus.setVisibility(View.GONE);
        }
        hldr.tvHelpSubject.setText(openCloses.get(position).getSubject());
        Date date = new Date(openCloses.get(position).getTimeStamp() * 1000L);

        String formattedDate[] = Utility.getFormattedDate(date).split(",");
        hldr.tvHelpDate.setText(formattedDate[0]);
        hldr.tvHelpTime.setText(formattedDate[1]);
        char c = openCloses.get(position).getSubject().charAt(0);
        hldr.tvHelpText.setText(c+"");

        if(openSize>0)
        {
            if(position == openSize-1)
                hldr.idView.setVisibility(View.GONE);
        }
        if(position==(getItemCount()-1)){
            hldr.idView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return  openCloses == null ? 0 : openCloses.size();
    }

    public void openCloseSize(int openSize)
    {
        this.openSize = openSize;
    }

    public class ViewHolders extends RecyclerView.ViewHolder
    {

        @BindView(R.id.idView)View idView;
        @BindView(R.id.tvOpenCloseStatus)TextView tvOpenCloseStatus;
        @BindView(R.id.tvHelpSubject)TextView tvHelpSubject;
        @BindView(R.id.tvHelpTime)TextView tvHelpTime;
        @BindView(R.id.tvHelpDate)TextView tvHelpDate;
        @BindView(R.id.tvHelpText)TextView tvHelpText;
        ViewHolders(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            appTypeface = AppTypeface.getInstance(mContext);
            tvOpenCloseStatus.setTypeface(appTypeface.getHind_semiBold());
            tvHelpSubject.setTypeface(appTypeface.getHind_bold());
            tvHelpTime.setTypeface(appTypeface.getHind_regular());
            tvHelpDate.setTypeface(appTypeface.getHind_regular());
            itemView.setOnClickListener(view -> {

                Intent intent = new Intent(mContext,HelpIndexTicketDetails.class);
                intent.putExtra("ISTOAddTICKET",false);
                intent.putExtra("ZendeskId",openCloses.get(getAdapterPosition()).getId());
                mContext.startActivity(intent);
                Activity activity= (Activity) mContext;
                activity.overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
            });

        }
    }
}
