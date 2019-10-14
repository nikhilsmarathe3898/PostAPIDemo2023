package adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.pojo.Slot;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by murashid on 28-Sep-17.
 * <h1>ScheduleListAdapter</h1>
 * Shedule Recycler view adapter for displaying the list of schedule in ScheduleViewData Frament
 * @see
 */

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {
    private ArrayList<Slot> slots;
    private Activity mActivity;

    private Typeface fontRegular,fontMedium;

    public ScheduleListAdapter(Activity context, ArrayList<Slot> slots) {
        this.slots = slots;
        this.mActivity = context;
       // fontRegular = Utility.getFontRegular(context);
      //  fontMedium = Utility.getFontMedium(context);
    }

    class ViewHolder extends  RecyclerView.ViewHolder
    {
        private AppTypeface appTypeface;

        TextView tvEventCustomerName, tvBookingTime, tvStartHour,tvStartPeriod, tvBookingEndHour, tvBookingEndPeriod, tvTo;
        LinearLayout llSchedule,llBooked, llScheduleBookingEndTime;
        ViewHolder(View itemView)
        {
            super(itemView);
            appTypeface = AppTypeface.getInstance(mActivity);
            tvStartHour= itemView.findViewById(R.id.tvStartHour);
            tvStartPeriod= itemView.findViewById(R.id.tvStartPeriod);
            tvBookingEndHour = itemView.findViewById(R.id.tvBookingEndHour);
            tvBookingEndPeriod = itemView.findViewById(R.id.tvBookingEndPeriod);
            tvTo= itemView.findViewById(R.id.tvTo);
            tvEventCustomerName = itemView.findViewById(R.id.tvEventCustomerName);
            tvBookingTime= itemView.findViewById(R.id.tvBookingTime);
            llSchedule = itemView.findViewById(R.id.llSchedule);
            llScheduleBookingEndTime = itemView.findViewById(R.id.llScheduleBookingEndTime);
            llBooked= itemView.findViewById(R.id.llBooked);

            tvStartHour.setTypeface(appTypeface.getHind_regular());
            tvStartPeriod.setTypeface(appTypeface.getHind_regular());
            tvBookingEndHour.setTypeface(appTypeface.getHind_regular());
            tvBookingEndPeriod.setTypeface(appTypeface.getHind_regular());
            tvTo.setTypeface(appTypeface.getHind_regular());
            tvEventCustomerName.setTypeface(appTypeface.getHind_medium());
            tvBookingTime.setTypeface(appTypeface.getHind_medium());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.single_row_schedule_calendar,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        try
        {
            holder.tvStartHour.setText(slots.get(position).getSlotHour());
            holder.tvStartPeriod.setText(slots.get(position).getSlotPeriod());
            if(slots.get(position).getCutomerName() != null)
            {
                holder.tvTo.setVisibility(View.VISIBLE);
                holder.llScheduleBookingEndTime.setVisibility(View.VISIBLE);
                Log.d(slots.get(position).getStatus(), "onBindViewHolder: ");
                if(slots.get(position).getStatus().equals(Constants.JOB_TIMER_INCOMPLETE)
                        || slots.get(position).getStatus().equals(Constants.JOB_COMPLETED_RAISE_INVOICE))
                {
                  //  holder.llBooked.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.customRed));

                    holder.llBooked.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.white));

                }
                else
                {
                    holder.llBooked.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.white));

                 //   holder.llBooked.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.customGreen));
                }
                holder.tvBookingEndHour.setText(slots.get(position).getSlotEndHourBooking());
                holder.tvBookingEndPeriod.setText(slots.get(position).getSlotEndPeriodBooking());
              //  holder.tvEventCustomerName.setText(slots.get(position).getEvent() +" - "+ slots.get(position).getCutomerName());
              //  holder.tvBookingTime.setText("( "+slots.get(position).getBookedStartHour()+" - "+slots.get(position).getBookedEndHour()+" )");
              //  holder.llSchedule.setOnClickListener(this);

                holder.tvEventCustomerName.setText("");
                holder.tvBookingTime.setText("");
                holder.llSchedule.setTag(holder);
            }
            else
            {
                holder.tvTo.setVisibility(View.GONE);
                holder.llScheduleBookingEndTime.setVisibility(View.GONE);
                holder.llBooked.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.white));
                holder.tvEventCustomerName.setText("");
                holder.tvBookingTime.setText("");

               // holder.llSchedule.setOnClickListener(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return slots == null ? 0 : slots.size();
    }

    /*@Override
    public void onClick(View view) {
        if(view.getId() == R.id.llSchedule )
        {
            ViewHolder holder = (ViewHolder) view.getTag();
            int position = holder.getAdapterPosition();

            String bookingId = slots.get(position).getBookingId();
            Intent intent;
            if(slots.get(position).getStatus().equals(VariableConstant.JOB_COMPLETED_RAISE_INVOICE))
            {
                intent = new Intent(mActivity, HistoryInvoiceActivity.class);
                intent.putExtra("isFromSchedule", true);
            }
            else
            {
                intent = new Intent(mActivity, BookingScheduleActivity.class);
            }

            intent.putExtra("bookingId", bookingId);
            mActivity.startActivity(intent);
            mActivity.overridePendingTransition(R.anim.bottom_to_top,R.anim.stay);
        }
    }*/

}

