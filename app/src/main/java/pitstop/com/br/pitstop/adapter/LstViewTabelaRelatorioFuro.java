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
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.model.Furo;

/**
 * Created by wilso on 15/12/2017.
 */

public class LstViewTabelaRelatorioFuro extends ArrayAdapter<Furo> {
    int groupid;
    List<Furo> item_list;
    ArrayList<String> desc;
    Context context;

    public LstViewTabelaRelatorioFuro(Context context, int vg, int id, List<Furo> item_list) {
        super(context, vg, id, item_list);
        this.context = context;
        groupid = vg;
        this.item_list = item_list;

    }

    static class ViewHolder {
        public TextView data;
        public TextView valor;
        public TextView usuario;
        public TextView produto;


    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            LstViewTabelaRelatorioFuro.ViewHolder viewHolder = new LstViewTabelaRelatorioFuro.ViewHolder();

            viewHolder.data = (TextView) rowView.findViewById(R.id.data);
            viewHolder.valor = (TextView) rowView.findViewById(R.id.valor);
            viewHolder.usuario = (TextView) rowView.findViewById(R.id.usuario);
            viewHolder.produto = (TextView) rowView.findViewById(R.id.produto);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        Furo items = item_list.get(position);
        if (items != null) {
            LstViewTabelaRelatorioFuro.ViewHolder holder = (LstViewTabelaRelatorioFuro.ViewHolder) rowView.getTag();
            ProdutoDAO produtoDAO = new ProdutoDAO(context);

            holder.data.setText(Util.dataNoformatoBrasileiro((items.getData())));
            holder.usuario.setText(items.getIdUsuario());

            holder.valor.setText(Util.moedaNoFormatoBrasileiro(items.getValor()));
            if(items.getIdProduto()!=null) {
                holder.produto.setText(produtoDAO.procuraPorId(items.getIdProduto()).getNome());
            }
            produtoDAO.close();


        }
        return rowView;
    }
}
