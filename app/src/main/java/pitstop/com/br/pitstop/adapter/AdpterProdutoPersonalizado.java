package pitstop.com.br.pitstop.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.R;

/**
 * Created by wilso on 27/09/2017.
 */

public class AdpterProdutoPersonalizado extends BaseAdapter {

    private final List<Produto> produtos;
    private final Activity act;

    public AdpterProdutoPersonalizado(List<Produto> produtos, Activity act) {
        this.produtos = produtos;
        this.act = act;
    }

    @Override
    public int getCount() {
        return produtos.size();
    }

    @Override
    public Object getItem(int position) {
        return produtos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = act.getLayoutInflater()
                .inflate(R.layout.lista_de_produto_personalizada, parent, false);

        Produto produto = produtos.get(position);

        //pegando as referências das Views
        TextView nome = (TextView)
                view.findViewById(R.id.nome);
        TextView preco = (TextView)
                view.findViewById(R.id.preco);
        TextView quantidade = (TextView)
                view.findViewById(R.id.quantidade);


        //populando as Views
        nome.setText(produto.getNome());
       // preco.setText("Preço: R$ "+String.valueOf(produto.getPreco()));
       // quantidade.setText("Quantidade: "+String.valueOf(produto.getQuantidade()+" und"));
        return view;
    }
}
