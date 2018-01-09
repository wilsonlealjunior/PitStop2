package pitstop.com.br.pitstop.activity;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.adapter.AdpterProdutoPersonalizado;
import pitstop.com.br.pitstop.adapter.LstViewTabelaVendaAdapter;
import pitstop.com.br.pitstop.dao.AvariaDAO;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.FuroDAO;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.AvariaEntradaProduto;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Furo;
import pitstop.com.br.pitstop.model.FuroEntradaProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

public class CadastroFuroActivity extends AppCompatActivity {
    Spinner spinnerLoja;
    Spinner spinnerFuncionario;
    TextView campoProduto;
    EditText campoquantidade;
    //    Button botãoCadastrar;
    List<String> labelsLoja = new ArrayList<>();
    List<Loja> lojas = new ArrayList<>();
    Button adicionarProduto;
    //    List<Produto> carrinho = new ArrayList<>();
//    List<Furo> furoCarinho = new ArrayList<>();
    List<Produto> produtos = new ArrayList<>();
    List<Produto> pesquisa = new ArrayList<>();
    List<Usuario> usuarios = new ArrayList<>();
    Produto produto = new Produto();
    Loja lojaEscolhida = new Loja();
    Usuario usuarioEscolhido = new Usuario();
    private Toolbar toolbar;
    ProdutoDAO produtoDAO;
    LojaDAO lojaDAO;
    UsuarioDAO usuarioDAO;
    EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(this);
    Produto produtoPrincipal;


    EventBus bus = EventBus.getDefault();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_furo);

        spinnerFuncionario = (Spinner) findViewById(R.id.spinner_funcionario);
        spinnerLoja = (Spinner) findViewById(R.id.spinner);
        campoProduto = (TextView) findViewById(R.id.produto);
        campoquantidade = (EditText) findViewById(R.id.quantidade);
        adicionarProduto = (Button) findViewById(R.id.adicionar);


        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadatrar Furo");     //Titulo para ser exibido na sua Action Bar em frente à seta

        lojaDAO = new LojaDAO(this);
        usuarioDAO = new UsuarioDAO(this);
        produtoDAO = new ProdutoDAO(this);
        lojas = lojaDAO.listarLojas();
        usuarios = usuarioDAO.listarUsuarios();
        if (lojas.size() == 0) {
            Toast.makeText(getApplicationContext(), "Não existe usuarios cadastradas", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        for (Loja loja : lojas) {
            labelsLoja.add(loja.getNome());
        }
        lojaEscolhida = lojas.get(0);
        produtos = produtoDAO.procuraPorLoja(lojaEscolhida);
        if (produtos.size() == 0) {
            Toast.makeText(getApplicationContext(), "Não existe Produtos cadastrados", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLoja);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoja.setAdapter(spinnerAdapter);

        spinnerLoja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lojaEscolhida = lojas.get(i);
                produtos = produtoDAO.procuraPorLoja(lojaEscolhida);
                Collections.sort(produtos);
                campoProduto.setText("");
                produto = null;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<Usuario> spinnerAdapterUsuario = new ArrayAdapter<Usuario>(this, android.R.layout.simple_spinner_item, usuarios);
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


        campoProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCustomDialogwithList();

            }
        });

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
//
//        produto.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produto));
//        entradaProdutoDAO.close();
//
//        int quantAntesCalcular = produto.getQuantidade();
//        produto.calcularQuantidade();
//        int quantDepoisCalcular = produto.getQuantidade();
//        if (quantAntesCalcular != quantDepoisCalcular) {
//            produto.desincroniza();
//            produtoDAO.altera(produto);
//            produtoDAO.close();
//
//        }


        if (campoProduto.getText().

                toString().

                isEmpty())

        {
            campoProduto.setError("Escolha um produto");
            campoProduto.requestFocus();
            Toast.makeText(CadastroFuroActivity.this, "Escolha um Produto", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (campoquantidade.getText().

                length() == 0)

        {
            campoquantidade.setError("Digite uma quantidade");
            campoquantidade.requestFocus();
//                    Toast.makeText(CadastroFuroActivity.this, "Digite a Quantidade", Toast.LENGTH_SHORT).show();
            return false;

        }

        int quantidadeFuro = Integer.valueOf(campoquantidade.getText().toString());
        if (quantidadeFuro == 0)

        {
            campoquantidade.setError("Digite uma quantidade maior que zero");
            campoquantidade.requestFocus();
            return false;
        }
        if (quantidadeFuro > produto.getQuantidade())

        {
            Toast.makeText(CadastroFuroActivity.this, "Quantidade informada maior do que o estoque do Produto", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }


    private void ShowCustomDialogwithList() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroFuroActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = CadastroFuroActivity.this.getLayoutInflater();
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

                if (!isValid()) {
                    break;
                }
                item.setVisible(false);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CadastroFuroActivity.this, R.style.DialogTheme);
                LayoutInflater inflater = CadastroFuroActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.card_avisos, null);
                dialogBuilder.setView(dialogView);

                TextView titulo = (TextView) dialogView.findViewById(R.id.titulo);
                TextView mensagem = (TextView) dialogView.findViewById(R.id.mensagem);
                Button positivo = (Button) dialogView.findViewById(R.id.positivo);
                Button negativo = (Button) dialogView.findViewById(R.id.negativo);
                mensagem.setText("Desejar confirmar o cadastro da furo de " + campoquantidade.getText().toString() + " unidade(s) do produto " + produto.getNome() + " da loja " + lojaEscolhida.getNome() + " ? ");
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

                        Furo furo = new Furo();
                        furo.setId(UUID.randomUUID().toString());
                        furo.setIdLoja(lojaEscolhida.getId());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        furo.setData((formatter.format(new Date())));
                        furo.setIdUsuario(usuarioEscolhido.getNome());

//                EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(this);
////                produto = produtoDAO.procuraPorId(p.getId());
//                produto.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produto));
//                entradaProdutoDAO.close();

                        if (produto.vinculado()) {
                            produtoPrincipal = produtoDAO.procuraPorId(produto.getIdProdutoPrincipal());
                            produtoDAO.close();
                        } else {
                            produtoPrincipal = produtoDAO.procuraPorId(produto.getId());
                            produtoDAO.close();
                        }
                        produtoPrincipal.setEntradaProdutos(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                        entradaProdutoDAO.close();
                        int saida = Integer.valueOf(campoquantidade.getText().toString());
                        for (EntradaProduto entradaProduto : produtoPrincipal.getEntradaProdutos()) {
                            int quantidadeDisponivel = (entradaProduto.getQuantidade() - entradaProduto.getQuantidadeVendidaMovimentada());
                            if ((saida <= quantidadeDisponivel)) {
                                entradaProduto.setQuantidadeVendidaMovimentada(entradaProduto.getQuantidadeVendidaMovimentada() + saida);
                                produtoPrincipal.setQuantidade(produtoPrincipal.getQuantidade() - saida);
                                entradaProduto.desincroniza();
                                entradaProdutoDAO.altera(entradaProduto);
                                entradaProdutoDAO.close();


                                FuroEntradaProduto furoEntradaProduto = new FuroEntradaProduto();
                                furoEntradaProduto.setId(UUID.randomUUID().toString());
                                furoEntradaProduto.setQuantidade(saida);
                                furoEntradaProduto.setIdFuro(furo.getId());
                                furoEntradaProduto.setIdEntradaProduto(entradaProduto.getId());
                                //avariaEntradaProduto.sincroniza();
                                furo.setValor(furo.getValor() + entradaProduto.getPrecoDeCompra() * Integer.valueOf(saida));

                                furo.getFuroEntradeProdutos().add(furoEntradaProduto);


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

                                    FuroEntradaProduto furoEntradaProduto = new FuroEntradaProduto();
                                    furoEntradaProduto.setId(UUID.randomUUID().toString());
                                    furoEntradaProduto.setQuantidade(quantidadeDisponivel);
                                    furoEntradaProduto.setIdFuro(furo.getId());
                                    furoEntradaProduto.setIdEntradaProduto(entradaProduto.getId());
                                    //avariaEntradaProduto.sincroniza();
                                    furo.setValor(furo.getValor() + entradaProduto.getPrecoDeCompra() * Integer.valueOf(quantidadeDisponivel));

                                    furo.getFuroEntradeProdutos().add(furoEntradaProduto);
                                }
                            }

                        }
                        for (String produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                            Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId);
                            produtoDAO.close();
                            produtoVinculo.setQuantidade(produtoVinculo.getQuantidade() - Integer.valueOf(campoquantidade.getText().toString()));
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
