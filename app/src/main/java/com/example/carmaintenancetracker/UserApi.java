package com.example.carmaintenancetracker;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApi {

    // POST request to add a new user
    @FormUrlEncoded
    @POST("/get_users.php")
    Call<ResponseBody> checkUser(
            @Field("username") String username,
            @Field("password") String password
    );

    // GET request to fetch all users
    @FormUrlEncoded
    @POST("/create_user.php")
    Call<ResponseBody> createUser(
            @Field("username") String username,
            @Field("name") String fullName,
            @Field("email") String email,
            @Field("phone_number") String phoneNumber,
            @Field("password") String password
    );

    // POST request to check if a username exists
    @FormUrlEncoded
    @POST("/check_username.php")
    Call<ResponseBody> checkUsername(
            @Field("username") String username
    );

}