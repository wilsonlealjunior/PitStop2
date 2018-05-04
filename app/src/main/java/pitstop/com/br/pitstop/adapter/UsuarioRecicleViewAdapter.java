package pitstop.com.br.pitstop.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.greenrobot.event.EventBus;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.SignupActivity;
import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.event.AtualizarListaUsuarioEvent;
import pitstop.com.br.pitstop.model.Usuario;

/**
 * Created by wilso on 29/12/2017.
 */

public class UsuarioRecicleViewAdapter extends RecyclerView.Adapter<UsuarioRecicleViewAdapter.ViewHolder> {
    private List<Usuario> usuarios;
    private static UsuarioRecicleViewAdapter.ItemClickListener itemClickListener;
    Context contexto;
    EventBus bus = EventBus.getDefault();

    public void setOnItemClickListener(UsuarioRecicleViewAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.contexto = contexto;
    }

    public UsuarioRecicleViewAdapter( List<Usuario> usuarios, Context context) {
        this.usuarios = usuarios;
        this.contexto = context;
    }

    @Override
    public UsuarioRecicleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_usuario_row, viewGroup, false);
        return new UsuarioRecicleViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UsuarioRecicleViewAdapter.ViewHolder viewHolder, final int i) {

        final Usuario u = usuarios.get(i);
        viewHolder.usuario_nome.setText(usuarios.get(i).getNome());
        viewHolder.usuario_excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(contexto, R.style.DialogTheme);
                LayoutInflater inflater = LayoutInflater.from(contexto);
                View dialogView = inflater.inflate(R.layout.card_avisos, null);
                dialogBuilder.setView(dialogView);

                TextView titulo = (TextView) dialogView.findViewById(R.id.titulo);
                TextView mensagem = (TextView) dialogView.findViewById(R.id.mensagem);
                Button positivo = (Button) dialogView.findViewById(R.id.positivo);
                Button negativo = (Button) dialogView.findViewById(R.id.negativo);
                mensagem.setText("Deseja excluir o usuario  "+u.getNome()+" ?");
                titulo.setText("Confirmação de exclusão");

                final AlertDialog alertDialog = dialogBuilder.create();
                Window window = alertDialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER); // set alert dialog in center
                alertDialog.setCancelable(false);

                negativo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        alertDialog.dismiss();
                    }
                });
                positivo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UsuarioDAO usuarioDAO = new UsuarioDAO(contexto);
                        u.setDesativado(1);
                        u.desincroniza();
                        usuarioDAO.altera(u);
                        usuarioDAO.close();
                        alertDialog.hide();
                        alertDialog.dismiss();
                        bus.post(new AtualizarListaUsuarioEvent());
                    }
                });
                alertDialog.show();



            }
        });
        viewHolder.usuario_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("Produto", l.getNome());
                Intent intentVaiProFormulario = new Intent(contexto, SignupActivity.class);
                intentVaiProFormulario.putExtra("usuario", u);
                contexto.startActivity(intentVaiProFormulario);
            }
        });


    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView usuario_nome;
        private ImageButton usuario_editar, usuario_excluir;


        public ViewHolder(View view) {
            super(view);
            usuario_editar = (ImageButton) view.findViewById(R.id.editar);
            usuario_nome = (TextView) view.findViewById(R.id.funcionario);
            usuario_excluir = (ImageButton) view.findViewById(R.id.excluir);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getAdapterPosition());
            }
//            Log.d(TAG, "Elemento " + getAdapterPosition() + " clicado.");
        }


    }

    public interface ItemClickListener {

        void onItemClick(int position);
    }


}

