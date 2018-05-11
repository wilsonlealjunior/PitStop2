package pitstop.com.br.pitstop.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;


/**
 * Created by wilso on 20/10/2017.
 */

public class LojaDAO {
    Realm realm;

    public LojaDAO(Context context) {
        realm = Realm.getDefaultInstance();
    }


    public void sincroniza(List<Loja> Lojas) {
        for (Loja loja :
                Lojas) {
            loja.sincroniza();

            if (existe(loja)) {
                close();
                altera(loja);
                close();
            } else {
                insere(loja);
                close();
            }

        }
    }

    private void verificaSeRealmEstaFechado() {
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    private boolean existe(Loja loja) {
        verificaSeRealmEstaFechado();
        Number n = realm.where(Loja.class)
                .equalTo("id", loja.getId())
                .count();
        return n.intValue() > 0;
    }

    public Loja procuraPorId(String id) {
        verificaSeRealmEstaFechado();
        Loja lojaRealm = (realm.where(Loja.class)
                .equalTo("id", id)
                .findFirst());
        return realm.copyFromRealm(lojaRealm);

    }


    public void insere(Loja loja) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        Loja lojaRealm;
        if (loja.getId() == null) {
            lojaRealm = realm.createObject(Loja.class, UUID.randomUUID().toString());
        } else {
            lojaRealm = realm.createObject(Loja.class, loja.getId());
        }
        pegarDados(loja, lojaRealm);
        realm.commitTransaction();
    }

    private void pegarDados(Loja loja, Loja lojaRealm) {
        verificaSeRealmEstaFechado();
        loja.setId(loja.getId());
        lojaRealm.setNome(loja.getNome());
        lojaRealm.setEndereco(loja.getEndereco());
        lojaRealm.setSincronizado(loja.getSincronizado());
    }

    public void insereLista(List<Loja> Lojas) {
        verificaSeRealmEstaFechado();
        for (Loja loja : Lojas) {
            insere(loja);
        }

    }

    public List<Loja> listarLojas() {
        verificaSeRealmEstaFechado();
        List<Loja> lojas = new ArrayList<>();
        lojas.addAll(realm.where(Loja.class)
                .findAll()
                .sort("nome"));
        return realm.copyFromRealm(lojas);
    }

    public void deleta(Loja loja) {
        realm.beginTransaction();
        verificaSeRealmEstaFechado();
        Loja lojaRealm = realm.where(Loja.class)
                .equalTo("id",loja.getId())
                .findFirst();
        lojaRealm.deleteFromRealm();
        realm.commitTransaction();
    }

    public void close() {realm.close();
    }

    public List<Loja> listaNaoSincronizados() {
        verificaSeRealmEstaFechado();
        List<Loja> lojas = new ArrayList<>();

        lojas.addAll(realm.where(Loja.class)
                .equalTo("sincronizado",0)
                .findAll());
        return realm.copyFromRealm(lojas);
    }

    public void altera(Loja loja) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        Loja lojaRealm = realm.where(Loja.class)
                .equalTo("id", loja.getId())
                .findFirst();
        pegarDados(loja, lojaRealm);
        realm.commitTransaction();
    }

}
