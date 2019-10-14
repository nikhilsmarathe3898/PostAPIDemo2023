package adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.localgenie.R;
import com.localgenie.providerdetails.ProviderDetails;
import com.localgenie.providerdetails.viewschedule.ScheduleActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.pojo.ProviderData;

/**
 * <h>SingleProviderAdapter</h>
 * Created by Ali on 1/31/2018.
 */

public class SingleProviderAdapter extends RecyclerView.Adapter implements Filterable
{
    private static final String TAG = "SingleProviderAdapter";
    private Context mContext;
    private ArrayList<ProviderData>providerData;
    private ArrayList<ProviderData> providerDataFull;
    private ArrayList<ProviderData>onLineProvider;
    private ArrayList<ProviderData>offLineProvider;
    private boolean isClicked = true;

    public SingleProviderAdapter(Context mContext, ArrayList<ProviderData> providerData) {
        this.mContext = mContext;
        this.providerData = providerData;
        providerDataFull = new ArrayList<>();
        providerDataFull.addAll(providerData);
        Log.d(TAG, "SingleProviderAdapter: providerData"+this.providerData+" providerDataFull"+providerDataFull);
        onLineProvider = new ArrayList<>();
        offLineProvider = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        if(viewType==0)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.single_provider_view,parent,false);
            return new Viewholdr(view);
        }else
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.provider_header,parent,false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder.getItemViewType()==1)
        {
            HeaderViewHolder holder1 = (HeaderViewHolder) holder;
            holder1.tvProStatusHeader.setText(providerData.get(position).getHeaderText());
        }
        else {

            final Viewholdr holdr = (Viewholdr) holder;
            onDataSet(providerData,position,holdr);

            if(Constants.bookingModel==5 || Constants.bookingModel==1)
                holdr.llAmountPerHr.setVisibility(View.GONE);
            else
                holdr.llAmountPerHr.setVisibility(View.VISIBLE);

            if(providerData.get(position).getStatus()==1)
                holdr.viewStatus.setSelected(true);
            else
                holdr.viewStatus.setSelected(false);
            if(providerData.get(position).isFavouriteProvider())
                holdr.favIcon.setVisibility(View.VISIBLE);
            else
                holdr.favIcon.setVisibility(View.GONE);
            if(Constants.jobType==1 || Constants.jobType==3){
                holdr.tvProTimeOfArrival.setText(R.string.consultation_fee);
            }

            if(Constants.jobType==3){
                holdr.tvMyEvnetProStatus.setVisibility(View.INVISIBLE);
            }
            holdr.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(isClicked)
                    {
                        callIntent(providerData.get(holdr.getAdapterPosition()).getId());
                        isClicked = false;
                    }

                }
            });
            holdr.favIcon.setOnLongClickListener(view -> {
                Toast.makeText(mContext,mContext.getString(R.string.yourFavouriteProvider),Toast.LENGTH_SHORT).show();
                return false;
            });
        }
    }

    private void callIntent(String id)
    {   Constants.providerId = id;
        Intent intent = new Intent(mContext, ProviderDetails.class);
        intent.putExtra("ProId",id);
        intent.putExtra("isProFileView",false);
        mContext.startActivity(intent);

    }

    private void onDataSet(ArrayList<ProviderData> provider, int position, Viewholdr holdr)
    {
        holdr.rtProvidrDtls.setRating(provider.get(position).getAverageRating());
        String name = provider.get(position).getFirstName() + " " + provider.get(position).getLastName();
        holdr.tvMyEventProName.setText(name);

        Glide.with(mContext)
                .load(provider.get(position).getImage())
                .apply(Utility.createGlideOption(mContext))
                /*.apply(new RequestOptions().transforms(new CenterCrop(),
                        new RoundedCorners(105)))*/
                .into(holdr.ivMyEventProPic);
        //  String amount = provider.get(position).getCurrencySymbol()+" "+provider.get(position).getAmount();


        String distance = provider.get(position).getDistance()+" "+Constants.distanceUnit;

        //   holdr.tvProTimeOfArrival.setText(distance);
        holdr.tvMyEvnetProStatus.setText(distance);
        Utility.setAmtOnRecept(provider.get(position).getAmount(),holdr.tvProAmount,Constants.currencySymbol);
        //  holdr.tvProAmount.setText(amount);

    }
    public void splitData() {
        // notifyDataSetChanged();
        isClicked = true;
        onLineProvider.clear();
        offLineProvider.clear();
        if (providerData != null) {
        for (ProviderData providerDatas : providerData) {
            if (providerDatas.isAvailableNow() == 1)
                onLineProvider.add(providerDatas);
            else
                offLineProvider.add(providerDatas);
        }
        providerData.clear();
        if (onLineProvider.size() > 0) {
            ProviderData providerDat = new ProviderData();
            providerDat.setHeader(true);
            providerDat.setHeaderText(mContext.getString(R.string.availableNow));
            onLineProvider.add(0, providerDat);
            providerData.addAll(onLineProvider);
        }
        if (offLineProvider.size() > 0) {
            ProviderData providerDat = new ProviderData();
            providerDat.setHeader(true);
            providerDat.setHeaderText(mContext.getString(R.string.availableLater));
            offLineProvider.add(0, providerDat);
            providerData.addAll(offLineProvider);
        }
    }
    }


    @Override
    public int getItemCount() {


        return providerData == null ? 0 : providerData.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(providerData.get(position).isHeader())
            return 1;
        else
            return 0;
    }

    public void splitIsFalse() {

        isClicked = true;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                ArrayList result=new ArrayList();
                String charString = charSequence.toString();
                Log.d(TAG, "performFiltering: "+charString);
                if (charString.isEmpty()) {
                    result.addAll(providerDataFull);
                } else {
                    Log.d(TAG, "performFiltering: "+providerData.size());

                    for (ProviderData row : providerDataFull) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name match
                        Log.d(TAG, "performFiltering: firstname: "+row.getFirstName()+" Charstring:"+charString.toLowerCase());
                        if (!row.isHeader() && ((row.getFirstName()+" "+row.getLastName()).toLowerCase().contains(charString.toLowerCase()) )) {
                            Log.d(TAG, "performFiltering: "+row.getFirstName());
                            result.add(row);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = result;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                providerData.clear();
                providerData.addAll((ArrayList<ProviderData>) filterResults.values);
                //splitData();
                notifyDataSetChanged();
            }
        };
    }

    class Viewholdr extends RecyclerView.ViewHolder
    {
        private AppTypeface appTypeface;
        @BindView(R.id.ivMyEventProPic)ImageView ivMyEventProPic;
        @BindView(R.id.tvMyEventProName)TextView tvMyEventProName;
        // @BindView(R.id.tvProSubCatName)TextView tvProSubCatName;
        @BindView(R.id.tvMyEvnetProStatus)TextView tvMyEvnetProStatus;
        @BindView(R.id.tvProAmount)TextView tvProAmount;
        @BindView(R.id.tvProTimeOfArrival)TextView tvProTimeOfArrival;
        @BindView(R.id.rtProvidrDtls)RatingBar rtProvidrDtls;
        @BindView(R.id.viewStatus)View viewStatus;
        @BindView(R.id.llAmountPerHr)LinearLayout llAmountPerHr;
        @BindView(R.id.favIcon)ImageView favIcon;
        public Viewholdr(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
            appTypeface = AppTypeface.getInstance(mContext);
            tvMyEventProName.setTypeface(appTypeface.getHind_semiBold());
            tvProAmount.setTypeface(appTypeface.getHind_semiBold());
            // tvProSubCatName.setTypeface(appTypeface.getHind_light());
            tvMyEvnetProStatus.setTypeface(appTypeface.getHind_light());
            rtProvidrDtls.setIsIndicator(true);

        }

    }

    class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvProStatusHeader)TextView tvProStatusHeader;
        private AppTypeface appTypeface;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            appTypeface = AppTypeface.getInstance(mContext);
            ButterKnife.bind(this,itemView);
            tvProStatusHeader.setTypeface(appTypeface.getHind_semiBold());
        }
    }
}
