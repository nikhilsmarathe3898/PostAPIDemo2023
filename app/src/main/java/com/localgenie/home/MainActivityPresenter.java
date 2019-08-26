package com.localgenie.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.localgenie.Dagger2.ActivityScoped;

import javax.inject.Inject;

/**
 * <h>MainActivityPresenter</h>
 * Created by Ali on 1/24/2018.
 */

@ActivityScoped
public class MainActivityPresenter implements MainActivityContract.MainPresenter
{

    @Inject
    MainActivityPresenter() {
    }

    @Override
    public void onFragmentTransition(String TAG, FragmentManager fragmentManager, Fragment fragmentService, Fragment fragmentProject, Fragment fragmentProfile,Fragment chatFragment,int frameId)
    {
        FragmentTransaction fTransaction = fragmentManager.beginTransaction();
        switch (TAG) {
            case "HOME":

                if (fragmentProject.isAdded())
                    fTransaction.hide(fragmentProject);
                if (fragmentProfile.isAdded())
                    fTransaction.hide(fragmentProfile);
                if(chatFragment.isAdded())
                    fTransaction.hide(chatFragment);
                if (fragmentService.isAdded()) {
                    fTransaction.show(fragmentService);
                } else {
                    fTransaction.add(frameId, fragmentService, TAG);
                }
                break;

            case "PROJECT":

                if (fragmentService.isAdded())
                    fTransaction.hide(fragmentService);
                if (fragmentProfile.isAdded())
                    fTransaction.hide(fragmentProfile);
                if(chatFragment.isAdded())
                    fTransaction.hide(chatFragment);
                if (fragmentProject.isAdded()) {
                    fTransaction.show(fragmentProject);
                } else {
                    fTransaction.add(frameId, fragmentProject, TAG);
                }
                break;

            case "PROFILE":

                if (fragmentProject.isAdded())
                    fTransaction.hide(fragmentProject);
                if (fragmentService.isAdded())
                    fTransaction.hide(fragmentService);
                if(chatFragment.isAdded())
                    fTransaction.hide(chatFragment);
                if (fragmentProfile.isAdded()) {
                    fTransaction.show(fragmentProfile);

                } else {
                    fTransaction.add(frameId, fragmentProfile, TAG);
                }
                break;
            case "CHAT":
                if (fragmentProject.isAdded())
                    fTransaction.hide(fragmentProject);
                if (fragmentService.isAdded())
                    fTransaction.hide(fragmentService);
                if(fragmentProfile.isAdded())
                    fTransaction.hide(fragmentProfile);
                if (chatFragment.isAdded()) {
                    fTransaction.show(chatFragment);
                } else {
                    fTransaction.add(frameId, chatFragment, TAG);
                }
                break;
        }
        fTransaction.commit();
    }

}
