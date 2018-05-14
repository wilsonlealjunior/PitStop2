package pitstop.com.br.pitstop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.activity.cadastro.CadastroProdutoActivity;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.ItemVenda;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

/**
 * Created by wilso on 27/04/2018.
 */

public class TabelaDescricaoVendaRecicleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ItemVenda> item_list;
    Context contexto;


    public TabelaDescricaoVendaRecicleViewAdapter(List<ItemVenda> item_list, Context context) {
        this.item_list = item_list;
        this.contexto = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_descricao_venda, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {
        UsuarioPreferences up = new UsuarioPreferences(contexto);
        if (item_list.size() > 0) {
            final ItemVenda items = item_list.get(i);
            EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(contexto);
            ProdutoDAO produtoDAO = new ProdutoDAO(contexto);
            Produto produto = produtoDAO.procuraPorId(items.getIdProduto());
            produtoDAO.close();
            EntradaProduto ep = entradaProdutoDAO.procuraPorId(items.getIdEntradaProduto());
            entradaProdutoDAO.close();
            ((ViewHolder) holder).produto.setText(produto.getNome());

            if (up.temUsuario()) {
                if (up.getUsuario().getRole().equals("Funcionario")) {
                    ((ViewHolder) holder).lucro.setText(Util.moedaNoFormatoBrasileiro((produto.getPreco()) * items.getQuantidadeVendida()));

                } else {
                    ((ViewHolder) holder).lucro.setText(Util.moedaNoFormatoBrasileiro((produto.getPreco() - ep.getPrecoDeCompra()) * items.getQuantidadeVendida()));
                }
            }
            ((ViewHolder) holder).quantidade.setText(String.valueOf(items.getQuantidadeVendida()));

        }

    }


    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView produto;
        public TextView quantidade;
        public TextView lucro;


        public ViewHolder(View view) {
            super(view);
            produto = (TextView) view.findViewById(R.id.produto);
            quantidade = (TextView) view.findViewById(R.id.quantidade);
            lucro = (TextView) view.findViewById(R.id.lucro);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Log.e("Posicao clicada foi", "" + getAdapterPosition());

        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        public TextView produto;
        public TextView quantidade;
        public TextView lucro;


        public ViewHolderHeader(View view) {
            super(view);
            produto = (TextView) view.findViewById(R.id.produto);
            quantidade = (TextView) view.findViewById(R.id.quantidade);
            lucro = (TextView) view.findViewById(R.id.lucro);
        }


    }


}
