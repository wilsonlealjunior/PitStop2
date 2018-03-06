package pitstop.com.br.pitstop.activity.cadastro;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.adapter.LstViewTabelaCarinhoFuro;
import pitstop.com.br.pitstop.adapter.NonScrollListView;
import pitstop.com.br.pitstop.dao.FuroDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.event.AtualizarGraficos;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Furo;
import pitstop.com.br.pitstop.model.ItemFuro;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Usuario;

public class CadastroFuroActivity extends BaseCadastroDeTransacaoDeProdutoActivity {
    Spinner spinnerLoja;
    Spinner spinnerFuncionario;


    //    Button botãoCadastrar;
    List<String> labelsLoja = new ArrayList<>();
    List<Loja> lojas = new ArrayList<>();
    Button adicionarFuro;
    List<Furo> carrinho = new ArrayList<>();
    //    List<Produto> carrinho = new ArrayList<>();
    List<Produto> todoProdutos = new ArrayList<>();
    List<Usuario> usuarios = new ArrayList<>();
    List<String> labelsUsuario = new ArrayList<>();

    Loja lojaEscolhida = new Loja();
    Usuario usuarioEscolhido = new Usuario();

    private NonScrollListView listaViewDeFurosCarrinho;
    LstViewTabelaCarinhoFuro adapterTableCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_furo);

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
        todoProdutos = produtoDAO.listarProdutos();
        produtoDAO.close();
        if (todoProdutos.size() == 0) {
            Toast.makeText(getApplicationContext(), "Não existe Produtos cadastrados", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setupView();

    }

    public void setupView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadatrar Furo");     //Titulo para ser exibido na sua Action Bar em frente à seta


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLoja);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoja.setAdapter(spinnerAdapter);


        spinnerLoja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lojaEscolhida = lojas.get(i);
                produtos.clear();
                for (Produto p : todoProdutos) {
                    if (p.getLoja().getId().equals(lojaEscolhida.getId())) {
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
        usuarios = usuarioDAO.listarUsuarios();
        usuarioDAO.close();
        for (Usuario user : usuarios) {
            labelsUsuario.add(user.getNome());
        }
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_carinho_furo, listaViewDeFurosCarrinho, false);
        listaViewDeFurosCarrinho.addHeaderView(headerView);
        registerForContextMenu(listaViewDeFurosCarrinho);
        adapterTableCarrinho = new LstViewTabelaCarinhoFuro(this, R.layout.tabela_carinho_furo, R.id.produto, carrinho);
        listaViewDeFurosCarrinho.setAdapter(adapterTableCarrinho);
        ArrayAdapter<String> spinnerAdapterUsuario = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsUsuario);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuncionario.setAdapter(spinnerAdapterUsuario);
        spinnerFuncionario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                usuarioEscolhido = usuarios.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        adicionarFuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValid()) {
                    return;
                }

                Furo furo = new Furo();
                furo.setId(UUID.randomUUID().toString());
                furo.setIdUsuario(usuarioEscolhido.getNome());
                furo.setIdLoja(lojaEscolhida.getId());
                furo.setPrecoDeVenda(produto.getPreco());
                furo.setQuantidade(Integer.valueOf(campoQuantidade.getText().toString()));
                furo.setValor(furo.getQuantidade() * furo.getPrecoDeVenda());
                furo.setIdProduto(produto.getId());
                carrinho.add(furo);

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
                produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - furo.getQuantidade());
                for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                    for (Produto produtoDaListView : produtos) {
                        if (produtoDaListView.getId().equals(produtoVinculoId)) {
                            produtoDaListView.setQuantidade(produtoDaListView.getQuantidade() - furo.getQuantidade());
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
        spinnerFuncionario = (Spinner) findViewById(R.id.spinner_funcionario);
        spinnerLoja = (Spinner) findViewById(R.id.spinner);

        adicionarFuro = (Button) findViewById(R.id.adicionar);
        listaViewDeFurosCarrinho = (NonScrollListView) findViewById(R.id.lista_de_furo);
        adapterTableCarrinho = new LstViewTabelaCarinhoFuro(this, R.layout.tabela_carinho_furo, R.id.produto, carrinho);

        super.loadView();

    }


    public boolean isValid() {

        hideKeyboard(this, getCurrentFocus());

        if (campoProduto.getText().toString().isEmpty()) {
            campoProduto.setError("Escolha um produto");
            campoProduto.requestFocus();
            snackbar.setText("Escolha um produto");
            snackbar.show();
//            Toast.makeText(CadastroFuroActivity.this, "Escolha um Produto", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (campoQuantidade.getText().length() == 0) {
            campoQuantidade.setError("Digite uma quantidade");
            campoQuantidade.requestFocus();
//                    Toast.makeText(CadastroFuroActivity.this, "Digite a Quantidade", Toast.LENGTH_SHORT).show();
            return false;

        }

        int quantidadeFuro = Integer.valueOf(campoQuantidade.getText().toString());
        if (quantidadeFuro == 0)

        {
            campoQuantidade.setError("Digite uma quantidade maior que zero");
            campoQuantidade.requestFocus();
            return false;
        }
        if (quantidadeFuro > produto.getQuantidade()) {
            snackbar.setText("Quantidade informada maior do que o estoque do Produto");
            snackbar.show();
//            Toast.makeText(CadastroFuroActivity.this, "Quantidade informada maior do que o estoque do Produto", Toast.LENGTH_SHORT).show();
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
                    Furo furo = (Furo) listaViewDeFurosCarrinho.getItemAtPosition(info.position);
                    Produto produtoFuro = null;

                    for (Produto produto : todoProdutos) {
                        if (produto.getId().equals(furo.getIdProduto())) {
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

                    produtoPrincipal.entrada(furo.getQuantidade());

                    for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                        for (Produto produtoDaListView : produtos) {
                            if (produtoDaListView.getId().equals(produtoVinculoId)) {
                                produtoDaListView.entrada(furo.getQuantidade());
                                break;
                            }
                        }
                    }

                    campoProduto.setText("");
                    campoQuantidade.setText("0");
                    if (carrinho.remove(furo)) {
                        snackbar.setText(" removido do carrinho");
                        snackbar.show();
//                        Toast.makeText(CadastroMovimentacaoProdutoActivity.this, " removido do carrinho", Toast.LENGTH_SHORT).show();
                        listaViewDeFurosCarrinho.setAdapter(adapterTableCarrinho);
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
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroFuroActivity.this, R.style.DialogTheme);
                LayoutInflater inflater = CadastroFuroActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.card_avisos, null);
                dialogBuilder.setView(dialogView);

                TextView titulo = (TextView) dialogView.findViewById(R.id.titulo);
                TextView mensagem = (TextView) dialogView.findViewById(R.id.mensagem);
                Button positivo = (Button) dialogView.findViewById(R.id.positivo);
                Button negativo = (Button) dialogView.findViewById(R.id.negativo);
                mensagem.setText("Desejar confirmar a transação ?");
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


                        String dataAtual = Util.dataNoformatoDoSQLite(new Date());

                        for (Furo furo : carrinho) {
                            furo.setData(dataAtual);

                            Produto produto = produtoDAO.procuraPorId(furo.getIdProduto());
                            produtoDAO.close();

                            if (produto.vinculado()) {
                                produtoPrincipal = produtoDAO.procuraPorId(produto.getIdProdutoPrincipal());
                                produtoDAO.close();
                            } else {
                                produtoPrincipal = produtoDAO.procuraPorId(produto.getId());
                                produtoDAO.close();
                            }
                            produtoPrincipal.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                            entradaProdutoDAO.close();
                            int saida = Integer.valueOf(furo.getQuantidade());
                            for (EntradaProduto entradaProduto : produtoPrincipal.getEntradaProdutos()) {
                                int quantidadeDisponivel = (entradaProduto.getQuantidade() - entradaProduto.getQuantidadeVendidaMovimentada());
                                if ((saida <= quantidadeDisponivel)) {
                                    entradaProduto.setQuantidadeVendidaMovimentada(entradaProduto.getQuantidadeVendidaMovimentada() + saida);
                                    produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - saida);
                                    entradaProduto.desincroniza();
                                    entradaProdutoDAO.altera(entradaProduto);
                                    entradaProdutoDAO.close();


                                    ItemFuro itemFuro = new ItemFuro();
                                    itemFuro.setId(UUID.randomUUID().toString());
                                    itemFuro.setQuantidade(saida);
                                    itemFuro.setIdFuro(furo.getId());
                                    itemFuro.setIdEntradaProduto(entradaProduto.getId());
                                    itemFuro.setPrecoDeVenda(furo.getPrecoDeVenda());
                                    //avariaEntradaProduto.sincroniza();
//                                    furo.setValor(furo.getValor() + furo.getPrecoDeVenda() * Integer.valueOf(saida));

                                    furo.getFuroEntradeProdutos().add(itemFuro);


                                    break;
                                } else {
                                    if (quantidadeDisponivel == 0) {
                                        continue;
                                    } else {
                                        saida = saida - (quantidadeDisponivel);
                                        entradaProduto.setQuantidadeVendidaMovimentada(entradaProduto.getQuantidadeVendidaMovimentada() + (quantidadeDisponivel));
                                        produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - quantidadeDisponivel);
                                        entradaProduto.desincroniza();
                                        entradaProdutoDAO.altera(entradaProduto);
                                        entradaProdutoDAO.close();

                                        ItemFuro itemFuro = new ItemFuro();
                                        itemFuro.setId(UUID.randomUUID().toString());
                                        itemFuro.setQuantidade(quantidadeDisponivel);
                                        itemFuro.setIdFuro(furo.getId());
                                        itemFuro.setIdEntradaProduto(entradaProduto.getId());
                                        itemFuro.setPrecoDeVenda(furo.getPrecoDeVenda());
                                        //avariaEntradaProduto.sincroniza();
//                                        furo.setValor(furo.getValor() + furo.getPrecoDeVenda() * Integer.valueOf(quantidadeDisponivel));

                                        furo.getFuroEntradeProdutos().add(itemFuro);
                                    }
                                }

                            }
                            for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                                Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId);
                                produtoDAO.close();
                                produtoVinculo.setQuantidade(produtoVinculo.getQuantidade() - furo.getQuantidade());
                                produtoVinculo.desincroniza();
                                produtoDAO.altera(produtoVinculo);
                                produtoDAO.close();
                            }

                            produtoPrincipal.desincroniza();
                            produtoDAO.altera(produtoPrincipal);
                            produtoDAO.close();


                            FuroDAO furoDAO = new FuroDAO(getApplicationContext());
                            furoDAO.insere(furo);

                            furoDAO.close();


                        }
                        bus.post(new AtualizaListaProdutoEvent());
                        bus.post(new AtualizaListaLojasEvent());
                        bus.post(new AtualizarGraficos());


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
