package com.localgenie.biddingFlow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.home.ServicesFrag;
import com.localgenie.model.QuestionList;
import com.localgenie.model.payment_method.CardGetData;
import com.localgenie.payment_details.PaymentDetailActivity;
import com.localgenie.selectPaymentMethod.SelectedCardInfoInterface;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.wallet.WalletActivity;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.CardListAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;

/**
 *
 * This fragment is used for Payment selection in biding or question flow
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyBiddingQuestionFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyBiddingQuestionFrag extends DaggerFragment implements SelectedCardInfoInterface.SelectedView{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_POSITION_QUE = "param1";
    public static final String ARG_POSITION = "param2";
    public static final String ARG_QUESTION_LIST = "param3";

    // TODO: Rename and change types of parameters
    private int positionQuestion;
    private int positionFixed = 0;
    private ArrayList<QuestionList> questionLists;

    @BindView(R.id.progressPayment)ProgressBar progressPayment;
    @BindView(R.id.llSelectCard)LinearLayout llSelectCard;
    @BindView(R.id.tvConfirmPaymentCard)TextView tvConfirmPaymentCard;
    @BindView(R.id.llConfirmCard)LinearLayout llConfirmCard;
    @BindView(R.id.recyclerViewCard)RecyclerView recyclerViewCard;
    @BindView(R.id.tvConfirmAddMoreCard)TextView tvConfirmAddMoreCard;


    @BindView(R.id.viewSelectCard) View viewSelectCard;
    @BindView(R.id.llSelectWallet) LinearLayout llSelectWallet;
    @BindView(R.id.tvConfirmPaymentWallet) TextView tvConfirmPaymentWallet;
    @BindView(R.id.tvWalletBalance) TextView tvWalletBalance;
    @BindView(R.id.tvWalletBalanceAmt) TextView tvWalletBalanceAmt;
   // @BindView(R.id.tvSelectedWalletPay) TextView tvSelectedWalletPay;
    @BindView(R.id.tvConfirmAddWalletBalance) TextView tvConfirmAddWalletBalance;


    @BindView(R.id.viewSelectWallet) View viewSelectWallet;
    @BindView(R.id.tvConfirmPaymentCash) TextView tvConfirmPaymentCash;
    @BindView(R.id.tvSelectedCashPay) TextView tvSelectedCashPay;

    @BindView(R.id.walletBottom) ConstraintLayout walletBottom;
    @BindView(R.id.tvExcessAmntDesc)TextView tvExcessAmntDesc;
    @BindView(R.id.btn_card)TextView btn_card;
    @BindView(R.id.btn_cash)Button btn_cash;

    private Context mContext;
    private Activity mActivity;

    private CardListAdapter cardListAdapter;
    private ArrayList<CardGetData> cardItem = new ArrayList<>();
    private BottomSheetBehavior sheetBehavior;

    @Inject
    AppTypeface appTypeface;
    @Inject
    AlertProgress alertProgress;
    @Inject
    SelectedCardInfoInterface.SelectedPresenter presenter;
    @Inject
    SessionManagerImpl manager;

    private OnFragmentInteractionListener mListener;
    private int paymentType;
    private double softLimit = 0, hardLimit = 0, balance = 0;
    private int cardSelected = -1;
    private int paymentTypeSelected = 0;


    @Inject
    public MyBiddingQuestionFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyBiddingQuestionFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static MyBiddingQuestionFrag newInstance(String param1, String param2) {
        MyBiddingQuestionFrag fragment = new MyBiddingQuestionFrag();
        Bundle args = new Bundle();
        args.putString(ARG_POSITION, param1);
        args.putString(ARG_QUESTION_LIST, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            positionFixed = getArguments().getInt(ARG_POSITION);
            positionQuestion = getArguments().getInt(ARG_POSITION_QUE);
            questionLists = (ArrayList<QuestionList>) getArguments().getSerializable(ARG_QUESTION_LIST);
        }
        updateFragmentQuestion(positionQuestion);
    }

    public void updateFragmentQuestion(int positionQuestion)
    {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_bidding_question, container, false);
        ButterKnife.bind(this,view);
        presenter.attachView(this);
        mActivity = getActivity();

        typeFaceSetValue();
        setVisiblity();
        return view;
    }
    private void setVisiblity()
    {
        if(ServicesFrag.paymentMode.isCard())
        {
            llSelectCard.setVisibility(View.VISIBLE);
            viewSelectCard.setVisibility(View.VISIBLE);
        }
        if(ServicesFrag.paymentMode.isWallet())
        {
            if(Constants.bookingType!=3)
            {
                llSelectWallet.setVisibility(View.VISIBLE);
                viewSelectWallet.setVisibility(View.VISIBLE);
            }

        }
        if(ServicesFrag.paymentMode.isCash())
            tvConfirmPaymentCash.setVisibility(View.VISIBLE);

        sheetBehavior = BottomSheetBehavior.from(walletBottom);
    }

    private void typeFaceSetValue()
    {
        tvConfirmPaymentCard.setTypeface(appTypeface.getHind_bold());
        tvConfirmPaymentCash.setTypeface(appTypeface.getHind_bold());
        tvConfirmPaymentWallet.setTypeface(appTypeface.getHind_bold());
        tvWalletBalanceAmt.setTypeface(appTypeface.getHind_regular());

        tvConfirmAddMoreCard.setTypeface(appTypeface.getHind_semiBold());
        tvConfirmAddWalletBalance.setTypeface(appTypeface.getHind_semiBold());
      //  tvSelectedCashPay.setTypeface(appTypeface.getHind_semiBold());

      //  tvSelectedWalletPay.setTypeface(appTypeface.getHind_semiBold());
        tvSelectedCashPay.setTypeface(appTypeface.getHind_semiBold());
        tvExcessAmntDesc.setTypeface(appTypeface.getHind_medium());
        btn_cash.setTypeface(appTypeface.getHind_semiBold());
        btn_card.setTypeface(appTypeface.getHind_semiBold());

        LinearLayoutManager llManager = new LinearLayoutManager(mContext);
        recyclerViewCard.setLayoutManager(llManager);
        cardListAdapter = new CardListAdapter(mContext,cardItem,this);
        recyclerViewCard.setAdapter(cardListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Utility.isNetworkAvailable(mContext))
        {
            onShowProgress();
            presenter.onGetCards();
            presenter.getWalletAmount();
        }else
            alertProgress.showNetworkAlert(mContext);
    }

    @OnClick({R.id.tvConfirmPaymentCard,R.id.tvConfirmPaymentCash,
            R.id.tvConfirmPaymentWallet,R.id.tvConfirmAddMoreCard,
            R.id.tvSelectedCashPay,R.id.tvConfirmAddWalletBalance
            ,R.id.btn_card,R.id.btn_cash})//R.id.tvSelectedWalletPay,
    //,R.id.tvConfirmPaymentWallet,
    public void onPaymentSelection(View v)
    {
        Intent intent;
       // Context mContext;
        switch (v.getId())
        {
            case R.id.tvSelectedCashPay: //OnClicking pay button
                onIntentCallForThePay();
                break;

            case R.id.btn_card:
                paymentType = 2;
             //   callCollapse();
                break;
            case R.id.btn_cash:
                paymentType = 1;
              //  callCollapse();
                break;

           /* case R.id.tvSelectedWalletPay:
                callExpandMethod();
                // paymentType = 3;
                break;*/

            case R.id.tvConfirmAddMoreCard:
                onToCallIntent();
                break;
            case R.id.tvConfirmAddWalletBalance:
                startActivity();
                break;
            case R.id.tvConfirmPaymentCard:
                setPaymentSelectionType(1);
                paymentTypeSelected = 1;
                cardSelected = -1;
                break;
            case R.id.tvConfirmPaymentCash:
                setPaymentSelectionType(2);
                cardSelected = -1;
               // mContext = this.mContext;
                presenter.setCashCardBookingView(2,balance,softLimit,hardLimit,mContext,alertProgress);
                break;
            case R.id.tvConfirmPaymentWallet:
             //   mContext = this;
                cardSelected = -1;
                setPaymentSelectionType(3);
                presenter.setCashCardBookingView(3,balance,softLimit,hardLimit,mContext,alertProgress);
                break;
        }
    }

    private void onIntentCallForThePay()
    {
        Intent intent;
        Context mContext;
        Log.d("TAG", "onIntentCallForThePay: "+paymentTypeSelected);
        switch (paymentTypeSelected)
        {
            case 2:
                paymentType = 1; //Cash selected
                walletButtonSelected(false,paymentType);
                break;
            case 3: // Wallet selected
                callExpandMethod();
                break;
            case 1: //Card selected
                if(cardSelected!=-1)
                {
                    nextIntentForCar(cardSelected);
                }
                break;

        }
    }

    private void setPaymentSelectionType(int i)
    {

        tvConfirmPaymentCard.setSelected(false);
        tvConfirmPaymentCash.setSelected(false);
        tvConfirmPaymentWallet.setSelected(false);
        //  tvSelectedWalletPay.setVisibility(View.GONE);
        //   tvSelectedCashPay.setVisibility(View.GONE);
        llConfirmCard.setVisibility(View.GONE);
        tvConfirmAddWalletBalance.setVisibility(View.GONE);
        switch (i)
        {
            case 1:
                tvConfirmPaymentCard.setSelected(true);
                llConfirmCard.setVisibility(View.VISIBLE);
                paymentTypeSelected = 1;
                //   rlConfirmCard.setVisibility(View.VISIBLE);
                break;
            case 2:
                paymentType = 1;
                paymentTypeSelected = 2;
                tvConfirmPaymentCash.setSelected(true);
                //   tvSelectedCashPay.setVisibility(View.GONE);
                break;
            case 3:
                // paymentType = 3;
                paymentTypeSelected = 3;
                tvConfirmPaymentWallet.setSelected(true);
                //  tvSelectedWalletPay.setVisibility(View.VISIBLE);
                tvConfirmAddWalletBalance.setVisibility(View.VISIBLE);
                break;
        }
    }



    /*private void callCollapse() {

        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        walletButtonSelected(true,paymentType);

    }*/

    private void walletButtonSelected(boolean isWallet, int paymentType) {

        if (mListener != null) {
            String cardId = "";
            if(paymentType == 2)
            {
                if(!"".equals(manager.getDefaultCardId()))
                    cardId =manager.getDefaultCardId();
                else
                    cardId = cardItem.get(0).getId();

            }

            mListener.onFragmentWalletInteraction(isWallet,paymentType,cardId);
        }
        Log.d("TAG", "walletButtonSelected: "+paymentType);
        onDestroyView();
    }

    private void callExpandMethod() {

        alertProgress.alertPositiveNegativeOnclick(mContext, getString(R.string.fare_may_vary), getString(R.string.wallet),
                getString(R.string.card), getString(R.string.cash), true, new DialogInterfaceListner() {
                    @Override
                    public void dialogClick(boolean isClicked) {
                        if(isClicked){//Positive button card                        {
                            if(cardItem.size()>0) {
                                paymentType = 2;
                                walletButtonSelected(true, paymentType);

                            }else
                                alertProgress.alertinfo(mContext,getString(R.string.pleaseAddCardToPay));
                        }
                        else
                        {
                            paymentType = 1;
                            walletButtonSelected(true, paymentType);
                        }

                    }
                });

       /* if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            //  btnBottomSheet.setText("Close sheet");
        }*/
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    }

    @Override
    public void onToCallIntent()
    {
        Intent intent = new Intent(mContext, PaymentDetailActivity.class);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);

    }

    @Override
    public void onToBackIntent(int adapterPosition) {
        cardSelected = adapterPosition;
    }

    public void nextIntentForCar(int adapterPosition)
    {
        paymentType = 2;
        CardGetData data = cardItem.get(adapterPosition);
        if(mListener!=null)
            mListener.onCardSelectedPayment(paymentType,data.getId(),data.getBrand(),data.getLast4());
        Log.d("TAG", "nextIntentForCar: "+data.getLast4());
        onDestroyView();
    }

    @Override
    public void addItems(ArrayList<CardGetData> cardsList) {
       /* cardItem.clear();
        cardItem.addAll(cardsList);
        cardListAdapter.notifyDataSetChanged();*/

        cardItem.clear();
        cardItem.addAll(cardsList);
        cardListAdapter.notifyDataSetChanged();
        setPaymentSelectionType(1);
        cardSelected = 0;
    }

    @Override
    public void onVisibilitySet() {
    //    tvSelectedWalletPay.setVisibility(View.GONE);
        tvConfirmAddWalletBalance.setVisibility(View.GONE);
       // tvSelectedCashPay.setVisibility(View.GONE);
        tvConfirmPaymentWallet.setSelected(false);
        tvConfirmPaymentCash.setSelected(false);

    }

    @Override
    public void showWalletAmount(String currencySymbol, double balance, double softLimit, double hardLimit) {
        this.hardLimit = hardLimit;
        this.softLimit = softLimit;
        this.balance = balance;
        Utility.setAmtOnRecept(balance,tvWalletBalanceAmt,currencySymbol);
    }

    @Override
    public void paymentSelection(int selectedCell) {
        setPaymentSelectionType(selectedCell);
        cardListAdapter.onAdapterChanged(-1);
    }


    @Override
    public void startActivity() {
        Intent intentq = new Intent(mContext, WalletActivity.class);
        startActivityForResult(intentq, Constants.WALLETCALL);
    }


    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message) {

    }

    @Override
    public void onError(String error) {
        alertProgress.alertinfo(mContext,error);
    }

    @Override
    public void onShowProgress() {
        progressPayment.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgress() {
        progressPayment.setVisibility(View.GONE);
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
        void onFragmentInteraction(Uri uri);
        void onFragmentWalletInteraction(boolean isWallet, int paymentType, String cardId);

        void onCardSelectedPayment(int paymentType, String id, String brand, String last4);
    }
}
