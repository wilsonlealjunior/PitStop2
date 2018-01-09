package pitstop.com.br.pitstop.sic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.preferences.LojaPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LojaSincronizador {
    private final Context context;
    private EventBus bus = EventBus.getDefault();
    private LojaPreferences lojaPreferences;


    public LojaSincronizador(Context context) {
        this.context = context;

        lojaPreferences = new LojaPreferences(context);
    }

    public void buscaTodos() {
        if (lojaPreferences.temVersao()) {
            buscaNovos();
        } else {
            buscaLojas();
        }
    }

    private void buscaNovos() {
        Call<List<Loja>> call = new RetrofitInializador().getLojaService().novos(lojaPreferences.getVersao());

        call.enqueue(buscaLojaCallback());
    }

    private void buscaLojas() {
        Call<List<Loja>> call = new RetrofitInializador().getLojaService().listarLojas();

        call.enqueue(buscaLojaCallback());
    }

    @NonNull
    private Callback<List<Loja>> buscaLojaCallback() {
        return new Callback<List<Loja>>() {
            @Override
            public void onResponse(Call<List<Loja>> call, Response<List<Loja>> response) {
                LojaDAO lojaDAO = new LojaDAO(context);
                lojaDAO.sincroniza(response.body());
                lojaDAO.close();
                bus.post(new AtualizaListaLojasEvent());
                sincronizaLojasInternos();
                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    lojaPreferences.salvarVersao(versao);
                    Log.e("VERSAO", lojaPreferences.getVersao());
                }
//                Toast.makeText(getActivity(), localizacoes.size() + "hj", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), g.toJson(response.body()) , Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<List<Loja>> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());

                Toast.makeText(context, "Houve um erro na sincronização das Lojas", Toast.LENGTH_SHORT).show();

            }
        };

    }

    private void sincronizaLojasInternos() {
        final LojaDAO dao = new LojaDAO(context);
        final List<Loja> lojas = dao.listaNaoSincronizados();
        Call<List<Loja>> call = new RetrofitInializador().getLojaService().atualiza(lojas);

        call.enqueue(new Callback<List<Loja>>() {
            @Override
            public void onResponse(Call<List<Loja>> call, Response<List<Loja>> response) {
                List<Loja> lojas = response.body();
                dao.sincroniza(lojas);
                dao.close();
                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    lojaPreferences.salvarVersao(versao);
                    Log.e("VERSAO", lojaPreferences.getVersao());
                }
            }

            @Override
            public void onFailure(Call<List<Loja>> call, Throwable t) {

            }
        });
    }


}