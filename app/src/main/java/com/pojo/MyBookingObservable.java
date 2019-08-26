package com.pojo;

import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * <h>MyBookingObservable</h>
 * Created by Ali on 2/14/2018.
 */

public class MyBookingObservable extends Observable<MyBookingStatus>
{
    private Observer<? super MyBookingStatus> observer;
    private static MyBookingObservable observebleClass;
    private ArrayList<Observer<? super MyBookingStatus>> myListObserver = new ArrayList<>();

    @Inject
    public MyBookingObservable() {
    }

    public static MyBookingObservable getInstance() {
        if (observebleClass == null) {
            observebleClass = new MyBookingObservable();

            return observebleClass;
        } else {
            return observebleClass;
        }
    }

    @Override
    protected void subscribeActual(Observer<? super MyBookingStatus> observer) {
        this.observer = observer;
        Log.w("TAG", "emitsubscribeActual: "+myListObserver.size());
        try{
            if(!myListObserver.contains(observer)){
                myListObserver.add(observer);
                Log.w("TAG", "emitsubscribeActual1: "+myListObserver.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void removeObserver(Observer<? super MyBookingStatus> observer)
    {
        Log.w("TAG", "emitremoveObserver: "+myListObserver.size());
        if(myListObserver.contains(observer))
        {
            myListObserver.remove(observer);
            Log.w("TAG", "emitremoveObserver1: "+myListObserver.size());
        }

    }

   public void emitJobStatus(MyBookingStatus myBookingStatus)
    {
        Log.d("TAG", "emitJobStatus: "+myBookingStatus.getData().getStatus());
        Log.w("TAG", "emit: "+myListObserver.size());
        for (int i =0 ;i<myListObserver.size();i++)
        {
            Observer<?super MyBookingStatus> tempobserver = myListObserver.get(i);
            tempobserver.onNext(myBookingStatus);
            tempobserver.onComplete();
        }
    }
}
