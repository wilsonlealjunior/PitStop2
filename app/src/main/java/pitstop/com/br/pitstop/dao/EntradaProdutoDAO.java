package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.ItemAvaria;
import pitstop.com.br.pitstop.model.Produto;

/**
 * Created by wilso on 29/10/2017.
 */

public class EntradaProdutoDAO {
    private Realm realm;
    public Context context;

    private void verificaSeRealmEstaFechado() {
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    public EntradaProdutoDAO(Context context) {
        realm = Realm.getDefaultInstance();
        this.context = context;
    }

    public void deleta(EntradaProduto entradaProduto) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        EntradaProduto entradaProdutoRealm = realm.where(EntradaProduto.class)
                .equalTo("id", entradaProduto.getId())
                .findFirst();
        if (entradaProdutoRealm == null)
            return;
        entradaProdutoRealm.deleteFromRealm();
        realm.commitTransaction();

    }

    public void sincroniza(List<EntradaProduto> entradaProdutos) {
        for (EntradaProduto entradaProduto :
                entradaProdutos) {
            entradaProduto.sincroniza();

//            Log.e("EntradaProduto-1", entradaProduto.getId());
            //Log.e("EntradaProduto-1 - sinc", String.valueOf(entradaProduto.getSincronizado()));
            if (existe(entradaProduto)) {
                close();
                if (entradaProduto.estaDesativado()) {
                    deleta(entradaProduto);
                    close();
                } else {
                    altera(entradaProduto);
                    close();
                }
            } else if (!entradaProduto.estaDesativado()) {
                insere(entradaProduto);
                close();
            }

        }
    }

    private boolean existe(EntradaProduto entradaProduto) {
        verificaSeRealmEstaFechado();
        Number n = realm.where(EntradaProduto.class).equalTo("id", entradaProduto.getId()).count();
        return (n.intValue() > 0);
    }




    public void altera(EntradaProduto entradaProduto) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(entradaProduto);
        realm.commitTransaction();
    }

    public void insere(EntradaProduto entradaProduto) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(entradaProduto);
        realm.commitTransaction();
    }


    public List<EntradaProduto> listaNaoSincronizados() {
        verificaSeRealmEstaFechado();
        List<EntradaProduto> entradaProdutos = new ArrayList<>();
        entradaProdutos.addAll(realm.where(EntradaProduto.class)
                .equalTo("sincronizado", 0)
                .findAll());
        return realm.copyFromRealm(entradaProdutos);
    }

    public List<EntradaProduto> listarEntradaProduto() {
        verificaSeRealmEstaFechado();
        List<EntradaProduto> entradaProdutos = new ArrayList<>();
        entradaProdutos.addAll(realm.where(EntradaProduto.class)
                .equalTo("sincronizado", 0)
                .equalTo("desativado", 0)
                .findAll());
        return realm.copyFromRealm(entradaProdutos);


    }

    public List<EntradaProduto> relatorio(String lojaEscolhidaId, String de, String ate) {
        verificaSeRealmEstaFechado();
        Date dateOrigem = Util.converteDoFormatoSQLParaDate(de);
        Date dateFim = Util.converteDoFormatoSQLParaDate(ate);
        List<EntradaProduto> EntradaProduto = new ArrayList<>();
        if (lojaEscolhidaId.equals("%")) {
            EntradaProduto.addAll(realm.where(EntradaProduto.class)
                    .between("data", dateOrigem, dateFim)
                    .equalTo("desativado", 0)
                    .findAll());
            return realm.copyFromRealm(EntradaProduto);
        } else {
            EntradaProduto.addAll(realm.where(EntradaProduto.class)
                    .between("data", dateOrigem, dateFim)
                    .equalTo("produto.loja.id", lojaEscolhidaId)
                    .equalTo("desativado", 0)
                    .findAll());
            return realm.copyFromRealm(EntradaProduto);

        }

    }

    public void close() {
        realm.close();
    }

    public List<EntradaProduto> procuraTodosDeUmProduto(Produto produto) {
        verificaSeRealmEstaFechado();
        List<EntradaProduto> entradaProdutos = new ArrayList<>();
        entradaProdutos.addAll(realm.where(EntradaProduto.class)
                .equalTo("sincronizado", 0)
                .equalTo("desativado", 0)
                .equalTo("produto.id", produto.getId())
                .notEqualTo("quantidade-quantidadeVendidaMovimentada", 0)
                .findAll());
        return realm.copyFromRealm(entradaProdutos);
    }

    public EntradaProduto procuraPorId(String id) {
        verificaSeRealmEstaFechado();
        EntradaProduto entradaProdutoRealm = realm.where(EntradaProduto.class)
                .equalTo("id", id)
                .equalTo("desativado", 0)
                .findFirst();
        if (entradaProdutoRealm == null)
            return null;
        return realm.copyFromRealm(entradaProdutoRealm);

    }

}
