package pitstop.com.br.pitstop.helper;

import android.widget.EditText;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.cadastro.CadastroLojaActivity;
import pitstop.com.br.pitstop.model.Loja;

/**
 * Created by wilso on 19/10/2017.
 */

public class CadastroLojaHelper {

    private final EditText campoEndereco;
    private final EditText campoNome;
    private Loja loja;

    public CadastroLojaHelper(CadastroLojaActivity activity) {
        campoNome = (EditText) activity.findViewById(R.id.loja);
        campoEndereco = (EditText) activity.findViewById(R.id.endereco);
        loja = new Loja();


    }

    public Loja PegarLoja() {

        if(campoNome.getText().toString().equals("")){
            campoNome.setError("Digite um nome");
            campoNome.requestFocus();
            return null;
        }
        loja.setNome(campoNome.getText().toString().toUpperCase());

        if(campoEndereco.getText().toString().equals("")){
            campoEndereco.setError("Digite um endereço");
            campoEndereco.requestFocus();
            return null;
        }
        loja.setEndereco(campoEndereco.getText().toString().toUpperCase());
        return loja;
    }

    public void preencheFormulario(Loja Loja) {
        campoNome.setText(Loja.getNome().toUpperCase());
        campoEndereco.setText(String.valueOf(Loja.getEndereco().toUpperCase()));
        this.loja = Loja;
    }
}
