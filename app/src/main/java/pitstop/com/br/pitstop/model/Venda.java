package pitstop.com.br.pitstop.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilso on 10/10/2017.
 */

public class Venda {

    String id;
    String idLoja;
    List<VendaEntradaProduto> vendaEntradaProdutos;
    String nomeVendedor;

    String dataDaVenda;
    String formaDePagamento;
    String MomentoDaUltimaAtualizacao;
    int sincronizado;
    double total;
    double lucro;
    private int desativado=0;

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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
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


    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public String getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
    }

    public List<VendaEntradaProduto> getVendaEntradaProdutos() {
        return vendaEntradaProdutos;
    }

    public void setVendaEntradaProdutos(List<VendaEntradaProduto> vendaEntradaProdutos) {
        this.vendaEntradaProdutos = vendaEntradaProdutos;
    }

    public String getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(String formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }


    public Venda() {
        vendaEntradaProdutos = new ArrayList<>();
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

    public String getDataDaVenda() {
        return dataDaVenda;
    }

    public void setDataDaVenda(String dataDaVenda) {
        this.dataDaVenda = dataDaVenda;
    }
}
