package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.AvariaEntradaProduto;
import pitstop.com.br.pitstop.model.Furo;
import pitstop.com.br.pitstop.model.FuroEntradaProduto;

/**
 * Created by wilso on 15/12/2017.
 */

public class FuroDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;

    public FuroDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(Furo furo) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", furo.getId());
        dados.put("id_loja", furo.getIdLoja());
        dados.put("id_usuario", furo.getIdUsuario());
        dados.put("desativado",furo.getDesativado());
        dados.put("sincronizado", furo.getSincronizado());
        dados.put("data", furo.getData());
        dados.put("valor", furo.getValor());

        FuroEntradaProdutoDAO furoEntradaProdutoDAO = new FuroEntradaProdutoDAO(context);
        for (FuroEntradaProduto furoEntradaProduto : furo.getFuroEntradeProdutos()) {
            furoEntradaProdutoDAO.insere(furoEntradaProduto);
            furoEntradaProdutoDAO.close();
        }

        db.insert("Furo", null, dados);
    }

    public void insereLista(List<Furo> furos) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (Furo furo : furos) {
            ContentValues dados = new ContentValues();
            dados.put("id", furo.getId());
            dados.put("id_loja", furo.getIdLoja());
            dados.put("desativado",furo.getDesativado());
            dados.put("id_usuario", furo.getIdUsuario());
            dados.put("sincronizado", furo.getSincronizado());
            dados.put("data", furo.getData());
            dados.put("valor", furo.getValor());

            FuroEntradaProdutoDAO furoEntradaProdutoDAO = new FuroEntradaProdutoDAO(context);
            for (FuroEntradaProduto furoEntradaProduto : furo.getFuroEntradeProdutos()) {
                furoEntradaProdutoDAO.insere(furoEntradaProduto);
                furoEntradaProdutoDAO.close();
            }

            db.insert("Furo", null, dados);
        }


    }

    public void deleta(Furo furo) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        FuroEntradaProdutoDAO furoEntradaProdutoDAO = new FuroEntradaProdutoDAO(context);

        for (FuroEntradaProduto furoEntradaProduto : furo.getFuroEntradeProdutos()) {
            furoEntradaProdutoDAO.deleta(furoEntradaProduto);
            furoEntradaProdutoDAO.close();
        }

        String[] params = {furo.getId().toString()};
        db.delete("Furo", "id = ?", params);
    }

    public List<Furo> listarfuros() {
        String sql = "SELECT * FROM Furo;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Furo> furos = new ArrayList<Furo>();
        while (c.moveToNext()) {
            Furo furo = new Furo();
            furo.setId(c.getString(c.getColumnIndex("id")));

            furo.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            furo.setIdUsuario(c.getString(c.getColumnIndex("id_usuario")));
            furo.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            furo.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            furo.setData(c.getString(c.getColumnIndex("data")));
            furo.setValor(Double.valueOf(c.getString(c.getColumnIndex("valor"))));

            FuroEntradaProdutoDAO furoEntradaProdutoDAO = new FuroEntradaProdutoDAO(context);
            furo.setFuroEntradeProdutos(furoEntradaProdutoDAO.procuraPorFuro(furo.getId()));
            furoEntradaProdutoDAO.close();


            furos.add(furo);

        }
        c.close();
        return furos;
    }


    public List<Furo> buscaPorLoja(String Idloja) {
        String sql = "SELECT * FROM Furo WHERE id_loja=?";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{Idloja});
        List<Furo> furos = new ArrayList<Furo>();
        while (c.moveToNext()) {
            Furo furo = new Furo();
            furo.setId(c.getString(c.getColumnIndex("id")));

            furo.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            furo.setIdUsuario(c.getString(c.getColumnIndex("id_usuario")));
            furo.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            furo.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            furo.setData(c.getString(c.getColumnIndex("data")));
            furo.setValor(Double.valueOf(c.getString(c.getColumnIndex("valor"))));

            FuroEntradaProdutoDAO furoEntradaProdutoDAO = new FuroEntradaProdutoDAO(context);
            furo.setFuroEntradeProdutos(furoEntradaProdutoDAO.procuraPorFuro(furo.getId()));
            furoEntradaProdutoDAO.close();


            furos.add(furo);

        }
        c.close();
        return furos;
    }

    public List<Furo> relatorio(String de, String ate,String Idloja, String funcionarioId) {
        String sql = "SELECT * FROM Furo WHERE desativado=0 and data between '"+de+"' and '"+ate+"' and id_loja like '"+Idloja+"' and id_usuario like '"+funcionarioId+"' order by data desc";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{});

        List<Furo> furos = new ArrayList<Furo>();
        while (c.moveToNext()) {
            Furo furo = new Furo();
            furo.setId(c.getString(c.getColumnIndex("id")));

            furo.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            furo.setIdUsuario(c.getString(c.getColumnIndex("id_usuario")));
            furo.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            furo.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            furo.setData(c.getString(c.getColumnIndex("data")));
            furo.setValor(Double.valueOf(c.getString(c.getColumnIndex("valor"))));

            FuroEntradaProdutoDAO furoEntradaProdutoDAO = new FuroEntradaProdutoDAO(context);
            furo.setFuroEntradeProdutos(furoEntradaProdutoDAO.procuraPorFuro(furo.getId()));
            furoEntradaProdutoDAO.close();


            furos.add(furo);

        }
        c.close();
        return furos;

    }



    public void sincroniza(List<Furo> furos) {
        for (Furo furo :
                furos) {

            furo.sincroniza();

            if (existe(furo)) {
                if(furo.estaDesativado()){
                    deleta(furo);
                } else {
                    altera(furo);
                }
            } else if (!furo.estaDesativado()){
                insere(furo);
            }

        }
    }

    private boolean existe(Furo furo) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Furo WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{furo.getId()});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }

    public void close() {
        databaseHelper.close();
        database = null;
    }

    public List<Furo> listaNaoSincronizados() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Furo WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);


        List<Furo> furos = new ArrayList<Furo>();
        while (c.moveToNext()) {
            Furo furo = new Furo();
            furo.setId(c.getString(c.getColumnIndex("id")));

            furo.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            furo.setIdUsuario(c.getString(c.getColumnIndex("id_usuario")));
            furo.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            furo.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            furo.setData(c.getString(c.getColumnIndex("data")));
            furo.setValor(Double.valueOf(c.getString(c.getColumnIndex("valor"))));

            FuroEntradaProdutoDAO furoEntradaProdutoDAO = new FuroEntradaProdutoDAO(context);
            furo.setFuroEntradeProdutos(furoEntradaProdutoDAO.procuraPorFuro(furo.getId()));
            furoEntradaProdutoDAO.close();


            furos.add(furo);

        }
        c.close();
        return furos;

    }

    public void altera(Furo furo) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", furo.getId());
        dados.put("id_loja", furo.getIdLoja());
        dados.put("id_usuario", furo.getIdUsuario());
        dados.put("desativado",furo.getDesativado());
        dados.put("sincronizado", furo.getSincronizado());
        dados.put("data", furo.getData());
        dados.put("valor", furo.getValor());


        String[] params = {furo.getId().toString()};
        db.update("Furo", dados, "id = ?", params);
    }


    public Furo procuraPorId(String id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Furo WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{id});
        Furo furo = null;
        while (c.moveToNext()) {
            furo = new Furo();
            furo.setId(c.getString(c.getColumnIndex("id")));

            furo.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            furo.setIdUsuario(c.getString(c.getColumnIndex("id_usuario")));
            furo.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            furo.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            furo.setData(c.getString(c.getColumnIndex("data")));
            furo.setValor(Double.valueOf(c.getString(c.getColumnIndex("valor"))));

            FuroEntradaProdutoDAO furoEntradaProdutoDAO = new FuroEntradaProdutoDAO(context);
            furo.setFuroEntradeProdutos(furoEntradaProdutoDAO.procuraPorFuro(furo.getId()));
            furoEntradaProdutoDAO.close();


        }
        c.close();
        return furo;

    }

}
