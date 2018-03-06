package pitstop.com.br.pitstop.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.ItemFuro;

/**
 * Created by wilso on 17/12/2017.
 */

public class LstViewTabelaDescricaoFuroAdapter extends ArrayAdapter<ItemFuro> {

    int groupid;
    List<ItemFuro> item_list;
    Context context;
    public LstViewTabelaDescricaoFuroAdapter(Context context, int vg, int id, List<ItemFuro> item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }

    static class ViewHolder {
        public TextView produto;
        public TextView quantidade;
        public TextView valor;
        public TextView precoDeVenda;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            LstViewTabelaDescricaoFuroAdapter.ViewHolder viewHolder = new LstViewTabelaDescricaoFuroAdapter.ViewHolder();
            viewHolder.produto= (TextView) rowView.findViewById(R.id.produto);
            viewHolder.quantidade= (TextView) rowView.findViewById(R.id.quantidade);
            viewHolder.valor = (TextView) rowView.findViewById(R.id.valor);
            viewHolder.precoDeVenda = (TextView)rowView.findViewById(R.id.precoDeVenda) ;
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        ItemFuro items=item_list.get(position);
        if(items!=null) {

            LstViewTabelaDescricaoFuroAdapter.ViewHolder holder = (LstViewTabelaDescricaoFuroAdapter.ViewHolder) rowView.getTag();

            EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(context);
            EntradaProduto ep = entradaProdutoDAO.procuraPorId(items.getIdEntradaProduto());
            entradaProdutoDAO.close();
            holder.produto.setText(String.valueOf(ep.getProduto().getNome()));
            holder.valor.setText(Util.moedaNoFormatoBrasileiro((items.getPrecoDeVenda()) * items.getQuantidade()));
            holder.quantidade.setText(String.valueOf(items.getQuantidade()));
            holder.precoDeVenda.setText(Util.moedaNoFormatoBrasileiro(items.getPrecoDeVenda()));
        }
        return rowView;
    }

}
