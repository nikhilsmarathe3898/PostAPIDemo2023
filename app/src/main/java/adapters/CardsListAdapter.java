package adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.model.payment_method.CardGetData;
import com.localgenie.utilities.Utility;

import java.util.List;

/**
 * @author Pramod
 * @since  18-01-2018.
 */

public class CardsListAdapter extends ArrayAdapter<CardGetData> {
    Context context;

    public CardsListAdapter(Context context, int resourceId, List<CardGetData> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder ;
        final CardGetData rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            assert mInflater != null;
            convertView = mInflater.inflate(R.layout.item_card_list, null);
            holder = new ViewHolder();
            holder.tv_payment_card_number = convertView.findViewById(R.id.tv_payment_card_number);
            holder.iv_payment_card = convertView.findViewById(R.id.iv_payment_card);
            holder.iv_payment_tick = convertView.findViewById(R.id.iv_payment_tick);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        assert rowItem != null;
        //to set the image of card based on the card type
        holder.iv_payment_card.setImageBitmap(Utility.setCreditCardLogo(rowItem.getBrand(),context));
        //to set the card number
        holder.tv_payment_card_number.setText("**** **** **** "+rowItem.getLast4());
        //to show/hide the tick depend on the default card/non default card
        if(rowItem.getIsDefault())
            holder.iv_payment_tick.setVisibility(View.VISIBLE);
        else
            holder.iv_payment_tick.setVisibility(View.GONE);

        return convertView;
    }

    /**
     * <h2>ViewHolder</h2>
     * This method is used to hold the views
     */
    private class ViewHolder {
        ImageView iv_payment_card,iv_payment_tick;
        TextView tv_payment_card_number;
    }
}
