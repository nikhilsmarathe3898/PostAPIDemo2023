package com.localgenie.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Ali on 1/24/2018.
 */

public interface MainActivityContract
{
    interface MainPresenter
    {
       void onFragmentTransition(String TAG, FragmentManager fragmentManager, Fragment fragmentService
               , Fragment fragmentProject, Fragment fragmentProfile, Fragment chatFragment, int frameId);

    }

    interface MainServicePresenter
    {
        void onPermissionCheck();
    }
}
