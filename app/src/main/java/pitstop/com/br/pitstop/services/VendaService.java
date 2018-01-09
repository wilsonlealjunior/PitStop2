package pitstop.com.br.pitstop.services;

import java.util.List;

import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Venda;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by wilso on 12/11/2017.
 */

public interface VendaService {
    @POST("adicionarVenda")
    Call<Void> insere(@Body Venda venda);

    @GET("listarVendas")
    Call<List<Venda>> listarVendas();

    @GET("diffVendas")
    Call<List<Venda>> novos(@Header("datahora") String versao);

    @PUT("sincronizaVendas")
    Call<List<Venda>> atualiza(@Body List<Venda> vendas);
}
