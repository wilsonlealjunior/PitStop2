package pitstop.com.br.pitstop.services;

import java.util.List;

import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.EntradaProduto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by wilso on 28/11/2017.
 */

public interface AvariaService {
    @POST("adicionarAvaria")
    Call<Void> insere(@Body Avaria avaria);

    @GET("listarAvarias")
    Call<List<Avaria>> listarAvarias();

    //ver o que tem de diferente no servidor em relação ao celular e servidor envia a diferenca para o celular
    @GET("diffAvarias")
    Call<List<Avaria>> novos(@Header("datahora") String versao);

    //atraves da variavel sinc é visto quem não está sincronizado então o celular envia para o servidor os que nao tem no servidor
    @PUT("sincronizaAvarias")
    Call<List<Avaria>> atualiza(@Body List<Avaria> entradaProduto);
}
