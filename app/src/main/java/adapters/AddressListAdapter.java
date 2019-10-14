package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.add_address.AddAddressActivity;
import com.localgenie.model.youraddress.YourAddrData;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.youraddress.YourAddressPresenter;

import java.util.List;

/**
 * @author Pramod
 * @since 19-01-2018.
 */

public class AddressListAdapter extends RecyclerView.Adapter {
    Context context;
    private YourAddressPresenter presenter;
    private String auth;
    private AppTypeface appTypeface;
    private boolean isBidding;
    public int selectedItem = -1;
    List<YourAddrData> items;

    public AddressListAdapter(Context context, List<YourAddrData> items, YourAddressPresenter presenter, String auth,boolean isBidding) {
        // super(context, 0, items);
        this.context = context;
        this.auth = auth;
        this.presenter = presenter;
        this.isBidding = isBidding;
        this.items = items;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(context).inflate(R.layout.your_address_row,parent,false);

        return new ViewHolderRecycler(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderRecycler hold = (ViewHolderRecycler) holder;

        hold.tv_edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, AddAddressActivity.class);
                if (items.get(hold.getAdapterPosition()) != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", items.get(hold.getAdapterPosition()));
                    bundle.putString("edit_addr", "y");
                    intent.putExtras(bundle);
                    Activity activity = (Activity) context;
                    activity.startActivityForResult(intent, 101);

                }
            }
        });

        hold.tv_delete_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (items.get(hold.getAdapterPosition()) != null) {
                    presenter.deleteAddress(auth, items.get(hold.getAdapterPosition()).getId(), items.get(hold.getAdapterPosition()),hold.getAdapterPosition());
                }
            }
        });


        String addressType = items.get(position).getTaggedAs();
        if(isBidding)
        {
            hold.iv_address_type.setImageResource(R.drawable.circle_check_uncheck);
            if(position == selectedItem)
            {
                hold.iv_address_type.setSelected(true);
            }else
            {
                hold.iv_address_type.setSelected(false);
            }

        }else
        {
            if(context.getResources().getString(R.string.home).equals(addressType))
            {
                hold.iv_address_type.setImageResource(R.drawable.ic_home);
                hold.tv_type.setText(context.getString(R.string.home));
            }else if( context.getResources().getString(R.string.office).equals(addressType))
            {
                hold.iv_address_type.setImageResource(R.drawable.ic_work);
                hold.tv_type.setText(context.getString(R.string.work));
            }else
            {
                hold.iv_address_type.setImageResource(R.drawable.ic_other_addr);
                hold.tv_type.setText(addressType);
            }

        }


        //to set Address
        String addressLine1;
        if(items.get(position).getHouseNo()!=null && !items.get(position).getHouseNo().equals(""))
        {
            addressLine1 = items.get(position).getHouseNo()+", "+items.get(position).getAddLine1();
        }else
        {
            addressLine1 = items.get(position).getAddLine1();
        }
        hold.tv_address1.setText(addressLine1);
        if (!TextUtils.isEmpty(items.get(position).getAddLine2()))
            hold.tv_address2.setText(items.get(position).getAddLine2());
        else
            hold.tv_address2.setVisibility(View.GONE);

        if(position == items.size()-1)
        {
            hold.viewDivider.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void removeSelectedItem() {
        if(selectedItem!=-1)
        {
            notifyItemChanged(selectedItem);
            selectedItem = -1;
            notifyItemChanged(selectedItem);

        }
    }

    /**
     * <h2>ViewHolder</h2>
     * This method is used to hold the views
     */
    private class ViewHolderRecycler extends RecyclerView.ViewHolder {
        ImageView iv_address_type;
        //iv_delete
        TextView tv_type, tv_address1, tv_address2;
        TextView tv_edit_address, tv_delete_address;
        View viewDivider;

        public ViewHolderRecycler(View itemView)
        {
            super(itemView);
            appTypeface = AppTypeface.getInstance(context);
            iv_address_type = itemView.findViewById(R.id.ivAddressType);
            viewDivider = itemView.findViewById(R.id.viewDivider);
            tv_type = itemView.findViewById(R.id.tvType);
            tv_address1 = itemView.findViewById(R.id.tvAddress1);
            tv_address2 = itemView.findViewById(R.id.tvAddress2);
            tv_edit_address = itemView.findViewById(R.id.tvEditAddress);
            tv_delete_address = itemView.findViewById(R.id.tvDeleteAddress);
            tv_delete_address.setTypeface(appTypeface.getHind_semiBold());
            tv_type.setTypeface(appTypeface.getHind_semiBold());
            tv_edit_address.setTypeface(appTypeface.getHind_semiBold());
            tv_address2.setTypeface(appTypeface.getHind_regular());
            tv_address1.setTypeface(appTypeface.getHind_regular());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.onItemClicked(getAdapterPosition());
                    if(isBidding)
                    {
                        notifyItemChanged(selectedItem);
                        selectedItem = getAdapterPosition();
                        notifyItemChanged(selectedItem);
                    }
                }
            });
        }
    }
    public void deleteItem(int index) {
        items.remove(index);
        notifyItemRemoved(index);
    //    notifyItemRangeChanged(index, items.size());

    }

}
