package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.model.MovimentacaoProduto;

/**
 * Created by wilso on 14/11/2017.
 */

public class MovimentacaoProdutoDAO {
    Realm realm = Realm.getDefaultInstance();
    Context context;

    public MovimentacaoProdutoDAO(Context context) {
        this.context = context;
    }

    private void verificaSeRealmEstaFechado() {
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }


    public void insere(MovimentacaoProduto movimentacaoProduto) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        MovimentacaoProduto movimentacaoProdutoRealm;
        movimentacaoProdutoRealm = realm.createObject(MovimentacaoProduto.class, movimentacaoProduto.getId());
        pegarDados(movimentacaoProduto, movimentacaoProdutoRealm);
        realm.commitTransaction();
    }

    private void pegarDados(MovimentacaoProduto movimentacaoProduto, MovimentacaoProduto movimentacaoProdutoRealm) {
        movimentacaoProdutoRealm.setDesativado(movimentacaoProduto.getDesativado());
        movimentacaoProdutoRealm.setIdLojaDe(movimentacaoProduto.getIdLojaDe());
        movimentacaoProdutoRealm.setIdProduto(movimentacaoProduto.getIdProduto());
        movimentacaoProdutoRealm.setIdLojaPara(movimentacaoProduto.getIdLojaPara());
        movimentacaoProdutoRealm.setQuantidade(movimentacaoProduto.getQuantidade());
        movimentacaoProdutoRealm.setSincronizado(movimentacaoProduto.getSincronizado());
        movimentacaoProdutoRealm.setData(movimentacaoProduto.getData());
    }

    public void insereLista(List<MovimentacaoProduto> movimentacaoProdutos) {
        verificaSeRealmEstaFechado();
        for (MovimentacaoProduto movimentacaoProduto : movimentacaoProdutos) {
            insere(movimentacaoProduto);
        }


    }

    public void deleta(MovimentacaoProduto movimentacaoProduto) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        MovimentacaoProduto movimentacaoProdutoRealm = realm.where(MovimentacaoProduto.class)
                .equalTo("id", movimentacaoProduto.getId())
                .findFirst();
        movimentacaoProdutoRealm.deleteFromRealm();
        realm.commitTransaction();
    }

    public List<MovimentacaoProduto> listarMovimentacaoProduto() {
        verificaSeRealmEstaFechado();
        List<MovimentacaoProduto> movimentacaoProdutos = new ArrayList<>();
        movimentacaoProdutos.addAll(realm.where(MovimentacaoProduto.class).findAll());
        return realm.copyFromRealm(movimentacaoProdutos);
    }

    public List<MovimentacaoProduto> relatorio(String de, String ate) {
        verificaSeRealmEstaFechado();
        Date dateOrigem = Util.converteDoFormatoSQLParaDate(de);
        Date dateFim = Util.converteDoFormatoSQLParaDate(ate);
        List<MovimentacaoProduto> movimentacaoProdutos = new ArrayList<>();
        movimentacaoProdutos.addAll(realm.where(MovimentacaoProduto.class)
                .between("data", dateOrigem, dateFim)
                .equalTo("desativado", 0)
                .findAll());
        return realm.copyFromRealm(movimentacaoProdutos);
    }


    public void sincroniza(List<MovimentacaoProduto> movimentacaoProdutos) {
        for (MovimentacaoProduto movimentacaoProduto :
                movimentacaoProdutos) {

            movimentacaoProduto.sincroniza();

            if (existe(movimentacaoProduto)) {
                close();
                if (movimentacaoProduto.estaDesativado()) {
                    deleta(movimentacaoProduto);
                    close();
                } else {
                    altera(movimentacaoProduto);
                    close();
                }
            } else if (!movimentacaoProduto.estaDesativado()) {
                insere(movimentacaoProduto);
                close();
            }

        }
    }

    private boolean existe(MovimentacaoProduto movimentacaoProduto) {
        verificaSeRealmEstaFechado();
        Number n = realm.where(MovimentacaoProduto.class).equalTo("id", movimentacaoProduto.getId()).count();
        return (n.intValue() > 0);
    }

    public void close() {
        realm.close();
    }

    public List<MovimentacaoProduto> listaNaoSincronizados() {
        verificaSeRealmEstaFechado();
        List<MovimentacaoProduto> movimentacaoProdutos = new ArrayList<>();
        movimentacaoProdutos.addAll(realm.where(MovimentacaoProduto.class)
                .equalTo("sincronizado", 0)
                .findAll());
        return realm.copyFromRealm(movimentacaoProdutos);

    }

    public void altera(MovimentacaoProduto movimentacaoProduto) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        MovimentacaoProduto movimentacaoProdutoRealm = realm.where(MovimentacaoProduto.class)
                .equalTo("id", movimentacaoProduto.getId())
                .findFirst();
        pegarDados(movimentacaoProduto, movimentacaoProdutoRealm);
        realm.commitTransaction();
    }


    public MovimentacaoProduto procuraPorId(String id) {
        verificaSeRealmEstaFechado();
        MovimentacaoProduto movimentacaoProdutoRealm =  realm.where(MovimentacaoProduto.class)
                .equalTo("id", id)
                .equalTo("desativado", 0)
                .findFirst();
        return realm.copyFromRealm(movimentacaoProdutoRealm);

    }
}
