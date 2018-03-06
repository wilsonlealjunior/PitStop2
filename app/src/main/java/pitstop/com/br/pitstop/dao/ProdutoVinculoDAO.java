package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilso on 30/12/2017.
 */

public class ProdutoVinculoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;

    public ProdutoVinculoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(String id_produto, String id_vinculo) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id_produto", id_produto);
        dados.put("id", id_vinculo);

        db.insert("ProdutoVinculo", null, dados);
    }

    public void insereLista(String id_produto, List<String> id_vinculo) {


        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (String vinculo : id_vinculo) {
            ContentValues dados = new ContentValues();
            dados.put("id", vinculo);
            dados.put("id_produto", id_produto);
            db.insert("ProdutoVinculo", null, dados);

        }

    }

    private boolean existe(String id_vinculo) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM ProdutoVinculo WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{id_vinculo});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }

    public void sincroniza(String id_produto, List<String> id_vinculo) {
        for (String id :
                id_vinculo) {
//            Log.i("log3", String.valueOf(produto.getQuantidade()));
//            produto.sincroniza();

            if (existe(id)) {
                close();
            } else {
                insere(id_produto, id);
                close();
            }

        }
    }

    public void deleta(String id_vinculo) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {id_vinculo};
        db.delete("ProdutoVinculo", "id = ?", params);
    }

    public void close() {
        databaseHelper.close();
        database = null;
    }


    public String procuraPorId(String id_vinculo) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM ProdutoVinculo WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{id_vinculo});
        String ProdutoVinculo = null;
        while (c.moveToNext()) {
            ProdutoVinculo = (c.getString(c.getColumnIndex("id")));
        }
        c.close();
        return ProdutoVinculo;

    }

    public List<String> procuraPorProduto(String id_produto) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM ProdutoVinculo WHERE id_produto=?";
        Cursor c = db.rawQuery(existe, new String[]{id_produto});
        List<String> vinculos = new ArrayList<>();
        while (c.moveToNext()) {
            String ProdutoVinculo = (c.getString(c.getColumnIndex("id")));
            vinculos.add(ProdutoVinculo);

        }
        c.close();
        return vinculos;

    }

}

