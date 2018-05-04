package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.ItemAvaria;
import pitstop.com.br.pitstop.model.Loja;


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
        dados.put("id_produto", avaria.getIdProduto());
        dados.put("quantidade", avaria.getQuantidade());
        ItemAvariaDAO itemAvariaDAO = new ItemAvariaDAO(context);
        for (ItemAvaria itemAvaria : avaria.getAvariaEntradeProdutos()) {
            itemAvariaDAO.insere(itemAvaria);
            itemAvariaDAO.close();
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
            dados.put("id_produto", avaria.getIdProduto());
            dados.put("quantidade", avaria.getQuantidade());
            ItemAvariaDAO itemAvariaDAO = new ItemAvariaDAO(context);
            for (ItemAvaria itemAvaria : avaria.getAvariaEntradeProdutos()) {
                itemAvariaDAO.insere(itemAvaria);
                itemAvariaDAO.close();
            }

            db.insert("Avaria", null, dados);
        }


    }

    public void deleta(Avaria avaria) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ItemAvariaDAO itemAvariaDAO = new ItemAvariaDAO(context);
        for (ItemAvaria itemAvaria : avaria.getAvariaEntradeProdutos()) {
            itemAvariaDAO.deleta(itemAvaria);
            itemAvariaDAO.close();
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
            avaria.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            avaria.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));

            ItemAvariaDAO itemAvariaDAO = new ItemAvariaDAO(context);
            avaria.setAvariaEntradeProdutos(itemAvariaDAO.procuraPorAvaria(avaria.getId()));
            itemAvariaDAO.close();


            avarias.add(avaria);

        }
        c.close();
        return avarias;
    }

    public double relatorioResumo(Loja lojaEscolhida, String de, String ate) {
        String lojaEscolhidaId ="%";
        if(lojaEscolhida!=null){
            lojaEscolhidaId = lojaEscolhida.getId();
        }
        String sql = "SELECT ifnull(sum(prejuizo),0) as prejuizo FROM Avaria where desativado=0 and id_loja like '" + lojaEscolhidaId + "' and data between '" + de + "' and '" + ate + "';";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        double prejuizo=0;
        while (c.moveToNext()) {
            prejuizo = Double.valueOf((c.getString(c.getColumnIndex("prejuizo"))));

        }
        c.close();
        return prejuizo;
    }

    public List<Avaria> relatorio(String lojaEscolhidaId, String de, String ate) {
        String sql = "SELECT * FROM Avaria where desativado=0 and id_loja like '" + lojaEscolhidaId + "' and data between '" + de + "' and '" + ate + "' order by data desc;";
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
            avaria.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            avaria.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            ItemAvariaDAO itemAvariaDAO = new ItemAvariaDAO(context);
            avaria.setAvariaEntradeProdutos(itemAvariaDAO.procuraPorAvaria(avaria.getId()));
            itemAvariaDAO.close();


            avarias.add(avaria);

        }
        c.close();
        return avarias;
    }


    public List<Avaria> buscaPorLoja(String Idloja) {
        String sql = "SELECT * FROM Avaria WHERE id_loja=? ";
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
            avaria.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            avaria.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            ItemAvariaDAO itemAvariaDAO = new ItemAvariaDAO(context);
            avaria.setAvariaEntradeProdutos(itemAvariaDAO.procuraPorAvaria(avaria.getId()));
            itemAvariaDAO.close();


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
                close();
                if (avaria.estaDesativado()) {
                    deleta(avaria);
                    close();
                } else {
                    altera(avaria);
                    close();
                }
            } else if (!avaria.estaDesativado()) {
                insere(avaria);
                close();
            }

        }
    }

    private boolean existe(Avaria avaria) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Avaria WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{avaria.getId()});
        int quantidade = cursor.getCount();
        cursor.close();
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
            avaria.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            avaria.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            ItemAvariaDAO itemAvariaDAO = new ItemAvariaDAO(context);
            avaria.setAvariaEntradeProdutos(itemAvariaDAO.procuraPorAvaria(avaria.getId()));
            itemAvariaDAO.close();


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
        dados.put("id_produto", avaria.getIdProduto());
        dados.put("quantidade", avaria.getQuantidade());


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
            avaria.setIdProduto(c.getString(c.getColumnIndex("id_produto")));
            avaria.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            ItemAvariaDAO itemAvariaDAO = new ItemAvariaDAO(context);
            avaria.setAvariaEntradeProdutos(itemAvariaDAO.procuraPorAvaria(avaria.getId()));
            itemAvariaDAO.close();


        }
        c.close();
        return avaria;

    }


}
