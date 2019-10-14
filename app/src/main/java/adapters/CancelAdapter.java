package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.jobDetailsStatus.JobDetailsOnTheWayContract;
import com.localgenie.utilities.AppTypeface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.pojo.CancelReasonPojo;

/**
 * <h>CancelAdapter</h>
 * Created by Ali on 3/14/2018.
 */

public class CancelAdapter extends RecyclerView.Adapter
{


    private Context mContext;
    private ArrayList<CancelReasonPojo.CancelReasonData> data;
    private int selectedPosition = -1;
    private JobDetailsOnTheWayContract.CancelBooking cancelInterface;

    public CancelAdapter(Context mContext, ArrayList<CancelReasonPojo.CancelReasonData> data, JobDetailsOnTheWayContract.CancelBooking cancelInterface)
    {
        this.mContext = mContext;
        this.data = data;
        this.cancelInterface = cancelInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cancel_bookingadapter,parent,false);

        return new ViewCancelHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewCancelHolder vHolder = (ViewCancelHolder) holder;

        if(selectedPosition == position)
        {
            vHolder.ivCheckSelector.setSelected(true);
        }else
        {
            vHolder.ivCheckSelector.setSelected(false);
        }
        vHolder.tvForCancel.setText(data.get(position).getReason());

    }

    @Override
    public int getItemCount()
    {
        return data == null ? 0 : data.size();
    }

    class ViewCancelHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvForCancel)TextView tvForCancel;
        @BindView(R.id.ivCheckSelector)ImageView ivCheckSelector;
        private AppTypeface appTypeface;
        public ViewCancelHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            appTypeface = AppTypeface.getInstance(mContext);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notifyItemChanged(selectedPosition);
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);

                    cancelInterface.onSelectedReason(data.get(selectedPosition).getRes_id());
                }
            });
        }
    }
}
