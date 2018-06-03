package pitstop.com.br.pitstop.sic;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
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
import pitstop.com.br.pitstop.model.RealmString;
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
    Realm realm;
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
        //esse produto é comoo o servidor está esperando
        List<ObjetosSink.Produto> produtosAppParaServidor = new ArrayList<>();
        Log.e("Recuperando nao Sinc", "Produtos");
        final List<Produto> produtos = produtoDAO.listaNaoSincronizados();
        produtoDAO.close();
        for (Produto p : produtos) {
            ObjetosSink.Produto produto = new ObjetosSink.Produto();

            produto.id = (p.getId());
            produto.nome = (p.getNome());
            produto.estoqueMinimo = (p.getEstoqueMinimo());
            produto.quantidade = (p.getQuantidade());
            produto.preco = (p.getPreco());
            produto.loja = (p.getLoja());
            produto.sincronizado = (p.getSincronizado());
            produto.idProdutoPrincipal = (p.getIdProdutoPrincipal());
            produto.vinculo = (p.getVinculo());
            for (RealmString idProdutoVinculoadoRealm : p.idProdutoVinculado) {
                produto.idProdutoVinculado.add((idProdutoVinculoadoRealm.getValor()));
            }

            produtosAppParaServidor.add(produto);

        }

        Log.e("Recuperando nao Sinc", "Lojas");
        final List<Loja> lojas = lojaDAO.listaNaoSincronizados();
        lojaDAO.close();
        Log.e("Recuperando nao Sinc", "Avarias");
        final List<Avaria> avarias = avariaDAO.listaNaoSincronizados();
        avariaDAO.close();
        Log.e("Recuperando nao Sinc", "Entrada de Produto");
        List<ObjetosSink.EntradaProduto> entradaProdutosAppParaServidor = new ArrayList<>();
        final List<EntradaProduto> entradaProdutos = entradaProdutoDAO.listaNaoSincronizados();
        for (EntradaProduto ep : entradaProdutos) {
            ObjetosSink.EntradaProduto entradaProduto = new ObjetosSink.EntradaProduto();
            entradaProduto.id = ep.getId();
            entradaProduto.precoDeCompra = ep.getPrecoDeCompra();
            entradaProduto.quantidade = ep.getQuantidade();
            entradaProduto.data = ep.getData();
            entradaProduto.sincronizado = ep.getSincronizado();
            entradaProduto.quantidadeVendidaMovimentada = ep.getQuantidadeVendidaMovimentada();
            entradaProduto.desativado = ep.getDesativado();
            ObjetosSink.Produto produto = new ObjetosSink.Produto();
            produto.id = (ep.getProduto().getId());
            produto.nome = (ep.getProduto().getNome());
            produto.estoqueMinimo = (ep.getProduto().getEstoqueMinimo());
            produto.quantidade = (ep.getProduto().getQuantidade());
            produto.preco = (ep.getProduto().getPreco());
            produto.loja = (ep.getProduto().getLoja());
            produto.sincronizado = (ep.getProduto().getSincronizado());
            produto.idProdutoPrincipal = (ep.getProduto().getIdProdutoPrincipal());
            produto.vinculo = (ep.getProduto().getVinculo());
            for (RealmString idProdutoVinculoadoRealm : ep.getProduto().getIdProdutoVinculado()) {
                produto.idProdutoVinculado.add((idProdutoVinculoadoRealm.getValor()));
            }


            entradaProduto.produto = produto;
            entradaProdutosAppParaServidor.add(entradaProduto);

        }
        entradaProdutoDAO.close();
        Log.e("Recuperando nao Sinc", "Movimentacao de produto");
        final List<MovimentacaoProduto> movimentacaoProdutos = movimentacaoProdutoDAO.listaNaoSincronizados();
        movimentacaoProdutoDAO.close();
        Log.e("Recuperando nao Sinc", "Venda");
        vendaDAO = new VendaDAO(context);
        final List<Venda> vendas = vendaDAO.listaNaoSincronizados();
        vendaDAO.close();
        Log.e("Recuperando nao Sinc", "Usuarios");
        final List<Usuario> usuarios = usuarioDAO.listaNaoSincronizados();
        usuarioDAO.close();
        Log.e("Recuperando nao Sinc", "Furos");
        final List<Furo> furos = furoDAO.listaNaoSincronizados();
        furoDAO.close();

        final ObjetosSink objetosSink = new ObjetosSink();
        objetosSink.setAvarias(avarias);
        objetosSink.setEntradaProdutos(entradaProdutosAppParaServidor);
        objetosSink.setLojas(lojas);
        objetosSink.setMovimentacaoProdutos(movimentacaoProdutos);
        objetosSink.setVendas(vendas);
        objetosSink.setProdutos(produtosAppParaServidor);
        objetosSink.setUsuarios(usuarios);
        objetosSink.setFuros(furos);
        Call<ObjetosSink> call = new RetrofitInializador().getObjetosSinkService().atualiza(objetosSink);

        call.enqueue(new Callback<ObjetosSink>() {
            @Override
            public void onResponse(Call<ObjetosSink> call, Response<ObjetosSink> response) {
                dialog.setMessage("Sincronizando Servidor");

                final ObjetosSink objetosSink = response.body();

                Log.e("Sincronizando", "Lojas");
                lojaDAO = new LojaDAO(context);
                lojaDAO.sincroniza(objetosSink.getLojas());
                lojaDAO.close();
                Log.e("Sincronizando", "Produtos");
                realm = Realm.getDefaultInstance();
                for (ObjetosSink.Produto p : objetosSink.getProdutos()) {
                    Produto produto = realm.where(Produto.class)
                            .equalTo("id", p.id)
                            .findFirst();
                    realm.beginTransaction();
                    produto.sincroniza();
                    realm.commitTransaction();
                }
                realm.close();
                Log.e("Sincronizando", "Avaria");
                avariaDAO = new AvariaDAO(context);
                avariaDAO.sincroniza(objetosSink.getAvarias());
                avariaDAO.close();
                Log.e("Sincronizando", "Movimentacao e produto");
                movimentacaoProdutoDAO = new MovimentacaoProdutoDAO(context);
                movimentacaoProdutoDAO.sincroniza(objetosSink.getMovimentacaoProdutos());
                movimentacaoProdutoDAO.close();
                Log.e("Sincronizando", "Vendas");
                vendaDAO = new VendaDAO(context);
                vendaDAO.sincroniza(objetosSink.getVendas());
                vendaDAO.close();
                realm = Realm.getDefaultInstance();
                Log.e("Sincronizando", "Entrada de Produtos");
                for (ObjetosSink.EntradaProduto ep : objetosSink.getEntradaProdutos()) {
                    EntradaProduto entradaProduto = realm.where(EntradaProduto.class)
                            .equalTo("id", ep.id)
                            .findFirst();
                    realm.beginTransaction();
                    entradaProduto.sincroniza();
                    if (entradaProduto.estaDesativado())
                        entradaProduto.deleteFromRealm();
                    realm.commitTransaction();
                }
                realm.close();
                Log.e("Sincronizando", "Usurios");
                usuarioDAO = new UsuarioDAO(context);
                usuarioDAO.sincroniza(objetosSink.getUsuarios());
                usuarioDAO.close();
                Log.e("Sincronizando", "Furos");
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
                sincronizacaoAtiva = false;


            }


            @Override
            public void onFailure(Call<ObjetosSink> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(context, "Houve um erro na sincronização", Toast.LENGTH_LONG).show();
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
            RealmList<Produto> produtosVindosDoServidor = new RealmList<>();
            for (ObjetosSink.Produto p : objetosSink.getProdutos()) {
                Produto produto = new Produto();

                produto.setId(p.id);
                produto.setNome(p.nome);
                produto.setEstoqueMinimo(p.estoqueMinimo);
                produto.setQuantidade(p.quantidade);
                produto.setPreco(p.preco);
                produto.setLoja(p.loja);
                produto.setSincronizado(p.sincronizado);
                produto.setIdProdutoPrincipal(p.idProdutoPrincipal);
                produto.setVinculo(p.vinculo);

                for (String idProdutoVinculoadoString : p.idProdutoVinculado) {
                    produto.getIdProdutoVinculado().add(new RealmString(idProdutoVinculoadoString));
                }
                produtosVindosDoServidor.add(produto);

            }


            Log.e("Persistindo", "Usuario");
            usuarioDAO = new UsuarioDAO(context);
            usuarioDAO.sincroniza(objetosSink.getUsuarios());
            usuarioDAO.close();
            Log.e("Persistindo", "Loja");
            lojaDAO = new LojaDAO(context);
            lojaDAO.sincroniza(objetosSink.getLojas());
            lojaDAO.close();
            Log.e("Persistindo", "Avaria");
            avariaDAO = new AvariaDAO(context);
            avariaDAO.sincroniza(objetosSink.getAvarias());
            avariaDAO.close();
            Log.e("Persistindo", "Movimentacao de produto");
            movimentacaoProdutoDAO = new MovimentacaoProdutoDAO(context);
            movimentacaoProdutoDAO.sincroniza(objetosSink.getMovimentacaoProdutos());
            movimentacaoProdutoDAO.close();
            Log.e("Persistindo", "Venda");
            vendaDAO = new VendaDAO(context);
            vendaDAO.sincroniza(objetosSink.getVendas());
            vendaDAO.close();
            Log.e("Persistindo", "Furo");
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
            Log.e("Persistindo", "Produto");
            produtoDAO = new ProdutoDAO(context);
            List<Produto> produtosAlteradosLocalmente = new ArrayList<>();
            produtosAlteradosLocalmente.addAll(produtoDAO.listaNaoSincronizados());
            produtoDAO.close();
            produtoDAO.sincroniza(produtosVindosDoServidor);
            produtoDAO.close();
            Log.e("Persistindo", "Entrada de produto");
            entradaProdutoDAO = new EntradaProdutoDAO(context);
            RealmList<EntradaProduto> entradaProdutosVindosDoServidor = new RealmList<>();
            for (ObjetosSink.EntradaProduto ep : objetosSink.getEntradaProdutos()) {
                EntradaProduto entradaProduto = new EntradaProduto();
                entradaProduto.setId(ep.id);
                entradaProduto.setPrecoDeCompra(ep.precoDeCompra);
                entradaProduto.setQuantidade(ep.quantidade);
                entradaProduto.setData(ep.data);

                produtoDAO = new ProdutoDAO(context);
                Produto produto = produtoDAO.procuraPorId(ep.produto.id);
                if (produto == null) {
                    produto = new Produto();
                    produto.setId(ep.produto.id);
                }
                produto.setNome(ep.produto.nome);
                produto.setEstoqueMinimo(ep.produto.estoqueMinimo);
                produto.setQuantidade(ep.produto.quantidade);
                produto.setPreco(ep.produto.preco);
                produto.setLoja(ep.produto.loja);
                produto.setSincronizado(ep.produto.sincronizado);
                produto.setIdProdutoPrincipal(ep.produto.idProdutoPrincipal);
                produto.setVinculo(ep.produto.vinculo);
                for (String idProdutoVinculoadoString : ep.produto.idProdutoVinculado) {
                    produto.idProdutoVinculado.add(new RealmString(idProdutoVinculoadoString));
                }
                entradaProduto.setProduto(produto);
                entradaProduto.setSincronizado(ep.sincronizado);

                entradaProduto.setQuantidadeVendidaMovimentada(ep.quantidadeVendidaMovimentada);
                entradaProduto.setDesativado(ep.desativado);

                produto.getEntradaProdutos().add(entradaProduto);
                produtoDAO.altera(produto);
                produtoDAO.close();
                entradaProdutosVindosDoServidor.add(entradaProduto);
            }
            entradaProdutoDAO.sincroniza(entradaProdutosVindosDoServidor);
            entradaProdutoDAO.close();
            Produto produtoPrincipal;
            //TODO fazer testes testando esse algoritmo
            List<Produto> produtosIncosistente = new ArrayList<>();
            for (Produto produtoLocalAlterado : produtosAlteradosLocalmente) {
                for (Produto produtoVindoServidor : produtosVindosDoServidor) {
                    if (produtoLocalAlterado.getId().equals(produtoVindoServidor.getId())) {
                        if (produtoVindoServidor.vinculado()) {
                            produtoPrincipal = produtoDAO.procuraPorId(produtoVindoServidor.getIdProdutoPrincipal());
                            produtoDAO.close();
//                            produtoPrincipal.getEntradaProdutos().addAll(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                            entradaProdutoDAO.close();
                            produtoPrincipal.calcularQuantidade();
                        } else {
                            produtoPrincipal = produtoDAO.procuraPorId(produtoVindoServidor.getId());
                            produtoDAO.close();
//                            produtoPrincipal.getEntradaProdutos().addAll(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                            entradaProdutoDAO.close();
                            produtoPrincipal.calcularQuantidade();
                        }
                        produtosIncosistente.add(produtoPrincipal);
                    }

                }

            }

            for (Produto p : produtosIncosistente) {
                for (RealmString produtoVinculoId : p.getIdProdutoVinculado()) {
                    Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId.getValor());
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
