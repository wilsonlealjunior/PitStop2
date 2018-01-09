package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pitstop.com.br.pitstop.model.Loja;


/**
 * Created by wilso on 20/10/2017.
 */

public class LojaDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public LojaDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }



    public void sincroniza(List<Loja> Lojas) {
        for (Loja loja :
                Lojas) {
            loja.sincroniza();

            Log.e("loja-1", loja.getId());
            Log.e("loja-1", loja.getNome());

            if (existe(loja)) {
                altera(loja);
            } else {
                insere(loja);
            }

        }
    }

    private boolean existe(Loja loja) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Lojas WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{loja.getId()});
        //Log.e("verificando nome_>",cursor.getString(cursor.getColumnIndex("id")));
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }

    public Loja procuraPorId(String id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Lojas WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{id});
        Loja loja =null;
        while (c.moveToNext()) {
            loja = new Loja();
            loja.setId(c.getString(c.getColumnIndex("id")));
            loja.setNome(c.getString(c.getColumnIndex("nome")));
            loja.setEndereco(c.getString(c.getColumnIndex("endereco")));
            loja.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));

        }
        c.close();
        return loja;

    }





    public void insere(Loja loja) {
        if(loja.getId()==null){
            loja.setId(UUID.randomUUID().toString());
        }
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();

        dados.put("id", loja.getId());
        dados.put("nome", loja.getNome());
        dados.put("endereco", loja.getEndereco());
        dados.put("sincronizado", loja.getSincronizado());

        db.insert("Lojas", null, dados);
    }

    public void insereLista(List<Loja> Lojas) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (Loja loja : Lojas) {
            ContentValues dados = new ContentValues();
            dados.put("id", loja.getId());
            dados.put("nome", loja.getNome());
            dados.put("endereco", loja.getEndereco());
            dados.put("sincronizado", loja.getSincronizado());

            db.insert("Lojas", null, dados);

        }

    }

    public List<Loja> listarLojas() {
        String sql = "SELECT * FROM Lojas;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Loja> Lojas = new ArrayList<Loja>();
        while (c.moveToNext()) {
            Loja loja = new Loja();
            loja.setId(c.getString(c.getColumnIndex("id")));
            loja.setNome(c.getString(c.getColumnIndex("nome")));
            loja.setEndereco(c.getString(c.getColumnIndex("endereco")));
            loja.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            Lojas.add(loja);

        }
        c.close();
        return Lojas;
    }

    public void deleta(Loja loja) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {loja.getId().toString()};
        db.delete("Lojas", "id = ?", params);
    }
    public void close(){
        databaseHelper.close();
        database = null;
    }

    public List<Loja> listaNaoSincronizados(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Lojas WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);
        List<Loja> Lojas = new ArrayList<Loja>();
        while (c.moveToNext()) {
            Loja loja = new Loja();
            loja.setId(c.getString(c.getColumnIndex("id")));
            loja.setNome(c.getString(c.getColumnIndex("nome")));
            loja.setEndereco(c.getString(c.getColumnIndex("endereco")));
            loja.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            Lojas.add(loja);
        }
        c.close();
        return Lojas;

    }

    public void altera(Loja loja) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", loja.getId());
        dados.put("nome", loja.getNome());
        dados.put("endereco", loja.getEndereco());
        dados.put("sincronizado", loja.getSincronizado());


        String[] params = {loja.getId().toString()};
        db.update("Lojas", dados, "id = ?", params);
    }

}
