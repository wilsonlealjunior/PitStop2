package pitstop.com.br.pitstop.services;

import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by wilso on 29/11/2017.
 */

public interface UsuarioService {
    @POST("cadastrarUsuario")
    Call<Usuario> insere(@Body Usuario usuario);


    @POST("editarUsuario")
    Call<Usuario> editar(@Body Usuario usuario);
}
