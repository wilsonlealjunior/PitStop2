package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.VendaEntradaProduto;
import pitstop.com.br.pitstop.model.Venda;

/**
 * Created by wilso on 12/11/2017.
 */

public class VendaEntradaProdutoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;
    public VendaEntradaProdutoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(VendaEntradaProduto vendaEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();


        ContentValues dados = new ContentValues();
        dados.put("id", vendaEntradaProduto.getId());
        dados.put("id_produto", vendaEntradaProduto.getIdProduto());
        dados.put("id_entradaProduto", vendaEntradaProduto.getIdEntradaProduto());
        dados.put("quantidadeVendida", vendaEntradaProduto.getQuantidadeVendida());
        dados.put("id_venda", vendaEntradaProduto.getIdVenda());
        dados.put("sincronizado", vendaEntradaProduto.getSincronizado());
        dados.put("precoDeVenda", vendaEntradaProduto.getPrecoDeVenda());



        db.insert("Venda_EntradaProduto", null, dados);
    }
    public void insereLista(List<VendaEntradaProduto> vendaEntradaProdutos) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (VendaEntradaProduto vendaEntradaProduto: vendaEntradaProdutos) {
            ContentValues dados = new ContentValues();
            dados.put("id", vendaEntradaProduto.getId());
            dados.put("id_produto", vendaEntradaProduto.getIdProduto());
            dados.put("id_entradaProduto", vendaEntradaProduto.getIdEntradaProduto());
            dados.put("quantidadeVendida", vendaEntradaProduto.getQuantidadeVendida());
            dados.put("id_venda", vendaEntradaProduto.getIdVenda());
            dados.put("sincronizado", vendaEntradaProduto.getSincronizado());
            dados.put("precoDeVenda", vendaEntradaProduto.getPrecoDeVenda());

            db.insert("Venda_EntradaProduto", null, dados);

        }


    }

    public void deleta(VendaEntradaProduto vendaEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {vendaEntradaProduto.getId().toString()};
        db.delete("Venda_EntradaProduto", "id = ?", params);
    }

    public List<VendaEntradaProduto> listarProdutoEntradaProduto() {
        String sql = "SELECT * FROM Venda_EntradaProduto;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<VendaEntradaProduto> vendaEntradaProdutos = new ArrayList<VendaEntradaProduto>();
        while (c.moveToNext()) {
            VendaEntradaProduto vendaEntradaProduto = new VendaEntradaProduto();
            vendaEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            vendaEntradaProduto.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            vendaEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            vendaEntradaProduto.setQuantidadeVendida(Integer.valueOf(c.getString(c.getColumnIndex("quantidadeVendida"))));
            vendaEntradaProduto.setIdVenda(c.getString(c.getColumnIndex("id_venda")));
            vendaEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            vendaEntradaProduto.setPrecoDeVenda(Double.valueOf(c.getString(c.getColumnIndex("precoDeVenda"))));
            vendaEntradaProdutos.add(vendaEntradaProduto);

        }
        c.close();
        return vendaEntradaProdutos;
    }


    public void sincroniza(List<VendaEntradaProduto> vendaEntradaProdutos) {
        for (VendaEntradaProduto vendaEntradaProduto :
                vendaEntradaProdutos) {
            vendaEntradaProduto.sincroniza();

            if (existe(vendaEntradaProduto)) {
                altera(vendaEntradaProduto);
            } else {
                insere(vendaEntradaProduto);
            }

        }
    }

    private boolean existe(VendaEntradaProduto vendaEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Venda_EntradaProduto WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{vendaEntradaProduto.getId()});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }
    public void close(){
        databaseHelper.close();
        database = null;
    }
    public List<VendaEntradaProduto> listaNaoSincronizados(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Venda_EntradaProduto WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);
        List<VendaEntradaProduto> vendaEntradaProdutos = new ArrayList<VendaEntradaProduto>();
        while (c.moveToNext()) {
            VendaEntradaProduto vendaEntradaProduto = new VendaEntradaProduto();
            vendaEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            vendaEntradaProduto.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            vendaEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            vendaEntradaProduto.setQuantidadeVendida(Integer.valueOf(c.getString(c.getColumnIndex("quantidadeVendida"))));
            vendaEntradaProduto.setIdVenda(c.getString(c.getColumnIndex("id_venda")));
            vendaEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            vendaEntradaProduto.setPrecoDeVenda(Double.valueOf(c.getString(c.getColumnIndex("precoDeVenda"))));
            vendaEntradaProdutos.add(vendaEntradaProduto);


        }
        c.close();
        return vendaEntradaProdutos;

    }

    public void altera(VendaEntradaProduto vendaEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", vendaEntradaProduto.getId());
        dados.put("id_produto", vendaEntradaProduto.getIdProduto());
        dados.put("id_entradaProduto", vendaEntradaProduto.getIdEntradaProduto());
        dados.put("quantidadeVendida", vendaEntradaProduto.getQuantidadeVendida());
        dados.put("id_venda", vendaEntradaProduto.getIdVenda());
        dados.put("sincronizado", vendaEntradaProduto.getSincronizado());
        dados.put("precoDeVenda", vendaEntradaProduto.getPrecoDeVenda());



        String[] params = {vendaEntradaProduto.getId().toString()};
        db.update("Venda_EntradaProduto", dados, "id = ?", params);
    }



    public List<VendaEntradaProduto> procuraPorVenda(Venda venda) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Venda_EntradaProduto WHERE id_venda=?";
        Cursor c = db.rawQuery(existe, new String[]{venda.getId()});
        List<VendaEntradaProduto> vendaEntradaProdutos = new ArrayList<VendaEntradaProduto>();
        while (c.moveToNext()) {
            VendaEntradaProduto vendaEntradaProduto = new VendaEntradaProduto();
            vendaEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            vendaEntradaProduto.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            vendaEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            vendaEntradaProduto.setQuantidadeVendida(Integer.valueOf(c.getString(c.getColumnIndex("quantidadeVendida"))));
            vendaEntradaProduto.setIdVenda(c.getString(c.getColumnIndex("id_venda")));
            vendaEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            vendaEntradaProduto.setPrecoDeVenda(Double.valueOf(c.getString(c.getColumnIndex("precoDeVenda"))));
            vendaEntradaProdutos.add(vendaEntradaProduto);




        }
        c.close();
        return vendaEntradaProdutos;

    }



}
