package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.pojo.ProviderDetailsResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <h>LangExpertiseAdapter</h>
 * Created by Ali on 2/6/2018.
 */

public class LangExpertiseAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private String[] langExpertise;
    private boolean isIconAvailable;
    private ArrayList<ProviderDetailsResponse.PredefinedArray> preDefined;
    private String TAG = LangExpertiseAdapter.class.getSimpleName();
    private boolean isPromoCode = false;
    private List<Spanned> spans;
    private List<Spanned> promoCodeAdapter;

    public LangExpertiseAdapter(Context mContext, String[] langExpertise, boolean isIconAvailable, ArrayList<ProviderDetailsResponse.PredefinedArray> preDefined) {
        this.mContext = mContext;
        this.langExpertise = langExpertise;
        this.isIconAvailable = isIconAvailable;
        this.preDefined = preDefined;
    }

    LangExpertiseAdapter(Context mContext, List<Spanned> spans, boolean b)
    {
        isPromoCode = b;
        this.spans = spans;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_cell_lang_expertise,parent,false);
        return new ViewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolders holders = (ViewHolders) holder;
        if(isPromoCode)
        {
            holders.imageIconMeta.setVisibility(View.GONE);
            holders.tvBulletIcon.setVisibility(View.GONE);
            holders.tvLangExpertise.setText(spans.get(position));
        }else
        {
            if(isIconAvailable)
            {
                holders.tvBulletIcon.setVisibility(View.GONE);
                holders.imageIconMeta.setVisibility(View.VISIBLE);
                if(!"".equals(preDefined.get(position).getIcon()))
                {
                    Glide.with(mContext)
                            .load(preDefined.get(position).getIcon())
                            .into(holders.imageIconMeta);


                }
                holders.tvLangExpertise.setText(preDefined.get(position).getName());
            }else
            {
                holders.imageIconMeta.setVisibility(View.GONE);
                holders.tvBulletIcon.setVisibility(View.VISIBLE);
                holders.tvLangExpertise.setText(langExpertise[position]);
            }
        }

    }

    @Override
    public int getItemCount()
    {
        if(isPromoCode)
        {
            return spans == null ? 0 : spans.size();
        }else
        {
            if(isIconAvailable)
                return preDefined == null ? 0 : preDefined.size();
            else
                return langExpertise == null ? 0 : langExpertise.length;
        }

    }

    class ViewHolders extends RecyclerView.ViewHolder
    {

        @BindView(R.id.tvLangExpertise)TextView tvLangExpertise;
        @BindView(R.id.tvBulletIcon)TextView tvBulletIcon;
        @BindView(R.id.imageIconMeta)ImageView imageIconMeta;
        private AppTypeface appTypeface;

        public ViewHolders(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            appTypeface = AppTypeface.getInstance(mContext);
            tvLangExpertise.setTypeface(appTypeface.getHind_regular());


        }
    }
}
