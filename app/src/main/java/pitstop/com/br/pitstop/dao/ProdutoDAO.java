package pitstop.com.br.pitstop.dao;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;

/**
 * Created by wilso on 25/09/2017.
 */

public class ProdutoDAO {
    private Realm realm;
    Context context;

    public ProdutoDAO(Context context) {
        realm = Realm.getDefaultInstance();
    }

    private void verificaSeRealmEstaFechado() {
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    public void insere(Produto produto) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(produto);
        realm.commitTransaction();
    }

    public void insereLista(List<Produto> produtos) {
        verificaSeRealmEstaFechado();
        for (Produto produto : produtos) {
            insere(produto);

        }

    }

    public void deleta(Produto produto) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        Produto produtoRealm = realm.where(Produto.class)
                .equalTo("id", produto.getId())
                .findFirst();
        produtoRealm.getIdProdutoVinculado().deleteAllFromRealm();
        produtoRealm.deleteFromRealm();
        realm.commitTransaction();
    }

    public List<Produto> listarProdutos() {
        verificaSeRealmEstaFechado();
        return realm.copyFromRealm(realm.where(Produto.class)
                .findAll());
    }

    public boolean existeProdutosCadastrados() {
        verificaSeRealmEstaFechado();
        Number n = realm.where(Produto.class).count();
        return n.intValue() > 0;
    }


    public void sincroniza(List<Produto> produtos) {
        verificaSeRealmEstaFechado();
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
        verificaSeRealmEstaFechado();
        Number n = realm.where(Produto.class)
                .equalTo("id", produto.getId())
                .count();
        return n.intValue() > 0;
    }

    public void close() {
        realm.close();
    }

    public List<Produto> listaNaoSincronizados() {
        verificaSeRealmEstaFechado();
        List<Produto> produtos = new ArrayList<Produto>();
        produtos.addAll(realm.where(Produto.class)
                .equalTo("sincronizado", 0)
                .findAll());
        return realm.copyFromRealm(produtos);
    }


    public void altera(Produto produto) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(produto);
        realm.commitTransaction();

    }

    public List<Produto> procuraPorLoja(Loja loja) {
        verificaSeRealmEstaFechado();
        List<Produto> produtos = new ArrayList<Produto>();
        produtos.addAll(realm.where(Produto.class)
                .equalTo("loja.id", loja.getId())
                .sort("nome")
                .findAll());
        return realm.copyFromRealm(produtos);
    }

    public Produto procuraPorNomeELoja(String nome, Loja loja) {
        verificaSeRealmEstaFechado();
        Produto produto = realm.where(Produto.class)
                .equalTo("loja.id", loja.getId())
                .equalTo("nome", nome)
                .findFirst();
        return realm.copyFromRealm(produto);
    }

    public Produto procuraPorId(String id) {
        Produto produtoRealm = realm.where(Produto.class)
                .equalTo("id", id)
                .findFirst();
        return realm.copyFromRealm(produtoRealm);

    }

    public Produto procuraPorNome(String nome) {
        Produto produto = realm.where(Produto.class)
                .equalTo("nome", nome)
                .findFirst();
        return realm.copyFromRealm(produto);


    }
}
