package pitstop.com.br.pitstop.services;

import java.util.List;

import pitstop.com.br.pitstop.model.MovimentacaoProduto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by wilso on 14/11/2017.
 */

public interface MovimentacaoProdutoService {
    @POST("adicionarMovimentacaoProduto")
    Call<Void> insere(@Body MovimentacaoProduto MovimentacaoProduto);

    @GET("listarMovimentacaoProduto")
    Call<List<MovimentacaoProduto>> listarMovimentacaoProduto();

    //ver o que tem de diferente no servidor em relação ao celular e servidor envia a diferenca para o celular
    @GET("diffMovimentacaoProduto")
    Call<List<MovimentacaoProduto>> novos(@Header("datahora") String versao);

    //atraves da variavel sinc é visto quem não está sincronizado então o celular envia para o servidor os que nao tem no servidor
    @PUT("sincronizaMovimentacaoProduto")
    Call<List<MovimentacaoProduto>> atualiza(@Body List<MovimentacaoProduto> MovimentacaoProduto);
}
