package pitstop.com.br.pitstop.services;


import pitstop.com.br.pitstop.model.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by wilso on 03/11/2017.
 */

public interface LoginLogoutService {

    @FormUrlEncoded
    @POST("login")
    Call<Usuario> login(@Field("username") String username, @Field("password") String password);

    @POST("logout")
    Call<Usuario> logout();

}
