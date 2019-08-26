package com.localgenie.add_address;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.localgenie.R;
import com.localgenie.model.youraddress.YourAddrData;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * @author Pramod
 * @since 19-01-2018.
 */

public class AddAddressActivity extends DaggerAppCompatActivity implements AddressView, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 12;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public final String TAG = "AddAddressActivity";
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    @BindView(R.id.bottom_sheet)
    View bottomSheet;

    @BindView(R.id.tvSetJobLocation)
    TextView tvSetJobLocation;
    @BindView(R.id.et_house_no)
    EditText et_house_no;
    @BindView(R.id.mapRow)
    RelativeLayout mapRow;
    @BindView(R.id.ivBackArrow)ImageView ivBackArrow;

    EditText new_location;

    TextView tvTagHome, tvTagWork, tvTagOther, tvCancelBtn;

    EditText etTagOther;

    String tag;

    String auth;

    private String addrLine1, addrLine2, city, state, country, full_addr,addressId,addressName;
    private String pinCode = "";


    double new_lat, new_long;

    boolean flag_address = false;

    Button btnSaveAndProceed;

    @Inject
    SessionManagerImpl sessionManager;

    @Inject
    AddAddressPresenter presenter;

    @Inject
    AppTypeface appTypeface;

    AlertDialog alertDialog;

    @Inject
    AlertProgress alertProgress;

    double currentLatitude, currentLongitude;

    RelativeLayout.LayoutParams rlp;
    private boolean isEdit = false;
    private boolean isEditable = false;

    private LatLng latlng;
    private BottomSheetBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.bind(this);


        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30 * 100)        // 1 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        //rlp.setMargins(0, 0, 30, 100);


        initialize();
        setTypeFaceValue();
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        rlp.setMargins(0, 0, 30, sheetBehavior.getPeekHeight() + 10);

        showExpanded();

    }

    private void showExpanded()
    {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void setTypeFaceValue() {
        tvSetJobLocation.setTypeface(appTypeface.getHind_semiBold());
        et_house_no.setTypeface(appTypeface.getHind_regular());


    }

    private void initialize() {
        mapFrag.getMapAsync(this);

        ivBackArrow.setOnClickListener(view -> onBackPressed());

        tvTagHome = bottomSheet.findViewById(R.id.tvTagAsHome);
        tvTagWork = bottomSheet.findViewById(R.id.tvTagAsWork);
        tvTagOther = bottomSheet.findViewById(R.id.tvTagAsOther);
        etTagOther = bottomSheet.findViewById(R.id.etTagAsOther);
        tvCancelBtn = bottomSheet.findViewById(R.id.tvCancelBtn);
        new_location = bottomSheet.findViewById(R.id.et_location);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String coming_from = bundle.getString("edit_addr");
            final YourAddrData data = (YourAddrData) bundle.getSerializable("data");
            Log.d(TAG, "initialize: " + coming_from);
            if ("y".equals(coming_from)) {
                if (data != null) {
                    isEdit = true;
                    String str = getFullAddress(data.getLatitude(), data.getLongitude());
                    et_house_no.setText(data.getHouseNo());
                    new_location.setText(str);
                    if(data.getTaggedAs().equalsIgnoreCase("home"))
                    {
                        homeTag();
                    }else if(data.getTaggedAs().equalsIgnoreCase("office") || data.getTaggedAs().equalsIgnoreCase("work"))
                    {
                        officeTag();
                    }else
                    {
                        tagOther();
                        etTagOther.setText(data.getTaggedAs());
                    }
                    Log.d(TAG, "initialize: " + mGoogleMap);
                    //  latlng =
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (mGoogleMap != null)
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(data.getLatitude(), data.getLongitude()), 16.0f));

                        }
                    }, 100);

                    isEditable = true;
                    addrLine1 = data.getAddLine1();
                    addrLine2 = data.getAddLine2();
                    new_lat = data.getLatitude();
                    new_long = data.getLongitude();
                    city = data.getCity();
                    state = data.getState();
                    country = data.getCountry();
                    pinCode = data.getPincode();
                    addressId = data.getId();
                    tag = data.getTaggedAs();
                    addressName = data.getName();

                }
            }

            auth = sessionManager.getAUTH();
        }
        btnSaveAndProceed = bottomSheet.findViewById(R.id.add_save_address);

        btnSaveAndProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String houseNo = et_house_no.getText().toString().trim();
                if(isEditable)
                {

                    alertDialog = alertProgress.getProgressDialog(AddAddressActivity.this,getString(R.string.wait_editing_address));
                    showProgress();
                    presenter.editAddress(auth,addressId,houseNo, addressName, addrLine1, addrLine2, city, state, country, "",
                            pinCode, new_lat, new_long, tag, 1);
                }else
                {
                    alertDialog = alertProgress.getProgressDialog(AddAddressActivity.this,getString(R.string.wait_adding_address));
                    showProgress();
                    if (tag == null) {
                        tag = getString(R.string.home);
                    }
                    if(pinCode!=null)
                        presenter.addAddress(auth, addrLine1, addrLine2, city, state, country, "", pinCode, new_lat, new_long, tag,
                                1,houseNo, addressName);
                    else
                    {
                        presenter.addAddress(auth, addrLine1, addrLine2, city, state, country, "", pinCode, new_lat, new_long, tag,
                                1,houseNo, addressName);
                    }
                }

            }
        });


        tvTagHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeTag();


            }
        });

        tvTagWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                officeTag();
            }
        });

        tvTagOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tagOther();

            }
        });

        tvCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvTagHome.setVisibility(View.VISIBLE);
                tvTagWork.setVisibility(View.VISIBLE);
                tvTagOther.setVisibility(View.VISIBLE);

                etTagOther.setVisibility(View.GONE);
                tvCancelBtn.setVisibility(View.GONE);
            }
        });

        etTagOther.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int other_tag_len = editable.length();
                if (other_tag_len > 0) {
                 //   tvCancelBtn.setText(getResources().getString(R.string.save));
                    tvCancelBtn.setText(getResources().getString(R.string.cancel));
                    if (TextUtils.isEmpty(editable.toString()))
                        tag = "other";
                    else
                        tag = editable.toString();
                } else
                    tvCancelBtn.setText(getResources().getString(R.string.cancel));
            }
        });

    }

    private void tagOther()
    {
        tvTagHome.setVisibility(View.GONE);
        tvTagWork.setVisibility(View.GONE);

        etTagOther.setVisibility(View.VISIBLE);
        tvCancelBtn.setVisibility(View.VISIBLE);
        tvCancelBtn.setClickable(true);
    }

    private void officeTag() {
        etTagOther.setVisibility(View.GONE);
        tvCancelBtn.setVisibility(View.GONE);

        tvTagWork.setBackgroundResource(R.drawable.parrot_green_stroke_bottom_bg);
        tvTagHome.setBackgroundResource(R.drawable.white_background);
        tvTagOther.setBackgroundResource(R.drawable.white_background);

        tvTagWork.setTextColor(ResourcesCompat.getColor(getResources(), R.color.parrotGreen, null));
        tvTagHome.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey_background_vdarker, null));
        tvTagOther.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey_background_vdarker, null));

        tag = getString(R.string.office);
    }

    private void homeTag() {
        etTagOther.setVisibility(View.GONE);
        tvCancelBtn.setVisibility(View.GONE);
        tvTagHome.setBackgroundResource(R.drawable.parrot_green_stroke_bottom_bg);
        tvTagWork.setBackgroundResource(R.drawable.white_background);
        tvTagOther.setBackgroundResource(R.drawable.white_background);

        tvTagHome.setTextColor(ResourcesCompat.getColor(getResources(), R.color.parrotGreen, null));
        tvTagWork.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey_background_vdarker, null));
        tvTagOther.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey_background_vdarker, null));

        tag = getString(R.string.home);
    }

    @Override
    public void setError(String message) {
        Toast.makeText(AddAddressActivity.this, message, Toast.LENGTH_LONG).show();
    }

    //contains your lat and lon
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        if (currentLatitude != 0f) {
            return;
        }


        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        String str = getFullAddress(currentLatitude, currentLongitude);
        new_location.setText(str);

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);


        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //BottomSheetBehavior.from(bottomSheet).setPeekHeight(200);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        rlp.setMargins(0, 0, 30, BottomSheetBehavior.from(bottomSheet).getPeekHeight() + 10);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        BottomSheetBehavior.from(bottomSheet).setPeekHeight(400);
                        rlp.setMargins(0, 0, 30, 100);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset < 10f) {
                    return;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void navToAddressScreen() {
        //Toast.makeText(AddAddressActivity.this, "Address added Successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void logout(String message) {

        alertProgress.alertPositiveOnclick(this, message, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(AddAddressActivity.this,sessionManager);
            }
        });
      //  Utility.setMAnagerWithBID(this,sessionManager);
    }

    @Override
    public void showProgress() {
        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    @Override
    public void hideProgress() {

        if (!isFinishing())
            alertDialog.dismiss();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        new_location.setClickable(true);
        new_location.setFocusable(false);
        new_location.setEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        //mGoogleMap.setPadding(0, 0, 0, bottomSheet.getLayoutParams().height);


        new_location.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new_location.setFocusableInTouchMode(true);
                if (MotionEvent.ACTION_UP == event.getAction())
                    openAutoComplete();
                return false;
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                new_location.setHint("Identifying Location");
            }
        });

        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {


                if(!isEdit)
                {
                    double lat = mGoogleMap.getCameraPosition().target.latitude;
                    double longitude = mGoogleMap.getCameraPosition().target.longitude;
                    Geocoder geocoder = new Geocoder(AddAddressActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(lat,
                                longitude, 1);
                        Log.e("Addresses", "-->" + addresses);
                        if (addresses.size() > 0) {
                            full_addr = addresses.get(0).toString();
                            Log.e("FULL_ADDR", full_addr);
                            addrLine1 = addresses.get(0).getAddressLine(0);
                            addrLine2 = addresses.get(0).getAddressLine(1);
                            new_lat = addresses.get(0).getLatitude();
                            new_long = addresses.get(0).getLongitude();
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            country = addresses.get(0).getCountryName();
                            pinCode = addresses.get(0).getPostalCode();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String str = getFullAddress(lat, longitude);

                    addressName = "";
                    new_location.setText(str);
                }
                isEdit = false;

            }
        });

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }
                //buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);

            } else {
                //Request Location Permission
                Log.e("AddAddress", "Location Perms denied");
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    public void openAutoComplete() {
        try {
            Intent intent = new Intent(AddAddressActivity.this, SearchAddressLocation.class);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            Log.e(TAG, "Error in Places : " + e);
            e.printStackTrace();
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(AddAddressActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }  else {
                handleNewLocation(location);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "Location Changed...");
        handleNewLocation(location);
    }

    public void onLocationSavedAddress(String address) {
        new_location.setText(address);
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                addressName = data.getStringExtra("placename");
                double lati = data.getDoubleExtra("LATITUDE", 0);
                double langi = data.getDoubleExtra("LONGITUDE", 0);
                Log.d(TAG, "onActivityResult: "+addressName+" latitude "+lati +" longitude "+langi);
                currentLatitude = lati;
                currentLongitude = langi;

                onLocationSavedAddress(addressName);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 16.0f));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addressName = data.getStringExtra("placename");
                    }
                }, 2000);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.e(TAG, "User Cancelled operation");
            }
        }
    }

    //Get address from the provided latitude and longitude
    public String getFullAddress(double lat, double lng) {
        try {
            Geocoder geocoder = new Geocoder(AddAddressActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);

                Log.e(TAG, "Address => " + add);
                return add;
            } else
                return "";

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return "Dragged location";
        }
    }
}