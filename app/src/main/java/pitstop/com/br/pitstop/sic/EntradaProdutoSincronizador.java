package pitstop.com.br.pitstop.sic;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.preferences.EntradaProdutoPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wilso on 29/10/2017.
 */

public class EntradaProdutoSincronizador {
    private final Context context;
    private EventBus bus = EventBus.getDefault();

    private EntradaProdutoPreferences entradaProdutoPreferences;

    public EntradaProdutoSincronizador(Context context) {

        this.context = context;
        entradaProdutoPreferences = new EntradaProdutoPreferences(context);
    }

    public void buscaTodos() {
        if (entradaProdutoPreferences.temVersao()) {
            buscaNovos();
        } else {
            buscaEntradaProdutos();
        }
    }

    private void buscaNovos() {
        Call<List<EntradaProduto>> call = new RetrofitInializador().getEntradaProdutoService().novos(entradaProdutoPreferences.getVersao());

        call.enqueue(buscaEntradaProdutosCallback());
    }

    private void buscaEntradaProdutos() {
        Call<List<EntradaProduto>> call = new RetrofitInializador().getEntradaProdutoService().listarEntradaProduto();

        call.enqueue(buscaEntradaProdutosCallback());
    }

    @NonNull
    private Callback<List<EntradaProduto>> buscaEntradaProdutosCallback() {
        return new Callback<List<EntradaProduto>>() {
            @Override
            public void onResponse(Call<List<EntradaProduto>> call, Response<List<EntradaProduto>> response) {
                EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(context);
                entradaProdutoDAO.sincroniza(response.body());
                entradaProdutoDAO.close();
                //bus.post(new AtualizaListaLocalizacaoEvent());
                sincronizaEntradaProdutosInternos();
                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    entradaProdutoPreferences.salvarVersao(versao);
                    Log.e("VERSAO", entradaProdutoPreferences.getVersao());
                }
//                Toast.makeText(getActivity(), localizacoes.size() + "hj", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), g.toJson(response.body()) , Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Call<List<EntradaProduto>> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());

                Toast.makeText(context, "Houve um erro na sincronização das entradas dos produtos", Toast.LENGTH_SHORT).show();

            }
        };

    }

    private void sincronizaEntradaProdutosInternos() {
        final EntradaProdutoDAO dao = new EntradaProdutoDAO(context);
        final List<EntradaProduto> entradaProdutos = dao.listaNaoSincronizados();
        Call<List<EntradaProduto>> call = new RetrofitInializador().getEntradaProdutoService().atualiza(entradaProdutos);

        call.enqueue(new Callback<List<EntradaProduto>>() {
            @Override
            public void onResponse(Call<List<EntradaProduto>> call, Response<List<EntradaProduto>> response) {
                List<EntradaProduto> entradaProdutos = response.body();
                dao.sincroniza(entradaProdutos);
                dao.close();
                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    entradaProdutoPreferences.salvarVersao(versao);
                    Log.e("VERSAO", entradaProdutoPreferences.getVersao());
                }
            }

            @Override
            public void onFailure(Call<List<EntradaProduto>> call, Throwable t) {

            }
        });
    }
}
