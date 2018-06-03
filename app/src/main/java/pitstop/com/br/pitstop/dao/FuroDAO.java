package pitstop.com.br.pitstop.dao;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.model.Furo;
import pitstop.com.br.pitstop.model.ItemAvaria;
import pitstop.com.br.pitstop.model.ItemFuro;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Usuario;

/**
 * Created by wilso on 15/12/2017.
 */

public class FuroDAO {
    private Realm realm;
    Context context;

    public FuroDAO(Context context) {
        realm = Realm.getDefaultInstance();
        this.context = context;
    }


    private void verificaSeRealmEstaFechado() {
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    public void insere(Furo furo) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(furo);
        realm.commitTransaction();

    }

    public void insereLista(List<Furo> furos) {
        verificaSeRealmEstaFechado();
        for (Furo furo : furos) {
            insere(furo);
        }
    }

    public void deleta(Furo furo) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        Furo furoRealm = realm.where(Furo.class)
                .equalTo("id", furo.getId())
                .findFirst();
        furoRealm.getFuroEntradeProdutos().deleteAllFromRealm();
        furoRealm.deleteFromRealm();
        realm.commitTransaction();
    }

    public List<Furo> listarfuros() {
        verificaSeRealmEstaFechado();
        List<Furo> furos = new ArrayList<>();
        furos.addAll(realm.where(Furo.class).findAll());
        return realm.copyFromRealm(furos);
    }


    public List<Furo> buscaPorLoja(String idLoja) {
        verificaSeRealmEstaFechado();
        List<Furo> furos = new ArrayList<>();
        furos.addAll(realm.where(Furo.class)
                .equalTo("idLoja", idLoja)
                .findAll());
        return realm.copyFromRealm(furos);
    }

    public double relatorioResumo(String de, String ate, Loja loja, Usuario usuario) {
        verificaSeRealmEstaFechado();
        Date dateOrigem = Util.converteDoFormatoSQLParaDate(de);
        Date dateFim = Util.converteDoFormatoSQLParaDate(ate);
        RealmQuery<Furo> consultaRelatorioPrejuizo = realm.where(Furo.class)
                .equalTo("desativado", 0)
                .between("data", dateOrigem, dateFim)
                .sort("data", Sort.DESCENDING);


        String funcionarioId = "%";
        String idLoja = "%";
        if (loja != null) {
            consultaRelatorioPrejuizo.equalTo("idLoja", loja.getId());
        }
        if (usuario != null) {

            consultaRelatorioPrejuizo.equalTo("idUsuario", usuario.getNome());
        }

        double prejuizo = consultaRelatorioPrejuizo.sum("valor").doubleValue();
        return prejuizo;
    }


    public List<Furo> relatorio(String de, String ate, String idloja, String funcionarioId) {
        verificaSeRealmEstaFechado();
        Date dateOrigem = Util.converteDoFormatoSQLParaDate(de);
        Date dateFim = Util.converteDoFormatoSQLParaDate(ate);
        RealmQuery<Furo> consultaRelatorio = realm.where(Furo.class)
                .equalTo("desativado", 0)
                .between("data", dateOrigem, dateFim)
                .sort("data", Sort.DESCENDING);

        if (!idloja.equals("%")) {
            consultaRelatorio.equalTo("idLoja", idloja);
        }
        if (!funcionarioId.equals("%")) {
            consultaRelatorio.equalTo("idUsuario", funcionarioId);
        }
        List<Furo> furos = new ArrayList<Furo>();
        furos.addAll(consultaRelatorio.findAll());
        return realm.copyFromRealm(furos);
    }


    public void sincroniza(List<Furo> furos) {
        for (Furo furo :
                furos) {

            furo.sincroniza();

            if (existe(furo)) {
                close();
                if (furo.estaDesativado()) {
                    deleta(furo);
                    close();
                } else {
                    altera(furo);
                    close();
                }
            } else if (!furo.estaDesativado()) {
                insere(furo);
                close();
            }

        }
    }

    private boolean existe(Furo furo) {
        verificaSeRealmEstaFechado();
        Number n = realm.where(Furo.class).equalTo("id", furo.getId()).count();
        return (n.intValue() > 0);
    }

    public void close() {
        realm.close();
    }

    public List<Furo> listaNaoSincronizados() {
        verificaSeRealmEstaFechado();
        List<Furo> furos = new ArrayList<>();
        furos.addAll(realm.where(Furo.class)
                .equalTo("sincronizado", 0)
                .findAll());
        return realm.copyFromRealm(furos);
    }

    public void altera(Furo furo) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(furo);
        realm.commitTransaction();
    }


    public Furo procuraPorId(String id) {
        verificaSeRealmEstaFechado();
        Furo furoRealm = realm.where(Furo.class)
                .equalTo("id", id)
                .equalTo("desativado", 0)
                .findFirst();
        return realm.copyFromRealm(furoRealm);

    }

}
