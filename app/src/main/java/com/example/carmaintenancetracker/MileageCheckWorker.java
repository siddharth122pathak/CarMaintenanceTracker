//package com.example.carmaintenancetracker;
//
//import android.content.Context;
//import android.icu.util.Calendar;
//import androidx.annotation.NonNull;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//import okhttp3.ResponseBody;
//import org.jetbrains.annotations.NotNull;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import java.util.List;
//
//public class MileageCheckWorker extends Worker {
//
//    private final UserVehicleApi userVehicleApi; //Retrofit API instance
//
//    public MileageCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//        userVehicleApi = RetrofitClient.getRetrofitInstance().create(UserVehicleApi.class);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        fetchVehiclesAndCheckMileage();
//        return Result.success();
//    }
//
//    //Fetches all vehicles from the server and checks mileage update status
//    private void fetchVehiclesAndCheckMileage() {
//        userVehicleApi.getAllVehicles().enqueue(new Callback<List<UserVehicle>>() {
//            @Override
//            public void onResponse(@NotNull Call<List<UserVehicle>> call, @NotNull Response<List<UserVehicle>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    long currentTime = System.currentTimeMillis();
//                    for (UserVehicle vehicle : response.body()) {
//                       //Check if notification is enabled and the mileage update is overdue
//                        if (vehicle.isNotificationEnabled() && shouldNotify(vehicle.getLastUpdateTimeStamp(), currentTime)) {
//                            String vehicleInfo = vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel();
//                            NotificationHelper.showNotification(getApplicationContext(), vehicleInfo, vehicle.getCarId());
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<List<UserVehicle>> call, @NotNull Throwable t) {
//                //Log the failure or handle it as needed
//            }
//        });
//    }
//
//    //Method to determine if the notification should be sent
//    private boolean shouldNotify(long lastUpdateTimeStamp, long currentTime) {
//        Calendar lastUpdateCalendar = Calendar.getInstance();
//        lastUpdateCalendar.setTimeInMillis(lastUpdateTimeStamp); //Set the calendar to the last update time
//        lastUpdateCalendar.add(Calendar.MONTH, 3); //Add 3 months to the last update time
//
//        //Return true if 3 months have passed, false otherwise
//        return lastUpdateCalendar.getTimeInMillis() <= currentTime;
//    }
//}
