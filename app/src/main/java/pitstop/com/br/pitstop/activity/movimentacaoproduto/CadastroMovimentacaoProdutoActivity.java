package pitstop.com.br.pitstop.activity.movimentacaoproduto;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.BaseCadastroDeTransacaoDeProdutoActivity;
import pitstop.com.br.pitstop.adapter.NonScrollListView;
import pitstop.com.br.pitstop.assyncTask.CarregarListaDeProdutoTask;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.event.CarregaListaDeProduto;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.MovimentacaoProduto;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.RealmString;

public class CadastroMovimentacaoProdutoActivity extends BaseCadastroDeTransacaoDeProdutoActivity {

    Spinner spinnerDe;
    Spinner spinnerPara;
    Button adicionarProduto;
    List<String> labelsLojas = new ArrayList<>();
    List<Loja> lojas = new ArrayList<>();
    List<Produto> todoProdutos = new ArrayList<>();
    List<MovimentacaoProduto> carinho = new ArrayList<>();
    Produto produtoPara = new Produto();
    Loja lojaDe = new Loja();
    Loja lojaPara = new Loja();
    private NonScrollListView listaViewDeProdutos;
    LstViewTabelaMovimentacaoAdapter adapterTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_movimentacao_produto);

        spinnerDe = (Spinner) findViewById(R.id.spinnerDe);
        spinnerPara = (Spinner) findViewById(R.id.spinnerPara);
        adicionarProduto = (Button) findViewById(R.id.adicionar_produto);

        loadView();
        lojas = lojaDAO.listarLojas();
        lojaDAO.close();

        if (lojas.size() < 2) {
            Toast.makeText(getApplicationContext(), "Não existe lojas suficientes cadastradas para movimentar produtos", Toast.LENGTH_LONG).show();
            finish();
            return;

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
//
//        setupView();
    }

    public void setupView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        //getSupportActionBar().setTitle("Seu titulo aqui");     //Titulo para ser exibido na sua Action Bar em frente à seta
        toolbar.setTitle("Movimentação de Produto");


        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_movimentacao_produto, listaViewDeProdutos, false);
        listaViewDeProdutos.addHeaderView(headerView);
        registerForContextMenu(listaViewDeProdutos);
        adapterTable = new LstViewTabelaMovimentacaoAdapter(this, R.layout.tabela_movimentacao_produto, R.id.produto, carinho);
        listaViewDeProdutos.setAdapter(adapterTable);
        for (Loja loja : lojas) {
            labelsLojas.add(loja.getNome());
        }
        lojaDe = lojas.get(0);
        ArrayAdapter<String> spinnerAdapterDe = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLojas);


        spinnerAdapterDe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDe.setAdapter(spinnerAdapterDe);
        spinnerDe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lojaDe = lojas.get(i);
                produtos.clear();
                for (Produto p : todoProdutos) {
                    if (p.getLoja().getId().equals(lojaDe.getId())) {
                        produtos.add(p);
                    }
                }
//                produtos = produtoDAO.procuraPorLoja(lojaDe);
//                Collections.sort(produtos);
                campoProduto.setText("");
                produto = null;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lojaPara = lojas.get(0);
        ArrayAdapter<String> spinnerAdapterPara = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLojas);

        spinnerAdapterPara.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPara.setAdapter(spinnerAdapterPara);
        spinnerPara.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lojaPara = lojas.get(i);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        adicionarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValid()) {
                    return;
                }
                int quantidademovimentada = Integer.valueOf(campoQuantidade.getText().toString());

                //como estamos fazendo operações sem salvar no banco então pegamos o produto principal na lista de produtos
                //que é usado na listView para termos o controle do que pode ser movimentado, para não movimentar mais do que
                //está disponivel no estoque

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

                MovimentacaoProduto movProd = new MovimentacaoProduto();
                movProd.setId(UUID.randomUUID().toString());
                movProd.setIdLojaDe(lojaDe.getId());
                movProd.setIdLojaPara(lojaPara.getId());
                movProd.setQuantidade(quantidademovimentada);
                movProd.setIdProduto(produtoPrincipal.getId());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                movProd.setData((new Date()));

                carinho.add(movProd);
                hideKeyboard(CadastroMovimentacaoProdutoActivity.this,getCurrentFocus());
                snackbar.setText("Produto " + produtoPrincipal.getNome() + " adicionado ao carrinho");
                snackbar.show();
//                Toast toast = Toast.makeText(CadastroMovimentacaoProdutoActivity.this, "Produto " + produtoPrincipal.getNome() + " adicionado ao carrinho", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();

                produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - quantidademovimentada);
                for (RealmString produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                    for (Produto produtoDaListView : produtos) {
                        if (produtoDaListView.getId().equals(produtoVinculoId.getValor())) {
                            produtoDaListView.setQuantidade(produtoDaListView.getQuantidade() - quantidademovimentada);
                            break;
                        }
                    }
                }
                adapterTable.notifyDataSetChanged();


            }
        });
        super.setupView();


    }

    public void loadView() {
        bus.register(this);
        listaViewDeProdutos = (NonScrollListView) findViewById(R.id.lista_de_produto);

        super.loadView();

    }


    public boolean isValid() {
        if (campoProduto.getText().toString().isEmpty()) {
            campoProduto.setError("escolha um produto");
            campoProduto.requestFocus();
            snackbar.setText("Escolha um Produto");
            snackbar.show();
//            Toast.makeText(CadastroMovimentacaoProdutoActivity.this, "Escolha um Produto", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (campoQuantidade.getText().toString().isEmpty()) {
            campoQuantidade.setError("Informe uma quantidade");
            campoQuantidade.requestFocus();
//            Toast.makeText(CadastroMovimentacaoProdutoActivity.this, "Digite a quantidade", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (campoQuantidade.getText().length() == 0) {
            campoQuantidade.setError("informe uma quantidade");
            campoQuantidade.requestFocus();
//            Toast.makeText(CadastroMovimentacaoProdutoActivity.this, "Digite a quantidade", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (lojaDe.getId().equals(lojaPara.getId())) {
            snackbar.setText("Origem e destino não podem ser o mesmo");
            snackbar.show();
//            Toast.makeText(CadastroMovimentacaoProdutoActivity.this, "Origem e destino não podem ser o mesmo", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Log.e("Quantidade", String.valueOf(produto.getQuantidade()));
        final int quantidademovimentada = Integer.valueOf(campoQuantidade.getText().toString());
        if (quantidademovimentada == 0) {
            campoQuantidade.setError("Digite uma quantidade maior que zero");
            campoQuantidade.requestFocus();
            return false;
        }
        if (quantidademovimentada > produto.getQuantidade()) {
            Toast.makeText(CadastroMovimentacaoProdutoActivity.this, "Quantidade informada maior do que o estoque do Produto", Toast.LENGTH_SHORT).show();
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
                    MovimentacaoProduto movimentacaoProduto = (MovimentacaoProduto) listaViewDeProdutos.getItemAtPosition(info.position);
                    Produto produtoMovimentado = null;

                    for (Produto produto : todoProdutos) {
                        if (produto.getId().equals(movimentacaoProduto.getIdProduto())) {
                            produtoMovimentado = produto;
                            break;
                        }
                    }

                    //aqui pegaremos o produto principal na listView e daremos baixa em todos os seus vinculos
                    if (produtoMovimentado.vinculado()) {
                        for (Produto p : todoProdutos) {
                            if (produtoMovimentado.getIdProdutoPrincipal().equals(p.getId())) {
                                produtoPrincipal = p;
                                break;
                            }
                        }
                    } else {
                        produtoPrincipal = produtoMovimentado;
                    }

                    produtoPrincipal.entrada(movimentacaoProduto.getQuantidade());

                    for (RealmString produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                        for (Produto produtoDaListView : produtos) {
                            if (produtoDaListView.getId().equals(produtoVinculoId.getValor())) {
                                produtoDaListView.entrada(movimentacaoProduto.getQuantidade());
                                break;
                            }
                        }
                    }

                    campoProduto.setText("");
                    campoQuantidade.setText("0");
                    if (carinho.remove(movimentacaoProduto)) {
                        snackbar.setText(" removido do carrinho");
                        snackbar.show();
//                        Toast.makeText(CadastroMovimentacaoProdutoActivity.this, " removido do carrinho", Toast.LENGTH_SHORT).show();
                        listaViewDeProdutos.setAdapter(adapterTable);
                        adapterTable.notifyDataSetChanged();
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
                if (carinho.size() == 0) {
                    item.setVisible(true);
                    snackbar.setText("Não existe produtos no carrinho");
                    snackbar.show();
//                    Toast.makeText(CadastroMovimentacaoProdutoActivity.this, "Não existe produtos no carrinho", Toast.LENGTH_SHORT).show();
                    break;
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroMovimentacaoProdutoActivity.this, R.style.DialogTheme);
                LayoutInflater inflater = CadastroMovimentacaoProdutoActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.card_avisos, null);
                dialogBuilder.setView(dialogView);

                TextView titulo = (TextView) dialogView.findViewById(R.id.titulo);
                TextView mensagem = (TextView) dialogView.findViewById(R.id.mensagem);
                Button positivo = (Button) dialogView.findViewById(R.id.positivo);
                Button negativo = (Button) dialogView.findViewById(R.id.negativo);
                mensagem.setText("Desejar confirmar o cadastro da movimentação?");
                titulo.setText("Confirmação de cadastro");

                final AlertDialog alertDialog = dialogBuilder.create();
                Window window = alertDialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER); // set alert dialog in center
                alertDialog.setCancelable(false);

                negativo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.setVisible(true);
                        alertDialog.hide();
                        alertDialog.dismiss();
                    }
                });
                positivo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        for (MovimentacaoProduto movimentacaoProduto : carinho) {
                            Produto p = produtoDAO.procuraPorId(movimentacaoProduto.getIdProduto());
                            produtoDAO.close();
                            if (p.vinculado()) {
                                produtoPrincipal = produtoDAO.procuraPorId(p.getIdProdutoPrincipal());
                                produtoDAO.close();
                            } else {
                                produtoPrincipal = produtoDAO.procuraPorId(p.getId());
                                produtoDAO.close();
                            }

                            produto = produtoPrincipal;
                            produtoPara = produtoDAO.procuraPorNomeELoja(produtoPrincipal.getNome(), lojaDAO.procuraPorId(movimentacaoProduto.getIdLojaPara()));
                            produtoDAO.close();
                            lojaDAO.close();
                            if (produtoPara == null) {
                                produtoPara = new Produto();
                                produtoPara.setNome(produto.getNome());
                                produtoPara.setLoja(lojaPara);
                                produtoPara.setPreco(produto.getPreco());
                                produtoPara.setQuantidade(0);
                                produtoPara.setId(UUID.randomUUID().toString());
                                produtoDAO.insere(produtoPara);
                                produtoDAO.close();
                            }
//                            produto.getEntradaProdutos().addAll(entradaProdutoDAO.procuraTodosDeUmProduto(produto));
                            entradaProdutoDAO.close();
                            //produto.calcularQuantidade();

                            int saida = movimentacaoProduto.getQuantidade();
                            for (EntradaProduto ep : produto.getEntradaProdutos()) {
                                int quantidadeDisponivel = (ep.getQuantidade() - ep.getQuantidadeVendidaMovimentada());
                                if ((saida <= quantidadeDisponivel)) {
                                    ep.setQuantidadeVendidaMovimentada(ep.getQuantidadeVendidaMovimentada() + saida);
                                    produto.setQuantidade(produto.getQuantidade() - saida);
                                    ep.desincroniza();
                                    entradaProdutoDAO.altera(ep);
                                    entradaProdutoDAO.close();

                                    EntradaProduto entradaProduto = new EntradaProduto();
                                    entradaProduto.setId(UUID.randomUUID().toString());
                                    entradaProduto.setData((new Date()));
                                    entradaProduto.setProduto(produtoPara);
                                    entradaProduto.setPrecoDeCompra(ep.getPrecoDeCompra());
                                    entradaProduto.setQuantidade(saida);
                                    entradaProduto.desincroniza();
                                    entradaProdutoDAO.insere(entradaProduto);
                                    entradaProdutoDAO.close();
                                    produtoPara.getEntradaProdutos().add(entradaProduto);


                                    break;
                                } else {
                                    if (quantidadeDisponivel == 0) {
                                        continue;
                                    } else {
                                        saida = saida - (quantidadeDisponivel);
                                        ep.setQuantidadeVendidaMovimentada(ep.getQuantidadeVendidaMovimentada() + (quantidadeDisponivel));
                                        produto.setQuantidade(produto.getQuantidade() - quantidadeDisponivel);
                                        ep.desincroniza();
                                        entradaProdutoDAO.altera(ep);
                                        entradaProdutoDAO.close();
                                        EntradaProduto entradaProduto = new EntradaProduto();
                                        entradaProduto.setId(UUID.randomUUID().toString());
                                        entradaProduto.setData((new Date()));
                                        entradaProduto.setProduto(produtoPara);
                                        entradaProduto.setPrecoDeCompra(ep.getPrecoDeCompra());
                                        entradaProduto.setQuantidade(quantidadeDisponivel);
                                        entradaProduto.desincroniza();
                                        entradaProdutoDAO.insere(entradaProduto);
                                        entradaProdutoDAO.close();
                                        produtoPara.getEntradaProdutos().add(entradaProduto);
                                    }
                                }


                            }
                            for (RealmString produtoVinculoId : produto.getIdProdutoVinculado()) {
                                Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId.getValor());
                                produtoDAO.close();
                                produtoVinculo.setQuantidade(produtoVinculo.getQuantidade() - movimentacaoProduto.getQuantidade());
                                produtoVinculo.desincroniza();
                                produtoDAO.altera(produtoVinculo);
                                produtoDAO.close();
                            }

                            for (RealmString produtoVinculoId : produtoPara.getIdProdutoVinculado()) {
                                Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId.getValor());
                                produtoDAO.close();
                                produtoVinculo.setQuantidade(produtoVinculo.getQuantidade() + movimentacaoProduto.getQuantidade());
                                produtoVinculo.desincroniza();
                                produtoDAO.altera(produtoVinculo);
                                produtoDAO.close();
                            }

                            movimentacaoProduto.desincroniza();

                            movimentacaoProdutoDAO.insere(movimentacaoProduto);
                            movimentacaoProdutoDAO.close();

                            produto.desincroniza();
                            produtoDAO.altera(produto);
                            produtoDAO.close();


                            produtoPara.entrada(movimentacaoProduto.getQuantidade());
                            produtoPara.desincroniza();
                            produtoDAO.altera(produtoPara);
                            produtoDAO.close();


                        }

                        //MovimentacaoProdutoSincronizador movimentacaoProdutoSincronizador = new MovimentacaoProdutoSincronizador(this);
                        //ProdutoSincronizador produtoSincronizador = new ProdutoSincronizador(this);
                        // produtoSincronizador.buscaTodos();
                        //movimentacaoProdutoSincronizador.buscaTodos();

                        bus.post(new AtualizaListaProdutoEvent());
                        bus.post(new AtualizaListaLojasEvent());


                        alertDialog.hide();
                        alertDialog.dismiss();
                        finish();
                    }
                });

                alertDialog.show();
                break;


        }

        return super.onOptionsItemSelected(item);

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void chamarSetupView(CarregaListaDeProduto event) {
        setupView();
    }


    @Override
    public void onDestroy() {
        // Unregister
        bus.unregister(this);
        super.onDestroy();
    }

}
