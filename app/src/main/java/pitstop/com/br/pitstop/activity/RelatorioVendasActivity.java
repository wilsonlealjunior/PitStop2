package pitstop.com.br.pitstop.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
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
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.adapter.LstViewTabelaDescricaoVendaAdapter;
import pitstop.com.br.pitstop.adapter.LstViewTabelaRelatorioVendas;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.dao.VendaDAO;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.AvariaEntradaProduto;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.model.Venda;
import pitstop.com.br.pitstop.model.VendaEntradaProduto;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelatorioVendasActivity extends AppCompatActivity {

    static final long VINTE_E_SEIS_HORAS_EM_MILISEGUNDO = 93600000;
    static final long TEMPO_EM_MINUTO_PARA_FUNCIONARIO_DELETAR_VENDA = 10;

    Date de;
    Date ate;
    Venda vendaClicada;
    boolean deletarVenda = true;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    Calendar dateTimeDe = Calendar.getInstance();
    Calendar dateTimeAte = Calendar.getInstance();

    private TextView mDisplayDateDe;
    private TextView total;
    private DatePickerDialog.OnDateSetListener mDateSetListenerDe;
    private TimePickerDialog.OnTimeSetListener mHoraSetListenerDe;

    private TextView mDisplayDateAte;
    private DatePickerDialog.OnDateSetListener mDateSetListenerAte;
    private TimePickerDialog.OnTimeSetListener mHoraSetListenerAte;
    private Toolbar toolbar;
    private Button btnGerarRelatorio;
    List<Venda> relatorioVendas = new ArrayList<>();


    LstViewTabelaRelatorioVendas adapterTable;
    private ListView listaViewDeVendas;

    String[] formaDePagamento = new String[]{"Forma de Pagamento", "dinheiro", "cartao"};
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
    Button btn_gerar_relatorio_pdf;
    ProgressDialog progressDialog;
    EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_vendas);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        lojas = lojaDAO.listarLojas();
        if (lojas.size() == 0) {
            Toast.makeText(RelatorioVendasActivity.this, "Não existe usuarios cadastradas", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }
        labelslojas.add("Escolha a loja");
        for (Loja l : lojas) {
            labelslojas.add(l.getNome());
        }
        usuarios = usuarioDAO.listarUsuarios();
        labelsUsuarios.add("Escolha um usuario");
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

        total = (TextView) findViewById(R.id.total);
        mDisplayDateDe = (TextView) findViewById(R.id.dataDe);
        mDisplayDateAte = (TextView) findViewById(R.id.dataAte);
        btnGerarRelatorio = (Button) findViewById(R.id.gerar_relatorio);
        btn_gerar_relatorio_pdf = (Button) findViewById(R.id.gerar_relatorio_pdf);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mDisplayDateDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDateDe();
            }
        });
        mDisplayDateAte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDateAte();
            }
        });

        listaViewDeVendas = (ListView) findViewById(R.id.lista_de_vendas);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_relatorio_vendas, listaViewDeVendas, false);
        TextView lucro = headerView.findViewById(R.id.lucro);
        listaViewDeVendas.addHeaderView(headerView);

        adapterTable = new LstViewTabelaRelatorioVendas(this, R.layout.tabela_relatorio_vendas, R.id.quantidade, relatorioVendas);
        registerForContextMenu(listaViewDeVendas);

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

        mDateSetListenerDe = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTimeDe.set(Calendar.YEAR, year);
                dateTimeDe.set(Calendar.MONTH, monthOfYear);
                dateTimeDe.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateTextLabelDe();
            }
        };
        mHoraSetListenerDe = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateTimeDe.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateTimeDe.set(Calendar.MINUTE, minute);
                updateTextLabelDe();
            }
        };


        mDateSetListenerAte = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Date d = new Date();
                dateTimeAte.set(Calendar.YEAR, year);
                dateTimeAte.set(Calendar.MONTH, monthOfYear);
                dateTimeAte.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateTextLabelAte();
            }
        };
        mHoraSetListenerAte = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateTimeAte.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateTimeAte.set(Calendar.MINUTE, minute);
                updateTextLabelAte();
            }
        };
        //updateTextLabel();
        btn_gerar_relatorio_pdf.setOnClickListener(new View.OnClickListener() {
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
                        call = new RetrofitInializador().getRelatorioService().relatorioVendasFuncionario(formaDePagamentoEscolhido, Idloja, funcionario, mDisplayDateDe.getText().toString(), mDisplayDateAte.getText().toString());
                    } else {
                        call = new RetrofitInializador().getRelatorioService().relatorioVendas(formaDePagamentoEscolhido, Idloja, funcionario, mDisplayDateDe.getText().toString(), mDisplayDateAte.getText().toString());

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
                        Toast.makeText(getApplicationContext(), "Verifique a conexao com a internet", Toast.LENGTH_SHORT).show();

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


        listaViewDeVendas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                vendaClicada = (Venda) listaViewDeVendas.getItemAtPosition(position);

                ShowCustomDialogwithList();

            }
        });

        if (up.temUsuario()) {
            if (up.getUsuario().getRole().equals("Funcionario")) {
                lojaSpinner.setVisibility(View.GONE);
                funcionarioSpinner.setVisibility(View.GONE);
                lucro.setVisibility(View.GONE);
                Date ate = new Date();
                long longate = ate.getTime();
                long longDe = longate - VINTE_E_SEIS_HORAS_EM_MILISEGUNDO;
                Date de = new Date(longDe);
                mDisplayDateDe.setText(formatter.format(de));
                mDisplayDateAte.setText(formatter.format(ate));
                mDisplayDateDe.setFocusable(false);
                mDisplayDateAte.setFocusable(false);
                mDisplayDateDe.setClickable(false);
                mDisplayDateAte.setClickable(false);


            }

        }


    }

    public boolean isValid() {

        if (mDisplayDateDe.getText().toString().equals("")) {
            mDisplayDateDe.setError("Escolha uma data");
            mDisplayDateDe.requestFocus();
            Toast.makeText(RelatorioVendasActivity.this, "escolha uma data de inicio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mDisplayDateAte.getText().toString().equals("")) {
            mDisplayDateAte.setError("Escolha uma data");
            mDisplayDateAte.requestFocus();
            Toast.makeText(RelatorioVendasActivity.this, "escolha uma data de termino", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            de = formatter.parse(mDisplayDateDe.getText().toString());
            ate = formatter.parse(mDisplayDateAte.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (de.after(ate)) {
            Toast.makeText(getApplicationContext(), "A data de origem deve ser menor do que a data final ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
            Toast.makeText(RelatorioVendasActivity.this, "Não existe aplicativo para visualizar o PDF", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }


    // Custom Dialog with List
    private void ShowCustomDialogwithList() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RelatorioVendasActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = RelatorioVendasActivity.this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.alert_dialog_personalizado_lstview, null);
        dialogBuilder.setView(dialogView);

        final ListView listView = (ListView) dialogView.findViewById(R.id.listview);
        SearchView pesquisaDialog = (SearchView) dialogView.findViewById(R.id.pesquisa);
        pesquisaDialog.setVisibility(View.INVISIBLE);
        TextView title = (TextView) dialogView.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Descrição da Venda");
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_descricao_venda, listView, false);
        TextView lucro = headerView.findViewById(R.id.lucro);
        if (up.temUsuario()) {
            if (up.getUsuario().getRole().equals("Funcionario")) {
                lucro.setText("Total");
            }
        }

        listView.addHeaderView(headerView);

        // Defined Array values to show in ListView

        LstViewTabelaDescricaoVendaAdapter adapterp = new LstViewTabelaDescricaoVendaAdapter(this, R.layout.tabela_descricao_venda, R.id.quantidade, vendaClicada.getVendaEntradaProdutos());
        listView.setAdapter(adapterp);


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


//        if (formaDePagamentoEscolhido == null && lojaEscolhida == null) {
//            vendas = vendaDAO.listarVendas();
//            vendaDAO.close();
//        } else if (formaDePagamentoEscolhido != null && lojaEscolhida == null) {
//            vendas = vendaDAO.buscaPorPagamento(formaDePagamentoEscolhido);
//            vendaDAO.close();
//        } else if (formaDePagamentoEscolhido == null && lojaEscolhida != null) {
//            vendas = vendaDAO.buscaPorLoja(lojaEscolhida.getId());
//            vendaDAO.close();
//        } else {
//            vendas = vendaDAO.buscaPorPagamentoELoja(formaDePagamentoEscolhido, lojaEscolhida.getId());
//            vendaDAO.close();
//        }

        try {
            de = formatter.parse(mDisplayDateDe.getText().toString());
            ate = formatter.parse(mDisplayDateAte.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stringDe = formatter.format(de);
        String stringAte = formatter.format(ate);
        relatorioVendas.clear();
        List<Venda> vendas = new ArrayList<>();
        if (up.temUsuario()) {
            if (up.getUsuario().getRole().equals("Funcionario")) {
                lojaEscolhida = up.getLoja();
                usuarioEscolhido = up.getUsuario();

            }
        }
        vendas = vendaDAO.relatorio(stringDe, stringAte, formaDePagamentoEscolhido, lojaEscolhida, usuarioEscolhido);
        vendaDAO.close();
        double auxTotal = 0.0;
        double auxLucro = 0.0;
        for (Venda venda : vendas) {

            auxTotal = auxTotal + venda.getTotal();
            auxLucro = auxLucro + venda.getLucro();
            relatorioVendas.add(venda);

        }

        if (up.temUsuario()) {
            if (up.getUsuario().getRole().equals("Funcionario")) {
                total.setText("O total das vendas é R$ " + auxTotal + " reais\n");

            } else {
                total.setText("O total das vendas é R$ " + auxTotal + " reais\n" + "O lucro é R$ " + auxLucro + " reais");

            }
        }


        listaViewDeVendas.setAdapter(adapterTable);
        adapterTable.notifyDataSetChanged();
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

    private void updateDateDe() {
        new TimePickerDialog(this, mHoraSetListenerDe, dateTimeDe.get(Calendar.HOUR_OF_DAY), dateTimeDe.get(Calendar.MINUTE), true).show();
        new DatePickerDialog(this, mDateSetListenerDe, dateTimeDe.get(Calendar.YEAR), dateTimeDe.get(Calendar.MONTH), dateTimeDe.get(Calendar.DAY_OF_MONTH)).show();


    }

    private void updateDateAte() {

        new TimePickerDialog(this, mHoraSetListenerAte, dateTimeAte.get(Calendar.HOUR_OF_DAY), dateTimeAte.get(Calendar.MINUTE), true).show();
        new DatePickerDialog(this, mDateSetListenerAte, dateTimeAte.get(Calendar.YEAR), dateTimeAte.get(Calendar.MONTH), dateTimeAte.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void updateTextLabelDe() {
        mDisplayDateDe.setText(formatter.format(dateTimeDe.getTime()));
    }

    private void updateTextLabelAte() {
        mDisplayDateAte.setText(formatter.format(dateTimeAte.getTime()));
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
                finish();
                break;
            case R.id.filtro:
                if (mDisplayDateAte.getVisibility() == View.GONE) {
                    mDisplayDateDe.setVisibility(View.VISIBLE);
                } else {
                    mDisplayDateDe.setVisibility(View.GONE);
                }
                if (mDisplayDateAte.getVisibility() == View.GONE) {
                    mDisplayDateAte.setVisibility(View.VISIBLE);
                } else {
                    mDisplayDateAte.setVisibility(View.GONE);
                }
                if (btnGerarRelatorio.getVisibility() == View.GONE) {
                    btnGerarRelatorio.setVisibility(View.VISIBLE);
                } else {
                    btnGerarRelatorio.setVisibility(View.GONE);

                }
                if (btn_gerar_relatorio_pdf.getVisibility() == View.GONE) {
                    btn_gerar_relatorio_pdf.setVisibility(View.VISIBLE);
                } else {
                    btn_gerar_relatorio_pdf.setVisibility(View.GONE);

                }
                if (up.temUsuario()) {
                    if (up.getUsuario().getRole().equals("Administrador")) {
                        if (lojaSpinner.getVisibility() == View.GONE) {
                            lojaSpinner.setVisibility(View.VISIBLE);
                        } else {
                            lojaSpinner.setVisibility(View.GONE);

                        }
                        if (funcionarioSpinner.getVisibility() == View.GONE) {
                            funcionarioSpinner.setVisibility(View.VISIBLE);
                        } else {
                            funcionarioSpinner.setVisibility(View.GONE);

                        }

                    }
                }


                if (formaDePagamentoSpinnner.getVisibility() == View.GONE) {
                    formaDePagamentoSpinnner.setVisibility(View.VISIBLE);
                } else {
                    formaDePagamentoSpinnner.setVisibility(View.GONE);

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    final ContextMenu.ContextMenuInfo menuInfo) {
        final MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                Venda venda = (Venda) listaViewDeVendas.getItemAtPosition(info.position);
                if (info.position != 0) {
                    for (Venda v : relatorioVendas) {
                        if (v.getId().equals(venda.getId())) {
                            SimpleDateFormat formatterData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date dataDaVenda = null;
                            try {
                                dataDaVenda = formatterData.parse(v.getDataDaVenda());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date agora = new Date();

                            long ldt1 = dataDaVenda.getTime();
                            long ldt2 = agora.getTime();
                            long res = (ldt2 - ldt1) / (1000 * 60);
                            if (res > TEMPO_EM_MINUTO_PARA_FUNCIONARIO_DELETAR_VENDA) {
                                if (up.temUsuario()) {
                                    if (up.getUsuario().getRole().equals("Funcionario")) {
                                        deletarVenda = false;

                                    }
                                }
                            }

                            if (deletarVenda) {
                                v.desativar();
                                v.desincroniza();
                                EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(getApplicationContext());
                                ProdutoDAO produtoDAO = new ProdutoDAO(getApplicationContext());
                                if ((v.getVendaEntradaProdutos() != null) && (!v.getVendaEntradaProdutos().isEmpty())) {
                                    for (VendaEntradaProduto vendaEntradaProduto : v.getVendaEntradaProdutos()) {
                                        int quantidade = vendaEntradaProduto.getQuantidadeVendida();
                                        EntradaProduto entradaProduto = entradaProdutoDAO.procuraPorId(vendaEntradaProduto.getIdEntradaProduto());
                                        entradaProdutoDAO.close();
                                        entradaProduto.setQuantidadeVendidaMovimentada(entradaProduto.getQuantidadeVendidaMovimentada() - quantidade);
                                        Produto produto = entradaProduto.getProduto();

                                        for (String produtoVinculoId : produto.getIdProdutoVinculado()) {
                                            Produto p = produtoDAO.procuraPorId(produtoVinculoId);
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
                                if (relatorioVendas.remove(venda)) {
//                                    Toast.makeText(RelatorioVendasActivity.this, "Venda deletada", Toast.LENGTH_SHORT).show();
                                    listaViewDeVendas.setAdapter(adapterTable);
                                    adapterTable.notifyDataSetChanged();
                                }

                                adapterTable.notifyDataSetChanged();
                                vendaDAO.altera(v);
                                vendaDAO.close();
                                aviso("Aviso", "Venda deletada com sucesso");
                                break;
                            } else {
                                aviso("Aviso", "Voce não pode fazer a exclusão pois ja se passaram mais de 10 minutos");

                            }

                        }


                    }

                }


                return false;
            }
        });
    }


}
