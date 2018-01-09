package pitstop.com.br.pitstop.sic;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.dao.VendaDAO;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.Venda;
import pitstop.com.br.pitstop.preferences.VendaPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wilso on 12/11/2017.
 */

public class VendaSincronizador {
    private final Context context;
    private EventBus bus = EventBus.getDefault();

    private VendaPreferences vendaPreferences;

    public VendaSincronizador(Context context) {
        this.context = context;

        vendaPreferences = new VendaPreferences(context);
    }

    public void buscaTodos() {
        if (vendaPreferences.temVersao()) {
            buscaNovos();
        } else {
            buscaVendas();
        }
    }

    private void buscaNovos() {
        Call<List<Venda>> call = new RetrofitInializador().getVendaService().novos(vendaPreferences.getVersao());
        Log.e("log2versao.", String.valueOf(vendaPreferences.getVersao()));

        call.enqueue(buscaVendaCallback());
    }

    private void buscaVendas() {
        Call<List<Venda>> call = new RetrofitInializador().getVendaService().listarVendas();
        Log.e("log2versaotodos", String.valueOf(vendaPreferences.getVersao()));

        call.enqueue(buscaVendaCallback());
    }

    @NonNull
    private Callback<List<Venda>> buscaVendaCallback() {
        return new Callback<List<Venda>>() {
            @Override
            public void onResponse(Call<List<Venda>> call, Response<List<Venda>> response) {
                VendaDAO vendaDAO = new VendaDAO(context);
                vendaDAO.sincroniza(response.body());
                vendaDAO.close();
                bus.post(new AtualizaListaProdutoEvent());
                sincronizaVendasInternos();
                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    vendaPreferences.salvarVersao(versao);
                    Log.e("VERSAOproduto", vendaPreferences.getVersao());
                }
//                Toast.makeText(getActivity(), localizacoes.size() + "hj", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), g.toJson(response.body()) , Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Call<List<Venda>> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());

                Toast.makeText(context, "Houve um erro na sincronização das vendas", Toast.LENGTH_SHORT).show();


            }
        };

    }

    private void sincronizaVendasInternos() {
        final VendaDAO dao = new VendaDAO(context);
        final List<Venda> vendas = dao.listaNaoSincronizados();
        dao.close();
        Call<List<Venda>> call = new RetrofitInializador().getVendaService().atualiza(vendas);

        call.enqueue(new Callback<List<Venda>>() {
            @Override
            public void onResponse(Call<List<Venda>> call, Response<List<Venda>> response) {
                List<Venda> v = response.body();
                dao.sincroniza(v);
                dao.close();

                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    vendaPreferences.salvarVersao(versao);
                    Log.e("VERSAOproduto", vendaPreferences.getVersao());
                }
            }


            @Override
            public void onFailure(Call<List<Venda>> call, Throwable t) {

            }

        });

    }
}
