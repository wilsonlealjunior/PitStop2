package pitstop.com.br.pitstop.sic;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.dao.AvariaDAO;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.preferences.AvariaPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wilso on 28/11/2017.
 */

public class AvariaSincronizador {
    private final Context context;
    private EventBus bus = EventBus.getDefault();
    private AvariaPreferences avariaPreferences;

    public AvariaSincronizador(Context context) {
        this.context = context;
        avariaPreferences = new AvariaPreferences(context);
    }

    public void buscaTodos() {
        if (avariaPreferences.temVersao()) {
            buscaNovos();
        } else {
            buscaAvarias();
        }
    }

    private void buscaNovos() {
        Call<List<Avaria>> call = new RetrofitInializador().getAvariaService().novos(avariaPreferences.getVersao());

        call.enqueue(buscaAvariasCallback());
    }

    private void buscaAvarias() {
        Call<List<Avaria>> call = new RetrofitInializador().getAvariaService().listarAvarias();

        call.enqueue(buscaAvariasCallback());
    }

    @NonNull
    private Callback<List<Avaria>> buscaAvariasCallback() {
        return new Callback<List<Avaria>>() {
            @Override
            public void onResponse(Call<List<Avaria>> call, Response<List<Avaria>> response) {
                AvariaDAO avariaDAO = new AvariaDAO(context);
                avariaDAO.sincroniza(response.body());
                avariaDAO.close();
                //bus.post(new AtualizaListaLocalizacaoEvent());
                sincronizaEntradaProdutosInternos();
                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    avariaPreferences.salvarVersao(versao);
                    Log.e("VERSAO", avariaPreferences.getVersao());
                }
//                Toast.makeText(getActivity(), localizacoes.size() + "hj", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), g.toJson(response.body()) , Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<List<Avaria>> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());

                Toast.makeText(context, "Houve um erro na sincronização das avarias", Toast.LENGTH_SHORT).show();


            }
        };

    }

    private void sincronizaEntradaProdutosInternos() {
        final AvariaDAO dao = new AvariaDAO(context);
        final List<Avaria> avarias = dao.listaNaoSincronizados();
        Call<List<Avaria>> call = new RetrofitInializador().getAvariaService().atualiza(avarias);

        call.enqueue(new Callback<List<Avaria>>() {
            @Override
            public void onResponse(Call<List<Avaria>> call, Response<List<Avaria>> response) {
                List<Avaria> avarias = response.body();
                dao.sincroniza(avarias);
                dao.close();
                if (response.body().size() != 0) {
                    //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
                    //ate acabar os elementos, pega um atuliza, pega outro, atualiza
                    String versao = response.body().get(0).getMomentoDaUltimaAtualizacao();

                    avariaPreferences.salvarVersao(versao);
                    Log.e("VERSAO", avariaPreferences.getVersao());
                }
            }

            @Override
            public void onFailure(Call<List<Avaria>> call, Throwable t) {

            }
        });
    }
}
