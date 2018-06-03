package pitstop.com.br.pitstop.dao;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.ItemFuro;
import pitstop.com.br.pitstop.model.ItemVenda;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.model.Venda;

/**
 * Created by wilso on 12/11/2017.
 */

public class VendaDAO {
    private Realm realm;
    Context context;

    public VendaDAO(Context context) {
        realm = Realm.getDefaultInstance();
        this.context = context;
    }


    public void insere(Venda venda) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(venda);
        realm.commitTransaction();
    }

    private void verificaSeRealmEstaFechado() {
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }


    public void deleta(Venda venda) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        Venda vendaRealm = realm.where(Venda.class)
                .equalTo("id", venda.getId())
                .findFirst();
        vendaRealm.getItemVendas().deleteAllFromRealm();
        vendaRealm.deleteFromRealm();
        realm.commitTransaction();
    }


    public List<Venda> relatorio(String de, String ate, String formaDePagamento, Loja loja, Usuario usuario) {
        Date dateOrigem = Util.converteDoFormatoSQLParaDate(de);
        Date dateFim = Util.converteDoFormatoSQLParaDate(ate);
        RealmQuery<Venda> consultaRelatorio = realm.where(Venda.class)
                .equalTo("desativado", 0)
                .between("dataDaVenda", dateOrigem, dateFim)
                .sort("dataDaVenda", Sort.DESCENDING);
        if (loja != null) {
            consultaRelatorio.equalTo("idLoja", loja.getId());
        }
        if (usuario != null) {
            consultaRelatorio.equalTo("nomeVendedor", usuario.getNome());
        }
        if (formaDePagamento != null) {
            consultaRelatorio.contains("formaDePagamento", formaDePagamento);
        }

        return realm.copyFromRealm(consultaRelatorio.findAll());
    }

    public List<Double> relatorioResumo(String de, String ate, String formaDePagamento, Loja loja, Usuario usuario) {
        verificaSeRealmEstaFechado();
        Date dateOrigem = Util.converteDoFormatoSQLParaDate(de);
        Date dateFim = Util.converteDoFormatoSQLParaDate(ate);
        RealmQuery<Venda> consultaRelatorioTotalDinheiro = realm.where(Venda.class)
                .equalTo("desativado", 0)
                .between("dataDaVenda", dateOrigem, dateFim);
        RealmQuery<Venda> consultaRelatorioTotalCartao = realm.where(Venda.class)
                .equalTo("desativado", 0)
                .between("dataDaVenda", dateOrigem, dateFim);
        RealmQuery<Venda> consultaRelatorioLucro = realm.where(Venda.class)
                .equalTo("desativado", 0)
                .between("dataDaVenda", dateOrigem, dateFim);
        if (loja != null) {
            consultaRelatorioTotalDinheiro.equalTo("idLoja", loja.getId());
            consultaRelatorioLucro.equalTo("idLoja", loja.getId());
            consultaRelatorioTotalCartao.equalTo("idLoja", loja.getId());
        }
        if (usuario != null) {
            consultaRelatorioTotalDinheiro.equalTo("nomeVendedor", usuario.getNome());
            consultaRelatorioLucro.equalTo("nomeVendedor", usuario.getNome());
            consultaRelatorioTotalCartao.equalTo("nomeVendedor", usuario.getNome());
        }
        if (formaDePagamento != null) {
            consultaRelatorioTotalDinheiro.contains("formaDePagamento", formaDePagamento);
            consultaRelatorioLucro.contains("formaDePagamento", formaDePagamento);
            consultaRelatorioTotalCartao.equalTo("formaDePagamento", formaDePagamento);
        }
        List<Double> totais = new ArrayList<>();
        totais.add(consultaRelatorioTotalDinheiro.sum("totalDinheiro").doubleValue() +
                consultaRelatorioTotalCartao.sum("totalCartao").doubleValue());

        totais.add(consultaRelatorioLucro.sum("lucro").doubleValue());
        return totais;
    }


    public double somaDoTotalPor(Loja loja, String dataInicial, String dataFinal) {
        verificaSeRealmEstaFechado();
        Date dateOrigem = Util.converteDoFormatoSQLParaDate(dataInicial);
        Date dateFim = Util.converteDoFormatoSQLParaDate(dataFinal);
        RealmQuery<Venda> consultaRelatorioTotalDinheiro = realm.where(Venda.class)
                .equalTo("desativado", 0)
                .between("dataDaVenda", dateOrigem, dateFim);

        RealmQuery<Venda> consultaRelatorioTotalCartao = realm.where(Venda.class)
                .equalTo("desativado", 0)
                .between("dataDaVenda", dateOrigem, dateFim);
        if (loja != null) {
            consultaRelatorioTotalDinheiro.equalTo("idLoja", loja.getId());
            consultaRelatorioTotalCartao.equalTo("idLoja", loja.getId());
        }
        return consultaRelatorioTotalDinheiro.sum("totalDinheiro").doubleValue()
                + consultaRelatorioTotalCartao.sum("totalCartao").doubleValue();
    }

    public double somaDoTotalPor(Usuario usuario, String dataInicial, String dataFinal) {
        verificaSeRealmEstaFechado();
        Date dateOrigem = Util.converteDoFormatoSQLParaDate(dataInicial);
        Date dateFim = Util.converteDoFormatoSQLParaDate(dataFinal);
        RealmQuery<Venda> consultaRelatorioTotalDinheiro = realm.where(Venda.class)
                .equalTo("desativado", 0)
                .between("dataDaVenda", dateOrigem, dateFim);

        RealmQuery<Venda> consultaRelatorioTotalCartao = realm.where(Venda.class)
                .equalTo("desativado", 0)
                .between("dataDaVenda", dateOrigem, dateFim);
        if (usuario != null) {
            consultaRelatorioTotalDinheiro.equalTo("nomeVendedor", usuario.getNome());
            consultaRelatorioTotalCartao.equalTo("nomeVendedor", usuario.getNome());
        }
        return consultaRelatorioTotalDinheiro.sum("totalDinheiro").doubleValue()
                + consultaRelatorioTotalCartao.sum("totalCartao").doubleValue();
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
        verificaSeRealmEstaFechado();
        Number n = realm.where(Venda.class).equalTo("id", venda.getId()).count();
        return (n.intValue() > 0);
    }

    public void close() {
        realm.close();
    }

    public List<Venda> listaNaoSincronizados() {
        verificaSeRealmEstaFechado();
        List<Venda> vendas = new ArrayList<>();
        vendas.addAll(realm.where(Venda.class)
                .equalTo("sincronizado", 0)
                .findAll());
        return realm.copyFromRealm(vendas);

    }

    public void altera(Venda venda) {
        verificaSeRealmEstaFechado();
        realm.beginTransaction();
        realm.insertOrUpdate(venda);
        realm.commitTransaction();
    }


    public Venda procuraPorId(String nome) {
        verificaSeRealmEstaFechado();
        Venda vendaRealm = realm.where(Venda.class)
                .equalTo("id", nome)
                .equalTo("desativado", 0)
                .findFirst();
        return realm.copyFromRealm(vendaRealm);

    }
}
