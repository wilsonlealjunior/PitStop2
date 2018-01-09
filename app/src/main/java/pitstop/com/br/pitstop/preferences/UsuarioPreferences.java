package pitstop.com.br.pitstop.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Usuario;

/**
 * Created by wilso on 29/11/2017.
 */

public class UsuarioPreferences {
    private static final String USUARIO_PREFERENCES = "pitstop.com.br.pitstop.preferences.UsuarioPreferences";
    private static final String USUARIO_LOGADO_NOME = "usuario_logado_nome";
    private static final String USUARIO_LOGADO_SENHA = "usuario_logado_senha";
    private static final String USUARIO_LOGADO_ROLE = "usuario_logado_role";
    private static final String USUARIO_LOGADO_LOJA_ID = "usuario_logado_loja_id";
    private static final String USUARIO_LOGADO_LOJA_NOME = "usuario_logado_loja_nome";
    private static final String USUARIO_LOGADO_LOJA_ENDERECO = "usuario_logado_loja_endereco";
    private Context context;

    public UsuarioPreferences(Context context) {
        this.context = context;

    }

    public void salvarUsuario(Usuario usuario, Loja loja) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USUARIO_LOGADO_NOME, usuario.getNome());
        editor.putString(USUARIO_LOGADO_ROLE, usuario.getRole());

        if (usuario.getSenha() != null) {
            editor.putString(USUARIO_LOGADO_SENHA, usuario.getSenha());
        }
        editor.putString(USUARIO_LOGADO_LOJA_ID, loja.getId());
        editor.putString(USUARIO_LOGADO_LOJA_NOME, loja.getNome());
        editor.putString(USUARIO_LOGADO_LOJA_ENDERECO, loja.getEndereco());
        editor.commit();

    }

    public void salvarUsuario(Usuario usuario) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USUARIO_LOGADO_NOME, usuario.getNome());
        editor.putString(USUARIO_LOGADO_ROLE, usuario.getRole());
        if (usuario.getSenha() != null) {
            editor.putString(USUARIO_LOGADO_SENHA, usuario.getSenha());
        }
        editor.commit();

    }


    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(USUARIO_PREFERENCES, context.MODE_PRIVATE);
    }

    public Usuario getUsuario() {
        SharedPreferences preferences = getSharedPreferences();
        Usuario user = new Usuario();

        user.setNome(preferences.getString(USUARIO_LOGADO_NOME, ""));
        user.setRole(preferences.getString(USUARIO_LOGADO_ROLE, ""));

        user.setSenha(preferences.getString(USUARIO_LOGADO_SENHA, ""));

        return user;

    }

    public Loja getLoja() {
        SharedPreferences preferences = getSharedPreferences();
        Loja lojaUser = new Loja();

        lojaUser.setId(preferences.getString(USUARIO_LOGADO_LOJA_ID, ""));
        lojaUser.setNome(preferences.getString(USUARIO_LOGADO_LOJA_NOME, ""));
        lojaUser.setEndereco(preferences.getString(USUARIO_LOGADO_LOJA_ENDERECO, ""));
        return lojaUser;

    }

    public void deletar() {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(USUARIO_LOGADO_NOME).commit();
        editor.remove(USUARIO_LOGADO_SENHA).commit();
        editor.remove(USUARIO_LOGADO_ROLE).commit();
        editor.remove(USUARIO_LOGADO_LOJA_ENDERECO).commit();
        editor.remove(USUARIO_LOGADO_LOJA_ID).commit();
        editor.remove(USUARIO_LOGADO_LOJA_NOME).commit();
        editor.remove(USUARIO_PREFERENCES).commit();
    }

    public boolean temUsuario() {
        return !getUsuario().getNome().isEmpty();

    }

    public boolean temLoja() {
        return !getLoja().getNome().isEmpty();

    }
}
