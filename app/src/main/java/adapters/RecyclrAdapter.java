package adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.add_address.SearchAddressLocation;

import java.util.ArrayList;

import com.pojo.address_pojo.DbSavedAddress;

import static android.app.Activity.RESULT_OK;

/**
 * <h>RecyclrAdapter</h>
 * Created by user on 6/9/2017.
 */

public class RecyclrAdapter extends RecyclerView.Adapter
{
    private Context mcontext;
    private ArrayList<DbSavedAddress> dbSavedAddresses;
    private SearchAddressLocation searchAddressLocation;

    public RecyclrAdapter(Context mcontext, ArrayList<DbSavedAddress> dbSavedAddresses, SearchAddressLocation searchAddressLocation)
    {
        this.mcontext = mcontext;
        this.dbSavedAddresses = dbSavedAddresses;
        this.searchAddressLocation = searchAddressLocation;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.recyclradaptr,parent,false);
        return new VeiwHlder(view,searchAddressLocation);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {

        if(holder instanceof VeiwHlder)
        {

            final VeiwHlder hlder = (VeiwHlder)holder;
            hlder.tv_addredd_name.setText(dbSavedAddresses.get(position).getAddress_name());
            Log.d("TAG ","FORMATEDADD "+dbSavedAddresses.get(position).getAddress_formate());
            hlder.tv_addredd_foramted.setText(dbSavedAddresses.get(position).getAddress_formate());
            hlder.rl_mainsel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent();
                    intent.putExtra("placename",dbSavedAddresses.get(hlder.getAdapterPosition()).getAddress_name());
                    intent.putExtra("formatedaddes",dbSavedAddresses.get(hlder.getAdapterPosition()).getAddress_formate());
                    intent.putExtra("LATITUDE",dbSavedAddresses.get(hlder.getAdapterPosition()).getAddress_lat());
                    intent.putExtra("LONGITUDE",dbSavedAddresses.get(hlder.getAdapterPosition()).getAddress_lng());
                    hlder.searchAddressLocation.setResult(RESULT_OK, intent);
                    hlder.searchAddressLocation.finish();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dbSavedAddresses.size();
    }

    private class VeiwHlder extends RecyclerView.ViewHolder
    {
        RelativeLayout rl_mainsel;
        SearchAddressLocation searchAddressLocation;
        TextView tv_addredd_name,tv_addredd_foramted;

        VeiwHlder(View itemView, SearchAddressLocation searchAddressLocation)
        {
            super(itemView);
          //  manager = new SharedPrefs(mcontext);
            this.searchAddressLocation = searchAddressLocation;
            rl_mainsel =  itemView.findViewById(R.id.rl_mainsel);
            tv_addredd_foramted =  itemView.findViewById(R.id.tv_addredd_foramted);
            tv_addredd_name =  itemView.findViewById(R.id.tv_addredd_name);

        }
    }
}
