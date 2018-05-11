package pitstop.com.br.pitstop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

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
        switch (viewType) {
            case TYPE_HEADER:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_relatorio_vendas, parent, false);
                return new ViewHolderHeader(view);
            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tabela_relatorio_vendas, parent, false);
                return new ViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UsuarioPreferences up = new UsuarioPreferences(context);
        if (holder instanceof ViewHolderHeader) {
            if (up.temUsuario()) {
                if (up.getUsuario().getRole().equals("Funcionario")) {
                    ((ViewHolderHeader) holder).lucro.setVisibility(View.GONE);
                }
            }
        } else {
            if (item_list.size() > 0) {
                final Venda items = item_list.get(position - 1);
                if (up.temUsuario()) {
                    if (up.getUsuario().getRole().equals("Funcionario")) {
                        ((ViewHolder) holder).lucro.setVisibility(View.GONE);
                    }
                }
                ((ViewHolder) holder).lucro.setText((Util.moedaNoFormatoBrasileiro(items.getLucro())));
                Date d = null;
                d = items.getDataDaVenda();
                ((ViewHolder) holder).data.setText(Util.dataNoformatoBrasileiro(d));
                ((ViewHolder) holder).totalDinheiro.setText(Util.moedaNoFormatoBrasileiro(items.getTotalDinheiro()));
                ((ViewHolder) holder).totalCartao.setText(Util.moedaNoFormatoBrasileiro((items.getTotalCartao())));
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return item_list.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener {
        public TextView data;
        public TextView totalDinheiro;
        public TextView totalCartao;
        public TextView lucro;


        public ViewHolder(View view) {
            super(view);
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
            MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");


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

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        public TextView data;
        public TextView totalDinheiro;
        public TextView totalCartao;
        public TextView lucro;


        public ViewHolderHeader(View view) {
            super(view);
            data = (TextView) view.findViewById(R.id.data);
            totalDinheiro = (TextView) view.findViewById(R.id.total_dinheiro);
            lucro = (TextView) view.findViewById(R.id.lucro);
            totalCartao = (TextView) view.findViewById(R.id.total_cartao);
        }


    }

    public interface ItemClickListener {

        void onItemClick(int position);
    }
    public interface ItemDeleteListener {

        void onItemDelete(int position);
    }

}
