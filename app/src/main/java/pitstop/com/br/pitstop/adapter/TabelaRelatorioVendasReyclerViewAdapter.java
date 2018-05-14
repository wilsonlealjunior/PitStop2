package pitstop.com.br.pitstop.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.dao.VendaDAO;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.ItemVenda;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Venda;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

/**
 * Created by wilso on 02/05/2018.
 */

public class TabelaRelatorioVendasReyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Venda> item_list;
    ArrayList<String> desc;
    Context context;
    private static ItemClickListener itemClickListener;
    UsuarioPreferences up;
    private static ItemDeleteListener itemDeleteListener;

    public TabelaRelatorioVendasReyclerViewAdapter(List<Venda> item_list, Context context) {
        this.item_list = item_list;
        this.context = context;
        up = new UsuarioPreferences(context);
    }

    public void setOnItemClickListener(TabelaRelatorioVendasReyclerViewAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnItemDeleteListener(ItemDeleteListener itemDeleteListener) {
        this.itemDeleteListener = itemDeleteListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_venda, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        UsuarioPreferences up = new UsuarioPreferences(context);
        if (item_list.size() > 0) {
            final Venda items = item_list.get(position);
            if (up.temUsuario()) {
                if (up.getUsuario().getRole().equals("Funcionario")) {
                    ((ViewHolder) holder).lucro.setVisibility(View.GONE);
                }
            }
            ((ViewHolder) holder).lucro.setText((Util.moedaNoFormatoBrasileiro(items.getLucro())));
            Date d = null;
            d = items.getDataDaVenda();

            String dataFormatada = Util.diaDaSemana(d.getTime(), true) + ", ";
            dataFormatada += Util.dataComDiaEHoraPorExtenso(d.getTime());
            ((ViewHolder) holder).data.setText(dataFormatada);
            ((ViewHolder) holder).totalDinheiro.setText(Util.moedaNoFormatoBrasileiro(items.getTotalDinheiro()));
            ((ViewHolder) holder).totalCartao.setText(Util.moedaNoFormatoBrasileiro((items.getTotalCartao())));
            ((ViewHolder) holder).btnDeletar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemDeleteListener != null) {
                        itemDeleteListener.onItemDelete((position));
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener {
        public TextView data;
        public TextView totalDinheiro;
        public TextView totalCartao;
        public TextView lucro;
        public ImageButton btnDeletar;

        public ViewHolder(View view) {
            super(view);

            btnDeletar = (ImageButton) view.findViewById(R.id.btn_deletar);
            data = (TextView) view.findViewById(R.id.data);
            totalDinheiro = (TextView) view.findViewById(R.id.total_dinheiro);
            lucro = (TextView) view.findViewById(R.id.lucro);
            totalCartao = (TextView) view.findViewById(R.id.total_cartao);
            view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(this);


        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Selecione uma ação");
//            MenuItem edit = contextMenu.add(Menu.NONE, 1, 1, "Edit");
            MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "Deletar");


//            edit.setOnMenuItemClickListener(onChange);
            delete.setOnMenuItemClickListener(onChange);
        }

        private final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        Toast.makeText(context, "Edit", Toast.LENGTH_LONG).show();
                        return true;
                    case 2:
                        if (itemDeleteListener != null) {
                            itemDeleteListener.onItemDelete((getAdapterPosition()));
                        }
                        return true;
                }
                return false;
            }
        };

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
