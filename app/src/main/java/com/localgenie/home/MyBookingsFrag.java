package com.localgenie.home;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.localgenie.R;
import com.localgenie.invoice.InvoiceActivity;
import com.localgenie.rateYourBooking.RateYourBooking;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;
import com.pojo.AllBookingEventPojo;
import com.pojo.MyBookingDataPojo;
import com.pojo.MyBookingObservable;
import com.pojo.MyBookingStatus;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;
import com.utility.NotificationUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import adapters.ViewPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class MyBookingsFrag extends DaggerFragment implements MyBookingFragContract.MyProjectView {

  private static boolean isBookingAccepted = false;
  @BindView(R.id.tabLayoutMyEvent)
  TabLayout tabLayoutMyEvent;
  @BindView(R.id.myEventprogressBar)
  ProgressBar myEventprogressBar;
  @BindView(R.id.viewPagerMyEvent)
  ViewPager viewPagerMyEvent;
  @BindView(R.id.toolbarlayout)
  Toolbar toolbarlayout;
  @BindView(R.id.tv_center)
  TextView tv_center;
  @Inject
  AppTypeface appTypeface;
  @Inject
  MyBookingFragContract.MyProjectPresenter presenter;
  @Inject
  AlertProgress alertProgress;
  @Inject
  SessionManagerImpl manager;
  @Inject
  MQTTManager mqttManager;
  private String TAG = MyBookingsFrag.class.getSimpleName();
  private ArrayList<AllBookingEventPojo> pendingPojo = new ArrayList<>();
  private ArrayList<AllBookingEventPojo> upComingPojo = new ArrayList<>();
  private ArrayList<AllBookingEventPojo> pastPojo = new ArrayList<>();
  private Observer<MyBookingStatus> observer;
  private MyBookingPageFrag pendingBooking, upComing, pastBooking;
  private Context mContext;
  private ViewPagerAdapter viewPagerAdapter;
  private MyBookingObservable myBookingObservable;
  private NotificationUtils notificationUtils;
  private CompositeDisposable disposable;
  private long fromDate, toDate;
  private boolean apiCalled;

  public MyBookingsFrag() {

  }

  @SuppressLint("ValidFragment")
  @Inject
  public MyBookingsFrag(MyBookingObservable myBookingObservable) {
    this.myBookingObservable = myBookingObservable;
  }

  public static void onMyGetBooking() {
    isBookingAccepted = true;
  }

  @Override
  public void onStart() {
    super.onStart();

  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_projects, container, false);
    mContext = getActivity();
    disposable = new CompositeDisposable();
    presenter.onContextReceived(mContext);
    ButterKnife.bind(this, view);
    MqttConnection();
    initializeToolBar();
    presenter.attachView(this);
    initializeRxJavaListener();
    notificationUtils = new NotificationUtils(mContext);
    return view;
  }

  private void MqttConnection() {
    if (!mqttManager.isMQTTConnected()) {
      mqttManager.createMQttConnection(manager.getSID(), false);
      new Handler().postDelayed(
          () -> mqttManager.subscribeToTopic(MqttEvents.JobStatus.value + "/" + manager.getSID(),
              1), 1000);
    } else {
      mqttManager.subscribeToTopic(MqttEvents.JobStatus.value + "/" + manager.getSID(), 1);
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    mContext = getActivity();
    presenter.attachView(this);
    Log.d(TAG, "onDestroyCALLEDOnResume: " + mContext + " isBookingAccepted " + isBookingAccepted);
    presenter.onContextReceived(mContext);

    Constants.isJobDetailsOpen = false;
    if (observer != null) {
      MyBookingObservable.getInstance().replay();
    }
    if (isBookingAccepted) {
      getBookingService();
      isBookingAccepted = false;
    }

    if (pendingBooking != null) {
      pendingBooking.isCalledActivity();
    }
    if (upComing != null) {
      upComing.isCalledActivity();
    }
    if (pastBooking != null) {
      pastBooking.isCalledActivity();
    }

  }

  private void initializeRxJavaListener() {
    // Log.d(TAG, "initializeRxJavaListener: "+MyBookingObservable.getInstance());
    Log.d(TAG, "initializeRxJavaListener: " + myBookingObservable);


    observer = new Observer<MyBookingStatus>() {
      @Override
      public void onSubscribe(Disposable d) {
        Log.d(TAG, " onSubscribe: " + d.isDisposed());
        disposable.add(d);
      }

      @Override
      public void onNext(MyBookingStatus myBookingStatus) {

        Log.d(TAG, "onNextMQTTRESPONSEFrag: " + myBookingStatus.getData().getStatus());
        int dataIndex = myBookingStatus.getData().getStatus();
        // presenter.attachView(this);
        if (dataIndex == 10) {

          if (!Constants.isJobDetailsOpen) {
            if (getActivity() != null) {
              Intent intent = new Intent(getActivity(), RateYourBooking.class);
              intent.putExtra("BID", myBookingStatus.getData().getBookingId());
              intent.putExtra("PROIMAGE", myBookingStatus.getData().getProProfilePic());

              startActivity(intent);
            }
          }

        } else if (dataIndex == 9 && myBookingStatus.getData().getCallType() == 3) {

          if (!Constants.isJobDetailsOpen) {
            if (getActivity() != null) {
              Intent intent = new Intent(getActivity(), InvoiceActivity.class);
              intent.putExtra("BID", myBookingStatus.getData().getBookingId());
              intent.putExtra("PROIMAGE", myBookingStatus.getData().getProProfilePic());
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(intent);
              ((AppCompatActivity) mContext).finish();
            }
          }

        } else // booking completed
        {
          if (myBookingStatus.getData().getStatus() == 3
              && myBookingStatus.getData().getBookingType() == 3)//&& !Constants.isJobDetailsOpen
          //    getBookingService();
          {
            presenter.onBookingService();
          } else {
            presenter.onMqttJobStatus(myBookingStatus, pendingPojo, upComingPojo, pastPojo,
                mContext, notificationUtils);
          }

        }
      }

      @Override
      public void onError(Throwable e) {

      }

      @Override
      public void onComplete() {

      }
    };
    MyBookingObservable.getInstance().subscribeOn(Schedulers.io())
        .subscribe(observer);
    MyBookingObservable.getInstance().subscribe(observer);

  }

  private void initializeToolBar() {
    Constants.isConfirmBook = false;
    getBookingService();
    ((AppCompatActivity) mContext).setSupportActionBar(toolbarlayout);
    ((AppCompatActivity) mContext).getSupportActionBar().setDisplayShowHomeEnabled(false);
    ((AppCompatActivity) mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    ((AppCompatActivity) mContext).getSupportActionBar().setDisplayShowTitleEnabled(false);

    tv_center.setText(mContext.getString(R.string.bookings));
    tv_center.setTypeface(appTypeface.getHind_semiBold());
    tabLayoutMyEvent.setTabGravity(TabLayout.GRAVITY_FILL);
    tabLayoutMyEvent.setSelectedTabIndicatorHeight(5);
    tabLayoutMyEvent.setSelectedTabIndicatorColor(Utility.getColor(mContext,
        R.color.parrotGreen));//getResources().getColor(R.color.actionbar_color)
    viewPagerMyEvent.setOffscreenPageLimit(3);
    viewPagerMyEvent.addOnPageChangeListener(
        new TabLayout.TabLayoutOnPageChangeListener(tabLayoutMyEvent));
    viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
    tabLayoutMyEvent.setupWithViewPager(viewPagerMyEvent);
    onCreateLoadAdapter();

  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mContext = context;
  }

  public void getBookingService() {
    if (Utility.isNetworkAvailable(mContext)) {
      onShowProgress();
      presenter.onBookingService();
    } else {
      alertProgress.showNetworkAlert(mContext);
    }
  }

  private void onCreateLoadAdapter() {

    pendingBooking = MyBookingPageFrag.newInstance(0);//pendingPojo
    viewPagerAdapter.addFragment(pendingBooking
        , getActivity().getResources().getString(R.string.pending));

    upComing = MyBookingPageFrag.newInstance(1);//upComingPojo
    viewPagerAdapter.addFragment(upComing,
        getActivity().getResources().getString(R.string.UpComing));

    pastBooking = MyBookingPageFrag.newInstance(2);//pastPojo
    viewPagerAdapter.addFragment(pastBooking
        , getActivity().getResources().getString(R.string.Past));
    viewPagerMyEvent.setAdapter(viewPagerAdapter);
    ViewGroup vg = (ViewGroup) tabLayoutMyEvent.getChildAt(0);
    int tabsCount = vg.getChildCount();
    for (int k = 0; k < tabsCount; k++) {
      ViewGroup vgTab = (ViewGroup) vg.getChildAt(k);
      int tabChildsCount = vgTab.getChildCount();
      for (int i = 0; i < tabChildsCount; i++) {
        View tabViewChild = vgTab.getChildAt(i);
        if (tabViewChild instanceof TextView) {
          ((TextView) tabViewChild).setTypeface(appTypeface.getHind_medium());
        }
      }
    }
  }

  @Override
  public void onSessionExpired() {

  }

  @Override
  public void onLogout(String message) {
    if (isAdded()) {
      alertProgress.alertPositiveOnclick(mContext, message, getString(R.string.logout),
          getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
              Utility.setMAnagerWithBID(mContext, manager);
            }
          });
    }
  }

  @Override
  public void onError(String error) {

    alertProgress.alertinfo(mContext, error);
  }

  @Override
  public void onShowProgress() {

    ((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    myEventprogressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void onHideProgress() {

    myEventprogressBar.setVisibility(View.GONE);
    ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    presenter.detachView();
    MyBookingObservable.getInstance().removeObserver(observer);
    //myBookingObservable.removeObserver(observer);
    disposable.clear();
  }

  @Override
  public void onBookingResponseSuccess(MyBookingDataPojo data) {
    pendingPojo.clear();
    upComingPojo.clear();
    pastPojo.clear();
    pendingPojo.addAll(data.getPending());
    upComingPojo.addAll(data.getUpcoming());
    pastPojo.addAll(data.getPast());
    //  Log.d(TAG, "onBookingResponseSuccess: " + data.getPast().get(0).getStatusMsg());
    onNotifyAdapter(pendingPojo, upComingPojo, pastPojo, true);
  }

  @Override
  public void onUpComingBooking(ArrayList<AllBookingEventPojo> upcoming) {
    upComingPojo.clear();
    upComingPojo.addAll(upcoming);
    onNotifyAdapter(pendingPojo, upComingPojo, pastPojo, false);
  }

  @Override
  public void onNotifyAdapter(ArrayList<AllBookingEventPojo> pendingPojo,
      ArrayList<AllBookingEventPojo> upComingPojo, ArrayList<AllBookingEventPojo> pastPojo,
      boolean isFirstOrItemAdded) {
    try {
      this.pendingPojo = pendingPojo;
      this.upComingPojo = upComingPojo;
      this.pastPojo = pastPojo;

      if (pendingBooking != null) {
        pendingBooking.notifyDataAdapter(this.pendingPojo, 1, isFirstOrItemAdded);
        pendingBooking.isCalledActivity();
      }
      if (upComing != null) {
        upComing.notifyDataAdapter(this.upComingPojo, 2, isFirstOrItemAdded);
        upComing.isCalledActivity();
      }
      if (pastBooking != null) {
        pastBooking.notifyDataAdapter(this.pastPojo, 3, isFirstOrItemAdded);
        pastBooking.isCalledActivity();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public Context onContext() {
    return mContext;
  }

  @Override
  public void onConnectionError(String message, boolean isGetApi) {
    try {
      alertProgress.tryAgain(mContext,
          mContext.getResources().getString(R.string.pleaseCheckInternet),
          getString(R.string.system_error), isClicked -> {
            if (isClicked) {
              if (isGetApi) {
                getBookingService();
              } else {
                onDateSelected(fromDate, toDate, apiCalled);
              }
            }

          });
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onPause() {
    super.onPause();

  }

  public void onMqttStatusUpdate(MyBookingStatus myBookingStatus) {
    presenter.onMqttJobStatus(myBookingStatus, pendingPojo, upComingPojo, pastPojo, mContext,
        notificationUtils);
  }

  public void onDateSelected(long fromDate, long toDate, boolean apiCalled) {
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.apiCalled = apiCalled;
    if (apiCalled) {
      onShowProgress();
    }
    presenter.onUpcommingApi(fromDate, toDate);
  }
}
