package com.github.florent37.singledateandtimepicker.dialog;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.florent37.singledateandtimepicker.DoubleDateTimePicker;
import com.github.florent37.singledateandtimepicker.R;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.widget.WheelMinutePicker;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * <h>SingleDateTimeDialogReturn</h>
 * Created by Ali on 12/7/2017.
 */

public class SingleDateTimeDialogReturn extends BaseDialog
{
    private Listener listener;
    private BottomSheetHelper bottomSheetHelper;
    private DoubleDateTimePicker picker;

    public static TimeZone timeZone;

    @Nullable
    private String timeText0, timeText1,timeText2, timeText3, title;
    @Nullable
    private String todayText;
    @Nullable
    private String buttonOkText,scheduledAt;
    @Nullable
    private Date tab1Date;
  /*  @Nullable
    private TimeZone timeZone;*/

    private TextView tvTime1,tvTime2,tvTime3,tvTime4;
    private View view1,view2,view3,view4;


    private SingleDateTimeDialogReturn(Context context) {
       /* final int layout = bottomSheet ? R.layout.bottom_sheet_picker_single :
                R.layout.bottom_sheet_picker;*/

        final int layout = R.layout.bottom_sheet_picker_single;
        this.bottomSheetHelper = new BottomSheetHelper(context, layout);

        this.bottomSheetHelper.setListener(new BottomSheetHelper.Listener() {
            @Override
            public void onOpen() {
            }

            @Override
            public void onLoaded(View view) {
                init(view);
            }

            @Override
            public void onClose() {
                SingleDateTimeDialogReturn.this.onClose();
            }
        });
    }

    private void init(View view) {
        picker =  view.findViewById(R.id.picker);
        tvTime1 = view.findViewById(R.id.tvTime1);
        tvTime2 = view.findViewById(R.id.tvTime2);
        tvTime3 = view.findViewById(R.id.tvTime3);
        tvTime4 = view.findViewById(R.id.tvTime4);
        view1 = view.findViewById(R.id.view1);
        view2 = view.findViewById(R.id.view2);
        view3 = view.findViewById(R.id.view3);
        view4 = view.findViewById(R.id.view4);
        final FrameLayout titleLayout = view.findViewById(R.id.sheetTitleLayout);
        final TextView sheetTitle = view.findViewById(R.id.sheetTitle);
        final TextView buttonOk = view.findViewById(R.id.buttonOk);
        if (buttonOk != null) {
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    okClicked = true;
                    close();
                }
            });

            if (mainColor != null) {
                buttonOk.setTextColor(mainColor);
            }
        }
        if(title != null) {
            if (sheetTitle != null) {
                sheetTitle.setText(title);
                if (mainColor != null) {
                    sheetTitle.setTextColor(mainColor);
                }
            }
           /* if (mainColor != null && titleLayout != null) {
                titleLayout.setBackgroundColor(mainColor);
            }*/
        }
        else {
            titleLayout.setVisibility(View.GONE);
        }

        picker.setTodayText(todayText);

        final View sheetContentLayout = view.findViewById(R.id.sheetContentLayout);
        if (sheetContentLayout != null) {
            sheetContentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            if (backgroundColor != null) {
                sheetContentLayout.setBackgroundColor(backgroundColor);
            }
        }

        if (timeText0 != null) {
            tvTime1.setText(timeText0);
        }
      /*  displayTab1();
        tvTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayTab1();
            }

        });
        if (timeText1 != null) {
            tvTime2.setText(timeText1);
        }
        tvTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayTab2();
            }
        });
        if (timeText2 != null) {
            tvTime3.setText(timeText2);
        }
        tvTime3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayTab3();
            }
        });
        if (timeText3!= null) {
            tvTime4.setText(timeText3);
        }
        tvTime4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayTab4();
            }
        });*/


        picker.setTimeZone(timeZone);
        if (buttonOk != null) {
            if (buttonOkText != null) {
                buttonOk.setText(buttonOkText);
            }

            if (mainColor != null) {
                buttonOk.setTextColor(mainColor);
            }
        }

        if(buttonOk!=null)
        {
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    okClicked = true;
                    close();
                }
            });
        }


        if (curved) {
            picker.setCurved(true);
           // picker.setVisibleItemCount(7);
            picker.setVisibleItemCount(DEFAULT_ITEM_COUNT_MODE_CURVED);
            picker.setIsAmPm(true);
        } else {
            picker.setCurved(false);
            picker.setIsAmPm(true);
           // picker.setVisibleItemCount(5);
            picker.setVisibleItemCount(DEFAULT_ITEM_COUNT_MODE_NORMAL);
        }

        picker.setMustBeOnFuture(mustBeOnFuture);

        picker.setStepMinutes(minutesStep);

        if (mainColor != null) {
            picker.setSelectedTextColor(mainColor);
        }

        if (minDate != null) {
            picker.setMinDate(minDate);
        }

        if (maxDate != null) {
            picker.setMaxDate(maxDate);
        }

        if (defaultDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(timeZone);
            calendar.setTime(defaultDate);
          //  picker.setTimeZone(timeZone);
            picker.selectDate(calendar);

        }
        if (tab1Date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(timeZone);
            calendar.setTime(tab1Date);
        //    picker.setTimeZone(timeZone);
            picker.selectDate(calendar);
        }

        if (dayFormatter != null) {
            picker.setDayFormatter(dayFormatter);
        }
    }

    public SingleDateTimeDialogReturn setTimeText0(String timeText0) {
        this.timeText0 = timeText0;
        return this;
    }
    public SingleDateTimeDialogReturn setTimeText1(String timeText1) {
        this.timeText1 = timeText1;
        return this;
    }
    public SingleDateTimeDialogReturn setTimeText2(String timeText2) {
        this.timeText2 = timeText2;
        return this;
    }
    public SingleDateTimeDialogReturn setTimeText3(String timeText3) {
        this.timeText3 = timeText3;
        return this;
    }

    public SingleDateTimeDialogReturn setButtonOkText(@Nullable String buttonOkText) {
        this.buttonOkText = buttonOkText;
        return this;
    }

    public SingleDateTimeDialogReturn setTitle(@Nullable String title) {
        this.title = title;
        return this;
    }

    public SingleDateTimeDialogReturn setTodayText(@Nullable String todayText) {
        this.todayText = todayText;
        return this;
    }

    public SingleDateTimeDialogReturn setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public SingleDateTimeDialogReturn setCurved(boolean curved) {
        this.curved = curved;
        return this;
    }

    public SingleDateTimeDialogReturn setMinutesStep(int minutesStep) {
        this.minutesStep = minutesStep;
        return this;
    }

    public SingleDateTimeDialogReturn setMustBeOnFuture(boolean mustBeOnFuture) {
        this.mustBeOnFuture = mustBeOnFuture;
        return this;
    }

    public SingleDateTimeDialogReturn setMinDateRange(Date minDate) {
        this.minDate = minDate;
        return this;
    }

    public SingleDateTimeDialogReturn setMaxDateRange(Date maxDate) {
        this.maxDate = maxDate;
        return this;
    }

    public SingleDateTimeDialogReturn setDefaultDate(Date defaultDate) {
        this.defaultDate = defaultDate;
        return this;
    }

    public SingleDateTimeDialogReturn setDayFormatter(SimpleDateFormat dayFormatter) {
        this.dayFormatter = dayFormatter;
        return this;
    }

    public SingleDateTimeDialogReturn setTab1Date(Date tab1Date) {
        this.tab1Date = tab1Date;
        return this;
    }

    public SingleDateTimeDialogReturn setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    @Override
    public void display() {
        super.display();
        this.bottomSheetHelper.display();
    }

    @Override
    public void dismiss(){
        super.dismiss();
        bottomSheetHelper.dismiss();
    }

    @Override
    public boolean isDisplaying() {
        return super.isDisplaying();
    }

    @Override
    public void close() {
        super.close();
        bottomSheetHelper.hide();
    }

    protected void onClose() {
        super.onClose();
        if (listener != null && okClicked) {
            listener.onDateSelected(Arrays.asList(picker.getDate()),scheduledAt);
        }
    }

    private void displayTab1() {
        tvTime1.setSelected(true);
        view1.setVisibility(View.VISIBLE);
        tvTime2.setSelected(false);view2.setVisibility(View.GONE);
        tvTime3.setSelected(false);view3.setVisibility(View.GONE);
        tvTime4.setSelected(false);view4.setVisibility(View.GONE);
        scheduledAt = tvTime1.getText().toString();
        tvTime1.setTextColor(mainColor);
        tvTime2.setTextColor(Color.BLACK);
        tvTime3.setTextColor(Color.BLACK);
        tvTime4.setTextColor(Color.BLACK);
    }

    private void displayTab2() {
        tvTime2.setSelected(true);view2.setVisibility(View.VISIBLE);
        tvTime1.setSelected(false);view1.setVisibility(View.GONE);
        tvTime3.setSelected(false);view3.setVisibility(View.GONE);
        tvTime4.setSelected(false);view4.setVisibility(View.GONE);
        scheduledAt = tvTime2.getText().toString();
        tvTime2.setTextColor(mainColor);
        tvTime1.setTextColor(Color.BLACK);
        tvTime3.setTextColor(Color.BLACK);
        tvTime4.setTextColor(Color.BLACK);
    }

    private void displayTab3() {
        tvTime3.setSelected(true);view3.setVisibility(View.VISIBLE);
        tvTime2.setSelected(false);view2.setVisibility(View.GONE);
        tvTime1.setSelected(false);view1.setVisibility(View.GONE);
        tvTime4.setSelected(false);view4.setVisibility(View.GONE);
        scheduledAt = tvTime3.getText().toString();
        tvTime3.setTextColor(mainColor);
        tvTime1.setTextColor(Color.BLACK);
        tvTime2.setTextColor(Color.BLACK);
        tvTime4.setTextColor(Color.BLACK);
    }

    private void displayTab4() {
        tvTime4.setSelected(true);view4.setVisibility(View.VISIBLE);
        tvTime2.setSelected(false);view2.setVisibility(View.GONE);
        tvTime3.setSelected(false);view3.setVisibility(View.GONE);
        tvTime1.setSelected(false);view1.setVisibility(View.GONE);
        scheduledAt = tvTime4.getText().toString();
        tvTime4.setTextColor(mainColor);
        tvTime1.setTextColor(Color.BLACK);
        tvTime2.setTextColor(Color.BLACK);
        tvTime3.setTextColor(Color.BLACK);
    }
    public interface Listener {
        void onDateSelected(List<Date> dates, String selectedTime);
    }

    public static class Builder
    {
        private Context context;
        @Nullable
        private SingleDateTimeDialogReturn.Listener listener;
        private boolean bottomSheet;
        private SingleDateTimeDialogReturn dialog;


        @Nullable
        private String timeText0, timeText1,timeText2, timeText3;
        @Nullable
        private String title;
        @Nullable
        private String buttonOkText;
        @Nullable
        private TimeZone timeZone;
        @Nullable
        private String todayText;

        private boolean curved;
        private boolean mustBeOnFuture = true;
        private int minutesStep = WheelMinutePicker.STEP_MINUTES_DEFAULT;

        private SimpleDateFormat dayFormatter;

        @ColorInt
        @Nullable
        private Integer backgroundColor = null;

        @ColorInt
        @Nullable
        private Integer mainColor = null;

        @ColorInt
        @Nullable
        private Integer titleTextColor = null;

        @Nullable
        private Date minDate;
        @Nullable
        private Date maxDate;
        @Nullable
        private Date defaultDate;
        @Nullable
        private Date tab1Date;

        public Builder(Context context) {
            this.context = context;
        }

        public SingleDateTimeDialogReturn.Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder todayText(@Nullable String todayText) {
            this.todayText = todayText;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder bottomSheet() {
            this.bottomSheet = true;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder curved() {
            this.curved = true;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder mustBeOnFuture() {
            this.mustBeOnFuture = true;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder dayFormatter(SimpleDateFormat dayFormatter) {
            this.dayFormatter = dayFormatter;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder minutesStep(int minutesStep) {
            this.minutesStep = minutesStep;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder titleTextColor(@NonNull @ColorInt int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder backgroundColor(@NonNull @ColorInt int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder mainColor(@NonNull @ColorInt int mainColor) {
            this.mainColor = mainColor;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder minDateRange(Date minDate) {
            this.minDate = minDate;
            return this;
        }



        public SingleDateTimeDialogReturn.Builder maxDateRange(Date maxDate) {
            this.maxDate = maxDate;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder defaultDate(Date defaultDate) {
            this.defaultDate = defaultDate;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder tab1Date(Date tab1Date) {
            this.tab1Date = tab1Date;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder listener(
                @Nullable SingleDateTimeDialogReturn.Listener listener) {
            this.listener = listener;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder timeText0(@Nullable String timeText0) {
            this.timeText0 = timeText0;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder timeText1(@Nullable String timeText1) {
            this.timeText1 = timeText1;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder timeText2(@Nullable String timeText2) {
            this.timeText2 = timeText2;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder timeText3(@Nullable String timeText3) {
            this.timeText3 = timeText3;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder buttonOkText(@Nullable String buttonOkText) {
            this.buttonOkText = buttonOkText;
            return this;
        }

        public SingleDateTimeDialogReturn.Builder timeZoneSet(TimeZone timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public SingleDateTimeDialogReturn build()
        {
            final SingleDateTimeDialogReturn dialog = new SingleDateTimeDialogReturn(context)
                    .setButtonOkText(buttonOkText)
                    .setCurved(curved)
                    .setTitle(title)
                    .setDayFormatter(dayFormatter)
                    .setDefaultDate(defaultDate)
                    .setListener(listener)
                    .setMaxDateRange(maxDate)
                    .setMinDateRange(minDate)
                    .setMinutesStep(minutesStep)
                    .setMustBeOnFuture(mustBeOnFuture)
                    .setTimeText0(timeText0)
                    .setTimeText1(timeText1)
                    .setTimeText2(timeText2)
                    .setTimeText3(timeText3)
                    .setTab1Date(tab1Date)
                    .setTimeZone(timeZone);
            if (mainColor != null) {
                dialog.setMainColor(mainColor);
            }

            if (backgroundColor != null) {
                dialog.setBackgroundColor(backgroundColor);
            }

            if (titleTextColor != null) {
                dialog.setTitleTextColor(titleTextColor);
            }
            return dialog;
        }

        public void display() {
            dialog = build();
            dialog.display();
        }

        public boolean isDisplay() {
            return dialog != null && dialog.isDisplaying();
        }

        public void close() {
            if (dialog != null) {
                dialog.close();
            }
        }

        public void dismiss(){
            if(dialog!=null)
                dialog.dismiss();
        }
    }
}

