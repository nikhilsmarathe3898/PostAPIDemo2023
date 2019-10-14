package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.R;
import com.localgenie.confirmbookactivity.ConfirmBookingContract;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.pojo.CartInfo;
import com.pojo.CartModifiedData;

/**
 * <h>SelectedService</h>
 * Created by Ali on 3/9/2018.
 */

public class SelectedService extends RecyclerView.Adapter
{

    private ArrayList<CartModifiedData.ItemSelected> servicesList;
    private ArrayList<CartInfo.CheckOutItem>checkOutItems;
    private Context mContext;
    private ConfirmBookingContract.ContractView contractView;
    private boolean isEditable;
    int inCall = 0;

    public SelectedService(Context mContext,boolean isEditable) {

        this.mContext = mContext;
        this.isEditable = isEditable;

    }

    public void onSelectedInterFace(ConfirmBookingContract.ContractView contractView,ArrayList<CartModifiedData.ItemSelected> servicesList)
    {
        this.contractView = contractView;
        this.servicesList = servicesList;
    }

    public void onCheckOutItem(ArrayList<CartInfo.CheckOutItem>checkOutItems)
    {
        this.checkOutItems = checkOutItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.selected_service_layout,parent,false);
        return new ViewHolderSelector(view);
    }

    @Override

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ViewHolderSelector vHolder = (ViewHolderSelector) holder;

        if(isEditable)
        {
            int quantAct = 0;

            try
            {
                quantAct = Integer.parseInt(servicesList.get(position).getQuantityAction());
            }catch (Exception e)
            {
             e.printStackTrace();
            }


            if(Constants.serviceSelected ==2)
            {
                String hiredBy;
                if(Constants.jobType ==2){
                    vHolder.llSelectedAddService.setVisibility(View.VISIBLE);
                    hiredBy = "Hourly @ "+Constants.currencySymbol+""+servicesList.get(position).getUnitPrice()+" /hr";
                    String quant = servicesList.get(position).getQuntity()+" hr(s)";
                    SpannableString ss1=  new SpannableString(quant);
                    ss1.setSpan(new RelativeSizeSpan(1.6f), 0,quant.length()-6, 0);
                    vHolder.tvSelectedQuantity.setText(ss1);
                }else {
                  //  vHolder.tvServiceQuant.setVisibility(View.VISIBLE);
                    hiredBy = mContext.getString(R.string.consultation_fee);
                //    String quant = "dur "+servicesList.get(position).getQuntity()+" mn";
                   // vHolder.tvServiceQuant.setText(quant);
                }

                vHolder.selectedServiceName.setText(hiredBy);
                Utility.setAmtOnRecept(servicesList.get(position).getAmount(),vHolder.serviceAmount,Constants.currencySymbol);


            }else
            {
                if(quantAct==0)
                {
                    vHolder.selectedRemoveBtn.setVisibility(View.VISIBLE);
                    vHolder.selectedRemoveBtn.setSelected(true);
                    vHolder.selectedServiceName.setText(servicesList.get(position).getServiceName());
                    Utility.setAmtOnRecept(servicesList.get(position).getAmount(),vHolder.serviceAmount,Constants.currencySymbol);

                }else {
                    vHolder.llSelectedAddService.setVisibility(View.VISIBLE);
                    String quantity = servicesList.get(position).getQuntity()+"";
                    SpannableString ss1=  new SpannableString(quantity);
                    ss1.setSpan(new RelativeSizeSpan(1.6f), 0,quantity.length(), 0);
                    vHolder.tvSelectedQuantity.setText(ss1);
                    servicesList.get(position).setTempQuantity(servicesList.get(position).getQuntity());
                    vHolder.selectedServiceName.setText(servicesList.get(position).getServiceName());
                    Utility.setAmtOnRecept(servicesList.get(position).getAmount(),vHolder.serviceAmount,Constants.currencySymbol);

                }
            }
        }else {
            if(inCall == 1 )
            {
                vHolder.selectedServiceName.setText(mContext.getString(R.string.consultation_fee));
            }else
            {
                String serviceQuantName = checkOutItems.get(position).getServiceName() +"   x "+checkOutItems.get(position).getQuntity();
                SpannableString ss1=  new SpannableString(serviceQuantName);
                ss1.setSpan(new RelativeSizeSpan(1.3f), checkOutItems.get(position).getServiceName().length()+1,serviceQuantName.length(), 0);
                vHolder.selectedServiceName.setText(ss1);
            }

            Utility.setAmtOnRecept(checkOutItems.get(position).getAmount(),vHolder.serviceAmount,Constants.bookingcurrencySymbol);

        }

    }



    @Override
    public int getItemCount() {
        if(isEditable)
            return servicesList == null ? 0 : servicesList.size();
        else
            return checkOutItems == null ? 0 : checkOutItems.size();
    }

    public void onInCallValue(int i) {
        inCall = i;
    }

    class ViewHolderSelector extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @BindView(R.id.selectedServiceName)TextView selectedServiceName;
        @BindView(R.id.selectedRemoveBtn)TextView selectedRemoveBtn;
        @BindView(R.id.selectedAddBtn)TextView selectedAddBtn;
        @BindView(R.id.llSelectedAddService)LinearLayout llSelectedAddService;
        @BindView(R.id.ivSelectedAdd)ImageView ivSelectedAdd;
        @BindView(R.id.tvSelectedQuantity)TextView tvSelectedQuantity;
        @BindView(R.id.ivSelectedRemove)ImageView ivSelectedRemove;
        @BindView(R.id.serviceAmount)TextView serviceAmount;
        @BindView(R.id.tvServiceQuant)TextView tvServiceQuant;



        private AppTypeface appTypeface;
         ViewHolderSelector(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            appTypeface = AppTypeface.getInstance(mContext);
            selectedRemoveBtn.setOnClickListener(this);
            ivSelectedAdd.setOnClickListener(this);
            ivSelectedRemove.setOnClickListener(this);
            serviceAmount.setTypeface(appTypeface.getHind_light());
            selectedServiceName.setTypeface(appTypeface.getHind_light());
            tvSelectedQuantity.setTypeface(appTypeface.getHind_light());
            tvServiceQuant.setTypeface(appTypeface.getHind_light());
        }

        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.selectedRemoveBtn:
                    contractView.onCartModified(servicesList.get(getAdapterPosition()).getServiceId(),2);
                    break;
                case R.id.ivSelectedAdd:
                    if(Constants.serviceSelected == 2)
                        contractView.onCartModified(servicesList.get(getAdapterPosition()).getServiceId(),1);
                    else
                    {
                        if(servicesList.get(getAdapterPosition()).getTempQuantity()<servicesList.get(getAdapterPosition()).getMaxquantity())
                            contractView.onCartModified(servicesList.get(getAdapterPosition()).getServiceId(),1);
                        else
                            Toast.makeText(mContext,"This is max you can add for this service.",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.ivSelectedRemove:
                    contractView.onCartModified(servicesList.get(getAdapterPosition()).getServiceId(),2);
                    servicesList.get(getAdapterPosition()).setTempQuantity(servicesList.get(getAdapterPosition()).getTempQuantity()-1);
                    break;
            }
        }
    }
}
