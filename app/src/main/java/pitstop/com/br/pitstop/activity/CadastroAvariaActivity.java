package pitstop.com.br.pitstop.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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
import pitstop.com.br.pitstop.dao.AvariaDAO;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.AvariaEntradaProduto;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

public class CadastroAvariaActivity extends AppCompatActivity {
    TextView campoProduto;
    EditText campoQuantidade;
    Snackbar snackbar;
    LinearLayout linearLayoutRootCadastroAvaria;
    private Toolbar toolbar;
    List<Produto> produtos = new ArrayList<>();
    List<Produto> pesquisa = new ArrayList<>();
    Produto produto = new Produto();
    ProdutoDAO produtoDAO;
    LojaDAO lojaDAO;
    EntradaProdutoDAO entradaProdutoDAO;
    Loja loja = new Loja();
    Produto produtoPrincipal;
    EventBus bus = EventBus.getDefault();



    Spinner spinnerLoja;
    List<String> labelsLoja = new ArrayList<>();
    List<Loja> lojas = new ArrayList<>();
    UsuarioPreferences usuarioPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_avaria);


        loadView();
        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        for (Loja loja : lojas) {
            labelsLoja.add(loja.getNome());
        }
//        if (lojas.size() == 0) {
//            Toast.makeText(getApplicationContext(), "Não existe usuarios cadastradas", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
//        loja = lojas.get(0);
//        boolean existeProdutoCadastrados = produtoDAO.procuraPorLoja(loja);
//        produtoDAO.close();
//        if (produtos.size() == 0) {
//            Toast.makeText(getApplicationContext(), "Não existe Produtos cadastrados", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }


        setupView();


    }

    public void setupView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadatrar Avaria");     //Titulo para ser exibido na sua Action Bar em frente à seta

        //inicializando atributos

        //configurando spinner de loja
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLoja);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoja.setAdapter(spinnerAdapter);
        spinnerLoja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //toda vez que é selecionado uma loja a lista de produtos é recarregada, então no campo produto é setado como vazio
                loja = lojas.get(i);
                produtos = produtoDAO.procuraPorLoja(loja);
//                Collections.sort(produtos);
                produtoDAO.close();
                campoProduto.setText("");
                produto = null;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //configurando snackbar
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        snackbar.setActionTextColor(Color.RED);

        campoProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCustomDialogwithList();

            }
        });

    }

    public void loadView() {
        linearLayoutRootCadastroAvaria = (LinearLayout) findViewById(R.id.ll_root_cadastr_avaria);
        snackbar = Snackbar.make(linearLayoutRootCadastroAvaria, "", Snackbar.LENGTH_LONG);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        spinnerLoja = (Spinner) findViewById(R.id.spinner);
        campoProduto = (TextView) findViewById(R.id.produto);
        campoQuantidade = (EditText) findViewById(R.id.quantidade);

        entradaProdutoDAO = new EntradaProdutoDAO(this);
        produtoDAO = new ProdutoDAO(this);
        lojaDAO = new LojaDAO(this);

        usuarioPreferences = new UsuarioPreferences(this);

    }


    private void ShowCustomDialogwithList() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroAvariaActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = CadastroAvariaActivity.this.getLayoutInflater();
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
                snackbar.setText("Produto : " + produto.getNome() + " selecionado");
                snackbar.show();
//                Toast.makeText(getApplicationContext(), "Produto : " + produto.getNome() + " selecionado", Toast.LENGTH_LONG)
//                        .show();
                alertDialog.hide();
                alertDialog.dismiss();

            }

        });
    }

    public static void hideKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cadastro, menu);

        return super.onCreateOptionsMenu(menu);

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
        hideKeyboard(this, getCurrentFocus());

//        produto.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produto));
//        entradaProdutoDAO.close();
//        int quantAntesCalcular = produto.getQuantidade();
//        produto.calcularQuantidade();
//        int quantDepoisCalcular = produto.getQuantidade();
//        if (quantAntesCalcular != quantDepoisCalcular) {
//            produto.desincroniza();
//            produtoDAO.altera(produto);
//            produtoDAO.close();
//
//        }
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
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_cadastro_ok:


                if (!isValid()) {
                    break;
                }
                item.setVisible(false);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroAvariaActivity.this, R.style.DialogTheme);
                LayoutInflater inflater = CadastroAvariaActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.card_avisos, null);
                dialogBuilder.setView(dialogView);

                TextView titulo = (TextView) dialogView.findViewById(R.id.titulo);
                TextView mensagem = (TextView) dialogView.findViewById(R.id.mensagem);
                Button positivo = (Button) dialogView.findViewById(R.id.positivo);
                Button negativo = (Button) dialogView.findViewById(R.id.negativo);
                mensagem.setText("Desejar confirmar o cadastro da avaria de " + campoQuantidade.getText().toString() + " unidade(s) do produto " + produto.getNome() + " da loja " + loja.getNome() + " ? ");
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

                        Avaria avaria = new Avaria();
                        avaria.setId(UUID.randomUUID().toString());
                        avaria.setIdLoja(loja.getId());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        avaria.setData((formatter.format(new Date())));


//                produto = produtoDAO.procuraPorId(p.getId());

                        if (produto.vinculado()) {
                            produtoPrincipal = produtoDAO.procuraPorId(produto.getIdProdutoPrincipal());
                            produtoDAO.close();
                        } else {
                            produtoPrincipal = produtoDAO.procuraPorId(produto.getId());
                            produtoDAO.close();
                        }

                        int saida = Integer.valueOf(campoQuantidade.getText().toString());
                        produtoPrincipal.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                        entradaProdutoDAO.close();
                        for (EntradaProduto entradaProduto : produtoPrincipal.getEntradaProdutos()) {
                            int quantidadeDisponivel = (entradaProduto.getQuantidade() - entradaProduto.getQuantidadeVendidaMovimentada());
                            if ((saida <= quantidadeDisponivel)) {
                                entradaProduto.setQuantidadeVendidaMovimentada(entradaProduto.getQuantidadeVendidaMovimentada() + saida);
                                produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - saida);
                                entradaProduto.desincroniza();
                                entradaProdutoDAO.altera(entradaProduto);
                                entradaProdutoDAO.close();


                                AvariaEntradaProduto avariaEntradaProduto = new AvariaEntradaProduto();
                                avariaEntradaProduto.setId(UUID.randomUUID().toString());
                                avariaEntradaProduto.setQuantidade(saida);
                                avariaEntradaProduto.setIdAvaria(avaria.getId());
                                avariaEntradaProduto.setIdEntradaProduto(entradaProduto.getId());
                                //avariaEntradaProduto.sincroniza();
                                avaria.setPrejuizo(avaria.getPrejuizo() + entradaProduto.getPrecoDeCompra() * Integer.valueOf(saida));

                                avaria.getAvariaEntradeProdutos().add(avariaEntradaProduto);


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

                                    AvariaEntradaProduto avariaEntradaProduto = new AvariaEntradaProduto();
                                    avariaEntradaProduto.setId(UUID.randomUUID().toString());
                                    avariaEntradaProduto.setQuantidade(quantidadeDisponivel);
                                    avariaEntradaProduto.setIdAvaria(avaria.getId());
                                    avariaEntradaProduto.setIdEntradaProduto(entradaProduto.getId());
                                    //avariaEntradaProduto.sincroniza();
                                    avaria.setPrejuizo(avaria.getPrejuizo() + entradaProduto.getPrecoDeCompra() * Integer.valueOf(quantidadeDisponivel));

                                    avaria.getAvariaEntradeProdutos().add(avariaEntradaProduto);
                                }
                            }

                        }
                        for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                            Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId);
                            produtoDAO.close();
                            produtoVinculo.setQuantidade(produtoVinculo.getQuantidade() - Integer.valueOf(campoQuantidade.getText().toString()));
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
                        //AvariaSincronizador avariaSincronizador = new AvariaSincronizador(this);
                        //avariaSincronizador.buscaTodos();


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


}
