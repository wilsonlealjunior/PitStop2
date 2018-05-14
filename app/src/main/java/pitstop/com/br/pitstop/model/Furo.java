package pitstop.com.br.pitstop.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wilso on 15/12/2017.
 */

public class Furo extends RealmObject {
    @PrimaryKey
    String id;
    RealmList<ItemFuro> furoEntradeProdutos = new RealmList<>();
    String idLoja;
    String idUsuario;
    String idProduto;
    int sincronizado;
    Date data;
    double valor;
    double precoDeVenda;
    int quantidade;
    private int desativado=0;

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
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
    public boolean estaDesativado() {
        return desativado == 1;
    }


    public Furo(){

    }

    public double getPrecoDeVenda() {
        return precoDeVenda;
    }

    public void setPrecoDeVenda(double precoDeVenda) {
        this.precoDeVenda = precoDeVenda;
    }

    public void sincroniza() {
        this.sincronizado = 1;
    }
    public void desincroniza() {
        this.sincronizado = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<ItemFuro> getFuroEntradeProdutos() {
        return furoEntradeProdutos;
    }

    public void setFuroEntradeProdutos(RealmList<ItemFuro> furoEntradeProdutos) {
        this.furoEntradeProdutos = furoEntradeProdutos;
    }



    public String getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }


    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
