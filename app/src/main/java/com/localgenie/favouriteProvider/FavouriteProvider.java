package com.localgenie.favouriteProvider;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.networking.LSPServices;
import com.localgenie.rateYourBooking.ResponsePojo;
import com.localgenie.utilities.AppTypeface;

import com.localgenie.utilities.Constants;
import com.localgenie.utilities.RemoveFavCallBack;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.FavoriteProviderCategoriesAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class FavouriteProvider extends DaggerAppCompatActivity implements FavouriteProviderContract.FavouriteProviderView, RemoveFavCallBack {

    private static final String TAG = "FAVORITEPROVIDER";
    @BindView(R.id.recyclerViewGrid)RecyclerView recyclerViewGrid;
    @BindView(R.id.favToolBar)Toolbar favToolBar;
    @BindView(R.id.tv_center)TextView tv_center;
    @BindView(R.id.progressBarSet)ProgressBar progressBarSet;
    @Inject FavouriteProviderContract.FavouriteProvider provider;
    @Inject
    LSPServices lspServices;
    private  ArrayList<ResponsePojo.FavProviderData> dataList = new ArrayList<>();
    private  FavProviderAdapter favProviderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_provider);
        ButterKnife.bind(this);
        toolBarSet();
        setGridForViews();
    }

    private void toolBarSet() {
        tv_center.setText(getString(R.string.favouriteProvider));
        setSupportActionBar(favToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        favToolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        favToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tv_center.setTypeface(AppTypeface.getInstance(this).getHind_semiBold());
    }

    private void setGridForViews() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        favProviderAdapter = new FavProviderAdapter(this,this);
        recyclerViewGrid.setLayoutManager(gridLayoutManager);
        recyclerViewGrid.setAdapter(favProviderAdapter);
        provider.onToGetFavouriteProvider();
    }

    @Override
    public void onResponseSuccess(ArrayList<ResponsePojo.FavProviderData> data) {

        dataList.clear();
        if(data.size()>0)
        {
            dataList.addAll(data);
        }
        favProviderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponseEmpty() {

    }

    @Override
    public void onSessionExpired() {

    }

    @Inject
    AlertProgress alertProgressl;
    @Inject
    SessionManagerImpl manager;
    @Override
    public void onLogout(String message)
    {
        alertProgressl.alertPositiveOnclick(this, message, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                // Utility.setMAnagerWithBID(AddAddressActivity.this,sessionManager);
                Utility.setMAnagerWithBID(FavouriteProvider.this,manager);
            }
        });
    }

    @Override
    public void onError(String error) {

        alertProgressl.alertinfo(this,error);
    }

    @Override
    public void onRetry(String message) {
        alertProgressl.tryAgain(this, getString(R.string.pleaseCheckInternet), getString(R.string.try_again), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                if(isClicked)
                {
                    provider.onToGetFavouriteProvider();
                }
            }
        });
    }

    @Override
    public void onShowProgress() {
        progressBarSet.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgress() {
        progressBarSet.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //  overridePendingTransition(R.anim.mainfadein,R.anim.slide_down_acvtivity);
        overridePendingTransition(R.anim.mainfadein,R.anim.side_slide_in);

    }

    @Override
    public void onRemoveFav(String catId, String providerId) {
        Observable<Response<ResponseBody>> responseObservable = lspServices.removeFromFav(manager.getAUTH(), Constants.selLang, catId, providerId);
        responseObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Response<ResponseBody> responseBodyResponse) {
                Log.d(TAG, "onNext: "+responseBodyResponse.toString());
                provider.onToGetFavouriteProvider();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private class FavProviderAdapter extends RecyclerView.Adapter
    {

        private final RemoveFavCallBack removeFavCallBack;
        private Context mContext;

         FavProviderAdapter(RemoveFavCallBack removeFavCallBack,Context mContext) {
             this.removeFavCallBack=removeFavCallBack;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.fav_pro_adapter,parent,false);
            return new ViewClassViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            ViewClassViewHolder hold = (ViewClassViewHolder) holder;
            hold.tvFavProName.setText(dataList.get(position).getName());
            StringBuilder catName = new StringBuilder();
            for(int i = 0;i<dataList.get(position).getCategoryData().size();i++)
            {
                if(catName.toString().equals(""))
                {
                    catName = new StringBuilder(dataList.get(position).getCategoryData().get(i).getCategoryName());
                }else
                {
                    catName.append(", ").append(dataList.get(position).getCategoryData().get(i).getCategoryName());
                }

            }
            hold.tvFavProCat.setText(catName.toString());
            if(!"".equals(dataList.get(position).getProfilePic()))
            {
                Glide.with(mContext)
                        .load(dataList.get(position).getProfilePic())
                        .apply(Utility.createGlideOption(mContext))
                        .into(hold.ivFavPro);
            }
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }


        private class ViewClassViewHolder extends RecyclerView.ViewHolder
        {
            ImageView ivFavPro;
            TextView tvFavProName,tvFavProCat;
            AppTypeface appTypeface;
             ViewClassViewHolder(View itemView) {
                super(itemView);
                appTypeface = AppTypeface.getInstance(mContext);
                ivFavPro = itemView.findViewById(R.id.ivFavPro);
                tvFavProName = itemView.findViewById(R.id.tvFavProName);
                tvFavProCat = itemView.findViewById(R.id.tvFavProCat);
                tvFavProName.setTypeface(appTypeface.getHind_bold());
                tvFavProCat.setTypeface(appTypeface.getHind_regular());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog=new Dialog(FavouriteProvider.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.favorite_provider_categories);
                        RecyclerView fav_provider_categories = dialog.findViewById(R.id.rv_fav_provider_categories);
                        Button btn_unFavorite = dialog.findViewById(R.id.btn_unFavorite);
                        FavoriteProviderCategoriesAdapter favoriteProviderCategories = new FavoriteProviderCategoriesAdapter(dataList.get(getAdapterPosition()).getCategoryData(), FavouriteProvider.this);
                        fav_provider_categories.setLayoutManager(new LinearLayoutManager(FavouriteProvider.this,LinearLayoutManager.VERTICAL,false));
                        fav_provider_categories.setAdapter(favoriteProviderCategories);
                        btn_unFavorite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String providerId = favoriteProviderCategories.getProviderId();
                                String selectedCategories = favoriteProviderCategories.getSelectedCategories();
                                if(providerId!=null && !providerId.isEmpty() && selectedCategories!=null && !selectedCategories.isEmpty()){
                                    removeFavCallBack.onRemoveFav(selectedCategories,providerId);
                                    dialog.dismiss();
                                }else{
                                    Toast.makeText(FavouriteProvider.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                        DisplayMetrics metrics = getResources().getDisplayMetrics();
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;
                        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                });
            }
        }
    }

}
