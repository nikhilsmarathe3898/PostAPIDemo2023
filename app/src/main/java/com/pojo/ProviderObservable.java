package com.pojo;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * <h>ProviderObservable</h>
 * Created by Ali on 1/30/2018.
 */

public class ProviderObservable extends Observable<ArrayList<ProviderData>>
{
   // private static ProviderObservable providerObservable;
    private Observer<?super ArrayList<ProviderData>>observer;
    @Inject
    public ProviderObservable() {
    }

   /* public static ProviderObservable getInstance()
    {
        if (providerObservable == null) {
            providerObservable = new ProviderObservable();

            return providerObservable;
        } else {
            return providerObservable;
        }
    }*/

    @Override
    protected void subscribeActual(Observer<? super ArrayList<ProviderData>> observer) {

        this.observer = observer;
    }
    public void emitData(ArrayList<ProviderData> data)
    {
        observer.onNext(data);
        observer.onComplete();
    }
}
