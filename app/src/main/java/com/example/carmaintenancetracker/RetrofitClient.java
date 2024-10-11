package com.example.carmaintenancetracker;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.logging.HttpLoggingInterceptor;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://oriosynology2.ddns.net"; // Replace with your actual base URL

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Optional: Add logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)  // Add the OkHttp client with logging
                    .addConverterFactory(GsonConverterFactory.create())  // Converts JSON to Java objects
                    .build();
        }
        return retrofit;
    }
}