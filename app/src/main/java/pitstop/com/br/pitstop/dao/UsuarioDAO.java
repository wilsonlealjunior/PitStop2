package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import pitstop.com.br.pitstop.model.Usuario;

/**
 * Created by wilso on 04/12/2017.
 */

public class UsuarioDAO {
    Realm realm;
    Context context;

    public UsuarioDAO(Context context) {
        realm = Realm.getDefaultInstance();
    }
    private void verificaSeRealmEstaFechado() {
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    public void insere(Usuario usuario) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(usuario);
        realm.commitTransaction();

    }

    public void deleta(Usuario usuario) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        Usuario usuarioRealm = realm.where(Usuario.class)
                .equalTo("nome", usuario.getNome())
                .findFirst();
        usuarioRealm.deleteFromRealm();
        realm.commitTransaction();
    }

    public List<Usuario> listarUsuarios() {
        verificaSeRealmEstaFechado();
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.addAll(realm.where(Usuario.class)
                .equalTo("desativado",0)
                .findAll());
        return realm.copyFromRealm(usuarios);
    }


    public void sincroniza(List<Usuario> usuarios) {
        for (Usuario usuario :
                usuarios) {
            usuario.sincroniza();
            if (existe(usuario)) {
                close();
                if(usuario.estaDesativado()){
                    deleta(usuario);
                    close();
                } else {
                    altera(usuario);
                    close();
                }
            } else if (!usuario.estaDesativado()){
                insere(usuario);
                close();
            }

        }
    }

    private boolean existe(Usuario usuario) {
        verificaSeRealmEstaFechado();
        Number n = realm.where(Usuario.class).equalTo("nome", usuario.getNome()).count();
        return (n.intValue() > 0);
    }

    public void close() {
       realm.close();
    }

    public List<Usuario> listaNaoSincronizados() {
        verificaSeRealmEstaFechado();
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.addAll(realm.where(Usuario.class)
                .equalTo("sincronizado", 0)
                .findAll());
        return realm.copyFromRealm(usuarios);
    }

    public void altera(Usuario usuario) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(usuario);
        realm.commitTransaction();
    }


    public Usuario procuraPorNome(String nome) {
        verificaSeRealmEstaFechado();
        Usuario usuario = (realm.where(Usuario.class)
                .equalTo("nome", nome)
                .findFirst());
        return realm.copyFromRealm(usuario);



    }


}
