package pitstop.com.br.pitstop.model;

/**
 * Created by wilso on 14/11/2017.
 */

public class MovimentacaoProduto {

    String id;
    String idLojaDe;
    String idLojaPara;
    String idProduto;
    int quantidade;
    int sincronizado;
    String data;
    String momentoDaUltimaAtualizacao;
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

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
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

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }



    public String getMomentoDaUltimaAtualizacao() {
        return momentoDaUltimaAtualizacao;
    }

    public void setMomentoDaUltimaAtualizacao(String momentoDaUltimaAtualizacao) {
        this.momentoDaUltimaAtualizacao = momentoDaUltimaAtualizacao;
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
