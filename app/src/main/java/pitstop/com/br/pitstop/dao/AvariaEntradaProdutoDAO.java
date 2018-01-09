package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.AvariaEntradaProduto;

/**
 * Created by wilso on 27/11/2017.
 */

public class AvariaEntradaProdutoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;
    public AvariaEntradaProdutoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(AvariaEntradaProduto avariaEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", avariaEntradaProduto.getId());
        dados.put("quantidade", avariaEntradaProduto.getQuantidade());
        dados.put("sincronizado",avariaEntradaProduto.getSincronizado());
        dados.put("id_avaria",avariaEntradaProduto.getIdAvaria());
        dados.put("id_entradaProduto",avariaEntradaProduto.getIdEntradaProduto());



        db.insert("Avaria_EntradaProduto", null, dados);
    }
    public void insereLista(List<AvariaEntradaProduto> avariaEntradaProdutos) {


        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (AvariaEntradaProduto avariaEntradaProduto: avariaEntradaProdutos) {
            ContentValues dados = new ContentValues();
            dados.put("id", avariaEntradaProduto.getId());
            dados.put("quantidade", avariaEntradaProduto.getQuantidade());
            dados.put("sincronizado",avariaEntradaProduto.getSincronizado());
            dados.put("id_avaria",avariaEntradaProduto.getIdAvaria());
            dados.put("id_entradaProduto",avariaEntradaProduto.getIdEntradaProduto());



            db.insert("Avaria_EntradaProduto", null, dados);

        }

    }

    public void deleta(AvariaEntradaProduto avariaEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {avariaEntradaProduto.getId().toString()};
        db.delete("Avaria_EntradaProduto", "id = ?", params);
    }

    public List<AvariaEntradaProduto> listarAvariaEntradaProduto() {

        String sql = "SELECT * FROM Avaria_EntradaProduto;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<AvariaEntradaProduto> avariaEntradaProdutos = new ArrayList<AvariaEntradaProduto>();
        while (c.moveToNext()) {
            AvariaEntradaProduto avariaEntradaProduto = new AvariaEntradaProduto();
            avariaEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            avariaEntradaProduto.setIdAvaria(c.getString(c.getColumnIndex("id_avaria")));
            avariaEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            avariaEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            avariaEntradaProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));



            avariaEntradaProdutos.add(avariaEntradaProduto);

        }
        c.close();
        return avariaEntradaProdutos;
    }







    public void sincroniza(List<AvariaEntradaProduto> avariasEntradaProdutos) {
        for (AvariaEntradaProduto avariaEntradaProduto :
                avariasEntradaProdutos) {

            avariaEntradaProduto.sincroniza();

            if (existe(avariaEntradaProduto)) {
                altera(avariaEntradaProduto);
            } else {
                insere(avariaEntradaProduto);
            }

        }
    }

    private boolean existe(AvariaEntradaProduto avariaEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Avaria_EntradaProduto WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{avariaEntradaProduto.getId()});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }
    public void close(){
        databaseHelper.close();
        database = null;
    }
    public List<AvariaEntradaProduto> listaNaoSincronizados(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Avaria_EntradaProduto WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);


        List<AvariaEntradaProduto> avariaEntradaProdutos = new ArrayList<AvariaEntradaProduto>();
        while (c.moveToNext()) {
            AvariaEntradaProduto avariaEntradaProduto = new AvariaEntradaProduto();
            avariaEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            avariaEntradaProduto.setIdAvaria(c.getString(c.getColumnIndex("id_avaria")));
            avariaEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            avariaEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            avariaEntradaProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));



            avariaEntradaProdutos.add(avariaEntradaProduto);

        }
        c.close();
        return avariaEntradaProdutos;

    }

    public void altera(AvariaEntradaProduto avariaEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", avariaEntradaProduto.getId());
        dados.put("quantidade", avariaEntradaProduto.getQuantidade());
        dados.put("sincronizado",avariaEntradaProduto.getSincronizado());
        dados.put("id_avaria",avariaEntradaProduto.getIdAvaria());
        dados.put("id_entradaProduto",avariaEntradaProduto.getIdEntradaProduto());




        String[] params = {avariaEntradaProduto.getId().toString()};
        db.update("Avaria_EntradaProduto", dados, "id = ?", params);
    }



    public AvariaEntradaProduto procuraPorId(String id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Avaria_EntradaProduto WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{id});
        AvariaEntradaProduto avariaEntradaProduto=null;
        while (c.moveToNext()) {
            avariaEntradaProduto = new AvariaEntradaProduto();
            avariaEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            avariaEntradaProduto.setIdAvaria(c.getString(c.getColumnIndex("id_avaria")));
            avariaEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            avariaEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            avariaEntradaProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));


        }
        c.close();
        return avariaEntradaProduto;

    }

    public List<AvariaEntradaProduto> procuraPorAvaria(String id_avaria) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Avaria_EntradaProduto WHERE id_avaria=?";
        Cursor c = db.rawQuery(existe, new String[]{id_avaria});
        AvariaEntradaProduto avariaEntradaProduto=null;
        List<AvariaEntradaProduto> avariaEntradaProdutos = new ArrayList<>();
        while (c.moveToNext()) {
            avariaEntradaProduto = new AvariaEntradaProduto();
            avariaEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            avariaEntradaProduto.setIdAvaria(c.getString(c.getColumnIndex("id_avaria")));
            avariaEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            avariaEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            avariaEntradaProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            avariaEntradaProdutos.add(avariaEntradaProduto);

        }
        c.close();
        return avariaEntradaProdutos;

    }

}
