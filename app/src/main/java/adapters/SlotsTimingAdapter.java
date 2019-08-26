package adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.confirmbookactivity.ConfirmBookActivity;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;
import com.pojo.Slots;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ali on 10/30/2018.
 */
public class SlotsTimingAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private boolean isProDetails = false;
    private ArrayList<Slots> slotData;

    public SlotsTimingAdapter(Context mContext,boolean isProDetails,ArrayList<Slots> slotData) {
        this.mContext = mContext;
        this.isProDetails = isProDetails;
        this.slotData = slotData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.time_slots_adapter,parent,false);

        return new SlotView(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        SlotView slotHold = (SlotView) holder;
        if(isProDetails)
        {
            timeMethod(slotHold.tvFromTimeSlots,slotData.get(position).getFrom());
            timeMethod(slotHold.tvToTimeSlots,slotData.get(position).getTo());
        }else
        {
            timeMethod(slotHold.tvFromTimeSlotSingle,slotData.get(position).getFrom());
            if(slotData.get(position).getIsBook()==0)
            {
                slotHold.tvFromTimeSlotSingle.setSelected(false);
            }else
                slotHold.tvFromTimeSlotSingle.setSelected(true);
        }

    }

    public void timeMethod(TextView tvTime, long bookingRequestedFor)
    {
        try {

            Date date = new Date(bookingRequestedFor * 1000L);
         //   SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
            sdf.setTimeZone(Utility.getTimeZone());
            String formattedDate = sdf.format(date);
            tvTime.setText(formattedDate);

        } catch (Exception e) {
            Log.d("TAG", "timeMethodException: " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return slotData.size();
    }

    private class SlotView extends RecyclerView.ViewHolder
    {
        TextView tvFromTimeSlotSingle,tvFromTimeSlots,tvToTimeSlots;
        LinearLayout llTimeSlotAdapter;
        public SlotView(View itemView) {
            super(itemView);
            llTimeSlotAdapter = itemView.findViewById(R.id.llTimeSlotAdapter);
            tvFromTimeSlotSingle = itemView.findViewById(R.id.tvFromTimeSlotSingle);
            tvFromTimeSlots = itemView.findViewById(R.id.tvFromTimeSlots);
            tvToTimeSlots = itemView.findViewById(R.id.tvToTimeSlots);
            if(isProDetails)
            {
                llTimeSlotAdapter.setVisibility(View.VISIBLE);
                tvFromTimeSlotSingle.setVisibility(View.GONE);
            }else
            {
                llTimeSlotAdapter.setVisibility(View.GONE);
                tvFromTimeSlotSingle.setVisibility(View.VISIBLE);
            }


            if(!isProDetails)
            {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(tvFromTimeSlotSingle.isSelected())
                        {

                            Intent intent = new Intent(mContext, ConfirmBookActivity.class);
                            Constants.fromTime = slotData.get(getAdapterPosition()).getFrom();
                            Constants.toTime = slotData.get(getAdapterPosition()).getTo();

                            Constants.bookingType = 2;
                            Constants.scheduledDate = slotData.get(getAdapterPosition()).getFrom()+"";
                            Constants.scheduledTime = slotData.get(getAdapterPosition()).getDuration();
                            Constants.selectedDuration= formatHoursAndMinutes(slotData.get(getAdapterPosition()).getDuration());


                            mContext.startActivity(intent);

                        }
                    }
                });
            }


        }
        public  String formatHoursAndMinutes(int totalMinutes) {
            String minutes = Integer.toString(totalMinutes % 60);
            minutes = minutes.length() == 1 ? "0" + minutes : minutes;
            return (totalMinutes / 60) + ":" + minutes;
        }
    }
}
