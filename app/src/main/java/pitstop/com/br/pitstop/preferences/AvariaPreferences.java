package pitstop.com.br.pitstop.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wilso on 28/11/2017.
 */

public class AvariaPreferences {
    private static final String AVARIA_PREFERENCES = "pitstop.com.br.pitstop.preferences.AvariaPreferences";
    private static final String VERSAO_DO_DADO = "versao_do_dado";
    private Context context;

    public AvariaPreferences(Context context) {
        this.context = context;

    }

    public void salvarVersao(String versao) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VERSAO_DO_DADO,versao);
        editor.commit();

    }


    private  SharedPreferences getSharedPreferences(){
        return  context.getSharedPreferences(AVARIA_PREFERENCES,context.MODE_PRIVATE);
    }

    public String getVersao(){
        SharedPreferences preferences = getSharedPreferences();
        return preferences.getString(VERSAO_DO_DADO,"");

    }
    public  boolean temVersao(){
        return !getVersao().isEmpty();

    }
}
