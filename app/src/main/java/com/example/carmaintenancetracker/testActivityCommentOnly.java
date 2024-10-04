/*
public interface ApiInterface {
    @GET("users")
    Call<List<User>> getUsers();

    @POST("users")
    Call<User> createUser(@Body User user);
}

public class ApiClient {
    private static final String BASE_URL = "https://your-api-url.com/api/";
    private OkHttpClient client = new OkHttpClient();

    public ApiInterface createApiInterface() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .build()
                .create(ApiInterface.class);
    }
}*/
