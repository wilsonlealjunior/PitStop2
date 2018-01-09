package pitstop.com.br.pitstop.services;

import java.util.List;

import pitstop.com.br.pitstop.model.Produto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by wilso on 18/10/2017.
 */

public interface ProdutoService {

    @POST("adicionarProduto")
    Call<Void> insere(@Body Produto produto);

    @GET("listarProdutos")
    Call<List<Produto>> listarProdutos();

    @GET("diffProduto")
    Call<List<Produto>> novos(@Header("datahora") String versao);

    @PUT("sincronizaProdutos")
    Call<List<Produto>> atualiza(@Body List<Produto> produtos);

}


