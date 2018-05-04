package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.ItemFuro;

/**
 * Created by wilso on 15/12/2017.
 */

public class ItemFuroDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;

    public ItemFuroDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(ItemFuro itemFuro) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();




        dados.put("id", itemFuro.getId());
        dados.put("quantidade", itemFuro.getQuantidade());
        dados.put("sincronizado", itemFuro.getSincronizado());
        dados.put("id_furo", itemFuro.getIdFuro());
        dados.put("id_entradaProduto", itemFuro.getIdEntradaProduto());
        dados.put("precoDeVenda", itemFuro.getPrecoDeVenda());


        db.insert("Item_Furo", null, dados);
    }

    public void insereLista(List<ItemFuro> itemFuros) {


        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (ItemFuro itemFuro : itemFuros) {
            ContentValues dados = new ContentValues();
            dados.put("id", itemFuro.getId());
            dados.put("quantidade", itemFuro.getQuantidade());
            dados.put("sincronizado", itemFuro.getSincronizado());
            dados.put("id_furo", itemFuro.getIdFuro());
            dados.put("id_entradaProduto", itemFuro.getIdEntradaProduto());
            dados.put("precoDeVenda", itemFuro.getPrecoDeVenda());


            db.insert("Item_Furo", null, dados);

        }

    }

    public void deleta(ItemFuro itemFuro) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {itemFuro.getId().toString()};
        db.delete("Item_Furo", "id = ?", params);
    }

    public List<ItemFuro> listarfuroEntradaProduto() {

        String sql = "SELECT * FROM Item_Furo;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<ItemFuro> itemFuros = new ArrayList<ItemFuro>();
        while (c.moveToNext()) {
            ItemFuro itemFuro = new ItemFuro();
            itemFuro.setId(c.getString(c.getColumnIndex("id")));
            itemFuro.setIdFuro(c.getString(c.getColumnIndex("id_furo")));
            itemFuro.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemFuro.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemFuro.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            itemFuro.setPrecoDeVenda(Double.valueOf(c.getString(c.getColumnIndex("precoDeVenda"))));


            itemFuros.add(itemFuro);

        }
        c.close();
        return itemFuros;
    }


    public void sincroniza(List<ItemFuro> FurosEntradaProdutos) {
        for (ItemFuro itemFuro :
                FurosEntradaProdutos) {

            itemFuro.sincroniza();

            if (existe(itemFuro)) {
                close();
                altera(itemFuro);
                close();
            } else {
                insere(itemFuro);
                close();
            }

        }
    }

    private boolean existe(ItemFuro itemFuro) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Item_Furo WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{itemFuro.getId()});
        int quantidade = cursor.getCount();
        cursor.close();
        return quantidade > 0;
    }

    public void close() {
        databaseHelper.close();
        database = null;
    }

    public List<ItemFuro> listaNaoSincronizados() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Item_Furo WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);


        List<ItemFuro> itemFuros = new ArrayList<ItemFuro>();
        while (c.moveToNext()) {
            ItemFuro itemFuro = new ItemFuro();
            itemFuro.setId(c.getString(c.getColumnIndex("id")));
            itemFuro.setIdFuro(c.getString(c.getColumnIndex("id_furo")));
            itemFuro.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemFuro.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemFuro.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            itemFuro.setPrecoDeVenda(Double.valueOf(c.getString(c.getColumnIndex("precoDeVenda"))));

            itemFuros.add(itemFuro);

        }
        c.close();
        return itemFuros;

    }

    public void altera(ItemFuro itemFuro) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", itemFuro.getId());
        dados.put("quantidade", itemFuro.getQuantidade());
        dados.put("sincronizado", itemFuro.getSincronizado());
        dados.put("id_furo", itemFuro.getIdFuro());
        dados.put("id_entradaProduto", itemFuro.getIdEntradaProduto());
        dados.put("precoDeVenda", itemFuro.getPrecoDeVenda());

        String[] params = {itemFuro.getId().toString()};
        db.update("Item_Furo", dados, "id = ?", params);
    }


    public ItemFuro procuraPorId(String id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Item_Furo WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{id});
        ItemFuro itemFuro = null;
        while (c.moveToNext()) {
            itemFuro = new ItemFuro();
            itemFuro.setId(c.getString(c.getColumnIndex("id")));
            itemFuro.setIdFuro(c.getString(c.getColumnIndex("id_furo")));
            itemFuro.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemFuro.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemFuro.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            itemFuro.setPrecoDeVenda(Double.valueOf(c.getString(c.getColumnIndex("precoDeVenda"))));

        }
        c.close();
        return itemFuro;

    }

    public List<ItemFuro> procuraPorFuro(String id_furo) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Item_Furo WHERE id_furo=?";
        Cursor c = db.rawQuery(existe, new String[]{id_furo});
        ItemFuro itemFuro = null;
        List<ItemFuro> itemFuros = new ArrayList<>();
        while (c.moveToNext()) {
            itemFuro = new ItemFuro();
            itemFuro.setId(c.getString(c.getColumnIndex("id")));
            itemFuro.setIdFuro(c.getString(c.getColumnIndex("id_furo")));
            itemFuro.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemFuro.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemFuro.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            itemFuro.setPrecoDeVenda(Double.valueOf(c.getString(c.getColumnIndex("precoDeVenda"))));
            itemFuros.add(itemFuro);

        }
        c.close();
        return itemFuros;

    }
}
