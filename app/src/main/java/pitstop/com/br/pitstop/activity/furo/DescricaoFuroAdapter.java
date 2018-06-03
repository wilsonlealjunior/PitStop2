package pitstop.com.br.pitstop.activity.furo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.ItemAvaria;
import pitstop.com.br.pitstop.model.ItemFuro;
import pitstop.com.br.pitstop.model.ItemVenda;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

/**
 * Created by wilso on 27/04/2018.
 */

public class DescricaoFuroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ItemFuro> item_list;
    Context contexto;


    public DescricaoFuroAdapter(List<ItemFuro> item_list, Context context) {
        this.item_list = item_list;
        this.contexto = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_descricao_furo, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {
        UsuarioPreferences up = new UsuarioPreferences(contexto);
        if (item_list.size() > 0) {
            final ItemFuro items = item_list.get(i);

            EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(contexto);
            EntradaProduto ep = entradaProdutoDAO.procuraPorId(items.getIdEntradaProduto());
            entradaProdutoDAO.close();
            ((ViewHolder) holder).produto.setText(String.valueOf(ep.getProduto().getNome()));
            ((ViewHolder) holder).total.setText(Util.moedaNoFormatoBrasileiro((items.getPrecoDeVenda()) * items.getQuantidade()));
            ((ViewHolder) holder).quantidade.setText(String.valueOf(items.getQuantidade()));
            ((ViewHolder) holder).precoDeVenda.setText(Util.moedaNoFormatoBrasileiro((items.getPrecoDeVenda())));

        }

    }


    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView produto;
        public TextView quantidade;
        public TextView total;
        public TextView precoDeVenda;


        public ViewHolder(View view) {
            super(view);
            produto = (TextView) view.findViewById(R.id.produto);
            quantidade = (TextView) view.findViewById(R.id.quantidade);
            total = (TextView) view.findViewById(R.id.total);
            precoDeVenda = (TextView) view.findViewById(R.id.precoDeVenda);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Log.e("Posicao clicada foi", "" + getAdapterPosition());

        }
    }


}
