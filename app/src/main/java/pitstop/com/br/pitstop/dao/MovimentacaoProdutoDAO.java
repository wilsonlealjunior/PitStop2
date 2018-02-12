package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.MovimentacaoProduto;

/**
 * Created by wilso on 14/11/2017.
 */

public class MovimentacaoProdutoDAO {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;
    public MovimentacaoProdutoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(MovimentacaoProduto movimentacaoProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", movimentacaoProduto.getId());
        dados.put("desativado",movimentacaoProduto.getDesativado());
        dados.put("id_lojaDe", movimentacaoProduto.getIdLojaDe());
        dados.put("id_Produto", movimentacaoProduto.getIdProduto());
        dados.put("id_lojaPara", movimentacaoProduto.getIdLojaPara());
        dados.put("quantidade", movimentacaoProduto.getQuantidade());
        dados.put("sincronizado",movimentacaoProduto.getSincronizado());
        dados.put("data",movimentacaoProduto.getData());

        db.insert("movimentacao_produto", null, dados);
    }
    public void insereLista(List<MovimentacaoProduto> movimentacaoProdutos) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (MovimentacaoProduto movimentacaoProduto: movimentacaoProdutos) {
            ContentValues dados = new ContentValues();
            dados.put("id", movimentacaoProduto.getId());
            dados.put("desativado",movimentacaoProduto.getDesativado());
            dados.put("id_lojaDe", movimentacaoProduto.getIdLojaDe());
            dados.put("id_Produto", movimentacaoProduto.getIdProduto());
            dados.put("id_lojaPara", movimentacaoProduto.getIdLojaPara());
            dados.put("quantidade", movimentacaoProduto.getQuantidade());
            dados.put("sincronizado",movimentacaoProduto.getSincronizado());
            dados.put("data",movimentacaoProduto.getData());

            db.insert("movimentacao_produto", null, dados);

        }


    }

    public void deleta(MovimentacaoProduto movimentacaoProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {movimentacaoProduto.getId().toString()};
        db.delete("movimentacao_produto", "id = ?", params);
    }

    public List<MovimentacaoProduto> listarMovimentacaoProduto() {
        String sql = "SELECT * FROM movimentacao_produto;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<MovimentacaoProduto> movimentacaoProdutos = new ArrayList<MovimentacaoProduto>();
        while (c.moveToNext()) {
            MovimentacaoProduto movimentacaoProduto = new MovimentacaoProduto();
            movimentacaoProduto.setId(c.getString(c.getColumnIndex("id")));
            movimentacaoProduto.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            movimentacaoProduto.setIdProduto(c.getString(c.getColumnIndex("id_Produto")));
            movimentacaoProduto.setIdLojaDe(c.getString(c.getColumnIndex("id_lojaDe")));
            movimentacaoProduto.setIdLojaPara(c.getString(c.getColumnIndex("id_lojaPara")));
            movimentacaoProduto.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            movimentacaoProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            movimentacaoProduto.setData(c.getString(c.getColumnIndex("data")));
            movimentacaoProdutos.add(movimentacaoProduto);

        }
        c.close();
        return movimentacaoProdutos;
    }

    public List<MovimentacaoProduto> relatorio(String de, String ate) {
        String sql = "SELECT * FROM movimentacao_produto where desativado=0 and data between '"+de+"' and '"+ate+"' order by data desc;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<MovimentacaoProduto> movimentacaoProdutos = new ArrayList<MovimentacaoProduto>();
        while (c.moveToNext()) {
            MovimentacaoProduto movimentacaoProduto = new MovimentacaoProduto();
            movimentacaoProduto.setId(c.getString(c.getColumnIndex("id")));
            movimentacaoProduto.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            movimentacaoProduto.setIdProduto(c.getString(c.getColumnIndex("id_Produto")));
            movimentacaoProduto.setIdLojaDe(c.getString(c.getColumnIndex("id_lojaDe")));
            movimentacaoProduto.setIdLojaPara(c.getString(c.getColumnIndex("id_lojaPara")));
            movimentacaoProduto.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            movimentacaoProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            movimentacaoProduto.setData(c.getString(c.getColumnIndex("data")));
            movimentacaoProdutos.add(movimentacaoProduto);

        }
        c.close();
        return movimentacaoProdutos;
    }


    public void sincroniza(List<MovimentacaoProduto> movimentacaoProdutos) {
        for (MovimentacaoProduto movimentacaoProduto :
                movimentacaoProdutos) {

            movimentacaoProduto.sincroniza();

            if (existe(movimentacaoProduto)) {
                if(movimentacaoProduto.estaDesativado()){
                    deleta(movimentacaoProduto);
                } else {
                    altera(movimentacaoProduto);
                }
            } else if (!movimentacaoProduto.estaDesativado()){
                insere(movimentacaoProduto);
            }

        }
    }

    private boolean existe(MovimentacaoProduto movimentacaoProduto) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM movimentacao_produto WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{movimentacaoProduto.getId()});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }
    public void close(){
        databaseHelper.close();
        database = null;
    }
    public List<MovimentacaoProduto> listaNaoSincronizados(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM movimentacao_produto WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);
        List<MovimentacaoProduto> movimentacaoProdutos = new ArrayList<MovimentacaoProduto>();
        while (c.moveToNext()) {
            MovimentacaoProduto movimentacaoProduto = new MovimentacaoProduto();
            movimentacaoProduto.setId(c.getString(c.getColumnIndex("id")));
            movimentacaoProduto.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            movimentacaoProduto.setIdProduto(c.getString(c.getColumnIndex("id_Produto")));
            movimentacaoProduto.setIdLojaDe(c.getString(c.getColumnIndex("id_lojaDe")));
            movimentacaoProduto.setIdLojaPara(c.getString(c.getColumnIndex("id_lojaPara")));
            movimentacaoProduto.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            movimentacaoProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            movimentacaoProduto.setData(c.getString(c.getColumnIndex("data")));


            movimentacaoProdutos.add(movimentacaoProduto);

        }
        c.close();
        return movimentacaoProdutos;

    }

    public void altera(MovimentacaoProduto movimentacaoProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", movimentacaoProduto.getId());
        dados.put("desativado",movimentacaoProduto.getDesativado());
        dados.put("id_Produto", movimentacaoProduto.getIdProduto());
        dados.put("id_lojaDe", movimentacaoProduto.getIdLojaDe());
        dados.put("id_lojaPara", movimentacaoProduto.getIdLojaPara());
        dados.put("quantidade", movimentacaoProduto.getQuantidade());
        dados.put("sincronizado",movimentacaoProduto.getSincronizado());
        dados.put("data",movimentacaoProduto.getData());


        String[] params = {movimentacaoProduto.getId().toString()};
        db.update("movimentacao_produto", dados, "id = ?", params);
    }



    public MovimentacaoProduto procuraPorId(String nome) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM movimentacao_produto WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{nome});
        MovimentacaoProduto movimentacaoProduto = new MovimentacaoProduto();
        while (c.moveToNext()) {
            movimentacaoProduto.setId(c.getString(c.getColumnIndex("id")));
            movimentacaoProduto.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            movimentacaoProduto.setIdLojaDe(c.getString(c.getColumnIndex("id_lojaDe")));
            movimentacaoProduto.setIdProduto(c.getString(c.getColumnIndex("id_Produto")));
            movimentacaoProduto.setIdLojaPara(c.getString(c.getColumnIndex("id_lojaPara")));
            movimentacaoProduto.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            movimentacaoProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            movimentacaoProduto.setData(c.getString(c.getColumnIndex("data")));


        }
        c.close();
        return movimentacaoProduto;

    }
}
