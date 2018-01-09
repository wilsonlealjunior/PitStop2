package pitstop.com.br.pitstop.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.adapter.AdpterProdutoPersonalizado;
import pitstop.com.br.pitstop.adapter.LstViewTabelaRelatorioEntradaProduto;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.helper.CadastroEntradaProdutoHelper;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;

public class CadastroEntradaProdutoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    CadastroEntradaProdutoHelper cadastroEntradaProdutoHelper;
    Spinner spinnerLoja;
    TextView campoProduto;
    EditText campoquantidade;
    EditText precoDeCompra;
    Button adicionarProduto;
    List<EntradaProduto> carinho = new ArrayList<>();
    List<String> labelsLoja = new ArrayList<>();
    List<Loja> lojas = new ArrayList<>();
    List<Produto> pesquisa = new ArrayList<>();
    List<Produto> produtos = new ArrayList<>();
    Produto produto = new Produto();
    Produto produtoPrincipal = new Produto();
    Loja loja = new Loja();
    private Toolbar toolbar;
    ProdutoDAO produtoDAO;
    LojaDAO lojaDAO;
    private ListView listaViewDeEntradaDeProdutos;
    LstViewTabelaRelatorioEntradaProduto adapterTable;
    EventBus bus = EventBus.getDefault();
    EntradaProdutoDAO entradaProdutoDAO;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_entrada_produto);

        loadView();


        //validando a pagina
        //inicilizando atributos
        produtos = produtoDAO.listarProdutos();
        produtoDAO.close();
        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        if (lojas.size() == 0) {
            Toast.makeText(getApplicationContext(), "Não existe usuarios cadastradas", Toast.LENGTH_LONG).show();
            finish();
            return;

        }
        if (produtos.size() == 0) {
            Toast.makeText(getApplicationContext(), "Não existe Produtos cadastrados", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
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
                Toast toast = Toast.makeText(CadastroEntradaProdutoActivity.this, "Produto " + produto.getNome() + " adicionado ao carrinho", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        });

        campoProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCustomDialogwithList();

            }
        });

    }

    public void loadView() {
        listaViewDeEntradaDeProdutos = (ListView) findViewById(R.id.lista_de_produto);
        spinnerLoja = (Spinner) findViewById(R.id.spinner);
        campoProduto = (TextView) findViewById(R.id.produto);
        campoquantidade = (EditText) findViewById(R.id.quantidade);
        precoDeCompra = (EditText) findViewById(R.id.preco_de_compra);
        adicionarProduto = (Button) findViewById(R.id.adicionar);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        cadastroEntradaProdutoHelper = new CadastroEntradaProdutoHelper(this);
        entradaProdutoDAO = new EntradaProdutoDAO(this);
        produtoDAO = new ProdutoDAO(this);
        lojaDAO = new LojaDAO(this);


    }

    public void validandoQuantidadeDoProdutoNoEstoque() {
        if (produto.vinculado()) {
            produtoPrincipal = produtoDAO.procuraPorId(produto.getIdProdutoPrincipal());
            produtoPrincipal.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
            entradaProdutoDAO.close();
        } else {
            produto.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produto));
            entradaProdutoDAO.close();
            produtoPrincipal = produto;

        }

        int quantAntesCalcular = produtoPrincipal.getQuantidade();
        produtoPrincipal.calcularQuantidade();
        int quantDepoisCalcular = produtoPrincipal.getQuantidade();
        if (quantAntesCalcular != quantDepoisCalcular) {
            for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId);
                produtoDAO.close();
                produtoVinculo.entrada(produtoPrincipal.getQuantidade());
                produtoVinculo.desincroniza();
                produtoDAO.altera(produtoVinculo);
                produtoDAO.close();
            }
            produto.desincroniza();
            produtoDAO.altera(produtoPrincipal);
            produtoDAO.close();

        }

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
            Toast.makeText(CadastroEntradaProdutoActivity.this, "Escolha um Produto", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (campoquantidade.getText().length() == 0) {
            campoquantidade.setError("Digite uma quantidade");
            campoquantidade.requestFocus();
            return false;
//                    Toast.makeText(CadastrarVendasActivity.this, "Digite a Quantidade", Toast.LENGTH_SHORT).show();
        }
        int quantidadeEntrada = Integer.valueOf(campoquantidade.getText().toString());
        if (quantidadeEntrada == 0) {
            campoquantidade.setError("Digite uma quantidade maior que zero");
            campoquantidade.requestFocus();
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
                        Toast.makeText(CadastroEntradaProdutoActivity.this, entradaProduto.getProduto().getNome() + " removido do carrinho", Toast.LENGTH_SHORT).show();
                        listaViewDeEntradaDeProdutos.setAdapter(adapterTable);
                        adapterTable.notifyDataSetChanged();
                    }
                }


                return false;
            }
        });
    }


    private void ShowCustomDialogwithList() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroEntradaProdutoActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = CadastroEntradaProdutoActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_personalizado_lstview, null);
        dialogBuilder.setView(dialogView);

        final ListView listView = (ListView) dialogView.findViewById(R.id.listview);
        SearchView pesquisaDialog = (SearchView) dialogView.findViewById(R.id.pesquisa);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLoja);

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
                //campoPreco.setText(String.valueOf(produto.getPreco()));

                // Show Alert
                Toast.makeText(getApplicationContext(), "Produto : " + produto.getNome() + " selecionado", Toast.LENGTH_LONG)
                        .show();
                alertDialog.hide();
                alertDialog.dismiss();

            }

        });
    }

    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        pesquisa.clear();

        for (int i = 0; i < produtos.size(); i++) {
            if (textlength <= produtos.get(i).getNome().length()) {
                if (txtPesquisa.equalsIgnoreCase((String) produtos.get(i).getNome().subSequence(0, textlength))) {
                    pesquisa.add(produtos.get(i));
                }
            }
        }
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

                if (carinho.size() == 0) {
                    Toast.makeText(CadastroEntradaProdutoActivity.this, "Não existe produtos no carrinho", Toast.LENGTH_SHORT).show();
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

                            bus.post(new AtualizaListaProdutoEvent());
                            bus.post(new AtualizaListaLojasEvent());


                        }


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
        Collections.sort(produtos);
        campoProduto.setText("");
        campoquantidade.setText("");
        precoDeCompra.setText("");
        produto = null;

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
