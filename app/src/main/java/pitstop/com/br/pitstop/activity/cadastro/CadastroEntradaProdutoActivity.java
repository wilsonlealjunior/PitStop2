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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.adapter.LstViewTabelaRelatorioEntradaProduto;
import pitstop.com.br.pitstop.adapter.NonScrollListView;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.helper.CadastroEntradaProdutoHelper;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;


public class CadastroEntradaProdutoActivity extends BaseCadastroDeTransacaoDeProdutoActivity implements AdapterView.OnItemSelectedListener {
    CadastroEntradaProdutoHelper cadastroEntradaProdutoHelper;
    Spinner spinnerLoja;
    EditText precoDeCompra;
    Button adicionarProduto;
    List<EntradaProduto> carinho = new ArrayList<>();
    List<String> labelsLoja = new ArrayList<>();
    List<Loja> lojas = new ArrayList<>();
    private NonScrollListView listaViewDeEntradaDeProdutos;
    LstViewTabelaRelatorioEntradaProduto adapterTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_entrada_produto);
        loadView();


        //validando a pagina
        //inicilizando atributos
        boolean existeProdutosCadastrados = produtoDAO.existeProdutosCadastrados();
        produtoDAO.close();
        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        if (lojas.size() == 0) {
            Toast.makeText(getApplicationContext(), "Não existe Lojas cadastradas", Toast.LENGTH_LONG).show();
//            finish();
//            snackbar.setText("Não lojas cadastradas");
//            snackbar.show();
            finish();
            return;

        }
//        if (!existeProdutosCadastrados) {
//            Toast.makeText(getApplicationContext(), "Não existe Produtos cadastrados", Toast.LENGTH_LONG).show();
////            snackbar.setText("Não existe Produtos cadastrados");
////            snackbar.show();
//            finish();
//            return;
//        }
        setupView();


    }


    public void setupView() {
        //configurando o toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastrar Entrada");     //Titulo para ser exibido na sua Action Bar em frente à seta

        //configurando o listView como uma tabela(carrinho de produto)
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_relatorio_entrada_produto, listaViewDeEntradaDeProdutos, false);
        listaViewDeEntradaDeProdutos.addHeaderView(headerView);
        adapterTable = new LstViewTabelaRelatorioEntradaProduto(this, R.layout.tabela_relatorio_entrada_produto, R.id.produto, carinho);
        listaViewDeEntradaDeProdutos.setAdapter(adapterTable);
        registerForContextMenu(listaViewDeEntradaDeProdutos);


        //configurando spinner da loja
        spinnerLoja.setOnItemSelectedListener(this);
        for (Loja loja : lojas) {
            labelsLoja.add(loja.getNome());
        }
        loja = lojas.get(0);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLoja);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoja.setAdapter(spinnerAdapter);


        //atribuindo funcionalidades para os EditText e botão
        adicionarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValid()) {
                    return;
                }

                final EntradaProduto entradaProdutoCar = cadastroEntradaProdutoHelper.PegarEntradaProduto();
                if (entradaProdutoCar == null) {
                    return;
                }
                //a quantidade do produto só sera mudada quando o usuario finalizar a operação
                //como ainda não mexemos na quantidade do produto então não precisamos verificar o vinculo com outro produto
                //nesse parte do codigo so colocaremos os itens no carrinho
                entradaProdutoCar.setProduto(produto);
                carinho.add(entradaProdutoCar);
                adapterTable.notifyDataSetChanged();
                snackbar.setText("Produto " + produto.getNome() + " adicionado ao carrinho");
                snackbar.show();
//                Toast toast = Toast.makeText(CadastroEntradaProdutoActivity.this, "Produto " + produto.getNome() + " adicionado ao carrinho", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();

            }
        });
        super.setupView();


    }

    public void loadView() {
        ;
        listaViewDeEntradaDeProdutos = (NonScrollListView) findViewById(R.id.lista_de_produto);
        spinnerLoja = (Spinner) findViewById(R.id.spinner);
        precoDeCompra = (EditText) findViewById(R.id.preco_de_compra);
        adicionarProduto = (Button) findViewById(R.id.adicionar);
        cadastroEntradaProdutoHelper = new CadastroEntradaProdutoHelper(this);

        super.loadView();
    }


    public boolean isValid() {
//        validandoQuantidadeDoProdutoNoEstoque();
        if (precoDeCompra.getText().toString().isEmpty()) {
            precoDeCompra.setError("Digite o preco de compra");
            precoDeCompra.requestFocus();
            return false;
        }
        if (campoProduto.getText().toString().isEmpty()) {
            campoProduto.setError("Escolha um produto");
            campoProduto.requestFocus();
            snackbar.setText("Escolha um produto");
            snackbar.show();

//            Toast.makeText(CadastroEntradaProdutoActivity.this, "Escolha um Produto", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (campoQuantidade.getText().length() == 0) {
            campoQuantidade.setError("Digite uma quantidade");
            campoQuantidade.requestFocus();
            return false;
//                    Toast.makeText(CadastrarVendasActivity.this, "Digite a Quantidade", Toast.LENGTH_SHORT).show();
        }
        int quantidadeEntrada = Integer.valueOf(campoQuantidade.getText().toString());
        if (quantidadeEntrada == 0) {
            campoQuantidade.setError("Digite uma quantidade maior que zero");
            campoQuantidade.requestFocus();
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
                    EntradaProduto entradaProduto = (EntradaProduto) listaViewDeEntradaDeProdutos.getItemAtPosition(info.position);
                    if (carinho.remove(entradaProduto)) {
                        snackbar.setText(entradaProduto.getProduto().getNome() + " removido do carrinho");
                        snackbar.show();
//                        Toast.makeText(CadastroEntradaProdutoActivity.this, entradaProduto.getProduto().getNome() + " removido do carrinho", Toast.LENGTH_SHORT).show();
                        listaViewDeEntradaDeProdutos.setAdapter(adapterTable);
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
                if (carinho.size() == 0) {
                    snackbar.setText("Não existe produtos no carrinho");
                    snackbar.show();
//                    Toast.makeText(CadastroEntradaProdutoActivity.this, "Não existe produtos no carrinho", Toast.LENGTH_SHORT).show();
                    break;
                }
                item.setVisible(false);
                //produto = produtoDAO.procuraPorIdELoja(produto.getNome(), loja);
//                produtoDAO.close();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroEntradaProdutoActivity.this, R.style.DialogTheme);
                LayoutInflater inflater = CadastroEntradaProdutoActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.card_avisos, null);
                dialogBuilder.setView(dialogView);

                TextView titulo = (TextView) dialogView.findViewById(R.id.titulo);
                TextView mensagem = (TextView) dialogView.findViewById(R.id.mensagem);
                Button positivo = (Button) dialogView.findViewById(R.id.positivo);
                Button negativo = (Button) dialogView.findViewById(R.id.negativo);
                mensagem.setText("Desejar confirmar o cadastro de entrada de produtos? ");
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
                        for (EntradaProduto entradaProduto : carinho) {
                            Produto p = entradaProduto.getProduto();
                            if (p.vinculado()) {
                                produtoPrincipal = produtoDAO.procuraPorId(p.getIdProdutoPrincipal());
                                produtoDAO.close();
                            } else {
                                produtoPrincipal = produtoDAO.procuraPorId(p.getId());
                                produtoDAO.close();
                            }

                            produtoPrincipal.entrada(entradaProduto.getQuantidade());
                            produtoPrincipal.desincroniza();
                            produtoDAO.altera(produtoPrincipal);
                            produtoDAO.close();

                            entradaProduto.desincroniza();
                            entradaProduto.setProduto(produtoPrincipal);
                            entradaProdutoDAO.insere(entradaProduto);
                            entradaProdutoDAO.close();

                            for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                                Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId);
                                produtoDAO.close();
                                produtoVinculo.entrada(entradaProduto.getQuantidade());
                                produtoVinculo.desincroniza();
                                produtoDAO.altera(produtoVinculo);
                                produtoDAO.close();
                            }


                        }

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

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        loja = lojas.get(position);
        produtos = produtoDAO.procuraPorLoja(loja);
        produtoDAO.close();
//        Collections.sort(produtos);
        campoProduto.setText("");
        campoQuantidade.setText("");
        precoDeCompra.setText("");
        produto = null;

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
