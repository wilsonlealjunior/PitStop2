package pitstop.com.br.pitstop.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilso on 20/09/2017.
 */

public class Produto implements Serializable, Comparable<Produto> {
    private String id;
    private String nome;
    private int estoqueMinimo;
    private int quantidade = 0;
    private double preco;
    private Loja loja;
    private List<EntradaProduto> entradaProdutos;
    private int sincronizado;
    private String momentoDaUltimaAtualizacao;
    private List<String> idProdutoVinculado;
    private String idProdutoPrincipal;
    private int vinculo;


    public String getIdProdutoPrincipal() {
        return idProdutoPrincipal;
    }

    public void setIdProdutoPrincipal(String idProdutoPrincipal) {
        this.idProdutoPrincipal = idProdutoPrincipal;
    }

    public String getMomentoDaUltimaAtualizacao() {
        return momentoDaUltimaAtualizacao;
    }

    public void setMomentoDaUltimaAtualizacao(String momentoDaUltimaAtualizacao) {
        this.momentoDaUltimaAtualizacao = momentoDaUltimaAtualizacao;
    }

    public List<String> getIdProdutoVinculado() {
        return idProdutoVinculado;
    }

    public void setIdProdutoVinculado(List<String> idProdutoVinculado) {
        this.idProdutoVinculado = idProdutoVinculado;
    }

    public int getVinculo() {
        return vinculo;
    }

    public void setVinculo(int vinculo) {
        this.vinculo = vinculo;
    }
    public boolean vinculado(){
        return vinculo ==1;
    }
    public void vincular(String idProdutoPrincipal){
        this.idProdutoPrincipal = idProdutoPrincipal;
        vinculo=1;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public Produto() {
        this.idProdutoVinculado = new ArrayList<>();
        this.entradaProdutos = new ArrayList<EntradaProduto>();

    }


    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Produto(String id, String nome, double preco, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        this.entradaProdutos = new ArrayList<EntradaProduto>();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Loja getLoja() {
        return loja;
    }

    public void setLoja(Loja loja) {
        this.loja = loja;
    }

    public List<EntradaProduto> getEntradaProdutos() {
        return entradaProdutos;
    }

    public void setEntradaProdutos(List<EntradaProduto> entradaProdutos) {
        this.entradaProdutos = entradaProdutos;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        //calcularQuantidade();
        return quantidade;
    }
//
//    public void calcularQuantidade() {
//        if ((quantidade == 0) && (entradaProdutos.size()==0)) {
//            quantidade = 0;
//            if (entradaProdutos != null) {
//                for (EntradaProduto l : entradaProdutos) {
//                    quantidade += (l.getQuantidade() - l.getQuantidadeVendidaMovimentada());
//
//                }
//            }
//        }
//
//    }

    public void calcularQuantidade() {
        quantidade = 0;
        if (entradaProdutos != null) {
            for (EntradaProduto l : entradaProdutos) {
                quantidade += (l.getQuantidade() - l.getQuantidadeVendidaMovimentada());

            }
        }
    }

    public void entrada(int entrada) {
        //calcularQuantidade();
        quantidade += entrada;

    }


    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Produto: " + nome + "\nPreco: " +
                preco + "\nQuantidade: " + quantidade;
    }

    public void sincroniza() {
        this.sincronizado = 1;
    }

    public void desincroniza() {
        this.sincronizado = 0;
    }

    @Override
    public int compareTo(@NonNull Produto produto) {
        return nome.compareTo(produto.getNome());

    }
}
