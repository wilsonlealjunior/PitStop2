package pitstop.com.br.pitstop.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.model.Venda;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

/**
 * Created by wilso on 22/11/2017.
 */

public class LstViewTabelaRelatorioVendas extends ArrayAdapter<Venda> {
    int groupid;
    List<Venda> item_list;
    ArrayList<String> desc;
    Context context;

    public LstViewTabelaRelatorioVendas(Context context, int vg, int id, List<Venda> item_list) {
        super(context, vg, id, item_list);
        this.context = context;
        groupid = vg;
        this.item_list = item_list;

    }

    static class ViewHolder {
        public TextView data;
        public TextView totalDinheiro;
        public TextView totalCartao;
        public TextView lucro;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            LstViewTabelaRelatorioVendas.ViewHolder viewHolder = new LstViewTabelaRelatorioVendas.ViewHolder();
            viewHolder.data = (TextView) rowView.findViewById(R.id.data);
            viewHolder.totalDinheiro = (TextView) rowView.findViewById(R.id.total_dinheiro);
            viewHolder.lucro = (TextView) rowView.findViewById(R.id.lucro);
            viewHolder.totalCartao = (TextView) rowView.findViewById(R.id.total_cartao);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        Venda items = item_list.get(position);
        if (items != null) {

            LstViewTabelaRelatorioVendas.ViewHolder holder = (LstViewTabelaRelatorioVendas.ViewHolder) rowView.getTag();
            UsuarioPreferences up = new UsuarioPreferences(context);
            if (up.temUsuario()) {
                if (up.getUsuario().getRole().equals("Funcionario")) {
                    holder.lucro.setVisibility(View.GONE);
                }
            }
            holder.lucro.setText((Util.moedaNoFormatoBrasileiro(items.getLucro())));
            Date d = null;
            d = Util.converteDoFormatoSQLParaDate(items.getDataDaVenda());
            holder.data.setText(Util.dataNoformatoBrasileiro(d));
            holder.totalDinheiro.setText(Util.moedaNoFormatoBrasileiro(items.getTotalDinheiro()));
            holder.totalCartao.setText(Util.moedaNoFormatoBrasileiro((items.getTotalCartao())));


        }
        return rowView;
    }
}
