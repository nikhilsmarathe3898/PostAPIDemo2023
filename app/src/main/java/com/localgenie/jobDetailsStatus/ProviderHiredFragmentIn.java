package com.localgenie.jobDetailsStatus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.localgenie.R;
import com.localgenie.providerdetails.ProviderDetails;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.CircleTransform;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.Utility;
import com.pojo.BidDispatchLog;
import com.pojo.BookingAccounting;
import com.pojo.BookingTimer;
import com.pojo.CartInfo;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import adapters.SelectedService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProviderHiredFragmentIn.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ProviderHiredFragmentIn extends DaggerFragment implements JobProviderInfo,OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    @BindView(R.id.ivProfilePic)ImageView ivProfilePic;
    @BindView(R.id.tvName)TextView tvName;
    @BindView(R.id.tvJobPostedProViewPro)TextView tvJobPostedProViewPro;
    @BindView(R.id.tvInCallDate)TextView tvInCallDate;
    @BindView(R.id.tvInCallDateTime)TextView tvInCallDateTime;
    @BindView(R.id.tvInCallLocation)TextView tvInCallLocation;
    @BindView(R.id.tvInCallShowLocation)TextView tvInCallShowLocation;
    @BindView(R.id.tvInCallAddress)TextView tvInCallAddress;
    @BindView(R.id.cardViewLocation)CardView cardViewLocation;
    @BindView(R.id.rl_address)RelativeLayout rl_address;


    /*******************************************************************/

    @BindView(R.id.tvJobRequestedService)TextView tvJobRequestedService;
    @BindView(R.id.recyclerViewReuestedService)RecyclerView recyclerViewReuestedService;

    @BindView(R.id.rlBiddingService)RelativeLayout rlBiddingService;
    @BindView(R.id.tvRequestedBiddingService)TextView tvRequestedBiddingService;
    @BindView(R.id.tvRequestedBiddingServiceAmt)TextView tvRequestedBiddingServiceAmt;

    @BindView(R.id.rlVisitFee)RelativeLayout rlVisitFee;
    @BindView(R.id.tvVisitFee)TextView tvVisitFee;
    @BindView(R.id.tvVisitFeeAmt)TextView tvVisitFeeAmt;

    @BindView(R.id.rlTravelFeeFee)RelativeLayout rlTravelFeeFee;
    @BindView(R.id.tvTravelFeeFee)TextView tvTravelFeeFee;
    @BindView(R.id.tvTravelFeeFeeAmt)TextView tvTravelFeeFeeAmt;

    @BindView(R.id.rlDiscountFee)RelativeLayout rlDiscountFee;
    @BindView(R.id.tvDiscountFee)TextView tvDiscountFee;
    @BindView(R.id.tvDiscountFeeAmt)TextView tvDiscountFeeAmt;

    @BindView(R.id.rlTotalFee)RelativeLayout rlTotalFee;
    @BindView(R.id.tvTotalFee)TextView tvTotalFee;
    @BindView(R.id.tvTotalFeeAmt)TextView tvTotalFeeAmt;

    @BindView(R.id.tvRequestedPayment)TextView tvRequestedPayment;
    @BindView(R.id.tvRequestedPaymentMethod)TextView tvRequestedPaymentMethod;


    @BindView(R.id.rlMainHiredFrag)RelativeLayout rlMainHiredFrag;
    @BindView(R.id.callBtn)Button callBtn;
    @BindView(R.id.msgBtn)Button msgBtn;

    SupportMapFragment mapFragment;


    /*******************************************************************/


    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private double proLat = 0,proLng = 0;
    private String proId;
    private int callType = 1;

    @Inject
    AppTypeface appTypeface;

    @Inject
    public ProviderHiredFragmentIn() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProviderHiredFragmentIn.
     */
    // TODO: Rename and change types and number of parameters
    /*public static ProviderHiredFragmentIn newInstance() {
        ProviderHiredFragmentIn fragment = new ProviderHiredFragmentIn();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }*/

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_provider_hired_fragment_in, container, false);
        mContext = getActivity();
        ((JobDetailsActivity)mContext).jobDetailProInfo(this);
        ButterKnife.bind(this,v);

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        typeFace();
        return v;
    }

    private void typeFace() {
        tvName.setTypeface(appTypeface.getHind_semiBold());
        tvJobPostedProViewPro.setTypeface(appTypeface.getHind_medium());
        tvInCallDate.setTypeface(appTypeface.getHind_semiBold());
        tvInCallDateTime.setTypeface(appTypeface.getHind_semiBold());
        tvInCallLocation.setTypeface(appTypeface.getHind_semiBold());
        tvInCallShowLocation.setTypeface(appTypeface.getHind_semiBold());
        tvInCallAddress.setTypeface(appTypeface.getHind_regular());



        LinearLayoutManager llManager = new LinearLayoutManager(mContext);
        recyclerViewReuestedService.setLayoutManager(llManager);

        tvJobRequestedService.setTypeface(appTypeface.getHind_semiBold());
        tvRequestedBiddingService.setTypeface(appTypeface.getHind_light());
        tvRequestedBiddingServiceAmt.setTypeface(appTypeface.getHind_light());
        tvVisitFee.setTypeface(appTypeface.getHind_light());
        tvVisitFeeAmt.setTypeface(appTypeface.getHind_light());
        tvTravelFeeFee.setTypeface(appTypeface.getHind_light());
        tvTravelFeeFeeAmt.setTypeface(appTypeface.getHind_light());
        tvDiscountFee.setTypeface(appTypeface.getHind_light());
        tvDiscountFeeAmt.setTypeface(appTypeface.getHind_light());
        tvTotalFee.setTypeface(appTypeface.getHind_semiBold());
        tvTotalFeeAmt.setTypeface(appTypeface.getHind_semiBold());
        tvRequestedPayment.setTypeface(appTypeface.getHind_semiBold());
        tvRequestedPaymentMethod.setTypeface(appTypeface.getHind_semiBold());

        msgBtn.setTypeface(appTypeface.getHind_semiBold());
        callBtn.setTypeface(appTypeface.getHind_semiBold());
    }

    @OnClick({R.id.tvInCallShowLocation,R.id.tvJobPostedProViewPro,R.id.callBtn})
    public void onClickView(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            case R.id.tvInCallShowLocation:

                intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+proLat+","+proLng));
                startActivity(intent);

                /*String uri = String.format(Locale.ENGLISH, "geo:%f,%f", proLat, proLng);
                 intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);*/
                break;
            case R.id.tvJobPostedProViewPro:
                intent = new Intent(getActivity(), ProviderDetails.class);
                intent.putExtra("ProId",proId);
                intent.putExtra("isProFileView",true);
                startActivity(intent);
                break;
            case R.id.callBtn:


               // mListener.onFragmentInteraction();//proId,name,proPic
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(boolean isVisible) {
        if (mListener != null) {
            mListener.onFragmentInteraction(isVisible);//"", name, proPic
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mContext = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContext = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng sydney = new LatLng(proLat, proLng);
        googleMap.addMarker(new MarkerOptions().position(sydney).title(name+" Location"));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 14.0f);
        googleMap.animateCamera(cameraUpdate);
        googleMap.moveCamera(cameraUpdate);



        googleMap.setOnMapClickListener(latLng -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="+proLat+","+proLng));
            startActivity(intent);
        });
    }

    int status;
    @Override
    public void providerInfo(String providerId, String name, String proPic, int reviewCount, float rating,
                             double amount, String currencySymbol, int status) {
        proId = providerId;
        this.status = status;
        if(mContext!=null)
        {
            if(status !=9)
                tvInCallShowLocation.setVisibility(View.VISIBLE);
            else
                tvInCallShowLocation.setVisibility(View.GONE);
        }

    }

    private void showMap() {
        mapFragment.getMapAsync(this);

    }

    String proPic,name;
    long bId;
    @Override
    public void proExpiry(String name, String proPic, long expiryTime, long serverTime,
                          LatLng customerLatLng, LatLng proLatLng) {

        proLat = proLatLng.latitude;
        proLng = proLatLng.longitude;



        this.proPic = proPic;
        this.name = name;

        if(mContext!=null)
            callMethod(name,proPic);

        if(mContext!=null)
        {
            if(status !=9)
            {
                cardViewLocation.setVisibility(View.VISIBLE);
                showMap();
            }
            else
            {
                cardViewLocation.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onJobTimer(BookingTimer bookingTimer, long serverTime, String statusMsg, long bid, int status,
                           long totalJobTime) {
        bId = bid;
    }

    @Override
    public void onBidJobInfoDetails(ArrayList<BidDispatchLog> bidDispatchLog, int statusCode) {

    }

    @Override
    public void onJobInfo(int bookingType, BookingAccounting accounting, CartInfo cart, String categoryName) {


        if(mContext!=null)
        {
            Log.d("TAG", "onJobInfo: "+bookingType);
            if(bookingType == 3)
            {
                rlBiddingService.setVisibility(View.VISIBLE);
                recyclerViewReuestedService.setVisibility(View.GONE);
                tvRequestedBiddingService.setText(categoryName);
                Utility.setAmtOnRecept(accounting.getBidPrice(),tvRequestedBiddingServiceAmt, Constants.currencySymbol);

            }else
            {
                rlBiddingService.setVisibility(View.GONE);
                recyclerViewReuestedService.setVisibility(View.VISIBLE);
                SelectedService selectedService = new SelectedService(mContext,false);
                selectedService.onInCallValue(1);
                selectedService.onCheckOutItem(cart.getCheckOutItem());
                recyclerViewReuestedService.setAdapter(selectedService);
            }

                       if(accounting.getTravelFee()>0)
            {
                rlTravelFeeFee.setVisibility(View.VISIBLE);
                Utility.setAmtOnRecept(accounting.getTravelFee(),tvTravelFeeFeeAmt, Constants.currencySymbol);

            }

            if(accounting.getVisitFee()>0)
            {
                rlVisitFee.setVisibility(View.VISIBLE);
                Utility.setAmtOnRecept(accounting.getVisitFee(),tvVisitFeeAmt, Constants.currencySymbol);
            }

            if(accounting.getDiscount()>0)
            {
                rlDiscountFee.setVisibility(View.VISIBLE);
                Utility.setAmtOnRecept(accounting.getDiscount(),tvDiscountFeeAmt, Constants.currencySymbol);

            }

            Utility.setAmtOnRecept(accounting.getTotal(),tvTotalFeeAmt,Constants.currencySymbol);

            if(accounting.getPaidByWallet()==1)
            {
                tvRequestedPaymentMethod.setText(getString(R.string.wallet));
            }else
                tvRequestedPaymentMethod.setText(accounting.getPaymentMethodText());


        }
    }

    @Override
    public void onJobAddress(String address, Date date) {
        tvInCallAddress.setText(address);
        tvInCallDateTime.setText(Utility.getFormattedDate(date));

        if(callType == 3){
            dateTimeMeasure(date);
            rl_address.setVisibility(View.GONE);

        }

    }

    private void dateTimeMeasure(Date date) {

        long diff = Utility.timeStamp(date.getTime()/1000,System.currentTimeMillis()/1000);

        if(diff>0)
        {
            if(checkFormat(diff))
            {
                onButtonPressed(true); // testing  changed it to true // developer: shijen
               // onButtonPressed(true);
              //  rlMainHiredFrag.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dateTimeMeasure(date);
                        //  long diff = Utility.timeStamp(date.getTime()/1000,System.currentTimeMillis()/1000);
                    }
                }, 4000);
            }else
                onButtonPressed(true);
             //   rlMainHiredFrag.setVisibility(View.VISIBLE);
        }else
        {
           // rlMainHiredFrag.setVisibility(View.GONE);
            onButtonPressed(true);
        }


    }

    private boolean checkFormat(long diff)
    {
        int seconds = (int) (diff / 1000);
        int minutes = (seconds % 3600) / 60;
        if(minutes>5)
            return true;
        else
            return false;
    }

    private void callMethod(String name, String proPic)
    {
        tvName.setText(name);
        if(!proPic.equals(""))
        {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.profile_price_bg)
                    .error(R.drawable.profile_price_bg)
                    .transform(new CircleTransform(mContext))
                    .priority(Priority.HIGH);
            if(mContext!=null)
            {
                Glide.with(mContext)
                        .load(proPic)
                        .apply(options)
                        .into(ivProfilePic);
            }

        }
    }

    public void callType(int callType)
    {
        this.callType = callType;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(boolean isVisible);//String proId, String name, String proPic
    }
}
