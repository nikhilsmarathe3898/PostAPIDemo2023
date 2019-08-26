package com.utility;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.localgenie.R;

import java.util.TimeZone;

/**
 * Created by murashid on 05-Aug-17.
 */

public class CalendarEventHelper {

    private static final String TAG = "CalendarEventHelper";
    private Context context;

    public CalendarEventHelper(Context context)
    {
        this.context = context;
    }

    public int addEvent(long bookingtime,long bookingId) {

        try {
            if(haveCalendarReadWritePermissions())
            {
                int calendarId = Integer.parseInt(getCalendarId());
                Log.d(TAG, "addEvent: Final  ID "+calendarId);
                long startTime = bookingtime*1000;
                //long endTime =Utility.convertUTCToTimeStamp(booking.getBookingRequestedFor());
                String description =  context.getString(R.string.remainderDescText);
                //String location = booking.getAddLine1();

                /*if(booking.getBookingEndtime().matches("[0-9]+"))
                {
                    endTime =Utility.convertUTCToTimeStamp(booking.getBookingEndtime());
                }*/

                /*if(booking.getAddLine2() != null && !booking.getAddLine2().equals(""))
                {
                    location = location + " "+booking.getAddLine2().equals("");
                }*/

                ContentResolver cr = context.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, startTime);
                //values.put(CalendarContract.Events.DTEND, endTime);
                values.put(CalendarContract.Events.DTEND,(startTime+3600000));
                values.put(CalendarContract.Events.TITLE, context.getString(R.string.app_name));
                values.put(CalendarContract.Events.DESCRIPTION,description);
                values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
                values.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED);
                values.put(CalendarContract.Events.HAS_ALARM, true);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
                //values.put(CalendarContract.Events.EVENT_LOCATION, location);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return 0;
                }

                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                Log.d(TAG, "Uri returned=>"+uri.toString());
                // get the event ID that is the last element in the Uri
                int eventID = Integer.parseInt(uri.getLastPathSegment());

                ContentValues reminders = new ContentValues();
                reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
                reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                reminders.put(CalendarContract.Reminders.MINUTES, 60);

                Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);

                return eventID;
            }
            else
            {
                Log.d(TAG, "addEvent: No Permission");
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "MakeNewCalendarEntry: "+e);
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteEvent(long eventId)
    {
        try {
             Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
             int rows = context.getContentResolver().delete(deleteUri, null, null);

            Log.d(TAG, "deleteEvent: "+rows);
        }
        catch (Exception e)
        {
            Log.d(TAG, "deleteEvent: "+e);
            e.printStackTrace();
        }
    }

    @Nullable
    private String getCalendarId() {

        String googleCalenderId = "";
        String normalCalenderId = "";

        if (haveCalendarReadWritePermissions()) {

            String projection[] = {"_id", "calendar_displayName"};
            Uri calendars;
            calendars = Uri.parse("content://com.android.calendar/calendars");

            ContentResolver contentResolver = context.getContentResolver();
            Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

            if(managedCursor !=null)
            {
                if (managedCursor.moveToFirst())
                {
                    String calName;
                    String calID;
                    int cont = 0;
                    int nameCol = managedCursor.getColumnIndex(projection[1]);
                    int idCol = managedCursor.getColumnIndex(projection[0]);
                    do
                    {
                        calName = managedCursor.getString(nameCol);
                        calID = managedCursor.getString(idCol);
                        Log.v(TAG, "CalendarName:" + calName + " ,id:" + calID);

                        if(calName.contains("@gmail"))
                        {
                            googleCalenderId = calID;
                        }
                        cont++;
                    } while (managedCursor.moveToNext());
                    managedCursor.close();

                    if(!googleCalenderId.equals(""))
                    {
                        return googleCalenderId;
                    }
                    else
                    {
                        return calID;
                    }
                }
                else
                {
                    return "3";
                }
            }
            else
            {
                return "3";
            }
        }
        else
        {
            return "3";
        }
    }



    private  boolean haveCalendarReadWritePermissions()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR);

        if (permissionCheck== PackageManager.PERMISSION_GRANTED)
        {
            permissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_CALENDAR);

            if (permissionCheck== PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
        }
        return false;
    }

}

