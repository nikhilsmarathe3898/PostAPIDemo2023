package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.addTocart.AddToCartContractor;
import com.localgenie.utilities.AppTypeface;
import com.pojo.ServiceResponse;
import com.utility.AlertProgress;

import java.util.ArrayList;

/**
 * Created by ${3Embed} on 24/1/18.
 */

public class CategoryAndServiceAdapter extends RecyclerView.Adapter<CategoryAndServiceAdapter.CategoryHolder> {
    private final Context mContext;
    private ArrayList<ServiceResponse.ServiceDataResponse> categoryLists;//=new ArrayList<>();
    private AddToCartContractor.ContractView callback;
    private AlertProgress alertProgress;
    private boolean isDataThere = false;
    public SubServicesAdapter adapter;

    public CategoryAndServiceAdapter(Context mContext, ArrayList<ServiceResponse.ServiceDataResponse> categoryLists, AddToCartContractor.ContractView callback) {
        this.categoryLists = categoryLists;
        this.mContext=mContext;
        this.callback=callback;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_header,parent,false);
        return new CategoryHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        if(categoryLists.get(position).getSub_cat_id().isEmpty()){
            holder.rl_heading_subCat.setVisibility(View.GONE);
        }
        holder.rvServices.setLayoutManager(holder.linearLayoutManager);
        holder.categoryHeader.setText(categoryLists.get(position).getSub_cat_name());
        holder.categoryDesc.setText(categoryLists.get(position).getSub_cat_desc());
        adapter = new SubServicesAdapter(mContext, categoryLists.get(position).getService(),callback);
        holder.rvServices.setAdapter(adapter);
       // holder.adapter.notifyDataSetChanged();
       /* holder.rvServices.smoothScrollToPosition(position);
        holder.rvServices.setNestedScrollingEnabled(false);*/

      //  adapter.sessionManager(isGuestLogin,alertProgress);
    }

    @Override
    public int getItemCount() {
        return categoryLists == null ? 0 :  categoryLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class CategoryHolder extends RecyclerView.ViewHolder{
        public RecyclerView rvServices;
        TextView categoryHeader,categoryDesc;
        RelativeLayout rl_heading_subCat;
        LinearLayoutManager linearLayoutManager;


        CategoryHolder(View itemView) {
            super(itemView);
            rvServices=itemView.findViewById(R.id.rv_services);
            linearLayoutManager = new LinearLayoutManager(mContext);

            categoryHeader=itemView.findViewById(R.id.categoryHeader);
            categoryDesc=itemView.findViewById(R.id.categoryDesc);
            rl_heading_subCat=itemView.findViewById(R.id.rl_heading_subCat);
            categoryHeader.setTypeface(AppTypeface.getInstance(mContext).getHind_semiBold());
            categoryDesc.setTypeface(AppTypeface.getInstance(mContext).getHind_regular());
          //  rvServices.setNestedScrollingEnabled(false);
        }
    }
}
