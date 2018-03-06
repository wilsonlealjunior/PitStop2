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
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.ItemVenda;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

/**
 * Created by wilso on 29/11/2017.
 */

public class LstViewTabelaDescricaoVendaAdapter extends ArrayAdapter<ItemVenda> {
    int groupid;
    List<ItemVenda> item_list;
    ArrayList<String> desc;
    Context context;
    public LstViewTabelaDescricaoVendaAdapter(Context context, int vg, int id, List<ItemVenda> item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }

    static class ViewHolder {
        public TextView produto;
        public TextView quantidade;
        public TextView lucro;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            LstViewTabelaDescricaoVendaAdapter.ViewHolder viewHolder = new LstViewTabelaDescricaoVendaAdapter.ViewHolder();
            viewHolder.produto= (TextView) rowView.findViewById(R.id.produto);
            viewHolder.quantidade= (TextView) rowView.findViewById(R.id.quantidade);
            viewHolder.lucro= (TextView) rowView.findViewById(R.id.lucro);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        Log.i("count", String.valueOf(item_list.size()));

        ItemVenda items=item_list.get(position);
        if(items!=null) {

            LstViewTabelaDescricaoVendaAdapter.ViewHolder holder = (LstViewTabelaDescricaoVendaAdapter.ViewHolder) rowView.getTag();

            EntradaProdutoDAO entradaProdutoDAO = new EntradaProdutoDAO(context);
            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            Produto produto = produtoDAO.procuraPorId(items.getIdProduto());
            produtoDAO.close();
            EntradaProduto ep = entradaProdutoDAO.procuraPorId(items.getIdEntradaProduto());
            entradaProdutoDAO.close();
            holder.produto.setText(produto.getNome());
            UsuarioPreferences up = new UsuarioPreferences(context);
            if(up.temUsuario()) {
                if (up.getUsuario().getRole().equals("Funcionario")) {
                    holder.lucro.setText(Util.moedaNoFormatoBrasileiro((produto.getPreco())*items.getQuantidadeVendida()));

                }
                else{
                    holder.lucro.setText(Util.moedaNoFormatoBrasileiro((produto.getPreco()-ep.getPrecoDeCompra())*items.getQuantidadeVendida()));
                }
            }
           holder.quantidade.setText(String.valueOf(items.getQuantidadeVendida()));


        }
        return rowView;
    }
}
