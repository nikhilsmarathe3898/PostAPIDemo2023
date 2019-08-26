package adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.chatting.ChattingActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.Utility;
import com.pojo.BookingChatHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ali on 5/28/2018.
 */
public class ChatBookingsPageAdapter extends RecyclerView.Adapter
{

    private Context mContext;
    ArrayList<BookingChatHistory> bookingChatHistories;
    public ChatBookingsPageAdapter(Context mContext, ArrayList<BookingChatHistory> bookingChatHistories)
    {
        this.mContext = mContext;
        this.bookingChatHistories = bookingChatHistories;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_row_chat_customer_list,parent,false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ChatViewHolder hold = (ChatViewHolder) holder;
        if(!"".equals(bookingChatHistories.get(position).getProfilePic()))
        {
            Glide.with(mContext)
                    .load(bookingChatHistories.get(position).getProfilePic())
                    .apply(Utility.createGlideOption(mContext))
                    .into(hold.ivCustomer);
        }
        if(bookingChatHistories.size()-1==position)
            hold.view1.setVisibility(View.GONE);

        String name = bookingChatHistories.get(position).getFirstName() + " " + bookingChatHistories.get(position).getLastName();
        hold.tvCustomerName.setText(name);
        hold.tvJobDetails.setText(bookingChatHistories.get(position).getCatName());
        if(bookingChatHistories.get(position).getLastCahtMsgTimeStamp()>0)
            timeMethod(hold.tvDate,hold.tvTime, bookingChatHistories.get(position).getLastCahtMsgTimeStamp());
    }

    private void timeMethod(TextView tvDate, TextView tvTime, long bookingRequestedFor) {
        try {


            Log.d("TAGTIME", " expireTime " + bookingRequestedFor);
            Date date = new Date(bookingRequestedFor * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
            sdf.setTimeZone(Utility.getTimeZone());
            String formattedDate = sdf.format(date);
            String splitDate[] =formattedDate.split(" ");
            Log.d("TAG", "timeMethod: "+formattedDate);
            tvDate.setText(splitDate[0]);
            String time = splitDate[1]+" "+splitDate[2];
            tvTime.setText(time);

        } catch (Exception e) {
            Log.d("TAG", "timeMethodException: " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return bookingChatHistories.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView ivCustomer;
        private TextView tvCustomerName,tvJobDetails,tvDate,tvTime,tvCount;
        private View view1;
        private AppTypeface appTypeface;
        private SessionManager manager;
        public ChatViewHolder(View itemView) {
            super(itemView);
            appTypeface = AppTypeface.getInstance(mContext);
            manager = SessionManager.getInstance(mContext);
            ivCustomer = itemView.findViewById(R.id.ivCustomer);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvJobDetails = itemView.findViewById(R.id.tvJobDetails);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCount = itemView.findViewById(R.id.tvCount);
            view1 = itemView.findViewById(R.id.view1);
            tvCustomerName.setTypeface(appTypeface.getHind_semiBold());
            tvJobDetails.setTypeface(appTypeface.getHind_regular());
            tvDate.setTypeface(appTypeface.getHind_semiBold());
            tvTime.setTypeface(appTypeface.getHind_regular());
            tvCount.setTypeface(appTypeface.getHind_regular());
            itemView.setOnClickListener(view -> {
                BookingChatHistory chat =  bookingChatHistories.get(getAdapterPosition());
                manager.setChatBookingID(chat.getBookingId());
                manager.setChatProId(chat.getProviderId());
                manager.setProName(chat.getFirstName()+" "+chat.getLastName());
                Intent intent = new Intent(mContext, ChattingActivity.class);
                if(chat.getStatus() == 1 || chat.getStatus() == 2
                        || chat.getStatus() == 3 || chat.getStatus() == 6 || chat.getStatus() == 7
                        || chat.getStatus() == 8 || chat.getStatus() == 9 || chat.getStatus() == 17)
                {
                    intent.putExtra("isChating",true);
                }
                intent.putExtra("STATUSCODE",chat.getStatus());
                intent.putExtra("CurrencySymbol",chat.getCurrencySymbol());
                intent.putExtra("AMOUNT",chat.getAmount());
                intent.putExtra("CallType",chat.getCallType());
                mContext.startActivity(intent);
            });
        }
    }
}
