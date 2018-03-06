package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.ItemAvaria;

/**
 * Created by wilso on 27/11/2017.
 */

public class ItemAvariaDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;
    public ItemAvariaDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(ItemAvaria itemAvaria) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", itemAvaria.getId());
        dados.put("quantidade", itemAvaria.getQuantidade());
        dados.put("sincronizado", itemAvaria.getSincronizado());
        dados.put("id_avaria", itemAvaria.getIdAvaria());
        dados.put("id_entradaProduto", itemAvaria.getIdEntradaProduto());



        db.insert("Item_Avaria", null, dados);
    }
    public void insereLista(List<ItemAvaria> itemAvarias) {


        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (ItemAvaria itemAvaria : itemAvarias) {
            ContentValues dados = new ContentValues();
            dados.put("id", itemAvaria.getId());
            dados.put("quantidade", itemAvaria.getQuantidade());
            dados.put("sincronizado", itemAvaria.getSincronizado());
            dados.put("id_avaria", itemAvaria.getIdAvaria());
            dados.put("id_entradaProduto", itemAvaria.getIdEntradaProduto());



            db.insert("Item_Avaria", null, dados);

        }

    }

    public void deleta(ItemAvaria itemAvaria) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {itemAvaria.getId().toString()};
        db.delete("Item_Avaria", "id = ?", params);
    }

    public List<ItemAvaria> listarAvariaEntradaProduto() {

        String sql = "SELECT * FROM Item_Avaria;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<ItemAvaria> itemAvarias = new ArrayList<ItemAvaria>();
        while (c.moveToNext()) {
            ItemAvaria itemAvaria = new ItemAvaria();
            itemAvaria.setId(c.getString(c.getColumnIndex("id")));
            itemAvaria.setIdAvaria(c.getString(c.getColumnIndex("id_avaria")));
            itemAvaria.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemAvaria.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemAvaria.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));



            itemAvarias.add(itemAvaria);

        }
        c.close();
        return itemAvarias;
    }







    public void sincroniza(List<ItemAvaria> avariasEntradaProdutos) {
        for (ItemAvaria itemAvaria :
                avariasEntradaProdutos) {

            itemAvaria.sincroniza();

            if (existe(itemAvaria)) {
                close();
                altera(itemAvaria);
                close();
            } else {
                insere(itemAvaria);
                close();
            }

        }
    }

    private boolean existe(ItemAvaria itemAvaria) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Item_Avaria WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{itemAvaria.getId()});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }
    public void close(){
        databaseHelper.close();
        database = null;
    }
    public List<ItemAvaria> listaNaoSincronizados(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Item_Avaria WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);


        List<ItemAvaria> itemAvarias = new ArrayList<ItemAvaria>();
        while (c.moveToNext()) {
            ItemAvaria itemAvaria = new ItemAvaria();
            itemAvaria.setId(c.getString(c.getColumnIndex("id")));
            itemAvaria.setIdAvaria(c.getString(c.getColumnIndex("id_avaria")));
            itemAvaria.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemAvaria.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemAvaria.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));



            itemAvarias.add(itemAvaria);

        }
        c.close();
        return itemAvarias;

    }

    public void altera(ItemAvaria itemAvaria) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", itemAvaria.getId());
        dados.put("quantidade", itemAvaria.getQuantidade());
        dados.put("sincronizado", itemAvaria.getSincronizado());
        dados.put("id_avaria", itemAvaria.getIdAvaria());
        dados.put("id_entradaProduto", itemAvaria.getIdEntradaProduto());




        String[] params = {itemAvaria.getId().toString()};
        db.update("Item_Avaria", dados, "id = ?", params);
    }



    public ItemAvaria procuraPorId(String id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Item_Avaria WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{id});
        ItemAvaria itemAvaria =null;
        while (c.moveToNext()) {
            itemAvaria = new ItemAvaria();
            itemAvaria.setId(c.getString(c.getColumnIndex("id")));
            itemAvaria.setIdAvaria(c.getString(c.getColumnIndex("id_avaria")));
            itemAvaria.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemAvaria.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemAvaria.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));


        }
        c.close();
        return itemAvaria;

    }

    public List<ItemAvaria> procuraPorAvaria(String id_avaria) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Item_Avaria WHERE id_avaria=?";
        Cursor c = db.rawQuery(existe, new String[]{id_avaria});
        ItemAvaria itemAvaria =null;
        List<ItemAvaria> itemAvarias = new ArrayList<>();
        while (c.moveToNext()) {
            itemAvaria = new ItemAvaria();
            itemAvaria.setId(c.getString(c.getColumnIndex("id")));
            itemAvaria.setIdAvaria(c.getString(c.getColumnIndex("id_avaria")));
            itemAvaria.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemAvaria.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemAvaria.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            itemAvarias.add(itemAvaria);

        }
        c.close();
        return itemAvarias;

    }

}
