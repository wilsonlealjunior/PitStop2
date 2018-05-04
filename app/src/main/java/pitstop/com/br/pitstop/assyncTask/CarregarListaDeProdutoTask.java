package pitstop.com.br.pitstop.assyncTask;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import de.greenrobot.event.EventBus;
import dmax.dialog.SpotsDialog;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.event.CarregaListaDeProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;

/**
 * Created by wilso on 17/03/2018.
 */

public class CarregarListaDeProdutoTask extends AsyncTask<Void, Void, String> {
    private Context context;
    AlertDialog dialog;
    Loja loja;
    List<Produto> produtos;
    ProdutoDAO produtoDAO;
    private EventBus bus = EventBus.getDefault();

    public CarregarListaDeProdutoTask(Context context, Loja loja, List<Produto> produtos) {
        this.context = context;
        this.loja = loja;
        this.produtos = produtos;
        produtoDAO = new ProdutoDAO(context);
    }

    @Override
    protected void onPreExecute() {
        dialog = new SpotsDialog(context, "Carregando Lista de Produtos", R.style.progressDialog);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        produtos.clear();
        if (loja == null) {
            produtos.addAll(produtoDAO.listarProdutos());
        } else {
            produtos.addAll(produtoDAO.procuraPorLoja(loja));
        }
        produtoDAO.close();
        String resposta1 = "Lista de produtos carregado";
        return resposta1;
    }

    @Override
    protected void onPostExecute(String resposta1) {
        dialog.dismiss();
        bus.post(new CarregaListaDeProduto());

    }
}
