package com.example.carmaintenancetracker;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserVehicleApi {

    //Add a new vehicle
    @FormUrlEncoded
    @POST("/add_vehicle.php")
    Call<ResponseBody> addVehicle(
            @Field("user_id") int userId,
            @Field("make") String make,
            @Field("model") String model,
            @Field("year") String year,
            @Field("nickname") String nickname
    );

    //Retrieve vehicles for a specific user
    @GET("/get_vehicles.php")
    Call<List<UserVehicle>> getVehicles(@Query("user_id") int userId);

    //Update a vehicle's information
    @FormUrlEncoded
    @POST("/update_vehicle.php")
    Call<ResponseBody> updateVehicle(
            @Field("vehicle_id") int vehicleId,
            @Field("make") String make,
            @Field("model") String model,
            @Field("year") String year,
            @Field("nickname") String nickname
    );

    //Delete a vehicle by its ID
    @FormUrlEncoded
    @POST("/delete_vehicle.php")
    Call<ResponseBody> deleteVehicle(@Field("vehicle_id") int vehicleId);
}