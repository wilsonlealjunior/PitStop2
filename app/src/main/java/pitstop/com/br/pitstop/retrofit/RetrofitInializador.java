package pitstop.com.br.pitstop.retrofit;

/**
 * Created by wilso on 18/10/2017.
 */

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


import pitstop.com.br.pitstop.services.AvariaService;
import pitstop.com.br.pitstop.services.LojaService;
import pitstop.com.br.pitstop.services.LoginLogoutService;
import pitstop.com.br.pitstop.services.EntradaProdutoService;
import pitstop.com.br.pitstop.services.MovimentacaoProdutoService;
import pitstop.com.br.pitstop.services.ObjetosSinkService;
import pitstop.com.br.pitstop.services.ProdutoService;
import pitstop.com.br.pitstop.services.RelatorioService;
import pitstop.com.br.pitstop.services.UsuarioService;
import pitstop.com.br.pitstop.services.VendaService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInializador {


    private final Retrofit retrofit;

    public RetrofitInializador() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder().
                connectTimeout(8, TimeUnit.SECONDS).
                readTimeout(15, TimeUnit.MINUTES).
                writeTimeout(15, TimeUnit.MINUTES);

        client.addInterceptor(interceptor);
        //Gson gson = new GsonBuilder().setLenient().create();


        retrofit = new Retrofit.Builder().baseUrl("http://10.0.0.8:8080/webServiceEstoque/")
                .addConverterFactory(GsonConverterFactory.create()).client(client.build()).build();


    }

    public ProdutoService getProdutoService() {
        return retrofit.create(ProdutoService.class);
    }

    public LojaService getLojaService() {
        return retrofit.create(LojaService.class);
    }

    public EntradaProdutoService getEntradaProdutoService() {
        return retrofit.create(EntradaProdutoService.class);
    }

    public LoginLogoutService getLoginLogoutService() {
        return retrofit.create(LoginLogoutService.class);
    }

    public VendaService getVendaService() {
        return retrofit.create(VendaService.class);
    }

    public MovimentacaoProdutoService getMovimentacaoProdutoService() {
        return retrofit.create(MovimentacaoProdutoService.class);
    }

    public AvariaService getAvariaService() {
        return retrofit.create(AvariaService.class);
    }

    public ObjetosSinkService getObjetosSinkService() {
        return retrofit.create(ObjetosSinkService.class);
    }

    public UsuarioService getUsuarioService() {
        return retrofit.create(UsuarioService.class);
    }

    public RelatorioService getRelatorioService() {
        return retrofit.create(RelatorioService.class);
    }

}