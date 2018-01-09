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

public class AvariaDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;

    public AvariaDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(Avaria avaria) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", avaria.getId());
        dados.put("id_loja", avaria.getIdLoja());
        dados.put("sincronizado", avaria.getSincronizado());
        dados.put("data", avaria.getData());
        dados.put("prejuizo", avaria.getPrejuizo());
        dados.put("desativado", avaria.getDesativado());

        AvariaEntradaProdutoDAO avariaEntradaProdutoDAO = new AvariaEntradaProdutoDAO(context);
        for (AvariaEntradaProduto avariaEntradaProduto : avaria.getAvariaEntradeProdutos()) {
            avariaEntradaProdutoDAO.insere(avariaEntradaProduto);
            avariaEntradaProdutoDAO.close();
        }

        db.insert("Avaria", null, dados);
    }

    public void insereLista(List<Avaria> avarias) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (Avaria avaria : avarias) {
            ContentValues dados = new ContentValues();
            dados.put("id", avaria.getId());
            dados.put("id_loja", avaria.getIdLoja());
            dados.put("sincronizado", avaria.getSincronizado());
            dados.put("data", avaria.getData());
            dados.put("prejuizo", avaria.getPrejuizo());
            dados.put("desativado", avaria.getDesativado());

            AvariaEntradaProdutoDAO avariaEntradaProdutoDAO = new AvariaEntradaProdutoDAO(context);
            for (AvariaEntradaProduto avariaEntradaProduto : avaria.getAvariaEntradeProdutos()) {
                avariaEntradaProdutoDAO.insere(avariaEntradaProduto);
                avariaEntradaProdutoDAO.close();
            }

            db.insert("Avaria", null, dados);
        }


    }

    public void deleta(Avaria avaria) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        AvariaEntradaProdutoDAO avariaEntradaProdutoDAO = new AvariaEntradaProdutoDAO(context);
        for (AvariaEntradaProduto avariaEntradaProduto : avaria.getAvariaEntradeProdutos()) {
            avariaEntradaProdutoDAO.deleta(avariaEntradaProduto);
            avariaEntradaProdutoDAO.close();
        }

        String[] params = {avaria.getId().toString()};
        db.delete("Avaria", "id = ?", params);
    }

    public List<Avaria> listarAvarias() {
        String sql = "SELECT * FROM Avaria;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Avaria> avarias = new ArrayList<Avaria>();
        while (c.moveToNext()) {
            Avaria avaria = new Avaria();
            avaria.setId(c.getString(c.getColumnIndex("id")));

            avaria.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            avaria.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            avaria.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            avaria.setData(c.getString(c.getColumnIndex("data")));
            avaria.setPrejuizo(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));

            AvariaEntradaProdutoDAO avariaEntradaProdutoDAO = new AvariaEntradaProdutoDAO(context);
            avaria.setAvariaEntradeProdutos(avariaEntradaProdutoDAO.procuraPorAvaria(avaria.getId()));
            avariaEntradaProdutoDAO.close();


            avarias.add(avaria);

        }
        c.close();
        return avarias;
    }

    public List<Avaria> relatorio(String lojaEscolhidaId, String de, String ate) {
        String sql = "SELECT * FROM Avaria where id_loja like '" + lojaEscolhidaId + "' and data between '" + de + "' and '" + ate + "' order by data desc;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Avaria> avarias = new ArrayList<Avaria>();
        while (c.moveToNext()) {
            Avaria avaria = new Avaria();
            avaria.setId(c.getString(c.getColumnIndex("id")));

            avaria.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            avaria.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            avaria.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            avaria.setData(c.getString(c.getColumnIndex("data")));
            avaria.setPrejuizo(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));

            AvariaEntradaProdutoDAO avariaEntradaProdutoDAO = new AvariaEntradaProdutoDAO(context);
            avaria.setAvariaEntradeProdutos(avariaEntradaProdutoDAO.procuraPorAvaria(avaria.getId()));
            avariaEntradaProdutoDAO.close();


            avarias.add(avaria);

        }
        c.close();
        return avarias;
    }


    public List<Avaria> buscaPorLoja(String Idloja) {
        String sql = "SELECT * FROM Avaria WHERE id_loja=?";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{Idloja});
        List<Avaria> avarias = new ArrayList<Avaria>();
        while (c.moveToNext()) {
            Avaria avaria = new Avaria();
            avaria.setId(c.getString(c.getColumnIndex("id")));

            avaria.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            avaria.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            avaria.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            avaria.setData(c.getString(c.getColumnIndex("data")));
            avaria.setPrejuizo(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));

            AvariaEntradaProdutoDAO avariaEntradaProdutoDAO = new AvariaEntradaProdutoDAO(context);
            avaria.setAvariaEntradeProdutos(avariaEntradaProdutoDAO.procuraPorAvaria(avaria.getId()));
            avariaEntradaProdutoDAO.close();


            avarias.add(avaria);

        }
        c.close();
        return avarias;
    }


    public void sincroniza(List<Avaria> avarias) {
        for (Avaria avaria :
                avarias) {

            avaria.sincroniza();

            if (existe(avaria)) {
                if (avaria.estaDesativado()) {
                    deleta(avaria);
                } else {
                    altera(avaria);
                }
            } else if (!avaria.estaDesativado()) {
                insere(avaria);
            }

        }
    }

    private boolean existe(Avaria avaria) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Avaria WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{avaria.getId()});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }

    public void close() {
        databaseHelper.close();
        database = null;
    }

    public List<Avaria> listaNaoSincronizados() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Avaria WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);


        List<Avaria> avarias = new ArrayList<Avaria>();
        while (c.moveToNext()) {
            Avaria avaria = new Avaria();
            avaria.setId(c.getString(c.getColumnIndex("id")));

            avaria.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            avaria.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            avaria.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            avaria.setData(c.getString(c.getColumnIndex("data")));
            avaria.setPrejuizo(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));

            AvariaEntradaProdutoDAO avariaEntradaProdutoDAO = new AvariaEntradaProdutoDAO(context);
            avaria.setAvariaEntradeProdutos(avariaEntradaProdutoDAO.procuraPorAvaria(avaria.getId()));
            avariaEntradaProdutoDAO.close();


            avarias.add(avaria);

        }
        c.close();
        return avarias;

    }

    public void altera(Avaria avaria) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", avaria.getId());

        dados.put("id_loja", avaria.getIdLoja());
        dados.put("desativado", avaria.getDesativado());
        dados.put("sincronizado", avaria.getSincronizado());
        dados.put("data", avaria.getData());
        dados.put("prejuizo", avaria.getPrejuizo());


        String[] params = {avaria.getId().toString()};
        db.update("Avaria", dados, "id = ?", params);
    }


    public Avaria procuraPorId(String id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Avaria WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{id});
        Avaria avaria = null;
        while (c.moveToNext()) {
            avaria = new Avaria();
            avaria.setId(c.getString(c.getColumnIndex("id")));
            avaria.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            avaria.setIdLoja(c.getString(c.getColumnIndex("id_loja")));

            avaria.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            avaria.setData(c.getString(c.getColumnIndex("data")));
            avaria.setPrejuizo(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));

            AvariaEntradaProdutoDAO avariaEntradaProdutoDAO = new AvariaEntradaProdutoDAO(context);
            avaria.setAvariaEntradeProdutos(avariaEntradaProdutoDAO.procuraPorAvaria(avaria.getId()));
            avariaEntradaProdutoDAO.close();


        }
        c.close();
        return avaria;

    }


}
