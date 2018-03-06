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

/**
 * Created by wilso on 29/11/2017.
 */

public class LstViewTabelaRelatorioAvaria extends ArrayAdapter<Avaria> {
    int groupid;
    List<Avaria> item_list;
    ArrayList<String> desc;
    Context context;

    public LstViewTabelaRelatorioAvaria(Context context, int vg, int id, List<Avaria> item_list) {
        super(context, vg, id, item_list);
        this.context = context;
        groupid = vg;
        this.item_list = item_list;

    }

    static class ViewHolder {
        public TextView loja;
        public TextView data;
        public TextView prejuizo;
        public TextView produto;


    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            LstViewTabelaRelatorioAvaria.ViewHolder viewHolder = new LstViewTabelaRelatorioAvaria.ViewHolder();

            viewHolder.data = (TextView) rowView.findViewById(R.id.data);
            viewHolder.loja = (TextView) rowView.findViewById(R.id.loja);
            viewHolder.prejuizo = (TextView) rowView.findViewById(R.id.prejuizo);
            viewHolder.produto = (TextView) rowView.findViewById(R.id.produto);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        Avaria items = item_list.get(position);
        if (items != null) {
            LstViewTabelaRelatorioAvaria.ViewHolder holder = (LstViewTabelaRelatorioAvaria.ViewHolder) rowView.getTag();

            LojaDAO lojaDAO = new LojaDAO(context);
            ProdutoDAO produtoDAO = new ProdutoDAO(context);

            holder.data.setText(Util.dataNoformatoBrasileiro(Util.converteDoFormatoSQLParaDate(items.getData())));
            holder.prejuizo.setText(Util.moedaNoFormatoBrasileiro(items.getPrejuizo()));
            holder.produto.setText(produtoDAO.procuraPorId(items.getIdProduto()).getNome());
            produtoDAO.close();
            holder.loja.setText(lojaDAO.procuraPorId(items.getIdLoja()).getNome());
            lojaDAO.close();


        }
        return rowView;
    }
}
