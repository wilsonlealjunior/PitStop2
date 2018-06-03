package pitstop.com.br.pitstop.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wilso on 11/05/2018.
 */

public class RealmString extends RealmObject {
    @PrimaryKey
    private String valor;

    public RealmString() {

    }

    public RealmString(String valor) {
        this.valor = valor;

    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
