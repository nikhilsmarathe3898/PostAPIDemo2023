package adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.promocode.PromoCodeContract;
import com.localgenie.utilities.AppTypeface;
import com.pojo.PromoCodeResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Ali on 5/18/2018.
 */
public class PromoCodeAdapter extends RecyclerView.Adapter
{
    private Context mContext;
    private ArrayList<PromoCodeResponse.PromoCodeData> promoCodeData;
    private PromoCodeContract.PromoPresent baseView;
    public PromoCodeAdapter(Context mContext, ArrayList<PromoCodeResponse.PromoCodeData> promoCodeData , PromoCodeContract.PromoPresent baseView) {
        this.mContext = mContext;
        this.promoCodeData = promoCodeData;
        this.baseView = baseView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.promo_code_list,parent,false);
        return new ViewPromoCode(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewPromoCode hold = (ViewPromoCode) holder;
        hold.tvPromoCode.setText(promoCodeData.get(position).getCode());
        hold.tvPromoCodeApply.setOnClickListener(applyPromoCode(promoCodeData.get(position).getCode()));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            hold.tvPromoCodeHowTo.setText(Html.fromHtml(promoCodeData.get(position).getDescription(), Html.FROM_HTML_MODE_COMPACT));
            hold.tvPromoCodeHowToDesc.setText(Html.fromHtml(promoCodeData.get(position).getHowItWorks(), Html.FROM_HTML_MODE_COMPACT));
          /*  Spanned moreDetails = Html.fromHtml(promoCodeData.get(position).getTermsAndConditions(), Html.FROM_HTML_MODE_COMPACT);
            Log.d("TAG", "onBindViewHolder: "+moreDetails);
            List<Spanned> spans =  parse(promoCodeData.get(position).getTermsAndConditions());
            Log.d("TAG", "onBindViewHolderSpanned: "+spans.size() +" spans "+spans);
            spans.remove(0);
            spans.remove(spans.size()-1);
            Log.d("TAG", "onBindViewHolderSpanned: "+spans.size() +" spans "+spans);*/

        }else
        {
         //

            hold.tvPromoCodeHowTo.setText(Html.fromHtml(promoCodeData.get(position).getDescription()));
            hold.tvPromoCodeHowToDesc.setText(Html.fromHtml(promoCodeData.get(position).getHowItWorks()));
          /*  Spanned moreDetails = Html.fromHtml(promoCodeData.get(position).getTermsAndConditions());
            Log.d("TAG", "onBindViewHolder: "+moreDetails);*/

        }




        if(position == promoCodeData.size()-1)
            hold.promoView.setVisibility(View.GONE);
    }

    public List<Spanned> parse(String termsAndConditions) {
        List<Spanned> spans = new ArrayList<Spanned>();
        Spannable unsegmented = (Spannable) Html.fromHtml(termsAndConditions);
        //Set ColorSpan because it defaults to white text color
        unsegmented.setSpan(new ForegroundColorSpan(Color.BLACK), 0, unsegmented.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        //get locations of '/n'
        Stack<Integer> loc = getNewLineLocations(unsegmented);
        loc.push(unsegmented.length());

        //divides up a span by each new line character position in loc
        while (!loc.isEmpty()) {
            Integer end = loc.pop();
            Integer start = loc.isEmpty() ? 0 : loc.peek();

            spans.add(0,(Spanned) unsegmented.subSequence(start, end));
        }

        return spans;
    }

    private Stack<Integer> getNewLineLocations(Spanned unsegmented) {
        Stack<Integer> loc = new Stack<>();
        String string = unsegmented.toString();
        int next = string.indexOf('\n');
        while (next > 0) {
            //avoid chains of newline characters
            if (string.charAt(next - 1) != '\n') {
                loc.push(next);
                next = string.indexOf('\n', loc.peek() + 1);
            } else {
                next = string.indexOf('\n', next + 1);
            }
            if (next >= string.length()) next = -1;
        }
        return loc;
    }

    @Override
    public int getItemCount() {
        return promoCodeData == null ? 0 : promoCodeData.size();
    }

    private class ViewPromoCode extends RecyclerView.ViewHolder
    {
        TextView tvPromoCode,tvPromoCodeApply,tvPromoCodeHowTo,tvPromoCodeHowToDesc,tvPromoCodeviewMore;
        View promoView;
        private AppTypeface appTypeface;
      //  private RecyclerView recyclerViewDetails;
        private LinearLayout llPromoDetails;
        private TextView tvPromoCodeDetails,tvPromoCodeHowToDetail,tvPromoDetailsApply,tvAddedDescription;
        private BottomSheetBehavior sheetBehavior;

        public ViewPromoCode(View itemView) {
            super(itemView);
            tvPromoCode = itemView.findViewById(R.id.tvPromoCode);
            tvPromoCodeApply = itemView.findViewById(R.id.tvPromoCodeApply);
            tvPromoCodeHowTo = itemView.findViewById(R.id.tvPromoCodeHowTo);
            tvPromoCodeHowToDesc = itemView.findViewById(R.id.tvPromoCodeHowToDesc);
            tvPromoCodeviewMore = itemView.findViewById(R.id.tvPromoCodeviewMore);
            promoView = itemView.findViewById(R.id.promoView);

          //  recyclerViewDetails = itemView.findViewById(R.id.recyclerViewDetails);
           // LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
          //  llPromoDetails = itemView.findViewById(R.id.llPromoDetails);
           // tvAddedDescription = itemView.findViewById(R.id.tvAddedDescription);
          //  tvPromoCodeDetails = itemView.findViewById(R.id.tvPromoCodeDetails);
          //  tvPromoCodeHowToDetail = itemView.findViewById(R.id.tvPromoCodeHowToDetail);
          //  tvPromoDetailsApply = itemView.findViewById(R.id.tvPromoDetailsApply);


          //  recyclerViewDetails.setLayoutManager(layoutManager);
          //  sheetBehavior = BottomSheetBehavior.from(llPromoDetails);

            appTypeface = AppTypeface.getInstance(mContext);
           /*
           tvPromoCodeHowToDetail.setTypeface(appTypeface.getHind_regular());
            tvPromoCodeDetails.setTypeface(appTypeface.getHind_semiBold());
            tvPromoDetailsApply.setTypeface(appTypeface.getHind_semiBold());
            tvAddedDescription.setTypeface(appTypeface.getHind_medium());*/
            tvPromoCode.setTypeface(appTypeface.getHind_semiBold());
            tvPromoCodeApply.setTypeface(appTypeface.getHind_semiBold());
            tvPromoCodeHowTo.setTypeface(appTypeface.getHind_medium());
            tvPromoCodeHowToDesc.setTypeface(appTypeface.getHind_regular());
            tvPromoCodeviewMore.setTypeface(appTypeface.getHind_medium());




            tvPromoCodeviewMore.setOnClickListener(view -> {

                baseView.onViewMoreClicked(promoCodeData.get(getAdapterPosition()));

            });



        }
    }

    private View.OnClickListener applyPromoCode(String promoCodeData) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("TAG", "onClick: "+promoCodeData);
                baseView.onLogout(promoCodeData);
            }
        };
    }
}
