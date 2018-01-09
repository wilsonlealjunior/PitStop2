package pitstop.com.br.pitstop.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wilso on 19/10/2017.
 */

public class EntradaProduto implements Serializable {

    private String id;
    private double precoDeCompra;
    private int quantidade;
    private String data;
    private Produto produto;
    private int sincronizado;
    private String momentoDaUltimaAtualizacao;
    private int quantidadeVendidaMovimentada;
    private int desativado=0;

    public void desativar(){
        desativado=1;
    }

    public int getDesativado() {
        return desativado;
    }

    public void setDesativado(int desativado) {
        this.desativado = desativado;
    }
    public boolean estaDesativado() {
        return desativado == 1;
    }


    public int getQuantidadeVendidaMovimentada() {
        return quantidadeVendidaMovimentada;
    }

    public void setQuantidadeVendidaMovimentada(int quantidadeVendidaMovimentada) {
        this.quantidadeVendidaMovimentada = quantidadeVendidaMovimentada;
    }

    public String getMomentoDaUltimaAtualizacao() {
        return momentoDaUltimaAtualizacao;
    }

    public void setMomentoDaUltimaAtualizacao(String momentoDaUltimaAtualizacao) {
        this.momentoDaUltimaAtualizacao = momentoDaUltimaAtualizacao;
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

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrecoDeCompra() {
        return precoDeCompra;
    }

    public void setPrecoDeCompra(double precoDeCompra) {
        this.precoDeCompra = precoDeCompra;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
