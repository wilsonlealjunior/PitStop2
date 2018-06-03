package pitstop.com.br.pitstop.activity.avaria;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.activity.BaseCadastroDeTransacaoDeProdutoActivity;
import pitstop.com.br.pitstop.activity.venda.RelatorioVendasActivity;
import pitstop.com.br.pitstop.adapter.NonScrollListView;
import pitstop.com.br.pitstop.assyncTask.CarregarListaDeProdutoTask;
import pitstop.com.br.pitstop.dao.AvariaDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.event.AtualizarGraficos;
import pitstop.com.br.pitstop.event.CarregaListaDeProduto;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.ItemAvaria;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.RealmString;
import pitstop.com.br.pitstop.model.Venda;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

public class CadastroAvariaActivity extends BaseCadastroDeTransacaoDeProdutoActivity {


    Spinner spinnerLoja;
    ProgressBar progressBarProduto;
    List<Avaria> carrinho = new ArrayList<>();
    List<String> labelsLoja = new ArrayList<>();
    List<Loja> lojas = new ArrayList<>();
    UsuarioPreferences usuarioPreferences;
    List<Produto> todoProdutos = new ArrayList<>();
    Button adicionarAvaria;
    private NonScrollListView listaViewDeAvariasCarrinho;
    LstViewTabelaCarrinhoAvaria adapterTableCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_avaria);

        loadView();
        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        if (lojas.size() == 0) {
            Toast.makeText(getApplicationContext(), "Não existe lojas cadastradas", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        for (Loja loja : lojas) {
            labelsLoja.add(loja.getNome());
        }

        CarregarListaDeProdutoTask carregarListaDeProdutoTask = new CarregarListaDeProdutoTask(this, null, todoProdutos);
        carregarListaDeProdutoTask.execute();
        //        todoProdutos = produtoDAO.listarProdutos();
//        produtoDAO.close();
//        if (todoProdutos.size() == 0) {
//            Toast.makeText(getApplicationContext(), "Não existe Produtos cadastrados", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
        //setupView();
    }

    public void setupView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadatrar Avaria");     //Titulo para ser exibido na sua Action Bar em frente à seta

        //inicializando atributos


        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_carrinho_avaria, listaViewDeAvariasCarrinho, false);
        listaViewDeAvariasCarrinho.addHeaderView(headerView);
        registerForContextMenu(listaViewDeAvariasCarrinho);
        adapterTableCarrinho = new LstViewTabelaCarrinhoAvaria(this, R.layout.tabela_carrinho_avaria, R.id.produto, carrinho);
        listaViewDeAvariasCarrinho.setAdapter(adapterTableCarrinho);
        //configurando spinner de loja
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLoja);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoja.setAdapter(spinnerAdapter);
        spinnerLoja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //toda vez que é selecionado uma loja a lista de produtos é recarregada, então no campo produto é setado como vazio
                loja = lojas.get(i);
                produtos.clear();
                for (Produto p : todoProdutos) {
                    if (p.getLoja().getId().equals(loja.getId())) {
                        produtos.add(p);
                    }
                }
                campoProduto.setText("");
                produto = null;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        adicionarAvaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValid()) {
                    return;
                }

                Avaria avaria = new Avaria();
                avaria.setId(UUID.randomUUID().toString());
                avaria.setIdLoja(loja.getId());
                avaria.setIdProduto(produto.getId());
                avaria.setQuantidade(Integer.valueOf(campoQuantidade.getText().toString()));
                carrinho.add(avaria);
                if (produto.vinculado()) {
                    for (Produto produtosDaListView : produtos) {
                        if (produto.getIdProdutoPrincipal().equals(produtosDaListView.getId())) {
                            produtoPrincipal = produtosDaListView;
                            break;
                        }
                    }
                } else {
                    produtoPrincipal = produto;
                }
                //dando baixa nos produtos da listView e nos seus vinculos
                produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - avaria.getQuantidade());
                for (RealmString produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                    for (Produto produtoDaListView : produtos) {
                        if (produtoDaListView.getId().equals(produtoVinculoId.getValor())) {
                            produtoDaListView.setQuantidade(produtoDaListView.getQuantidade() - avaria.getQuantidade());
                            break;
                        }
                    }
                }
                adapterTableCarrinho.notifyDataSetChanged();

            }
        });


        super.setupView();
    }

    public void loadView() {
        bus.register(this);
        spinnerLoja = (Spinner) findViewById(R.id.spinner);
        usuarioPreferences = new UsuarioPreferences(this);
        adicionarAvaria = (Button) findViewById(R.id.adicionar);
        listaViewDeAvariasCarrinho = (NonScrollListView) findViewById(R.id.lista_de_avaria);
        adapterTableCarrinho = new LstViewTabelaCarrinhoAvaria(this, R.layout.tabela_carrinho_avaria, R.id.produto, carrinho);
        super.loadView();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void chamarSetupView(CarregaListaDeProduto event) {
        setupView();
    }


    public boolean isValid() {
        hideKeyboard(this, getCurrentFocus());

        if (campoProduto.getText().toString().isEmpty()) {
            campoProduto.setError("Escolha um produto");
            campoProduto.requestFocus();
            snackbar.setText("Escolha um Produto");
            snackbar.show();
//            Toast.makeText(CadastroAvariaActivity.this, "Escolha um Produto", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (campoQuantidade.getText().length() == 0) {
            campoQuantidade.setError("Digite uma quantidade");
            campoQuantidade.requestFocus();
            return false;
//                    Toast.makeText(CadastrarVendasActivity.this, "Digite a Quantidade", Toast.LENGTH_SHORT).show();
        }
        int quantidadeComprada = Integer.valueOf(campoQuantidade.getText().toString());
        if (quantidadeComprada == 0) {
            campoQuantidade.setError("Digite uma quantidade maior que zero");
            campoQuantidade.requestFocus();
            return false;
        }
        if (quantidadeComprada > produto.getQuantidade()) {
            snackbar.setText("Quantidade informada maior do que o estoque do Produto");
            snackbar.show();
//            Toast.makeText(CadastroAvariaActivity.this, "Quantidade informada maior do que o estoque do Produto", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    final ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                if (info.position != 0) {
                    Avaria avaria = (Avaria) listaViewDeAvariasCarrinho.getItemAtPosition(info.position);
                    Produto produtoFuro = null;

                    for (Produto produto : todoProdutos) {
                        if (produto.getId().equals(avaria.getIdProduto())) {
                            produtoFuro = produto;
                            break;
                        }
                    }

                    //aqui pegaremos o produto principal na listView e daremos baixa em todos os seus vinculos
                    if (produtoFuro.vinculado()) {
                        for (Produto p : todoProdutos) {
                            if (produtoFuro.getIdProdutoPrincipal().equals(p.getId())) {
                                produtoPrincipal = p;
                                break;
                            }
                        }
                    } else {
                        produtoPrincipal = produtoFuro;
                    }

                    produtoPrincipal.entrada(avaria.getQuantidade());

                    for (RealmString produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                        for (Produto produtoDaListView : produtos) {
                            if (produtoDaListView.getId().equals(produtoVinculoId.getValor())) {
                                produtoDaListView.entrada(avaria.getQuantidade());
                                break;
                            }
                        }
                    }

                    campoProduto.setText("");
                    campoQuantidade.setText("0");
                    if (carrinho.remove(avaria)) {
                        snackbar.setText(" removido do carrinho");
                        snackbar.show();
//                        Toast.makeText(CadastroMovimentacaoProdutoActivity.this, " removido do carrinho", Toast.LENGTH_SHORT).show();
                        listaViewDeAvariasCarrinho.setAdapter(adapterTableCarrinho);
                        adapterTableCarrinho.notifyDataSetChanged();
                    }
                }


                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cadastro_ok:
                item.setVisible(false);
                if (carrinho.size() == 0) {
                    item.setVisible(true);
                    snackbar.setText("Não existe produtos no carrinho");
                    snackbar.show();
//                    Toast.makeText(CadastrarVendasActivity.this, "Não existe produtos no carrinho", Toast.LENGTH_SHORT).show();
                    break;
                }
                Util.alert(CadastroAvariaActivity.this, "Confirmação de Avaria", "Deseja Confirmar a Avaria ?", "Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String dataAtual = Util.dataNoformatoDoSQLite(new Date());
                        for (Avaria avaria : carrinho) {
                            avaria.setData(new Date());
                            Produto produto = produtoDAO.procuraPorId(avaria.getIdProduto());
                            produtoDAO.close();
                            if (produto.vinculado()) {
                                produtoPrincipal = produtoDAO.procuraPorId(produto.getIdProdutoPrincipal());
                                produtoDAO.close();
                            } else {
                                produtoPrincipal = produtoDAO.procuraPorId(produto.getId());
                                produtoDAO.close();
                            }

                            int saida = Integer.valueOf(avaria.getQuantidade());

//                            produtoPrincipal.getEntradaProdutos().addAll(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                            entradaProdutoDAO.close();
                            for (EntradaProduto entradaProduto : produtoPrincipal.getEntradaProdutos()) {
                                int quantidadeDisponivel = (entradaProduto.getQuantidade() - entradaProduto.getQuantidadeVendidaMovimentada());
                                if ((saida <= quantidadeDisponivel)) {
                                    entradaProduto.setQuantidadeVendidaMovimentada(entradaProduto.getQuantidadeVendidaMovimentada() + saida);
                                    produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - saida);
                                    entradaProduto.desincroniza();
                                    entradaProdutoDAO.altera(entradaProduto);
                                    entradaProdutoDAO.close();


                                    ItemAvaria itemAvaria = new ItemAvaria();
                                    itemAvaria.setId(UUID.randomUUID().toString());
                                    itemAvaria.setQuantidade(saida);
                                    itemAvaria.setIdAvaria(avaria.getId());
                                    itemAvaria.setIdEntradaProduto(entradaProduto.getId());
                                    //itemAvaria.sincroniza();
                                    avaria.setPrejuizo(avaria.getPrejuizo() + entradaProduto.getPrecoDeCompra() * Integer.valueOf(saida));

                                    avaria.getAvariaEntradeProdutos().add(itemAvaria);


                                    break;
                                } else {
                                    if (quantidadeDisponivel == 0) {
                                        continue;
                                    } else {
                                        saida = saida - (quantidadeDisponivel);
                                        entradaProduto.setQuantidadeVendidaMovimentada(entradaProduto.getQuantidadeVendidaMovimentada() + (quantidadeDisponivel));
                                        //é para sair quantidade disponivel
                                        produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - quantidadeDisponivel);
                                        entradaProduto.desincroniza();
                                        entradaProdutoDAO.altera(entradaProduto);
                                        entradaProdutoDAO.close();

                                        ItemAvaria itemAvaria = new ItemAvaria();
                                        itemAvaria.setId(UUID.randomUUID().toString());
                                        itemAvaria.setQuantidade(quantidadeDisponivel);
                                        itemAvaria.setIdAvaria(avaria.getId());
                                        itemAvaria.setIdEntradaProduto(entradaProduto.getId());
                                        //itemAvaria.sincroniza();
                                        avaria.setPrejuizo(avaria.getPrejuizo() + entradaProduto.getPrecoDeCompra() * Integer.valueOf(quantidadeDisponivel));

                                        avaria.getAvariaEntradeProdutos().add(itemAvaria);
                                    }
                                }

                            }
                            for (RealmString produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                                Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId.getValor());
                                produtoDAO.close();
                                produtoVinculo.setQuantidade(produtoVinculo.getQuantidade() - avaria.getQuantidade());
                                produtoVinculo.desincroniza();
                                produtoDAO.altera(produtoVinculo);
                                produtoDAO.close();
                            }

                            produtoPrincipal.desincroniza();
                            produtoDAO.altera(produtoPrincipal);
                            produtoDAO.close();


                            AvariaDAO avariaDAO = new AvariaDAO(getApplicationContext());
                            avariaDAO.insere(avaria);

                            avariaDAO.close();

                        }


                        //AvariaSincronizador avariaSincronizador = new AvariaSincronizador(this);
                        //avariaSincronizador.buscaTodos();


                        bus.post(new AtualizaListaProdutoEvent());
                        bus.post(new AtualizaListaLojasEvent());
                        bus.post(new AtualizarGraficos());


                        finish();
                    }
                }, "Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        item.setVisible(true);

                    }
                }, null, null);
                break;


        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        // Unregister
        bus.unregister(this);
        super.onDestroy();
    }


}
