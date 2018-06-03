package pitstop.com.br.pitstop.activity.furo;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import okhttp3.ResponseBody;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.activity.DataHoraView;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.FuroDAO;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Furo;
import pitstop.com.br.pitstop.model.ItemFuro;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.RealmString;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelatorioFuroActivity extends AppCompatActivity {
    Date de;
    Date ate;
    Furo furoClicado;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private Toolbar toolbar;
    private Button btnGerarRelatorio;
    private Snackbar snackbar;
    private LinearLayout linearLayoutRootRelatorioFuro;
    List<Furo> relatorioFuros = new ArrayList<>();
    List<Furo> furos = new ArrayList<>();
    private TextView total;

    FuroAdapter adapterTable;
    private RecyclerView furoRecylerView;


    List<String> labelsLojas = new ArrayList<>();
    List<Loja> lojas;
    Loja lojaEscolhida;
    Usuario funcionarioEscolhido;
    List<String> labelsUsuarios = new ArrayList<>();
    List<Usuario> usuarios;
    Spinner lojaSpinner;
    Spinner funcionarioSpinner;
    LojaDAO lojaDAO = new LojaDAO(this);
    FuroDAO furoDAO = new FuroDAO(this);
    UsuarioDAO usuarioDAO = new UsuarioDAO(this);
    Button btnGerarRelatorioPDF;
    ProgressDialog progressDialog;
    private Boolean furoDeletada = false;
    private EventBus bus = EventBus.getDefault();
    CardView cardViewFiltros;
    ViewGroup viewRoot;
    DataHoraView dataHoraView;

    CardView cardViewSelecionarFiltros;
    LinearLayout relatorioFiltrosView;
    LinearLayout linearLayoutRelatorioFuro;
    Button btnNovaConsulta;
    TextView nenhumRegistroEncontrado;
    LinearLayout llRelatorioFormaDePagamento;
    TextView relatorioLoja;
    TextView relatorioFuncionario;
    TextView relatorioDataInicial;
    TextView relatorioaDataFinal;
    TextView relatorioTotal;
    LinearLayout llRelatorioLucro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_furo);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        viewRoot = (ViewGroup) findViewById(android.R.id.content);
        dataHoraView = new DataHoraView(viewRoot, this);

        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        if (lojas.size() == 0) {
            Toast.makeText(RelatorioFuroActivity.this, "Não existe usuarios cadastradas", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }
        labelsLojas.add("Todas");
        for (Loja l : lojas) {
            labelsLojas.add(l.getNome());
        }
        usuarios = usuarioDAO.listarUsuarios();
        usuarioDAO.close();
        labelsUsuarios.add("Todos");
        for (Usuario u : usuarios) {
            labelsUsuarios.add(u.getNome());
        }

        ArrayAdapter<String> spinnerAdapterFuncionario = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsUsuarios);
        spinnerAdapterFuncionario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        funcionarioSpinner = (Spinner) findViewById(R.id.spinner_funcionario);
        funcionarioSpinner.setAdapter(spinnerAdapterFuncionario);

        ArrayAdapter<String> spinnerAdapterLoja = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLojas);
        spinnerAdapterLoja.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lojaSpinner = (Spinner) findViewById(R.id.spinner_loja);
        lojaSpinner.setAdapter(spinnerAdapterLoja);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Relatorio de Furo");

        linearLayoutRootRelatorioFuro = (LinearLayout) findViewById(R.id.ll_root_relatorio_furo);
        snackbar = Snackbar.make(linearLayoutRootRelatorioFuro, "", Snackbar.LENGTH_LONG);
        total = (TextView) findViewById(R.id.total);

        relatorioFiltrosView = (LinearLayout) findViewById(R.id.filtros_view);
        relatorioFiltrosView.setVisibility(View.GONE);
        linearLayoutRelatorioFuro = findViewById(R.id.ll_relatorio_furo);
        linearLayoutRelatorioFuro.setVisibility(View.GONE);
        btnNovaConsulta = findViewById(R.id.nova_consulta);
        nenhumRegistroEncontrado = findViewById(R.id.nenhum_registro_encontrado);
        cardViewSelecionarFiltros = (CardView) findViewById(R.id.card_view_filtros);
        relatorioaDataFinal = findViewById(R.id.relatorio_data_final);
        relatorioDataInicial = findViewById(R.id.relatorio_data_inicial);
        relatorioLoja = findViewById(R.id.relatorio_loja);
        llRelatorioFormaDePagamento = findViewById(R.id.ll_relatorio_forma_pagamento);
        relatorioTotal = findViewById(R.id.relatorio_total);
        llRelatorioLucro = findViewById(R.id.ll_relatorio_lucro);
        relatorioFuncionario = findViewById(R.id.relatorio_funcionario);
        llRelatorioLucro.setVisibility(View.GONE);
        llRelatorioFormaDePagamento.setVisibility(View.GONE);

        btnGerarRelatorio = (Button) findViewById(R.id.gerar_relatorio);
        btnGerarRelatorioPDF = (Button) findViewById(R.id.gerar_relatorio_pdf);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        cardViewFiltros = (CardView) findViewById(R.id.card_view_filtros);


        furoRecylerView = (RecyclerView) findViewById(R.id.lista_de_furo);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        furoRecylerView.setLayoutManager(layoutManager);
        furoRecylerView.setHasFixedSize(true);
        furoRecylerView.setNestedScrollingEnabled(true);
        adapterTable = new FuroAdapter(relatorioFuros, this);
        furoRecylerView.setAdapter(adapterTable);


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
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        snackbar.setActionTextColor(Color.RED);
        funcionarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    funcionarioEscolhido = usuarios.get(i - 1);
                } else {
                    funcionarioEscolhido = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnGerarRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarRelatorio();

            }
        });

        adapterTable.setOnItemClickListener(new FuroAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                furoClicado = (Furo) relatorioFuros.get(position);
                Log.e("Furo clicada", String.valueOf(position));
                ShowCustomDialogwithList();

            }
        });
        adapterTable.setOnItemDeleteListener(new FuroAdapter.ItemDeleteListener() {
            @Override
            public void onItemDelete(final int position) {
                Util.alert(RelatorioFuroActivity.this, "Deletar Furo", "Deseja deletar a Furo?", "Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Furo furo = (Furo) relatorioFuros.get(position);
                        deletarFuro(furo);
                    }
                }, "Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }, null, null);


            }
        });
        btnNovaConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relatorioFuros.clear();
                adapterTable.notifyDataSetChanged();
                trocarCards();

            }
        });
        btnGerarRelatorioPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!isValid()) {
                    return;
                }

                String lojaId;
                String funcionarioId;


                if (lojaEscolhida == null) {
                    lojaId = "%";
                } else {
                    lojaId = lojaEscolhida.getId();
                }
                if (funcionarioEscolhido == null) {
                    funcionarioId = "%";
                } else {
                    funcionarioId = funcionarioEscolhido.getNome();
                }
                progressDialog.setMessage("Gerando PDF");
                progressDialog.show();
                Call<ResponseBody> call = new RetrofitInializador().getRelatorioService().relatorioFuro(lojaId, funcionarioId, dataHoraView.getEditTextDataInicio().getText().toString(), dataHoraView.getEditTextDataFim().getText().toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("TAG", "server contacted and has file");

                            boolean writtenToDisk = writeResponseBodyToDisk(response.body());
//                            snackbar.setText("PDF gerado com sucesso");
//                            snackbar.show();
                            Toast.makeText(getApplicationContext(), "PDF gerado com sucesso", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            views();

                            Log.d("TAG", "file download was a success? " + writtenToDisk);
                        } else {
                            Log.d("TAG", "server contact failed");
                            progressDialog.dismiss();
//                            snackbar.setText("Erro ao gerar o pdf");
//                            snackbar.show();
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

    }

    public boolean isValid() {
        if (dataHoraView.getEditTextDataInicio().getText().toString().equals("")) {
            dataHoraView.getEditTextDataInicio().setError("Escolha uma data");
            dataHoraView.getEditTextDataInicio().requestFocus();
            snackbar.setText("escolha uma data de inicio");
            snackbar.show();
//            Toast.makeText(RelatorioFuroActivity.this, "escolha uma data de inicio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dataHoraView.getEditTextDataFim().getText().toString().equals("")) {
            dataHoraView.getEditTextDataFim().setError("Escolha uma data");
            dataHoraView.getEditTextDataFim().requestFocus();
            snackbar.setText("escolha uma data de termino");
            snackbar.show();
//            Toast.makeText(RelatorioFuroActivity.this, "escolha uma data de termino", Toast.LENGTH_SHORT).show();
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


    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {

            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "relatorioFuro.pdf");
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

    public void views() {
        progressDialog.setMessage("Preparando para exibir PDF");
        progressDialog.show();
        File pdfFile = new File(getExternalFilesDir(null) + File.separator + "relatorioFuro.pdf");  // -> filename = maven.pdf
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
            Toast.makeText(RelatorioFuroActivity.this, "Não existe aplicativo para visualizar o PDF", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    // Custom Dialog with List
    private void ShowCustomDialogwithList() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RelatorioFuroActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = RelatorioFuroActivity.this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.alert_dialog_recycler_view, null);
        dialogBuilder.setView(dialogView);

        RecyclerView recyclerView;
        DescricaoFuroAdapter descricaoFuroAdapter;
        recyclerView = (RecyclerView) dialogView.findViewById(R.id.listview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        descricaoFuroAdapter = new DescricaoFuroAdapter(furoClicado.getFuroEntradeProdutos(), this);
        recyclerView.setAdapter(descricaoFuroAdapter);
        SearchView pesquisaDialog = (SearchView) dialogView.findViewById(R.id.pesquisa);
        pesquisaDialog.setVisibility(View.INVISIBLE);
        TextView title = (TextView) dialogView.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Descrição da Furo");

        // Defined Array values to show in ListView


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

    }

    private void gerarRelatorio() {

        if (!isValid()) {
            return;
        }

        String lojaId;
        String funcionarioId;
        if (lojaEscolhida == null) {
            lojaId = "%";
            relatorioLoja.setText("Todas");
        } else {
            relatorioLoja.setText(lojaEscolhida.getNome());
            lojaId = lojaEscolhida.getId();
        }
        if (funcionarioEscolhido == null) {
            relatorioFuncionario.setText("Todos");
            funcionarioId = "%";
        } else {
            relatorioFuncionario.setText(funcionarioEscolhido.getNome());
            funcionarioId = funcionarioEscolhido.getNome();
        }


        try {
            de = formatter.parse(dataHoraView.getEditTextDataInicio().getText().toString());
            ate = formatter.parse(dataHoraView.getEditTextDataFim().getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stringDe = formatter1.format(de);
        String stringAte = formatter1.format(ate);
        relatorioDataInicial.setText(Util.dataComDiaEHoraPorExtenso(de.getTime()));
        relatorioaDataFinal.setText(Util.dataComDiaEHoraPorExtenso(ate.getTime()));
        relatorioFuros.clear();
        furos.clear();
        trocarCards();

        furos.addAll(furoDAO.relatorio(stringDe, stringAte, lojaId, funcionarioId));
        furoDAO.close();
        double auxFuro = 0.0;
        for (Furo furo : furos) {
            auxFuro += furo.getValor();
            relatorioFuros.add(furo);
        }
        if (!relatorioFuros.isEmpty()) {
            nenhumRegistroEncontrado.setVisibility(View.GONE);
        }
        final NumberFormat formatoBrasileiro = DecimalFormat.getCurrencyInstance(new Locale("pt", "br"));
        relatorioTotal.setText(formatoBrasileiro.format(auxFuro).
                replace("R$", "R$ ").
                replace("-R$", "R$ -"));
//        total.setText("O total dos furos é R$ " + auxFuro + " reais");


        furoRecylerView.setAdapter(adapterTable);
        adapterTable.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);
        if (cardViewSelecionarFiltros.getVisibility() == View.VISIBLE)
            toolbar.getMenu().findItem(R.id.filtro).setVisible(false);
        else
            toolbar.getMenu().findItem(R.id.filtro).setVisible(true);
        return super.onCreateOptionsMenu(menu);

    }

    private void trocarCards() {
        if (cardViewSelecionarFiltros.getVisibility() == View.VISIBLE) {
            Util.collapse(cardViewSelecionarFiltros, null);
            Util.expand(relatorioFiltrosView, null);
            Util.expand(linearLayoutRelatorioFuro, null);
            toolbar.getMenu().findItem(R.id.filtro).setVisible(true);
        } else {
            Util.expand(cardViewSelecionarFiltros, null);
            Util.collapse(relatorioFiltrosView, null);
            Util.collapse(linearLayoutRelatorioFuro, null);
            toolbar.getMenu().findItem(R.id.filtro).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (cardViewSelecionarFiltros.getVisibility() == View.VISIBLE) {
                    if (furoDeletada) {
                        bus.post(new AtualizaListaProdutoEvent());
                    }
                    finish();
                } else {
                    relatorioFuros.clear();
                    adapterTable.notifyDataSetChanged();
                    trocarCards();

                }
                break;
            case R.id.filtro:
                if (relatorioFiltrosView.getVisibility() == View.GONE) {
                    Util.expand(relatorioFiltrosView, null);
                } else {
                    Util.collapse(relatorioFiltrosView, null);
//                    cardViewSelecionarFiltros.setVisibility(View.GONE);
                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    private void deletarFuro(Furo furo) {
        for (Furo f : relatorioFuros) {
            if (f.getId().equals(furo.getId())) {
                f.desativar();
                f.desincroniza();
                EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(getApplicationContext());
                ProdutoDAO produtoDAO = new ProdutoDAO(getApplicationContext());
                if ((f.getFuroEntradeProdutos() != null) && (!f.getFuroEntradeProdutos().isEmpty())) {
                    for (ItemFuro itemFuro : f.getFuroEntradeProdutos()) {
                        int quantidade = itemFuro.getQuantidade();
                        EntradaProduto entradaProduto = entradaProdutoDAO.procuraPorId(itemFuro.getIdEntradaProduto());
                        entradaProdutoDAO.close();
                        entradaProduto.setQuantidadeVendidaMovimentada(entradaProduto.getQuantidadeVendidaMovimentada() - quantidade);
                        Produto produto = entradaProduto.getProduto();
                        for (RealmString produtoVinculoId : produto.getIdProdutoVinculado()) {
                            Produto p = produtoDAO.procuraPorId(produtoVinculoId.getValor());
                            produtoDAO.close();
                            p.setQuantidade(p.getQuantidade() + quantidade);
                            p.desincroniza();
                            produtoDAO.altera(p);
                            produtoDAO.close();
                        }
                        produto.setQuantidade(produto.getQuantidade() + quantidade);
                        entradaProduto.desincroniza();
                        produto.desincroniza();
                        entradaProdutoDAO.altera(entradaProduto);
                        entradaProdutoDAO.close();
                        produtoDAO.altera(produto);
                        produtoDAO.close();


                    }
                }


                furoDAO.altera(f);
                furoDAO.close();
                adapterTable.notifyDataSetChanged();
                break;

            }


        }
        if (relatorioFuros.remove(furo)) {
            snackbar.setText("Furo deletada");
            furoDeletada = true;
            snackbar.show();
//                        Toast.makeText(RelatorioFuroActivity.this, "Furo deletada", Toast.LENGTH_SHORT).show();
            furoRecylerView.setAdapter(adapterTable);
            adapterTable.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (furoDeletada) {
            bus.post(new AtualizaListaProdutoEvent());
        }
        super.onBackPressed();
    }

}
