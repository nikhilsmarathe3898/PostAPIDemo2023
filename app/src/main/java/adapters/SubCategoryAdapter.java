package adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.localgenie.R;
import com.localgenie.model.SubCategory;
import com.localgenie.providerList.ProviderListContract;
import com.localgenie.utilities.AppTypeface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <h>SubCategoryAdapter</h>
 * Created by Ali on 1/30/2018.
 */

public class SubCategoryAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<SubCategory>subCategoryArrayList;
    private boolean isProvider = true;
    private boolean isCategorySelected = true;
    private int selectedPosition = -1;
    public String subCatId,subCatName;
    private ProviderListContract.providerView providerView;

    public SubCategoryAdapter(Context mContext, ArrayList<SubCategory> subCategoryArrayList, boolean isProvider,
                              ProviderListContract.providerView providerView) {
        this.mContext = mContext;
        this.subCategoryArrayList = subCategoryArrayList;
        this.isProvider = isProvider;
        this.providerView = providerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.subcategory_adapter,parent,false);
        return new ViewHolders(view);
    }

    public void resetAdapter()
    {
        selectedPosition = -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {

        ViewHolders holdr = (ViewHolders) holder;

        if(isCategorySelected)
        {
            if(subCategoryArrayList.get(position).isCategorySelected())
            {
                selectedPosition = position;
                subCatId = subCategoryArrayList.get(position).getId();
                subCatName = subCategoryArrayList.get(position).getSubCatName();
            }
        }

        if(isProvider)
        {
            if(!subCategoryArrayList.get(position).getAppImageUrl().equals(""))
            {
                Glide.with(mContext)
                        .load(subCategoryArrayList.get(position).getAppImageUrl())
                        .apply(holdr.options)
                        .apply(new RequestOptions().transforms(new CenterCrop(),
                                new RoundedCorners(105)))
                        .into(holdr.ivSubCategory);
            }

            holdr.tvSubCategoryName.setText(subCategoryArrayList.get(position).getSubCatName());
        }else {
            if(!subCategoryArrayList.get(position).getAppImageUrl().equals(""))
            {

            }

            if(position==selectedPosition)
            {
                holdr.tvSubCategoryNameHorizontal.setSelected(true);
                if(!subCategoryArrayList.get(position).getAppImageUrl().equals(""))
                    loadSelectedImage(subCategoryArrayList.get(position).getAppImageUrl(),holdr);
            }
            else
            {
                holdr.tvSubCategoryNameHorizontal.setSelected(false);
                if(!subCategoryArrayList.get(position).getUnSelAppImageUrl().equals(""))
                    loadSelectedImage(subCategoryArrayList.get(position).getUnSelAppImageUrl(),holdr);
            }
            holdr.tvSubCategoryNameHorizontal.setText(subCategoryArrayList.get(position).getSubCatName());
        }

    }

    private void loadSelectedImage(String appImageUrl, ViewHolders holdr)
    {
        Glide.with(mContext)
                .load(appImageUrl)
                .apply(holdr.options)
                .apply(new RequestOptions().transforms(new CenterCrop(),
                        new RoundedCorners(60)))
                .into(holdr.ivSubCategoryHorizontal);
    }

    @Override
    public int getItemCount() {
        return subCategoryArrayList.size();
    }

    class ViewHolders extends RecyclerView.ViewHolder
    {
        @BindView(R.id.ivSubCategory)ImageView ivSubCategory;
        @BindView(R.id.tvSubCategoryName)AppCompatTextView tvSubCategoryName;
        @BindView(R.id.llVertical)LinearLayout llVertical;
        @BindView(R.id.llHorizontal)LinearLayout llHorizontal;
        @BindView(R.id.ivSubCategoryHorizontal)ImageView ivSubCategoryHorizontal;
        @BindView(R.id.tvSubCategoryNameHorizontal)TextView tvSubCategoryNameHorizontal;
        private RequestOptions options;
        AppTypeface appTypeface;
        ViewHolders(View itemView) {
            super(itemView);

            appTypeface = AppTypeface.getInstance(mContext);
            ButterKnife.bind(this,itemView);
            tvSubCategoryName.setTypeface(appTypeface.getHind_light());
            options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.profile_price_bg)
                    .error(R.drawable.profile_price_bg)
                    //.transform(new CircleTransform(mContext))
                    .priority(Priority.HIGH);
            if(!isProvider)
            {
                tvSubCategoryNameHorizontal.setTypeface(appTypeface.getHind_regular());
                llHorizontal.setVisibility(View.VISIBLE);
                llVertical.setVisibility(View.GONE);
                itemView.setOnClickListener(onClickAdapter());
            }else
            {
                itemView.setOnClickListener(onViewClicked());
            }
        }

        private View.OnClickListener onViewClicked()
        {
            return view ->
            {
                isCategorySelected = false;
                subCatId = subCategoryArrayList.get(getAdapterPosition()).getId();
                subCatName = subCategoryArrayList.get(getAdapterPosition()).getSubCatName();
                if(providerView!=null)
                    providerView.setOnItemSelected(subCatId,subCatName);
            };
        }

        private View.OnClickListener onClickAdapter()
        {
            return view -> {
                isCategorySelected = false;
                subCatId = subCategoryArrayList.get(getAdapterPosition()).getId();
                subCatName = subCategoryArrayList.get(getAdapterPosition()).getSubCatName();
                notifyItemChanged(selectedPosition);
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
            };
        }
    }
}
