package com.localgenie.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.pojo.BookingChatHistory;

import java.util.ArrayList;

import adapters.ChatBookingsPageAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ali on 5/28/2018.
 */
public class ChatPageFragment extends Fragment
{
    private static final String TAG = "MyEVENTPAGEFRAG";
    private static final String PAGE_COUNT= "pagecount";
    private Context mcontext;
    private ArrayList<BookingChatHistory> bookingChatHistories = new ArrayList<>();
    private ChatBookingsPageAdapter adapterAssign;
    private int pageCount = 0;
    @BindView(R.id.recyclerChatBookings)RecyclerView recyclerChatBookings;
    @BindView(R.id.rlNoChatFound)RelativeLayout rlNoChatFound;
    @BindView(R.id.tvNoChatBooking)TextView tvNoChatBooking;

    public static ChatPageFragment newInstance(int pageValue)
    {
        Bundle args = new Bundle();
        // args.putSerializable(PENDINGALL_JOBS, bookingEvents);
        args.putInt(PAGE_COUNT,pageValue);
        ChatPageFragment fragment = new ChatPageFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle=getArguments();

        pageCount = bundle.getInt(PAGE_COUNT,0);
        mcontext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_bookings_page, container, false);
        ButterKnife.bind(this,view);
        initializeView();
        tvNoChatBooking.setTypeface(AppTypeface.getInstance(mcontext).getHind_regular());
        return view;
    }

    private void initializeView() {

        LinearLayoutManager llManager = new LinearLayoutManager(mcontext);
        AppTypeface typeface = AppTypeface.getInstance(mcontext);
        int resId = R.anim.layoutanimation_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mcontext, resId);
        recyclerChatBookings.setLayoutAnimation(animation);
        recyclerChatBookings.setLayoutManager(llManager);
        adapterAssign = new ChatBookingsPageAdapter(mcontext, bookingChatHistories);
        recyclerChatBookings.setAdapter(adapterAssign);
        textValueToBeSet();
    }

    public void notifyDataAdapter(ArrayList<BookingChatHistory> eventData) {
        try {
        bookingChatHistories.clear();
        bookingChatHistories.addAll(eventData);
        if (bookingChatHistories.size() > 0) {
            rlNoChatFound.setVisibility(View.GONE);
            recyclerChatBookings.setVisibility(View.VISIBLE);
        } else {
            recyclerChatBookings.setVisibility(View.GONE);
            rlNoChatFound.setVisibility(View.VISIBLE);
        }
        adapterAssign.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }

    private void textValueToBeSet()
    {
        if(pageCount==0)
        {
            tvNoChatBooking.setText(getString(R.string.youHaveNoOpenChat));
        }else if(pageCount==1)
        {
            tvNoChatBooking.setText(getString(R.string.youHaveNoPastChat));
        }
    }

}
