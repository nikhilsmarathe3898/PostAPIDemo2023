package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.localserviceprovider.ProviderListActivity;
import com.localgenie.R;

import java.util.ArrayList;

/**
 * Created by ${3Embed} on 12/10/17.
 */

public class ServiceSelectAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<String> services;
    ArrayList<String> selectedService;

    public ServiceSelectAdapter(Context context, ArrayList<String> arrayList) {
        services=arrayList;
        this.context=context;
        selectedService=new ArrayList();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout=LayoutInflater.from(parent.getContext()).inflate(R.layout.service_select,null);
        return new ViewHolderService(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderService mholder= (ViewHolderService) holder;
        mholder.tvService.setText(services.get(position));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }
    class ViewHolderService extends RecyclerView.ViewHolder{
        TextView tvService;
        ImageView ivCheck;
        public ViewHolderService(View itemView) {
            super(itemView);
            tvService=itemView.findViewById(R.id.tvService);
            ivCheck=itemView.findViewById(R.id.ivCheck);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedService.contains(services.get(getAdapterPosition()))){
                        selectedService.remove(services.get(getAdapterPosition()));
                        ivCheck.setVisibility(View.INVISIBLE);
                    }else{
                        selectedService.add(services.get(getAdapterPosition()));
                        ivCheck.setVisibility(View.VISIBLE);
                    }
                    Log.d("SelectedService", " onClick: "+selectedService);
                }
            });
        }
    }
}
