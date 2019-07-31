package adapters;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.rateYourBooking.JobPhotosCallback;

import java.util.ArrayList;

/**
 * Created by ${3Embed} on 3/1/19.
 */
public class CourierFlowJobPhotosAdapter extends RecyclerView.Adapter {
    private ArrayList<String> imageSource=new ArrayList<>();
    private JobPhotosCallback callback;
    private boolean canbeEdited =true;

    public CourierFlowJobPhotosAdapter(ArrayList<String> imageSource, JobPhotosCallback callback, boolean canBeEdited){
        this.imageSource=imageSource;
        this.callback=callback;
        this.canbeEdited=canBeEdited;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder=LayoutInflater.from(parent.getContext()).inflate(R.layout.job_photos_item,parent,false);
        return new JobPhotosViewHolder(viewHolder) ;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(!canbeEdited){
            ((JobPhotosViewHolder)holder).iv_removeImage.setVisibility(View.GONE);
        }
        if(position==imageSource.size() && canbeEdited){
            ((JobPhotosViewHolder)holder).iv_removeImage.setVisibility(View.GONE);
            //((JobPhotosViewHolder)holder).iv_jobImage.setImageResource(R.drawable.ic_invoice_take_photo_deafult_image);
        }else{
            Glide.with(((JobPhotosViewHolder)holder).iv_jobImage).load(imageSource.get(position)).into(((JobPhotosViewHolder)holder).iv_jobImage);
        }
    }

    @Override
    public int getItemCount() {
        if(canbeEdited){
            return (imageSource.size()+1);
        }else{
            return imageSource.size();
        }
    }

    public class JobPhotosViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_jobImage,iv_removeImage;
        public JobPhotosViewHolder(View itemView) {
            super(itemView);
            iv_jobImage=itemView.findViewById(R.id.iv_jobImage);
            iv_removeImage=itemView.findViewById(R.id.iv_removeImage);
            iv_removeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageSource.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
            iv_jobImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition()==imageSource.size()) {
                        if(callback!=null){
                            callback.onAddPhotos();
                        }
                    }
                }
            });
        }
    }
}
