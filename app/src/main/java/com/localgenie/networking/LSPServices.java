package com.localgenie.networking;

import com.localgenie.model.ForgotPwdReq;
import com.localgenie.model.ForgotPwdResponse;
import com.localgenie.model.ProfileResponse;
import com.localgenie.model.ServerOtpResponse;
import com.localgenie.model.ServerResponse;
import com.localgenie.model.SignUpReq;
import com.localgenie.model.SignUpResponse;
import com.localgenie.model.ValidationReq;
import com.localgenie.model.card.DeleteCard;
import com.localgenie.model.faq.FAQResponse;
import com.localgenie.model.guest_login.GuestLoginResponse;
import com.localgenie.model.payment_method.CardGetResponse;
import com.localgenie.utilities.UploadAmazonS3;
import com.pojo.EditProfileBody;

import org.json.JSONArray;

import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Pramod
 * @since 15 Dec-2017
 */

public interface LSPServices {

    //@Header("lan") String lang
    //Guest Login API
    @FormUrlEncoded
    @POST("/customer/guestSignIn")
    Observable<Response<GuestLoginResponse>> doGuestLogin(@Header("lan") String lang,
                                                          @Field("deviceId") String deviceId,
                                                          @Field("appVersion") String appVersion,
                                                          @Field("devMake") String devMake,
                                                          @Field("devModel") String devModel,
                                                          @Field("devType") Integer devType,
                                                          @Field("deviceTime") String deviceTime,
                                                          @Field("deviceOsVersion") String deviceOsVersion,
                                                          @Field("ipAddress") String ipAddress);

    //@Header("lan") String lang,
    //LoginResponse
    @FormUrlEncoded
    @POST("/customer/signIn")
    Observable<Response<ResponseBody>> performLogin(@Header("lan") String lang,
                                                    @Field("emailOrPhone") String emailOrPhone,
                                                    @Field("password") String password,
                                                    @Field("deviceId") String deviceId,
                                                    @Field("pushToken") String pushToken,
                                                    @Field("appVersion") String appVersion,
                                                    @Field("devMake") String devMake,
                                                    @Field("devModel") String devModel,
                                                    @Field("devType") Integer devType,
                                                    @Field("deviceTime") String deviceTime,
                                                    @Field("loginType") Integer loginType,
                                                    @Field("deviceOsVersion") String deviceOsVersion,
                                                    @Field("facebookId") String facebookId,
                                                    @Field("googleId") String googleId,
                                                    @Field("latitude") Double latitude,
                                                    @Field("longitude") Double longitude);


    @POST("/customer/logout")
    Observable<Response<ServerResponse>> logout(@Header("authorization") String authorization,@Header("lan") String lang
    );


    @GET("/customer/profile/me")
    Observable<Response<ProfileResponse>> getProfile(@Header("authorization") String authorization,@Header("lan") String lang);


    @PATCH("/customer/profile/me")
    Observable<Response<ServerResponse>> editProfile(@Header("authorization") String authorization,
                                                     @Header("lan") String lang,
                                                     @Body EditProfileBody editProfileBody);

    //  @GET("/customer/categories")
    @GET ("/customer/categories/{lat}/{long}/{ipAddress}")
    Observable<Response<ResponseBody>> getCategories(@Header("authorization") String authorization,
                                                     @Header("lan") String lang,
                                                     @Path("lat") double lat, @Path("long") Double longitude,
                                                     @Path("ipAddress") String ipAddress);


    @FormUrlEncoded
    @PATCH("/customer/password")
    Observable<Response<ServerResponse>> changePassword(@Header("lan") String lang,@Field("password") String password,
                                                        @Field("userId") String userId,
                                                        @Field("userType") Integer userType);

    @FormUrlEncoded
    @PATCH("/customer/password/me")
    Observable<Response<ServerResponse>> profChangePwd(@Header("authorization") String authorization,
                                                       @Header("lan") String lang,
                                                       @Field("oldPassword") String oldPassword,
                                                       @Field("newPassword") String newPassword);

    @POST("/customer/registerUser")
    Observable<Response<SignUpResponse>> doRegister(@Header("lan") String lang,@Body SignUpReq signUpReq);

    @POST("/customer/emailValidation")
    Observable<Response<ServerResponse>> checkEmailExists(@Header("lan") String lang,@Body ValidationReq validationReq);

    @FormUrlEncoded
    @POST("/customer/phoneNumberValidation")
    Observable<Response<ServerResponse>> checkPhoneExists(@Header("lan") String lang,
                                                          @Field("countryCode") String countryCode,
                                                          @Field("phone") String phone);

    @POST("/customer/forgotPassword")
    Observable<Response<ForgotPwdResponse>> forgotPassword(@Header("lan") String lang,
                                                           @Body ForgotPwdReq forgotPwdReq);


    @FormUrlEncoded
    @POST("/customer/verifyPhoneNumber")
    Observable<Response<ServerOtpResponse>> verifyPhone(@Header("lan") String lang,
                                                        @Field("code") String code,
                                                        @Field("userId") String userId);

    @FormUrlEncoded
    @POST("/customer/verifyVerificationCode")
    Observable<Response<ServerResponse>> verifyOtp(@Header("lan") String lang,
                                                   @Field("code") String code,
                                                   @Field("userId") String userId,
                                                   @Field("trigger") Integer trigger,
                                                   @Field("userType") Integer userType);

    //Change Email API
    @FormUrlEncoded
    @PATCH("/customer/email")
    Observable<Response<ServerResponse>> changeEmail(@Header("authorization") String authorization,
                                                     @Header("lan") String lang,
                                                     @Field("userType") String userType,
                                                     @Field("email") String email);

    //Change phone number API
    @FormUrlEncoded
    @PATCH("/customer/phoneNumber")
    Observable<Response<ServerResponse>> changePhoneNo(@Header("authorization") String authorization,
                                                       @Header("lan") String lang,
                                                       @Field("userType") Integer userType,
                                                       @Field("countryCode") String countryCode,
                                                       @Field("phone") String phone);


    @FormUrlEncoded
    @POST("/customer/resendOtp")
    Observable<Response<ServerResponse>> resendOtp(@Header("lan") String lang,
                                                   @Field("userId") String userId,
                                                   @Field("userType") Integer userType,
                                                   @Field("trigger") Integer trigger);


    @GET("/card")
    Observable<Response<CardGetResponse>> getCard(@Header("authorization") String authorization,
                                                  @Header("lan") String lang);

    @FormUrlEncoded
    @POST("/card")
    Observable<Response<ServerResponse>> addCard(@Header("authorization") String authorization,
                                                 @Header("lan") String lang,
                                                 @Field("email") String email,
                                                 @Field("cardToken") String cardToken);

    @FormUrlEncoded
    @PATCH("/card")
    Observable<Response<ServerResponse>> makeDefaultCard(@Header("authorization") String authorization,
                                                         @Header("lan") String lang,
                                                         @Field("cardId") String cardId);

    @HTTP(method = "DELETE", path = "/card", hasBody = true)
    Observable<Response<ServerResponse>> deleteCard(@Header("authorization") String authorization,
                                                    @Header("lan") String lang,
                                                    @Body DeleteCard deleteCard);


    //ServerResponse
    @FormUrlEncoded
    @POST("/customer/location")
    Observable<Response<ResponseBody>> getLocation(@Header("authorization") String authorization,
                                                   @Header("lan") String selLang,
                                                   @Field("lat") double lat,
                                                   @Field("long") double longitude,
                                                   @Field("categoryId") String catId,
                                                   @Field("subCategoryId") String subCatId,
                                                   @Field("distance") double distance,
                                                   @Field("minPrice") double minPrice,
                                                   @Field("maxPrice") double maxPrice,
                                                   @Field("bookingType") int bookingType,
                                                   @Field("scheduleDate") String scheduleDate,
                                                   @Field("scheduleTime") int scheduleTime,
                                                   @Field("endTimeStamp") long endTimeStamp,
                                                   @Field("days") ArrayList<String> days,
                                                   @Field("deviceTime") String deviceTime,
                                                   @Field("ipAddress") String ipAddress,
                                                   @Field("callType") int callType);


    // GET /customer/provider/{lat}/{long}/{categoryId}/{ipAddress}

    //@GET("/customer/provider/{lat}/{long}/{categoryId}/{ipAddress}")
    @FormUrlEncoded
    @POST ("/customer/provider")
    Observable<Response<ResponseBody>> getProviders(@Header("authorization") String authorization,
                                                    @Header("lan") String selLang,
                                                    @Field("lat") double lat,
                                                    @Field("long") double longitude,
                                                    @Field("categoryId") String catId,
                                                    @Field("subCategoryId") String subCatId,
                                                    @Field("distance") double distance,
                                                    @Field("minPrice") double minPrice,
                                                    @Field("maxPrice") double maxPrice,
                                                    @Field("bookingType") int bookingType,
                                                    @Field("scheduleDate") String scheduleDate,
                                                    @Field("scheduleTime") int scheduleTime,
                                                    @Field("endTimeStamp") long endTimeStamp,
                                                    @Field("days") ArrayList<String> days,
                                                    @Field("deviceTime") String deviceTime,
                                                    @Field("ipAddress") String ipAddress,
                                                    @Field("callType") int callType);


    @GET("/customer/address")
    Observable<Response<ResponseBody>> getAddress(@Header("authorization") String authorization,
                                                  @Header("lan") String lan);

    @FormUrlEncoded
    @POST("/customer/address")
    Observable<Response<ServerResponse>> addAddress(@Header("authorization") String authorization,
                                                    @Field("addLine1") String addLine1,
                                                    @Field("addLine2") String addLine2,
                                                    @Field("houseNo") String houseNo,
                                                    @Field("name") String name,
                                                    @Field("city") String city,
                                                    @Field("state") String state,
                                                    @Field("country") String country,
                                                    @Field("placeId") String placeId,
                                                    @Field("pincode") String pincode,
                                                    @Field("latitude") Double latitude,
                                                    @Field("longitude") Double longitude,
                                                    @Field("taggedAs") String taggedAs,
                                                    @Field("userType") Integer userType);

    @FormUrlEncoded
    @PATCH("/customer/address")
    Observable<Response<ResponseBody>> editAddress(@Header("authorization") String authorization,
                                                   @Header("lan") String lngId,
                                                   @Field("id") String id,
                                                   @Field("houseNo") String houseNo,
                                                   @Field("name") String name,
                                                   @Field("addLine1") String addLine1,
                                                   @Field("addLine2") String addLine2,
                                                   @Field("city") String city,
                                                   @Field("state") String state,
                                                   @Field("country") String country,
                                                   @Field("placeId") String placeId,
                                                   @Field("pincode") String pincode,
                                                   @Field("latitude") Double latitude,
                                                   @Field("longitude") Double longitude,
                                                   @Field("taggedAs") String taggedAs,
                                                   @Field("userType") Integer userType);

    @DELETE("/customer/address/{id}")
    Observable<Response<ServerResponse>> deleteAddress(@Header("authorization") String authorization,
                                                       @Header("lan") String lang,
                                                       @Path("id") String id);



    @GET("/customer/support/{userType}")
    Observable<Response<FAQResponse>> getFAQ(@Header("lan") String lanId,
                                             @Path("userType") Integer userType);


    //GET /customer/providerDetails/{providerId}/{categoryId}/{lat}/{long}
    @GET("/customer/providerDetails/{providerId}/{categoryId}/{lat}/{long}/{callType}")
    Observable<Response<ResponseBody>> getProviderDetails(@Header("authorization") String authorization,
                                                          @Header("lan") String lanId,
                                                          @Path("providerId") String proId,
                                                          @Path("categoryId") String CatId,
                                                          @Path("lat") double lat,
                                                          @Path("long") double lng,
                                                          @Path("callType") int callType);
    //customer/accessToken
    @GET("/customer/accessToken")
    Observable<Response<ResponseBody>> getAccessToken(@Header("authorization") String authorization,
                                                      @Header("lan") String lanId);

    //GET /customer/services/{categoryId}/{providerId}


    @GET("/customer/services/{categoryId}/{providerId}")
    Observable<Response<ResponseBody>> getSubServices(@Header("authorization") String authorization,
                                                      @Header("lan") String lanId,
                                                      @Path("categoryId") String catId,
                                                      @Path("providerId") String proId);

    //http://45.77.190.140:9999/customer/cart
    @FormUrlEncoded
    @POST("/customer/cart")
    Observable<Response<ResponseBody>> onCatModification(@Header("authorization") String authorization,
                                                         @Header("lan") String lanId,
                                                         @Field("serviceType") int serviceType,
                                                         @Field("bookingType") int bookingType,
                                                         @Field("categoryId") String catId,
                                                         @Field("serviceId") String serviceId,
                                                         @Field("quntity") int quantity,
                                                         @Field("action") int actionCount,
                                                         @Field("providerId") String proId,
                                                         @Field("callType") int callType);

    //GET /customer/cart/{categoryId}/{providerId}
    @GET("/customer/cart/{categoryId}/{providerId}/{callType}/{bookingType}")
    Observable<Response<ResponseBody>> getSubCart(@Header("authorization") String authorization,
                                                  @Header("lan") String lanId,
                                                  @Path("categoryId") String catId,
                                                  @Path("providerId") String proId,
                                                  @Path("callType")int callType,
                                                  @Path("bookingType")int bookingType);

    @FormUrlEncoded
    @POST("/customer/booking")
    Observable<Response<ResponseBody>> onLiveBooking(@Header("authorization") String authorization,
                                                     @Header("lan") String lng,
                                                     @Field("callType") int callType,
                                                     @Field("bookingModel") int bookingModel,
                                                     @Field("bookingType") int bookingType,
                                                     @Field("paymentMethod") int paymentType,
                                                     @Field("paidByWallet") int paidByWallet,
                                                     @Field("placeName") String plaessLine,
                                                     @Field("addLine2") String addressLine2,
                                                     @Field("addLine1") String addressLine1,
                                                     @Field("latitude") double lat,
                                                     @Field("longitude") double lang,
                                                     @Field("providerId") String proId,
                                                     @Field("categoryId") String catId,
                                                     @Field("cartId") String cartId,
                                                     @Field("jobDescription") String jobDesc,
                                                     @Field("promoCode") String promoCode,
                                                     @Field("paymentCardId") String cardId,
                                                     @Field("bookingDate") String bookingDate,
                                                     @Field("scheduleTime") int scheduledTime,
                                                     @Field("endTimeStamp") long endTime,
                                                     @Field("days") ArrayList<String> days,
                                                     @Field("deviceTime") String deviceTime,
                                                     @Field("bidPrice") double bidPrice,
                                                     @Field("questionAndAnswer") JSONArray questions);
    @FormUrlEncoded
    @PATCH("/customer/responseBooking")
    Observable<Response<ResponseBody>> onBookingHire(@Header("authorization") String authorization,
                                                     @Header("lan") String lng,
                                                     @Field("bookingId") long bid,
                                                     @Field("providerId") String proId);


    @GET("/customer/bookings")
    Observable<Response<ResponseBody>> onToGetAllBookings(@Header("authorization") String authorization,
                                                          @Header("lan") String lng);
    @GET("/customer/bookings/{from}/{to}")
    Observable<Response<ResponseBody>> onToGetAllBookingsUpComing(@Header("authorization") String authorization,
                                                                  @Header("lan") String lng,
                                                                  @Path("from") long fromDate,
                                                                  @Path("to") long toDate);

    @GET("/customer/booking/{bookingId}")
    Observable<Response<ResponseBody>> onToGetBookingDetails(@Header("authorization") String authorization,
                                                             @Header("lan") String lng,
                                                             @Path("bookingId") long bid);

    @GET("/customer/booking/invoice/{bookingId}")
    Observable<Response<ResponseBody>> onToGetInvoiceDetails(@Header("authorization") String authorization,
                                                             @Header("lan") String lng,
                                                             @Path("bookingId") long bid);


    @FormUrlEncoded
    @POST("/customer/reviewAndRating")
    Observable<Response<ResponseBody>> onUpdateReview(@Header("authorization") String authorization,
                                                      @Header("lan") String lng,
                                                      @Field("bookingId") long bid,
                                                      @Field("rating") JSONArray rating,
                                                      @Field("review") String reviewMsg);

    @GET ("/customer/reviewAndRatingPending")
    Observable<Response<ResponseBody>> onTOGetPendingBooking(@Header("authorization") String authorization,
                                                             @Header("lan") String lng);


    @GET ("/customer/bookingPaymentPending")
    Observable<Response<ResponseBody>> onTOGetPendingInvoiceBooking(@Header("authorization") String authorization,
                                                             @Header("lan") String lng);


    //GET /zendesk/user/ticket/{emailId}
    @GET("/zendesk/user/ticket/{emailId}")
    Observable<Response<ResponseBody>> onToGetZendeskTicket(@Header("authorization") String authorization,
                                                            @Header("lan") String lng,
                                                            @Path("emailId") String emailId);
    @FormUrlEncoded
    @PUT("/zendesk/ticket/comments")
    Observable<Response<ResponseBody>> commentOnTicket(@Header("authorization") String authorization,
                                                       @Header("lan") String lng,
                                                       @Field("id") int zenId,
                                                       @Field("body") String body,
                                                       @Field("author_id") String authorId);

    @GET("/zendesk/ticket/history/{id}")
    Observable<Response<ResponseBody>> onToGetZendeskHistory(@Header("authorization") String authorization,
                                                             @Header("lan") String lng,
                                                             @Path("id") int zenId);

    @FormUrlEncoded
    @POST("/zendesk/ticket")
    Observable<Response<ResponseBody>>createTicket(@Header("authorization") String authorization,
                                                   @Header("lan") String lng,
                                                   @Field("subject") String subject,
                                                   @Field("body") String body,
                                                   @Field("status") String status,
                                                   @Field("priority") String priority,
                                                   @Field("type") String type,
                                                   @Field("requester_id") String requesterId);



    @GET ("/chatHistory/{bookingId}/{providerId}/{pageNo}")
    Observable<Response<ResponseBody>> getChatHistory(@Header("authorization") String auth,
                                                      @Header("lan") String lang,
                                                      @Path("bookingId") long bookingId,
                                                      @Path("providerId") String proId,
                                                      @Path("pageNo") int pageNo);


    @FormUrlEncoded
    @POST("/message")
    Observable<Response<ResponseBody>> postMessage(@Header("authorization") String auth,
                                                   @Header("lan") String lang,
                                                   @Field("type") int type,
                                                   @Field("timestamp") long timeStamp,
                                                   @Field("content") String msgContent,
                                                   @Field("fromID") String customerId,
                                                   @Field("bid") String bid,
                                                   @Field("targetId") String proId);



    @Multipart
    @POST("/uploadImage")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part file);// @Part("bookingId") RequestBody description,



    @GET("/customer/cancelReasons/{bookingId}")
    Observable<Response<ResponseBody>>onCancelReasons(@Header("authorization") String authorization,
                                                      @Header("lan") String lan,
                                                      @Path("bookingId") String userType);

    // PATCH /customer/cancelBooking
    @FormUrlEncoded
    @PATCH("/customer/cancelBooking")
    Observable<Response<ResponseBody>>cancelBooking(@Header("authorization") String authorization,
                                                    @Header("lan") String lng,
                                                    @Field("bookingId") long bid,
                                                    @Field("resonId") int reasonId);

    @GET("/customer/lastDues")
    Observable<Response<ResponseBody>>lastDues(@Header("authorization") String authorization,
                                               @Header("lan") String lng);

    @GET("/customer/wallet")
    Observable<Response<ResponseBody>>getWalletLimits(@Header("authorization") String authorization,
                                                      @Header("lan") String lng);

    @FormUrlEncoded
    @POST("/customer/wallet/recharge")
    Observable<Response<ResponseBody>>rechargeWallet(@Header("authorization") String authorization,
                                                     @Header("lan") String lng,
                                                     @Field("cardId") String cardId,
                                                     @Field("amount") String amount);

    @GET("/customer/wallet/transction/{pageIndex}")
    Observable<Response<ResponseBody>>getWalletTransaction(@Header("authorization") String authorization,
                                                           @Header("lan") String lng,
                                                           @Path("pageIndex") int pageIndex);


    @GET("/customer/providerReview/{providerId}/{categoryId}/{pageNo}")
    Observable<Response<ResponseBody>> getReviews(@Header("authorization") String authorization,
                                                  @Header("lan") String lng,
                                                  @Path("providerId") String proId,
                                                  @Path("categoryId") String catId,
                                                  @Path("pageNo") int page);

    @GET("/server/serverTime")
    Observable<Response<ResponseBody>> onTogetServerTime(@Header("lan") String selLang);

    @FormUrlEncoded
    @POST ("/customer/promoCodeValidation")
    Observable<Response<ResponseBody>> postPromoCodeValidation(@Header("authorization") String authorization,
                                                               @Header("lan") String lang,
                                                               @Field("latitude") double lat,
                                                               @Field("longitude") double lng,
                                                               @Field("cartId") String cartId,
                                                               @Field("paymentMethod") int payment,
                                                               @Field("couponCode") String couponCode);
    @GET("/customer/promoCode/{lat}/{long}")
    Observable<Response<ResponseBody>>getPromoCode(@Header("authorization") String authorization,
                                                   @Header("lan") String lang,
                                                   @Path("lat") double lat,
                                                   @Path("long") double lng);

    @FormUrlEncoded
    @POST("/customer/referralCodeValidation")
    Observable<Response<ResponseBody>> onReferralCodeCheck(@Header("lan") String lang,
                                                           @Field("referralCode") String referralCode);
    @GET("/customer/language")
    Observable<Response<ResponseBody>>onLanguageCalled(@Header("lan") String lang);

    @GET("/customer/referralCode")
    Observable<Response<ResponseBody>> getReferralCode(@Header("authorization") String authorization,
                                                       @Header("lan") String lang);

    @GET("/customer/booking/chat")
    Observable<Response<ResponseBody>> getBookingChat(@Header("authorization") String authorization,
                                                      @Header("lan") String lang);

    @FormUrlEncoded
    @POST("/customer/favouriteProvider")
    Observable<Response<ResponseBody>> addTOFav(@Header("authorization") String authorization,
                                                @Header("lan") String lang,
                                                @Field("categoryId") String catId,
                                                @Field("providerId") String providerId);

    @DELETE("/customer/favouriteProvider/{providerId}/{categoryId}")
    Observable<Response<ResponseBody>>removeFromFav(@Header("authorization") String authorization,
                                                    @Header("lan") String lang,
                                                    @Path("categoryId") String catId,
                                                    @Path("providerId") String providerId);

    @PATCH("/customer/reminderBooking")
    Observable<Response<ResponseBody>>reminderEvent(@Header("authorization") String authorization,
                                                    @Header("lan") String lang,
                                                    @Field("bookingId") long bid,
                                                    @Field("reminderId") int reasonId);
    //Invoice raising for telecalling
    @FormUrlEncoded
    @PATCH ("/customer/bookingStatus")
    Observable<Response<ResponseBody>> bookingstatus(@Header("authorization") String authorization,
                                                               @Header("lan") String lang,
                                                               @Field("bookingId") long bid,
                                                               @Field("signatureUrl") String signatureUrl,
                                                               @Field("additionalService") String[] additionalService);


/*
    @GET ("/customer/booking/invoice/{bookingId}")
    Observable<Response<ResponseBody>> bookingInvoice(@Header("authorization") String authorization,
                                                     @Header("lan") String lang,
                                                     @Path("bookingId") long bid);
*/


    @GET ("/customer/favouriteProvider")
    Observable<Response<ResponseBody>>onToGetFavProvider(@Header("authorization") String authorization,
                                                         @Header("lan") String lang);
    @GET("/server/appVersion/{userType}")
    Observable<Response<ResponseBody>> onTOGetAppVersion(@Header("lan") String lang,
                                                         @Path("userType") int userType);

    @PATCH("/server/call")
    Observable<Response<ResponseBody>>telCallAnsDec(@Header("authorization") String authorization,
                                                    @Header("lan") String lang,
                                                    @Field("callId") String callId,
                                                    @Field("callStatus") int callStatus);

    @GET("/server/call")
    Observable<Response<ResponseBody>> getCallDetails(@Header("authorization") String authorization,
                                                      @Header("lan") String lang);

    @FormUrlEncoded
    @POST("/server/call")
    Observable<Response<ResponseBody>> onMakeCall(@Header("authorization") String authorization,
                                                  @Header("lan") String lang,
                                                  @Field("callType") String callType,
                                                  @Field("targetId") String callerId,
                                                  @Field("bookingId")String callId);

    @GET ("/customer/providerSlot/{providerId}/{categoryId}/{date}/{callType}")
    Observable<Response<ResponseBody>> getSlotsTimes(@Header("authorization") String authorization,
                                                     @Header("lan") String lang,
                                                     @Path("providerId") String proId,
                                                     @Path("categoryId") String catId,
                                                     @Path("date")long date,
                                                     @Path("callType") int callType);


    @GET("/customer/schedule")
    Observable<Response<ResponseBody>> getSchdeuleSlots(@Header("authorization") String authorization,
                                                     @Header("lan") String lang,
                                                     @Query("month") String month,
                                                        @Query("providerId") String providerId);

    @FormUrlEncoded
    @PATCH("/customer/booking/bidAccept")
    Observable<Response<ResponseBody>>acceptBidBooking(@Header("authorization") String authorization,
                                                    @Header("lan") String lng,
                                                    @Field("bookingId") long bid,
                                                    @Field("status") int status);
}
