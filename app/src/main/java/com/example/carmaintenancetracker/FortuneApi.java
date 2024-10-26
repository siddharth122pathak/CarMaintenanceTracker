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

    // GET request to check the tire config
    @GET("/check_tire_config.php")
    Call<ResponseBody> checkTireConfig(
            @Query("year") String year,
            @Query("make") String make,
            @Query("model") String model
    );

    // GET request to check the brake inspection config
    @GET("/check_brake_inspection_config.php")
    Call<ResponseBody> checkBrakeInspectionConfig(
            @Query("year") String year,
            @Query("make") String make,
            @Query("model") String model
    );

    // GET request to check the cabin filter config
    @GET("/check_cabin_filter_config.php")
    Call<ResponseBody> checkCabinFilterConfig(
            @Query("year") String year,
            @Query("make") String make,
            @Query("model") String model
    );

    // GET request to check the coolant config
    @GET("/check_coolant_config.php")
    Call<ResponseBody> checkCoolantConfig(
            @Query("year") String year,
            @Query("make") String make,
            @Query("model") String model
    );

    // GET request to check the engine filter config
    @GET("/check_engine_filter_config.php")
    Call<ResponseBody> checkEngineFilterConfig(
            @Query("year") String year,
            @Query("make") String make,
            @Query("model") String model
    );

    // GET request to check the spark plugs config
    @GET("/check_spark_plugs_config.php")
    Call<ResponseBody> checkSparkPlugsConfig(
            @Query("year") String year,
            @Query("make") String make,
            @Query("model") String model
    );

    // GET request to check the transmission config
    @GET("/check_transmission_config.php")
    Call<ResponseBody> checkTransmissionConfig(
            @Query("year") String year,
            @Query("make") String make,
            @Query("model") String model
    );
}