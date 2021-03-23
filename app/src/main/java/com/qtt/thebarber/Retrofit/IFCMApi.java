package com.qtt.thebarber.Retrofit;

import com.qtt.thebarber.Model.FCMResponse;
import com.qtt.thebarber.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    //copy from staff
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAVn_44l8:APA91bG2OqUbz6UR_k9alEpuGJXNN0A2_-1gyDAJhJ8-vIWoprqiXMZ_MAGMUqxLpiHUDneJXI89Mbiqf3uO9qN7Z80ylOpt5EuDr6mWcegkMD1Nq_PxTxSM3xCjnvQD8CVAtz9B9pPv"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
