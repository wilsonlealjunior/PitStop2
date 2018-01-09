package pitstop.com.br.pitstop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.adapter.LojaRecicleViewAdpater;
import pitstop.com.br.pitstop.adapter.ProdutoRecicleViewAdapter;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;


import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ListarLojaFragment extends Fragment {

    Context context;
    private RecyclerView recyclerView;
    private LojaRecicleViewAdpater lojaRecicleViewAdpater;
    private SwipeRefreshLayout swipe;


    List<Loja> lojas = new ArrayList<>();
    LojaRecicleViewAdpater adapterPesquisa;
    Button novaLoja;
    ArrayList<Loja> pesquisa = new ArrayList<>();
    private SearchView textPesquisaLoja;


    public ListarLojaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_loja, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_loja_view);
        recyclerView.setHasFixedSize(true);
        lojaRecicleViewAdpater = new LojaRecicleViewAdpater(lojas, context);
        adapterPesquisa = new LojaRecicleViewAdpater(pesquisa, context);
        recyclerView.setAdapter(lojaRecicleViewAdpater);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_lista_loja);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                carregaLista();
            }
        });

        lojaRecicleViewAdpater.setOnItemClickListener(new LojaRecicleViewAdpater.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intentVaiProFormulario = getActivity().getIntent();
                Loja l = lojas.get(position);
                intentVaiProFormulario.putExtra("loja", l);
                Fragment fragment = new ListarProdutoFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();

                // set the toolbar title
//                getSupportActionBar().setTitle(item.getTitle().toString());
//                startActivity(intentVaiProFormulario);
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        carregaLista();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        textPesquisaLoja = (SearchView) view.findViewById(R.id.text_pesquisa_loja);
        novaLoja = (Button) view.findViewById(R.id.nova_loja);
        textPesquisaLoja.setQueryHint("Digite sua busca aqui");


        EventBus eventBus = EventBus.getDefault();
        eventBus.register(this);


        adapterPesquisa = new LojaRecicleViewAdpater(pesquisa, context);


        textPesquisaLoja.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                pesquisar(searchQuery.toString().trim());


                recyclerView.setAdapter(adapterPesquisa);

//                textPesquisa.invalidate();
                return true;
            }
        });

        novaLoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentVaiProFormulario = new Intent(getActivity(), CadastroLojaActivity.class);
                startActivity(intentVaiProFormulario);

            }
        });


        carregaLista();


    }


    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        pesquisa.clear();

        for (int i = 0; i < lojas.size(); i++) {
            if (textlength <= lojas.get(i).getNome().length()) {
                if (txtPesquisa.equalsIgnoreCase((String) lojas.get(i).getNome().subSequence(0, textlength))) {
                    pesquisa.add(lojas.get(i));
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void atualizaListaLojaEvent(AtualizaListaLojasEvent event) {
        carregaLista();
    }


    private void carregaLista() {
        LojaDAO lojaDAO = new LojaDAO(context);
        lojas.clear();
        lojas.addAll(lojaDAO.listarLojas());
        lojaDAO.close();
        lojaRecicleViewAdpater.notifyDataSetChanged();
        swipe.setRefreshing(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = getContext();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
