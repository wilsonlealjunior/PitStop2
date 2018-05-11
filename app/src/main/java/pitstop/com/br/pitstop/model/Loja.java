package pitstop.com.br.pitstop.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wilso on 19/10/2017.
 */

public class Loja extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private String nome;
    private String endereco;
    private RealmList<Produto> produtos = new RealmList<>();
    private String momentoDaUltimaAtualizacao;
    private int sincronizado;




    public Loja(){

    }

    protected Loja(Parcel in) {
        id = in.readString();
        nome = in.readString();
        endereco = in.readString();
        momentoDaUltimaAtualizacao = in.readString();
        sincronizado = in.readInt();
        produtos .addAll(in.createTypedArrayList(Produto.CREATOR));

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nome);
        dest.writeString(endereco);
        dest.writeString(momentoDaUltimaAtualizacao);
        dest.writeInt(sincronizado);
        dest.writeTypedList(produtos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Loja> CREATOR = new Creator<Loja>() {
        @Override
        public Loja createFromParcel(Parcel in) {
            return new Loja(in);
        }

        @Override
        public Loja[] newArray(int size) {
            return new Loja[size];
        }
    };

    public int getSincronizado() {
        return sincronizado;
    }

    public void sincroniza() {
        this.sincronizado = 1;
    }

    public void desincroniza() {
        this.sincronizado = 0;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }


    public void setProdutos(RealmList<Produto> produtos) {
        this.produtos = produtos;
    }

    @Override
    public String toString() {
        return nome;
    }


}
