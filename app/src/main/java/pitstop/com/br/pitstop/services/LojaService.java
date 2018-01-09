package pitstop.com.br.pitstop.services;

import java.util.List;

import pitstop.com.br.pitstop.model.Loja;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by wilso on 19/10/2017.
 */

public interface LojaService {

    @POST("adicionarLoja")
    Call<Void> insere(@Body Loja lojas);

    @GET("listarLojas")
    Call<List<Loja>> listarLojas();

    //ver o que tem de diferente no servidor em relação ao celular e servidor envia a diferenca para o celular
    @GET("diffLoja")
    Call<List<Loja>> novos(@Header("datahora") String versao);

    //atraves da variavel sinc é visto quem não está sincronizado então o celular envia para o servidor os que nao tem no servidor
    @PUT("sincronizaLojas")
    Call<List<Loja>> atualiza(@Body List<Loja> lojas);
}
