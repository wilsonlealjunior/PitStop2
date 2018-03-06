package pitstop.com.br.pitstop;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.activity.NavigationViewActivity;

import pitstop.com.br.pitstop.activity.NavigationViewFuncionarioActivity;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.ObjetosSink;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;

import pitstop.com.br.pitstop.sic.ObjetosSinkSincronizador;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    //Administrador descomenta aqui
    Spinner spinerLojas;


    private EditText inputUsuario, inputPassword;
    private ProgressBar progressBar;
    private Button btnLogin, btnReset;
    Loja lojaEscolhida;
    TextView tvLoja;
    List<Loja> lojas;
    List<String> labelsLojas = new ArrayList<>();
    LojaDAO lojaDAO = new LojaDAO(this);
    //Administrador descomenta aqui
        ArrayAdapter<String> spinnerAdapterLojas;
    UsuarioPreferences usuarioPreferences;

    String nomeusuario;
    String password;

    ProgressDialog progressDialog;

    ObjetosSinkSincronizador objetosSinkSincronizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the view now
        setContentView(R.layout.activity_login);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        //Administrador descomenta aqui
        spinerLojas = (Spinner) findViewById(R.id.spiner_loja);
        objetosSinkSincronizador = new ObjetosSinkSincronizador(this);
        inputUsuario = (EditText) findViewById(R.id.usuario);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvLoja = (TextView) findViewById(R.id.loja);
        usuarioPreferences = new UsuarioPreferences(getApplicationContext());
        //Aeroporto
        //Administrador comenta aqui
//        lojaEscolhida = lojaDAO.procuraPorId("38176321-bc0d-462f-8b47-56e118ac14d1");
        if (lojaEscolhida != null) {

            tvLoja.setText(lojaEscolhida.getNome());

        }

        if (usuarioPreferences.temUsuario()) {
//            progressDialog.setCancelable(false);
//            progressDialog.setMessage("Autenticando usuário");
//            progressDialog.show();

            Usuario u = usuarioPreferences.getUsuario();
            Log.e("NomeUsuario", u.getNome());
            Log.e("SenhaUsuario", u.getSenha());
            if (u.getRole().equals("Funcionario")) {

                Intent intent = new Intent(getApplicationContext(), NavigationViewFuncionarioActivity.class);
                startActivity(intent);
                finish();
                return;

            } else {
                Intent intent = new Intent(getApplicationContext(), NavigationViewActivity.class);
                startActivity(intent);
                finish();
                return;

            }

        }

        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        labelsLojas.clear();
        for (Loja loja : lojas) {
            labelsLojas.add(loja.getNome());

        }
        //Administrador descomenta aqui
        spinnerAdapterLojas = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelsLojas);

        spinnerAdapterLojas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinerLojas.setAdapter(spinnerAdapterLojas);
        spinerLojas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lojaEscolhida = lojas.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomeusuario = inputUsuario.getText().toString();
                password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(nomeusuario)) {
                    Toast.makeText(getApplicationContext(), "Digite o Login address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Digite o  password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Autenticando usuário");
                progressDialog.show();

                Call<Usuario> call = new RetrofitInializador().getLoginLogoutService().login(nomeusuario, password);
                call.enqueue(login());


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        lojas = lojaDAO.listarLojas();

        labelsLojas.clear();
        for (Loja loja : lojas) {
            labelsLojas.add(loja.getNome());

        }
//        spinnerAdapterLojas.notifyDataSetChanged();


    }

    @NonNull
    private Callback<Usuario> login() {
        return (new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                Usuario usuario = response.body();
                if (usuario == null) {
                    Toast.makeText(getApplicationContext(), "Usuario ou senha invalido", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                if ((usuario.getRole().equals("Funcionario")) && (lojaEscolhida == null)) {
                    Toast.makeText(getApplicationContext(), "So administradores podem fazer o primeiro acesso", Toast.LENGTH_SHORT).show();
                    progressDialog.setMessage("Deslogando Usuario");
                    logout("usuarioLogado-" + usuario.getNome());
                    return;
                }


                if (lojaEscolhida == null) {
                    usuario.setSenha(password);
                    usuarioPreferences.salvarUsuario(usuario);

                } else {
                    usuario.setSenha(password);
                    usuarioPreferences.salvarUsuario(usuario, lojaEscolhida);

                }
                progressDialog.setMessage("Sincronizando dados");

//                objetosSinkSincronizador.buscaTodos();
                if (usuario.getRole().equals("Administrador")) {
                    Intent vaiParaNavigationView = new Intent(getApplicationContext(), NavigationViewActivity.class);
//                    vaiParaNavigationView.putExtra("login","login");
                    progressDialog.dismiss();
                    startActivity(vaiParaNavigationView);
                } else {
                    Intent vaiParaNavigationView = new Intent(getApplicationContext(), NavigationViewFuncionarioActivity.class);
                    progressDialog.dismiss();
                    startActivity(vaiParaNavigationView);
                }


            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                Toast.makeText(getApplicationContext(), "Verifique a conexao com a internet", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

    public void logout(final String usuarioLogado) {
        Call<Usuario> call = new RetrofitInializador().getLoginLogoutService().logout();
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.body() == null) {
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                Toast.makeText(getApplicationContext(), "Verifique a conexao com a internet", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


    }
}
