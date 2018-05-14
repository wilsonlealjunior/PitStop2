package pitstop.com.br.pitstop.sic;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import dmax.dialog.SpotsDialog;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.dao.AvariaDAO;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.FuroDAO;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.MovimentacaoProdutoDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.dao.VendaDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.event.AtualizarGraficos;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Furo;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.MovimentacaoProduto;
import pitstop.com.br.pitstop.model.ObjetosSink;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.model.Venda;
import pitstop.com.br.pitstop.preferences.ObjetosSinkPreferences;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wilso on 28/11/2017.
 */

public class ObjetosSinkSincronizador {
    private final Context context;
    private EventBus bus = EventBus.getDefault();
    private ObjetosSinkPreferences objetosSinkPreferences;
    private UsuarioPreferences usuarioPreferences;
    //    ProgressDialog progressDialog;
    AlertDialog dialog;


    ProdutoDAO produtoDAO;
    LojaDAO lojaDAO;
    AvariaDAO avariaDAO;
    EntradaProdutoDAO entradaProdutoDAO;
    MovimentacaoProdutoDAO movimentacaoProdutoDAO;
    VendaDAO vendaDAO;
    UsuarioDAO usuarioDAO;
    FuroDAO furoDAO;
    boolean sincronizacaoAtiva = false;

    public ObjetosSinkSincronizador(Context context) {
        this.context = context;
//        progressDialog = new ProgressDialog(context);
        usuarioPreferences = new UsuarioPreferences(context);
        objetosSinkPreferences = new ObjetosSinkPreferences(context);
        dialog = new SpotsDialog(context, R.style.progressDialog);
        dialog.setCancelable(false);
        produtoDAO = new ProdutoDAO(context);
        lojaDAO = new LojaDAO(context);
        entradaProdutoDAO = new EntradaProdutoDAO(context);
        movimentacaoProdutoDAO = new MovimentacaoProdutoDAO(context);
        vendaDAO = new VendaDAO(context);
        usuarioDAO = new UsuarioDAO(context);
        furoDAO = new FuroDAO(context);
        avariaDAO = new AvariaDAO(context);
    }

    public void buscaTodos() {
        if (!sincronizacaoAtiva) {
            sincronizacaoAtiva = true;
            if (objetosSinkPreferences.temVersao()) {
                buscaNovos();
            } else {
                buscaObjetosSink();
            }
        }
    }

    private void buscaNovos() {
        Call<ObjetosSink> call = new RetrofitInializador().getObjetosSinkService().novos(objetosSinkPreferences.getVersao());
        Log.e("log2versao.", String.valueOf(objetosSinkPreferences.getVersao()));
        dialog.setCancelable(false);
        dialog.show();
        call.enqueue(buscaObjetosSinkCallback());
    }


    private void buscaObjetosSink() {
        Call<ObjetosSink> call = new RetrofitInializador().getObjetosSinkService().listarObjetosSink();
        Log.e("log2versaotodos", String.valueOf(objetosSinkPreferences.getVersao()));
        dialog.setCancelable(false);
        dialog.show();

        call.enqueue(buscaObjetosSinkCallback());
    }

    @NonNull
    private Callback<ObjetosSink> buscaObjetosSinkCallback() {
        return new Callback<ObjetosSink>() {
            @Override
            public void onResponse(Call<ObjetosSink> call, Response<ObjetosSink> response) {
                final ObjetosSink objetosSink = response.body();
                dialog.setMessage("Salvando Dados");
                PersistirEntidadades persistirEntidadades = new PersistirEntidadades(objetosSink);
                persistirEntidadades.execute();
            }

            @Override
            public void onFailure(Call<ObjetosSink> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                Toast.makeText(context, "Houve um erro na sincronização", Toast.LENGTH_LONG).show();
                dialog.dismiss();

                bus.post(new AtualizarGraficos());
                bus.post(new AtualizaListaLojasEvent());
//                bus.post(new AtualizaListaProdutoEvent());
                if (usuarioPreferences.getUsuario().getRole().equals("Funcionario")) {
                    bus.post(new AtualizaListaProdutoEvent());
                }


//                progressDialog.dismiss();
                sincronizacaoAtiva = false;

            }
        };

    }


    private void sincronizaObjetosSinkInternos() {
        Log.e("Recuperando nao Sinc","Produtos");
        final List<Produto> produtos = produtoDAO.listaNaoSincronizados();
        produtoDAO.close();
        Log.e("Recuperando nao Sinc","Lojas");
        final List<Loja> lojas = lojaDAO.listaNaoSincronizados();
        lojaDAO.close();
        Log.e("Recuperando nao Sinc","Avarias");
        final List<Avaria> avarias = avariaDAO.listaNaoSincronizados();
        avariaDAO.close();
        Log.e("Recuperando nao Sinc","Entrada de Produto");
        final List<EntradaProduto> entradaProdutos = entradaProdutoDAO.listaNaoSincronizados();
        entradaProdutoDAO.close();
        Log.e("Recuperando nao Sinc","Movimentacao de produto");
        final List<MovimentacaoProduto> movimentacaoProdutos = movimentacaoProdutoDAO.listaNaoSincronizados();
        movimentacaoProdutoDAO.close();
        Log.e("Recuperando nao Sinc","Venda");
        vendaDAO = new VendaDAO(context);
        final List<Venda> vendas = vendaDAO.listaNaoSincronizados();
        vendaDAO.close();
        Log.e("Recuperando nao Sinc","Usuarios");
        final List<Usuario> usuarios = usuarioDAO.listaNaoSincronizados();
        usuarioDAO.close();
        Log.e("Recuperando nao Sinc","Furos");
        final List<Furo> furos = furoDAO.listaNaoSincronizados();
        furoDAO.close();

        final ObjetosSink objetosSink = new ObjetosSink();
        objetosSink.setAvarias(avarias);
        objetosSink.setEntradaProdutos(entradaProdutos);
        objetosSink.setLojas(lojas);
        objetosSink.setMovimentacaoProdutos(movimentacaoProdutos);
        objetosSink.setVendas(vendas);
        objetosSink.setProdutos(produtos);
        objetosSink.setUsuarios(usuarios);
        objetosSink.setFuros(furos);
        Call<ObjetosSink> call = new RetrofitInializador().getObjetosSinkService().atualiza(objetosSink);

        call.enqueue(new Callback<ObjetosSink>() {
            @Override
            public void onResponse(Call<ObjetosSink> call, Response<ObjetosSink> response) {
                dialog.setMessage("Sincronizando Servidor");
                final ObjetosSink objetosSink = response.body();

                Log.e("Sincronizando","Lojas");
                lojaDAO = new LojaDAO(context);
                lojaDAO.sincroniza(objetosSink.getLojas());
                lojaDAO.close();
                Log.e("Sincronizando","Produtos");
                produtoDAO = new ProdutoDAO(context);
                produtoDAO.sincroniza(objetosSink.getProdutos());
                produtoDAO.close();
                Log.e("Sincronizando","Avaria");
                avariaDAO = new AvariaDAO(context);
                avariaDAO.sincroniza(objetosSink.getAvarias());
                avariaDAO.close();
                Log.e("Sincronizando","Movimentacao e produto");
                movimentacaoProdutoDAO = new MovimentacaoProdutoDAO(context);
                movimentacaoProdutoDAO.sincroniza(objetosSink.getMovimentacaoProdutos());
                movimentacaoProdutoDAO.close();
                Log.e("Sincronizando","Vendas");
                vendaDAO = new VendaDAO(context);
                vendaDAO.sincroniza(objetosSink.getVendas());
                vendaDAO.close();
                Log.e("Sincronizando","Entrada de Produtos");
                entradaProdutoDAO = new EntradaProdutoDAO(context);
                entradaProdutoDAO.sincroniza(objetosSink.getEntradaProdutos());
                entradaProdutoDAO.close();
                Log.e("Sincronizando","Usurios");
                usuarioDAO = new UsuarioDAO(context);
                usuarioDAO.sincroniza(objetosSink.getUsuarios());
                usuarioDAO.close();
                Log.e("Sincronizando","Furos");
                furoDAO = new FuroDAO(context);
                furoDAO.sincroniza(objetosSink.getFuros());
                furoDAO.close();


                String versao = objetosSink.getMomentoDaUltimaAtualizacao();

                if (versao != null) {
                    objetosSinkPreferences.salvarVersao(versao);
                }

                Log.e("VERSAOproduto", objetosSinkPreferences.getVersao());
                dialog.dismiss();

                bus.post(new AtualizaListaLojasEvent());
                bus.post(new AtualizaListaProdutoEvent());
                bus.post(new AtualizarGraficos());
                Toast.makeText(context, "Sincronizado com Sucesso", Toast.LENGTH_LONG).show();


//                progressDialog.dismiss();
                sincronizacaoAtiva = false;


            }


            @Override
            public void onFailure(Call<ObjetosSink> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(context, "Houve um erro na sincronização", Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
                sincronizacaoAtiva = false;


            }

        });

    }

    private class PersistirEntidadades extends AsyncTask<Void, Void, String> {
        ObjetosSink objetosSink;

        public PersistirEntidadades(ObjetosSink objetosSink) {
            this.objetosSink = objetosSink;
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected String doInBackground(Void... params) {
            Log.e("Persistindo","Usuario");
            usuarioDAO = new UsuarioDAO(context);
            usuarioDAO.sincroniza(objetosSink.getUsuarios());
            usuarioDAO.close();
            Log.e("Persistindo","Loja");
            lojaDAO = new LojaDAO(context);
            lojaDAO.sincroniza(objetosSink.getLojas());
            lojaDAO.close();
            Log.e("Persistindo","Avaria");
            avariaDAO = new AvariaDAO(context);
            avariaDAO.sincroniza(objetosSink.getAvarias());
            avariaDAO.close();
            Log.e("Persistindo","Movimentacao de produto");
            movimentacaoProdutoDAO = new MovimentacaoProdutoDAO(context);
            movimentacaoProdutoDAO.sincroniza(objetosSink.getMovimentacaoProdutos());
            movimentacaoProdutoDAO.close();
            Log.e("Persistindo","Venda");
            vendaDAO = new VendaDAO(context);
            vendaDAO.sincroniza(objetosSink.getVendas());
            vendaDAO.close();
            Log.e("Persistindo","Furo");
            furoDAO = new FuroDAO(context);
            furoDAO.sincroniza(objetosSink.getFuros());
            furoDAO.close();
            //esse trecho de codigo serve para
            // deixar a quantidade consistente
            // se o produto for alterado no
            // servidor e localmente então receberemos
            // todas as entidades principalmente entrada
            // produto que tem as informações da quantidade,
            // os outros dados como nome preço vai ser sempre dando prioridade para o servidor
            Log.e("Persistindo","Produto");
            produtoDAO = new ProdutoDAO(context);
            List<Produto> produtosAlteradosLocalmente = new ArrayList<>();
            produtosAlteradosLocalmente.addAll(produtoDAO.listaNaoSincronizados());
            produtoDAO.close();
            produtoDAO.sincroniza(objetosSink.getProdutos());
            produtoDAO.close();
            Log.e("Persistindo","Entrada de produto");
            entradaProdutoDAO = new EntradaProdutoDAO(context);
            entradaProdutoDAO.sincroniza(objetosSink.getEntradaProdutos());
            entradaProdutoDAO.close();
            Produto produtoPrincipal;
            //TODO fazer testes testando esse algoritmo
            List<Produto> produtosIncosistente = new ArrayList<>();
            for (Produto produtoLocalAlterado : produtosAlteradosLocalmente) {
                for (Produto produtoVindoServidor : objetosSink.getProdutos()) {
                    if (produtoLocalAlterado.getId().equals(produtoVindoServidor.getId())) {
                        if (produtoVindoServidor.vinculado()) {
                            produtoPrincipal = produtoDAO.procuraPorId(produtoVindoServidor.getIdProdutoPrincipal());
                            produtoDAO.close();
                            produtoPrincipal.getEntradaProdutos().addAll(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                            entradaProdutoDAO.close();
                            produtoPrincipal.calcularQuantidade();
                        } else {
                            produtoPrincipal = produtoDAO.procuraPorId(produtoVindoServidor.getId());
                            produtoDAO.close();
                            produtoPrincipal.getEntradaProdutos().addAll(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                            entradaProdutoDAO.close();
                            produtoPrincipal.calcularQuantidade();
                        }
                        produtosIncosistente.add(produtoPrincipal);
                    }

                }

            }

            for (Produto p : produtosIncosistente) {
                for (String produtoVinculoId : p.getIdProdutoVinculado()) {
                    Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId);
                    produtoDAO.close();
                    produtoVinculo.setQuantidade(p.getQuantidade());
                    produtoVinculo.desincroniza();
                    produtoDAO.altera(produtoVinculo);
                    produtoDAO.close();
                }
                p.desincroniza();
                produtoDAO.altera(p);
                produtoDAO.close();
            }
            sincronizaObjetosSinkInternos();

            //TENHO QUE PEGAR A DATA MAIS RECENTES DOS OBJETOS por que no pior caso ele vai pegar a data mais antiga
            //ate acabar os elementos, pega um atuliza, pega outro, atualiza
            String versao = objetosSink.getMomentoDaUltimaAtualizacao();

            if (versao != null) {
                objetosSinkPreferences.salvarVersao(versao);

            }
            Log.e("VERSAOproduto", objetosSinkPreferences.getVersao());
//                Toast.makeText(getActivity(), localizacoes.size() + "hj", Toast.LENGTH_SHORT).show();
            //Toast.makeText(getActivity(), g.toJson(response.body()) , Toast.LENGTH_SHORT).show();


            return "";


        }

        @Override
        protected void onPostExecute(String t) {


        }


    }
}
