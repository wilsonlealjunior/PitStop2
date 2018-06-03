package pitstop.com.br.pitstop.activity.furo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.model.Furo;
import pitstop.com.br.pitstop.model.ItemFuro;
import pitstop.com.br.pitstop.model.Produto;

/**
 * Created by wilso on 25/12/2017.
 */

public class LstViewTabelaCarinhoFuro extends ArrayAdapter<Furo> {
    int groupid;
    List<Furo> item_list;
    ArrayList<String> desc;
    Context context;

    public LstViewTabelaCarinhoFuro(Context context, int vg, int id, List<Furo> item_list) {
        super(context, vg, id, item_list);
        this.context = context;
        groupid = vg;
        this.item_list = item_list;

    }

    static class ViewHolder {
        public TextView loja;
        public TextView produto;
        public TextView usuario;
        public TextView quantidade;


    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            LstViewTabelaCarinhoFuro.ViewHolder viewHolder = new LstViewTabelaCarinhoFuro.ViewHolder();

            viewHolder.loja = (TextView) rowView.findViewById(R.id.loja);
            viewHolder.produto = (TextView) rowView.findViewById(R.id.produto);
            viewHolder.usuario = (TextView) rowView.findViewById(R.id.usuario);
            viewHolder.quantidade = (TextView) rowView.findViewById(R.id.quantidade);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        Furo items = item_list.get(position);
        if (items != null) {
            LstViewTabelaCarinhoFuro.ViewHolder holder = (LstViewTabelaCarinhoFuro.ViewHolder) rowView.getTag();

            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            Produto produto = produtoDAO.procuraPorId(items.getIdProduto());
            produtoDAO.close();


//            holder.produto.setText(items.);
            holder.usuario.setText(String.valueOf(items.getIdUsuario()));
            holder.loja.setText(produto.getLoja().getNome());
            holder.quantidade.setText(String.valueOf(items.getQuantidade()));
            holder.produto.setText(produto.getNome());

        }
        return rowView;
    }
}

