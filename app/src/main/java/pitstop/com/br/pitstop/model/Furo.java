package pitstop.com.br.pitstop.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilso on 15/12/2017.
 */

public class Furo {
    String id;
    List<FuroEntradaProduto> furoEntradeProdutos;
    String idLoja;
    String idUsuario;
    int sincronizado;
    String data;
    double valor;
    String momentoDaUltimaAtualizacao;
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


    public Furo(){
        furoEntradeProdutos = new ArrayList<>();
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

    public List<FuroEntradaProduto> getFuroEntradeProdutos() {
        return furoEntradeProdutos;
    }

    public void setFuroEntradeProdutos(List<FuroEntradaProduto> furoEntradeProdutos) {
        this.furoEntradeProdutos = furoEntradeProdutos;
    }

    public String getMomentoDaUltimaAtualizacao() {
        return momentoDaUltimaAtualizacao;
    }

    public void setMomentoDaUltimaAtualizacao(String momentoDaUltimaAtualizacao) {
        this.momentoDaUltimaAtualizacao = momentoDaUltimaAtualizacao;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
