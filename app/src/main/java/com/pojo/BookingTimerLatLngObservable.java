package com.pojo;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * <h>BookingTimerLatLngObservable</h>
 * Created by Ali on 2/19/2018.
 */

public class BookingTimerLatLngObservable extends Observable<BookingTimerLatLng>
{
    private static BookingTimerLatLngObservable bookingTimerLatLngObservable;

    private Observer<? super BookingTimerLatLng> observer;

    public static BookingTimerLatLngObservable getInstance() {
       if(bookingTimerLatLngObservable == null)
       {
           bookingTimerLatLngObservable = new BookingTimerLatLngObservable();
           return bookingTimerLatLngObservable;
       }else
           return bookingTimerLatLngObservable;
    }

    @Override
    protected void subscribeActual(Observer<? super BookingTimerLatLng> observer)
    {
     this.observer = observer;
    }

    public void emitTimerLatLng(BookingTimer bookingTimer,String jobTimerStatus
                                ,int status)//ProLocation providerDetailsBooking
    {
        try {
            Log.d("TAG", "emitTimerLatLng: "+jobTimerStatus);
            BookingTimerLatLng bookingTimerLatLng = new BookingTimerLatLng();
            bookingTimerLatLng.setBookingTimer(bookingTimer);
            bookingTimerLatLng.setStatus(status);
          //  bookingTimerLatLng.setProviderLocation(providerDetailsBooking);
            bookingTimerLatLng.setJobTimerStatus(jobTimerStatus);
            if(observer!=null)
            {
                observer.onNext(bookingTimerLatLng);
                observer.onComplete();
            }
        }catch (Exception e)
        {
            Log.d("TAG", "emitTimerLatLngException: "+e.getMessage());
        }


    }
}
