package pitstop.com.br.pitstop.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wilso on 28/11/2017.
 */

public class ObjetosSinkPreferences {

    private static final String OBJETOSSINK_PREFERENCES = "pitstop.com.br.pitstop.preferences.ObjetosSinkPreferences";
    private static final String VERSAO_DO_DADO = "versao_do_dado";
    private Context context;

    public ObjetosSinkPreferences(Context context) {
        this.context = context;

    }

    public void salvarVersao(String versao) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VERSAO_DO_DADO,versao);
        editor.commit();

    }
    private  SharedPreferences getSharedPreferences(){
        return  context.getSharedPreferences(OBJETOSSINK_PREFERENCES,context.MODE_PRIVATE);
    }

    public String getVersao(){
        SharedPreferences preferences = getSharedPreferences();
        return preferences.getString(VERSAO_DO_DADO,"");

    }
    public  boolean temVersao(){
        return !getVersao().isEmpty();

    }
}
