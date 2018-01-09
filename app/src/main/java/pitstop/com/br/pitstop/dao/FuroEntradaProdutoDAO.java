package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.Furo;
import pitstop.com.br.pitstop.model.FuroEntradaProduto;

/**
 * Created by wilso on 15/12/2017.
 */

public class FuroEntradaProdutoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;
    public FuroEntradaProdutoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(FuroEntradaProduto furoEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", furoEntradaProduto.getId());
        dados.put("quantidade", furoEntradaProduto.getQuantidade());
        dados.put("sincronizado",furoEntradaProduto.getSincronizado());
        dados.put("id_furo",furoEntradaProduto.getIdFuro());
        dados.put("id_entradaProduto",furoEntradaProduto.getIdEntradaProduto());



        db.insert("Furo_EntradaProduto", null, dados);
    }
    public void insereLista(List<FuroEntradaProduto> furoEntradaProdutos) {


        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (FuroEntradaProduto furoEntradaProduto: furoEntradaProdutos) {
            ContentValues dados = new ContentValues();
            dados.put("id", furoEntradaProduto.getId());
            dados.put("quantidade", furoEntradaProduto.getQuantidade());
            dados.put("sincronizado",furoEntradaProduto.getSincronizado());
            dados.put("id_furo",furoEntradaProduto.getIdFuro());
            dados.put("id_entradaProduto",furoEntradaProduto.getIdEntradaProduto());



            db.insert("Furo_EntradaProduto", null, dados);

        }

    }

    public void deleta(FuroEntradaProduto furoEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String[] params = {furoEntradaProduto.getId().toString()};
        db.delete("Furo_EntradaProduto", "id = ?", params);
    }

    public List<FuroEntradaProduto> listarfuroEntradaProduto() {

        String sql = "SELECT * FROM Furo_EntradaProduto;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<FuroEntradaProduto> furoEntradaProdutos = new ArrayList<FuroEntradaProduto>();
        while (c.moveToNext()) {
            FuroEntradaProduto furoEntradaProduto = new FuroEntradaProduto();
            furoEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            furoEntradaProduto.setIdFuro(c.getString(c.getColumnIndex("id_furo")));
            furoEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            furoEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            furoEntradaProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));



            furoEntradaProdutos.add(furoEntradaProduto);

        }
        c.close();
        return furoEntradaProdutos;
    }







    public void sincroniza(List<FuroEntradaProduto> FurosEntradaProdutos) {
        for (FuroEntradaProduto furoEntradaProduto :
                FurosEntradaProdutos) {

            furoEntradaProduto.sincroniza();

            if (existe(furoEntradaProduto)) {
                altera(furoEntradaProduto);
            } else {
                insere(furoEntradaProduto);
            }

        }
    }

    private boolean existe(FuroEntradaProduto furoEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Furo_EntradaProduto WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{furoEntradaProduto.getId()});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }
    public void close(){
        databaseHelper.close();
        database = null;
    }
    public List<FuroEntradaProduto> listaNaoSincronizados(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Furo_EntradaProduto WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);


        List<FuroEntradaProduto> furoEntradaProdutos = new ArrayList<FuroEntradaProduto>();
        while (c.moveToNext()) {
            FuroEntradaProduto furoEntradaProduto = new FuroEntradaProduto();
            furoEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            furoEntradaProduto.setIdFuro(c.getString(c.getColumnIndex("id_furo")));
            furoEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            furoEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            furoEntradaProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));



            furoEntradaProdutos.add(furoEntradaProduto);

        }
        c.close();
        return furoEntradaProdutos;

    }

    public void altera(FuroEntradaProduto furoEntradaProduto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", furoEntradaProduto.getId());
        dados.put("quantidade", furoEntradaProduto.getQuantidade());
        dados.put("sincronizado",furoEntradaProduto.getSincronizado());
        dados.put("id_furo",furoEntradaProduto.getIdFuro());
        dados.put("id_entradaProduto",furoEntradaProduto.getIdEntradaProduto());




        String[] params = {furoEntradaProduto.getId().toString()};
        db.update("Furo_EntradaProduto", dados, "id = ?", params);
    }



    public FuroEntradaProduto procuraPorId(String id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Furo_EntradaProduto WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{id});
        FuroEntradaProduto furoEntradaProduto=null;
        while (c.moveToNext()) {
            furoEntradaProduto = new FuroEntradaProduto();
            furoEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            furoEntradaProduto.setIdFuro(c.getString(c.getColumnIndex("id_furo")));
            furoEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            furoEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            furoEntradaProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));


        }
        c.close();
        return furoEntradaProduto;

    }

    public List<FuroEntradaProduto> procuraPorFuro(String id_furo) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Furo_EntradaProduto WHERE id_furo=?";
        Cursor c = db.rawQuery(existe, new String[]{id_furo});
        FuroEntradaProduto furoEntradaProduto=null;
        List<FuroEntradaProduto> furoEntradaProdutos = new ArrayList<>();
        while (c.moveToNext()) {
            furoEntradaProduto = new FuroEntradaProduto();
            furoEntradaProduto.setId(c.getString(c.getColumnIndex("id")));
            furoEntradaProduto.setIdFuro(c.getString(c.getColumnIndex("id_furo")));
            furoEntradaProduto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            furoEntradaProduto.setIdEntradaProduto(c.getString(c.getColumnIndex("id_entradaProduto")));
            furoEntradaProduto.setQuantidade(Integer.valueOf(c.getString(c.getColumnIndex("quantidade"))));
            furoEntradaProdutos.add(furoEntradaProduto);

        }
        c.close();
        return furoEntradaProdutos;

    }
}
