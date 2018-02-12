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
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Venda;
import pitstop.com.br.pitstop.model.VendaEntradaProduto;
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
        public TextView total;
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
            viewHolder.total = (TextView) rowView.findViewById(R.id.total);
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
            holder.lucro.setText(String.valueOf(items.getLucro()));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = null;
            try {
                d = format.parse(items.getDataDaVenda());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.data.setText(String.valueOf(format.format(d)));
            //essa é uma parte que deve ser mudada, como o sistema
            // a estava em producao e nao tinha a forma de pagamento
            // "dinheiro e cartao" entao a variavel total representava
            // a venda tanto no cartao como em dinheiro, porem foi acrescentado
            // essa nova forma de venda e por isso uma nova variavel total foi adicionada
            // (Total Cartao) que so é usada para essa nova forma de pagamento, o total continua representando o
            // total da venda tanto no cartao como em dinheiro mas quando a venda é em cartao em dinheiro o total
            // representa a venda em dinheior e o total cartao representa a venda em cartao
            if (items.getFormaDePagamento().equals("cartao")) {
                holder.total.setText("R$ " + String.valueOf("0"));
                holder.totalCartao.setText("R$ " + String.valueOf(items.getTotal()));
            } else {
                holder.total.setText("R$ " + String.valueOf(items.getTotal()));
                holder.totalCartao.setText("R$ " + String.valueOf(items.getTotalCartao()));
            }
        }
        return rowView;
    }
}
