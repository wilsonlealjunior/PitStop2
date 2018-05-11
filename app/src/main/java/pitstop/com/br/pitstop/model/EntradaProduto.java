package pitstop.com.br.pitstop.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wilso on 19/10/2017.
 */

public class EntradaProduto extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private double precoDeCompra;
    private int quantidade;
    private Date data;
    private Produto produto;
    private int sincronizado;
    private String momentoDaUltimaAtualizacao;
    private int quantidadeVendidaMovimentada;
    private int desativado = 0;


    public EntradaProduto() {

    }


    protected EntradaProduto(Parcel in) {
        id = in.readString();
        precoDeCompra = in.readDouble();
        quantidade = in.readInt();
        produto = in.readParcelable(Produto.class.getClassLoader());
        sincronizado = in.readInt();
        momentoDaUltimaAtualizacao = in.readString();
        quantidadeVendidaMovimentada = in.readInt();
        desativado = in.readInt();
        data = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(precoDeCompra);
        dest.writeInt(quantidade);
        dest.writeParcelable(produto, flags);
        dest.writeInt(sincronizado);
        dest.writeString(momentoDaUltimaAtualizacao);
        dest.writeInt(quantidadeVendidaMovimentada);
        dest.writeInt(desativado);
        dest.writeLong(data.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EntradaProduto> CREATOR = new Creator<EntradaProduto>() {
        @Override
        public EntradaProduto createFromParcel(Parcel in) {
            return new EntradaProduto(in);
        }

        @Override
        public EntradaProduto[] newArray(int size) {
            return new EntradaProduto[size];
        }
    };

    public void desativar() {
        desativado = 1;
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


    public int getQuantidadeVendidaMovimentada() {
        return quantidadeVendidaMovimentada;
    }

    public void setQuantidadeVendidaMovimentada(int quantidadeVendidaMovimentada) {
        this.quantidadeVendidaMovimentada = quantidadeVendidaMovimentada;
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


    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }



    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
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

    public double getPrecoDeCompra() {
        return precoDeCompra;
    }

    public void setPrecoDeCompra(double precoDeCompra) {
        this.precoDeCompra = precoDeCompra;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

}
