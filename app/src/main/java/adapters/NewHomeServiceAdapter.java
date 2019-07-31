package adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.localgenie.R;
import com.localgenie.home.ServiceFragContract;
import com.localgenie.model.CatDataArray;
import com.localgenie.model.Category;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Utility;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ali on 7/31/2018.
 */
public class NewHomeServiceAdapter extends RecyclerView.Adapter
{
    private Context mContext;

    private ArrayList<CatDataArray>catArray;

    private ServiceFragContract.ServiceView serviceView;
    private int selectedPosition = -1;


    public NewHomeServiceAdapter(Context mContext, ArrayList<CatDataArray> catArray, ServiceFragContract.ServiceView serviceView) {
        this.mContext = mContext;
        this.catArray = catArray;
        this.serviceView = serviceView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.new_home_category,parent,false);
        return new NewHomeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        NewHomeHolder hold = (NewHomeHolder) holder;

        hold.tvHomeCatName.setText(catArray.get(position).getGroupName());
        hold.tvHomeSubCatName.setText(catArray.get(position).getDescription());


        if(!"".equals(catArray.get(position).getSelectImage()))
        {

            Glide.with(mContext)
                    .load(catArray.get(position).getSelectImage())
                    .apply(new RequestOptions().transforms(new CenterInside(), new RoundedCorners(10)))
                    .into(hold.imageBanner);
        }
        hold.categoryList.clear();
        hold.categoryList.addAll(catArray.get(position).getCategory());
        hold.trendingAdapter.notifyDataSetChanged();


        if(position == selectedPosition)
        {
            if(catArray.get(position).isExpanded())
                hold.recyclerViewHomeCat.setVisibility(View.VISIBLE);
            else
                hold.recyclerViewHomeCat.setVisibility(View.GONE);
            hold.ivOpenCategory.setSelected(true);
            hold.llLIstNewHOme.setBackgroundColor(Utility.getColor(mContext,R.color.lightYellow));
            serviceView.onPositionSelected(hold.getAdapterPosition());

        }else
        {
            hold.recyclerViewHomeCat.setVisibility(View.GONE);
            hold.ivOpenCategory.setSelected(false);
            hold.llLIstNewHOme.setBackgroundColor(Utility.getColor(mContext,R.color.grey_background_lighter));
        }

    }



    private Palette createPaletteSync(Bitmap bitmap)
    {

        return Palette.from(bitmap).generate();

    }

    @Override
    public int getItemCount() {
        return catArray.size();
    }

    class NewHomeHolder extends RecyclerView.ViewHolder
    {

        private ImageView ivOpenCategory,imageBanner;
        private TextView tvHomeCatName,tvHomeSubCatName;
        //  private View viewCategory;
        private AppTypeface appTypeface;
        private RecyclerView recyclerViewHomeCat;
        private NewHomeSubCategory trendingAdapter;
        private LinearLayout llHomeCategoryService,llLIstNewHOme;

        ArrayList<Category> categoryList = new ArrayList<>();


        NewHomeHolder(View itemView) {
            super(itemView);
            appTypeface = AppTypeface.getInstance(mContext);

            ivOpenCategory = itemView.findViewById(R.id.ivOpenCategory);
            tvHomeCatName = itemView.findViewById(R.id.tvHomeCatName);
            tvHomeSubCatName = itemView.findViewById(R.id.tvHomeSubCatName);
            recyclerViewHomeCat = itemView.findViewById(R.id.recyclerViewHomeCat);
            llHomeCategoryService = itemView.findViewById(R.id.llHomeCategoryService);
            imageBanner = itemView.findViewById(R.id.imageBanner);
            llLIstNewHOme = itemView.findViewById(R.id.llLIstNewHOme);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3);
            trendingAdapter = new NewHomeSubCategory(categoryList,mContext);
            recyclerViewHomeCat.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mContext, R.anim.layoutanimation_from_bottom));
            recyclerViewHomeCat.setLayoutManager(gridLayoutManager);
            recyclerViewHomeCat.setAdapter(trendingAdapter);

            tvHomeCatName.setTypeface(appTypeface.getHind_bold());
            tvHomeSubCatName.setTypeface(appTypeface.getHind_regular());


            llHomeCategoryService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    catArray.get(getAdapterPosition()).setExpanded(!catArray.get(getAdapterPosition()).isExpanded());

                    notifyItemChanged(selectedPosition);
                        selectedPosition = getAdapterPosition();

                    notifyItemChanged(selectedPosition);
                }
            });
        }
    }



    private class RequestAsyncCall extends AsyncTask<String ,Void,Bitmap>
    {
        Bitmap bitmap = null;

        LinearLayout llLIstNewHOme;
        RequestAsyncCall(LinearLayout llLIstNewHOme) {
            this.llLIstNewHOme = llLIstNewHOme;
        }

        @Override
        protected Bitmap doInBackground(String... strings)
        {
            try {
                URL url = new URL(strings[0]);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            int backgroundColor = Utility.getColor(mContext,R.color.lightYellow);

            Palette p = createPaletteSync(bitmap);
            Palette.Swatch vibrantSwatch = p.getVibrantSwatch();

            if(vibrantSwatch != null){
                backgroundColor = vibrantSwatch.getRgb();
            }
            llLIstNewHOme.setBackgroundColor(backgroundColor);

        }
    }
}
