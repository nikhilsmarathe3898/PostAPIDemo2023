package com.localgenie.add_address;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import adapters.DataBaseHelper;
import adapters.PlaceAutoCompleteAdapter;
import adapters.RecyclrAdapter;

import com.localgenie.utilities.MyLinearLayoutManager;
import com.pojo.address_pojo.DbSavedAddress;
import com.pojo.address_pojo.Drop_Location_Google_Pojo;
import com.pojo.address_pojo.Place_Auto_Complete_Pojo;

/**
 * <h>SearchAddressLocation</h>
 * Created by Ali on 1/9/2018.
 */

public class SearchAddressLocation extends AppCompatActivity implements PlaceAutoCompleteAdapter.PlaceAutoCompleteInterface, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,View.OnClickListener
{
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
    private String TAG = SearchAddressLocation.class.getSimpleName();
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private RecyclerView mRecyclerView;
    private PlaceAutoCompleteAdapter mAdapter;
    private ArrayList<DbSavedAddress> mSavedAddressList;
    private RecyclrAdapter mSavedAdapter;
    private EditText mSearchEdittext;
    private ImageView mClear;
    private DataBaseHelper dataBaseHelper;
    // private int keyId;
  //  private SharedPrefs manager;
    //  private LinearLayout llout;

    /**
     * Creating the request list for getting the lat-long based on our requested address.*/
    private static String getPlaceDetailsUrl(String ref)
    {
       // String key = "AIzaSyC8nl5QpifmNAcAgQe-0v9dQW6SPNCpBRY";
        String key = Constants.SERVER_KEY;
        //AIzaSyDsPjrUONYvtcd4Nbon-3-db43vZlkgZrU
        String reference = "reference="+ref;                // reference of place
        String sensor = "sensor=false";                     // Sensor enabled
        String parameters = reference+"&"+sensor+"&"+key;   // Building the parameters to the web service
        String output = "json";                             // Output format
        // Building the url to the web service
        // return "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;
        return "https://maps.googleapis.com/maps/api/place/details/json?placeid="+ref+"&key="+key;
    }

    /**
     * This method is providing the LAT-LONG based on the URL, we got from getPlaceDetailsUrl().
     * @param inputURL represent a URL.
     * @return DbSavedAddress
     */

    public static DbSavedAddress getPlaceData(String inputURL) {
        DbSavedAddress dropAddressPoj = new DbSavedAddress();
        Log.d("TAG","value of url: activity: "+inputURL);
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            URL url = new URL(inputURL);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            Log.d("TAG"," ,address:11: "+jsonResults);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("TAG", "Error API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        Gson gson = new Gson();
        Drop_Location_Google_Pojo google_pojo = gson.fromJson(jsonResults.toString(), Drop_Location_Google_Pojo.class);

        if(google_pojo.getStatus().equals("OK"))
        {
            double lat = google_pojo.getResult().getGeometry().getLocation().getLat();
            double lng = google_pojo.getResult().getGeometry().getLocation().getLng();
            dropAddressPoj.setAddress_lat(lat);
            dropAddressPoj.setAddress_lng(lng);
            dropAddressPoj.setAddress_name(google_pojo.getResult().getName());
            dropAddressPoj.setAddress_formate(google_pojo.getResult().getFormatted_address());
        }
        return dropAddressPoj;
    }

    /**
     * This is the method that got called by system and connect the google api client.
     */
    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * This is used to disconnecting the api client.
     */
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * This is the onCreate method that is called firstly, when user came to login screen.
     * @param savedInstanceState contains an instance of Bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchaddresslocation);
        mContext = SearchAddressLocation.this;
        dataBaseHelper = new DataBaseHelper(mContext);
     //   manager = new SharedPrefs(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        initViews();
    }

    /**
     * This method initialize the all UI elements of our layout.
     */
    private void initViews(){
        String comingfrom = "";
        if(getIntent().getExtras()!=null)
        {
            comingfrom = getIntent().getStringExtra("CominFROM");
        }
        Toolbar actionbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(actionbar);
        TextView tvgeneralTitle =  findViewById(R.id.tv_center);
        tvgeneralTitle.setText("Search Location");
        actionbar.setNavigationIcon(R.drawable.ic_clear_black_24dp);
        tvgeneralTitle.setTypeface(AppTypeface.getInstance(this).getHind_semiBold());
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RelativeLayout rl_my_location = (RelativeLayout) findViewById(R.id.rl_my_location);
        TextView tv_select_map = (TextView) findViewById(R.id.tv_select_map);
        mRecyclerView = (RecyclerView)findViewById(R.id.list_search);
        mRecyclerView.setHasFixedSize(true);
        MyLinearLayoutManager llm = new MyLinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);
        mSearchEdittext = (EditText)findViewById(R.id.search_et);
        mClear = (ImageView)findViewById(R.id.clear);
        mSavedAddressList = dataBaseHelper.getAllSavedAddress();

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAdapter = new PlaceAutoCompleteAdapter(this, R.layout.view_placesearch, mGoogleApiClient, BOUNDS_INDIA, null);
        mSavedAdapter = new RecyclrAdapter(this, mSavedAddressList,SearchAddressLocation.this);
        mRecyclerView.setAdapter(mSavedAdapter);
        if(comingfrom.equals("HOMEFRAG"))
            rl_my_location.setOnClickListener(this);
        else
            rl_my_location.setVisibility(View.GONE);

    }

    /**
     * This is onResume() method, which is getting call each time.*/
    @Override
    protected void onResume() {
        super.onResume();
        mClear.setOnClickListener(this);
        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    mClear.setVisibility(View.VISIBLE);
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    mClear.setVisibility(View.GONE);
                    if (mSavedAdapter != null && mSavedAddressList.size() > 0) {
                        mRecyclerView.setAdapter(mSavedAdapter);
                    }
                }
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Log.e("", "NOT CONNECTED");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * This is the method, where all onclick events, we can get and differentiate it based on their views.
     * @param v views
     */
    @Override
    public void onClick(View v) {
        if(v == mClear){
            mSearchEdittext.setText("");
            if(mAdapter!=null){
                mAdapter.clearList();
            }

        }
        if(v.getId()== R.id.rl_my_location)
        {
            Intent intent = new Intent();
            /*intent.putExtra("LATITUDE",Double.parseDouble(manager.getSplashLatitude()));
            intent.putExtra("LONGITUDE",Double.parseDouble(manager.getSplashLongitude()));*/
            intent.putExtra("LATITUDE", Constants.currentLat);
            intent.putExtra("LONGITUDE",Constants.currentLng);
            intent.putExtra("placename","NO");
          //  VariableConstant.isADRESELETD = false;
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * This method is used to send our control to the previous activity.
     */
    @Override
    public void onBackPressed() {
        String flag = "";
        Log.d("TAG","value of flag: "+ flag);

        finish();

        InputMethodManager im = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
     * @param mResultList Address List
     * @param position position of the address
     */
    @Override
    public void onPlaceClick(final ArrayList<Place_Auto_Complete_Pojo> mResultList, final int position) {
        if(mResultList!=null){
            try {
                //  final String ref_key = String.valueOf(mResultList.get(position).getRef_key());
                final String place_Id = mResultList.get(position).getPlace_id();
                //  final String url = getPlaceDetailsUrl(ref_key);
                final String url = getPlaceDetailsUrl(place_Id);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean insertFlag = true;
                        DbSavedAddress addressPojo = getPlaceData(url);//DropAddressPojo

                        for (int pos = 0 ; pos < mSavedAddressList.size(); pos++)
                        {
                            Log.d(TAG,"FULLADDRES "+ mSavedAddressList.get(0).getAddress_name());

                            if (addressPojo.getAddress_name().equals(mSavedAddressList.get(pos).getAddress_name()) && addressPojo.getAddress_lat()==mSavedAddressList.get(pos).getAddress_lat()
                                    && addressPojo.getAddress_lng()==mSavedAddressList.get(pos).getAddress_lng())
                            {
                                insertFlag = false;
                                // dataBaseHelper.addNewAddress(addressPojo.getAddress_name(),addressPojo.getAddress_formate(),addressPojo.getAddress_lat(),addressPojo.getAddress_lng());
                                break;
                            }
                        }
                        if (insertFlag)
                            dataBaseHelper.addNewAddress(addressPojo.getAddress_name(),addressPojo.getAddress_formate(),addressPojo.getAddress_lat(),addressPojo.getAddress_lng());
                        Log.d(TAG," ,value of full: "+addressPojo.getAddress_name());
                        sendControlBack(addressPojo);       //This is used to send control to the activity, once we click on addresses, entered on auto place search box.
                    }
                });
                thread.start();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param place object for the place to be choosen
     */
    private void sendControlBack(DbSavedAddress place)
    {

        Intent intent = new Intent();
        intent.putExtra("placename",place.getAddress_name());
        intent.putExtra("formatedaddes",place.getAddress_formate());
        intent.putExtra("LATITUDE",place.getAddress_lat());
        intent.putExtra("LONGITUDE",place.getAddress_lng());
        Log.d(TAG, "sendControlBack: "+place.getAddress_name() +" lat "+place.getAddress_lat()
        +" long "+place.getAddress_lng());
        setResult(RESULT_OK, intent);
        finish();

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
}
