package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;

/**
 * Created by wilso on 25/09/2017.
 */

public class ProdutoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    Context context;

    public ProdutoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }


    public void insere(Produto produto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (produto.getId() == null) {
            produto.setId(UUID.randomUUID().toString());
        }
        ContentValues dados = new ContentValues();
        dados.put("id", produto.getId());
        dados.put("nome", produto.getNome());
        dados.put("preco", produto.getPreco());
        dados.put("quantidade", produto.getQuantidade());
        dados.put("estoque_minimo", produto.getEstoqueMinimo());
        dados.put("loja_id", produto.getLoja().getId());
        dados.put("sincronizado", produto.getSincronizado());
        dados.put("id_ProdutoPrincipal", produto.getIdProdutoPrincipal());
        dados.put("vinculo", produto.getVinculo());
        ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
        for (String vinculo : produto.getIdProdutoVinculado()) {
            produtoVinculoDAO.insere(produto.getId(), vinculo);
            produtoVinculoDAO.close();

        }

        db.insert("Produtos", null, dados);
    }

    public void insereLista(List<Produto> produtos) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (Produto produto : produtos) {
            ContentValues dados = new ContentValues();
            dados.put("id", produto.getId());
            dados.put("nome", produto.getNome());
            dados.put("preco", produto.getPreco());
            dados.put("estoque_minimo", produto.getEstoqueMinimo());
            dados.put("quantidade", produto.getQuantidade());
            dados.put("loja_id", produto.getLoja().getId());
            dados.put("sincronizado", produto.getSincronizado());
            dados.put("id_ProdutoPrincipal", produto.getIdProdutoPrincipal());
            dados.put("vinculo", produto.getVinculo());
            ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
            for (String vinculo : produto.getIdProdutoVinculado()) {
                produtoVinculoDAO.insere(produto.getId(), vinculo);
                produtoVinculoDAO.close();

            }

            db.insert("Produtos", null, dados);

        }

    }

    public void deleta(Produto produto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
        for (String vinculo : produto.getIdProdutoVinculado()) {
            produtoVinculoDAO.deleta(vinculo);
            produtoVinculoDAO.close();

        }

        String[] params = {produto.getId().toString()};
        db.delete("Produtos", "id = ?", params);
    }

    public List<Produto> listarProdutos() {
        String sql = "SELECT * FROM Produtos order by nome;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Produto> produtos = new ArrayList<Produto>();
        while (c.moveToNext()) {
            Produto produto = new Produto();
            produto.setId(c.getString(c.getColumnIndex("id")));
            produto.setNome(c.getString(c.getColumnIndex("nome")));
            produto.setPreco(Double.parseDouble(c.getString(c.getColumnIndex("preco"))));

            produto.setEstoqueMinimo(Integer.parseInt(c.getString(c.getColumnIndex("estoque_minimo"))));
            produto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            produto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            produto.setVinculo(Integer.parseInt(c.getString(c.getColumnIndex("vinculo"))));
            produto.setIdProdutoPrincipal(c.getString(c.getColumnIndex("id_ProdutoPrincipal")));
            LojaDAO lojaDAO = new LojaDAO(context);
            Loja l = new Loja();
            l = lojaDAO.procuraPorId(c.getString(c.getColumnIndex("loja_id")));
            //Log.e("TESTE-->>",l.getNome());
            produto.setLoja(l);
            lojaDAO.close();
            ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
            List<String> vinculos = produtoVinculoDAO.procuraPorProduto(produto.getId());
            produtoVinculoDAO.close();
            produto.setIdProdutoVinculado(vinculos);

            produtos.add(produto);

        }
        c.close();
        return produtos;
    }

    public boolean existeProdutosCadastrados(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT count(id) FROM Produtos LIMIT 1";
        Cursor cursor = db.rawQuery(existe, null);
        int quantidade = cursor.getCount();
        return quantidade > 0;
    }

    //nomes e precos iguais
    public List<Produto> listarProdutosdpadrao() {
        String sql = "SELECT nome, MAX(preco) as preco FROM Produtos group by nome;";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Produto> produtos = new ArrayList<Produto>();
        while (c.moveToNext()) {
            Produto produto = new Produto();
            produto.setNome(c.getString(c.getColumnIndex("nome")));
            produto.setPreco(Double.parseDouble(c.getString(c.getColumnIndex("preco"))));

            produtos.add(produto);
        }
        c.close();
        return produtos;
    }


    public void sincroniza(List<Produto> produtos) {
        for (Produto produto :
                produtos) {
//            Log.i("log3", String.valueOf(produto.getQuantidade()));
            produto.sincroniza();

            if (existe(produto)) {
                close();
                altera(produto);
                close();
            } else {
                insere(produto);
                close();
            }

        }
    }

    private boolean existe(Produto produto) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT id FROM Produtos WHERE id=? LIMIT 1";
        Cursor cursor = db.rawQuery(existe, new String[]{produto.getId()});
        int quantidade = cursor.getCount();
        cursor.close();
        return quantidade > 0;
    }

    public void close() {
        databaseHelper.close();
        database = null;
    }

    public List<Produto> listaNaoSincronizados() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Produtos WHERE sincronizado = 0";
        Cursor c = db.rawQuery(sql, null);
        List<Produto> produtos = new ArrayList<Produto>();
        while (c.moveToNext()) {
            Produto produto = new Produto();
            produto.setId(c.getString(c.getColumnIndex("id")));
            produto.setNome(c.getString(c.getColumnIndex("nome")));
            produto.setPreco(Double.parseDouble(c.getString(c.getColumnIndex("preco"))));
            produto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            produto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            produto.setEstoqueMinimo(Integer.parseInt(c.getString(c.getColumnIndex("estoque_minimo"))));
            produto.setVinculo(Integer.parseInt(c.getString(c.getColumnIndex("vinculo"))));
            produto.setIdProdutoPrincipal(c.getString(c.getColumnIndex("id_ProdutoPrincipal")));
            LojaDAO lojaDAO = new LojaDAO(context);
            Loja l = new Loja();
            l = lojaDAO.procuraPorId(c.getString(c.getColumnIndex("loja_id")));
            //Log.e("TESTE-->>",l.getNome());
            produto.setLoja(l);
            lojaDAO.close();

            ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
            List<String> vinculos = produtoVinculoDAO.procuraPorProduto(produto.getId());
            produtoVinculoDAO.close();
            produto.setIdProdutoVinculado(vinculos);


            produtos.add(produto);
        }
        c.close();
        return produtos;

    }

    public List<Produto> listaProdutosPorParteDoNome(String nome) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM Produtos WHERE nome like '%"+nome+"%'";
        Cursor c = db.rawQuery(sql, null);
        List<Produto> produtos = new ArrayList<Produto>();
        while (c.moveToNext()) {
            Produto produto = new Produto();
            produto.setId(c.getString(c.getColumnIndex("id")));
            produto.setNome(c.getString(c.getColumnIndex("nome")));
            produto.setPreco(Double.parseDouble(c.getString(c.getColumnIndex("preco"))));
            produto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            produto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            produto.setEstoqueMinimo(Integer.parseInt(c.getString(c.getColumnIndex("estoque_minimo"))));
            produto.setVinculo(Integer.parseInt(c.getString(c.getColumnIndex("vinculo"))));
            produto.setIdProdutoPrincipal(c.getString(c.getColumnIndex("id_ProdutoPrincipal")));
            LojaDAO lojaDAO = new LojaDAO(context);
            Loja l = new Loja();
            l = lojaDAO.procuraPorId(c.getString(c.getColumnIndex("loja_id")));
            //Log.e("TESTE-->>",l.getNome());
            produto.setLoja(l);
            lojaDAO.close();

            ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
            List<String> vinculos = produtoVinculoDAO.procuraPorProduto(produto.getId());
            produtoVinculoDAO.close();
            produto.setIdProdutoVinculado(vinculos);


            produtos.add(produto);
        }
        c.close();
        return produtos;

    }

    public void altera(Produto produto) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues dados = new ContentValues();
        dados.put("id", produto.getId());
        dados.put("nome", produto.getNome());
        dados.put("preco", produto.getPreco());
        dados.put("quantidade", produto.getQuantidade());
        dados.put("estoque_minimo", produto.getEstoqueMinimo());
        dados.put("loja_id", produto.getLoja().getId());
        dados.put("sincronizado", produto.getSincronizado());
        dados.put("id_ProdutoPrincipal", produto.getIdProdutoPrincipal());
        dados.put("vinculo", produto.getVinculo());
        Log.e("log2", String.valueOf(produto.getQuantidade()));
        ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
        produtoVinculoDAO.sincroniza(produto.getId(), produto.getIdProdutoVinculado());
        produtoVinculoDAO.close();


        String[] params = {produto.getId().toString()};
        db.update("Produtos", dados, "id = ?", params);
    }

    public List<Produto> procuraPorLoja(Loja loja) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Produtos WHERE loja_id=? order by nome";
        Cursor c = db.rawQuery(existe, new String[]{loja.getId()});
        List<Produto> produtos = new ArrayList<Produto>();
        while (c.moveToNext()) {
            Produto produto = new Produto();
            produto.setId(c.getString(c.getColumnIndex("id")));
            produto.setNome(c.getString(c.getColumnIndex("nome")));
            produto.setPreco(Double.parseDouble(c.getString(c.getColumnIndex("preco"))));
            produto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            produto.setEstoqueMinimo(Integer.parseInt(c.getString(c.getColumnIndex("estoque_minimo"))));
            produto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            produto.setVinculo(Integer.parseInt(c.getString(c.getColumnIndex("vinculo"))));
            produto.setIdProdutoPrincipal(c.getString(c.getColumnIndex("id_ProdutoPrincipal")));
            produto.setLoja(loja);

            ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
            List<String> vinculos = produtoVinculoDAO.procuraPorProduto(produto.getId());
            produtoVinculoDAO.close();
            produto.setIdProdutoVinculado(vinculos);

            produtos.add(produto);

        }
        c.close();
        return produtos;

    }

    public Produto procuraPorNomeELoja(String nome, Loja loja) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Produtos WHERE nome=? and loja_id=?";
        Cursor c = db.rawQuery(existe, new String[]{nome, loja.getId()});
        Produto produto = null;
        while (c.moveToNext()) {
            produto = new Produto();
            produto.setId(c.getString(c.getColumnIndex("id")));
            produto.setNome(c.getString(c.getColumnIndex("nome")));
            produto.setPreco(Double.parseDouble(c.getString(c.getColumnIndex("preco"))));
            produto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            produto.setEstoqueMinimo(Integer.parseInt(c.getString(c.getColumnIndex("estoque_minimo"))));
            produto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            produto.setVinculo(Integer.parseInt(c.getString(c.getColumnIndex("vinculo"))));
            produto.setIdProdutoPrincipal(c.getString(c.getColumnIndex("id_ProdutoPrincipal")));
            produto.setLoja(loja);

            ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
            List<String> vinculos = produtoVinculoDAO.procuraPorProduto(produto.getId());
            produtoVinculoDAO.close();
            produto.setIdProdutoVinculado(vinculos);

        }
        c.close();
        return produto;

    }

    public Produto procuraPorId(String id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Produtos WHERE id=?";
        Cursor c = db.rawQuery(existe, new String[]{id});
        Produto produto = null;
        while (c.moveToNext()) {
            produto = new Produto();
            produto.setId(c.getString(c.getColumnIndex("id")));
            produto.setNome(c.getString(c.getColumnIndex("nome")));
            produto.setPreco(Double.parseDouble(c.getString(c.getColumnIndex("preco"))));
            produto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            produto.setEstoqueMinimo(Integer.parseInt(c.getString(c.getColumnIndex("estoque_minimo"))));
            produto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            produto.setVinculo(Integer.parseInt(c.getString(c.getColumnIndex("vinculo"))));
            produto.setIdProdutoPrincipal(c.getString(c.getColumnIndex("id_ProdutoPrincipal")));

            LojaDAO lojaDAO = new LojaDAO(context);
            Loja l = new Loja();
            l = lojaDAO.procuraPorId(c.getString(c.getColumnIndex("loja_id")));
            //Log.e("TESTE-->>",l.getNome());
            produto.setLoja(l);
            lojaDAO.close();

            ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
            List<String> vinculos = produtoVinculoDAO.procuraPorProduto(produto.getId());
            produtoVinculoDAO.close();
            produto.setIdProdutoVinculado(vinculos);

        }
        c.close();
        return produto;

    }

    public Produto procuraPorNome(String nome) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String existe = "SELECT * FROM Produtos WHERE nome=?";
        Cursor c = db.rawQuery(existe, new String[]{nome});
        Produto produto = null;
        while (c.moveToNext()) {
            produto = new Produto();
            produto.setId(c.getString(c.getColumnIndex("id")));
            produto.setNome(c.getString(c.getColumnIndex("nome")));
            produto.setPreco(Double.parseDouble(c.getString(c.getColumnIndex("preco"))));
            produto.setQuantidade(Integer.parseInt(c.getString(c.getColumnIndex("quantidade"))));
            produto.setEstoqueMinimo(Integer.parseInt(c.getString(c.getColumnIndex("estoque_minimo"))));
            produto.setSincronizado(Integer.parseInt(c.getString(c.getColumnIndex("sincronizado"))));
            produto.setVinculo(Integer.parseInt(c.getString(c.getColumnIndex("vinculo"))));
            produto.setIdProdutoPrincipal(c.getString(c.getColumnIndex("id_ProdutoPrincipal")));
            LojaDAO lojaDAO = new LojaDAO(context);
            Loja l = new Loja();
            l = lojaDAO.procuraPorId(c.getString(c.getColumnIndex("loja_id")));
            //Log.e("TESTE-->>",l.getNome());
            produto.setLoja(l);
            lojaDAO.close();

            ProdutoVinculoDAO produtoVinculoDAO = new ProdutoVinculoDAO(context);
            List<String> vinculos = produtoVinculoDAO.procuraPorProduto(produto.getId());
            produtoVinculoDAO.close();
            produto.setIdProdutoVinculado(vinculos);

        }
        c.close();
        return produto;

    }
}
