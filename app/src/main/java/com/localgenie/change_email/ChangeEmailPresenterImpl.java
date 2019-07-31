package com.localgenie.change_email;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.localgenie.model.ServerResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import org.json.JSONObject;
import javax.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author Pramod
 * @since  12-03-2018.
 */

public class ChangeEmailPresenterImpl implements ChangeEmailPresenter {

    @Inject
    ChangeEmailView changeEmailView;

    @Inject
    SessionManagerImpl manager;



    @Inject
    LSPServices lspServices;


    @Inject
    ChangeEmailPresenterImpl() {

    }

    @Override
    public boolean validateEmail(String email) {
        return email == null || email.length() == 0 || "".equals(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() && !Patterns.DOMAIN_NAME.matcher(email).matches();
    }

    @Override
    public boolean validatePhone(String phone) {
        return TextUtils.isEmpty(phone) || "".equals(phone) || !Patterns.PHONE.matcher(phone).matches();
    }


    /**
     * <h2>changeEmail</h2>
     *     <p>This method is used to change the email id of the user.
     *     API CALL for changing the email address of a user.</p>
     * @param emailId emailId to be changed.
     */
    @Override
    public void changeEmail(final String emailId) {
        //userType :- 1 - slave, 2 - master
        Observable<Response<ServerResponse>> response = lspServices.changeEmail(manager.getAUTH(), Constants.selLang,"1", emailId);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ServerResponse>>() {

                    @Override
                    public void onNext(Response<ServerResponse> value) {
                        switch (value.code()) {
                            case 200:
                                Log.e("CHG_EMAIL", "Value : " + value.body().getMessage());
                                changeEmailView.setSuccessEmail();
                                changeEmailView.navToProfile();
                               // manager.setEmail(emailId);
                                break;
                            default:
                                try {
                                    if (value.errorBody() != null) {
                                        JSONObject errJson = new JSONObject(value.errorBody().string());
                                        changeEmailView.setError(errJson.getString("message"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        //compositeDisposable.dispose();
                    }
                });
    }


    /**
     * <h2>changeMobile</h2>
     *     <p>This method is used to change the mobile number of the user.
     *     API CALL for changing the mobile number of a user.</p>
     * @param countryCode countryCode of mobile number to be changed.
     * @param mobile mobile number to be changed.
     */
    @Override
    public void changeMobile(String countryCode, String mobile) {
        //userType :- 1 - slave, 2 - master
        Observable<Response<ServerResponse>> response = lspServices.changePhoneNo(manager.getAUTH(),Constants.selLang, 1, countryCode, mobile);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ServerResponse>>() {


                    @Override
                    public void onNext(Response<ServerResponse> value) {
                        switch (value.code()) {
                            case 200:
                                Log.e("CHG_PHONE", "Value : " + value.body().getMessage());
                                //changeEmailView.setError(value.body().getMessage());
                                changeEmailView.navToOTP();

                                break;
                            default:
                                try {
                                    if (value.errorBody() != null) {
                                        JSONObject errJson = new JSONObject(value.errorBody().string());
                                        changeEmailView.setError(errJson.getString("message"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        //verDisposable.dispose();
                    }
                });
    }
}
