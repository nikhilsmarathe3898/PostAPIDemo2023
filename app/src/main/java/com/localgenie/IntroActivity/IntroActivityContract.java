package com.localgenie.IntroActivity;

import com.pojo.LanguageResponse;

import java.util.ArrayList; /**
 * @author Pramod
 * @since  16/11/17.
 */

public interface IntroActivityContract {
    interface IntroView{

        /**
         * <h2>showProgress</h2>
         * This method is used to show progress bar on call of APIS
         */
        void showProgress();


        /**
         * <h2>hideProgress</h2>
         * This method is used to hide progress bar after the call of API success
         */
        void hideProgress();


        /**
         * <h2>loginSuccess</h2>
         * <p>This method is called on success of the guest login API and used to Navigate to HomeScreen</p>
         * @param auth Auth token
         * @param
         */
        void loginSuccess(String auth, String device_id);


        /**
         * <h2>onError</h2>
         * This method is used to show the error message if the Login API call fails and gives error from server
         * @param msg Message to show via TOAST
         */
        void onError(String msg);

        void setLanguage(String langName, boolean b);

        void showLanguagesDialog(int i, ArrayList<LanguageResponse.LanguagesLists> languagesLists);
    }
    interface IntroPresenter{
        /**
         * <h2>loginSuccess</h2>
         * <p>This method is called on success of the guest login API and used to Navigate to HomeScreen</p>
         */
        void doGuestLogin();

        void onLanguageCalled();

        void changeLanguage(String langCode, String langName, int direction);
    }
}
