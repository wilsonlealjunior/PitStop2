package pitstop.com.br.pitstop.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wilso on 14/11/2017.
 */

public class MovimentacaoProduto extends RealmObject {
    @PrimaryKey
    String id;
    String idLojaDe;
    String idLojaPara;
    String idProduto;
    int quantidade;
    int sincronizado;
    Date data;
    private int desativado = 0;

    public void desativar() {
        desativado = 1;
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

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
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

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }


    public String getIdLojaDe() {
        return idLojaDe;
    }

    public void setIdLojaDe(String idLojaDe) {
        this.idLojaDe = idLojaDe;
    }

    public String getIdLojaPara() {
        return idLojaPara;
    }

    public void setIdLojaPara(String idLojaPara) {
        this.idLojaPara = idLojaPara;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void sincroniza() {
        this.sincronizado = 1;
    }

    public void desincroniza() {
        this.sincronizado = 0;
    }
}
