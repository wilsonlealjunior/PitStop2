package pitstop.com.br.pitstop.sic;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.dao.MovimentacaoProdutoDAO;
import pitstop.com.br.pitstop.model.MovimentacaoProduto;
import pitstop.com.br.pitstop.preferences.MovimentacaoProdutoPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wilso on 14/11/2017.
 */

public class MovimentacaoProdutoSincronizador {
    private final Context context;

    private EventBus bus = EventBus.getDefault();
    private MovimentacaoProdutoPreferences MovimentacaoProdutoPreferences;

    public MovimentacaoProdutoSincronizador(Context context) {
        this.context = context;

        MovimentacaoProdutoPreferences = new MovimentacaoProdutoPreferences(context);
    }

    public void buscaTodos() {
        if (MovimentacaoProdutoPreferences.temVersao()) {
            buscaNovos();
        } else {
            buscaEntradaProdutos();
        }
    }

    private void buscaNovos() {
        Call<List<MovimentacaoProduto>> call = new RetrofitInializador().getMovimentacaoProdutoService().novos(MovimentacaoProdutoPreferences.getVersao());

        call.enqueue(buscaEntradaProdutosCallback());
    }

    private void buscaEntradaProdutos() {
        Call<List<MovimentacaoProduto>> call = new RetrofitInializador().getMovimentacaoProdutoService().listarMovimentacaoProduto();

        call.enqueue(buscaEntradaProdutosCallback());
    }

    @NonNull
    private Callback<List<MovimentacaoProduto>> buscaEntradaProdutosCallback() {
        return new Callback<List<MovimentacaoProduto>>() {
            @Override
            public void onResponse(Call<List<MovimentacaoProduto>> call, Response<List<MovimentacaoProduto>> response) {
                MovimentacaoProdutoDAO movimentacaoProdutoDAO = new MovimentacaoProdutoDAO(context);
                movimentacaoProdutoDAO.sincroniza(response.body());
                movimentacaoProdutoDAO.close();
                //bus.post(new AtualizaListaLocalizacaoEvent());
                sincronizaEntradaProdutosInternos();
                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    MovimentacaoProdutoPreferences.salvarVersao(versao);
                    Log.e("VERSAO", MovimentacaoProdutoPreferences.getVersao());
                }
//                Toast.makeText(getActivity(), localizacoes.size() + "hj", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), g.toJson(response.body()) , Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Call<List<MovimentacaoProduto>> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());

                Toast.makeText(context, "Houve um erro na sincronização das movimentacoes dos produtos", Toast.LENGTH_SHORT).show();


            }
        };

    }

    private void sincronizaEntradaProdutosInternos() {
        final MovimentacaoProdutoDAO dao = new MovimentacaoProdutoDAO(context);
        final List<MovimentacaoProduto> movimentacaoProdutos = dao.listaNaoSincronizados();
        Call<List<MovimentacaoProduto>> call = new RetrofitInializador().getMovimentacaoProdutoService().atualiza(movimentacaoProdutos);

        call.enqueue(new Callback<List<MovimentacaoProduto>>() {
            @Override
            public void onResponse(Call<List<MovimentacaoProduto>> call, Response<List<MovimentacaoProduto>> response) {
                List<MovimentacaoProduto> movimentacaoProdutos = response.body();
                dao.sincroniza(movimentacaoProdutos);
                dao.close();
                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    MovimentacaoProdutoPreferences.salvarVersao(versao);
                    Log.e("VERSAO", MovimentacaoProdutoPreferences.getVersao());
                }
            }

            @Override
            public void onFailure(Call<List<MovimentacaoProduto>> call, Throwable t) {

            }
        });
    }
}
