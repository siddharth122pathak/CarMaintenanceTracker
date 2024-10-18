package com.example.carmaintenancetracker;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;


public interface FortuneApi {

    // GET request to check the oil config
    @GET("/check_oil_config.php")
    Call<ResponseBody> checkOilConfig(
            @Query("year") String year,
            @Query("make") String make,
            @Query("model") String model
    );
}