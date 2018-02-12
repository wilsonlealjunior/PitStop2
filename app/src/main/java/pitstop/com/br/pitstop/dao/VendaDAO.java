package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.model.AvariaEntradaProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.model.VendaEntradaProduto;
import pitstop.com.br.pitstop.model.Venda;

/**
 * Created by wilso on 12/11/2017.
 */

public class VendaDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;

    public VendaDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(Venda venda) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", venda.getId());
        dados.put("nomeVendedor", venda.getNomeVendedor());
        dados.put("desativado", venda.getDesativado());
        dados.put("formaDePagamento", venda.getFormaDePagamento());
        dados.put("dataDaVenda", venda.getDataDaVenda());
        dados.put("sincronizado", venda.getSincronizado());
        dados.put("id_loja", venda.getIdLoja());
        dados.put("total", venda.getTotal());
        dados.put("totalCartao", venda.getTotalCartao());
        dados.put("prejuizo", venda.getLucro());
        VendaEntradaProdutoDAO vendaEntradaProdutoDAO = new VendaEntradaProdutoDAO(context);
        for (VendaEntradaProduto pl : venda.getVendaEntradaProdutos()) {
            vendaEntradaProdutoDAO.insere(pl);
            vendaEntradaProdutoDAO.close();
        }

        db.insert("Vendas", null, dados);
    }

    public void insereLista(List<Venda> vendas) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (Venda venda : vendas) {
            ContentValues dados = new ContentValues();
            dados.put("id", venda.getId());
            dados.put("desativado", venda.getDesativado());
            dados.put("nomeVendedor", venda.getNomeVendedor());
            dados.put("formaDePagamento", venda.getFormaDePagamento());
            dados.put("dataDaVenda", venda.getDataDaVenda());
            dados.put("sincronizado", venda.getSincronizado());
            dados.put("id_loja", venda.getIdLoja());
            dados.put("total", venda.getTotal());

            dados.put("totalCartao", venda.getTotalCartao());
            dados.put("prejuizo", venda.getLucro());
            VendaEntradaProdutoDAO vendaEntradaProdutoDAO = new VendaEntradaProdutoDAO(context);
            for (VendaEntradaProduto pl : venda.getVendaEntradaProdutos()) {
                vendaEntradaProdutoDAO.insere(pl);
                vendaEntradaProdutoDAO.close();
            }

            db.insert("Vendas", null, dados);

        }

    }


    public void deleta(Venda venda) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        VendaEntradaProdutoDAO vendaEntradaProdutoDAO = new VendaEntradaProdutoDAO(context);
        for (VendaEntradaProduto vendaEntradaProduto : venda.getVendaEntradaProdutos()) {
            vendaEntradaProdutoDAO.deleta(vendaEntradaProduto);
            vendaEntradaProdutoDAO.close();
        }

        String[] params = {venda.getId().toString()};
        db.delete("Vendas", "id = ?", params);
    }

    public List<Venda> listarVendas() {
        String sql = "SELECT * FROM Vendas;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Venda> vendas = new ArrayList<Venda>();
        while (c.moveToNext()) {
            Venda venda = new Venda();
            venda.setId(c.getString(c.getColumnIndex("id")));
            venda.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            venda.setFormaDePagamento(c.getString(c.getColumnIndex("formaDePagamento")));
            venda.setDataDaVenda(c.getString(c.getColumnIndex("dataDaVenda")));
            venda.setNomeVendedor((c.getString(c.getColumnIndex("nomeVendedor"))));
            venda.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            venda.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            venda.setTotal(Double.valueOf(c.getString(c.getColumnIndex("total"))));
            venda.setTotalCartao(Double.valueOf(c.getString(c.getColumnIndex("totalCartao"))));
            venda.setLucro(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));
            VendaEntradaProdutoDAO vendaEntradaProdutoDAO = new VendaEntradaProdutoDAO(context);
            venda.setVendaEntradaProdutos(vendaEntradaProdutoDAO.procuraPorVenda(venda));
            vendaEntradaProdutoDAO.close();


            vendas.add(venda);

        }
        c.close();
        return vendas;
    }

    public List<Venda> buscaPorPagamentoELoja(String formaDePagamento, String Idloja) {
        String sql = "SELECT * FROM Vendas WHERE formaDePagamento=? and id_loja=?";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{formaDePagamento, Idloja});

        List<Venda> vendas = new ArrayList<Venda>();
        while (c.moveToNext()) {
            Venda venda = new Venda();
            venda.setId(c.getString(c.getColumnIndex("id")));
            venda.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            venda.setFormaDePagamento(c.getString(c.getColumnIndex("formaDePagamento")));
            venda.setDataDaVenda(c.getString(c.getColumnIndex("dataDaVenda")));
            venda.setNomeVendedor((c.getString(c.getColumnIndex("nomeVendedor"))));
            venda.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            venda.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            venda.setTotal(Double.valueOf(c.getString(c.getColumnIndex("total"))));
            venda.setTotalCartao(Double.valueOf(c.getString(c.getColumnIndex("totalCartao"))));
            venda.setLucro(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));
            VendaEntradaProdutoDAO vendaEntradaProdutoDAO = new VendaEntradaProdutoDAO(context);
            venda.setVendaEntradaProdutos(vendaEntradaProdutoDAO.procuraPorVenda(venda));
            vendaEntradaProdutoDAO.close();


            vendas.add(venda);

        }
        c.close();
        return vendas;
    }

    public List<Venda> relatorio(String de, String ate, String formaDePagamento, Loja L, Usuario f) {
        String funcionario;
        String Idloja;
        if (formaDePagamento == null) {
            formaDePagamento = "%";
        }
        else{
            formaDePagamento = "%"+formaDePagamento+"%";
        }
        if (f == null) {
            funcionario = "%";
        } else {
            funcionario = f.getNome();
        }
        if (L == null) {
            Idloja = "%";
        } else {
            Idloja = L.getId();
        }
        String sql = "SELECT * FROM Vendas WHERE desativado = 0 and dataDaVenda between '" + de + "' and '" + ate + "' and formaDePagamento like '" + formaDePagamento + "' and nomeVendedor like '" + funcionario + "' and id_loja like '" + Idloja + "' order by dataDaVenda desc";
        Log.e("SQL", sql);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{});

        List<Venda> vendas = new ArrayList<>();
        while (c.moveToNext()) {
            Venda venda = new Venda();
            venda.setId(c.getString(c.getColumnIndex("id")));
            venda.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            venda.setFormaDePagamento(c.getString(c.getColumnIndex("formaDePagamento")));
            venda.setDataDaVenda(c.getString(c.getColumnIndex("dataDaVenda")));
            venda.setNomeVendedor((c.getString(c.getColumnIndex("nomeVendedor"))));
            venda.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            venda.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            venda.setTotal(Double.valueOf(c.getString(c.getColumnIndex("total"))));
            venda.setTotalCartao(Double.valueOf(c.getString(c.getColumnIndex("totalCartao"))));
            venda.setLucro(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));
            VendaEntradaProdutoDAO vendaEntradaProdutoDAO = new VendaEntradaProdutoDAO(context);
            venda.setVendaEntradaProdutos(vendaEntradaProdutoDAO.procuraPorVenda(venda));
            vendaEntradaProdutoDAO.close();


            vendas.add(venda);

        }
        c.close();
        return vendas;
    }

    public List<Venda> buscaPorPagamento(String formaDePagamento) {
        String sql = "SELECT * FROM Vendas WHERE formaDePagamento=?";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{formaDePagamento});

        List<Venda> vendas = new ArrayList<Venda>();
        while (c.moveToNext()) {
            Venda venda = new Venda();
            venda.setId(c.getString(c.getColumnIndex("id")));
            venda.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            venda.setFormaDePagamento(c.getString(c.getColumnIndex("formaDePagamento")));
            venda.setDataDaVenda(c.getString(c.getColumnIndex("dataDaVenda")));
            venda.setNomeVendedor((c.getString(c.getColumnIndex("nomeVendedor"))));
            venda.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            venda.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            venda.setTotal(Double.valueOf(c.getString(c.getColumnIndex("total"))));
            venda.setTotalCartao(Double.valueOf(c.getString(c.getColumnIndex("totalCartao"))));
            venda.setLucro(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));

            VendaEntradaProdutoDAO vendaEntradaProdutoDAO = new VendaEntradaProdutoDAO(context);
            venda.setVendaEntradaProdutos(vendaEntradaProdutoDAO.procuraPorVenda(venda));
            vendaEntradaProdutoDAO.close();


            vendas.add(venda);

        }
        c.close();
        return vendas;
    }

    public List<Venda> buscaPorLoja(String Idloja) {
        String sql = "SELECT * FROM Vendas WHERE id_loja=?";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, new String[]{Idloja});

        List<Venda> vendas = new ArrayList<Venda>();
        while (c.moveToNext()) {
            Venda venda = new Venda();
            venda.setId(c.getString(c.getColumnIndex("id")));
            venda.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            venda.setFormaDePagamento(c.getString(c.getColumnIndex("formaDePagamento")));
            venda.setDataDaVenda(c.getString(c.getColumnIndex("dataDaVenda")));
            venda.setNomeVendedor((c.getString(c.getColumnIndex("nomeVendedor"))));
            venda.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            venda.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            venda.setTotal(Double.valueOf(c.getString(c.getColumnIndex("total"))));
            venda.setTotalCartao(Double.valueOf(c.getString(c.getColumnIndex("totalCartao"))));
            venda.setLucro(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));
            VendaEntradaProdutoDAO vendaEntradaProdutoDAO = new VendaEntradaProdutoDAO(context);
            venda.setVendaEntradaProdutos(vendaEntradaProdutoDAO.procuraPorVenda(venda));
            vendaEntradaProdutoDAO.close();

            vendas.add(venda);

        }
        c.close();
        return vendas;
    }


    public void sincroniza(List<Venda> vendas) {
        for (Venda venda :
                vendas) {

            venda.sincroniza();

            if (existe(venda)) {
                if (venda.estaDesativado()) {
                    deleta(venda);
                } else {
                    altera(venda);
                }
            } else if (!venda.estaDesativado()) {
                insere(venda);
            }

        }
    }

    private boolean existe(Venda venda) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Vendas WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{venda.getId()});
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }

    public void close() {
        databaseHelper.close();
        database = null;
    }

    public List<Venda> listaNaoSincronizados() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Vendas WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);
        List<Venda> vendas = new ArrayList<Venda>();
        while (c.moveToNext()) {
            Venda venda = new Venda();
            venda.setId(c.getString(c.getColumnIndex("id")));
            venda.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            venda.setFormaDePagamento(c.getString(c.getColumnIndex("formaDePagamento")));
            venda.setDataDaVenda(c.getString(c.getColumnIndex("dataDaVenda")));
            venda.setNomeVendedor((c.getString(c.getColumnIndex("nomeVendedor"))));
            venda.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            venda.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            venda.setTotal(Double.valueOf(c.getString(c.getColumnIndex("total"))));
            venda.setTotalCartao(Double.valueOf(c.getString(c.getColumnIndex("totalCartao"))));
            venda.setLucro(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));
            VendaEntradaProdutoDAO vendaEntradaProdutoDAO = new VendaEntradaProdutoDAO(context);
            venda.setVendaEntradaProdutos(vendaEntradaProdutoDAO.procuraPorVenda(venda));
            vendaEntradaProdutoDAO.close();


            vendas.add(venda);

        }
        c.close();
        return vendas;

    }

    public void altera(Venda venda) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", venda.getId());
        dados.put("desativado", venda.getDesativado());
        dados.put("formaDePagamento", venda.getFormaDePagamento());
        dados.put("dataDaVenda", venda.getDataDaVenda());
        dados.put("nomeVendedor", venda.getNomeVendedor());
        dados.put("sincronizado", venda.getSincronizado());
        dados.put("id_loja", venda.getIdLoja());
        dados.put("total", venda.getTotal());
        dados.put("totalCartao", venda.getTotalCartao());
        dados.put("prejuizo", venda.getLucro());


        String[] params = {venda.getId().toString()};
        db.update("Vendas", dados, "id = ?", params);
    }


    public Venda procuraPorId(String nome) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Vendas WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{nome});
        Venda venda = null;
        while (c.moveToNext()) {
            venda = new Venda();
            venda.setId(c.getString(c.getColumnIndex("id")));
            venda.setDesativado(Integer.parseInt(c.getString(c.getColumnIndex("desativado"))));
            venda.setFormaDePagamento(c.getString(c.getColumnIndex("formaDePagamento")));
            venda.setDataDaVenda(c.getString(c.getColumnIndex("dataDaVenda")));
            venda.setNomeVendedor((c.getString(c.getColumnIndex("nomeVendedor"))));
            venda.setSincronizado(Integer.valueOf(c.getString(c.getColumnIndex("sincronizado"))));
            venda.setIdLoja(c.getString(c.getColumnIndex("id_loja")));
            venda.setTotal(Double.valueOf(c.getString(c.getColumnIndex("total"))));
            venda.setTotalCartao(Double.valueOf(c.getString(c.getColumnIndex("totalCartao"))));
            venda.setLucro(Double.valueOf(c.getString(c.getColumnIndex("prejuizo"))));
            VendaEntradaProdutoDAO vendaEntradaProdutoDAO = new VendaEntradaProdutoDAO(context);
            venda.setVendaEntradaProdutos(vendaEntradaProdutoDAO.procuraPorVenda(venda));
            vendaEntradaProdutoDAO.close();

        }
        c.close();
        return venda;

    }
}
