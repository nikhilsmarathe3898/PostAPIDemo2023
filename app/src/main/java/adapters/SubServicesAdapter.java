package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.addTocart.AddToCart;
import com.localgenie.addTocart.AddToCartContractor;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.localgenie.utilities.Utility;
import com.pojo.ServiceResponse;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

/**
 * <h>ServicesAdapter</h>
 * Created by ${3Embed} on 25/1/18.
 */

public class SubServicesAdapter extends RecyclerView.Adapter{
    private ArrayList<ServiceResponse.ServiceData> servicesList;
    private Context mContext;
    ServiceResponse.ServiceData pojo;
    private AddToCartContractor.ContractView callback;
    private AlertProgress alertProgress;
    private String messageCart;
    public SubServicesAdapter(Context mContext, ArrayList<ServiceResponse.ServiceData> servicesList, AddToCartContractor.ContractView callback) {
        this.servicesList = servicesList;
        this.mContext=mContext;
        this.callback=callback;
    }
    public void subServiceList(ArrayList<ServiceResponse.ServiceData> subServicesList)
    {
      //  servicesList.clear();
     //   servicesList.addAll(subServicesList);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item,parent,false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hold, int position)
    {
        ServiceViewHolder holder = (ServiceViewHolder) hold;
        holder.serviceName.setText(servicesList.get(position).getSer_name());
        Utility.setAmtOnRecept(servicesList.get(position).getIs_unit(),holder.totalCost,Constants.currencySymbol);
      //  holder.totalCost.setText(cost);
        String quantity = servicesList.get(position).getTempQuant()+"";
        holder.tv_quantity.setText(quantity);
        holder.tvPerSignature.setText(servicesList.get(position).getUnit());
        holder.serviceDisc.setText(servicesList.get(position).getSer_desc());
        Log.d("TAG", "onBindViewHolderElQUIN: "+servicesList.get(position).getQuantity()+" Tempquant "+servicesList.get(position).getTempQuant()
                +" onBindMax "+servicesList.get(position).getMaxquantity());
        if(servicesList.get(position).getQuantity()==1)
        {
            if(servicesList.get(position).getTempQuant()==0){
                holder.ll_add_service.setVisibility(View.GONE);
                holder.addbtn.setVisibility(View.VISIBLE);
                holder.addbtn.setSelected(false);
                holder.removeBtn.setVisibility(View.GONE);
            }else {
                Log.d("TAG", "onBindViewHolderElQUIN: "+servicesList.get(position).getTempQuant()
                        +" onBindMax "+servicesList.get(position).getMaxquantity());
                if(servicesList.get(position).getMaxquantity()>0)
                {
                    holder.ll_add_service.setVisibility(View.VISIBLE);
                    holder.removeBtn.setVisibility(View.GONE);

                }else if(servicesList.get(position).getMaxquantity()==0 && servicesList.get(position).getTempQuant()==1)
                {
                    holder.removeBtn.setVisibility(View.VISIBLE);
                }

                //  holder.ll_add_service.setVisibility(View.VISIBLE);

                holder.addbtn.setVisibility(View.GONE);
            }
        }else{
            if(servicesList.get(position).getTempQuant()==0){
                holder.ll_add_service.setVisibility(View.GONE);
                holder.removeBtn.setVisibility(View.GONE);
                holder.addbtn.setVisibility(View.VISIBLE);
                holder.addbtn.setSelected(false);
            }else if(servicesList.get(position).getTempQuant()==1){
                holder.ll_add_service.setVisibility(View.GONE);
                holder.removeBtn.setVisibility(View.VISIBLE);
             //   holder.addbtn.setSelected(true);
                holder.addbtn.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }



    class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.serviceName)TextView serviceName;
        @BindView(R.id.totalCost)TextView totalCost;
        @BindView(R.id.tvPerSignature)TextView tvPerSignature;
        @BindView(R.id.viewMore)TextView viewMore;
        @BindView(R.id.serviceDisc)TextView serviceDisc;
        @BindView(R.id.addbtn)TextView addbtn;
        @BindView(R.id.tv_quantity)TextView tv_quantity;
        @BindView(R.id.ll_add_service)LinearLayout ll_add_service;
        @BindView(R.id.iv_remove)ImageView iv_remove;
        @BindView(R.id.iv_add)ImageView iv_add;
        @BindView(R.id.removeBtn)TextView removeBtn;
        private AppTypeface appTypeface;

        ServiceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            removeBtn.setSelected(true);
            appTypeface = AppTypeface.getInstance(mContext);
            viewMore.setOnClickListener(this);
            serviceName.setTypeface(appTypeface.getHind_semiBold());
            totalCost.setTypeface(appTypeface.getHind_regular());
            tvPerSignature.setTypeface(appTypeface.getHind_regular());
            serviceDisc.setTypeface(appTypeface.getHind_regular());
            viewMore.setTypeface(appTypeface.getHind_regular());
            addbtn.setTypeface(appTypeface.getHind_regular());
            removeBtn.setTypeface(appTypeface.getHind_regular());
            tv_quantity.setTypeface(appTypeface.getHind_regular());
            addbtn.setOnClickListener(this);
            removeBtn.setOnClickListener(this);
            iv_remove.setOnClickListener(this);
            iv_add.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final int position=getAdapterPosition();
            pojo = servicesList.get(position);

            final int quantity=pojo.getTempQuant();
            for(ServiceResponse.ServiceData servicePojo:servicesList)
                Log.d("Shijen", "onClick: "+servicePojo.getSer_name()+" "+servicePojo.getTempQuant());

            switch (view.getId()){
                case R.id.iv_add:
                    if(pojo.getTempQuant()<pojo.getMaxquantity()){
                        callback.onCartModified(servicesList.get(position).get_id(),1,1);
                        pojo.setTempQuant(pojo.getTempQuant()+1);
                        //  Constants.serviceSelected = 1;
                        //observable.emit(new ServiceSelectedPojo(com.pojo.getSer_name(),com.pojo.getIs_unit(),com.pojo.getTempQuant()));
                    }else{
                        Toast.makeText(mContext,"This is max you can add for this service.",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.removeBtn:
                    if(pojo.getTempQuant()==1){
                        callback.onCartModified(servicesList.get(position).get_id(),2, 1);
                        pojo.setTempQuant(pojo.getTempQuant()-1);
                        //Constants.serviceSelected = 1;
                        //observable.emit(new ServiceSelectedPojo(com.pojo.getSer_name(),-com.pojo.getIs_unit(),-com.pojo.getTempQuant()));
                    }
                    break;
                case R.id.iv_remove:
                    if(pojo.getTempQuant()>0){
                        callback.onCartModified(servicesList.get(position).get_id(),2, 1);
                        pojo.setTempQuant(pojo.getTempQuant()-1);
                        //Constants.serviceSelected = 1;
                        //observable.emit(new ServiceSelectedPojo(com.pojo.getSer_name(),-com.pojo.getIs_unit(),-com.pojo.getTempQuant()));
                    }
                    break;
                case R.id.addbtn:

                    if(AddToCart.isGuestLogin)
                    {
                        callback.onGuestToLogin();
                    }else
                    {
                        if(!AddToCart.isCartPresent)
                        {
                            callCategory(position,quantity);
                        }else
                        {
                            showAlert(position,quantity);
                        }

                    }

                    break;
                case R.id.viewMore:
                    callback.onViewMore(pojo.getIs_unit(),pojo.getSer_name(),pojo.getSer_desc());
                    break;
            }
            notifyItemChanged(position);
        }
    }

    private void showAlert(int position, int quantity) {

        alertProgress = new AlertProgress(mContext);
        Log.d("TAG", "showAlert: "+AddToCart.messageCart);
        alertProgress.alertPositiveNegativeOnclick(mContext, AddToCart.messageCart, mContext.getResources().getString(R.string.itemsAlreadyInCart),mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.cancel),false , new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {

                if(isClicked)
                {
                    AddToCart.isCartPresent = false;
                    callCategory(position,quantity);
                }

            }
        });
    }

    public void callCategory(int position, int quantity)
    {
        if(pojo.getTempQuant() ==1 && pojo.getQuantity()==0)
        {
            callback.onCartModified(servicesList.get(position).get_id(),2, 1);
            pojo.setTempQuant(pojo.getTempQuant()-1);
        }else if(pojo.getTempQuant()==0) {
            callback.onCartModified(servicesList.get(position).get_id(),1, 1);
            pojo.setTempQuant(quantity+1);
        }
        notifyItemChanged(position);
    }
}
