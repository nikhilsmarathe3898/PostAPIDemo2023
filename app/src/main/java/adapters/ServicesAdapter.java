package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.localgenie.R;
import com.localgenie.biddingFlow.BiddingQuestions;
import com.localgenie.bookingtype.BookingType;
import com.localgenie.inCallOutCall.InCallOutCall;
import com.localgenie.model.Category;
import com.localgenie.providerList.ProviderList;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * <h>ServicesAdapter</h>
 * Created by ${3Embed} on 5/10/17.
 */

public class ServicesAdapter extends RecyclerView.Adapter {
    private ArrayList<Category> categoryList;
    // private OnServiceClicked context;
    private Context activityContext;
    private Activity mActivity;
    private boolean isVertical=false;
    public ServicesAdapter(ArrayList<Category> categoryList, Context activityContext, boolean isVertical) {//OnServiceClicked ui,

        this.categoryList = categoryList;
        this.activityContext = activityContext;
        mActivity = (Activity) activityContext;
        // this.context= ui;
        this.isVertical=isVertical;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(isVertical){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.service_list_item_v,parent,false);
        }else{
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.service_list_item,parent,false);
        }
        return new ServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ServicesViewHolder viewHolder= (ServicesViewHolder) holder;
        if (categoryList.size()>0) {
            viewHolder.serviceName.setText(categoryList.get(position).getCatName());

            if (categoryList.get(position).getBannerImageApp()!=null) {


                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.profile_price_bg)
                        .error(R.drawable.profile_price_bg)
                        .priority(Priority.HIGH);
                Glide.with(activityContext)
                        .load(categoryList.get(position).getBannerImageApp())
                        .apply(options)
                        .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(20)))
                        .into(viewHolder.serviceImage);


            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public interface OnServiceClicked{
        public void onServiceClicked(String serviceName);
    }

    class ServicesViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvServiceName)AppCompatTextView serviceName;
        @BindView(R.id.ivServicePic)ImageView serviceImage;
        private AppTypeface appTypeface;
        private Bundle bundle;
        ServicesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            appTypeface = AppTypeface.getInstance(activityContext);
            serviceName.setTypeface(appTypeface.getHind_regular());
            bundle = new Bundle();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //  Constants.jobType = categoryList.get(getAdapterPosition()).getJobType();
                    Date date = new Date();
                    HashMap<Integer,Boolean> hashMap = new HashMap<>();
                    hashMap.put(1,categoryList.get(getAdapterPosition()).getCallType().isIncall());
                    hashMap.put(2,categoryList.get(getAdapterPosition()).getCallType().isOutcall());
                    hashMap.put(3,categoryList.get(getAdapterPosition()).getCallType().isTelecall());

                    Constants.catName = categoryList.get(getAdapterPosition()).getCatName();
                    Constants.bookingModel =  categoryList.get(getAdapterPosition()).getBillingModel();
                    Constants.serviceType =  categoryList.get(getAdapterPosition()).getServiceType();
                    Constants.offers=categoryList.get(getAdapterPosition()).getOffers();
                    Constants.visitFee=0;
                    Constants.proId = "0";

                    if(Constants.bookingModel!=1 && Constants.bookingModel!=5)
                    {
                        Constants.pricePerHour = categoryList.get(getAdapterPosition()).getPricePerHour();
                    }
                    if(Constants.serviceType==1)
                    {
                        Constants.visitFee = categoryList.get(getAdapterPosition()).getVisitFee();
                    }
                    Intent intent;
                    if(Constants.serviceType==3)
                    {
                        intent = new Intent(activityContext, BiddingQuestions.class);//BookingType
                        callIntentBidding(intent,categoryList.get(getAdapterPosition()),bundle,true);
                    }else
                    {
                        if(categoryList.get(getAdapterPosition()).getQuestionArr().size()>0)
                        {
                            intent = new Intent(activityContext, BiddingQuestions.class);//BookingType
                            callIntentBidding(intent,categoryList.get(getAdapterPosition()),bundle,false);

                        //    callIntent(intent,categoryList.get(getAdapterPosition()),bundle);
                        }else
                        {
                            if(!hashMap.get(1) && !hashMap.get(2) && hashMap.get(3))
                            {
                                Constants.jobType = 3;
                                Constants.bookingType = 2;
                                Constants.scheduledDate= ""+getStartOfDayInMillisToday(date)/1000;
                                intent = new Intent(activityContext, ProviderList.class);
                                callIntentProvider(intent,categoryList.get(getAdapterPosition()));
                            }else if(!hashMap.get(1) && hashMap.get(2) && !hashMap.get(3))
                            {
                                Constants.jobType = 2;
                                intent = new Intent(activityContext, BookingType.class);//BookingType
                                callIntent(intent,categoryList.get(getAdapterPosition()),bundle);

                            }else if(hashMap.get(1) && !hashMap.get(2) && !hashMap.get(3))
                            {
                                Constants.jobType = 1;
                                Constants.bookingType = 2;
                                Constants.scheduledDate = ""+getStartOfDayInMillisToday(date)/1000;
                                intent = new Intent(activityContext, ProviderList.class);
                                callIntentProvider(intent,categoryList.get(getAdapterPosition()));
                                // // // // // // // // // // // //
                            }else
                            {
                                intent = new Intent(activityContext, InCallOutCall.class);
                                callIntent(intent,categoryList.get(getAdapterPosition()),bundle);
                            }
                        }

                    }


                }
            });
        }

    }

    private void callIntentBidding(Intent intent, Category category, Bundle bundle, boolean isBidding)
    {

        bundle.putString("CatId",category.getId());
        bundle.putSerializable("SubCat",category.getSubCategory());
        bundle.putSerializable("BookingTypeAction",category.getBookingTypeAction());
        bundle.putSerializable("QUESTIONS",category.getQuestionArr());
        bundle.putSerializable("CallType",category.getCallType());
        bundle.putSerializable("OFFERS",category.getOffers());
        bundle.putDouble("MinAmount",category.getMinimumFees());
        bundle.putDouble("MaxAmount",category.getMaximumFees());
        bundle.putBoolean("IsBidding",isBidding);

        intent.putExtras(bundle);
        activityContext.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
    }

    private void callIntentProvider(Intent intent,Category category)
    {
        Constants.catId = category.getId();
        intent.putExtra("CatId",category.getId());
        intent.putExtra("SubCat",category.getSubCategory());
        intent.putExtra("MinAmount",category.getMinimumFees());
        intent.putExtra("MaxAmount",category.getMaximumFees());
        activityContext.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);

    }

    private void callIntent(Intent intent, Category category, Bundle bundle)
    {

        bundle.putString("CatId",category.getId());
        bundle.putSerializable("SubCat",category.getSubCategory());
        bundle.putDouble("MinAmount",category.getMinimumFees());
        bundle.putDouble("MaxAmount",category.getMaximumFees());
        bundle.putSerializable("BookingTypeAction",category.getBookingTypeAction());
        bundle.putSerializable("QUESTIONS",category.getQuestionArr());
        bundle.putSerializable("CallType",category.getCallType());
        bundle.putSerializable("OFFERS",category.getOffers());
        intent.putExtras(bundle);
        activityContext.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
    }

    public long getStartOfDayInMillisToday(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);//.getTime()
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
