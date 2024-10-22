package com.example.carmaintenancetracker.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.GET;


public interface MaintenanceApi {

    // POST request to add maintenance
    @FormUrlEncoded
    @POST("/add_maintenance.php")
    Call<ResponseBody> addMaintenance(
            @Field("maintenance_type") String maintenanceType,
            @Field("maintenance_date") String maintenanceDate,
            @Field("maintenance_description") String maintenanceDescription
    );

    // POST request to add more maintenance (for additional actions)
    @FormUrlEncoded
    @POST("/add_additional_maintenance.php")
    Call<ResponseBody> addAdditionalMaintenance(
            @Field("maintenance_type") String maintenanceType,
            @Field("maintenance_date") String maintenanceDate
    );

    // Define the HTTP GET request to fetch maintenance data
    @GET("get_maintenance_data.php")
    Call<ResponseBody> getMaintenanceData(
            @Query("table") String table,
            @Query("make") String make,
            @Query("model") String model,
            @Query("year") int year
    );


}