package pitstop.com.br.pitstop.activity.movimentacaoproduto;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.activity.DataHoraView;

import pitstop.com.br.pitstop.activity.movimentacaoproduto.MovimentacaoProdutoAdapter;
import pitstop.com.br.pitstop.dao.MovimentacaoProdutoDAO;

import pitstop.com.br.pitstop.model.MovimentacaoProduto;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelatorioMovimentacaoProdutoActivity extends AppCompatActivity {
    Date de;
    Date ate;


    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private Toolbar toolbar;
    private Button btnGerarRelatorio;
    Button btn_gerar_relatorio_pdf;
    ProgressDialog progressDialog;
    private Snackbar snackbar;
    private LinearLayout linearLayoutRootRelatorioMovimentacao;
    ViewGroup viewRoot;
    DataHoraView dataHoraView;
    CardView cardViewFiltros;
    LinearLayout linearLayoutRelatorioMovimentacaoProduto;


    MovimentacaoProdutoAdapter adapterTable;
    private RecyclerView RecyclerViewMovimentacaoProduto;
    List<MovimentacaoProduto> movimentacoesProdutos = new ArrayList<>();


    MovimentacaoProdutoDAO movimentacaoProdutoDAO = new MovimentacaoProdutoDAO(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_movimentacao_produto);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        viewRoot = (ViewGroup) findViewById(android.R.id.content);
        dataHoraView = new DataHoraView(viewRoot, this);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Relatorio de Movimentações");

        linearLayoutRootRelatorioMovimentacao = (LinearLayout) findViewById(R.id.ll_root_relatorio_movimentacao);
        snackbar = Snackbar.make(linearLayoutRootRelatorioMovimentacao, "", Snackbar.LENGTH_LONG);
        cardViewFiltros = (CardView) findViewById(R.id.card_view_filtros);
        btnGerarRelatorio = (Button) findViewById(R.id.gerar_relatorio);
        btn_gerar_relatorio_pdf = (Button) findViewById(R.id.gerar_relatorio_pdf);
        linearLayoutRelatorioMovimentacaoProduto = findViewById(R.id.ll_relatorio_movimentacao);
        linearLayoutRelatorioMovimentacaoProduto.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        RecyclerViewMovimentacaoProduto = (RecyclerView) findViewById(R.id.lista_de_movimentacaoProduto);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerViewMovimentacaoProduto.setLayoutManager(layoutManager);
        RecyclerViewMovimentacaoProduto.setHasFixedSize(true);
        RecyclerViewMovimentacaoProduto.setNestedScrollingEnabled(true);
        adapterTable = new MovimentacaoProdutoAdapter(movimentacoesProdutos, this);
        RecyclerViewMovimentacaoProduto.setAdapter(adapterTable);

        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        snackbar.setActionTextColor(Color.RED);


        btn_gerar_relatorio_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValid()) {
                    return;
                }

                progressDialog.setMessage("Gerando PDF");
                progressDialog.show();
                Call<ResponseBody> call = new RetrofitInializador().getRelatorioService().relatorioMovimentacaoProduto(dataHoraView.getEditTextDataInicio().getText().toString(), dataHoraView.getEditTextDataFim().getText().toString());
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


        btnGerarRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarRelatorio();

            }
        });
    }

    public void views() {
        progressDialog.setMessage("Preparando para exibir PDF");
        progressDialog.show();
        File pdfFile = new File(getExternalFilesDir(null) + File.separator + "relatorioMovimentacao.pdf");  // -> filename = maven.pdf
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
            Toast.makeText(RelatorioMovimentacaoProdutoActivity.this, "Não existe aplicativo para visualizar o PDF", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {

            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "relatorioMovimentacao.pdf");
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

    public boolean isValid() {
        if (dataHoraView.getEditTextDataInicio().getText().toString().equals("")) {
            dataHoraView.getEditTextDataInicio().setError("Escolha uma data");
            dataHoraView.getEditTextDataInicio().requestFocus();
            snackbar.setText("escolha uma data de inicio");
            snackbar.show();
//            Toast.makeText(RelatorioMovimentacaoProdutoActivity.this, "escolha uma data de inicio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dataHoraView.getEditTextDataFim().getText().toString().equals("")) {
            dataHoraView.getEditTextDataFim().setError("Escolha uma data");
            dataHoraView.getEditTextDataFim().requestFocus();
            snackbar.setText("escolha uma data de Termino");
            snackbar.show();
//            Toast.makeText(RelatorioMovimentacaoProdutoActivity.this, "escolha uma data de termino", Toast.LENGTH_SHORT).show();
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


    private void gerarRelatorio() {

        if (!isValid()) {
            return;
        }
        movimentacoesProdutos.clear();
        if (linearLayoutRelatorioMovimentacaoProduto.getVisibility() == View.GONE) {
            Util.expand(linearLayoutRelatorioMovimentacaoProduto, null);
        }
        else{
            Util.collapse(linearLayoutRelatorioMovimentacaoProduto, null);
            Util.expand(linearLayoutRelatorioMovimentacaoProduto, null);
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


        movimentacoesProdutos.addAll(movimentacaoProdutoDAO.relatorio(stringDe, stringAte));
        movimentacaoProdutoDAO.close();
        adapterTable.notifyDataSetChanged();



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
                if (cardViewFiltros.getVisibility() == View.GONE) {
                    Util.expand(cardViewFiltros, null);
//                    cardViewFiltros.setVisibility(View.VISIBLE);
                } else {
                    Util.collapse(cardViewFiltros, null);
//                    cardViewFiltros.setVisibility(View.GONE);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
