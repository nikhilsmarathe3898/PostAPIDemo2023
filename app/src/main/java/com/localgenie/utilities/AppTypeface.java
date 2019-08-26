package com.localgenie.utilities;

import android.content.Context;
import android.graphics.Typeface;

/**
 * <h2>AppTypeface</h2>
 * This class contains several methods that are used for setting and getting methods for typeFace.
 */

public class AppTypeface {
    private static AppTypeface setTypeface = null;
    private Typeface hind_regular, hind_semiBold, hind_bold, hind_medium, hind_light,digital_clock;

    private AppTypeface(Context context) {
        initTypefaces(context);
    }

    /**
     * <h2>AppTypeface</h2>
     *
     * @param context: calling activity reference
     * @return : Single instance of this class
     */
    public static AppTypeface getInstance(Context context) {
        if (setTypeface == null) {
            setTypeface = new AppTypeface(context.getApplicationContext());
        }
        return setTypeface;
    }

    /**
     * <h2>initTypefaces</h2>
     * <p>
     * method to initializes the typefaces of the app
     * </p>
     *
     * @param context Context of the Activity
     */
    private void initTypefaces(Context context) {

        this.hind_regular = Typeface.createFromAsset(context.getAssets(), "fonts/Hind-Regular_0.ttf");
        this.hind_semiBold = Typeface.createFromAsset(context.getAssets(), "fonts/Hind-Semibold_0.ttf");
        this.hind_bold = Typeface.createFromAsset(context.getAssets(), "fonts/Hind-Bold.ttf");
        this.hind_medium = Typeface.createFromAsset(context.getAssets(), "fonts/Hind-Medium_0.ttf");
        this.hind_light = Typeface.createFromAsset(context.getAssets(), "fonts/Hind-Light_0.ttf");
        this.digital_clock = Typeface.createFromAsset(context.getAssets(), "fonts/digital-7.ttf");
    }


    //======== GETTER METHODS FOR ALL TYPEFACES

    public Typeface getHind_regular() {
        return hind_regular;
    }

    public Typeface getHind_semiBold() {
        return hind_semiBold;
    }

    public Typeface getHind_bold() {
        return hind_bold;
    }

    public Typeface getHind_medium() {
        return hind_medium;
    }

    public Typeface getHind_light() {
        return hind_light;
    }

    public Typeface getDigital_clock() {
        return digital_clock;
    }
}
