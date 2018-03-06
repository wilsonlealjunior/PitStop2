package pitstop.com.br.pitstop.adapter;

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
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.Furo;
import pitstop.com.br.pitstop.model.ItemFuro;
import pitstop.com.br.pitstop.model.Produto;

/**
 * Created by wilso on 25/12/2017.
 */

public class LstViewTabelaCarrinhoAvaria extends ArrayAdapter<Avaria> {
    int groupid;
    List<Avaria> item_list;
    ArrayList<String> desc;
    Context context;

    public LstViewTabelaCarrinhoAvaria(Context context, int vg, int id, List<Avaria> item_list) {
        super(context, vg, id, item_list);
        this.context = context;
        groupid = vg;
        this.item_list = item_list;

    }

    static class ViewHolder {
        public TextView loja;
        public TextView produto;
        public TextView quantidade;


    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            LstViewTabelaCarrinhoAvaria.ViewHolder viewHolder = new LstViewTabelaCarrinhoAvaria.ViewHolder();

            viewHolder.loja = (TextView) rowView.findViewById(R.id.loja);
            viewHolder.produto = (TextView) rowView.findViewById(R.id.produto);
            viewHolder.quantidade = (TextView) rowView.findViewById(R.id.quantidade);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        Avaria items = item_list.get(position);
        if (items != null) {
            LstViewTabelaCarrinhoAvaria.ViewHolder holder = (LstViewTabelaCarrinhoAvaria.ViewHolder) rowView.getTag();

            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            Produto produto = produtoDAO.procuraPorId(items.getIdProduto());
            produtoDAO.close();


//            holder.produto.setText(items.);
            holder.loja.setText(produto.getLoja().getNome());
            holder.quantidade.setText(String.valueOf(items.getQuantidade()));
            holder.produto.setText(produto.getNome());

        }
        return rowView;
    }
}

