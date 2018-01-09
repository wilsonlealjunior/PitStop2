package pitstop.com.br.pitstop;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.Produto;

/**
 * Created by wilso on 17/10/2017.
 */

public class ReceberProdutosTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private ProgressDialog dialog;

    public ReceberProdutosTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "Aguarde", "Recebendos Produtos...", true, true);
    }

    @Override
    protected String doInBackground(Void... params) {
//        AlunoDAO dao = new AlunoDAO(context);
//        List<Aluno> alunos = dao.buscaAlunos();
//        dao.close();
//
//        AlunoConverter conversor = new AlunoConverter();
//        String json = conversor.converteParaJSON(alunos);
//
//        WebClient client = new WebClient();
//        String resposta = client.post(json);
//        return resposta;

        WebCliente client = new WebCliente();
        String resposta = client.listarProdutos();
        Gson gson = new Gson();
        TypeToken<List<Produto>> token = new TypeToken<List<Produto>>() {
        };
        List<Produto> produtos = gson.fromJson(resposta, token.getType());
        for (Produto p : produtos) {
            Log.e("nome_____-----____--->>", p.getNome());

        }

        return resposta;


    }

    @Override
    protected void onPostExecute(String resposta) {
        dialog.dismiss();
        Toast.makeText(context, resposta, Toast.LENGTH_LONG).show();
    }
}
