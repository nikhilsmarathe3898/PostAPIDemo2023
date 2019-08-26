package com.pojo;


import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * <h>LiveTrackObservable</h>
 * Created by Ali on 11/10/2017.
 */

public class LiveTrackObservable extends Observable<LiveTackPojo> {
    private static LiveTrackObservable observebleClass;
    private Observer<? super LiveTackPojo> observer;

    public static LiveTrackObservable getInstance() {
        if (observebleClass == null) {
            observebleClass = new LiveTrackObservable();
            return observebleClass;
        } else {
            return observebleClass;
        }
    }

    @Override
    protected void subscribeActual(Observer<? super LiveTackPojo> observer) {
        this.observer = observer;
    }

    public void emitLiveTrack(LiveTackPojo liveTackPojo) {
        observer.onNext(liveTackPojo);
        observer.onComplete();

    }
}
