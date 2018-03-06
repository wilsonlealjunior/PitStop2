package pitstop.com.br.pitstop.activity.relatorio;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import pitstop.com.br.pitstop.activity.DataHoraView;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelatorioGeralActivity extends AppCompatActivity {
    Date de;
    Date ate;
    static final long VINTE_E_SEIS_HORAS_EM_MILISEGUNDO = 93600000;

    UsuarioPreferences usuarioPreferencia = new UsuarioPreferences(this);
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private Toolbar toolbar;
    private Button btnGerarRelatorio;
    private Snackbar snackbar;
    private LinearLayout linearLayoutRootRelatorioGeral;

    DataHoraView dataHoraView;
    private TextView total;
    TextView textViewLoja;

    List<String> labelsLojas = new ArrayList<>();
    List<Loja> lojas;
    Loja lojaEscolhida;
    Spinner lojaSpinner;
    LojaDAO lojaDAO = new LojaDAO(this);
    Button btnGerarRelatorioPDF;
    ProgressDialog progressDialog;
    CardView cardViewFiltros;
    ViewGroup viewRoot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_geral);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        viewRoot = (ViewGroup) findViewById(android.R.id.content);
        dataHoraView = new DataHoraView(viewRoot, this);

        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        if (lojas.size() == 0) {
            Toast.makeText(RelatorioGeralActivity.this, "Não existe usuarios cadastradas", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        labelsLojas.add("Todas");
        for (Loja l : lojas) {
            labelsLojas.add(l.getNome());
        }
        ArrayAdapter<String> spinnerAdapterLoja = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLojas);
        spinnerAdapterLoja.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lojaSpinner = (Spinner) findViewById(R.id.spinner_loja);
        lojaSpinner.setAdapter(spinnerAdapterLoja);
        textViewLoja = (TextView) findViewById(R.id.tv_loja);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Relatorio Geral");

        linearLayoutRootRelatorioGeral = (LinearLayout) findViewById(R.id.ll_root_relatorio_geral);
        snackbar = Snackbar.make(linearLayoutRootRelatorioGeral, "", Snackbar.LENGTH_LONG);
        total = (TextView) findViewById(R.id.total);


        btnGerarRelatorio = (Button) findViewById(R.id.gerar_relatorio);
        btnGerarRelatorioPDF = (Button) findViewById(R.id.gerar_relatorio_pdf);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);



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
        if (usuarioPreferencia.temUsuario()) {
            if (usuarioPreferencia.getUsuario().getRole().equals("Funcionario")) {
                lojaSpinner.setVisibility(View.GONE);
                lojaEscolhida = usuarioPreferencia.getLoja();
                textViewLoja.setVisibility(View.GONE);
            }
        }


        btnGerarRelatorioPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String lojaId;

                if (!isValid()) {
                    return;
                }
                if (lojaEscolhida == null) {
                    lojaId = "%";
                } else {
                    lojaId = lojaEscolhida.getId();
                }
                Call<ResponseBody> call = null;
                progressDialog.setMessage("Gerando PDF");
                progressDialog.show();
                if (usuarioPreferencia.temUsuario()) {
                    Usuario u = usuarioPreferencia.getUsuario();
                    if (usuarioPreferencia.getUsuario().getRole().equals("Funcionario")) {
                        call = new RetrofitInializador().getRelatorioService().relatorioGeralFuncionario(usuarioPreferencia.getLoja().getId(), usuarioPreferencia.getUsuario().getNome(), dataHoraView.getEditTextDataInicio().getText().toString(), dataHoraView.getEditTextDataFim().getText().toString());
                    } else {
                        call = new RetrofitInializador().getRelatorioService().relatorioGeral(lojaId, dataHoraView.getEditTextDataInicio().getText().toString(), dataHoraView.getEditTextDataFim().getText().toString());

                    }
                }
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
//            Toast.makeText(RelatorioGeralActivity.this, "escolha uma data de inicio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dataHoraView.getEditTextDataFim().getText().toString().equals("")) {
            dataHoraView.getEditTextDataFim().setError("Escolha uma data");
            dataHoraView.getEditTextDataFim().requestFocus();
            snackbar.setText("escolha uma data de termino");
            snackbar.show();
//            Toast.makeText(RelatorioGeralActivity.this, "escolha uma data de termino", Toast.LENGTH_SHORT).show();
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

            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "relatorioGeral.pdf");
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
        File pdfFile = new File(getExternalFilesDir(null) + File.separator + "relatorioGeral.pdf");  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
            progressDialog.dismiss();
        } catch (ActivityNotFoundException e) {
//            snackbar.setText("\"Não existe aplicativo para visualizar o PDF\"");
//            snackbar.show();
            Toast.makeText(RelatorioGeralActivity.this, "Não existe aplicativo para visualizar o PDF", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.filtro:
                if (dataHoraView.getEditTextDataInicio().getVisibility() == View.GONE) {
                    dataHoraView.getEditTextDataInicio().setVisibility(View.VISIBLE);
                } else {
                    dataHoraView.getEditTextDataInicio().setVisibility(View.GONE);
                }
                if (dataHoraView.getEditTextDataFim().getVisibility() == View.GONE) {
                    dataHoraView.getEditTextDataFim().setVisibility(View.VISIBLE);
                } else {
                    dataHoraView.getEditTextDataFim().setVisibility(View.GONE);
                }
                if (btnGerarRelatorio.getVisibility() == View.GONE) {
                    btnGerarRelatorio.setVisibility(View.VISIBLE);
                } else {
                    btnGerarRelatorio.setVisibility(View.GONE);

                }
                if (btnGerarRelatorioPDF.getVisibility() == View.GONE) {
                    btnGerarRelatorioPDF.setVisibility(View.VISIBLE);
                } else {
                    btnGerarRelatorioPDF.setVisibility(View.GONE);
                }
                if (lojaSpinner.getVisibility() == View.GONE) {
                    lojaSpinner.setVisibility(View.VISIBLE);
                } else {
                    lojaSpinner.setVisibility(View.GONE);

                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }

}

