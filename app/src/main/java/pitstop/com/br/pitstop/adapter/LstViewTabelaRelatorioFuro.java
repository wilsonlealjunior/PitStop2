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
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.model.Avaria;
import pitstop.com.br.pitstop.model.Furo;

/**
 * Created by wilso on 15/12/2017.
 */

public class LstViewTabelaRelatorioFuro extends ArrayAdapter<Furo> {
    int groupid;
    List<Furo> item_list;
    ArrayList<String> desc;
    Context context;
    public LstViewTabelaRelatorioFuro(Context context, int vg, int id, List<Furo> item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }

    static class ViewHolder {
        public TextView loja;
        public TextView data;
        public TextView valor;
        public TextView usuario;
        public TextView quantidade;




    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            LstViewTabelaRelatorioFuro.ViewHolder viewHolder = new LstViewTabelaRelatorioFuro.ViewHolder();

            viewHolder.data= (TextView) rowView.findViewById(R.id.data);
            viewHolder.loja = (TextView) rowView.findViewById(R.id.loja);
            viewHolder.valor= (TextView) rowView.findViewById(R.id.valor);
            viewHolder.usuario=(TextView) rowView.findViewById(R.id.usuario);
            viewHolder.quantidade=(TextView) rowView.findViewById(R.id.quantidade);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        Furo items=item_list.get(position);
        if(items!=null) {
            LstViewTabelaRelatorioFuro.ViewHolder holder = (LstViewTabelaRelatorioFuro.ViewHolder) rowView.getTag();

            LojaDAO lojaDAO = new LojaDAO(context);

            holder.data.setText(items.getData());
            holder.usuario.setText(String.valueOf(items.getIdUsuario()));
            holder.loja.setText(lojaDAO.procuraPorId(items.getIdLoja()).getNome());
            lojaDAO.close();
            holder.valor.setText(String.valueOf(items.getValor()));



        }
        return rowView;
    }
}
