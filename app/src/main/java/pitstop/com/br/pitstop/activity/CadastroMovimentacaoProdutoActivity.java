package pitstop.com.br.pitstop.activity;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.adapter.AdpterProdutoPersonalizado;
import pitstop.com.br.pitstop.adapter.LstViewTabelaMovimentacaoAdapter;
import pitstop.com.br.pitstop.adapter.NonScrollListView;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.MovimentacaoProdutoDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.MovimentacaoProduto;
import pitstop.com.br.pitstop.model.Produto;

public class CadastroMovimentacaoProdutoActivity extends AppCompatActivity {
    TextView campoProduto;
    EditText campoQuantidade;
    Snackbar snackbar;
    LinearLayout linearLayoutRootCadastroMovimentacao;
    List<Produto> pesquisa = new ArrayList<>();
    List<Produto> produtos = new ArrayList<>();
    private Toolbar toolbar;
    ProdutoDAO produtoDAO;

    Spinner spinnerDe;
    Spinner spinnerPara;

    boolean prosseguir = false;

    EditText precoDeCompra;

    Button adicionarProduto;

    List<String> labelsLojas = new ArrayList<>();
    List<Loja> lojas = new ArrayList<>();

    List<Produto> todoProdutos = new ArrayList<>();

    List<MovimentacaoProduto> carinho = new ArrayList<>();
    Produto produtoDe = new Produto();
    Produto produtoPara = new Produto();
    Loja lojaDe = new Loja();
    Loja lojaPara = new Loja();

    private NonScrollListView listaViewDeProdutos;
    LstViewTabelaMovimentacaoAdapter adapterTable;
    LojaDAO lojaDAO;
    Produto produtoPrincipal;
    // private EntradaProdutoSincronizador entradaProdutoSincronizador;
    // private ProdutoSincronizador produtoSincronizador;
    EventBus bus = EventBus.getDefault();
    EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(this);
    MovimentacaoProdutoDAO movimentacaoProdutoDAO = new MovimentacaoProdutoDAO(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_movimentacao_produto);

        spinnerDe = (Spinner) findViewById(R.id.spinnerDe);
        spinnerPara = (Spinner) findViewById(R.id.spinnerPara);
        campoProduto = (TextView) findViewById(R.id.produto);
        campoQuantidade = (EditText) findViewById(R.id.quantidade);
        adicionarProduto = (Button) findViewById(R.id.adicionar_produto);
        linearLayoutRootCadastroMovimentacao = (LinearLayout) findViewById(R.id.ll_root_cadastro_movimentacao);
        snackbar = Snackbar.make(linearLayoutRootCadastroMovimentacao, "", Snackbar.LENGTH_LONG);

        lojaDAO = new LojaDAO(this);
        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        produtoDAO = new ProdutoDAO(this);
        produtoDAO.close();
        if (lojas.size() < 2) {
            Toast.makeText(getApplicationContext(), "Não existe usuarios suficientes cadastradas para movimentar produtos", Toast.LENGTH_LONG).show();
            finish();
            return;

        }
//        produtos = produtoDAO.procuraPorLoja(lojas.get(0));
//        produtoDAO.close();
        todoProdutos = produtoDAO.listarProdutos();
        if (todoProdutos.size() == 0) {
            Toast.makeText(getApplicationContext(), "Não existe Produtos cadastrados", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        //getSupportActionBar().setTitle("Seu titulo aqui");     //Titulo para ser exibido na sua Action Bar em frente à seta
        toolbar.setTitle("Movimentação de Produto");

        listaViewDeProdutos = (NonScrollListView) findViewById(R.id.lista_de_produto);
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
                produtoDe = null;

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

                if (produtoDe.vinculado()) {
                    for (Produto produtosDaListView : produtos) {
                        if (produtoDe.getIdProdutoPrincipal().equals(produtosDaListView.getId())) {
                            produtoPrincipal = produtosDaListView;
                            break;
                        }
                    }
                } else {
                    produtoPrincipal = produtoDe;
                }

                MovimentacaoProduto movProd = new MovimentacaoProduto();
                movProd.setId(UUID.randomUUID().toString());
                movProd.setIdLojaDe(lojaDe.getId());
                movProd.setIdLojaPara(lojaPara.getId());
                movProd.setQuantidade(quantidademovimentada);
                movProd.setIdProduto(produtoPrincipal.getId());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                movProd.setData(formatter.format(new Date()));

                carinho.add(movProd);

                snackbar.setText("Produto " + produtoPrincipal.getNome() + " adicionado ao carrinho");
                snackbar.show();
//                Toast toast = Toast.makeText(CadastroMovimentacaoProdutoActivity.this, "Produto " + produtoPrincipal.getNome() + " adicionado ao carrinho", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();

                produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - quantidademovimentada);
                for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                    for (Produto produtoDaListView : produtos) {
                        if (produtoDaListView.getId().equals(produtoVinculoId)) {
                            produtoDaListView.setQuantidade(produtoDaListView.getQuantidade() - quantidademovimentada);
                            break;
                        }
                    }
                }
                adapterTable.notifyDataSetChanged();


            }
        });


        campoProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCustomDialogwithList();

            }
        });

        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        snackbar.setActionTextColor(Color.RED);


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
        if (quantidademovimentada > produtoDe.getQuantidade()) {
            Toast.makeText(CadastroMovimentacaoProdutoActivity.this, "Quantidade informada maior do que o estoque do Produto", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void ShowCustomDialogwithList() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroMovimentacaoProdutoActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = CadastroMovimentacaoProdutoActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_personalizado_lstview, null);
        dialogBuilder.setView(dialogView);

        final ListView listView = (ListView) dialogView.findViewById(R.id.listview);
        SearchView pesquisaDialog = (SearchView) dialogView.findViewById(R.id.pesquisa);

        final AdpterProdutoPersonalizado adapterPesquisa = new AdpterProdutoPersonalizado(pesquisa, this);
        pesquisaDialog.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                pesquisar(searchQuery.toString().trim());


                listView.setAdapter(adapterPesquisa);

//                textPesquisa.invalidate();
                return true;
            }
        });


        // Defined Array values to show in ListView

        AdpterProdutoPersonalizado adapterp = new AdpterProdutoPersonalizado(produtos, this);

        listView.setAdapter(adapterp);


        final AlertDialog alertDialog = dialogBuilder.create();
        Window window = alertDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER); // set alert dialog in center
        alertDialog.setCancelable(false);
        // window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL); // set alert dialog in Bottom


        // Cancel Button
        Button cancel_btn = (Button) dialogView.findViewById(R.id.buttoncancellist);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.hide();
                alertDialog.dismiss();
            }
        });


        alertDialog.show();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                produtoDe = (Produto) listView.getItemAtPosition(position);
                produtoPara = produtoDAO.procuraPorNomeELoja(((Produto) listView.getItemAtPosition(position)).getNome(), lojaPara);
                produtoDAO.close();
                campoProduto.setText(produtoDe.getNome());
                //campoPreco.setText(String.valueOf(produto.getPreco()));

                // Show Alert
                snackbar.setText( "Produto : " + produtoDe.getNome() + " selecionado");
                snackbar.show();
//                Toast.makeText(getApplicationContext(), "Produto : " + produtoDe.getNome() + " selecionado", Toast.LENGTH_LONG)
//                        .show();
                alertDialog.hide();
                alertDialog.dismiss();

            }

        });
    }

    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        pesquisa.clear();

        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getNome().toLowerCase().contains(txtPesquisa.toLowerCase()))
                pesquisa.add(produtos.get(i));
//            if (textlength <= produtos.get(i).getNome().length()) {
//                if (txtPesquisa.equalsIgnoreCase((String) produtos.get(i).getNome().subSequence(0, textlength))) {
//                    pesquisa.add(produtos.get(i));
//                }
//            }
        }
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

                    for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                        for (Produto produtoDaListView : produtos) {
                            if (produtoDaListView.getId().equals(produtoVinculoId)) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cadastro, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
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

                            produtoDe = produtoPrincipal;
                            produtoPara = produtoDAO.procuraPorNomeELoja(produtoPrincipal.getNome(), lojaDAO.procuraPorId(movimentacaoProduto.getIdLojaPara()));
                            produtoDAO.close();
                            lojaDAO.close();
                            if (produtoPara == null) {
                                produtoPara = new Produto();
                                produtoPara.setNome(produtoDe.getNome());
                                produtoPara.setLoja(lojaPara);
                                produtoPara.setPreco(produtoDe.getPreco());
                                produtoPara.setQuantidade(0);
                                produtoPara.setId(UUID.randomUUID().toString());
                                produtoDAO.insere(produtoPara);
                                produtoDAO.close();
                            }
                            produtoDe.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produtoDe));
                            entradaProdutoDAO.close();
                            //produtoDe.calcularQuantidade();

                            int saida = movimentacaoProduto.getQuantidade();
                            for (EntradaProduto ep : produtoDe.getEntradaProdutos()) {
                                int quantidadeDisponivel = (ep.getQuantidade() - ep.getQuantidadeVendidaMovimentada());
                                if ((saida <= quantidadeDisponivel)) {
                                    ep.setQuantidadeVendidaMovimentada(ep.getQuantidadeVendidaMovimentada() + saida);
                                    produtoDe.setQuantidade(produtoDe.getQuantidade() - saida);
                                    ep.desincroniza();
                                    entradaProdutoDAO.altera(ep);
                                    entradaProdutoDAO.close();

                                    EntradaProduto entradaProduto = new EntradaProduto();
                                    entradaProduto.setId(UUID.randomUUID().toString());
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    entradaProduto.setData(formatter.format(new Date()));
                                    entradaProduto.setProduto(produtoPara);
                                    entradaProduto.setPrecoDeCompra(ep.getPrecoDeCompra());
                                    entradaProduto.setQuantidade(saida);
                                    entradaProduto.desincroniza();
                                    entradaProdutoDAO.insere(entradaProduto);
                                    entradaProdutoDAO.close();


                                    break;
                                } else {
                                    if (quantidadeDisponivel == 0) {
                                        continue;
                                    } else {
                                        saida = saida - (quantidadeDisponivel);
                                        ep.setQuantidadeVendidaMovimentada(ep.getQuantidadeVendidaMovimentada() + (quantidadeDisponivel));
                                        produtoDe.setQuantidade(produtoDe.getQuantidade() - quantidadeDisponivel);
                                        ep.desincroniza();
                                        entradaProdutoDAO.altera(ep);
                                        entradaProdutoDAO.close();
                                        EntradaProduto entradaProduto = new EntradaProduto();
                                        entradaProduto.setId(UUID.randomUUID().toString());
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        entradaProduto.setData(formatter.format(new Date()));
                                        entradaProduto.setProduto(produtoPara);
                                        entradaProduto.setPrecoDeCompra(ep.getPrecoDeCompra());
                                        entradaProduto.setQuantidade(quantidadeDisponivel);
                                        entradaProduto.desincroniza();
                                        entradaProdutoDAO.insere(entradaProduto);
                                        entradaProdutoDAO.close();
                                    }
                                }


                            }
                            for (String produtoVinculoId : produtoDe.getIdProdutoVinculado()) {
                                Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId);
                                produtoDAO.close();
                                produtoVinculo.setQuantidade(produtoVinculo.getQuantidade() - movimentacaoProduto.getQuantidade());
                                produtoVinculo.desincroniza();
                                produtoDAO.altera(produtoVinculo);
                                produtoDAO.close();
                            }

                            for (String produtoVinculoId : produtoPara.getIdProdutoVinculado()) {
                                Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId);
                                produtoDAO.close();
                                produtoVinculo.setQuantidade(produtoVinculo.getQuantidade() + movimentacaoProduto.getQuantidade());
                                produtoVinculo.desincroniza();
                                produtoDAO.altera(produtoVinculo);
                                produtoDAO.close();
                            }

                            movimentacaoProduto.desincroniza();

                            movimentacaoProdutoDAO.insere(movimentacaoProduto);
                            movimentacaoProdutoDAO.close();

                            produtoDe.desincroniza();
                            produtoDAO.altera(produtoDe);
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


}
