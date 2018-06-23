package com.heaven.task;

import com.heaven.task.Model.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Heaven on 20.07.2017.
 */

public interface ExampleInterface {
    @GET("tobtc")
    Call<Number> requestExample(
           // @Query("client_id") String client_id,
          //  @Query("client_secret") String client_secret,
          //  @Query("v") String v,
            @Query("currency") String queryCurrency,
            @Query("value") String queryValue);
}
