package com.localgenie.home;

/**
 * Created by Ali on 1/29/2018.
 */

public interface BaseView<T>
{
    void onSessionExpired();
    void onLogout(String message);
    void onError(String error);
    void onShowProgress();
    void onHideProgress();

}
