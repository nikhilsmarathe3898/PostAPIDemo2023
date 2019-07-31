package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.pojo.ChatData;
import java.util.ArrayList;


/**
 * <h>ChattingAdapter</h>
 * Created by Ali on 12/22/2017.
 */

public class ChattingAdapter extends RecyclerView.Adapter
{
    private Context mContext;
    private ArrayList<ChatData>chatData;
    private String customerId;

    public ChattingAdapter(Context mContext, ArrayList<ChatData> chatData,String customerId)
    {
        this.mContext = mContext;
        this.chatData = chatData;
        this.customerId = customerId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chatting_adapter,parent,false);
        return new ViewHolders(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ViewHolders hldr = (ViewHolders) holder;
      //  if(chatData.get(position).getCustProType()==1)
        if(customerId.equals(chatData.get(position).getFromID()))
        {
            hldr.rlCustMsg.setVisibility(View.VISIBLE);
            hldr.rlProMsg.setVisibility(View.GONE);
            if(chatData.get(position).getType()==1)
            {
                hldr.tvCustMsg.setText(chatData.get(position).getContent());
                hldr.tvCustMsg.setVisibility(View.VISIBLE);
                hldr.ivCustSendPic.setVisibility(View.GONE);
            }

            else
            {
                hldr.tvCustMsg.setVisibility(View.GONE);
                hldr.ivCustSendPic.setVisibility(View.VISIBLE);
                String url = chatData.get(position).getContent();
                if(!url.equals(""))
                {
                    Glide.with(mContext)
                            .load(url)
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10)))
                            .into(hldr.ivCustSendPic);
                }
            }
        }else
        {
            hldr.rlCustMsg.setVisibility(View.GONE);
            hldr.rlProMsg.setVisibility(View.VISIBLE);
            if(chatData.get(position).getType()==1)
            {
                hldr.tvProMsg.setText(chatData.get(position).getContent());
                hldr.tvProMsg.setVisibility(View.VISIBLE);
                hldr.ivProReceivedPic.setVisibility(View.GONE);

            }
            else
            {
                hldr.tvProMsg.setVisibility(View.GONE);
                hldr.ivProReceivedPic.setVisibility(View.VISIBLE);
                String url = chatData.get(position).getContent();
                if(!url.equals(""))
                {
                   /* Picasso.with(mContext)
                            .load(url)
                            .resize((int)hldr.width,(int)hldr.height)
                            .into(hldr.ivProReceivedPic);*/
                    Glide.with(mContext)
                            .load(url)
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10)))
                            .into(hldr.ivProReceivedPic);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    class ViewHolders extends RecyclerView.ViewHolder
    {
        private RelativeLayout rlCustMsg,rlProMsg;
        private ImageView ivChatSendStatus,ivCustSendPic,ivProReceivedPic;
        private TextView tvCustMsg,tvProMsg;
        private AppTypeface appTypeface;
        private double width,height;
        public ViewHolders(View itemView) {
            super(itemView);
            appTypeface = AppTypeface.getInstance(mContext);

            ivChatSendStatus = itemView.findViewById(R.id.ivChatSendStatus);
            ivCustSendPic = itemView.findViewById(R.id.ivCustSendPic);
            ivProReceivedPic = itemView.findViewById(R.id.ivProReceivedPic);
            tvCustMsg = itemView.findViewById(R.id.tvCustMsg);
            tvProMsg = itemView.findViewById(R.id.tvProMsg);
            rlCustMsg = itemView.findViewById(R.id.rlCustMsg);
            rlProMsg = itemView.findViewById(R.id.rlProMsg);
            tvCustMsg.setTypeface(appTypeface.getHind_regular());
            tvProMsg.setTypeface(appTypeface.getHind_regular());

        }
    }
}
