package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.home.ViewAllCategory;
import com.localgenie.model.Category;
import com.localgenie.model.CatDataArray;
import com.localgenie.utilities.AppTypeface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <h>ServicesCategoryAdapter</h>
 * Created by ${3Embed} on 5/10/17.
 */

public class ServicesCategoryAdapter extends RecyclerView.Adapter {
   private ArrayList<CatDataArray> serviceCategories;
   private Context context;
   private Activity mActivity;

    public ServicesCategoryAdapter(ArrayList<CatDataArray> serviceCategories, Context context) {
        this.serviceCategories = serviceCategories;
        this.context=context;
        mActivity = (Activity) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.service_horizontal_list,parent,false);
        return new ServicesCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ServicesCategoryViewHolder vHolder= (ServicesCategoryViewHolder) holder;
        vHolder.tvServiceCategory.setText(serviceCategories.get(position).getGroupName());

        vHolder.category.addAll(serviceCategories.get(position).getCategory());
        vHolder.servicesAdapter.notifyDataSetChanged();


    }

    @Override
    public int getItemCount() {
        return serviceCategories.size();
    }
    class ServicesCategoryViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvServiceCategory)TextView tvServiceCategory;
        @BindView(R.id.tvViewAll)TextView tvViewAll;
        @BindView(R.id.rvServicesList)RecyclerView rvServicesList;
        private LinearLayoutManager linearLayoutManager;
        private ArrayList<Category> category = new ArrayList<>();
        private ServicesAdapter servicesAdapter;
        private AppTypeface appTypeface;

        ServicesCategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            appTypeface = AppTypeface.getInstance(context);
            servicesAdapter= new ServicesAdapter(category,context, false); // Using same adapter for two places
            linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
            rvServicesList.setLayoutManager(linearLayoutManager);
            rvServicesList.setAdapter(servicesAdapter);
            tvServiceCategory.setTypeface(appTypeface.getHind_bold());
            tvViewAll.setTypeface(appTypeface.getHind_semiBold());


        }

        @OnClick(R.id.tvViewAll)
        public void onViewAllClicked()
        {
            Intent intent = new Intent(context, ViewAllCategory.class);
            intent.putExtra("CATEGORY",serviceCategories.get(getAdapterPosition()).getCategory());
            intent.putExtra("CATEGORYTITLE",serviceCategories.get(getAdapterPosition()).getGroupName());
            context.startActivity(intent);
            mActivity.overridePendingTransition(R.anim.slide_in_up,R.anim.stay_still);
        }
    }
}
