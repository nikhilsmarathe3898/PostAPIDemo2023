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
import com.localgenie.utilities.Utility;
import com.localgenie.walletTransaction.CreditDebitTransctions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * <h1>WalletTransactionsAdapter</h1>
 * This class is used to inflate the wallet transactions list
 *
 * @since 20/09/17.
 */
public class WalletTransactionsAdapter extends RecyclerView.Adapter<WalletTransactionsAdapter.WalletViewHolder> {

    private Context mContext;
    private ArrayList<CreditDebitTransctions> transactionsAL;


    /**
     * <h2>WalletTransactionsAdapter</h2>
     * This is the constructor of our adapter.
     */
    public WalletTransactionsAdapter(Context context, ArrayList<CreditDebitTransctions> _transactionsAL) {
        this.mContext = context;
        this.transactionsAL = _transactionsAL;


    }

    @Override
    public WalletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewList = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_transactions, parent, false);
        return new WalletViewHolder(viewList);

    }

    @Override
    public void onBindViewHolder(WalletViewHolder walletViewHolder, final int position) {
        CreditDebitTransctions walletDataDetailsItem = transactionsAL.get(position);

        if (walletDataDetailsItem.getTxnType().equalsIgnoreCase("DEBIT")) {
            walletViewHolder.iv_wallet_transaction_arrow.setBackgroundColor(mContext.getResources().getColor(R.color.red_google));
            walletViewHolder.iv_wallet_transaction_arrow.setRotation(90);
        } else {
            walletViewHolder.iv_wallet_transaction_arrow.setBackgroundColor(mContext.getResources().getColor(R.color.parrotGreen));
            walletViewHolder.iv_wallet_transaction_arrow.setRotation(-90);
        }

        walletViewHolder.tv_wallet_transactionId.setText(
                mContext.getString(R.string.transactionID) + " " + walletDataDetailsItem.getTxnId().trim());

        if (walletDataDetailsItem.getTripId().isEmpty() || walletDataDetailsItem.getTripId().equalsIgnoreCase("N/A")) {
            walletViewHolder.tv_wallet_transaction_bid.setVisibility(View.GONE);
        } else {
            walletViewHolder.tv_wallet_transaction_bid.setText(mContext.getString(R.string.bookingID) + walletDataDetailsItem.getTripId());
        }


        Utility.setAmtOnRecept(walletDataDetailsItem.getAmount(),walletViewHolder.tv_wallet_transaction_amount, walletDataDetailsItem.getCurrency());

        walletViewHolder.tv_wallet_transaction_description.setText(walletDataDetailsItem.getTrigger().trim());
        walletViewHolder.tv_wallet_transaction_date.setText(epochTimeConverter(walletDataDetailsItem.getTimestamp().trim()));
    }

    @Override
    public int getItemCount() {
        return transactionsAL == null ? 0 : transactionsAL.size();
    }
    //====================================================================

    /**
     * <h2>epochTimeConverter</h2>
     * <p>
     * method to convert received epoch seconds into formatted date time string
     * </p>
     *
     * @param milliSecsString input the milli sec
     * @return returns the date in the format dd MMM yyyy, hh:mm a
     */
    private String epochTimeConverter(String milliSecsString) {
        if (milliSecsString.isEmpty()) {
            return "";
        }
        long milliSecs = Long.parseLong(milliSecsString);
        Date date = new Date(milliSecs * 1000);
        DateFormat format = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        format.setTimeZone(Utility.getTimeZone());
        return format.format(date);
    }
    //====================================================================

    /**
     * <h1>ListViewHolder</h1>
     * <p>
     * This method is used to hold the views
     * </p>
     */
    class WalletViewHolder extends RecyclerView.ViewHolder {
        TextView tv_wallet_transactionId, tv_wallet_transaction_bid, tv_wallet_transaction_amount;
        TextView tv_wallet_transaction_description, tv_wallet_transaction_date;
        ImageView iv_wallet_transaction_arrow;

        AppTypeface appTypeface;

        WalletViewHolder(View cellView) {
            super(cellView);
            appTypeface = AppTypeface.getInstance(mContext);
            tv_wallet_transactionId = cellView.findViewById(R.id.tv_wallet_transactionId);
            tv_wallet_transactionId.setTypeface(appTypeface.getHind_regular());

            tv_wallet_transaction_bid = cellView.findViewById(R.id.tv_wallet_transaction_bid);
            tv_wallet_transaction_bid.setTypeface(appTypeface.getHind_regular());

            tv_wallet_transaction_amount = cellView.findViewById(R.id.tv_wallet_transaction_amount);
            tv_wallet_transaction_amount.setTypeface(appTypeface.getHind_medium());

            tv_wallet_transaction_description = cellView.findViewById(R.id.tv_wallet_transaction_description);
            tv_wallet_transaction_description.setTypeface(appTypeface.getHind_regular());

            tv_wallet_transaction_date = cellView.findViewById(R.id.tv_wallet_transaction_date);
            tv_wallet_transaction_date.setTypeface(appTypeface.getHind_regular());

            iv_wallet_transaction_arrow = cellView.findViewById(R.id.iv_wallet_transaction_arrow);
        }
    }
    //================================================================/
}
