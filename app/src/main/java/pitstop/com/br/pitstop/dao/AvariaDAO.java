package pitstop.com.br.pitstop.dao;

import android.content.ClipData;
import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.ItemAvaria;
import pitstop.com.br.pitstop.model.Loja;


/**
 * Created by wilso on 27/11/2017.
 */

public class AvariaDAO {
    private Realm realm = Realm.getDefaultInstance();
    Context context;

    public AvariaDAO(Context context) {
        this.context = context;
    }

    public void insere(Avaria avaria) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(avaria);
        realm.commitTransaction();
    }

    private void verificaSeRealmEstaFechado() {
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }



    public void deleta(Avaria avaria) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        Avaria avariaRealm = realm.where(Avaria.class)
                .equalTo("id", avaria.getId())
                .findFirst();
        avariaRealm.getAvariaEntradeProdutos().deleteAllFromRealm();
        avariaRealm.deleteFromRealm();
        realm.commitTransaction();

    }

    public double relatorioResumo(Loja lojaEscolhida, String de, String ate) {
        verificaSeRealmEstaFechado();
        Date dateOrigem = Util.converteDoFormatoSQLParaDate(de);
        Date dateFim = Util.converteDoFormatoSQLParaDate(ate);
        if (lojaEscolhida != null) {
            Number valor = realm.where(Avaria.class)
                    .equalTo("idLoja", lojaEscolhida.getId())
                    .equalTo("desativado", 0)
                    .between("data", dateOrigem, dateFim)
                    .sum("prejuizo");
            return valor.doubleValue();
        } else {
            Number valor = realm.where(Avaria.class)
                    .between("data", dateOrigem, dateFim)
                    .equalTo("desativado", 0)
                    .sum("prejuizo");
            return valor.doubleValue();
        }

    }

    public List<Avaria> relatorio(String lojaEscolhidaId, String de, String ate) {
        verificaSeRealmEstaFechado();
        Date dateOrigem = Util.converteDoFormatoSQLParaDate(de);
        Date dateFim = Util.converteDoFormatoSQLParaDate(ate);
        List<Avaria> avarias = new ArrayList<>();
        if (lojaEscolhidaId.equals("%")) {
            avarias.addAll(realm.where(Avaria.class)
                    .between("data", dateOrigem, dateFim)
                    .equalTo("desativado", 0)
                    .findAll());
            return realm.copyFromRealm(avarias);
        } else {
            avarias.addAll(realm.where(Avaria.class)
                    .equalTo("idLoja", lojaEscolhidaId)
                    .equalTo("desativado", 0)
                    .between("data", dateOrigem, dateFim)
                    .findAll());
            return realm.copyFromRealm(avarias);
        }

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
        verificaSeRealmEstaFechado();
        Number n = realm.where(Avaria.class).equalTo("id", avaria.getId()).count();
        return (n.intValue() > 0);
    }

    public void close() {
        realm.close();
    }

    public List<Avaria> listaNaoSincronizados() {
        verificaSeRealmEstaFechado();
        List<Avaria> avarias = new ArrayList<>();
        avarias.addAll(realm.where(Avaria.class)
                .equalTo("sincronizado", 0)
                .findAll());
        return realm.copyFromRealm(avarias);
    }

    public void altera(Avaria avaria) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(avaria);
        realm.commitTransaction();
    }


    public Avaria procuraPorId(String id) {
        verificaSeRealmEstaFechado();
        Avaria avariaRealm = realm.where(Avaria.class)
                .equalTo("id", id)
                .equalTo("desativado", 0)
                .findFirst();

        return realm.copyFromRealm(avariaRealm);

    }


}
