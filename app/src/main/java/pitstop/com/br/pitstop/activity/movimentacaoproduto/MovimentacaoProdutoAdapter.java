package pitstop.com.br.pitstop.activity.movimentacaoproduto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.model.Furo;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.MovimentacaoProduto;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

/**
 * Created by wilso on 02/05/2018.
 */

public class MovimentacaoProdutoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<MovimentacaoProduto> item_list;
    ArrayList<String> desc;
    Context context;
    private static ItemClickListener itemClickListener;
    UsuarioPreferences up;
    private static ItemDeleteListener itemDeleteListener;

    public MovimentacaoProdutoAdapter(List<MovimentacaoProduto> item_list, Context context) {
        this.item_list = item_list;
        this.context = context;
        up = new UsuarioPreferences(context);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnItemDeleteListener(ItemDeleteListener itemDeleteListener) {
        this.itemDeleteListener = itemDeleteListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_movimentacao_produto, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        UsuarioPreferences up = new UsuarioPreferences(context);
        if (item_list.size() > 0) {
            final MovimentacaoProduto items = item_list.get(position);
            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            Produto p = produtoDAO.procuraPorId(items.getIdProduto());
            produtoDAO.close();
            LojaDAO lojaDAO = new LojaDAO(context);
            Date d = null;
            d = items.getData();
            String dataFormatada = "";
            dataFormatada += Util.dataComDiaEHoraPorExtenso(d.getTime());
            ((ViewHolder) holder).data.setText(dataFormatada);
            ((ViewHolder) holder).produto.setText(p.getNome());
            ((ViewHolder) holder).quantidade.setText(String.valueOf(items.getQuantidade()));
            ((ViewHolder) holder).lojaOrigem.setText(lojaDAO.procuraPorId(items.getIdLojaDe()).getNome());
            ((ViewHolder) holder).lojaDestino.setText(lojaDAO.procuraPorId(items.getIdLojaPara()).getNome());
            lojaDAO.close();

        }
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        public TextView lojaOrigem;
        public TextView lojaDestino;
        public TextView quantidade;
        public TextView data;
        public TextView produto;


        public ViewHolder(View view) {
            super(view);

            data = (TextView) view.findViewById(R.id.data);
            lojaOrigem = (TextView) view.findViewById(R.id.loja_origem);
            lojaDestino = (TextView) view.findViewById(R.id.loja_destino);
            produto = (TextView) view.findViewById(R.id.produto);

            quantidade = (TextView) view.findViewById(R.id.quantidade);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick((getAdapterPosition()));
            }
        }





    }

    public interface ItemClickListener {

        void onItemClick(int position);
    }

    public interface ItemDeleteListener {

        void onItemDelete(int position);
    }


}
