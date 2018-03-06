package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.ItemVenda;
import pitstop.com.br.pitstop.model.Venda;

/**
 * Created by wilso on 12/11/2017.
 */

public class ItemVendaDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;
    public ItemVendaDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(ItemVenda itemVenda) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();


        ContentValues dados = new ContentValues();
        dados.put("id", itemVenda.getId());
        dados.put("id_produto", itemVenda.getIdProduto());
        dados.put("id_entradaProduto", itemVenda.getIdEntradaProduto());
        dados.put("quantidadeVendida", itemVenda.getQuantidadeVendida());
        dados.put("id_venda", itemVenda.getIdVenda());
        dados.put("sincronizado", itemVenda.getSincronizado());
        dados.put("precoDeVenda", itemVenda.getPrecoDeVenda());



        db.insert("Item_Venda", null, dados);
    }
    public void insereLista(List<ItemVenda> itemVendas) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (ItemVenda itemVenda : itemVendas) {
            ContentValues dados = new ContentValues();
            dados.put("id", itemVenda.getId());
            dados.put("id_produto", itemVenda.getIdProduto());
            dados.put("id_entradaProduto", itemVenda.getIdEntradaProduto());
            dados.put("quantidadeVendida", itemVenda.getQuantidadeVendida());
            dados.put("id_venda", itemVenda.getIdVenda());
            dados.put("sincronizado", itemVenda.getSincronizado());
            dados.put("precoDeVenda", itemVenda.getPrecoDeVenda());

            db.insert("Item_Venda", null, dados);

        }


    }

    public void deleta(ItemVenda itemVenda) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {itemVenda.getId().toString()};
        db.delete("Item_Venda", "id = ?", params);
    }

    public List<ItemVenda> listarProdutoEntradaProduto() {
        String sql = "SELECT * FROM Item_Venda;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<ItemVenda> itemVendas = new ArrayList<ItemVenda>();
        while (c.moveToNext()) {
            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setId(c.getString(c.getColumnIndex("id")));
            itemVenda.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            itemVenda.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemVenda.setQuantidadeVendida(Integer.valueOf(c.getString(c.getColumnIndex("quantidadeVendida"))));
            itemVenda.setIdVenda(c.getString(c.getColumnIndex("id_venda")));
            itemVenda.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemVenda.setPrecoDeVenda(Double.valueOf(c.getString(c.getColumnIndex("precoDeVenda"))));
            itemVendas.add(itemVenda);

        }
        c.close();
        return itemVendas;
    }


    public void sincroniza(List<ItemVenda> itemVendas) {
        for (ItemVenda itemVenda :
                itemVendas) {
            itemVenda.sincroniza();

            if (existe(itemVenda)) {
                close();
                altera(itemVenda);
                close();
            } else {
                insere(itemVenda);
                close();
            }

        }
    }

    private boolean existe(ItemVenda itemVenda) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Item_Venda WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{itemVenda.getId()});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }
    public void close(){
        databaseHelper.close();
        database = null;
    }
    public List<ItemVenda> listaNaoSincronizados(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Item_Venda WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);
        List<ItemVenda> itemVendas = new ArrayList<ItemVenda>();
        while (c.moveToNext()) {
            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setId(c.getString(c.getColumnIndex("id")));
            itemVenda.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            itemVenda.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemVenda.setQuantidadeVendida(Integer.valueOf(c.getString(c.getColumnIndex("quantidadeVendida"))));
            itemVenda.setIdVenda(c.getString(c.getColumnIndex("id_venda")));
            itemVenda.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemVenda.setPrecoDeVenda(Double.valueOf(c.getString(c.getColumnIndex("precoDeVenda"))));
            itemVendas.add(itemVenda);


        }
        c.close();
        return itemVendas;

    }

    public void altera(ItemVenda itemVenda) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", itemVenda.getId());
        dados.put("id_produto", itemVenda.getIdProduto());
        dados.put("id_entradaProduto", itemVenda.getIdEntradaProduto());
        dados.put("quantidadeVendida", itemVenda.getQuantidadeVendida());
        dados.put("id_venda", itemVenda.getIdVenda());
        dados.put("sincronizado", itemVenda.getSincronizado());
        dados.put("precoDeVenda", itemVenda.getPrecoDeVenda());



        String[] params = {itemVenda.getId().toString()};
        db.update("Item_Venda", dados, "id = ?", params);
    }



    public List<ItemVenda> procuraPorVenda(Venda venda) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Item_Venda WHERE id_venda=?";
        Cursor c = db.rawQuery(existe, new String[]{venda.getId()});
        List<ItemVenda> itemVendas = new ArrayList<ItemVenda>();
        while (c.moveToNext()) {
            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setId(c.getString(c.getColumnIndex("id")));
            itemVenda.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            itemVenda.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            itemVenda.setQuantidadeVendida(Integer.valueOf(c.getString(c.getColumnIndex("quantidadeVendida"))));
            itemVenda.setIdVenda(c.getString(c.getColumnIndex("id_venda")));
            itemVenda.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            itemVenda.setPrecoDeVenda(Double.valueOf(c.getString(c.getColumnIndex("precoDeVenda"))));
            itemVendas.add(itemVenda);




        }
        c.close();
        return itemVendas;

    }



}
