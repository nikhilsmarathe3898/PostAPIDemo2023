package com.localgenie.networking;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import io.reactivex.Observable;

/**
 * @author Pramod
 * @since 15 Jan-2018
 */

public interface ChatApiService {

 /*   @GET ("/chatHistory/{bookingId}/{pageNo}")
    Observable<Response<ResponseBody>> getChatHistory(@Header("authorization")String auth,
                                                @Header("lan")int lang,
                                                @Path("bookingId")long bookingId,
                                                @Path("pageNo")int pageNo);

    @FormUrlEncoded
    @POST("/message")
    Observable<Response<ResponseBody>> postMessage(@Header("authorization")String auth,
                                                   @Header("lan")int lang,
                                                   @Field("type")int type,
                                                   @Field("timestamp")long timeStamp,
                                                   @Field("content")String msgContent,
                                                   @Field("fromID")String customerId,
                                                   @Field("bid")String bid,
                                                   @Field("targetId")String proId);

    @Multipart
    @POST("/upload")
    Call<ResponseBody> upload(
            @Part("bookingId") RequestBody description,
            @Part MultipartBody.Part file);*/


    @GET("/getCityList")
    Observable<Response<ResponseBody>> getCty();


    @FormUrlEncoded
    @POST("/call")
    Observable<Response<ResponseBody>> initCall(@Header("authorization")String auth,
                                                @Header("lan")String lang,
                                                @Field("type") String type,
                                                @Field("room") String room,
                                                @Field("to") String to,
                                                @Field("bookingId") String bookingId);
    @GET("/call/{callId}")
    Observable<Response<ResponseBody>> checkIsAvailable(@Header("authorization")String auth,
                                                        @Header("lan")String lang,
                                                        @Path("callId") String callId);

    @PUT("/call/{callId}")
    Observable<Response<ResponseBody>> callAnswer(@Header("authorization")String auth,
                                                  @Header("lan")String lang,
                                                  @Path("callId") String callId);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/call",hasBody = true)
    Observable<Response<ResponseBody>> endCall(@Header("authorization")String auth,
                                               @Header("lan")String lang,
                                               @Field("callId") String callId,
                                               @Field("callFrom") String callFrom);

}
