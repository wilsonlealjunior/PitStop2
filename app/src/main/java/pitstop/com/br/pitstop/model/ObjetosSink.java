package pitstop.com.br.pitstop.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilso on 28/11/2017.
 */

public class ObjetosSink {
    List<Avaria> avarias;
    List<EntradaProduto> entradaProdutos;
    List<Loja> lojas;
    List<MovimentacaoProduto> movimentacaoProdutos;
    List<Produto> produtos;
    List<Venda> vendas;
    List<Usuario> usuarios;
    List<Furo> furos;
    String momentoDaUltimaAtualizacao;

    public ObjetosSink(){
        usuarios = new ArrayList<>();
        avarias = new ArrayList<>();
        entradaProdutos = new ArrayList<>();
        lojas = new ArrayList<>();
        movimentacaoProdutos = new ArrayList<>();
        produtos = new ArrayList<>();
        vendas = new ArrayList<>();
        furos = new ArrayList<>();

    }

    public String getMomentoDaUltimaAtualizacao() {
        return momentoDaUltimaAtualizacao;
    }

    public void setMomentoDaUltimaAtualizacao(String momentoDaUltimaAtualizacao) {
        this.momentoDaUltimaAtualizacao = momentoDaUltimaAtualizacao;
    }

    public List<Furo> getFuros() {
        return furos;
    }

    public void setFuros(List<Furo> furos) {
        this.furos = furos;
    }

    public List<Avaria> getAvarias() {
        return avarias;
    }

    public void setAvarias(List<Avaria> avarias) {
        this.avarias = avarias;
    }

    public List<EntradaProduto> getEntradaProdutos() {
        return entradaProdutos;
    }

    public void setEntradaProdutos(List<EntradaProduto> entradaProdutos) {
        this.entradaProdutos = entradaProdutos;
    }

    public List<Loja> getLojas() {
        return lojas;
    }

    public void setLojas(List<Loja> lojas) {
        this.lojas = lojas;
    }

    public List<MovimentacaoProduto> getMovimentacaoProdutos() {
        return movimentacaoProdutos;
    }

    public void setMovimentacaoProdutos(List<MovimentacaoProduto> movimentacaoProdutos) {
        this.movimentacaoProdutos = movimentacaoProdutos;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public List<Venda> getVendas() {
        return vendas;
    }

    public void setVendas(List<Venda> vendas) {
        this.vendas = vendas;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
