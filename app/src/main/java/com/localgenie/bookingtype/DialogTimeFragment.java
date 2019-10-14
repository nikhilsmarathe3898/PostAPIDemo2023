package com.localgenie.bookingtype;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.localgenie.R;
import com.localgenie.utilities.AppTypeface;
import com.utility.AlertProgress;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DialogTimeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DialogTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DialogTimeFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_CUSTOME = "isCustomSelected";
    private static final String ARG_SELECTED = "selectedDays";
    private static final String ARG_CurrentDay = "currentDay";

    private static final String TAG = "DialogTimeFragment";

    // TODO: Rename and change types of parameters
    private String mParam1,currentDay = "";
    private ArrayList<String> selectedDays;
    private boolean mParam2,isCustomSelected,isCurrentDaysAvailable;


    private OnFragmentInteractionListener mListener;

    private AlertProgress alertProgress;
    TextView tvSelectedTIme,tvDone;
    ImageView ivClose;
    TimePicker timePicker;
    Context mContext;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy',' h:mm a",Locale.getDefault());
    static SimpleDateFormat sdf;
    static Calendar selectedDate;
    public DialogTimeFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment DialogTimeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DialogTimeFragment newInstance(boolean param2,String calendar,boolean isCustomSelected
            ,ArrayList<String> selectedDays,String currentDays) {
        DialogTimeFragment fragment = new DialogTimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, calendar);
        args.putBoolean(ARG_PARAM2, param2);
        args.putBoolean(ARG_CUSTOME, isCustomSelected);
        args.putStringArrayList(ARG_SELECTED, selectedDays);
        args.putString(ARG_CurrentDay, currentDays);
        selectedDate = Calendar.getInstance();
        //April 30, 2019, 1:00 PM
        sdf = new SimpleDateFormat("d-MMMM-yyyy, h:m a");
        try {
            Date parse = sdf.parse(calendar);
            selectedDate.setTimeInMillis(parse.getTime());
            Log.d(TAG, "newInstance: selected day:"+parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam2 = getArguments().getBoolean(ARG_PARAM2);
            mParam1 = getArguments().getString(ARG_PARAM1);
            isCustomSelected = getArguments().getBoolean(ARG_CUSTOME);
            currentDay = getArguments().getString(ARG_CurrentDay);
            selectedDays = getArguments().getStringArrayList(ARG_SELECTED);
            Log.d("TAG", "onCreate: "+isCustomSelected +" currentDays "+currentDay
                    +" SelectedDays "+selectedDays);

            if(currentDay!=null && !currentDay.equals("")) {
                for(String day : selectedDays) {
                    if(day.equals(currentDay)) {
                        isCurrentDaysAvailable = true;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dialog_time,container,false);
        initialize(v);
        return v;
    }

    String statusAmPm;
    private void initialize(View v) {
        tvSelectedTIme = v.findViewById(R.id.tvSelectedTIme);
        tvDone = v.findViewById(R.id.tvDone);
        ivClose = v.findViewById(R.id.ivClose);
        timePicker = v.findViewById(R.id.timePicker);
        tvSelectedTIme.setTypeface(AppTypeface.getInstance(mContext).getHind_medium());
        tvDone.setTypeface(AppTypeface.getInstance(mContext).getHind_semiBold());
        setTimePickerInterval(timePicker);
        String dateTime = simpleDateFormat.format(System.currentTimeMillis());
        Log.d("TAG", "initialize: "+dateTime+ " param "+mParam1);

        tvDone.setOnClickListener(view -> {
            Calendar tempCal = Calendar.getInstance();
            int hours = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute() * 30;
            Log.d(TAG, "initialize: hours: " + hours + " Minute: " + minute);
            tempCal.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DATE),
                    hours, minute);

            if(mParam2){
                Calendar tempPlusBuffer=Calendar.getInstance();
                tempPlusBuffer.setTimeInMillis(Calendar.getInstance().getTimeInMillis() + TimeUnit.HOURS.toMillis(1));
                tempCal.set(tempCal.get(Calendar.YEAR),tempCal.get(Calendar.MONTH),tempCal.get(Calendar.DATE),hours,minute);
                if(tempCal.before(tempPlusBuffer)){
                    alertProgress.alertinfo(mContext,getString(R.string.select_time_hour));
                    return;
                }
            }
            Log.d(TAG, "initialize: tempcal" + tempCal);
            onButtonPressed(tempCal.getTime(),"",mParam2);

           /* if (hours > 12) {
                hours -= 12;
                statusAmPm = "PM";
            } else if (hours == 0) {
                hours += 12;
                statusAmPm = "AM";
            } else if (hours == 12)
                statusAmPm = "PM";
            else
                statusAmPm = "AM";
*/
            /*try
            {
                String datetimeAp = convertAmPm(mParam1.split(",")[2]);

                int min = timePicker.getCurrentMinute()*30;
                if(currentDay!=null && !currentDay.equals("")) {

                    if(isCurrentDaysAvailable) {
                        callCurrentDaysCalculationForTIme(datetimeAp,hours,min);
                    }else {
                        String time = hours+":"+min+" "+statusAmPm;
                        onButtonPressed(mParam1,time,mParam2);
                    }
                }else
                {
                    if(dateTime.split(",")[0].equals(mParam1.split(",")[0]))
                       callCurrentDaysCalculationForTIme(datetimeAp,hours,min);
                    else
                    {
                        String time = hours+":"+min+" "+statusAmPm;
                        onButtonPressed(mParam1,time,mParam2);
                    }
                }


            }catch (Exception e)
            {
                e.printStackTrace();
            }

        });*/

        });}

   /* private void callCurrentDaysCalculationForTIme(String datetimeAp, int hours, int min) {

        Log.d(TAG, "callCurrentDaysCalculationForTIme: "+datetimeAp);
        String spltDateTime[] = datetimeAp.split(":");
        Log.d("DIALOG", "glRepeatPartTimeGrid: "+timePicker.getCurrentHour()+" CurrentTime "+Integer.parseInt(spltDateTime[0])+" min "+min
                +" SplittedTime "+Integer.parseInt(spltDateTime[1]));

        if(timePicker.getCurrentHour() > Integer.parseInt(spltDateTime[0]))
        {
            int diffhr = timePicker.getCurrentHour()- Integer.parseInt(spltDateTime[0]);
            if(diffhr>1)
            {
                String time = hours+":"+min+" "+statusAmPm;
                onButtonPressed(mParam1,time,mParam2);
            }else
            {
                if(min>Integer.parseInt(spltDateTime[1]))
                {
                    String time = hours+":"+min+" "+statusAmPm;
                    onButtonPressed(mParam1,time,mParam2);
                }else
                    alertProgress.alertinfo(mContext,getString(R.string.bookingTimeShouldBeMoreThenCurrentTime));
            }

        }else
        {
            alertProgress.alertinfo(mContext,getString(R.string.bookingTimeShouldBeMoreThenCurrentTime));
        }


    }*/

    private String convertAmPm(String datTime) {

        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("h:mm a");
        Date date = null;
        String dateFormat = "";
        try {
            date = parseFormat.parse(datTime);
            dateFormat = displayFormat.format(date);
            return dateFormat;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Date date, String time, boolean mParam2)
    {

     /*   String[] timSEl = mParam1.split(",");
        String timeDone = timSEl[0]+","+timSEl[1]+", "+time;
        Log.d("TAG", "onButtonPressed: "+timeDone);*/
        //try {
        //String dateTIme = sdf.format(date);
        if (mListener != null) {
            mListener.onFragmentInteraction(date,mParam2);
            dismiss();
        }
        /*} catch (ParseException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        alertProgress = new AlertProgress(mContext);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Date uri,boolean isSchedule);
    }

    private final int TIME_PICKER_INTERVAL = 30;
    private void setTimePickerInterval(TimePicker timePicker) {
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            // Field timePickerField = classForid.getField("timePicker");

            Field field = classForid.getField("minute");
            NumberPicker minutePicker = (NumberPicker) timePicker
                    .findViewById(field.getInt(null));

            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(1);
            ArrayList<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues
                    .toArray(new String[0]));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
