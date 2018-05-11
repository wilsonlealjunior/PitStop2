package pitstop.com.br.pitstop.helper;

import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.cadastro.CadastroEntradaProdutoActivity;
import pitstop.com.br.pitstop.model.EntradaProduto;

/**
 * Created by wilso on 23/11/2017.
 */

public class CadastroEntradaProdutoHelper {
    private final EditText precoDeCompra;
    private final EditText campoquantidade;
    private EntradaProduto entradaProduto;

    public CadastroEntradaProdutoHelper(CadastroEntradaProdutoActivity activity) {
        campoquantidade = (EditText) activity.findViewById(R.id.quantidade);
        precoDeCompra = (EditText) activity.findViewById(R.id.preco_de_compra);
        entradaProduto = new EntradaProduto();

    }

    public EntradaProduto PegarEntradaProduto() {
        entradaProduto = new EntradaProduto();
        entradaProduto.setId(UUID.randomUUID().toString());
        if(precoDeCompra.getText().toString().equals("")){
            precoDeCompra.setError("Digite o Preço de Compra");
            precoDeCompra.requestFocus();
            return null;
        }
        entradaProduto.setPrecoDeCompra(Double.valueOf(precoDeCompra.getText().toString()));
        if(campoquantidade.getText().toString().equals("")){
            campoquantidade.setError("Digite a quantidade");
            campoquantidade.requestFocus();
            return null;
        }
        entradaProduto.setQuantidade(Integer.valueOf(campoquantidade.getText().toString()));
        entradaProduto.setData((new Date()));
        return entradaProduto;
    }

    public void preencheFormulario(EntradaProduto l) {
        precoDeCompra.setText(String.valueOf(l.getPrecoDeCompra()));
        campoquantidade.setText(String.valueOf(entradaProduto.getQuantidade()));
        this.entradaProduto = l;
    }
}
