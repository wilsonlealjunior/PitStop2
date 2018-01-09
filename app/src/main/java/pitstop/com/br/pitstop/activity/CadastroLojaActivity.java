package pitstop.com.br.pitstop.activity;

import android.content.Intent;
import android.support.annotation.IdRes;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.adapter.AdpterProdutoPersonalizado;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.helper.CadastroLojaHelper;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;

public class CadastroLojaActivity extends AppCompatActivity {
    private CadastroLojaHelper cadastroLojaHelper;
    private AHBottomNavigation bottomNavigation;
    private List<Loja> pesquisa = new ArrayList<>();
    private List<Loja> lojas = new ArrayList<>();
    private Toolbar toolbar;
    EventBus bus = EventBus.getDefault();
    private RadioGroup rgCopiar;
    private RadioButton rbCopiarPositivo;
    private RadioButton rbCopiarNegativo;
    private EditText nomeLojaCopiarProdutos;
    private Loja lojaEscolhidaParaCopiarProdutos;

    LojaDAO lojaDAO;
    ProdutoDAO produtoDAO;
    //LojaSincronizador localizacaoSincronizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_loja);

        lojaDAO = new LojaDAO(this);
        produtoDAO = new ProdutoDAO(this);

        //localizacaoSincronizador = new LojaSincronizador(this);
        cadastroLojaHelper = new CadastroLojaHelper(this);
        //mostrarBarraInferior();
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastrar Loja");

        Intent intent = getIntent();
        Loja loja = (Loja) intent.getSerializableExtra("loja");
        if (loja != null) {
            cadastroLojaHelper.preencheFormulario(loja);
        }

        lojas = lojaDAO.listarLojas();
        nomeLojaCopiarProdutos = (EditText) findViewById(R.id.nome_loja_copiar_produtos);
        nomeLojaCopiarProdutos.setVisibility(View.GONE);
        nomeLojaCopiarProdutos.setFocusable(false);
        nomeLojaCopiarProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCustomDialogwithList();
            }
        });
        rgCopiar = (RadioGroup) findViewById(R.id.rg_copiar);
        rbCopiarNegativo = (RadioButton) findViewById(R.id.rb_copiar_negativo);
        rbCopiarPositivo = (RadioButton) findViewById(R.id.rb_copiar_positivo);
        rgCopiar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checked) {
                if (rbCopiarPositivo.getId() == (checked)) {
                    nomeLojaCopiarProdutos.setVisibility(View.VISIBLE);
                } else if (rbCopiarNegativo.getId() == (checked)) {
                    nomeLojaCopiarProdutos.setVisibility(View.GONE);
                }

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


                final Loja loja = cadastroLojaHelper.PegarLoja();
                //quando eu pego a loja e tem alguma campo em branco eu retorno null para fazer a verificação
                if (loja == null) {
                    break;
                }
                //validando caso a loja tenha os produtos copiado de outra loja
                if (rgCopiar.getCheckedRadioButtonId() == (R.id.rb_copiar_positivo)) {
                    if (nomeLojaCopiarProdutos.getText().toString().isEmpty()) {
                        nomeLojaCopiarProdutos.setError("Escolha um produto a ser vinculado");
                        break;
                    }
                }
                item.setVisible(false);
                if (loja.getId() != null) {
                    loja.desincroniza();
                    lojaDAO.altera(loja);
                    lojaDAO.close();
                    Toast.makeText(CadastroLojaActivity.this, "Loja " + loja.getNome() + " Editado!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroLojaActivity.this, R.style.DialogTheme);
                LayoutInflater inflater = CadastroLojaActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.card_avisos, null);
                dialogBuilder.setView(dialogView);

                TextView titulo = (TextView) dialogView.findViewById(R.id.titulo);
                TextView mensagem = (TextView) dialogView.findViewById(R.id.mensagem);
                Button positivo = (Button) dialogView.findViewById(R.id.positivo);
                Button negativo = (Button) dialogView.findViewById(R.id.negativo);
                mensagem.setText("Desejar confirmar o cadastro da da loja " + loja.getNome() + " ? ");
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

                        lojaDAO.insere(loja);
                        lojaDAO.close();
                        if (rgCopiar.getCheckedRadioButtonId() == rbCopiarPositivo.getId()) {


                            List<Produto> produtos = produtoDAO.procuraPorLoja(lojaEscolhidaParaCopiarProdutos);
                            for (Produto produtoPrincipal : produtos) {
                                //o if é para saber se ele é um produtoPrincipal
                                if (!produtoPrincipal.vinculado()) {
                                    produtoPrincipal.setId(UUID.randomUUID().toString());
                                    for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                                        for (Produto produtoVinculado : produtos) {
                                            if (produtoVinculoId.equals(produtoVinculado.getId())) {
                                                produtoVinculado.setIdProdutoPrincipal(produtoPrincipal.getId());
                                                produtoPrincipal.getIdProdutoVinculado().remove(produtoVinculado.getId());
                                                produtoVinculado.setId(UUID.randomUUID().toString());
                                                produtoPrincipal.getIdProdutoVinculado().add(produtoVinculado.getId());
                                            }
                                        }
                                    }
                                }
                                produtoPrincipal.desincroniza();
                                produtoPrincipal.setQuantidade(0);
                                produtoPrincipal.setLoja(loja);
                            }


                            produtoDAO.insereLista(produtos);
                            produtoDAO.close();
                        }

                        Toast.makeText(CadastroLojaActivity.this, "Loja " + loja.getNome() + " salvo!", Toast.LENGTH_SHORT).show();
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

    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        pesquisa.clear();

        for (int i = 0; i < lojas.size(); i++) {
            if (textlength <= lojas.get(i).getNome().length()) {
                if (txtPesquisa.equalsIgnoreCase((String) lojas.get(i).getNome().subSequence(0, textlength))) {
                    pesquisa.add(lojas.get(i));
                }
            }
        }
    }


    private void ShowCustomDialogwithList() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroLojaActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = CadastroLojaActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_personalizado_lstview, null);
        dialogBuilder.setView(dialogView);

        final ListView listView = (ListView) dialogView.findViewById(R.id.listview);
        SearchView pesquisaDialog = (SearchView) dialogView.findViewById(R.id.pesquisa);
        final ArrayAdapter<Loja> adapterPesquisa = new ArrayAdapter<Loja>(this,
                android.R.layout.simple_list_item_1, pesquisa);
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

        final ArrayAdapter<Loja> adapterLoja = new ArrayAdapter<Loja>(this,
                android.R.layout.simple_list_item_1, lojas);

        listView.setAdapter(adapterLoja);

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
                lojaEscolhidaParaCopiarProdutos = (Loja) listView.getItemAtPosition(position);
                nomeLojaCopiarProdutos.setText(lojaEscolhidaParaCopiarProdutos.getNome());
                // Show Alert
                Toast.makeText(getApplicationContext(), "Loja : " + lojaEscolhidaParaCopiarProdutos.getNome() + " selecionado", Toast.LENGTH_LONG)
                        .show();
                alertDialog.hide();
                alertDialog.dismiss();

            }

        });
    }

}
