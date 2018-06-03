package pitstop.com.br.pitstop.activity.movimentacaoproduto;

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
import pitstop.com.br.pitstop.model.MovimentacaoProduto;

/**
 * Created by wilso on 14/11/2017.
 */

public class LstViewTabelaMovimentacaoAdapter extends ArrayAdapter<MovimentacaoProduto> {
    int groupid;
    List<MovimentacaoProduto> item_list;
    ArrayList<String> desc;
    Context context;
    public LstViewTabelaMovimentacaoAdapter(Context context, int vg, int id, List<MovimentacaoProduto> item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }

    static class ViewHolder {
        public TextView de;
        public TextView para;
        public TextView quantidade;
        public TextView data;
        public TextView nome;


    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            LstViewTabelaMovimentacaoAdapter.ViewHolder viewHolder = new LstViewTabelaMovimentacaoAdapter.ViewHolder();

            viewHolder.de= (TextView) rowView.findViewById(R.id.de);
            viewHolder.para= (TextView) rowView.findViewById(R.id.para);
            viewHolder.quantidade= (TextView) rowView.findViewById(R.id.quantidade);
            viewHolder.nome= (TextView) rowView.findViewById(R.id.nome);
            viewHolder.data= (TextView) rowView.findViewById(R.id.data);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        MovimentacaoProduto items=item_list.get(position);
        if(items!=null) {
            LstViewTabelaMovimentacaoAdapter.ViewHolder holder = (LstViewTabelaMovimentacaoAdapter.ViewHolder) rowView.getTag();
            LojaDAO lojaDAO = new LojaDAO(context);
            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            holder.de.setText(lojaDAO.procuraPorId(items.getIdLojaDe()).getNome());
            lojaDAO.close();
            holder.para.setText(lojaDAO.procuraPorId(items.getIdLojaPara()).getNome());
            lojaDAO.close();
            holder.quantidade.setText(String.valueOf(items.getQuantidade()));
            holder.nome.setText(String.valueOf(produtoDAO.procuraPorId(items.getIdProduto()).getNome()));
            produtoDAO.close();
            String data = Util.dataNoformatoBrasileiro((items.getData()));
            holder.data.setText(String.valueOf(data));

        }
        return rowView;
    }
}
