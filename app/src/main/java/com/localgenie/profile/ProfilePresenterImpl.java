package com.localgenie.profile;

import android.util.Log;

import com.localgenie.model.ProfileData;
import com.localgenie.model.ProfileResponse;
import com.localgenie.model.ServerResponse;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.pojo.EditProfileBody;
import com.utility.RefreshToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author Pramod
 * @since 4/12/2017.
 */

public class ProfilePresenterImpl implements ProfilePresenter {

    @Inject
    ProfileView profileView;

    @Inject
    LSPServices lspServices;

    @Inject
    SessionManagerImpl sessionManager;

    private CompositeDisposable compositeDisposable;

    private CompositeDisposable verDisposable;

    @Inject
    ProfilePresenterImpl(ProfileView profileView) {
        this.profileView = profileView;
        this.compositeDisposable=new CompositeDisposable();
        this.verDisposable=new CompositeDisposable();
    }

    @Override
    public void getProfile(String auth) {
        if (profileView != null) {
            profileView.showProgress("FETCH");
        }
        if (auth == null || "".equals(auth.trim())) {
            Log.e("SIDE_PROF", "Invalid Auth");
            profileView.hideProgress();
        } else {
            Log.e("SIDE_PROF", "SDP_Auth ::  " + auth);
        }
        Observable<Response<ProfileResponse>> request = lspServices.getProfile(auth,Constants.selLang);

        request.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ProfileResponse>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ProfileResponse> value) {
                        JSONObject errJsonD;
                        Log.e("SIDE_PROF", "Reqq URL :: " + value.raw().request().url());
                        Log.e("SIDE_PROF", "code :: " + value.code() + " msg " + value.message());
                        try
                        {
                            switch (value.code())
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    ProfileResponse profileResponse = value.body();
                                    Log.e("SIDE_PROF", "Success msg :: " + profileResponse.getMessage());

                                    ProfileData profileData = profileResponse.getData();
                                    if (profileData != null) {
                                        String firstName = profileData.getFirstName();

                                        String lastName = profileData.getLastName();


                                        String email = profileData.getEmail();

                                        String phoneNo = profileData.getPhone();
                                        String about = profileData.getAbout();
                                        String profilePic = profileData.getProfilePic();

                                        Log.e("SIDE_PROF", "Got vals ::  " + firstName + " \n\n last name  :: " + lastName + " \n\n email  :: " + email + " \n\n phone ::  " + phoneNo + " \n\n about :: " + about + "\n\n profile_pic :: " + profilePic);

                                        profileView.setProfileFirstName(firstName);
                                        profileView.setProfileLastName(lastName);
                                        profileView.setProfileEmail(email);
                                        profileView.setProfileMob(phoneNo);
                                        profileView.setAbout(about);
                                        profileView.setProfilePic(profilePic);
                                        profileView.setCountryCode(profileData.getCountryCode());

                                        sessionManager.setFirstName(firstName);
                                        sessionManager.setLastName(lastName);
                                        sessionManager.setEmail(email);
                                        sessionManager.setMobileNo(profileData.getPhone());
                                        sessionManager.setProfilePicUrl(profilePic);
                                        sessionManager.setAbout(about);
                                        sessionManager.setProfileCalled(true);
                                        sessionManager.setCountryCode(profileData.getCountryCode());

                                        //profileView.onLogout(email);
                                    }else
                                        profileView.hideProgress();

                                    break;
                                case Constants.SESSION_EXPIRED:
                                    errJsonD = new JSONObject(value.errorBody().string());

                                    RefreshToken.onRefreshToken(errJsonD.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                        @Override
                                        public void onSuccessRefreshToken(String newToken) {

                                            sessionManager.setAUTH(newToken);
                                            getProfile(newToken);
                                        }

                                        @Override
                                        public void onFailureRefreshToken() {

                                        }

                                        @Override
                                        public void sessionExpired(String msg) {
                                            profileView.onLogout(msg);
                                            profileView.hideProgress();
                                        }
                                    });
                                    break;
                                case Constants.SESSION_LOGOUT:
                                    profileView.hideProgress();
                                    errJsonD = new JSONObject(value.errorBody().string());
                                    profileView.onError(errJsonD.getString("message"));
                                    break;
                                default:
                                    errJsonD = new JSONObject(value.errorBody().string());
                                    profileView.hideProgress();
                                    profileView.onError(errJsonD.getString("message"));
                                    break;
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        profileView.hideProgress();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        profileView.hideProgress();
                    }
                });

    }

    @Override
    public void editProfile(String auth,String profile_pic,String first_name,String last_name,String about_me) {
        //LSPServices service = ServiceFactory.createRetrofitService(LSPServices.class);
        EditProfileBody body = new EditProfileBody();
        if(!"".equals(profile_pic))
        {
            body.setProfilePic(profile_pic);
            sessionManager.setProfilePicUrl(profile_pic);
        }
        if(!"".equals(first_name))
        {
            body.setFirstName(first_name);
            sessionManager.setFirstName(first_name);
        }
        if(!"".equals(last_name))
        {
            body.setLastName(last_name);
            sessionManager.setLastName(last_name);
        }
        if(!"".equals(about_me))
        {
            body.setAbout(about_me);
            sessionManager.setAbout(about_me);
        }
        Log.d("tag", "editProfile: "+body.getFirstName() +" laST "+body.getLastName()+" pROpIC "+body.getProfilePic()
                +" about "+body.getAbout());
        Observable<Response<ServerResponse>> request = lspServices.editProfile(auth,Constants.selLang,body);
        request.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ServerResponse>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        verDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ServerResponse> value) {
                        Log.e("EDIT", "Reqq URL :: " + value.raw().request().url());
                        Log.e("EDIT", "code :: " + value.code() + " msg " + value.message());
                        try {
                            if (200 == value.code()) {
                                ServerResponse profResponse = value.body();
                                if (profResponse != null) {
                                    Log.e("EDIT", "Success msg :: " + profResponse.getMessage());
                                    //profileView.editEnable(false);
                                    profileView.onSuccess(profResponse.getMessage());
                                } else {
                                    Log.e("EDIT", value.message());
                                }
                            } else if (Constants.SESSION_LOGOUT == value.code()) {
                                JSONObject errJsonER = new JSONObject(value.errorBody().string());

                                profileView.onLogout(errJsonER.getString("message"));
                                profileView.hideProgress();
                            } else if (Constants.SESSION_EXPIRED == value.code()) {
                                JSONObject errJsonD = new JSONObject(value.errorBody().string());
                                RefreshToken.onRefreshToken(errJsonD.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                    @Override
                                    public void onSuccessRefreshToken(String newToken) {

                                        sessionManager.setAUTH(newToken);
                                        editProfile(newToken, profile_pic, first_name, last_name, about_me);
                                    }

                                    @Override
                                    public void onFailureRefreshToken() {

                                    }

                                    @Override
                                    public void sessionExpired(String msg) {
                                        profileView.onLogout(msg);
                                        profileView.hideProgress();
                                    }
                                });
                            } else {
                                Log.e("EDIT", "Invalid response " + value.code() + "  msg ::  " + value.message());
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void doLogout(String auth) {
        //LSPServices service = ServiceFactory.createRetrofitService(LSPServices.class);
        Observable<Response<ServerResponse>> request = lspServices.logout(auth, Constants.selLang);
        if (profileView != null) {
            profileView.showProgress("LOGOUT");
        }
        request.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ServerResponse>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        verDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<ServerResponse> value)
                    {
                        if (200 == value.code()) {
                            //profileView.onSuccess();
                            if (value.body()!=null) {
                                profileView.hideProgress();

                                profileView.navToLogin();
                            }
                        } else if(498 == value.code()) {

                            try {
                                JSONObject errJsonD = new JSONObject(value.errorBody().string());
                                profileView.onLogout(errJsonD.getString("message"));
                                profileView.hideProgress();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }else if(440 == value.code())
                        {
                            try {
                                JSONObject errJsonD = new JSONObject(value.errorBody().string());
                                RefreshToken.onRefreshToken(errJsonD.getString("data"), lspServices, new RefreshToken.RefreshTokenImple() {
                                    @Override
                                    public void onSuccessRefreshToken(String newToken) {

                                        sessionManager.setAUTH(newToken);
                                        doLogout(newToken);
                                    }

                                    @Override
                                    public void onFailureRefreshToken() {

                                    }

                                    @Override
                                    public void sessionExpired(String msg) {
                                        profileView.onLogout(msg);
                                        profileView.hideProgress();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.e("LOGOUT","Invalid response "+value.code()+ "  msg ::  " + value.message());
                        }else{
                            profileView.hideProgress();
                        }


                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
