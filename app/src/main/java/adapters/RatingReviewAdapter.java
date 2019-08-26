package adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.ReadMoreSpannable;
import com.localgenie.utilities.SessionManager;
import com.localgenie.utilities.Utility;

import java.util.ArrayList;

import com.pojo.ProviderDetailsResponse;

/**
 * <h>RatingReviewAdapter</h>
 * Created by Ali on 9/19/2017.
 */

public class RatingReviewAdapter extends RecyclerView.Adapter {
    private Context mcontext;
    private ArrayList<ProviderDetailsResponse.ProviderResponseDetails.ReviewList> mReviwList;
    private String readMore, readLess;
    private boolean isProfile;

    public RatingReviewAdapter(Context mcontext, ArrayList<ProviderDetailsResponse.ProviderResponseDetails.ReviewList> rateReviewses, boolean isProfile) {
        this.mcontext = mcontext;
        mReviwList = rateReviewses;
        readMore = mcontext.getResources().getString(R.string.readMore);
        readLess = mcontext.getResources().getString(R.string.readLess);
        this.isProfile = isProfile;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mcontext).inflate(R.layout.signlecellreviwadptr, parent, false);
        return new ViewHoldr(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHoldr hldr = (ViewHoldr) holder;
        Log.e("REWIESBY", "onBindViewHolder: "+mReviwList.get(position).getReviewBy());
        hldr.tvReviwName.setText(mReviwList.get(position).getReviewBy());
        hldr.tvReview.setText(mReviwList.get(position).getReview());
        hldr.rtReviewadptr.setRating(mReviwList.get(position).getRating());
        hldr.rtReviewadptr.setIsIndicator(true);
        if (hldr.tvReview.getText().toString().length() > 100) {
            ReadMoreSpannable redmore = new ReadMoreSpannable(readMore, readLess);
            ReadMoreSpannable.makeTextViewResizable(hldr.tvReview, 3, readMore, true);
        }
        String url = mReviwList.get(position).getProfilePic().replace(" ", "%20");

        int[] dateValue = Utility.calculateTimeDifference(mReviwList.get(position).getReviewAt(), Constants.serverTime);
        String dateTimeValue = "";
        if (dateValue[0] >= 24) {
            int days = dateValue[0] / 24;
            if(days>1)
                dateTimeValue = days + " " + hldr.resources.getString(R.string.daysAgo);
            else
                dateTimeValue = "Yesterday";
        } else if (dateValue[0] > 0)
            dateTimeValue = dateValue[0] + " " + hldr.resources.getString(R.string.hoursAgo);
        else if (dateValue[1] > 0)
            dateTimeValue = dateValue[1] + " " + hldr.resources.getString(R.string.minutesAgo);
        else
            dateTimeValue = dateValue[2] + " " + hldr.resources.getString(R.string.secondsAgo);
        hldr.tvReviewTime.setText(dateTimeValue);
        if (isProfile) {
            if (!url.equals("")) {
               /* double size[] = Scaler.getScalingFactor(mcontext);
                double height = (40) * size[1];
                double width = (40) * size[0];
                Picasso.with(mcontext).load(url)
                        .resize((int) width, (int) height)
                        .transform(new CircleTransform())
                        .into(hldr.ivReviewImage);*/


                Glide.with(mcontext)
                        .load(url)
                        .apply(Utility.createGlideOption(mcontext))
                       // .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(60)))
                        .into(hldr.ivReviewImage);

            }
        } else {
            if (!url.equals("")) {
              /*  double size[] = Scaler.getScalingFactor(mcontext);
                double height = (50) * size[1];
                double width = (50) * size[0];
                Picasso.with(mcontext).load(url)
                        .resize((int) width, (int) height)
                        .transform(new CircleTransform())
                        .into(hldr.ivReview);*/

                Glide.with(mcontext)
                        .load(url)
                        .apply(Utility.createGlideOption(mcontext))
                      //  .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(30)))
                        .into(hldr.ivReview);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mReviwList.size();
    }

    private class ViewHoldr extends RecyclerView.ViewHolder {
        private TextView tvReviwName, tvReview, tvReviewTime;
        private ImageView ivReview, ivReviewImage;
        private RatingBar rtReviewadptr;
        private Resources resources;
        private SessionManager manager;

        ViewHoldr(View itemView) {
            super(itemView);
            resources = mcontext.getResources();
            AppTypeface appTypeface = AppTypeface.getInstance(mcontext);
            rtReviewadptr = itemView.findViewById(R.id.rtReviewadptr);
            manager = SessionManager.getInstance(mcontext);

            ivReview = itemView.findViewById(R.id.ivReview);
            if (isProfile) {
                ivReview.setVisibility(View.INVISIBLE);
            }
            ivReviewImage = itemView.findViewById(R.id.ivReviewImage);
            tvReviwName = itemView.findViewById(R.id.tvReviwName);
            tvReview = itemView.findViewById(R.id.tvReview);
            tvReviewTime = itemView.findViewById(R.id.tvReviewTime);
            rtReviewadptr.setIsIndicator(true);
            tvReviwName.setTypeface(appTypeface.getHind_semiBold());
            tvReview.setTypeface(appTypeface.getHind_regular());
            tvReviewTime.setTypeface(appTypeface.getHind_regular());

        }
    }
}
