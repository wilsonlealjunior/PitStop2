package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Produto;

/**
 * Created by wilso on 29/10/2017.
 */

public class EntradaProdutoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;

    public EntradaProdutoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }
    public void deleta(EntradaProduto entradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {entradaProduto.getId().toString()};
        db.delete("EntradaProduto", "id = ?", params);
    }

    public void sincroniza(List<EntradaProduto> entradaProdutos) {
        for (EntradaProduto entradaProduto :
                entradaProdutos) {
            entradaProduto.sincroniza();

//            Log.e("EntradaProduto-1", entradaProduto.getId());
            //Log.e("EntradaProduto-1 - sinc", String.valueOf(entradaProduto.getSincronizado()));
            if (existe(entradaProduto)) {
                close();
                if(entradaProduto.estaDesativado()){
                    deleta(entradaProduto);
                    close();
                } else {
                    altera(entradaProduto);
                    close();
                }
            } else if (!entradaProduto.estaDesativado()){
                insere(entradaProduto);
                close();
            }

        }
    }

    private boolean existe(EntradaProduto entradaProduto) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM EntradaProduto WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{entradaProduto.getId()});
        //Log.e("verificando nome_>",cursor.getString(cursor.getColumnIndex("id")));
        int quantidade = cursor.getCount();
        cursor.close();
        return quantidade > 0;
    }

    public void altera(EntradaProduto entradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", entradaProduto.getId());
        dados.put("precoDeCompra", entradaProduto.getPrecoDeCompra());
        dados.put("quantidade", entradaProduto.getQuantidade());
        dados.put("data", entradaProduto.getData());
        dados.put("produto_id", entradaProduto.getProduto().getId());
        dados.put("sincronizado", entradaProduto.getSincronizado());
        dados.put("desativado", entradaProduto.getDesativado());
        dados.put("quantidadeVendidaMovimentada", entradaProduto.getQuantidadeVendidaMovimentada());

        String[] params = {entradaProduto.getId().toString()};
        db.update("EntradaProduto", dados, "id = ?", params);
    }

    public void insere(EntradaProduto entradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", entradaProduto.getId());
        dados.put("precoDeCompra", entradaProduto.getPrecoDeCompra());
        dados.put("quantidade", entradaProduto.getQuantidade());
        dados.put("data", entradaProduto.getData());
        dados.put("produto_id", entradaProduto.getProduto().getId());
        dados.put("sincronizado", entradaProduto.getSincronizado());
        dados.put("desativado", entradaProduto.getDesativado());
        dados.put("quantidadeVendidaMovimentada", entradaProduto.getQuantidadeVendidaMovimentada());


        db.insert("EntradaProduto", null, dados);
    }

    public void insereLista(List<EntradaProduto> entradaProdutos) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (EntradaProduto entradaProduto : entradaProdutos) {
            ContentValues dados = new ContentValues();
            dados.put("id", entradaProduto.getId());
            dados.put("precoDeCompra", entradaProduto.getPrecoDeCompra());
            dados.put("quantidade", entradaProduto.getQuantidade());
            dados.put("data", entradaProduto.getData());
            dados.put("produto_id", entradaProduto.getProduto().getId());
            dados.put("sincronizado", entradaProduto.getSincronizado());
            dados.put("desativado", entradaProduto.getDesativado());
            dados.put("quantidadeVendidaMovimentada", entradaProduto.getQuantidadeVendidaMovimentada());


            db.insert("EntradaProduto", null, dados);

        }

    }

    public List<EntradaProduto> listaNaoSincronizados() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM EntradaProduto WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);
        List<EntradaProduto> entradaProdutos = new ArrayList<EntradaProduto>();
        while (c.moveToNext()) {
            EntradaProduto entradaProduto = new EntradaProduto();
            entradaProduto.setId(c.getString(c.getColumnIndex("id")));
            entradaProduto.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            entradaProduto.setPrecoDeCompra(Double.valueOf(c.getString(c.getColumnIndex("precoDeCompra"))));
            entradaProduto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            entradaProduto.setData(c.getString(c.getColumnIndex("data")));

            entradaProduto.setQuantidadeVendidaMovimentada(Integer.parseInt(c.getString(c.getColumnIndex("quantidadeVendidaMovimentada"))));
            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            entradaProduto.setProduto(produtoDAO.procuraPorId(c.getString(c.getColumnIndex("produto_id"))));
            produtoDAO.close();
            entradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));

            entradaProdutos.add(entradaProduto);

        }
        c.close();
        return entradaProdutos;

    }

    public List<EntradaProduto> listarEntradaProduto() {
        String sql = "SELECT * FROM EntradaProduto;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        List<EntradaProduto> entradaProdutos = new ArrayList<EntradaProduto>();
        while (c.moveToNext()) {
            EntradaProduto entradaProduto = new EntradaProduto();
            entradaProduto.setId(c.getString(c.getColumnIndex("id")));
            entradaProduto.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            entradaProduto.setPrecoDeCompra(Double.valueOf(c.getString(c.getColumnIndex("precoDeCompra"))));
            entradaProduto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            entradaProduto.setQuantidadeVendidaMovimentada(Integer.parseInt(c.getString(c.getColumnIndex("quantidadeVendidaMovimentada"))));

            entradaProduto.setData(c.getString(c.getColumnIndex("data")));

            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            entradaProduto.setProduto(produtoDAO.procuraPorId(c.getString(c.getColumnIndex("produto_id"))));
            entradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            produtoDAO.close();
            entradaProdutos.add(entradaProduto);

        }
        c.close();
        return entradaProdutos;
    }

    public List<EntradaProduto> relatorio(String lojaEscolhida, String de, String ate) {
        String sql = "SELECT ep.desativado,ep.id,ep.data,ep.precoDeCompra,ep.quantidade,ep.quantidadeVendidaMovimentada,ep.sincronizado,ep.produto_id  FROM EntradaProduto ep inner join Produtos p on ep.produto_id=p.id inner join Lojas l on p.loja_id=l.id where ep.desativado=0 and ep.data between '" + de + "' and '" + ate + "' and l.id like '" + lojaEscolhida + "' order by ep.data desc ;";
        Log.e("sql", sql);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<EntradaProduto> entradaProdutos = new ArrayList<EntradaProduto>();
        while (c.moveToNext()) {
            EntradaProduto entradaProduto = new EntradaProduto();
            entradaProduto.setId(c.getString(c.getColumnIndex("id")));
            entradaProduto.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            entradaProduto.setPrecoDeCompra(Double.valueOf(c.getString(c.getColumnIndex("precoDeCompra"))));
            entradaProduto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            entradaProduto.setQuantidadeVendidaMovimentada(Integer.parseInt(c.getString(c.getColumnIndex("quantidadeVendidaMovimentada"))));
            entradaProduto.setData(c.getString(c.getColumnIndex("data")));
            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            entradaProduto.setProduto(produtoDAO.procuraPorId(c.getString(c.getColumnIndex("produto_id"))));
            entradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            produtoDAO.close();
            entradaProdutos.add(entradaProduto);

        }
        c.close();
        return entradaProdutos;
    }

    public void close() {
        databaseHelper.close();
        database = null;
    }

    public List<EntradaProduto> procuraTodosDeUmProduto(Produto produto) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM EntradaProduto WHERE desativado=0 and produto_id=? and (quantidade-quantidadeVendidaMovimentada)!=0  order by data asc";
        Cursor c = db.rawQuery(existe, new String[]{produto.getId()});
        List<EntradaProduto> entradaProdutos = new ArrayList<EntradaProduto>();
        while (c.moveToNext()) {
            EntradaProduto entradaProduto = new EntradaProduto();
            entradaProduto.setId(c.getString(c.getColumnIndex("id")));
            entradaProduto.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            entradaProduto.setPrecoDeCompra(Double.valueOf(c.getString(c.getColumnIndex("precoDeCompra"))));
            entradaProduto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            entradaProduto.setQuantidadeVendidaMovimentada(Integer.parseInt(c.getString(c.getColumnIndex("quantidadeVendidaMovimentada"))));
            entradaProduto.setData(c.getString(c.getColumnIndex("data")));
            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            entradaProduto.setProduto(produtoDAO.procuraPorId(c.getString(c.getColumnIndex("produto_id"))));
            entradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            entradaProdutos.add(entradaProduto);
            produtoDAO.close();


        }
        c.close();
        return entradaProdutos;

    }

    public EntradaProduto procuraPorId(String id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM EntradaProduto WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{id});
        EntradaProduto entradaProduto = null;
        while (c.moveToNext()) {
            entradaProduto = new EntradaProduto();
            entradaProduto.setId(c.getString(c.getColumnIndex("id")));
            entradaProduto.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            entradaProduto.setPrecoDeCompra(Double.valueOf(c.getString(c.getColumnIndex("precoDeCompra"))));
            entradaProduto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            entradaProduto.setQuantidadeVendidaMovimentada(Integer.parseInt(c.getString(c.getColumnIndex("quantidadeVendidaMovimentada"))));
            entradaProduto.setData(c.getString(c.getColumnIndex("data")));
            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            entradaProduto.setProduto(produtoDAO.procuraPorId(c.getString(c.getColumnIndex("produto_id"))));
            entradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            produtoDAO.close();


        }
        c.close();
        return entradaProduto;

    }

}
