package pitstop.com.br.pitstop.activity.cadastro;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.adapter.AdpterProdutoPersonalizado;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.helper.CadastroProdutoHelper;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

public class CadastroProdutoActivity extends AppCompatActivity {
    private List<Produto> produtos = new ArrayList<>();
    private List<Produto> pesquisa = new ArrayList<>();
    private List<Loja> lojas = new ArrayList<>();
    List<String> labelsLojas = new ArrayList<>();
    ArrayAdapter<String> spinnerAdapterLojas;

    private CadastroProdutoHelper cadastroProdutoHelper;
    private Toolbar toolbar;
    private RadioGroup rgVinculacao;
    private RadioButton rbVinculado;
    private RadioButton rbSemVinculo;
    private CardView cardViewVinculacao;
    private EditText etNomeDoProdutoVinculado;
    private Spinner spinnerLoja;
    private UsuarioPreferences usuarioPreferences;
    private ScrollView scrollRootViewCadastroProduto;

    Produto produto;
    Loja lojaEscolhida;
    Produto produtoPrincipal;
    EventBus bus = EventBus.getDefault();
    LojaDAO lojaDAO;
    ProdutoDAO produtoDAO;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        LoadView();

        //validando os requisitos para a funcionalidade da pagina
        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        if (lojas.size() == 0) {
            Toast.makeText(CadastroProdutoActivity.this, "não existe lojas cadastradas", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Intent intent = getIntent();
        String produtoId = intent.getStringExtra("produtoId");
        if(produtoId!=null) {
             produto = produtoDAO.procuraPorId(produtoId);
        }
        if (produto != null) {
            cadastroProdutoHelper.preencheFormulario(produto);
            cardViewVinculacao.setVisibility(View.GONE);

        }

        setupView();


    }

    public void setupView() {

        //configurando toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastrar Produto");

        //connfigurações da tela (ocultando e desocultando)
        etNomeDoProdutoVinculado.setVisibility(View.GONE);
        spinnerLoja.setVisibility(View.GONE);
        rgVinculacao.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checked) {
                if (rbVinculado.getId() == (checked)) {
                    spinnerLoja.setVisibility(View.VISIBLE);
                    etNomeDoProdutoVinculado.setVisibility(View.VISIBLE);
                } else if (rbSemVinculo.getId() == (checked)) {
                    etNomeDoProdutoVinculado.setVisibility(View.GONE);
                    spinnerLoja.setVisibility(View.GONE);
                }

            }
        });

        //inicializando e setando funcionalidades nos atributos
        etNomeDoProdutoVinculado.setFocusable(false);
        etNomeDoProdutoVinculado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCustomDialogwithList();
            }
        });


        labelsLojas.clear();
        for (Loja loja : lojas) {
            labelsLojas.add(loja.getNome());

        }
        //configurando o spinner de lojas
        spinnerAdapterLojas = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLojas);
        spinnerAdapterLojas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoja.setAdapter(spinnerAdapterLojas);
        spinnerLoja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lojaEscolhida = lojas.get(i);
                produtos = produtoDAO.procuraPorLoja(lojaEscolhida);
                produtoDAO.close();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        snackbar.setActionTextColor(Color.RED);

    }


    public void LoadView() {
        scrollRootViewCadastroProduto = (ScrollView)findViewById(R.id.sv_root_cadastro_produto);
        snackbar = Snackbar.make(scrollRootViewCadastroProduto, "", Snackbar.LENGTH_LONG);
        cardViewVinculacao = (CardView) findViewById(R.id.cardView_vinculacao);
        spinnerLoja = (Spinner) findViewById(R.id.spiner_loja);
        lojaDAO = new LojaDAO(this);
        produtoDAO = new ProdutoDAO(this);
        rbVinculado = (RadioButton) findViewById(R.id.rb_vinculado);
        rbSemVinculo = (RadioButton) findViewById(R.id.rb_sem_vinculo);
        usuarioPreferences = new UsuarioPreferences(this);
        cadastroProdutoHelper = new CadastroProdutoHelper(this);
        rgVinculacao = (RadioGroup) findViewById(R.id.rg_vinculacao);
        etNomeDoProdutoVinculado = (EditText) (findViewById(R.id.nome_produto_vinculado));
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);


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
                produto = cadastroProdutoHelper.PegarProduto();
                //valindando nome e preco do produto
                if (produto == null) {
                    break;
                }
                //validando caso o produto seja vinculado a outro produto
                if (rgVinculacao.getCheckedRadioButtonId() == (R.id.rb_vinculado)) {
                    if (etNomeDoProdutoVinculado.getText().toString().isEmpty()) {
                        etNomeDoProdutoVinculado.setError("Escolha um produto a ser vinculado");
                        break;
                    }
                }
                item.setVisible(false);
                if (produto.getId() != null) {
                    produto.desincroniza();
                    produtoDAO.altera(produto);
                    produtoDAO.close();
                    Toast.makeText(CadastroProdutoActivity.this, "Produto " + produto.getNome() + " Editado!", Toast.LENGTH_SHORT).show();
                    bus.post(new AtualizaListaProdutoEvent());
                    bus.post(new AtualizaListaLojasEvent());
                    finish();
                    break;
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroProdutoActivity.this, R.style.DialogTheme);
                LayoutInflater inflater = CadastroProdutoActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.card_avisos, null);
                dialogBuilder.setView(dialogView);

                TextView titulo = (TextView) dialogView.findViewById(R.id.titulo);
                TextView mensagem = (TextView) dialogView.findViewById(R.id.mensagem);
                Button positivo = (Button) dialogView.findViewById(R.id.positivo);
                Button negativo = (Button) dialogView.findViewById(R.id.negativo);
                mensagem.setText("Desejar confirmar o cadastro do produto " + produto.getNome() + " ? ");
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
                        for (Loja loja : lojas) {
                            produto.setId(UUID.randomUUID().toString());
                            produto.setLoja(loja);
                            if (rgVinculacao.getCheckedRadioButtonId() == (R.id.rb_vinculado)) {
                                Produto produtoaux = produtoDAO.procuraPorNomeELoja(produtoPrincipal.getNome(), loja);
                                produtoDAO.close();
                                if (produtoaux == null) {
                                    continue;
                                } else {
                                    produtoPrincipal = produtoaux;
                                }
                                produto.vincular(produtoPrincipal.getId());
                                produto.setQuantidade(produtoPrincipal.getQuantidade());
                                produtoPrincipal.getIdProdutoVinculado().add(produto.getId());
                                produtoPrincipal.desincroniza();
                                produtoDAO.insere(produto);
                                produtoDAO.close();
                                produtoDAO.altera(produtoPrincipal);
                                produtoDAO.close();
                            } else if (rgVinculacao.getCheckedRadioButtonId() == (R.id.rb_sem_vinculo)) {
                                produtoDAO.insere(produto);
                                produtoDAO.close();

                            }
                        }
                        Toast.makeText(CadastroProdutoActivity.this, "Produto " + produto.getNome() + " salvo!", Toast.LENGTH_SHORT).show();
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

        return super.

                onOptionsItemSelected(item);

    }

    private void ShowCustomDialogwithList() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroProdutoActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = CadastroProdutoActivity.this.getLayoutInflater();
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
                produtoPrincipal = (Produto) listView.getItemAtPosition(position);
                etNomeDoProdutoVinculado.setText(produtoPrincipal.getNome());
                // Show Alert
                snackbar.setText("Produto : " + produtoPrincipal.getNome() + " selecionado");
                snackbar.show();
//                Toast.makeText(getApplicationContext(), "Produto : " + produtoPrincipal.getNome() + " selecionado", Toast.LENGTH_LONG)
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

}
