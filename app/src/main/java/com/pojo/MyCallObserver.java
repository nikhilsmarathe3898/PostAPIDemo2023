package com.pojo;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Created by Ali on 12/20/2018.
 */
public class MyCallObserver extends Observable<MyCallData> {

    Observer<? super MyCallData> observer;

    private static MyCallObserver myCallObserver;

    public static MyCallObserver getInstance()
    {
        if(myCallObserver==null)
        {
            myCallObserver = new MyCallObserver();
            return myCallObserver;
        }else
            return myCallObserver;
    }

    @Override
    protected void subscribeActual(Observer<? super MyCallData> observer) {
        this.observer = observer;
    }

    public void emitCallData(MyCallData myCallData)
    {
        observer.onNext(myCallData);
        observer.onComplete();
    }
}
