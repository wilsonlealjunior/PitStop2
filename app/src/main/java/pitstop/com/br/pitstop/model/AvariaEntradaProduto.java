package pitstop.com.br.pitstop.model;

/**
 * Created by wilso on 27/11/2017.
 */

public class AvariaEntradaProduto {
    String id;
    String idAvaria;
    String idEntradaProduto;
    int quantidade;
    int sincronizado;

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

    public String getIdAvaria() {
        return idAvaria;
    }

    public void setIdAvaria(String idAvaria) {
        this.idAvaria = idAvaria;
    }

    public String getIdEntradaProduto() {
        return idEntradaProduto;
    }

    public void setIdEntradaProduto(String idEntradaProduto) {
        this.idEntradaProduto = idEntradaProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }
}
