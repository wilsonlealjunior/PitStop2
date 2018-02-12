package pitstop.com.br.pitstop;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.preferences.ObjetosSinkPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import pitstop.com.br.pitstop.sic.ObjetosSinkSincronizador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputPassword2;
    private Button btnSignUp;
    String[] roles = new String[]{"Funcionario", "Administrador"};
    Spinner spinnerRole;
    String perfilEscolhido;
    private Toolbar toolbar;
    boolean edicao = false;
    ObjetosSinkPreferences objetosSinkPreferences;
    ObjetosSinkSincronizador objetosSinkSincronizador;
    Usuario usuarioEditar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastro de Usuario");     //Titulo para ser exibido na sua Action Bar em frente à seta
//        toolbar.setTitle("Cadastro de Usuario");

        objetosSinkSincronizador = new ObjetosSinkSincronizador(this);
        spinnerRole = (Spinner) findViewById(R.id.spiner_role);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.usuario);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword2 = (EditText) findViewById(R.id.password2);
        objetosSinkPreferences = new ObjetosSinkPreferences(this);

        Intent intent = getIntent();
        usuarioEditar = (Usuario) intent.getSerializableExtra("usuario");


        ArrayAdapter<String> spinnerAdapterRoles = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roles);

        spinnerAdapterRoles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(spinnerAdapterRoles);
        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                perfilEscolhido = roles[i];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (usuarioEditar != null) {
            btnSignUp.setText("Alterar");
            edicao = true;
            inputEmail.setText(usuarioEditar.getNome());
            inputEmail.setFocusable(false);
            inputPassword.setText(usuarioEditar.getSenha());
            inputPassword2.setText(usuarioEditar.getSenha());
            if (usuarioEditar.getRole().equals("Funcionario")) {
                spinnerRole.setSelection(0);
            } else {
                spinnerRole.setSelection(1);

            }

        }
        intent.removeExtra("usuario");


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usuarioNome = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String password2 = inputPassword2.getText().toString().trim();

                if (TextUtils.isEmpty(usuarioNome)) {
                    Toast.makeText(getApplicationContext(), "Enter nome usuario!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password2)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(password2)) {
                    Toast.makeText(getApplicationContext(), "Senhas não conferem", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 4) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Call<Usuario> call;
                if (!edicao) {
                    Usuario usuario = new Usuario();
                    usuario.setNome(usuarioNome);
                    usuario.setSenha((password));
                    usuario.setRole(perfilEscolhido);
                    call = new RetrofitInializador().getUsuarioService().insere(usuario);
                } else {
                    usuarioEditar.setNome(usuarioNome);
                    usuarioEditar.setSenha((password));
                    usuarioEditar.setRole(perfilEscolhido);
                    call = new RetrofitInializador().getUsuarioService().editar(usuarioEditar);
                }
                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.code() == 500) {
                            Toast.makeText(getApplicationContext(), "Erro ao cadastrar usuario, verifique o nome", Toast.LENGTH_LONG).show();

                        } else {
                            Usuario usuario = response.body();
                            if (edicao == false) {
                                Toast.makeText(getApplicationContext(), "Usuario Cadastrado com sucesso", Toast.LENGTH_LONG).show();
                                UsuarioDAO usuarioDAO = new UsuarioDAO(getApplicationContext());
                                usuarioDAO.insere(usuario);
                            } else {
                                Toast.makeText(getApplicationContext(), "Usuario alterado com sucesso", Toast.LENGTH_LONG).show();
                                UsuarioDAO usuarioDAO = new UsuarioDAO(getApplicationContext());
                                usuarioDAO.altera(usuario);
                            }
                            Intent intentVaiProNavigation = new Intent(getApplicationContext(), LoginActivity.class);
                            intentVaiProNavigation.putExtra("usuario", usuario);
                            startActivity(intentVaiProNavigation);

                        }


                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Verifique a conexao com a internet", Toast.LENGTH_LONG).show();


                    }
                });
            }


        });
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

    @Override
    protected void onResume() {
        super.onResume();

    }
}
