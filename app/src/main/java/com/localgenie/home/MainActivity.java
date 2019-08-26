package com.localgenie.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.localgenie.Login.LoginActivity;
import com.localgenie.R;
import com.localgenie.sidescreens.SidescreensFrag;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.videocalling.UtilityVideoCall;
import com.mqtt.MQTTManager;
import com.mqtt.MqttEvents;
import com.pojo.MyBookingObservable;
import com.pojo.MyBookingStatus;
import com.utility.AlertProgress;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class MainActivity extends DaggerAppCompatActivity implements MyBookingPageFrag.OnFragmentInteractionListener {

    private FragmentManager fragmentManager;
    @BindView(R.id.tvService)TextView tvService;
    @BindView(R.id.tvMyProject)TextView tvMyProject;
    @BindView(R.id.tvProfile)TextView tvProfile;
    @BindView(R.id.tvMyChat)TextView tvMyChat;
    @Inject AppTypeface appTypeface;
    @Inject ServicesFrag fragmentService;
    @Inject MyBookingsFrag fragmentProject;
    @Inject SidescreensFrag fragmentProfile;
    @Inject ChatFragment chatFragment;
    @Inject MainActivityContract.MainPresenter presenter;
    @Inject MQTTManager mqttManager;
    @Inject SessionManagerImpl manager;
    @Inject CompositeDisposable disposable;
    @Inject
    AlertProgress alertProgress;
    private boolean  flag_guest_login = false;

    @Inject
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialize();
        onAppOpen();
    }

    private void onAppOpen() {
        manager.setAppOpenTime(manager.getAppOpenTime() + 1);
        if(manager.getAppOpenTime() % 5 == 0 && !manager.getDontShowRate())
        {
            alertProgress.rateApp(this, manager, isClicked -> {

            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag_guest_login = manager.getGuestLogin();

        if(!flag_guest_login)
        {
            manager.setAppOpen(true);
            if(mqttManager.isMQTTConnected())
            {
               publishCalls();
            }else
            {
                if(!mqttManager.isMQTTConnected())
                    mqttManager.createMQttConnection(manager.getSID(),false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        publishCalls();
                    }
                }, 1000);
            }
        }
    }

    private void publishCalls() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("status", 1);
            mqttManager.publish(MqttEvents.CallsAvailability.value + "/" + manager.getSID(), obj, 0, true);//UserId
            UtilityVideoCall.getInstance().setActiveOnACall(false, false);
            mqttManager.subscribeToTopic(MqttEvents.Calls.value+"/"+manager.getSID(),0);
            mqttManager.subscribeToTopic(MqttEvents.Call.value+"/"+manager.getSID(),0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * <p>initialize</p>
     * Initialize the variable for the MainActivity
     */
    private void initialize() {
        fragmentManager = getSupportFragmentManager();
        if(Constants.isConfirmBook)
        {
            presenter.onFragmentTransition("PROJECT",fragmentManager,fragmentService,fragmentProject,fragmentProfile,chatFragment,R.id.frameLayoutContainer);
            selectedService(1);
        }else
        {
            Constants.isMenuActivityCalled = true;
            presenter.onFragmentTransition("HOME",fragmentManager,fragmentService,fragmentProject,fragmentProfile,chatFragment,R.id.frameLayoutContainer);
            selectedService(0);
        }
        tvService.setTypeface(appTypeface.getHind_regular());
        tvMyProject.setTypeface(appTypeface.getHind_regular());
        tvProfile.setTypeface(appTypeface.getHind_regular());
        tvMyChat.setTypeface(appTypeface.getHind_regular());

        InitializeObserver();

    }

    private void InitializeObserver()
    {
        Observer observer = new Observer<MyBookingStatus>() {
            @Override
            public void onSubscribe(Disposable d) {
               // Log.d("TAG", " onSubscribe: "+d.isDisposed());
                disposable.add(d);
            }

            @Override
            public void onNext(MyBookingStatus myBookingStatus) {

                Log.d("TAG", "onNextMain: "+myBookingStatus.getData().getStatus());
                int dataIndex = myBookingStatus.getData().getStatus();
                if( dataIndex == 15)
                {
                    if(fragmentProject!=null && fragmentProject.isAdded())
                    {
                        fragmentProject.onMqttStatusUpdate(myBookingStatus);
                    }else
                    {
                        presenter.onFragmentTransition("PROJECT",fragmentManager,fragmentService,fragmentProject,fragmentProfile,chatFragment,R.id.frameLayoutContainer);
                        selectedService(1);
                    }

                }
                if( dataIndex == 1)
                {
                    if(fragmentProject!=null && fragmentProject.isAdded())
                    {
                        fragmentProject.getBookingService();
                    }else
                    {
                        presenter.onFragmentTransition("PROJECT",fragmentManager,fragmentService,fragmentProject,fragmentProfile,chatFragment,R.id.frameLayoutContainer);
                        selectedService(1);
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
        MyBookingObservable.getInstance().subscribe(observer);
    }

    /**
     *
     * @param view clicked for the view of the Activity
     */
    @OnClick({R.id.tvService,R.id.tvMyProject,R.id.tvProfile,R.id.tvMyChat})
    public void serviceClicked(View view) {
        switch (view.getId())
        {
            case R.id.tvService:
                disposable.clear();
                selectedService(0);
                presenter.onFragmentTransition("HOME",fragmentManager,fragmentService,fragmentProject,fragmentProfile,chatFragment,R.id.frameLayoutContainer);
                break;
            case R.id.tvMyProject:
                if(flag_guest_login)
                {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    disposable.clear();
                    selectedService(1);
                    if(fragmentProject.isAdded())
                    {
                        if(!Constants.isLoggedIn)
                        {
                            fragmentProject.getBookingService();
                        }
                    }
                    presenter.onFragmentTransition("PROJECT",fragmentManager,fragmentService,fragmentProject,fragmentProfile,chatFragment,R.id.frameLayoutContainer);
                }
                break;
            case R.id.tvMyChat:
                if(flag_guest_login)
                {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    selectedService(2);
                /*if(chatFragment.isAdded())
                {
                    chatFragment.chatHistory();
                }*/
                    presenter.onFragmentTransition("CHAT",fragmentManager,fragmentService,fragmentProject,fragmentProfile,chatFragment,R.id.frameLayoutContainer);
                }



                break;
            case R.id.tvProfile:
                disposable.clear();
                selectedService(3);
                if(fragmentProfile.isAdded())
                {
                    fragmentProfile.walletBalanceValues();
                }
                presenter.onFragmentTransition("PROFILE",fragmentManager,fragmentService,fragmentProject,fragmentProfile,chatFragment,R.id.frameLayoutContainer);

                break;

        }
    }

    /**
     * <p>selectedService</p>
     * @param position selected position of the activity to show the fragment
     */
    private void selectedService(int position)
    {
        switch (position)
        {
            case 0:
                tvService.setSelected(true);
                tvMyProject.setSelected(false);
                tvProfile.setSelected(false);
                tvMyChat.setSelected(false);
               /* if(mqttManager.isMQTTConnected())
                {
                    mqttManager.unSubscribeToTopic(MqttEvents.JobStatus.value + "/" + manager.getSID());
                }*/
                break;
            case 1:
                /*if(mqttManager.isMQTTConnected())
                {
                    mqttManager.subscribeToTopic(MqttEvents.JobStatus.value + "/" + manager.getSID(),1);
                }*/
                tvService.setSelected(false);
                tvMyProject.setSelected(true);
                tvProfile.setSelected(false);
                tvMyChat.setSelected(false);
                break;
            case 2:
                tvService.setSelected(false);
                tvMyProject.setSelected(false);
                tvProfile.setSelected(false);
                tvMyChat.setSelected(true);
                break;

            case 3:
                tvService.setSelected(false);
                tvMyProject.setSelected(false);
                tvProfile.setSelected(true);
                tvMyChat.setSelected(false);
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Utility.checkAndShowNetworkError(this);
    }

    @Override
    public void onDateSelectedApi(long fromDate, long toDate, boolean ApiCalled) {
        fragmentProject.onDateSelected(fromDate,toDate,ApiCalled);
    }
}
