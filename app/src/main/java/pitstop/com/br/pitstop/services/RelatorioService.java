package pitstop.com.br.pitstop.services;

import java.util.List;

import okhttp3.ResponseBody;
import pitstop.com.br.pitstop.model.Avaria;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by wilso on 10/12/2017.
 */

public interface RelatorioService {

    @FormUrlEncoded
    @POST("relatorioVendas")
    Call<ResponseBody> relatorioVendas(@Field("formaDePagamentoEscolhido") String formaDePagamentoEscolhido, @Field("lojaEscolhidaId") String lojaEscolhidaId, @Field("usuarioEscolhido") String usuarioEscolhido, @Field("de") String de, @Field("ate") String ate);


    @FormUrlEncoded
    @POST("relatorioAvarias")
    Call<ResponseBody> relatorioAvarias(@Field("lojaEscolhidaId") String lojaEscolhidaId, @Field("de") String de, @Field("ate") String ate);

    @FormUrlEncoded
    @POST("relatorioEntradaProdutos")
    Call<ResponseBody> relatorioEntradaProdutos(@Field("de") String de, @Field("ate") String ate, @Field("lojaEscolhidaId") String lojaEscolhidaId);


    @FormUrlEncoded
    @POST("relatorioMovimentacaoProduto")
    Call<ResponseBody> relatorioMovimentacaoProduto(@Field("de") String de, @Field("ate") String ate);

    @FormUrlEncoded
    @POST("relatorioFuros")
    Call<ResponseBody> relatorioFuro(@Field("lojaId") String lojaId, @Field("funcionarioId") String funcionarioId, @Field("de") String de, @Field("ate") String ate);

    @FormUrlEncoded
    @POST("relatorioGeralFuncionario")
    Call<ResponseBody> relatorioGeralFuncionario(@Field("lojaEscolhidaId") String lojaEscolhidaId, @Field("usuarioEscolhido") String usuarioEscolhido, @Field("de") String de, @Field("ate") String ate);

    @FormUrlEncoded
    @POST("relatorioGeral")
    Call<ResponseBody> relatorioGeral(@Field("lojaEscolhidaId") String lojaEscolhidaId, @Field("de") String de, @Field("ate") String ate);

    @FormUrlEncoded
    @POST("relatorioVendasFuncionario")
    Call<ResponseBody> relatorioVendasFuncionario(@Field("formaDePagamentoEscolhido") String formaDePagamentoEscolhido, @Field("lojaEscolhidaId") String lojaEscolhidaId, @Field("usuarioEscolhido") String usuarioEscolhido, @Field("de") String de, @Field("ate") String ate);

    @FormUrlEncoded
    @POST("estoqueAtual")
    Call<ResponseBody> estoqueAtual(@Field("loja") String loja);


}
