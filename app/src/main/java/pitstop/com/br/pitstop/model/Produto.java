package pitstop.com.br.pitstop.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by wilso on 20/09/2017.
 */

public class Produto extends RealmObject implements Parcelable, Comparable<Produto> {
    @PrimaryKey
    private String id;
    private String nome;
    private int estoqueMinimo;
    private int quantidade = 0;
    private double preco;
    private Loja loja;
    private RealmList<EntradaProduto> entradaProdutos = new RealmList<>();
    private int sincronizado;
    private String momentoDaUltimaAtualizacao;
    @Required
    private RealmList<String> idProdutoVinculado = new RealmList<>();
    private String idProdutoPrincipal;
    private int vinculo;


    protected Produto(Parcel in) {
        id = in.readString();
        nome = in.readString();
        estoqueMinimo = in.readInt();
        quantidade = in.readInt();
        preco = in.readDouble();
        loja = in.readParcelable(Loja.class.getClassLoader());
        sincronizado = in.readInt();
        momentoDaUltimaAtualizacao = in.readString();
        idProdutoPrincipal = in.readString();
        vinculo = in.readInt();
        entradaProdutos.addAll(in.createTypedArrayList(EntradaProduto.CREATOR));
        idProdutoVinculado.addAll(in.createTypedArrayList(in.STRING_CREATOR));

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nome);
        dest.writeInt(estoqueMinimo);
        dest.writeInt(quantidade);
        dest.writeDouble(preco);
        dest.writeParcelable(loja, flags);
        dest.writeInt(sincronizado);
        dest.writeString(momentoDaUltimaAtualizacao);
        dest.writeString(idProdutoPrincipal);
        dest.writeInt(vinculo);
        dest.writeTypedList(entradaProdutos);
        dest.writeStringList(idProdutoVinculado);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Produto> CREATOR = new Creator<Produto>() {
        @Override
        public Produto createFromParcel(Parcel in) {
            return new Produto(in);
        }

        @Override
        public Produto[] newArray(int size) {
            return new Produto[size];
        }
    };

    public String getIdProdutoPrincipal() {
        return idProdutoPrincipal;
    }

    public void setIdProdutoPrincipal(String idProdutoPrincipal) {
        this.idProdutoPrincipal = idProdutoPrincipal;
    }

    public String getMomentoDaUltimaAtualizacao() {
        return momentoDaUltimaAtualizacao;
    }

    public void setMomentoDaUltimaAtualizacao(String momentoDaUltimaAtualizacao) {
        this.momentoDaUltimaAtualizacao = momentoDaUltimaAtualizacao;
    }

    public RealmList<String> getIdProdutoVinculado() {
        return idProdutoVinculado;
    }


    public int getVinculo() {
        return vinculo;
    }

    public void setVinculo(int vinculo) {
        this.vinculo = vinculo;
    }

    public boolean vinculado() {
        return vinculo == 1;
    }

    public void vincular(String idProdutoPrincipal) {
        this.idProdutoPrincipal = idProdutoPrincipal;
        vinculo = 1;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public Produto() {

    }


    public void setEntradaProdutos(RealmList<EntradaProduto> entradaProdutos) {
        this.entradaProdutos = entradaProdutos;
    }

    public void setIdProdutoVinculado(RealmList<String> idProdutoVinculado) {
        this.idProdutoVinculado = idProdutoVinculado;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Produto(String id, String nome, double preco, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Loja getLoja() {
        return loja;
    }

    public void setLoja(Loja loja) {
        this.loja = loja;
    }

    public RealmList<EntradaProduto> getEntradaProdutos() {
        return entradaProdutos;
    }


    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        //calcularQuantidade();
        return quantidade;
    }
//
//    public void calcularQuantidade() {
//        if ((quantidade == 0) && (entradaProdutos.size()==0)) {
//            quantidade = 0;
//            if (entradaProdutos != null) {
//                for (EntradaProduto l : entradaProdutos) {
//                    quantidade += (l.getQuantidade() - l.getQuantidadeVendidaMovimentada());
//
//                }
//            }
//        }
//
//    }

    public void calcularQuantidade() {
        quantidade = 0;
        if (entradaProdutos != null) {
            for (EntradaProduto l : entradaProdutos) {
                quantidade += (l.getQuantidade() - l.getQuantidadeVendidaMovimentada());

            }
        }
    }

    public void entrada(int entrada) {
        //calcularQuantidade();
        quantidade += entrada;

    }


    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Produto: " + nome + "\nPreco: " +
                preco + "\nQuantidade: " + quantidade;
    }

    public void sincroniza() {
        this.sincronizado = 1;
    }

    public void desincroniza() {
        this.sincronizado = 0;
    }

    @Override
    public int compareTo(@NonNull Produto produto) {
        return nome.compareTo(produto.getNome());

    }

}


