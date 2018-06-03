package pitstop.com.br.pitstop.activity.entradaproduto;

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
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

/**
 * Created by wilso on 02/05/2018.
 */

public class EntradaProdutoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<EntradaProduto> item_list;
    ArrayList<String> desc;
    Context context;
    private static ItemClickListener itemClickListener;
    UsuarioPreferences up;
    private static ItemDeleteListener itemDeleteListener;

    public EntradaProdutoAdapter(List<EntradaProduto> item_list, Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_entrada_produto, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        UsuarioPreferences up = new UsuarioPreferences(context);
        if (item_list.size() > 0) {
            final EntradaProduto items = item_list.get(position);
            Date d = null;
            d = items.getData();
            String dataFormatada = "";
            dataFormatada += Util.dataComDiaEHoraPorExtenso(d.getTime());
            ((ViewHolder) holder).data.setText(dataFormatada);
            ((ViewHolder) holder).produto.setText(items.getProduto().getNome());
            ((ViewHolder) holder).precoDeCompra.setText(Util.moedaNoFormatoBrasileiro(items.getPrecoDeCompra()));
            ((ViewHolder) holder).quantidade.setText(String.valueOf(items.getQuantidade()));
            ((ViewHolder) holder).loja.setText(String.valueOf(items.getProduto().getLoja().getNome()));
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
        public TextView produto;
        public TextView data;
        public TextView precoDeCompra;
        public TextView quantidade;
        public TextView loja;

        public ImageButton btnDeletar;

        public ViewHolder(View view) {
            super(view);

            btnDeletar = (ImageButton) view.findViewById(R.id.btn_deletar);
            data = (TextView) view.findViewById(R.id.data);
            precoDeCompra = (TextView) view.findViewById(R.id.preco_de_compra);
            quantidade = (TextView) view.findViewById(R.id.quantidade);
            loja = (TextView) view.findViewById(R.id.loja);
            produto = (TextView) view.findViewById(R.id.produto);
            view.setOnClickListener(this);
            view.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick((getAdapterPosition()));
            }
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
    }

    public interface ItemClickListener {

        void onItemClick(int position);
    }

    public interface ItemDeleteListener {

        void onItemDelete(int position);
    }


}
