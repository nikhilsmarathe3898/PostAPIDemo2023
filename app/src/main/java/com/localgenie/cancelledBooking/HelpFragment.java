package com.localgenie.cancelledBooking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.zendesk.zendeskTicketDetails.HelpIndexTicketDetails;
import com.pojo.HelpReason;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

/**
 * <h>HelpFragment</h>
 * Created by Ali on 3/14/2018.
 */

public class HelpFragment extends DaggerFragment
{

    @BindView(R.id.tvHelpSubj)TextView tvHelpSubj;
    @BindView(R.id.recyclerView)RecyclerView recyclerView;
    ArrayList<HelpReason>helpReasons;
    private long bid;

    private Context mContext;
    @Inject
    public HelpFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.help_fragment, container, false);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("edtText");
             helpReasons = (ArrayList<HelpReason>) getArguments().getSerializable("HelpText");
             bid = getArguments().getLong("BID",0);
            Log.i("TAG", "onCreateView: "+mParam1);
        }
        ButterKnife.bind(this,view);
        mContext = getActivity();
        initializeAdapter();
        return view;
    }

    private void initializeAdapter()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        HelpAdapter helpAdapetr = new HelpAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(helpAdapetr);

    }

    private class HelpAdapter extends RecyclerView.Adapter
    {

        public HelpAdapter() {
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.helptextlist,parent,false);
            return new ViewHolderHelp(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolderHelp hold = (ViewHolderHelp) holder;
            hold.tvHelpSubj.setText(helpReasons.get(position).getName());

        }

        @Override
        public int getItemCount() {
            return helpReasons == null ? 0 : helpReasons.size();
        }


        private class ViewHolderHelp extends RecyclerView.ViewHolder
        {
            TextView tvHelpSubj;
             ViewHolderHelp(View view)
            {
                super(view);
                tvHelpSubj = view.findViewById(R.id.tvHelpSubj);
                tvHelpSubj.setTypeface(AppTypeface.getInstance(mContext).getHind_regular());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, HelpIndexTicketDetails.class);
                        intent.putExtra("BID",bid);
                        intent.putExtra("ISTOAddTICKET",true);
                        intent.putExtra("helpSubJect",helpReasons.get(getAdapterPosition()).getName());
                        startActivity(intent);

                    }
                });
            }
        }
    }
}
