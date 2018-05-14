package pitstop.com.br.pitstop.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import pitstop.com.br.pitstop.Util;

/**
 * Created by wilso on 27/11/2017.
 */

public class Avaria extends RealmObject {
    @PrimaryKey
    String id;
    RealmList<ItemAvaria> avariaEntradeProdutos = new RealmList<>();
    String idLoja;
    String idProduto;
    int quantidade;
    int sincronizado;
    private Date data;
    double prejuizo;
    private int desativado = 0;

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

    public void desativar() {
        desativado = 1;
    }



    public int getDesativado() {
        return desativado;
    }

    public void setDesativado(int desativado) {
        this.desativado = desativado;
    }


    public Avaria() {
        avariaEntradeProdutos = new RealmList<>();

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


    public void sincroniza() {
        this.sincronizado = 1;
    }

    public void desincroniza() {
        this.sincronizado = 0;
    }


    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<ItemAvaria> getAvariaEntradeProdutos() {
        return avariaEntradeProdutos;
    }

    public void setAvariaEntradeProdutos(RealmList<ItemAvaria> avariaEntradeProdutos) {
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
