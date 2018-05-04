package pitstop.com.br.pitstop.activity.relatorio;


import java.text.NumberFormat;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.ResponseBody;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.activity.DataHoraView;
import pitstop.com.br.pitstop.adapter.TabelaDescricaoVendaRecicleViewAdapter;
import pitstop.com.br.pitstop.adapter.TabelaRelatorioVendasReyclerViewAdapter;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.dao.VendaDAO;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.ItemVenda;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.model.Venda;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelatorioVendasActivity extends AppCompatActivity {

    static final long TEMPO_EM_MINUTO_PARA_FUNCIONARIO_DELETAR_VENDA = 10;

    private EventBus bus = EventBus.getDefault();
    Date de;
    Date ate;
    Venda vendaClicada;
    boolean PodeDeletarVenda = true;
    boolean vendaDeletada = false;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    private Toolbar toolbar;
    private RecyclerView listaDeVendasRecycleView;
    private Snackbar snackbar;
    private LinearLayout linearLayoutRootRelatorioVendas;
    List<Venda> relatorioVendas = new ArrayList<>();
    double auxTotal = 0.0;
    double auxLucro = 0.0;


    //    LstViewTabelaRelatorioVendas adapterTable;
    TabelaRelatorioVendasReyclerViewAdapter vendasReyclerViewAdapter;
    //    private ListView listaViewDeVendas;
    private RecyclerView RecyclerViewDeVendas;

    String[] formaDePagamento = new String[]{"Todas", "dinheiro", "cartao"};
    String formaDePagamentoEscolhido;
    Spinner formaDePagamentoSpinnner;

    List<String> labelslojas = new ArrayList<>();
    List<Loja> lojas;
    Loja lojaEscolhida;
    Spinner lojaSpinner;
    List<String> labelsUsuarios = new ArrayList<>();
    List<Usuario> usuarios;
    Usuario usuarioEscolhido;
    Spinner funcionarioSpinner;
    LojaDAO lojaDAO = new LojaDAO(this);
    VendaDAO vendaDAO = new VendaDAO(this);
    UsuarioDAO usuarioDAO = new UsuarioDAO(this);
    UsuarioPreferences up = new UsuarioPreferences(this);
    EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(this);
    ProdutoDAO produtoDAO = new ProdutoDAO(this);
    TextView tvResumeCardLucro;
    TextView tvResumeCardTotal;
    TextView textViewLoja;
    TextView textViewFuncionario;
    CardView cardViewResumo;
    ViewGroup viewRoot;
    DataHoraView dataHoraView;
    ProgressDialog progressDialog;
    Button btnGerarRelatorioPDF;
    private Button btnGerarRelatorio;
    CardView cardViewFiltros;
    LinearLayout llProgressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_vendas);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        viewRoot = (ViewGroup) findViewById(android.R.id.content);
        dataHoraView = new DataHoraView(viewRoot, this);


        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        if (lojas.size() == 0) {
            Toast.makeText(RelatorioVendasActivity.this, "Não existe lojas cadastradas", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }
        labelslojas.add("Todas");
        for (Loja l : lojas) {
            labelslojas.add(l.getNome());
        }
        usuarios = usuarioDAO.listarUsuarios();
        usuarioDAO.close();
        labelsUsuarios.add("Todos");
        for (Usuario u : usuarios) {
            labelsUsuarios.add(u.getNome());
        }

        ArrayAdapter<String> spinnerAdapterLoja = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelslojas);
        spinnerAdapterLoja.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lojaSpinner = (Spinner) findViewById(R.id.spinner_loja);
        lojaSpinner.setAdapter(spinnerAdapterLoja);


        ArrayAdapter<String> spinnerAdapterFormaPagamento = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, formaDePagamento);
        spinnerAdapterFormaPagamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formaDePagamentoSpinnner = (Spinner) findViewById(R.id.spinner_forma_pagamento);
        formaDePagamentoSpinnner.setAdapter(spinnerAdapterFormaPagamento);


        ArrayAdapter<String> spinnerAdapterFuncionario = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsUsuarios);
        spinnerAdapterFuncionario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        funcionarioSpinner = (Spinner) findViewById(R.id.spinner_funcionario);
        funcionarioSpinner.setAdapter(spinnerAdapterFuncionario);


        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Relatorio de Vendas");

        linearLayoutRootRelatorioVendas = (LinearLayout) findViewById(R.id.ll_root_relatorio_vendas);
        snackbar = Snackbar.make(linearLayoutRootRelatorioVendas, "", Snackbar.LENGTH_LONG);
        btnGerarRelatorio = (Button) findViewById(R.id.gerar_relatorio);
        btnGerarRelatorioPDF = (Button) findViewById(R.id.gerar_relatorio_pdf);
        tvResumeCardTotal = (TextView) findViewById(R.id.resumo_card_total);
        tvResumeCardLucro = (TextView) findViewById(R.id.resumo_card_lucro);
        cardViewResumo = (CardView) findViewById(R.id.lista_transacoes_resumo);
        cardViewFiltros = (CardView) findViewById(R.id.card_view_filtros);
        textViewLoja = (TextView) findViewById(R.id.tv_loja);
        llProgressBar = (LinearLayout) findViewById(R.id.progressBar);
        llProgressBar.setVisibility(View.GONE);
        textViewFuncionario = (TextView) findViewById(R.id.tv_funcionario);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        cardViewResumo.setVisibility(View.GONE);




        listaDeVendasRecycleView = (RecyclerView) findViewById(R.id.lista_de_vendas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listaDeVendasRecycleView.setLayoutManager(layoutManager);
        listaDeVendasRecycleView.setHasFixedSize(true);
        listaDeVendasRecycleView.setNestedScrollingEnabled(true);


        vendasReyclerViewAdapter = new TabelaRelatorioVendasReyclerViewAdapter(relatorioVendas, this);

        listaDeVendasRecycleView.setAdapter(vendasReyclerViewAdapter);
        formaDePagamentoSpinnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    formaDePagamentoEscolhido = formaDePagamento[i];
                    Log.e("Forma de pagamento", formaDePagamentoEscolhido);
                } else {
                    formaDePagamentoEscolhido = null;
                }

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
        lojaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    lojaEscolhida = lojas.get(i - 1);
                    Log.e("Loja Escolhida", lojaEscolhida.getNome());
                } else {
                    lojaEscolhida = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        funcionarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    usuarioEscolhido = usuarios.get(i - 1);
                } else {
                    usuarioEscolhido = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //updateTextLabel();

        btnGerarRelatorioPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValid()) {
                    return;
                }


                String funcionario;
                String Idloja;
                if (formaDePagamentoEscolhido == null) {
                    formaDePagamentoEscolhido = "%";
                }
                if (usuarioEscolhido == null) {
                    funcionario = "%";
                } else {
                    funcionario = usuarioEscolhido.getNome();
                }
                if (lojaEscolhida == null) {
                    Idloja = "%";
                } else {
                    Idloja = lojaEscolhida.getId();
                }

                progressDialog.setMessage("Gerando PDF");

                progressDialog.show();
                Call<ResponseBody> call = null;
                if (up.temUsuario()) {
                    if (up.getUsuario().getRole().equals("Funcionario")) {
                        lojaEscolhida = up.getLoja();
                        Idloja = lojaEscolhida.getId();
                        usuarioEscolhido = up.getUsuario();
                        funcionario = usuarioEscolhido.getNome();
                        call = new RetrofitInializador().getRelatorioService().relatorioVendasFuncionario(formaDePagamentoEscolhido, Idloja, funcionario, dataHoraView.getEditTextDataInicio().getText().toString(), dataHoraView.getEditTextDataFim().getText().toString());
                    } else {
                        call = new RetrofitInializador().getRelatorioService().relatorioVendas(formaDePagamentoEscolhido, Idloja, funcionario, dataHoraView.getEditTextDataInicio().getText().toString(), dataHoraView.getEditTextDataFim().getText().toString());

                    }
                }


                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("TAG", "server contacted and has file");

                            boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                            Toast.makeText(getApplicationContext(), "PDF gerado com sucesso", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            views();

                            Log.d("TAG", "file download was a success? " + writtenToDisk);
                        } else {
                            Log.d("TAG", "server contact failed");
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Erro ao gerar o pdf", Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("onFailure chamado", t.getMessage());
                        progressDialog.dismiss();
                        snackbar.setText("Verifique a conexao com a internet");
                        snackbar.show();
//                        Toast.makeText(getApplicationContext(), "Verifique a conexao com a internet", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });


        btnGerarRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                gerarRelatorio();


            }
        });

        vendasReyclerViewAdapter.setOnItemClickListener(new TabelaRelatorioVendasReyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position != 0) {
                    vendaClicada = (Venda) relatorioVendas.get(position - 1);
                    Log.e("Venda clicada", String.valueOf(position - 1));
                    ShowCustomDialogwithList();
                }
            }
        });
        vendasReyclerViewAdapter.setOnItemDeleteListener(new TabelaRelatorioVendasReyclerViewAdapter.ItemDeleteListener() {
            @Override
            public void onItemDelete(int position) {
                if (position != 0) {
                    PodeDeletarVenda = true;
                    Venda venda = (Venda) relatorioVendas.get(position - 1);
                    DeletarVenda(venda);

                }
            }
        });

        if (up.temUsuario()) {
            if (up.getUsuario().getRole().equals("Funcionario")) {
                lojaSpinner.setVisibility(View.GONE);
                funcionarioSpinner.setVisibility(View.GONE);
//                lucro.setVisibility(View.GONE);
                textViewFuncionario.setVisibility(View.GONE);
                textViewLoja.setVisibility(View.GONE);
                tvResumeCardLucro.setVisibility(View.GONE);


            }

        }


    }

    private void DeletarVenda(Venda venda) {
        for (Venda v : relatorioVendas) {
            if (v.getId().equals(venda.getId())) {
                verificarSeFuncionarioPodeDeletarVenda(v);
                if (PodeDeletarVenda) {
                    vendaDeletada = true;
                    v.desativar();
                    v.desincroniza();
                    deletaItensDaVenda(v);
                    if (relatorioVendas.remove(venda)) {
                        vendasReyclerViewAdapter.notifyDataSetChanged();
                    }
                    vendasReyclerViewAdapter.notifyDataSetChanged();
                    VendaDAO vendaDAO = new VendaDAO(RelatorioVendasActivity.this);
                    vendaDAO.altera(v);
                    vendaDAO.close();
                    aviso("Alerta", "Venda deletada com sucesso");
                    break;
                } else {
                    aviso("Alerta", "Voce não pode fazer a exclusão pois ja se passaram mais de 10 minutos");

                }

            }


        }
    }

    private void deletaItensDaVenda(Venda v) {

        if ((v.getItemVendas() != null) && (!v.getItemVendas().isEmpty())) {
            for (ItemVenda itemVenda : v.getItemVendas()) {
                int quantidade = itemVenda.getQuantidadeVendida();
                EntradaProduto entradaProduto = entradaProdutoDAO.procuraPorId(itemVenda.getIdEntradaProduto());
                entradaProdutoDAO.close();
                entradaProduto.setQuantidadeVendidaMovimentada(entradaProduto.getQuantidadeVendidaMovimentada() - quantidade);
                Produto produto = entradaProduto.getProduto();

                ajustaVinculosDoProduto(quantidade, produto);

                produto.setQuantidade(produto.getQuantidade() + quantidade);
                entradaProduto.desincroniza();
                produto.desincroniza();
                entradaProdutoDAO.altera(entradaProduto);
                entradaProdutoDAO.close();
                produtoDAO.altera(produto);
                produtoDAO.close();


            }
        }
    }

    private void ajustaVinculosDoProduto(int quantidade, Produto produto) {
        for (String produtoVinculoId : produto.getIdProdutoVinculado()) {
            Produto p = produtoDAO.procuraPorId(produtoVinculoId);
            produtoDAO.close();
            p.setQuantidade(p.getQuantidade() + quantidade);
            p.desincroniza();
            produtoDAO.altera(p);
            produtoDAO.close();
        }
    }

    private void verificarSeFuncionarioPodeDeletarVenda(Venda v) {
        Date dataDaVenda = Util.converteDoFormatoSQLParaDate(v.getDataDaVenda());
        Date agora = new Date();
        long ldt1 = dataDaVenda.getTime();
        long ldt2 = agora.getTime();
        long res = (ldt2 - ldt1) / (1000 * 60);
        if (res > TEMPO_EM_MINUTO_PARA_FUNCIONARIO_DELETAR_VENDA) {
            if (up.temUsuario()) {
                if (up.getUsuario().getRole().equals("Funcionario")) {
                    PodeDeletarVenda = false;

                }
            }
        }
    }

    public void views() {
        progressDialog.setMessage("Preparando para exibir PDF");
        progressDialog.show();
        File pdfFile = new File(getExternalFilesDir(null) + File.separator + "relatorioVenda.pdf");  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
            progressDialog.dismiss();
        } catch (ActivityNotFoundException e) {
//            snackbar.setText("Não existe aplicativo para visualizar o PDF");
//            snackbar.show();
            Toast.makeText(RelatorioVendasActivity.this, "Não existe aplicativo para visualizar o PDF", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    public boolean isValid() {

        if (dataHoraView.getEditTextDataInicio().getText().toString().equals("")) {
            dataHoraView.getEditTextDataInicio().setError("Escolha uma data");
            dataHoraView.getEditTextDataInicio().requestFocus();
            snackbar.setText("escolha uma data de inicio");
            snackbar.show();
//            Toast.makeText(RelatorioVendasActivity.this, "escolha uma data de inicio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dataHoraView.getEditTextDataFim().getText().toString().equals("")) {
            dataHoraView.getEditTextDataFim().setError("Escolha uma data");
            dataHoraView.getEditTextDataFim().requestFocus();
            snackbar.setText("escolha uma data de termino");
            snackbar.show();
//            Toast.makeText(RelatorioVendasActivity.this, "escolha uma data de termino", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            de = formatter.parse(dataHoraView.getEditTextDataInicio().getText().toString());
            ate = formatter.parse(dataHoraView.getEditTextDataFim().getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (de.after(ate)) {
            snackbar.setText("A data de origem deve ser menor do que a data final ");
            snackbar.show();
//            Toast.makeText(getApplicationContext(), "A data de origem deve ser menor do que a data final ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Custom Dialog with List
    private void ShowCustomDialogwithList() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RelatorioVendasActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = RelatorioVendasActivity.this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.alert_dialog_recycler_view, null);
        dialogBuilder.setView(dialogView);

        RecyclerView recyclerView;
        TabelaDescricaoVendaRecicleViewAdapter tabelaDescricaoVendaRecicleViewAdapter;
        recyclerView = (RecyclerView) dialogView.findViewById(R.id.listview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        tabelaDescricaoVendaRecicleViewAdapter = new TabelaDescricaoVendaRecicleViewAdapter(vendaClicada.getItemVendas(), this);
        recyclerView.setAdapter(tabelaDescricaoVendaRecicleViewAdapter);
        SearchView pesquisaDialog = (SearchView) dialogView.findViewById(R.id.pesquisa);
        pesquisaDialog.setVisibility(View.INVISIBLE);
        TextView title = (TextView) dialogView.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Descrição da Venda");


        final AlertDialog alertDialog = dialogBuilder.create();
        Window window = alertDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER); // set alert dialog in center
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

    }

    private void gerarRelatorio() {

        if (!isValid()) {
            return;
        }


        relatorioVendas.clear();

        if (up.temUsuario()) {
            if (up.getUsuario().getRole().equals("Funcionario")) {
                lojaEscolhida = up.getLoja();
                usuarioEscolhido = up.getUsuario();

            }
        }
        CarregadorDeRelatorio carregadorDeRelatorio = new CarregadorDeRelatorio();
        carregadorDeRelatorio.execute();

    }


    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {

            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "relatorioVenda.pdf");
            //File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"relatorio.pdf");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    //Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (vendaDeletada) {
                    bus.post(new AtualizaListaProdutoEvent());
                }
                finish();
                break;
            case R.id.filtro:
                if (cardViewFiltros.getVisibility() == View.GONE) {
                    Util.expand(cardViewFiltros, null);
                } else {
                    Util.collapse(cardViewFiltros, null);
//                    cardViewFiltros.setVisibility(View.GONE);
                }

                break;


        }
        return super.onOptionsItemSelected(item);
    }

    public void aviso(String tituloAviso, String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RelatorioVendasActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = RelatorioVendasActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.card_avisos, null);
        dialogBuilder.setView(dialogView);

        TextView titulo = (TextView) dialogView.findViewById(R.id.titulo);
        TextView mensagem = (TextView) dialogView.findViewById(R.id.mensagem);
        Button positivo = (Button) dialogView.findViewById(R.id.positivo);
        positivo.setText("Ok");
        Button negativo = (Button) dialogView.findViewById(R.id.negativo);
        negativo.setVisibility(View.GONE);
        mensagem.setText(msg);
        titulo.setText(tituloAviso);

        final AlertDialog alertDialog = dialogBuilder.create();
        Window window = alertDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER); // set alert dialog in center
        alertDialog.setCancelable(false);

        positivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.hide();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    private class CarregadorDeRelatorio extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            llProgressBar.setVisibility(View.VISIBLE);
            relatorioVendas.clear();
            vendasReyclerViewAdapter.notifyDataSetChanged();
            cardViewResumo.setVisibility(View.GONE);


        }

        @Override
        protected String doInBackground(Void... params) {
            List<Venda> vendas = new ArrayList<>();
            de = Util.converteDoFormatoBrasileitoParaDate(dataHoraView.getEditTextDataInicio().getText().toString());
            ate = Util.converteDoFormatoBrasileitoParaDate(dataHoraView.getEditTextDataFim().getText().toString());
            String stringDe = Util.dataNoformatoDoSQLite(de);
            String stringAte = Util.dataNoformatoDoSQLite(ate);
            vendas = vendaDAO.relatorio(stringDe, stringAte, formaDePagamentoEscolhido, lojaEscolhida, usuarioEscolhido);
            vendaDAO.close();
            auxTotal = 0.0;
            auxLucro = 0.0;
            for (Venda venda : vendas) {
                if (formaDePagamentoEscolhido != null) {
                    if (formaDePagamentoEscolhido.equals("dinheiro")) {
                        auxTotal = auxTotal + venda.getTotalDinheiro();
                        auxLucro = auxLucro + venda.getLucro();
                    } else if (formaDePagamentoEscolhido.equals("cartao")) {
                        auxTotal = auxTotal + venda.getTotalCartao();
                        auxLucro = auxLucro + venda.getLucro();
                    }
                } else {
                    auxTotal = auxTotal + venda.getTotalDinheiro() + venda.getTotalCartao();
                    auxLucro = auxLucro + venda.getLucro();
                }
                relatorioVendas.add(venda);

            }

            return "";
        }

        @Override
        protected void onPostExecute(String t) {

            if (up.temUsuario()) {
                if (up.getUsuario().getRole().equals("Funcionario")) {
                    final NumberFormat formatoBrasileiro = DecimalFormat.getCurrencyInstance(new Locale("pt", "br"));
                    tvResumeCardTotal.setText(formatoBrasileiro.format(auxTotal).
                            replace("R$", "R$ ").
                            replace("-R$", "R$ -"));

                } else {
//                total.setText("O total das vendas é R$ " + auxTotal + " reais\n" + "O lucro é R$ " + auxLucro + " reais");
                    BigDecimal totalBigDecimaal = new BigDecimal(auxTotal);
                    final NumberFormat formatoBrasileiro = DecimalFormat.getCurrencyInstance(new Locale("pt", "br"));
                    tvResumeCardTotal.setText(formatoBrasileiro.format(totalBigDecimaal).
                            replace("R$", "R$ ").
                            replace("-R$", "R$ -"));
                    BigDecimal lucroBigDecimal = new BigDecimal(auxLucro);
                    tvResumeCardLucro.setText(formatoBrasileiro.format(lucroBigDecimal).
                            replace("R$", "R$ ").
                            replace("-R$", "R$ -"));
                }
            }


//            listaViewDeVendas.setAdapter(adapterTable);
            vendasReyclerViewAdapter.notifyDataSetChanged();
            cardViewResumo.setVisibility(View.VISIBLE);
            llProgressBar.setVisibility(View.GONE);


        }


    }

    @Override
    public void onBackPressed() {
        if (vendaDeletada) {
            bus.post(new AtualizaListaProdutoEvent());
        }
        super.onBackPressed();
    }


}
