package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.Usuario;

/**
 * Created by wilso on 04/12/2017.
 */

public class UsuarioDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;

    public UsuarioDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(Usuario usuario) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("nome", usuario.getNome());
        dados.put("senha", usuario.getSenha());
        dados.put("role", usuario.getRole());
        dados.put("desativado", usuario.getDesativado());
        dados.put("sincronizado",usuario.getSincronizado());


        db.insert("Usuarios", null, dados);
    }

    public void insereLista(List<Usuario> usuarios) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (Usuario usuario : usuarios) {
            ContentValues dados = new ContentValues();
            dados.put("nome", usuario.getNome());
            dados.put("senha", usuario.getSenha());
            dados.put("role", usuario.getRole());
            dados.put("desativado", usuario.getDesativado());
            dados.put("sincronizado",usuario.getSincronizado());


            db.insert("Usuarios", null, dados);
        }


    }

    public void deleta(Usuario usuario) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {usuario.getNome()};
        db.delete("Usuarios", "nome = ?", params);
    }

    public List<Usuario> listarUsuarios() {
        String sql = "SELECT * FROM Usuarios where desativado = 0;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Usuario> usuarios = new ArrayList<Usuario>();
        while (c.moveToNext()) {
            Usuario usuario = new Usuario();
            usuario.setNome(c.getString(c.getColumnIndex("nome")));

            usuario.setSenha(c.getString(c.getColumnIndex("senha")));

            usuario.setRole(c.getString(c.getColumnIndex("role")));
            usuario.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            usuario.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));


            usuarios.add(usuario);

        }
        c.close();
        return usuarios;
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
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT nome FROM Usuarios WHERE nome=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{usuario.getNome()});
        int quantidade = cursor.getCount();
        cursor.close();
        return quantidade > 0;
    }

    public void close() {
        databaseHelper.close();
        database = null;
    }

    public List<Usuario> listaNaoSincronizados() {
        String sql = "SELECT * FROM Usuarios where sincronizado = 0;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Usuario> usuarios = new ArrayList<Usuario>();
        while (c.moveToNext()) {
            Usuario usuario = new Usuario();
            usuario.setNome(c.getString(c.getColumnIndex("nome")));

            usuario.setSenha(c.getString(c.getColumnIndex("senha")));

            usuario.setRole(c.getString(c.getColumnIndex("role")));

            usuario.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            usuario.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));


            usuarios.add(usuario);

        }
        c.close();
        return usuarios;


    }

    public void altera(Usuario usuario) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("nome", usuario.getNome());
        dados.put("senha", usuario.getSenha());
        dados.put("role", usuario.getRole());
        dados.put("desativado", usuario.getDesativado());
        dados.put("sincronizado",usuario.getSincronizado());


        String[] params = {usuario.getNome()};
        db.update("Usuarios", dados, "nome = ?", params);
    }


    public Usuario procuraPorNome(String nome) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Usuarios WHERE nome=?";
        Cursor c = db.rawQuery(existe, new String[]{nome});
        Usuario usuario = null;
        while (c.moveToNext()) {
            usuario = new Usuario();
            usuario.setNome(c.getString(c.getColumnIndex("nome")));

            usuario.setSenha(c.getString(c.getColumnIndex("senha")));

            usuario.setRole(c.getString(c.getColumnIndex("role")));
            usuario.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            usuario.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));


        }
        c.close();
        return usuario;

    }


}
