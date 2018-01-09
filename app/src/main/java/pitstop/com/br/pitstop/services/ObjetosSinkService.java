package pitstop.com.br.pitstop.services;

import java.util.List;

import pitstop.com.br.pitstop.model.MovimentacaoProduto;
import pitstop.com.br.pitstop.model.ObjetosSink;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by wilso on 28/11/2017.
 */

public interface ObjetosSinkService {
    @POST("adicionarObjetosSink")
    Call<Void> insere(@Body ObjetosSink objetosSink);

    @GET("listarObjetosSink")
    Call<ObjetosSink> listarObjetosSink();

    //ver o que tem de diferente no servidor em relação ao celular e servidor envia a diferenca para o celular
    @GET("diffObjetosSink")
    Call<ObjetosSink> novos(@Header("datahora") String versao);

    //atraves da variavel sinc é visto quem não está sincronizado então o celular envia para o servidor os que nao tem no servidor
    @PUT("sincronizaObjetosSink")
    Call<ObjetosSink> atualiza(@Body ObjetosSink objetosSink);
}
