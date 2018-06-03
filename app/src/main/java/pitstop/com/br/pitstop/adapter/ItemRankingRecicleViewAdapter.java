package pitstop.com.br.pitstop.adapter;

/**
 * Created by wilso on 23/12/2017.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.model.Produto;

public class ItemRankingRecicleViewAdapter extends RecyclerView.Adapter<ItemRankingRecicleViewAdapter.ViewHolder> {
    private List<Produto> produtos;
    private static ItemClickListener itemClickListener;
    Context contexto;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.contexto = contexto;
    }

    public ItemRankingRecicleViewAdapter(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.contexto = context;
    }

    @Override
    public ItemRankingRecicleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ranking_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemRankingRecicleViewAdapter.ViewHolder viewHolder, final int i) {


    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView produto_nome, produto_preco, produto_loja, produto_quantidade, produto_sincronizado;
        private ImageButton produto_editar;
        private RelativeLayout ll_nome_produto;


        @SuppressLint("WrongViewCast")
        public ViewHolder(View view) {
            super(view);
            produto_sincronizado = (TextView) view.findViewById(R.id.sincronizado);
            produto_editar = (ImageButton) view.findViewById(R.id.editar);
            produto_nome = (TextView) view.findViewById(R.id.produto);
            produto_preco = (TextView) view.findViewById(R.id.preco);
            produto_quantidade = (TextView) view.findViewById(R.id.quantidade);
            produto_loja = (TextView) view.findViewById(R.id.loja);
            ll_nome_produto = (RelativeLayout) view.findViewById(R.id.ll_nome_produto);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(produtos.get(getAdapterPosition()).getSincronizado());
            }
//            Log.d(TAG, "Elemento " + getAdapterPosition() + " clicado.");
        }


    }

    public interface ItemClickListener {

        void onItemClick(int position);
    }


}
