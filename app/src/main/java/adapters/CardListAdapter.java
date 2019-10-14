package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.model.payment_method.CardGetData;
import com.localgenie.selectPaymentMethod.SelectedCardInfoInterface;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <h>CardListAdapter</h>
 * Created by Ali on 3/12/2018.
 */

public class CardListAdapter extends RecyclerView.Adapter
{
    private Context mContext;
    private ArrayList<CardGetData> cardItem;
    private int selectedPosition = 0;
    private SelectedCardInfoInterface.SelectedView selectedView;

    public CardListAdapter(Context mContext, ArrayList<CardGetData> cardItem, SelectedCardInfoInterface.SelectedView selectedView) {
        this.mContext = mContext;
        this.cardItem = cardItem;
        this.selectedView = selectedView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_item_list,parent,false);
        return new ViewHolderNormal(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Log.d("TAG", "onBindViewHolder: "+position+" holder "+holder.getItemViewType());
        Log.d("TAG", "onBindViewHolder: "+cardItem.get(position).getLast4());
        ViewHolderNormal vHolderNormal = (ViewHolderNormal) holder;
        String cardNum = mContext.getString(R.string.stars)+" "+cardItem.get(position).getLast4();
        vHolderNormal.cardInfo.setText(cardNum);
        if(selectedPosition == position)
        {
            vHolderNormal.cardInfo.setSelected(true);
            vHolderNormal.cardInfo.setCompoundDrawablesWithIntrinsicBounds(null,null,mContext.getResources().getDrawable(R.drawable.ic_check_black_24dp),null);
            vHolderNormal.tvSelectedCardPay.setVisibility(View.GONE);//VISIBLE

        }else
        {
            vHolderNormal.cardInfo.setSelected(false);
            vHolderNormal.cardInfo.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);

            vHolderNormal.tvSelectedCardPay.setVisibility(View.GONE);
        }

        vHolderNormal.ivCardImage.setImageBitmap(Utility.setCreditCardLogo(cardItem.get(position).getBrand(),mContext));
    }

    @Override
    public int getItemCount() {
        //Log.d("TAG", "getItemCount: "+cardItem.size());
        return cardItem == null ? 0 : cardItem.size();
    }


    class ViewHolderNormal extends RecyclerView.ViewHolder
    {
        @BindView(R.id.ivCardImage)ImageView ivCardImage;
        @BindView(R.id.cardInfo)TextView cardInfo;
        @BindView(R.id.tvSelectedCardPay)TextView tvSelectedCardPay;
        private AppTypeface appTypeface;
        ViewHolderNormal(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            appTypeface = AppTypeface.getInstance(mContext);
            cardInfo.setTypeface(appTypeface.getHind_regular());
            tvSelectedCardPay.setTypeface(appTypeface.getHind_semiBold());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedView.onVisibilitySet();
                    notifyItemChanged(selectedPosition);
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    selectedView.onToBackIntent(getAdapterPosition());
                }
            });

            /*tvSelectedCardPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selectedView.onToBackIntent(getAdapterPosition());
                }
            });*/
        }
    }

    public void onAdapterChanged(int Position)
    {
        notifyItemChanged(selectedPosition);
        selectedPosition = Position;
        notifyItemChanged(selectedPosition);
    }

}
