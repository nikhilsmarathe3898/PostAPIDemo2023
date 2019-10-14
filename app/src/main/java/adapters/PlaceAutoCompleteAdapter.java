package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.maps.model.LatLngBounds;
import com.localgenie.R;
import com.localgenie.utilities.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.pojo.address_pojo.Place_Auto_Complete_Pojo;

/**
 * <h1>PlaceAutoComplete_Adapter</h1>
 * This class is used to provide the PlaceAutoComplete screen, where we can see our Address.
 * @author 3embed (Shubham)
 * @since 3 Jan 2017.
 */
public class PlaceAutoCompleteAdapter extends RecyclerView.Adapter<PlaceAutoCompleteAdapter.PlaceViewHolder> implements Filterable {

    private static final String TAG = "PlaceAutcmpltAdapter";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    private static final String LOG_TAG = "Google Place complete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
   // private static final String API_KEY = "AIzaSyC8nl5QpifmNAcAgQe-0v9dQW6SPNCpBRY";
    private static final String API_KEY = Constants.SERVER_KEY;
    private Context mContext;
    private PlaceAutoCompleteInterface mListener;
    private ArrayList<Place_Auto_Complete_Pojo> mResultList;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds mBounds;
    private int layout;
    private AutocompleteFilter mPlaceFilter;
    /**
     * Constructor for initializing the Adapter class.
     * @param context contains the context of calling activity.
     * @param resource contains the layout resource.
     * @param googleApiClient GoogleApiClient context.
     * @param bounds Lat long boundaries
     * @param filter Filter.
     */
    public PlaceAutoCompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds, AutocompleteFilter filter){
        this.mContext = context;
        layout = resource;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
        this.mListener = (PlaceAutoCompleteInterface)mContext;
    }

    /**
     * This is the method for calling, the API, while entering the input text on Edit box.
     * @param input , contains the inputted string, which we entered on Edit text.
     * @return the complete list of PlaceAutoComplete Response getting from API calling.
     */
    private static ArrayList<Place_Auto_Complete_Pojo> autocomplete(String input,String lat,String lng) {
        ArrayList resultList = null;
        ArrayList<Place_Auto_Complete_Pojo> auto_complete_pojo_list = new ArrayList<Place_Auto_Complete_Pojo>();
        Place_Auto_Complete_Pojo auto_complete_pojo = new Place_Auto_Complete_Pojo();

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&location="+lat+","+lng+"&radius=500&amplanguage=en");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            Log.d(TAG, " urL : "+sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            Log.d(TAG," ,address: "+jsonResults);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            Log.d(TAG," ,address: "+jsonObj+ " -----, "+predsJsonArray);
            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                auto_complete_pojo = new Place_Auto_Complete_Pojo();
                Log.d(TAG," response:"+predsJsonArray.getJSONObject(i).getString("description"));
                Log.d(TAG,"============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                auto_complete_pojo.setAddress(predsJsonArray.getJSONObject(i).getString("description"));
                auto_complete_pojo.setRef_key(predsJsonArray.getJSONObject(i).getString("reference"));
                auto_complete_pojo.setPlace_id(predsJsonArray.getJSONObject(i).getString("place_id"));
                auto_complete_pojo_list.add(i, auto_complete_pojo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return auto_complete_pojo_list;
    }

    /*
    Clear List items
     */
    public void clearList(){
        if(mResultList!=null && mResultList.size()>0){
            mResultList.clear();
        }
    }
    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                   Log.d(TAG,"HIACONSTAIN "+constraint);
                     mResultList = autocomplete(constraint.toString(),Constants.currentLat+"",
                             Constants.currentLng+"");
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                }
            }
        };
        return filter;
    }

    /**
     * Method where, we are inflating our Layout and view and can perform work on the view.
     * @param viewGroup , ViewGroup
     * @param viewType
     * @return
     */
    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(layout, viewGroup, false);
        PlaceViewHolder mPredictionHolder = new PlaceViewHolder(convertView);
        return mPredictionHolder;
    }

    /**
     * For doing work on our views.
     * @param mPredictionHolder ViewHolder
     * @param i position
     */
    @Override
    public void onBindViewHolder(final PlaceViewHolder mPredictionHolder, int i) {
        Log.d(TAG,"value type:"+mResultList.get(i));
        String addressValue = ""+mResultList.get(i).getAddress();
        mPredictionHolder.mAddress.setText(addressValue);
        mPredictionHolder.mParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPlaceClick(mResultList,mPredictionHolder.getAdapterPosition());
            }
        });

    }

    /**
     * Getting the Total Item.
     * @return size.
     */
    @Override
    public int getItemCount() {
       return mResultList == null ? 0 : mResultList.size();
    }

    /**
     * Getting the current item data.
     * @param position current position
     * @return Actual data.
     */
    public Place_Auto_Complete_Pojo getItem(int position) {
        return mResultList.get(position);
    }

    public interface PlaceAutoCompleteInterface{
        public void onPlaceClick(ArrayList<Place_Auto_Complete_Pojo> mResultList, int position);
    }

    /*
    View Holder For Trip History
     */
    class PlaceViewHolder extends RecyclerView.ViewHolder {
        private CardView mParentLayout;
        private TextView mAddress;

        private PlaceViewHolder(View itemView) {
            super(itemView);
            mParentLayout = (CardView)itemView.findViewById(R.id.predictedRow);
            mAddress = (TextView)itemView.findViewById(R.id.address);
        }
    }
}
