package pitstop.com.br.pitstop.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilso on 27/11/2017.
 */

public class Avaria {
    String id;
    List<ItemAvaria> avariaEntradeProdutos;
    String idLoja;
    String idProduto;
    int quantidade;
    int sincronizado;
    String data;
    String momentoDaUltimaAtualizacao;
    double prejuizo;
    private int desativado=0;

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void desativar(){
        desativado=1;
    }

    public int getDesativado() {
        return desativado;
    }

    public void setDesativado(int desativado) {
        this.desativado = desativado;
    }


    public Avaria(){
        avariaEntradeProdutos = new ArrayList<>();

    }

    public boolean estaDesativado() {
        return desativado == 1;
    }

    public double getPrejuizo() {
        return prejuizo;
    }

    public void setPrejuizo(double prejuizo) {
        this.prejuizo = prejuizo;
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


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ItemAvaria>  getAvariaEntradeProdutos() {
        return avariaEntradeProdutos;
    }

    public void setAvariaEntradeProdutos(List<ItemAvaria>  avariaEntradeProdutos) {
        this.avariaEntradeProdutos = avariaEntradeProdutos;
    }

    public String getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }
}
