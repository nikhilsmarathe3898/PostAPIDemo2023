package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.localgenie.R;
import com.localgenie.cancelledBooking.CancelledBooking;
import com.localgenie.jobDetailsStatus.JobDetailsActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.CircleTransform;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.pojo.AllBookingEventPojo;

/**
 * <H>MyBookingsPageAdapter</H>
 * Created by Ali on 2/12/2018.
 */

public class MyBookingsPageAdapter extends RecyclerView.Adapter{


    private Context mContext;
    private Activity mActivity;
    private ArrayList<AllBookingEventPojo> allEvents;
    private boolean isActivityCalled = true;

    public MyBookingsPageAdapter(Context mContext, ArrayList<AllBookingEventPojo> allEventsList) {
        this.mContext = mContext;
        allEvents = allEventsList;
        mActivity = (Activity) mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_booking_page_adapter, parent, false);
        return new ViewHolderHold(view);
    }

    public void isActivityCalled(boolean isCalled)
    {
        isActivityCalled = isCalled;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ViewHolderHold holderHold = (ViewHolderHold) holder;

        holderHold.tvMyEvnetProStatus.setText(allEvents.get(position).getAddLine1());
        holderHold.tvStatus.setText(allEvents.get(position).getStatusMsg());
        timeMethod(holderHold.tvMyEvnetDateNTime, allEvents.get(position).getBookingRequestedFor());

        holderHold.tvBookingCategory.setText(allEvents.get(position).getCategory());

        if(Constants.jobType == 3)
        {
            holderHold.tvMyEvnetProStatus.setVisibility(View.GONE);
        }

        if(allEvents.get(position).getTotalAmount()>0)
        {
            String totalAmt = allEvents.get(position).getCurrencySymbol()+" "+allEvents
                    .get(position).getTotalAmount();
            holderHold.tvBookingAmount.setText(totalAmt);

        }
        switch (allEvents.get(position).getStatus())
        {
            case 1:
            case 2:
                break;
            case 3:
            case 6:
            case 7:
            case 8:
            case 9:
                setPicInToImageView(position,holderHold);
                break;
            case 4:
                holderHold.tvStatus.setTextColor(Utility.getColor(mContext, R.color.red_login_dark));
                holderHold.tvAsap.setText("------");
                break;
            case 5:
                holderHold.tvStatus.setTextColor(Utility.getColor(mContext, R.color.saffron));
                holderHold.tvAsap.setText("------");
                break;
            case 10:
                holderHold.tvStatus.setTextColor(Utility.getColor(mContext, R.color.parrotGreen));
                setPicInToImageView(position,holderHold);
            case 11:
                holderHold.tvStatus.setTextColor(Utility.getColor(mContext, R.color.blue_facebook));
                setPicInToImageView(position,holderHold);
                break;
            case 12:
                holderHold.tvStatus.setTextColor(Utility.getColor(mContext, R.color.blue_facebook));
                setPicInToImageView(position,holderHold);
                break;
        }

    }

    private void setPicInToImageView(int position, ViewHolderHold holderHold)
    {

        if(!allEvents.get(position).getFirstName().equals(""))
        {
            String name = allEvents.get(position).getFirstName() +" "+allEvents.get(position).getLastName();
            holderHold.tvMyEventProName.setText(name);
        }
        holderHold.llProfileAccepted.setVisibility(View.VISIBLE);
        holderHold.tvAsap.setVisibility(View.GONE);
        try {
            if(!allEvents.get(position).getProfilePic().equals(""))
            {
                holderHold.ivMyEventProPic.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(allEvents.get(position).getProfilePic())
                        .apply(holderHold.options)
                        .into(holderHold.ivMyEventProPic);
            }else
            {
                holderHold.ivMyEventProPic.setVisibility(View.GONE);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void timeMethod(TextView tvMyEvnetDateNTime, long bookingRequestedFor)
    {

        try {

            Date date = new Date(bookingRequestedFor * 1000L);
            tvMyEvnetDateNTime.setText(Utility.getFormattedDate(date));
        } catch (Exception e) {
            Log.d("TAG", "timeMethodException: " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return allEvents.size();
    }

    class ViewHolderHold extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        AppTypeface appTypeface;
        @BindView(R.id.ivMyEventProPic)ImageView ivMyEventProPic;
        @BindView(R.id.tvMyEventProName)TextView tvMyEventProName;
        @BindView(R.id.tvBookingAmount)TextView tvBookingAmount;
        @BindView(R.id.tvMyEvnetDateNTime)TextView tvMyEvnetDateNTime;
        @BindView(R.id.tvMyEvnetProStatus)TextView tvMyEvnetProStatus;
        @BindView(R.id.tvAsap)TextView tvAsap;
        @BindView(R.id.tvStatus)TextView tvStatus;
        @BindView(R.id.tvBookingCategory)TextView tvBookingCategory;
        @BindView(R.id.llProfileAccepted)LinearLayout llProfileAccepted;

        private RequestOptions options;
        public ViewHolderHold(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
            options = new RequestOptions()
                    .transform(new CircleTransform(mContext))
                    .priority(Priority.HIGH)
                    .placeholder(R.drawable.profile_price_bg)
                    .error(R.drawable.profile_price_bg);

            appTypeface = AppTypeface.getInstance(mContext);
            tvMyEventProName.setTypeface(appTypeface.getHind_regular());
            tvBookingAmount.setTypeface(appTypeface.getHind_semiBold());
            tvMyEvnetDateNTime.setTypeface(appTypeface.getHind_regular());
            tvMyEvnetProStatus.setTypeface(appTypeface.getHind_regular());
            tvAsap.setTypeface(appTypeface.getHind_regular());
            tvStatus.setTypeface(appTypeface.getHind_regular());
            tvBookingCategory.setTypeface(appTypeface.getHind_semiBold());

        }

        @Override
        public void onClick(View view)
        {
            Log.d("TAG", "onClick: " + allEvents.get(getAdapterPosition()).getStatus());
            AllBookingEventPojo allBookingEventPojo = allEvents.get(getAdapterPosition());
            ArrayList<AllBookingEventPojo> alPojo = new ArrayList<>();
            alPojo.add(allBookingEventPojo);
            Intent intent;
            switch (allEvents.get(getAdapterPosition()).getStatus()) {
                case 1:
                case 2:
                case 3:
                case 6:
                case 7:
                case 8:
                case 9:
               /* case 15:*/
                case 17:
                    if(isActivityCalled)
                    {
                        intent = new Intent(mContext, JobDetailsActivity.class);
                        callNextActivity(intent,alPojo,false);
                    }

                    break;

                   /* intent = new Intent(mContext, CompletedInvoiceInfo.class);
                    callNextActivity(intent,alPojo,true);*/
                case 5:
                case 4:
                case 11:
                case 10:
                case 12:
                    if(isActivityCalled)
                    {
                        intent = new Intent(mContext, CancelledBooking.class);
                        callNextActivity(intent,alPojo,true);
                    }

                    break;
            }
        }

        private void callNextActivity(Intent intent, ArrayList<AllBookingEventPojo> allBookingEventPojo, boolean b) {

            Constants.bookingcurrencySymbol = allBookingEventPojo.get(0).getCurrencySymbol();
            intent.putExtra("BID", allEvents.get(getAdapterPosition()).getBookingId());
            intent.putExtra("STATUS", allEvents.get(getAdapterPosition()).getStatus());
            intent.putExtra("BookingModel", allEvents.get(getAdapterPosition()).getBookingModel());
            intent.putExtra("CallType",allEvents.get(getAdapterPosition()).getCallType());
            if(b)
            {
                intent.putExtra("ALLPOJO", allBookingEventPojo);
                intent.putExtra("ImageUrl", allEvents.get(getAdapterPosition()).getProfilePic());
            }

            mContext.startActivity(intent);
            isActivityCalled = false;
            mActivity.overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
        }
    }

}
