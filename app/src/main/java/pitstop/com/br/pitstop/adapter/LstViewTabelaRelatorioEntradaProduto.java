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
import pitstop.com.br.pitstop.model.EntradaProduto;

/**
 * Created by wilso on 27/11/2017.
 */

public class LstViewTabelaRelatorioEntradaProduto extends ArrayAdapter<EntradaProduto> {
    int groupid;
    List<EntradaProduto> item_list;
    ArrayList<String> desc;
    Context context;
    public LstViewTabelaRelatorioEntradaProduto(Context context, int vg, int id, List<EntradaProduto> item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }

    static class ViewHolder {
        public TextView produto;
        public TextView precoDeCompra;
        public TextView quantidade;
        public TextView loja;


    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            LstViewTabelaRelatorioEntradaProduto.ViewHolder viewHolder = new LstViewTabelaRelatorioEntradaProduto.ViewHolder();
            viewHolder.produto= (TextView) rowView.findViewById(R.id.produto);
            viewHolder.precoDeCompra= (TextView) rowView.findViewById(R.id.preco_de_compra);
            viewHolder.quantidade= (TextView) rowView.findViewById(R.id.quantidade);
            viewHolder.loja = (TextView) rowView.findViewById(R.id.loja);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        EntradaProduto items=item_list.get(position);
        if(items!=null) {
            LstViewTabelaRelatorioEntradaProduto.ViewHolder holder = (LstViewTabelaRelatorioEntradaProduto.ViewHolder) rowView.getTag();
            holder.produto.setText(items.getProduto().getNome());
            holder.precoDeCompra.setText(String.valueOf(items.getPrecoDeCompra()));
            holder.quantidade.setText(String.valueOf(items.getQuantidade()));
            holder.loja.setText(String.valueOf(items.getProduto().getLoja().getNome()));

        }
        return rowView;
    }

}
