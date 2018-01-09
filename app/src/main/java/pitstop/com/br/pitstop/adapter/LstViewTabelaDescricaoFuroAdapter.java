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
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.AvariaEntradaProduto;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.FuroEntradaProduto;

/**
 * Created by wilso on 17/12/2017.
 */

public class LstViewTabelaDescricaoFuroAdapter extends ArrayAdapter<FuroEntradaProduto> {

    int groupid;
    List<FuroEntradaProduto> item_list;
    ArrayList<String> desc;
    Context context;
    public LstViewTabelaDescricaoFuroAdapter(Context context, int vg, int id, List<FuroEntradaProduto> item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }

    static class ViewHolder {
        public TextView produto;
        public TextView quantidade;
        public TextView valor;

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
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        FuroEntradaProduto items=item_list.get(position);
        if(items!=null) {

            LstViewTabelaDescricaoFuroAdapter.ViewHolder holder = (LstViewTabelaDescricaoFuroAdapter.ViewHolder) rowView.getTag();

            EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(context);
            EntradaProduto ep = entradaProdutoDAO.procuraPorId(items.getIdEntradaProduto());
            holder.produto.setText(String.valueOf(ep.getProduto().getNome()));
            holder.valor.setText(String.valueOf((ep.getPrecoDeCompra())*items.getQuantidade()));
            holder.quantidade.setText(String.valueOf(items.getQuantidade()));

        }
        return rowView;
    }
}
