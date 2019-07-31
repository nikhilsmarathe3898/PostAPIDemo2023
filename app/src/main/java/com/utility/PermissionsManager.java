package com.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * <h>PermissionsManager</h>
 * Created by Ali on 11/29/2017.
 */

public class PermissionsManager
{
    private static final String COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String WRITE_CALENDAR_PERMISSION = Manifest.permission.WRITE_CALENDAR;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private final int REQUEST_PERMISSIONS_CODE = 0;

    private PermissionsListener listener;

    public PermissionsManager() {
    }

    public PermissionsListener getListener() {
        return listener;
    }

    public void setListener(PermissionsListener listener) {
        this.listener = listener;
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean isCoarseLocationPermissionGranted(Context context) {
        return isPermissionGranted(context, COARSE_LOCATION_PERMISSION);
    }

    private static boolean isFineLocationPermissionGranted(Context context) {
        return isPermissionGranted(context, FINE_LOCATION_PERMISSION);
    }

    private static boolean isCameraPermissionGranted(Context mContext)
    {
        return isPermissionGranted(mContext,CAMERA_PERMISSION);
    }

   /* public boolean isFileReadPermissionGranted(Context mContext)
    {
        return isPermissionGranted(mContext,READ_EXTERNAL_STORAGE);
    }*/

    public boolean isFileWritePermissionGranted(Context mContext)
    {
        return isPermissionGranted(mContext,WRITE_EXTERNAL_STORAGE);
    }

    public boolean areFilePermissionGranted(Context mContext)
    {
        return isFileWritePermissionGranted(mContext);// && isFileReadPermissionGranted(mContext);

    }
    public boolean areCameraFilePermissionGranted(Context mContext)
    {
        return !areFilePermissionGranted(mContext) && !isCameraPermissionGranted(mContext);
    }

    public boolean isCalendarWritePermissionGranted(Context mContext)
    {
        return isPermissionGranted(mContext,WRITE_CALENDAR_PERMISSION);
    }

    public  boolean areLocationPermissionsGranted(Context context) {
        return !isCoarseLocationPermissionGranted(context)
                && !isFineLocationPermissionGranted(context);
    }

    public boolean areRuntimePermissionsRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public void requestCalendarPermission(Activity mActivity)
    {
        requestCalendarPermission(mActivity, true);
    }

    private void requestCalendarPermission(Activity activity,boolean requestCalendar)
    {
        String[] permissions = requestCalendar
                ? new String[] {CAMERA_PERMISSION}
                : new String[] {CAMERA_PERMISSION};
      /*  ArrayList<String> cameraPermission = new ArrayList<>();
        cameraPermission.add(CAMERA_PERMISSION);*/
        requestPermissions(activity,permissions);
    }
    public void requestLocationPermissions(Activity activity) {
        // Request fine location permissions by default
        requestLocationPermissions(activity, true);
    }

    private void requestLocationPermissions(Activity activity, boolean requestFineLocation) {
        String[] permissions = requestFineLocation
                ? new String[] {FINE_LOCATION_PERMISSION}
                : new String[] {COARSE_LOCATION_PERMISSION};
       /* ArrayList<String> locationPermissions = new ArrayList<>();
        locationPermissions.add(FINE_LOCATION_PERMISSION);
        locationPermissions.add(COARSE_LOCATION_PERMISSION);*/
        requestPermissions(activity, permissions);
    }
    public void requestCameraPermissions(Activity activity) {
        // Request fine location permissions by default
        requestCameraPermission(activity, true);
    }

    private void requestCameraPermission(Activity activity,boolean requestCamera)
    {
        String[] permissions = requestCamera
                ? new String[] {CAMERA_PERMISSION,WRITE_EXTERNAL_STORAGE}
                : new String[] {CAMERA_PERMISSION,WRITE_EXTERNAL_STORAGE};
      /*  ArrayList<String> cameraPermission = new ArrayList<>();
        cameraPermission.add(CAMERA_PERMISSION);*/
        requestPermissions(activity,permissions);
    }



    private void requestPermissions(Activity activity, String[] permissions) {
        ArrayList<String> permissionsToExplain = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                permissionsToExplain.add(permission);
            }
        }

        if (listener != null && permissionsToExplain.size() > 0) {
            // The developer should show an explanation to the user asynchronously
            listener.onExplanationNeeded(permissionsToExplain);
        }

        ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSIONS_CODE);
    }

    /**
     * You should call this method from your activity onRequestPermissionsResult.
     *
     * @param requestCode  The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either
     *                     PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                if (listener != null) {
                    boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    listener.onPermissionResult(granted);
                }
                break;
            default:
                // Ignored
        }
    }
}
