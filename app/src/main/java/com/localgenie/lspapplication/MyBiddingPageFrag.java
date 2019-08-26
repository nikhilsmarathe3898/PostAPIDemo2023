package com.localgenie.lspapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.jobDetailsStatus.JobProviderInfo;
import com.localgenie.utilities.AppTypeface;
import com.pojo.BidDispatchLog;

import java.util.ArrayList;

import adapters.ResponsesAdapter;

/**
 * Created by Ali on 7/9/2018.
 */
public class MyBiddingPageFrag extends Fragment implements JobProviderInfo.JobBidCalling
{
    private static final String TAG = MyBiddingPageFrag.class.getSimpleName();
    private static final String PAGE_COUNT= "pagecount";
    ArrayList<BidDispatchLog> bookingEventsBid = new ArrayList<>();
  //  ArrayList<BidDispatchLog> bookingEventsBidMsg = new ArrayList<>();
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RelativeLayout rlWaitingForResponse;
    private TextView tvWaitingForResponse;
    private  ResponsesAdapter responsesAdapter;
   // public ResponsesAdapterMsg responsesAdapterMsg;
    private JobProviderInfo.JobBidCalling mListener;
    public static MyBiddingPageFrag newInstance(int pageValue)
    {
        Bundle args = new Bundle();
        // args.putSerializable(PENDINGALL_JOBS, bookingEvents);
        args.putInt(PAGE_COUNT,pageValue);
        MyBiddingPageFrag fragment = new MyBiddingPageFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_response, container, false);

        mRecyclerView = layout.findViewById(R.id.mRecyclerView);
        tvWaitingForResponse = layout.findViewById(R.id.tvWaitingForResponse);
        rlWaitingForResponse = layout.findViewById(R.id.rlWaitingForResponse);
        mContext = getActivity();
        tvWaitingForResponse.setTypeface(AppTypeface.getInstance(mContext).getHind_medium());
        initialize();
        return layout;
    }

    private void initialize() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        LinearLayoutManager linearLayoutManagerMsg = new LinearLayoutManager(mContext);
        responsesAdapter = new ResponsesAdapter(mContext,bookingEventsBid,this);
     //   responsesAdapterMsg = new ResponsesAdapterMsg(mContext,bookingEventsBidMsg,this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
      //  mRecyclerView.setLayoutManager(linearLayoutManagerMsg);
        mRecyclerView.setAdapter(responsesAdapter);
      //  mRecyclerView.setAdapter(responsesAdapterMsg);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof JobProviderInfo.JobBidCalling) {
            mListener = (JobProviderInfo.JobBidCalling) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void notifyDataAdapter(ArrayList<BidDispatchLog> responseBid, int type) {
        bookingEventsBid.clear();
        bookingEventsBid.addAll(responseBid);
        responsesAdapter.setType(type);
        if (responseBid.size() > 0) {
            rlWaitingForResponse.setVisibility(View.GONE);
            tvWaitingForResponse.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            rlWaitingForResponse.setVisibility(View.VISIBLE);
            tvWaitingForResponse.setVisibility(View.VISIBLE);
        }
        responsesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onIntentCall(Intent intent) {
        mListener.onIntentCall(intent);
    }

    public void notifyDataAdapterMsg(ArrayList<BidDispatchLog> responseMsg) {
    }
}
