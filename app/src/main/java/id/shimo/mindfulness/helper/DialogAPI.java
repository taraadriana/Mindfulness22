package id.shimo.mindfulness.helper;

import id.shimo.mindfulness.model.Journal;
import retrofit2.Call;
import okhttp3.ResponseBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;

public interface DialogAPI {
    public static final String baseUrl="http://192.168.209.203:8000/api/";

    @FormUrlEncoded
    @POST("register")
    public Call<ResponseBody> createUser(@Field("name")String name,
                                         @Field("username") String username,
                                         @Field("gender") String gender,
                                         @Field("email")String email,
                                         @Field("password") String password,
                                         @Field("age") int age
                                         );

    @FormUrlEncoded
    @POST("journal/create")
    public Call<ResponseBody> createJournal(@Field("journal") String journalJson);

    @FormUrlEncoded
    @POST("journal/update")
    public Call<ResponseBody> updateJournal(@Field("journal") String journalJson);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "journal/delete", hasBody = true)
    public Call<ResponseBody> deleteJournal(@Field("id") int id_journal);

}
