package adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.model.faq.FaqData;
import com.localgenie.model.faq.Subcat;

import java.util.List;

/**
 * @author Pramod
 * @since  23-01-2018.
 */

public class FaqDetailAdapter extends BaseAdapter {
    private Context context;

    private boolean isFaqDetails;
    private List<Subcat> items;
    private List<FaqData> faqData;


    public FaqDetailAdapter(@NonNull Context context) {
        this.context = context;
    }

    public void faqDetails(boolean isFaqDetails,List<Subcat> items, List<FaqData> faqData)
    {
        this.isFaqDetails = isFaqDetails;
        this.items = items;
        this.faqData = faqData;
        Log.d("TAG", "faqDetails: "+isFaqDetails);
    }

    @Override
    public int getCount()
    {
        if(isFaqDetails)
        {
            return items.size();
        }else
            return faqData.size();
    }

    @Override
    public Object getItem(int i)
    {
        if(isFaqDetails)
        {
            return items.get(i);
        }else
            return faqData.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        if(isFaqDetails)
        {
            return items.size();
        }else
            return faqData.size();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder ;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            assert mInflater != null;
            convertView = mInflater.inflate(R.layout.faq_list_row, null);
            holder = new ViewHolder();
            holder.tv_name = convertView.findViewById(R.id.tvFaqName);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if(isFaqDetails)
        {
            final Subcat rowItem = (Subcat) getItem(position);
            assert rowItem != null;
            holder.tv_name.setText(rowItem.getName());
        }else
        {
            final FaqData rowItemData = (FaqData) getItem(position);

            assert rowItemData != null;

            holder.tv_name.setText(rowItemData.getName());

        }


        return convertView;
    }

    /**
     * <h2>ViewHolder</h2>
     * This method is used to hold the views
     */
    private class ViewHolder {
        TextView tv_name;
    }
}
