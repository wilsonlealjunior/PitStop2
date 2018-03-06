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

import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.R;

/**
 * Created by wilso on 02/10/2017.
 */

public class LstViewTabelaEstoqueAdpter extends ArrayAdapter<Produto> {

    int groupid;
    List<Produto> item_list;
    ArrayList<String> desc;
    Context context;
    public LstViewTabelaEstoqueAdpter(Context context, int vg, int id, List<Produto> item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }

    static class ViewHolder {
        public TextView produto;
        public TextView preco;
        public TextView quantidade;
        public TextView loja;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            LstViewTabelaEstoqueAdpter.ViewHolder viewHolder = new LstViewTabelaEstoqueAdpter.ViewHolder();
            viewHolder.produto= (TextView) rowView.findViewById(R.id.produto);
            viewHolder.preco= (TextView) rowView.findViewById(R.id.preco);
            viewHolder.quantidade= (TextView) rowView.findViewById(R.id.quantidade);
            viewHolder.loja = (TextView) rowView.findViewById(R.id.loja);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        Produto items=item_list.get(position);
        if(items!=null) {
            LstViewTabelaEstoqueAdpter.ViewHolder holder = (LstViewTabelaEstoqueAdpter.ViewHolder) rowView.getTag();
            if(items.getQuantidade()<items.getEstoqueMinimo()){
                holder.produto.setBackgroundColor(getContext().getResources().getColor(R.color.estoque_baixo));
                holder.produto.setTextColor(getContext().getResources().getColor(R.color.white));
                holder.preco.setBackgroundColor(getContext().getResources().getColor(R.color.estoque_baixo));
                holder.preco.setTextColor(getContext().getResources().getColor(R.color.white));
                holder.quantidade.setBackgroundColor(getContext().getResources().getColor(R.color.estoque_baixo));
                holder.quantidade.setTextColor(getContext().getResources().getColor(R.color.white));
                holder.loja.setBackgroundColor(getContext().getResources().getColor(R.color.estoque_baixo));
                holder.loja.setTextColor(getContext().getResources().getColor(R.color.white));

            }
            else{
                holder.produto.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                holder.produto.setTextColor(getContext().getResources().getColor(R.color.black));
                holder.preco.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                holder.preco.setTextColor(getContext().getResources().getColor(R.color.black));
                holder.quantidade.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                holder.quantidade.setTextColor(getContext().getResources().getColor(R.color.black));
                holder.loja.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                holder.loja.setTextColor(getContext().getResources().getColor(R.color.black));


            }
            holder.produto.setText(items.getNome());
            holder.preco.setText(Util.moedaNoFormatoBrasileiro(items.getPreco()));
            holder.quantidade.setText(String.valueOf(items.getQuantidade()));
            holder.loja.setText(String.valueOf(items.getLoja().getNome()));

        }
        return rowView;
    }
}
