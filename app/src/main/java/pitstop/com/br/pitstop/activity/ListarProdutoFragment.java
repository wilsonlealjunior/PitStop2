package pitstop.com.br.pitstop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.adapter.ProdutoRecicleViewAdapter;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;


import android.app.Activity;
import android.widget.Button;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;


public class ListarProdutoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProdutoRecicleViewAdapter produtoRecicleViewAdapter;
    private SwipeRefreshLayout swipe;
    Context context;
    ArrayList<Produto> produtos = new ArrayList<>();
    ProdutoRecicleViewAdapter adapterPesquisa;
    Button novoProduto;
    ArrayList<Produto> pesquisa = new ArrayList<>();
    private SearchView textPesquisaProduto;
    Loja lojaVindaDaTelaListaLoja;
    UsuarioPreferences usuarioPreferences;


    public ListarProdutoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_produto, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_produo_view);
        recyclerView.setHasFixedSize(true);
        produtoRecicleViewAdapter = new ProdutoRecicleViewAdapter(produtos, context);
        adapterPesquisa = new ProdutoRecicleViewAdapter(pesquisa, context);
        recyclerView.setAdapter(produtoRecicleViewAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_lista_produto);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                carregaLista();
            }
        });

        produtoRecicleViewAdapter.setOnItemClickListener(new ProdutoRecicleViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("teste para pegar", "Elemento " + position + " clicado.");
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
        textPesquisaProduto = (SearchView) view.findViewById(R.id.text_pesquisa_produto);
        novoProduto = (Button) view.findViewById(R.id.novo_produto);
        usuarioPreferences = new UsuarioPreferences(context);
        Usuario usuario = usuarioPreferences.getUsuario();
        if (usuario.getRole().equals("Funcionario")) {
            novoProduto.setVisibility(View.GONE);
        }

        EventBus eventBus = EventBus.getDefault();
        eventBus.register(this);


        Intent intent = getActivity().getIntent();
        lojaVindaDaTelaListaLoja = (Loja) intent.getSerializableExtra("loja");
        intent.removeExtra("loja");

        textPesquisaProduto.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                pesquisar(searchQuery.toString().trim());


                recyclerView.setAdapter(adapterPesquisa);
                adapterPesquisa.notifyDataSetChanged();

//                textPesquisa.invalidate();
                return true;
            }
        });

        novoProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                auth.signOut();
                //comentar para testar o webservice
                Intent intentVaiProFormulario = new Intent(getActivity(), CadastroProdutoActivity.class);
                startActivity(intentVaiProFormulario);

            }
        });

        carregaLista();


    }


    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        pesquisa.clear();

        for (int i = 0; i < produtos.size(); i++) {
            if (textlength <= produtos.get(i).getNome().length()) {
                if (txtPesquisa.equalsIgnoreCase((String) produtos.get(i).getNome().subSequence(0, textlength))) {
                    pesquisa.add(produtos.get(i));
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void atualizaListaProdutoEvent(AtualizaListaProdutoEvent event) {
        carregaLista();
    }

    private void carregaLista() {
        ProdutoDAO produtoDAO = new ProdutoDAO(context);
        produtos.clear();
        Usuario user = usuarioPreferences.getUsuario();
        if (usuarioPreferences.getUsuario().getRole().equals("Administrador")) {
            if (lojaVindaDaTelaListaLoja == null) {
                produtos.addAll(produtoDAO.listarProdutos());
            } else {
                produtos.addAll(produtoDAO.procuraPorLoja(lojaVindaDaTelaListaLoja));
            }
        } else {
            produtos.addAll(produtoDAO.procuraPorLoja(usuarioPreferences.getLoja()));

        }
        Collections.sort(produtos);


        produtoDAO.close();
        produtoRecicleViewAdapter.notifyDataSetChanged();
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
