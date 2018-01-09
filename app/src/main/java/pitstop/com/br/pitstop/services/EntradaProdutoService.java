package pitstop.com.br.pitstop.services;

import java.util.List;

import pitstop.com.br.pitstop.model.EntradaProduto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by wilso on 29/10/2017.
 */

public interface EntradaProdutoService {
    @POST("adicionarEntradaProdutos")
    Call<Void> insere(@Body EntradaProduto entradaProduto);

    @GET("listarEntradaProdutos")
    Call<List<EntradaProduto>> listarEntradaProduto();

    //ver o que tem de diferente no servidor em relação ao celular e servidor envia a diferenca para o celular
    @GET("diffEntradaProdutos")
    Call<List<EntradaProduto>> novos(@Header("datahora") String versao);

    //atraves da variavel sinc é visto quem não está sincronizado então o celular envia para o servidor os que nao tem no servidor
    @PUT("sincronizaEntradaProdutos")
    Call<List<EntradaProduto>> atualiza(@Body List<EntradaProduto> entradaProduto);
}
