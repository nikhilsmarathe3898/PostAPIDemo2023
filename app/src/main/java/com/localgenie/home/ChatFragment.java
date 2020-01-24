package com.localgenie.home;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.pojo.BookingChatHistory;
import com.pojo.BookingChatPojo;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;
import com.utility.ShimmerLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.ViewPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends DaggerFragment implements ChattingFragPresenter.ViewPresent {

    private TextView tvCountActiveChat,tvCountPastChat;
    private ArrayList<BookingChatHistory> activeChat = new ArrayList<>();
    private ArrayList<BookingChatHistory> pastChat = new ArrayList<>();
    @BindView(R.id.shimmerChat)ShimmerLayout shimmerChat;
    @BindView(R.id.tl_jobDetails)TabLayout tabLayout;
    @BindView(R.id.vp_jobDetails)ViewPager viewPager;
    @BindView(R.id.toolBarChat)Toolbar toolBarChat;
    @BindView(R.id.tv_center)TextView tv_center;
    @BindView(R.id.swipeToRefresh)SwipeRefreshLayout swipeToRefresh;

    private ChatPageFragment activeChatFrag, pastChatFrag;

    LayoutInflater inflater;
    @Inject
    AppTypeface appTypeface;
    @Inject
    ChattingFragPresenter.Presenter presenter;
    @Inject
    SessionManagerImpl manager;
    @Inject
    AlertProgress alertProgress;
    private Context mContext;

    /* private ArrayList<Fragment> fragmentList;
     private ArrayList<String> fragmentTitleList;*/
    @Inject
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =inflater.inflate(R.layout.fragment_response_message, container, false);
        ButterKnife.bind(this,layout);
        mContext = getActivity();
        presenter.attachView(this);
        this.inflater = inflater;
        initialize(layout);

        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void initialize(View layout) {


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        activeChatFrag = ChatPageFragment.newInstance(0);
        pastChatFrag = ChatPageFragment.newInstance(1);
        viewPagerAdapter.addFragment(activeChatFrag,"");
        viewPagerAdapter.addFragment(pastChatFrag,"");

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
      //  tabLayoutMyEvent.setSelectedTabIndicatorHeight(5);
        tabLayout.setSelectedTabIndicatorColor(Utility.getColor(getActivity(), R.color.parrotGreen));//getResources().getColo
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setAdapter(viewPagerAdapter);
        for(int i =0 ;i<tabLayout.getTabCount();i++){
            tabLayout.getTabAt(i).setCustomView(getCustomTabView(i));
        }

        ((AppCompatActivity)mContext).setSupportActionBar(toolBarChat);
        ((AppCompatActivity)mContext).getSupportActionBar().setDisplayShowHomeEnabled(false);
        ((AppCompatActivity)mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)mContext).getSupportActionBar().setDisplayShowTitleEnabled(false);

        tv_center.setText(mContext.getString(R.string.chats));
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        onShowProgress();
        presenter.onChattingActiveNonActive();

        swipeTORefresh();

    }

    private void swipeTORefresh() {

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeToRefresh.setColorSchemeResources(android.R.color.holo_green_light,
                        android.R.color.holo_blue_bright,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light);
                presenter.onChattingActiveNonActive();
            }
        });



    }

    /**************************************************************************/
    private View getCustomTabView(int i) {
        View view = inflater.inflate(R.layout.custome_tablayout_text, null, false);
        TextView tvHeader = view.findViewById(R.id.tvHeader);
        TextView tvCount = view.findViewById(R.id.tvCount);
        tvCount.setSelected(true);
        tvHeader.setTypeface(appTypeface.getHind_medium());
        tvCount.setTypeface(appTypeface.getHind_regular());
        if(i==0)
        {
            tvHeader.setText(getString(R.string.activeChats));
            tvCountActiveChat = tvCount;
        }
        else
        {
            tvHeader.setText(getString(R.string.pastChats));
            tvCountPastChat = tvCount;
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(BookingChatPojo.BookingChatData data)
    {
        activeChat.clear();
        pastChat.clear();
        activeChat.addAll(data.getAccepted());
        pastChat.addAll(data.getPast());
        tvCountActiveChat.setText(data.getAccepted().size()+"");
        tvCountPastChat.setText(data.getPast().size()+"");
        onNotifyAdapter(activeChat, pastChat);
    }

    private void onNotifyAdapter(ArrayList<BookingChatHistory> activeChat, ArrayList<BookingChatHistory> pastChat) {

        try{
        this.activeChat = activeChat;
        this.pastChat = pastChat;

        if (activeChatFrag != null) {
            activeChatFrag.notifyDataAdapter(this.activeChat);
        }
        if (pastChatFrag != null) {
            pastChatFrag.notifyDataAdapter(this.pastChat);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorNotConnected(String message) {

        alertProgress.tryAgain(getActivity(), getString(R.string.pleaseCheckInternet)/*message + ", " + getString(R.string.pleaseCheckInternet)*/, getString(R.string.system_error), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                if(isClicked)
                {
                    onShowProgress();
                    presenter.onChattingActiveNonActive();
                }

            }
        });
    }

    @Override
    public void onSessionExpired() {

    }

    @Override
    public void onLogout(String message) {
        alertProgress.alertPositiveOnclick(mContext, message, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(mContext,manager);
            }
        });
     //   Utility.setMAnagerWithBID(getActivity(),manager);
    }

    @Override
    public void onError(String error) {

        alertProgress.alertinfo(getActivity(),error);
    }

    @Override
    public void onShowProgress() {
        //   progressBar.setVisibility(View.VISIBLE);
        shimmerChat.startShimmerAnimation();
        //  shimmerProvider.sta
    }

    @Override
    public void onHideProgress() {
        shimmerChat.stopShimmerAnimation();
        shimmerChat.setVisibility(View.GONE);
        swipeToRefresh.setRefreshing(false);
    }
    /**************************************************************************/

}
