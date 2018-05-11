package pitstop.com.br.pitstop.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wilso on 10/10/2017.
 */

public class Venda extends RealmObject{
    @PrimaryKey
    String id;
    String idLoja;
    RealmList<ItemVenda> itemVendas = new RealmList<>();
    String nomeVendedor;
    Date dataDaVenda;
    String formaDePagamento;
    String MomentoDaUltimaAtualizacao;
    int sincronizado;
    double totalDinheiro;
    double totalCartao;
    double lucro;
    private int desativado=0;


    public double getTotalCartao() {
        return totalCartao;
    }

    public void setTotalCartao(double totalCartao) {
        this.totalCartao = totalCartao;
    }

    public void desativar(){
        desativado=1;
    }

    public int getDesativado() {
        return desativado;
    }
    public boolean estaDesativado() {
        return desativado == 1;
    }

    public void setDesativado(int desativado) {
        this.desativado = desativado;
    }

    public double getTotalDinheiro() {
        return totalDinheiro;
    }

    public void setTotalDinheiro(double totalDinheiro) {
        this.totalDinheiro = totalDinheiro;
    }

    public double getLucro() {
        return lucro;
    }

    public void setLucro(double lucro) {
        this.lucro = lucro;
    }

    public void sincroniza() {
        this.sincronizado = 1;
    }

    public void desincroniza() {
        this.sincronizado = 0;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public String getMomentoDaUltimaAtualizacao() {
        return MomentoDaUltimaAtualizacao;
    }

    public void setMomentoDaUltimaAtualizacao(String momentoDaUltimaAtualizacao) {
        MomentoDaUltimaAtualizacao = momentoDaUltimaAtualizacao;
    }

    public void setItemVendas(RealmList<ItemVenda> itemVendas) {
        this.itemVendas = itemVendas;

    }

    public RealmList<ItemVenda> getItemVendas() {
        return itemVendas;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public String getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
    }


    public String getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(String formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }


    public Venda(){
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getNomeVendedor() {
        return nomeVendedor;
    }

    public void setNomeVendedor(String nomeVendedor) {
        this.nomeVendedor = nomeVendedor;
    }

    public Date getDataDaVenda() {
        return dataDaVenda;
    }

    public void setDataDaVenda(Date dataDaVenda) {
        this.dataDaVenda = dataDaVenda;
    }
}
