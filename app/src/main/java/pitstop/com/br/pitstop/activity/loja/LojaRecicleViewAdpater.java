package pitstop.com.br.pitstop.activity.loja;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.model.Loja;

/**
 * Created by wilso on 23/12/2017.
 */

public class LojaRecicleViewAdpater extends RecyclerView.Adapter<LojaRecicleViewAdpater.ViewHolder> {
    private List<Loja> lojas;
    private static LojaRecicleViewAdpater.ItemClickListener itemClickListener;
    Context contexto;

    public void setOnItemClickListener(LojaRecicleViewAdpater.ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
        this.contexto = contexto;
    }

    public LojaRecicleViewAdpater(List<Loja> lojas, Context context) {
        this.lojas = lojas;
        this.contexto=context;
    }

    @Override
    public LojaRecicleViewAdpater.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_loja_row, viewGroup, false);
        return new LojaRecicleViewAdpater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LojaRecicleViewAdpater.ViewHolder viewHolder, final int i) {

        final Loja l = lojas.get(i);
        if(l.getSincronizado()==0) {
            viewHolder.loja_sincronizado.setText("Desincronizado");
            viewHolder.loja_sincronizado.setBackgroundResource((R.drawable.oval_desincronizado));

        }else
        {
            viewHolder.loja_sincronizado.setText("Sincronizado");
            viewHolder.loja_sincronizado.setBackgroundResource((R.drawable.oval));

        }

        viewHolder.loja_nome.setText(lojas.get(i).getNome());
        viewHolder.loja_endereco.setText(lojas.get(i).getEndereco());
        viewHolder.loja_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("Produto", l.getNome());
                Intent intentVaiProFormulario = new Intent(contexto, CadastroLojaActivity.class);
                intentVaiProFormulario.putExtra("lojaId", l.getId());
                contexto.startActivity(intentVaiProFormulario);
            }
        }) ;


    }

    @Override
    public int getItemCount() {
        return lojas.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView loja_nome,  loja_sincronizado,loja_endereco;
        private ImageButton loja_editar;



     public ViewHolder(View view) {
            super(view);
            loja_sincronizado = (TextView)view.findViewById(R.id.sincronizado);
            loja_editar = (ImageButton) view.findViewById(R.id.editar);
            loja_nome = (TextView) view.findViewById(R.id.loja);
            loja_endereco = (TextView) view.findViewById(R.id.endereco);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener != null) {
                itemClickListener.onItemClick(getAdapterPosition());
            }
//            Log.d(TAG, "Elemento " + getAdapterPosition() + " clicado.");
        }


    }

    public interface ItemClickListener {

        void onItemClick(int position);
    }




}
