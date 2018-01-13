package pitstop.com.br.pitstop.model;

/**
 * Created by wilso on 10/01/2018.
 */

public class Caixa {
    private String DateInicio;
    private String DateFim;
    private int aberto;
    private String nomeDeQuemAbriu;
    private String nomeDeQuemFechou;
    private String momentoDaUltimaAtualizacao;
    private int sincronizado;
    private int desativado;


    public String getDateInicio() {
        return DateInicio;
    }

    public void setDateInicio(String dateInicio) {
        DateInicio = dateInicio;
    }

    public String getDateFim() {
        return DateFim;
    }

    public void setDateFim(String dateFim) {
        DateFim = dateFim;
    }

    public int getAberto() {
        return aberto;
    }

    public void setAberto(int aberto) {
        this.aberto = aberto;
    }

    public String getNomeDeQuemAbriu() {
        return nomeDeQuemAbriu;
    }

    public void setNomeDeQuemAbriu(String nomeDeQuemAbriu) {
        this.nomeDeQuemAbriu = nomeDeQuemAbriu;
    }

    public String getNomeDeQuemFechou() {
        return nomeDeQuemFechou;
    }

    public void setNomeDeQuemFechou(String nomeDeQuemFechou) {
        this.nomeDeQuemFechou = nomeDeQuemFechou;
    }

    public String getMomentoDaUltimaAtualizacao() {
        return momentoDaUltimaAtualizacao;
    }

    public void setMomentoDaUltimaAtualizacao(String momentoDaUltimaAtualizacao) {
        this.momentoDaUltimaAtualizacao = momentoDaUltimaAtualizacao;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public int getDesativado() {
        return desativado;
    }

    public void setDesativado(int desativado) {
        this.desativado = desativado;
    }
}
