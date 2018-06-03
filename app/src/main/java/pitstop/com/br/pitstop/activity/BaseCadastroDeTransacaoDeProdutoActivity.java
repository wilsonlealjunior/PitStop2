package pitstop.com.br.pitstop.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.BaseActivity;
import pitstop.com.br.pitstop.adapter.AdpterProdutoPersonalizado;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.MovimentacaoProdutoDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.dao.VendaDAO;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;


public abstract class BaseCadastroDeTransacaoDeProdutoActivity extends AppCompatActivity implements BaseActivity {

    protected TextView campoProduto;
    protected EditText campoQuantidade;
    protected Snackbar snackbar;
    protected LinearLayout linearLayoutRootCadastro;
    protected Toolbar toolbar;
    protected List<Produto> produtos = new ArrayList<>();
    protected List<Produto> pesquisa = new ArrayList<>();
    protected Produto produto = new Produto();
    protected ProdutoDAO produtoDAO;
    protected LojaDAO lojaDAO;
    protected UsuarioDAO usuarioDAO;
    protected MovimentacaoProdutoDAO movimentacaoProdutoDAO;
    protected EntradaProdutoDAO entradaProdutoDAO;
    protected VendaDAO vendaDAO;
    protected Loja loja = new Loja();
    protected Produto produtoPrincipal;
    protected EventBus bus = EventBus.getDefault();

    public abstract boolean isValid();

    public void setupView() {
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
                mostraListaDeProduto();

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


    public static void hideKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
        }

        return super.onOptionsItemSelected(item);

    }

    public void mostraListaDeProduto() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BaseCadastroDeTransacaoDeProdutoActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = BaseCadastroDeTransacaoDeProdutoActivity.this.getLayoutInflater();
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


    public void  loadView() {
        linearLayoutRootCadastro = (LinearLayout) findViewById(R.id.ll_root_cadastro);
        snackbar = Snackbar.make(linearLayoutRootCadastro, "", Snackbar.LENGTH_LONG);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        campoProduto = (TextView) findViewById(R.id.produto);
        campoQuantidade = (EditText) findViewById(R.id.quantidade);
        entradaProdutoDAO = new EntradaProdutoDAO(this);
        produtoDAO = new ProdutoDAO(this);
        lojaDAO = new LojaDAO(this);
        usuarioDAO = new UsuarioDAO(this);
        vendaDAO = new VendaDAO(this);
        movimentacaoProdutoDAO = new MovimentacaoProdutoDAO(this);
    }


}
