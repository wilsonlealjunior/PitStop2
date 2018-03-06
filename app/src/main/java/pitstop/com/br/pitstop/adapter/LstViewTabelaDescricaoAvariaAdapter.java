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
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.model.ItemAvaria;
import pitstop.com.br.pitstop.model.EntradaProduto;

/**
 * Created by wilso on 30/11/2017.
 */

public class LstViewTabelaDescricaoAvariaAdapter extends ArrayAdapter<ItemAvaria> {

    int groupid;
    List<ItemAvaria> item_list;
    ArrayList<String> desc;
    Context context;
    public LstViewTabelaDescricaoAvariaAdapter(Context context, int vg, int id, List<ItemAvaria> item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }

    static class ViewHolder {
        public TextView produto;
        public TextView quantidade;
        public TextView prejuizo;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            LstViewTabelaDescricaoAvariaAdapter.ViewHolder viewHolder = new LstViewTabelaDescricaoAvariaAdapter.ViewHolder();
            viewHolder.produto= (TextView) rowView.findViewById(R.id.produto);
            viewHolder.quantidade= (TextView) rowView.findViewById(R.id.quantidade);
            viewHolder.prejuizo = (TextView) rowView.findViewById(R.id.prejuizo);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        ItemAvaria items=item_list.get(position);
        if(items!=null) {

            LstViewTabelaDescricaoAvariaAdapter.ViewHolder holder = (LstViewTabelaDescricaoAvariaAdapter.ViewHolder) rowView.getTag();

            EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(context);
            EntradaProduto ep = entradaProdutoDAO.procuraPorId(items.getIdEntradaProduto());
            entradaProdutoDAO.close();
            holder.produto.setText(String.valueOf(ep.getProduto().getNome()));
            holder.prejuizo.setText(Util.moedaNoFormatoBrasileiro((ep.getPrecoDeCompra()) * items.getQuantidade()));
            holder.quantidade.setText(String.valueOf(items.getQuantidade()));

        }
        return rowView;
    }
}
