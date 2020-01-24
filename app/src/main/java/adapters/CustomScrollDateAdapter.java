package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ali on 11/5/2018.
 */
public class CustomScrollDateAdapter extends RecyclerView.Adapter{

    private ArrayList<Long> longArrayList;
    private Context mContext;
    private Calendar today;
    private Calendar tomorrow;

    public CustomScrollDateAdapter(ArrayList<Long> longArrayList, Context mContext) {
        this.longArrayList = longArrayList;
        this.mContext = mContext;
        today = Calendar.getInstance();
        tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.custom_date,parent,false);
        return new ViewDateHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewDateHolder hold = (ViewDateHolder) holder;

        //hold.tvCustomDateTime.setText(""+longArrayList.get(position));
        timeMethod(hold.tvCustomDateTime,longArrayList.get(position));

    }

    public void timeMethod(TextView tvDateTime,long bookingRequestedFor)
    {
        String setDate;
        try {

            Date date = new Date(bookingRequestedFor * 1000L);
            //   SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            sdf.setTimeZone(Utility.getTimeZone());
            String formattedDate = sdf.format(date);

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                setDate = "Today, " + formattedDate.split(",")[1];
            } else if (calendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
                setDate = "Tomorrow, " + formattedDate.split(",")[1];
            } else {
                setDate = formattedDate;
            }

            tvDateTime.setText(setDate);

        } catch (Exception e) {
            Log.d("TAG", "timeMethodException: " + e.toString());
        }

    }

    @Override
    public int getItemCount() {
        return longArrayList == null ? 0 : longArrayList.size();
    }
    class ViewDateHolder extends RecyclerView.ViewHolder {
        TextView tvCustomDateTime;
        AppTypeface appTypeface;
        public ViewDateHolder(View itemView) {
            super(itemView);
            appTypeface = AppTypeface.getInstance(mContext);
            tvCustomDateTime = itemView.findViewById(R.id.tvCustomDateTime);
            tvCustomDateTime.setTypeface(appTypeface.getHind_semiBold());

        }
    }
}
