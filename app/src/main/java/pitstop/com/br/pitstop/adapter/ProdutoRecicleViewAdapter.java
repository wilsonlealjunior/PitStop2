package pitstop.com.br.pitstop.adapter;

/**
 * Created by wilso on 23/12/2017.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.CadastroProdutoActivity;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

public class ProdutoRecicleViewAdapter extends RecyclerView.Adapter<ProdutoRecicleViewAdapter.ViewHolder> {
    private ArrayList<Produto> produtos;
    private static ItemClickListener itemClickListener;
    Context contexto;

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
        this.contexto = contexto;
    }

    public ProdutoRecicleViewAdapter(ArrayList<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.contexto=context;
    }

    @Override
    public ProdutoRecicleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_produto_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProdutoRecicleViewAdapter.ViewHolder viewHolder, final int i) {

       final Produto p = produtos.get(i);
        if(p.getSincronizado()==0) {
            viewHolder.produto_sincronizado.setText("Desincronizado");
            viewHolder.produto_sincronizado.setBackgroundResource((R.drawable.oval_desincronizado));

        }else
        {
            viewHolder.produto_sincronizado.setText("Sincronizado");
            viewHolder.produto_sincronizado.setBackgroundResource((R.drawable.oval));

        }
        if(p.getQuantidade()<=p.getEstoqueMinimo()){
            viewHolder.produto_nome.setBackgroundResource((android.R.color.holo_red_light));
            viewHolder.ll_nome_produto.setBackgroundResource((android.R.color.holo_red_light));
        }
        else{
            viewHolder.produto_nome.setBackgroundResource((android.R.color.holo_blue_dark));
            viewHolder.ll_nome_produto.setBackgroundResource((android.R.color.holo_blue_dark));
        }
        viewHolder.produto_nome.setText(produtos.get(i).getNome());
        viewHolder.produto_preco.setText(String.valueOf(produtos.get(i).getPreco()));
        viewHolder.produto_quantidade.setText(String.valueOf(produtos.get(i).getQuantidade()));
        viewHolder.produto_loja.setText(produtos.get(i).getLoja().getNome());

        viewHolder.produto_editar.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
//                Log.e("Produto", p.getNome());
                UsuarioPreferences usuarioPreferences = new UsuarioPreferences(contexto);
                if(usuarioPreferences.temUsuario()) {
                    if(usuarioPreferences.getUsuario().getRole().equals("Administrador")) {
                        Intent intentVaiProFormulario = new Intent(contexto, CadastroProdutoActivity.class);
                        intentVaiProFormulario.putExtra("produto", p);
                        contexto.startActivity(intentVaiProFormulario);
                    }
                }
            }
    }) ;


    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView produto_nome, produto_preco, produto_loja, produto_quantidade,produto_sincronizado;
        private ImageButton produto_editar;
        private RelativeLayout ll_nome_produto;


        @SuppressLint("WrongViewCast")
        public ViewHolder(View view) {
            super(view);
            produto_sincronizado = (TextView)view.findViewById(R.id.sincronizado);
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
            if(itemClickListener != null) {
                itemClickListener.onItemClick(produtos.get(getAdapterPosition()).getSincronizado());
            }
//            Log.d(TAG, "Elemento " + getAdapterPosition() + " clicado.");
        }


    }

    public interface ItemClickListener {

        void onItemClick(int position);
    }




}
