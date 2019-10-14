package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.chatting.ChattingActivity;
import com.localgenie.jobDetailsStatus.JobProviderInfo;
import com.localgenie.providerdetails.ProviderDetails;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.Utility;
import com.pojo.BidDispatchLog;

import java.util.ArrayList;

/**
 * Created by Ali on 8/1/2018.
 */
public class ResponsesAdapterMsg  extends RecyclerView.Adapter {
    Context context;
    // private ArrayList<ResponsePojo> responseList;
    private ArrayList<BidDispatchLog> responseList;
    JobProviderInfo.JobBidCalling jobInfoImpl;

    public ResponsesAdapterMsg(Context context, ArrayList<BidDispatchLog> responseList, JobProviderInfo.JobBidCalling jobInfoImpl) {
        this.context = context;
        this.responseList = responseList;
        this.jobInfoImpl = jobInfoImpl;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.responses_list_item,parent,false);

        return new ResponseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,  int position) {
      ResponseViewHolder mHolder= (ResponseViewHolder) holder;

        mHolder.tvNameResponses.setText(responseList.get(position).getProviderName());

        if("".equals(responseList.get(position).getBidDescription()))
            mHolder.services.setText(responseList.get(position).getCatName());
        else
            mHolder.services.setText(responseList.get(position).getBidDescription());
        Utility.setAmtOnRecept(responseList.get(position).getQuotedPrice(),mHolder.tvPricePerHr, Constants.currencySymbol);

        if(responseList.get(position).isChatAddred())
        {
            mHolder.rlCallMsg.setVisibility(View.GONE);
            mHolder.tvJobDetailsHire.setVisibility(View.VISIBLE);
        }

        if(!"".equals(responseList.get(position).getProfilePic()))
        {
            Glide.with(context)
                    .load(responseList.get(position).getProfilePic())
                    .apply(Utility.createGlideOption(context))
                    .into(mHolder.ivPic);
        }
    }
    @Override
    public int getItemCount() {
        return responseList == null ? 0 : responseList.size();
    }
    class ResponseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPic,callbtn,msgBtn;
        TextView tvNameResponses,services,tvPricePerHr,tvJobDetailsHire;
        RelativeLayout rlCallMsg;
        AppTypeface appTypeface;
        SessionManager manager;
        ResponseViewHolder(View itemView) {
            super(itemView);
            appTypeface = AppTypeface.getInstance(context);
            manager = SessionManager.getInstance(context);
            ivPic=itemView.findViewById(R.id.ivPic);
            tvNameResponses=itemView.findViewById(R.id.tvNameResponses);
            callbtn=itemView.findViewById(R.id.callbtn);
            msgBtn=itemView.findViewById(R.id.msgBtn);
            services=itemView.findViewById(R.id.services);
            tvPricePerHr=itemView.findViewById(R.id.tvPricePerHr);
            tvJobDetailsHire=itemView.findViewById(R.id.tvJobDetailsHire);
            rlCallMsg=itemView.findViewById(R.id.rlCallMsg);
            tvNameResponses.setTypeface(appTypeface.getHind_medium());
            services.setTypeface(appTypeface.getHind_regular());
            tvPricePerHr.setTypeface(appTypeface.getHind_medium());
            tvJobDetailsHire.setTypeface(appTypeface.getHind_semiBold());
            itemView.setOnClickListener(view -> {
                if(responseList.get(getAdapterPosition()).isChat())
                {
                    Intent intent = new Intent(context, ChattingActivity.class);
                    manager.setChatBookingID(responseList.get(getAdapterPosition()).getBid());
                    manager.setChatProId(responseList.get(getAdapterPosition()).getProviderId());
                    manager.setProName(responseList.get(getAdapterPosition()).getFirstName()+" "+responseList.get(getAdapterPosition()).getLastName());
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
                }else
                    callHireIntent(responseList.get(getAdapterPosition()));

            });
            tvJobDetailsHire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callHireIntent(responseList.get(getAdapterPosition()));
                }
            });

            msgBtn.setOnClickListener(view -> {
                Intent intent = new Intent(context, ChattingActivity.class);
                manager.setChatBookingID(responseList.get(getAdapterPosition()).getBid());
                manager.setChatProId(responseList.get(getAdapterPosition()).getProviderId());
                manager.setProName(responseList.get(getAdapterPosition()).getFirstName()+" "+responseList.get(getAdapterPosition()).getLastName());
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
            });
            callbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intentCall = new Intent(Intent.ACTION_DIAL);
                    intentCall.setData(Uri.parse("tel:" + responseList.get(getAdapterPosition()).getPhone()));
                    context.startActivity(intentCall);
                }
            });
        }

        private void callHireIntent(BidDispatchLog bidDispatchLog)
        {
            Intent intent=new Intent(context,ProviderDetails.class);
            intent.putExtra("ProId",bidDispatchLog.getProviderId());
            intent.putExtra("isProFileView",true);
            intent.putExtra("isBidding",true);
            intent.putExtra("bidDesc",bidDispatchLog.getBidDescription());
            intent.putExtra("bidAmount",bidDispatchLog.getQuotedPrice());
            intent.putExtra("BID",bidDispatchLog.getBid());
            jobInfoImpl.onIntentCall(intent);
            // context.startActivity(intent);
        }
    }
}
