package pitstop.com.br.pitstop.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wilso on 24/10/2017.
 */

public class LojaPreferences {
    private static final String LOJAS_PREFERENCES = "pitstop.com.br.pitstop.preferences.LojaPreferences";
    private static final String VERSAO_DO_DADO = "versao_do_dado";
    private  Context context;

    public LojaPreferences(Context context) {
        this.context = context;

    }

    public void salvarVersao(String versao) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VERSAO_DO_DADO,versao);
        editor.commit();

    }
    private  SharedPreferences getSharedPreferences(){
        return  context.getSharedPreferences(LOJAS_PREFERENCES,context.MODE_PRIVATE);
    }

    public String getVersao(){
        SharedPreferences preferences = getSharedPreferences();
        return preferences.getString(VERSAO_DO_DADO,"");

    }
    public  boolean temVersao(){
        return !getVersao().isEmpty();

    }
}
