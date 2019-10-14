package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.providerList.ProviderListContract;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.pojo.FilteredResponse;

/**
 * <h>FilterAdapter</h>
 * Created by Ali on 2/1/2018.
 */

public class FilterAdapter extends RecyclerView.Adapter
{
    private Context mContext;
    private ArrayList<FilteredResponse>filteredResponces;

    private ProviderListContract.providerView providerView;
    public FilterAdapter(Context mContext, ArrayList<FilteredResponse> filteredResponces, ProviderListContract.providerView providerView) {
        this.mContext = mContext;
        this.filteredResponces = filteredResponces;
        this.providerView = providerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.filter_adapter,parent,false);

        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        viewHolderClass.tvFilterSelected.setText(filteredResponces.get(position).getGeneralName());


    }

    @Override
    public int getItemCount()
    {
        return filteredResponces == null ? 0 : filteredResponces.size();
    }

    class ViewHolderClass extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvFilterSelected)TextView tvFilterSelected;
       private AppTypeface appTypeface;
        ViewHolderClass(View itemView)
        {
            super(itemView);
            appTypeface = AppTypeface.getInstance(mContext);
            ButterKnife.bind(this,itemView);
            tvFilterSelected.setTypeface(appTypeface.getHind_regular());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (filteredResponces.get(getAdapterPosition()).getType())
                    {
                        case 1:
                            filteredResponces.remove(filteredResponces.get(getAdapterPosition()));
                            Constants.filteredAddress = "";
                            Constants.filteredLat = 0;
                            Constants.filteredLng = 0;
                            providerView.setLatLng();
                            break;
                        case 2:
                            filteredResponces.remove(filteredResponces.get(getAdapterPosition()));
                            Constants.distance = 30;
                            Log.d("TAG", "onClickdistance: "+filteredResponces.size());
                           // providerView.onFilterRemoveArray(filteredResponces);
                            break;
                        case 3:
                            filteredResponces.remove(filteredResponces.get(getAdapterPosition()));
                            Constants.subCatId = "";
                            Log.d("TAG", "onClicksubCatId: "+filteredResponces.size());
                          //  providerView.onFilterRemoveArray(filteredResponces);
                            break;
                        case 4:
                            filteredResponces.remove(filteredResponces.get(getAdapterPosition()));
                            Constants.minPrice = 0.0;
                            Constants.maxPrice = 0.0;

                            break;
                    }
                   // notifyDataSetChanged();
                    providerView.onFilterRemoveArray(filteredResponces);

                }
            });
        }
    }
}
