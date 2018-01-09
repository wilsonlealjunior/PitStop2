package pitstop.com.br.pitstop.sic;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.preferences.ProdutoPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wilso on 25/10/2017.
 */

public class ProdutoSincronizador {
    private final Context context;
    private EventBus bus = EventBus.getDefault();
    private ProdutoPreferences produtoPreferences;


    public ProdutoSincronizador(Context context) {
        this.context = context;

        produtoPreferences = new ProdutoPreferences(context);
    }

    public void buscaTodos() {
        if (produtoPreferences.temVersao()) {
            buscaNovos();
        } else {
            buscaProdutos();
        }
    }

    private void buscaNovos() {
        Call<List<Produto>> call = new RetrofitInializador().getProdutoService().novos(produtoPreferences.getVersao());
        Log.e("log2versao.",String.valueOf(produtoPreferences.getVersao()));

        call.enqueue(buscaProdutoCallback());
    }

    private void buscaProdutos() {
        Call<List<Produto>> call = new RetrofitInializador().getProdutoService().listarProdutos();
        Log.e("log2versaotodos",String.valueOf(produtoPreferences.getVersao()));

        call.enqueue(buscaProdutoCallback());
    }

    @NonNull
    private Callback<List<Produto>> buscaProdutoCallback() {
        return new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                ProdutoDAO produtoDAO = new ProdutoDAO(context);
                produtoDAO.sincroniza(response.body());
                for (Produto p :response.body()) {
                    Log.e("log2nome-.",p.getNome());
                    Log.e("log2quatidade.",String.valueOf(p.getQuantidade()));

                }
                produtoDAO.close();
                bus.post(new AtualizaListaProdutoEvent());
                sincronizaProdutosInternos();
                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    produtoPreferences.salvarVersao(versao);
                    Log.e("VERSAOproduto", produtoPreferences.getVersao());
                }
//                Toast.makeText(getActivity(), localizacoes.size() + "hj", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), g.toJson(response.body()) , Toast.LENGTH_SHORT).show();



            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());

                Toast.makeText(context, "Houve um erro na sincronização dos produtos", Toast.LENGTH_SHORT).show();

            }
        };

    }

    private void sincronizaProdutosInternos() {
        final ProdutoDAO dao = new ProdutoDAO(context);
        final List<Produto> produtos = dao.listaNaoSincronizados();
        dao.close();
        Call<List<Produto>> call = new RetrofitInializador().getProdutoService().atualiza(produtos);

        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                List<Produto> p = response.body();
                dao.sincroniza(p);
                dao.close();

                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    produtoPreferences.salvarVersao(versao);
                    Log.e("VERSAOproduto", produtoPreferences.getVersao());
                }
            }



            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {

            }

        });

    }
}
