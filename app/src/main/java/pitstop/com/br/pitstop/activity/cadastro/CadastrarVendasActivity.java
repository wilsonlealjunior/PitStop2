package pitstop.com.br.pitstop.activity.cadastro;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
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
import pitstop.com.br.pitstop.Print;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.adapter.AdpterProdutoPersonalizado;
import pitstop.com.br.pitstop.adapter.LstViewTabelaVendaAdapter;
import pitstop.com.br.pitstop.adapter.NonScrollListView;
import pitstop.com.br.pitstop.assyncTask.CarregarListaDeProdutoTask;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.event.AtualizarGraficos;
import pitstop.com.br.pitstop.event.CarregaListaDeProduto;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.ItemVenda;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Venda;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;


public class CadastrarVendasActivity extends BaseCadastroDeTransacaoDeProdutoActivity implements AdapterView.OnItemSelectedListener {


    Spinner spinnerFormaDeVenda;
    private TextView campoPreco;
    private TextView campoTotal;
    private TextView tvTotalCartao;

    Button adicionarProduto;
    private NonScrollListView listaViewDeProdutosCarrinho;
    LstViewTabelaVendaAdapter adapterTableCarrinho;


    List<Produto> carrinho = new ArrayList<>();


    double total = 0.0;
    double totalCartao = 0.0;
    Venda venda;
    String[] formaDePagamento = new String[]{"dinheiro", "cartao", "dinheiro e cartao"};
    UsuarioPreferences usuarioPreferences;


    EditText campoTotalCartao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_vendas);
        //vendaSincronizador = new VendaSincronizador(this);

        loadView();
//        setupView();
        if (!usuarioPreferences.temUsuario()) {

            Toast.makeText(CadastrarVendasActivity.this, "Não existe usuario logado", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }
        if (!usuarioPreferences.temLoja()) {

            Toast.makeText(CadastrarVendasActivity.this, "Por favor deslogue e esolha uma loja", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        CarregarListaDeProdutoTask carregarListaDeProdutoTask = new CarregarListaDeProdutoTask(this, loja, produtos);
        carregarListaDeProdutoTask.execute();
        //        mostraListaDeProduto();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void carregaListaDeProduto(CarregaListaDeProduto event) {
        setupView();
        mostraListaDeProduto();
    }

    @Override
    public void onDestroy() {
        // Unregister
        bus.unregister(this);
        super.onDestroy();
    }

    public void loadView() {
        bus.register(this);
        campoTotalCartao = (EditText) findViewById(R.id.total_cartao);
        spinnerFormaDeVenda = (Spinner) findViewById(R.id.spinner);
        campoPreco = (TextView) findViewById(R.id.preco);
        campoTotal = (TextView) findViewById(R.id.totalDinheiro);
        tvTotalCartao = (TextView) findViewById(R.id.tv_total_cartao);
        adicionarProduto = (Button) findViewById(R.id.adicionar);
        listaViewDeProdutosCarrinho = (NonScrollListView) findViewById(R.id.lista_de_produto);
        adapterTableCarrinho = new LstViewTabelaVendaAdapter(this, R.layout.tabela_carinho_venda, R.id.produto, carrinho);
        usuarioPreferences = new UsuarioPreferences(this);
        venda = new Venda();
        loja = usuarioPreferences.getLoja();
        super.loadView();


    }


    public boolean isValid() {
//        validandoQuantidadeDoProdutoNoEstoque();
        if (campoProduto.getText().toString().isEmpty()) {
            campoProduto.setError("Escolha um produto");
            campoProduto.requestFocus();
            snackbar.setText("Escolha um produto");
            snackbar.show();
//            Toast.makeText(CadastrarVendasActivity.this, "Escolha um Produto", Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(CadastrarVendasActivity.this, "Quantidade informada maior do que o estoque do Produto", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    public void setupView() {
        //configurando o spinner da forma de pagamento
        spinnerFormaDeVenda.setOnItemSelectedListener(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, formaDePagamento);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormaDeVenda.setAdapter(spinnerAdapter);
        //configurando a toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastrar Venda");     //Titulo para ser exibido na sua Action Bar em frente à seta
        //configurando a lista
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_carinho_venda, listaViewDeProdutosCarrinho, false);
        listaViewDeProdutosCarrinho.addHeaderView(headerView);
        listaViewDeProdutosCarrinho.setAdapter(adapterTableCarrinho);
        registerForContextMenu(listaViewDeProdutosCarrinho);


        //inicizalizando variaveis

//        produtos = produtoDAO.procuraPorLoja(loja);
//        produtoDAO.close();
        campoTotalCartao.setVisibility(View.GONE);
        tvTotalCartao.setVisibility(View.GONE);
//        campoTotalCartao.setFocusable(false);
//        Collections.sort(produtos);
        campoTotalCartao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    Toast.makeText(CadastrarVendasActivity.this, editable.toString(), Toast.LENGTH_SHORT);
                    Double n = Double.parseDouble(editable.toString());
                    campoTotal.setText("R$ " + (total - n));
                    totalCartao = n;
                } else {
                    campoTotal.setText("R$ " + total);
                    totalCartao = 0;

                }


            }
        });


        //funcionalidades para os botões
        adicionarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValid()) {
                    return;
                }

                int quantidadeComprada = Integer.valueOf(campoQuantidade.getText().toString());

                //coloco um novo produto com as mesmas caracteristicas do produto que está no banco a diferença é quantidade
                //a quantidade é a quantidade comprada.
                Produto prodCarinho = new Produto();
                prodCarinho.setId(produto.getId());
                prodCarinho.setQuantidade(quantidadeComprada);
                prodCarinho.setNome(produto.getNome());
                prodCarinho.setPreco(produto.getPreco());
                prodCarinho.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(prodCarinho));
                entradaProdutoDAO.close();
                carrinho.add(prodCarinho);
                total += prodCarinho.getQuantidade() * prodCarinho.getPreco();
                hideKeyboard(CadastrarVendasActivity.this,getCurrentFocus());
                snackbar.setText("Produto " + produto.getNome() + " adicionado ao carrinho");
                snackbar.show();
//                Toast toast = Toast.makeText(CadastrarVendasActivity.this, "Produto " + produto.getNome() + " adicionado ao carrinho", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();


             /*   TUDO ISSO É SO PARA DAR BAIXA NA LISTVIEW DO CARRINHO, PARA NÃO VENDER ALEM DO QUE ESTÁ
                DISPONÍVEL NO ESTOQUE.

                aqui pegaremos o produto principal na listView e daremos baixa em todos os seus vinculos

                como estamos fazendo operações sem salvar no banco então pegamos o produto principal na lista de produtos
                que é usado na listView para termos o controle do que pode ser movimentado, para não movimentar mais do que
                está disponivel no estoque*/
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
                produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - quantidadeComprada);
                for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                    for (Produto produtoDaListView : produtos) {
                        if (produtoDaListView.getId().equals(produtoVinculoId)) {
                            produtoDaListView.setQuantidade(produtoDaListView.getQuantidade() - quantidadeComprada);
                            break;
                        }
                    }
                }
                adapterTableCarrinho.notifyDataSetChanged();
                campoTotal.setText("R$ " + String.valueOf(total));


            }
        });
        super.setupView();


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    final ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                Produto produtoVindoDaListView = (Produto) listaViewDeProdutosCarrinho.getItemAtPosition(info.position);
                if (info.position != 0) {
                    Produto produtoDaListaDoListView = null;
                    for (Produto p : produtos) {
                        if (p.getId().equals(produtoVindoDaListView.getId())) {
                            produtoDaListaDoListView = p;
                            break;
                        }
                    }
                    //aqui pegaremos o produto principal na listView e daremos baixa em todos os seus vinculos
                    if (produtoDaListaDoListView.vinculado()) {
                        for (Produto p : produtos) {
                            if (produtoDaListaDoListView.getIdProdutoPrincipal().equals(p.getId())) {
                                produtoPrincipal = p;
                                break;
                            }
                        }
                    } else {
                        produtoPrincipal = produtoDaListaDoListView;
                    }
                    //dando baixa nos produtos da listView e nos seus vinculos
                    produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() + produtoVindoDaListView.getQuantidade());
                    for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                        for (Produto p : produtos) {
                            if (p.getId().equals(produtoVinculoId)) {
                                p.setQuantidade(p.getQuantidade() + produtoVindoDaListView.getQuantidade());
                                break;
                            }
                        }
                    }


//                    produtoDaListaDeProdutos.setQuantidade(produtoDaListaDeProdutos.getQuantidade() + produtoVindoDaListView.getQuantidade());
                    total -= produtoVindoDaListView.getQuantidade() * produtoVindoDaListView.getPreco();
                    campoTotal.setText("R$ " + String.valueOf(total));

                    adapterTableCarrinho.notifyDataSetChanged();

//                        }

//                    }
                    if (carrinho.remove(produtoVindoDaListView)) {
                        snackbar.setText(produtoVindoDaListView.getNome() + " removido do carrinho");
                        snackbar.show();
//                        Toast.makeText(CadastrarVendasActivity.this, produtoVindoDaListView.getNome() + " removido do carrinho", Toast.LENGTH_SHORT).show();
                        listaViewDeProdutosCarrinho.setAdapter(adapterTableCarrinho);
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
                if (venda.getFormaDePagamento() == null) {
                    item.setVisible(true);
                    snackbar.setText("Escolha a forma de pagamento");
                    snackbar.show();
//                    Toast.makeText(CadastrarVendasActivity.this, "Escolha a forma de pagamento", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (venda.getFormaDePagamento().equals("dinheiro e cartao")) {
                    if (totalCartao > total) {
                        item.setVisible(true);
                        snackbar.setText("Venda no cartão não pode ser maior do que o valor total da venda");
                        snackbar.show();
                        campoTotalCartao.requestFocus();
                        break;
                    }
                    if (totalCartao == 0) {
                        item.setVisible(true);
                        campoTotalCartao.setError("digite um valor diferente de zero");
                        campoTotalCartao.requestFocus();
                        break;
                    }
                }


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastrarVendasActivity.this, R.style.DialogTheme);
                LayoutInflater inflater = CadastrarVendasActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.card_avisos, null);
                dialogBuilder.setView(dialogView);

                TextView titulo = (TextView) dialogView.findViewById(R.id.titulo);
                TextView mensagem = (TextView) dialogView.findViewById(R.id.mensagem);
                Button positivo = (Button) dialogView.findViewById(R.id.positivo);
                Button negativo = (Button) dialogView.findViewById(R.id.negativo);
                mensagem.setText("Desejar confirmar o cadastro da venda?");
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

                        venda.setId(UUID.randomUUID().toString());
                        venda.setNomeVendedor(usuarioPreferences.getUsuario().getNome());
                        venda.setIdLoja(usuarioPreferences.getLoja().getId());


                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        venda.setDataDaVenda(formatter.format(new Date()));

                        venda.desincroniza();
                        if (venda.getFormaDePagamento().equals("dinheiro")) {
                            venda.setTotalDinheiro(total);
                            venda.setTotalCartao(0);
                        } else if (venda.getFormaDePagamento().equals("cartao")) {
                            venda.setTotalCartao(total);
                            venda.setTotalDinheiro(0);
                        }
                        if (venda.getFormaDePagamento().equals("dinheiro e cartao")) {
                            venda.setTotalCartao(totalCartao);
                            venda.setTotalDinheiro(total - totalCartao);
                        }

                        for (Produto p : carrinho) {


                            produto = produtoDAO.procuraPorId(p.getId());
                            produtoDAO.close();
                            //aqui pegaremos o produto principal no banco
                            if (produto.vinculado()) {
                                produtoPrincipal = produtoDAO.procuraPorId(produto.getIdProdutoPrincipal());
                                produtoDAO.close();
                            } else {
                                produtoPrincipal = produto;
                            }

                            produtoPrincipal.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                            entradaProdutoDAO.close();
                            int saida = p.getQuantidade();
                            for (EntradaProduto l : produtoPrincipal.getEntradaProdutos()) {
                                //quantidade de produto disponivel
                                int quantidadeDisponivel = (l.getQuantidade() - l.getQuantidadeVendidaMovimentada());
                                //verifico se a quantidade disponivel é maior do que o solicitado na compra
                                //se não eu verifico se tem quantidade disponivel
                                //depois verifico se a quantidade disponivel é != 0 e se é menor que o solicitado
                                if ((saida <= quantidadeDisponivel)) {
                                    l.setQuantidadeVendidaMovimentada(l.getQuantidadeVendidaMovimentada() + saida);
                                    produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - saida);
                                    l.desincroniza();
                                    entradaProdutoDAO.altera(l);
                                    entradaProdutoDAO.close();

                                    ItemVenda itemVenda = new ItemVenda();
                                    itemVenda.setId(UUID.randomUUID().toString());
                                    itemVenda.setIdVenda(venda.getId());
                                    itemVenda.setIdEntradaProduto(l.getId());
                                    itemVenda.setPrecoDeVenda(produto.getPreco());
                                    itemVenda.setIdProduto(produto.getId());
                                    itemVenda.setQuantidadeVendida(saida);

                                    //itemVenda.sincroniza();
                                    venda.setLucro(venda.getLucro() + (produto.getPreco() - l.getPrecoDeCompra()) * saida);

                                    venda.getItemVendas().add(itemVenda);

                                    break;
                                } else {
                                    if (quantidadeDisponivel == 0) {
                                        continue;
                                    } else {
                                        saida = saida - (quantidadeDisponivel);
                                        l.setQuantidadeVendidaMovimentada(l.getQuantidadeVendidaMovimentada() + (quantidadeDisponivel));
                                        produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - quantidadeDisponivel);
                                        l.desincroniza();
                                        entradaProdutoDAO.altera(l);
                                        entradaProdutoDAO.close();

                                        ItemVenda itemVenda = new ItemVenda();
                                        itemVenda.setId(UUID.randomUUID().toString());
                                        itemVenda.setIdVenda(venda.getId());
                                        itemVenda.setIdEntradaProduto(l.getId());
                                        itemVenda.setIdProduto(produto.getId());
                                        itemVenda.setQuantidadeVendida(quantidadeDisponivel);
                                        itemVenda.setPrecoDeVenda(produto.getPreco());
                                        //itemVenda.sincroniza();
                                        venda.setLucro(venda.getLucro() + (l.getProduto().getPreco() - l.getPrecoDeCompra()) * quantidadeDisponivel);

                                        venda.getItemVendas().add(itemVenda);
                                    }
                                }

                            }


                            produtoPrincipal.desincroniza();
                            produtoDAO.altera(produtoPrincipal);
                            produtoDAO.close();

                            //dando baixa em todos os vinculos do produtoPrincipal
                            for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                                for (Produto produtoDaListaDeProduto : produtos) {
                                    if (produtoDaListaDeProduto.getId().equals(produtoVinculoId)) {
                                        Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId);
                                        produtoDAO.close();
                                        produtoVinculo.setQuantidade(produtoVinculo.getQuantidade() - p.getQuantidade());
                                        produtoVinculo.desincroniza();
                                        produtoDAO.altera(produtoVinculo);
                                        produtoDAO.close();
                                        break;
                                    }
                                }
                            }


                        }

                        vendaDAO.insere(venda);
                        vendaDAO.close();
                        bus.post(new AtualizaListaProdutoEvent());
                        bus.post(new AtualizaListaLojasEvent());
                        bus.post(new AtualizarGraficos());

                        alertDialog.hide();
                        alertDialog.dismiss();
                        finish();
                    }
                });
                alertDialog.show();
                String cumpom = "PITSTOP: " + usuarioPreferences.getLoja().getNome() + "\n";
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cumpom += "Data: " + Util.dataNoformatoBrasileiro(new Date()) + "\n";
                cumpom += "Vendedor: " + usuarioPreferences.getUsuario().getNome() + "\n";
                cumpom += "-----------------------\n";
                for (Produto p : carrinho) {
                    cumpom += p.getNome() + "\n";
                    cumpom += p.getQuantidade() + " UND X " + p.getPreco() + "   " + Util.moedaNoFormatoBrasileiro(p.getQuantidade() * p.getPreco()) + "\n";
                }
                cumpom += "-----------------------\n";
                cumpom += "TOTAL         " + Util.moedaNoFormatoBrasileiro(total) + "\n\n";
//                Log.e("Cupom", cumpom);
                Print imprimir = new Print(CadastrarVendasActivity.this, cumpom);
                imprimir.imprime();

                break;


        }

        return super.

                onOptionsItemSelected(item);

    }

    @Override
    public void mostraListaDeProduto() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastrarVendasActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = CadastrarVendasActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_personalizado_lstview, null);
        dialogBuilder.setView(dialogView);

        final ListView listView = (ListView) dialogView.findViewById(R.id.listview);
        SearchView pesquisaDialog = (SearchView) dialogView.findViewById(R.id.pesquisa);
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLoja);

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
                produto = (Produto) listView.getItemAtPosition(position);
                campoProduto.setText(produto.getNome());

                campoPreco.setText(String.valueOf(produto.getPreco()));

                // Show Alert
                snackbar.setText("Produto : " + produto.getNome() + " selecionado");
                snackbar.show();
//                Toast.makeText(getApplicationContext(), "Produto : " + produto.getNome() + " selecionado", Toast.LENGTH_LONG)
//                        .show();
                alertDialog.hide();
                alertDialog.dismiss();

            }

        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        venda.setFormaDePagamento(formaDePagamento[i]);
        if (i == 2) {
            campoTotalCartao.setVisibility(View.VISIBLE);
            tvTotalCartao.setVisibility(View.VISIBLE);
//                campoTotalCartao.setFocusableInTouchMode(true);
        } else {
            campoTotalCartao.setVisibility(View.GONE);
            tvTotalCartao.setVisibility(View.GONE);
            totalCartao = 0.0;
            campoTotalCartao.setText("0");
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
