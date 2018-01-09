package pitstop.com.br.pitstop.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.SignupActivity;
import pitstop.com.br.pitstop.adapter.UsuarioRecicleViewAdapter;
import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizarListaUsuarioEvent;
import pitstop.com.br.pitstop.model.Usuario;

public class ListarUsuarioFragment extends Fragment {

    Context context;
    private RecyclerView recyclerView;
    private UsuarioRecicleViewAdapter usuarioRecicleViewAdapter;


    List<Usuario> usuarios = new ArrayList<>();
    Button novoUsuario;


    public ListarUsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_listar_usuario, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_usuario_view);
        recyclerView.setHasFixedSize(true);
        usuarioRecicleViewAdapter = new UsuarioRecicleViewAdapter(getLayoutInflater(savedInstanceState),usuarios, context);
        recyclerView.setAdapter(usuarioRecicleViewAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        carregaLista();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        novoUsuario = (Button) view.findViewById(R.id.novo_usuario);
        EventBus eventBus = EventBus.getDefault();
        eventBus.register(this);

        novoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentVaiProFormulario = new Intent(getActivity(), SignupActivity.class);
                startActivity(intentVaiProFormulario);

            }
        });


        carregaLista();


    }

    private void carregaLista() {
        UsuarioDAO usuarioDAO = new UsuarioDAO(context);
        usuarios.clear();
        usuarios.addAll(usuarioDAO.listarUsuarios());
        usuarioDAO.close();
        usuarioRecicleViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = getContext();

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void AtualizarListaUsuarioEvent(AtualizarListaUsuarioEvent event) {
        carregaLista();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
