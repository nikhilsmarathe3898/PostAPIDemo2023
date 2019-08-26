package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.localgenie.R;
import com.localgenie.biddingFlow.BiddingContractor;
import com.pojo.QuestionImage;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ali on 7/24/2018.
 */
public class QuestionAdapterGrid extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<QuestionImage> questionImages;
    private BiddingContractor.BiddingContractView biddingContractor;
    private boolean isImageInfo = false;
    public QuestionAdapterGrid(Context mContext, ArrayList<QuestionImage> questionImages,BiddingContractor.BiddingContractView biddingContractor) {
        this.mContext = mContext;
        this.questionImages = questionImages;
        this.biddingContractor = biddingContractor;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView;
        if(viewType == 0)
        {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.image_taken,parent,false);
            return new ImageViewHolder(itemView);
        }else
        {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.image_to_take,parent,false);
            return new ImageViewTakesHold(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(questionImages.get(position).isImage())
        {
            Log.d("TAG", "onBindViewHolder: "+questionImages.get(position).getImage());
            ImageViewHolder imageHold = (ImageViewHolder) holder;
            String url = questionImages.get(position).getImage();

            if(isImageInfo)
            {
                if(!"".equals(url))
                    Glide.with(mContext)
                            .load(url)
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(20)))
                            .into(imageHold.imageViewTaken);
            }else
            {
                if(!"".equals(url))
                    Glide.with(mContext)
                            .load(new File(url))
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(20)))
                            .into(imageHold.imageViewTaken);
            }

            if(!isImageInfo)
                imageHold.ivDelete.setVisibility(View.VISIBLE);

        }else
        {
            if(questionImages.size()>1)
            {
                ImageViewTakesHold imageHold = (ImageViewTakesHold) holder;
            }
        }

    }

    @Override
    public int getItemCount()
    {
        return questionImages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(questionImages.get(position).isImage())
            return  0;
        else
            return 1;
    }

    public void onIsQuestionInfo(boolean isInfo) {
        isImageInfo = isInfo;
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageViewTaken,ivDelete;

        ImageViewHolder(View itemView) {
            super(itemView);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            imageViewTaken = itemView.findViewById(R.id.imageViewTaken);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    biddingContractor.deletePhoto(getAdapterPosition(),questionImages.get(getAdapterPosition()).getImagePostion());
                }
            });
        }
    }

    private class ImageViewTakesHold extends RecyclerView.ViewHolder
    {
        private ImageView imageToTake;
        ImageViewTakesHold(View itemView) {
            super(itemView);
            imageToTake = itemView.findViewById(R.id.imageToTake);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    biddingContractor.onToTakeImage(getAdapterPosition());
                }
            });

        }
    }
}
