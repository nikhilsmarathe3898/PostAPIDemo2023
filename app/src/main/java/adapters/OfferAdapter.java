package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.model.Offers;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;

import java.util.ArrayList;

/**
 * Created by Ali on 9/26/2018.
 */
public class OfferAdapter extends RecyclerView.Adapter
{
    private Context mContext;
    private ArrayList<Offers>offers;

    public OfferAdapter(Context mContext, ArrayList<Offers> offers) {
        this.mContext = mContext;
        this.offers = offers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.offer_layout,parent,false);
        return new ViewHold(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHold hold = (ViewHold) holder;
        hold.tvDiscountUpTo.setText(offers.get(position).getTitle());
       /* String shiftRequired = mContext.getString(R.string.minimum)+" "+offers.get(position).getMinShiftBooking()
                +" "+mContext.getString(R.string.to)+" "+mContext.getString(R.string.maximum)+" "+
                offers.get(position).getMaxShiftBooking() +" "+mContext.getString(R.string.shifts);*/

        hold.tvShiftRange.setText(offers.get(position).getOfferDescription());
        String discount;
        if(offers.get(position).getDiscountType()==2)
            discount = offers.get(position).getValue()+"%";
        else
            discount = Constants.currencySymbol+" "+offers.get(position).getValue();

        hold.tvDiscount.setText(discount);
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    private class ViewHold extends RecyclerView.ViewHolder
    {
        private TextView tvDiscountUpTo,tvDiscount,tvShiftRange;

        private AppTypeface appTypeface;

        ViewHold(View itemView) {
            super(itemView);

            appTypeface = AppTypeface.getInstance(mContext);
            tvDiscountUpTo = itemView.findViewById(R.id.tvDiscountUpTo);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvShiftRange = itemView.findViewById(R.id.tvShiftRange);
            tvDiscountUpTo.setTypeface(appTypeface.getHind_regular());
            tvShiftRange.setTypeface(appTypeface.getHind_regular());
            tvDiscount.setTypeface(appTypeface.getHind_bold());

        }
    }
}
