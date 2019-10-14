package adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.rateYourBooking.ResponsePojo;
import com.localgenie.utilities.RemoveFavCallBack;

import java.util.ArrayList;

public class FavoriteProviderCategoriesAdapter extends RecyclerView.Adapter {
    ArrayList<ResponsePojo.FavProviderCatData> categoryList;
    String categoryId;
    String providerId;
    int selectedPosition=-1;
    View selectedItem=null;

    public FavoriteProviderCategoriesAdapter(ArrayList<ResponsePojo.FavProviderCatData> categoryList, RemoveFavCallBack removeFavCallBack) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cancel_bookingadapter,parent,false);
        return new FavoriteProviderVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FavoriteProviderVH viewholder = (FavoriteProviderVH) holder;
        viewholder.tvForCancel.setText(categoryList.get(position).getCategoryName());
    }

    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
    }

    public String  getProviderId() {
        return providerId;
    }

    public String getSelectedCategories() {
        return categoryId;
    }


    class  FavoriteProviderVH extends RecyclerView.ViewHolder {
        TextView tvForCancel;
        public FavoriteProviderVH(View itemView) {
            super(itemView);
            tvForCancel=itemView.findViewById(R.id.tvForCancel);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedItem!=null){
                        selectedItem.setSelected(false);
                    }
                    selectedItem=itemView;
                    selectedItem.setSelected(true);
                    selectedPosition=getAdapterPosition();
                    categoryId = categoryList.get(getAdapterPosition()).getCategoryId();
                    providerId = categoryList.get(getAdapterPosition()).getProviderId();
                }
            });
        }
    }
}
