package pitstop.com.br.pitstop.model;

/**
 * Created by wilso on 15/12/2017.
 */

public class ItemFuro {
    String id;
    String idFuro;
    String idEntradaProduto;
    int quantidade;
    int sincronizado;
    double precoDeVenda;

    public void sincroniza() {
        this.sincronizado = 1;
    }
    public void desincroniza() {
        this.sincronizado = 0;
    }


    public double getPrecoDeVenda() {
        return precoDeVenda;
    }

    public void setPrecoDeVenda(double precoDeVenda) {
        this.precoDeVenda = precoDeVenda;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdFuro() {
        return idFuro;
    }

    public void setIdFuro(String idFuro) {
        this.idFuro = idFuro;
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