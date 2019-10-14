package adapters;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.localgenie.R;
import com.pojo.InvoiceDetails;

import java.util.ArrayList;

/**
 * <h>RatingAdapter</h>
 * Created by ${3Embed} on 29/9/17.
 */

public class RatingAdapter extends RecyclerView.Adapter {
    ArrayList<InvoiceDetails.CustomerRating> ratingTextList;
    Context context;
    ViewChange viewChange;


    public RatingAdapter(ArrayList<InvoiceDetails.CustomerRating> ratingText, Context context, ViewChange viewChange) {
        this.ratingTextList = ratingText;
        this.context=context;
        this.viewChange=viewChange;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_grid_cell,parent,false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        RatingViewHolder hold = (RatingViewHolder) holder;
        hold.tvRateTitle.setText(ratingTextList.get(position).getName());
        ratingTextList.get(position).setRatings(hold.rbProvider.getRating());

      //  ((RatingViewHolder)holder).ratingText.setText(ratingTextList.get(position));
    }
    public void clearList(){
        int size=ratingTextList.size();
        ratingTextList.clear();
        notifyItemRangeRemoved(0,size);
    }
    public void setList(ArrayList<String> list){
     //   ratingTextList.addAll(list);
        notifyItemRangeInserted(0,list.size());
    }

    @Override
    public int getItemCount() {
        return ratingTextList == null ? 0 : ratingTextList.size();
    }

    class RatingViewHolder extends RecyclerView.ViewHolder{
        TextView ratingText,tvRateTitle;
        RatingBar rbProvider;
        View viewLast;
         RatingViewHolder(View itemView) {
            super(itemView);
             tvRateTitle = itemView.findViewById(R.id.tvRateTitle);
             rbProvider = itemView.findViewById(R.id.rbProvider);
             viewLast = itemView.findViewById(R.id.viewLast);
         //  ratingText=itemView.findViewById(R.id.tvRatingText);
             rbProvider.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                 @Override
                 public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                     Log.d("TAG", "onRatingChanged: "+v +" rating "+ratingBar.getRating());
                     viewChange.onTextSelected(ratingBar.getRating(), getAdapterPosition());
                   //  OnRatingBarChangeListener
                 }
             });
           /* itemView.setOnClickListener(view -> {


            });*/
        }
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            // Add top margin only for the first item to avoid double space between items
            if ((parent.getChildLayoutPosition(view) == 0)|(parent.getChildLayoutPosition(view) == 1) | (parent.getChildLayoutPosition(view) == 2)) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

    public interface  ViewChange{
        void onTextSelected(float rating, int position);
    }
}
