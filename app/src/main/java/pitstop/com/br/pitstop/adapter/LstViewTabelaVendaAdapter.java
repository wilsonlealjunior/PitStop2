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

import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.R;

/**
 * Created by wilso on 02/10/2017.
 */

public class LstViewTabelaVendaAdapter extends ArrayAdapter<Produto> {
    int groupid;
    List<Produto> item_list;
    ArrayList<String> desc;
    Context context;
    public LstViewTabelaVendaAdapter(Context context, int vg, int id, List<Produto> item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }

    static class ViewHolder {
        public TextView produto;
        public TextView preco;
        public TextView quantidade;
        public TextView total;


    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.produto= (TextView) rowView.findViewById(R.id.produto);
            viewHolder.preco= (TextView) rowView.findViewById(R.id.preco);
            viewHolder.quantidade= (TextView) rowView.findViewById(R.id.quantidade);
            viewHolder.total= (TextView) rowView.findViewById(R.id.total);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        Produto items=item_list.get(position);
        if(items!=null) {
            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.produto.setText(items.getNome());
            holder.preco.setText(String.valueOf(items.getPreco()));
            holder.quantidade.setText(String.valueOf(items.getQuantidade()));
            holder.total.setText(String.valueOf(items.getQuantidade()*items.getPreco()));

        }
        return rowView;
    }

}
