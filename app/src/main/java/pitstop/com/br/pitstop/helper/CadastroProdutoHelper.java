package pitstop.com.br.pitstop.helper;

import android.widget.EditText;
import android.widget.Spinner;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.CadastroProdutoActivity;
import pitstop.com.br.pitstop.model.Produto;

/**
 * Created by wilso on 25/09/2017.
 */

public class CadastroProdutoHelper {
    private final EditText campoPreco;
//    private final EditText campoQuantidade;
    private final EditText campoNome;
    private final EditText campoEstoqueMinimo;
    private Produto produto;

    public CadastroProdutoHelper(CadastroProdutoActivity activity) {
        campoNome = (EditText) activity.findViewById(R.id.nome);
//        spinner = (Spinner) activity.findViewById(R.id.spinner);
        campoPreco = (EditText) activity.findViewById(R.id.preco);
        campoEstoqueMinimo = (EditText) activity.findViewById(R.id.estoque_minimo);
//        campoQuantidade = (EditText) activity.findViewById(R.id.quantidade);



        produto = new Produto();


    }

    public Produto PegarProduto() {
        if(campoNome.getText().toString().equals("")){
            campoNome.setError("Digite um nome");
            campoNome.requestFocus();
            return null;
        }
        produto.setNome(campoNome.getText().toString().toUpperCase());
        //produto.setQuantidade(Integer.parseInt(campoQuantidade.getText().toString()));
        if(campoPreco.getText().toString().equals("")){
            campoPreco.setError("Digite o Preço");
            campoPreco.requestFocus();
            return null;
        }
        if(campoEstoqueMinimo.getText().toString().equals("")){
            campoEstoqueMinimo.setError("Digite o estoque minimo");
            campoEstoqueMinimo.requestFocus();
            return null;
        }
        produto.setEstoqueMinimo(Integer.valueOf(campoEstoqueMinimo.getText().toString()));
        produto.setPreco(Double.valueOf(campoPreco.getText().toString()));

        return produto;
    }

    public void preencheFormulario(Produto produto) {
        campoNome.setText(produto.getNome().toUpperCase());
        campoPreco.setText(String.valueOf(produto.getPreco()));
        campoEstoqueMinimo.setText(String.valueOf(produto.getEstoqueMinimo()));

//        campoQuantidade.setText(String.valueOf(produto.getQuantidade()));
//        campoQuantidade.setInputType(InputType.TYPE_NULL );
        this.produto = produto;
    }
}
