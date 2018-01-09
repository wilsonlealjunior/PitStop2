package pitstop.com.br.pitstop.sic;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
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
    ProgressDialog progressDialog;

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
        progressDialog = new ProgressDialog(context);
        objetosSinkPreferences = new ObjetosSinkPreferences(context);

        produtoDAO = new ProdutoDAO(context);
        lojaDAO = new LojaDAO(context);
        avariaDAO = new AvariaDAO(context);
        entradaProdutoDAO = new EntradaProdutoDAO(context);
        movimentacaoProdutoDAO = new MovimentacaoProdutoDAO(context);
        vendaDAO = new VendaDAO(context);
        usuarioDAO = new UsuarioDAO(context);
        furoDAO = new FuroDAO(context);
    }

    public void buscaTodos() {
        if (sincronizacaoAtiva == false) {
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
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sincronizando dados");
        progressDialog.show();
        call.enqueue(buscaObjetosSinkCallback());
    }


    private void buscaObjetosSink() {
        Call<ObjetosSink> call = new RetrofitInializador().getObjetosSinkService().listarObjetosSink();
        Log.e("log2versaotodos", String.valueOf(objetosSinkPreferences.getVersao()));

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sincronizando dados");
        progressDialog.show();
        call.enqueue(buscaObjetosSinkCallback());
    }

    @NonNull
    private Callback<ObjetosSink> buscaObjetosSinkCallback() {
        return new Callback<ObjetosSink>() {
            @Override
            public void onResponse(Call<ObjetosSink> call, Response<ObjetosSink> response) {
                ObjetosSink objetosSink = response.body();


                usuarioDAO.sincroniza(objetosSink.getUsuarios());
                lojaDAO.sincroniza(objetosSink.getLojas());
                avariaDAO.sincroniza(objetosSink.getAvarias());
                entradaProdutoDAO.sincroniza(objetosSink.getEntradaProdutos());
                movimentacaoProdutoDAO.sincroniza(objetosSink.getMovimentacaoProdutos());
                vendaDAO.sincroniza(objetosSink.getVendas());
                furoDAO.sincroniza(objetosSink.getFuros());
                lojaDAO.close();
                avariaDAO.close();
                entradaProdutoDAO.close();
                movimentacaoProdutoDAO.close();
                vendaDAO.close();
                usuarioDAO.close();

                //esse trecho de codigo serve para
                // deixar a quantidade consistente
                // se o produto for alterado no
                // servidor e localmente então receberemos
                // todas as entidades principalmente entrada
                // produto que tem as informações da quantidade,
                // os outros dados como nome preço vai ser sempre dando prioridade para o servidor
                List<Produto> produtosAlteradosLocalmente = produtoDAO.listaNaoSincronizados();
                produtoDAO.close();
                Produto produtoPrincipal;
                List<Produto> produtosIncosistente = new ArrayList<>();
                for (Produto produtoLocalAlterado : produtosAlteradosLocalmente) {
                    for (Produto produtoVindoServidor : objetosSink.getProdutos()) {
                        if (produtoLocalAlterado.getId().equals(produtoVindoServidor.getId())) {
                            if (produtoVindoServidor.vinculado()) {
                                produtoPrincipal = produtoDAO.procuraPorId(produtoVindoServidor.getIdProdutoPrincipal());
                                produtoDAO.close();
                                produtoPrincipal.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                                entradaProdutoDAO.close();
                                produtoPrincipal.calcularQuantidade();
                            }
                            else{
                                produtoPrincipal = produtoDAO.procuraPorId(produtoVindoServidor.getId());
                                produtoDAO.close();
                                produtoPrincipal.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                                entradaProdutoDAO.close();
                                produtoPrincipal.calcularQuantidade();
                            }
                            produtosIncosistente.add(produtoPrincipal);
                        }

                    }

                }
                produtoDAO.sincroniza(objetosSink.getProdutos());
                produtoDAO.close();


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


            }

            @Override
            public void onFailure(Call<ObjetosSink> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                Toast.makeText(context, "Houve um erro na sincronização dos produtos", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                sincronizacaoAtiva = false;

            }
        };

    }


    private void sincronizaObjetosSinkInternos() {


        final List<Produto> produtos = produtoDAO.listaNaoSincronizados();
        final List<Loja> lojas = lojaDAO.listaNaoSincronizados();
        final List<Avaria> avarias = avariaDAO.listaNaoSincronizados();
        final List<EntradaProduto> entradaProdutos = entradaProdutoDAO.listaNaoSincronizados();
        final List<MovimentacaoProduto> movimentacaoProdutos = movimentacaoProdutoDAO.listaNaoSincronizados();
        final List<Venda> vendas = vendaDAO.listaNaoSincronizados();
        final List<Usuario> usuarios = usuarioDAO.listaNaoSincronizados();
        final List<Furo> furos = furoDAO.listaNaoSincronizados();
        produtoDAO.close();
        lojaDAO.close();
        avariaDAO.close();
        entradaProdutoDAO.close();
        movimentacaoProdutoDAO.close();
        vendaDAO.close();
        usuarioDAO.close();
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
                ObjetosSink objetosSink = response.body();
                lojaDAO.sincroniza(objetosSink.getLojas());
                lojaDAO.close();
                produtoDAO.sincroniza(objetosSink.getProdutos());
                produtoDAO.close();
                avariaDAO.sincroniza(objetosSink.getAvarias());
                avariaDAO.close();
                movimentacaoProdutoDAO.sincroniza(objetosSink.getMovimentacaoProdutos());
                movimentacaoProdutoDAO.close();
                vendaDAO.sincroniza(objetosSink.getVendas());
                vendaDAO.close();
                entradaProdutoDAO.sincroniza(objetosSink.getEntradaProdutos());
                entradaProdutoDAO.close();
                usuarioDAO.sincroniza(objetosSink.getUsuarios());
                usuarioDAO.close();
                furoDAO.sincroniza(objetosSink.getFuros());
                furoDAO.close();


                String versao = objetosSink.getMomentoDaUltimaAtualizacao();

                if (versao != null) {
                    objetosSinkPreferences.salvarVersao(versao);
                }
                Log.e("VERSAOproduto", objetosSinkPreferences.getVersao());
                bus.post(new AtualizaListaProdutoEvent());
                bus.post(new AtualizaListaLojasEvent());
                progressDialog.dismiss();
                sincronizacaoAtiva = false;

            }


            @Override
            public void onFailure(Call<ObjetosSink> call, Throwable t) {
                progressDialog.dismiss();
                sincronizacaoAtiva = false;

            }

        });

    }
}
