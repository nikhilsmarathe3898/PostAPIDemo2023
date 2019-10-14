package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;

/**
 * <h>SidescreenAdapter</h>
 * Created by ${3Embed} on 4/10/17.
 */

public class SidescreenAdapter extends RecyclerView.Adapter {
    String[] sideScreenNames;
    int[] images;
    Context context;
    OnSideScreenClick onSideScreenClick;

    public SidescreenAdapter(String[] sideScreenNames, int[] images, Context context,OnSideScreenClick onSideScreenClick){
        this.context=context;
        this.sideScreenNames=sideScreenNames;
        this.images=images;
        this.onSideScreenClick = onSideScreenClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_sidescreen_layout,null);
        VhSidescreen viewHolder = new VhSidescreen(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VhSidescreen viewHolder= (VhSidescreen) holder;
        viewHolder.tvScreen.setText(sideScreenNames[position]);
        if(sideScreenNames[position].equals("Wallet"))
        {
            Utility.setAmtOnRecept(Constants.walletAmount,viewHolder.tvWalletAmount,Constants.walletCurrency);
            String walletAmount = "("+viewHolder.tvWalletAmount.getText().toString()+")";

            viewHolder.tvWalletAmount.setText(walletAmount);

            //viewHolder.tvWalletAmount.setText("$ 5000.00");
        }
        viewHolder.ivImage.setImageDrawable(context.getResources().getDrawable(images[position]));
        if(position==(getItemCount()-1)){
            viewHolder.divider.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return sideScreenNames == null ? 0 : sideScreenNames.length;
    }
    public interface OnSideScreenClick{
        void onSideScreenClicked(String sideScreenName);
    }

    class VhSidescreen extends RecyclerView.ViewHolder{
        ImageView ivImage;
        TextView tvScreen,tvWalletAmount;
        View divider;
        AppTypeface appTypeface;

        public VhSidescreen(View itemView) {
            super(itemView);
            ivImage=itemView.findViewById(R.id.ivImage);
            tvScreen=itemView.findViewById(R.id.tvScreens);
            divider=itemView.findViewById(R.id.divider);
            tvWalletAmount=itemView.findViewById(R.id.tvWalletAmount);
            appTypeface = AppTypeface.getInstance(context);
            tvScreen.setTypeface(appTypeface.getHind_medium());
            tvWalletAmount.setTypeface(appTypeface.getHind_semiBold());

            tvScreen.setTypeface(AppTypeface.getInstance(context).getHind_medium());
            itemView.setOnClickListener(view -> {
                onSideScreenClick.onSideScreenClicked(sideScreenNames[getAdapterPosition()]);
            });
        }
    }
}
