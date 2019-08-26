package com.localgenie.Login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.localgenie.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 *<h>Facebook_login</h>
 * <P>
 *     Class contain a async task to get the data from facebook .
 *     Contains a method to do facebook login .
 *     Here doing facebook login and taking data as user_friends .
 * </P>
 * @author 3Embed
 * @since  4/02/2016
 */
public class FacebookLoginHelper
{
    private Activity mActivity;
    private boolean isReady=false;
    private String TAG = FacebookLoginHelper.class.getSimpleName();

    @Inject
    FacebookLoginHelper()
    {
    }

    public void initializeFacebookSdk(Activity loginActivity)
    {
        FacebookSdk.sdkInitialize(getApplicationContext());
        isReady = true;
        mActivity = loginActivity;
    }

    /**
     *<h2>facebookLogin</h2>
     * <P>
     *     method to do facebook login
     * </P>
     * @param callbackmanager:  facebook sdk interface
     * @param required_data_list: contains the fields which values need to be fetched from facebook
     * @param facebook_callback: interface to pass the facebook login response to call activity or fragment
     */
    public void facebookLogin(CallbackManager callbackmanager, final ArrayList<String> required_data_list, final Facebook_callback facebook_callback)
    {
        LoginManager.getInstance().logOut();
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            facebook_callback.error(response.getError().toString());
                        } else {
                            facebook_callback.success(object);
                            Log.d(TAG, "Success RepeatDaysCallback"+object.toString());
                        }
                    }
                });

                StringBuilder requiredData = new StringBuilder();
                int request_size = required_data_list.size();
                for (int count = 0; count < request_size; count++) {
                    requiredData.append(required_data_list.get(count));
                    if (request_size > 1 & count < request_size - 1) {
                        requiredData.append(",");
                    }
                }
                if (requiredData.toString().equals("")) {
                    facebook_callback.error(mActivity.getResources().getString(R.string.cant_cancel));
                } else {
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", requiredData.toString());
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }

            @Override
            public void onCancel() {
                facebook_callback.cancel(mActivity.getResources().getString(R.string.cancel));
            }

            @Override
            public void onError(FacebookException error) {
                facebook_callback.error(error.toString());
            }
        });
    }

    /**
     * <h2>createFacebook_requestData</h2>
     * <P>
     *   Creating facebook request data to which data you want to access from Facebook.
     * </P>
     */
    public ArrayList<String> createFacebook_requestData()
    {
        /*id,email,first_name,last_name,picture*/
        ArrayList<String> requestParameter= new ArrayList<>();
        requestParameter.add("id");
        requestParameter.add("email");
        requestParameter.add("first_name");
        requestParameter.add("last_name");
        requestParameter.add("picture");
        requestParameter.add("name");
        return requestParameter;
    }

    /**
     * <h2>refreshToken</h2>
     * <p>
     *     method to refresh the facebook token
     * </p>
     */
    public void refreshToken()
    {
        if(isReady)
        {
            LoginManager.getInstance().logOut();
            Log.d(TAG,"Logout"+"Token Refreshed");
            AccessToken.refreshCurrentAccessTokenAsync();
        }
    }

    /**
     * <h>Facebook_callback</h>
     * <P>
     *     RepeatDaysCallback interface of facebook.
     * </P>
     */
    public interface Facebook_callback
    {
        void success(JSONObject json);

        void error(String error);

        void cancel(String cancel);

    }
}