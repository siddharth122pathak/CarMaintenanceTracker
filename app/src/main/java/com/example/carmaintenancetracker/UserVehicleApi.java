package com.example.carmaintenancetracker;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserVehicleApi {

    @FormUrlEncoded
    @POST("/add_vehicle.php")
    Call<ResponseBody> setVehicle(
            @Field("userId") String userId,
            @Field("make") String make,
            @Field("model") String model,
            @Field("year") String year,
            @Field("nickname") String nickname
    );

    // Get all vehicles
    @GET("/get_all_vehicles.php")
    Call<ResponseBody> getAllVehicles(
            @Query("userId") String userId
    );

    // Get the active vehicle
    @GET("/get_active_vehicle.php")
    Call<ResponseBody> getActiveVehicle( @Query("id") String userId);


    // Update mileage for a vehicle
    @FormUrlEncoded
    @POST("/update_mileage.php")
    Call<ResponseBody> updateMileage(
            @Field("car_id") String carId,
            @Field("miles") int newMileage
    );

    // Update notification setting for a vehicle
    @FormUrlEncoded
    @POST("/update_notification.php")
    Call<ResponseBody> updateNotificationSetting(
            @Field("car_id") String carId,
            @Field("notification_status") int notificationStatus // Use 1 for true, 0 for false
    );

    // Swap two vehicles
    @FormUrlEncoded
    @POST("/swap_vehicle.php")
    Call<ResponseBody> swapVehicles(
            @Field("id") String userId,
            @Field("primaryVehicleId") String primaryVehicleId,
            @Field("targetVehicleId") String targetVehicleId
    );

    // Get the last updated date of a vehicle
    @GET("/get_last_updated.php")
    Call<ResponseBody> getLastUpdated(
            @Query("car_id") String carId
    );

    // Get vehicle details by car_id
    @GET("/get_vehicle_by_index.php")
    Call<ResponseBody> getVehicleByIndex(
            @Query("car_id") String carId,
            @Query("id") String userId
    );

    // Get vehicle details by car_id
    @POST("/update_vehicle_nickname.php")
    Call<ResponseBody> updateVehicleNickname(
            @Field("car_id") String carId,
            @Field("nickname") String nickname
    );
}