package pitstop.com.br.pitstop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.adapter.ProdutoRecicleViewAdapter;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.preferences.ObjetosSinkPreferences;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;
import pitstop.com.br.pitstop.sic.ObjetosSinkSincronizador;


import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class ListarProdutoFragment extends Fragment implements SearchView.OnQueryTextListener {

    Spinner spinnerLoja;
    CardView cardViewSpinerLoja;
    private RecyclerView recyclerView;
    private ProdutoRecicleViewAdapter produtoRecicleViewAdapter;
    private SwipeRefreshLayout swipe;
    Context context;
    List<Produto> produtos = new ArrayList<>();
    ProdutoRecicleViewAdapter adapterPesquisa;
    Button novoProduto;
    List<Produto> pesquisa = new ArrayList<>();
    Loja lojaVindaDaTelaListaLoja;
    UsuarioPreferences usuarioPreferences;
    Toolbar toolbar;
    SearchView searchView;
    ProdutoDAO produtoDAO;
    List<String> labelsLojas = new ArrayList<>();
    List<Loja> lojas = new ArrayList<>();
    LojaDAO lojaDAO;
    ObjetosSinkSincronizador objetosSinkSincronizador;
    EventBus bus = EventBus.getDefault();


    public ListarProdutoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    private void setUpToolbar() {
        toolbar.inflateMenu(R.menu.menu_sinc);
        toolbar.setTitle("Listagem de produtos");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sincronizar_dados:
                        objetosSinkSincronizador.buscaTodos();
                        bus.post(new AtualizaListaProdutoEvent());
                        bus.post(new AtualizaListaLojasEvent());

                        break;
                }
                return false;
            }
        });


    }

    public void setupSearchView() {
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Pesquisar...");
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setUpToolbar();
        setupSearchView();
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
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);


//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        objetosSinkSincronizador = new ObjetosSinkSincronizador(context);
        cardViewSpinerLoja = view.findViewById(R.id.card_view_do_spinner_loja);
        produtoDAO = new ProdutoDAO(context);
        lojaDAO = new LojaDAO(context);
        spinnerLoja = (Spinner) view.findViewById(R.id.spinner_loja);
        novoProduto = (Button) view.findViewById(R.id.novo_produto);
        usuarioPreferences = new UsuarioPreferences(context);
        Usuario usuario = usuarioPreferences.getUsuario();
        if (usuario.getRole().equals("Funcionario")) {
            novoProduto.setVisibility(View.GONE);
            cardViewSpinerLoja.setVisibility(View.GONE);
        }

        lojas = lojaDAO.listarLojas();
        labelsLojas.add("Todas");
        for (Loja loja : lojas) {
            labelsLojas.add(loja.getNome());
        }
        ArrayAdapter<String> spinnerAdapterDe = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, labelsLojas);
        spinnerAdapterDe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoja.setAdapter(spinnerAdapterDe);
        spinnerLoja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    produtos.clear();
                    produtos.addAll(produtoDAO.listarProdutos());
                } else {
                    produtos.clear();
                    produtos.addAll(produtoDAO.procuraPorLoja(lojas.get(i - 1)));
                }
                Collections.sort(produtos);
                produtoDAO.close();
                produtoRecicleViewAdapter.notifyDataSetChanged();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        EventBus eventBus = EventBus.getDefault();
        eventBus.register(this);


        Intent intent = getActivity().getIntent();
        lojaVindaDaTelaListaLoja = (Loja) intent.getSerializableExtra("loja");
        intent.removeExtra("loja");


        novoProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                auth.signOut();
                //comentar para testar o webservice
                Intent intentVaiProFormulario = new Intent(getActivity(), CadastroProdutoActivity.class);
                startActivity(intentVaiProFormulario);

            }
        });


//        carregaLista();


    }


    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        pesquisa.clear();
//        pesquisa = produtoDAO.listaProdutosPorParteDoNome(txtPesquisa);
//        adapterPesquisa  = new ProdutoRecicleViewAdapter(pesquisa, context); ;

        for (int i = 0; i < produtos.size(); i++) {
//            String nomeProduto = produtos.get(i).getNome().trim();
//            nomeProduto = produtos.get(i).getNome().replaceAll("\\s\\s+", " ");
//            String[] nomes = nomeProduto.split(" ");
//            if (textlength <= produtos.get(i).getNome().length()) {
//                if (txtPesquisa.equalsIgnoreCase((String) nomes[1].subSequence(0, textlength)) || txtPesquisa.equalsIgnoreCase((String) nomes[2].subSequence(0, textlength)) || txtPesquisa.equalsIgnoreCase((String) produtos.get(i).getNome().subSequence(0, textlength))) {
            if (produtos.get(i).getNome().toLowerCase().contains(txtPesquisa.toLowerCase()))
                pesquisa.add(produtos.get(i));
//                }
//            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void atualizaListaProdutoEvent(AtualizaListaProdutoEvent event) {
        carregaLista();
    }

    private void carregaLista() {
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        pesquisar(newText.toString().trim());
        recyclerView.setAdapter(adapterPesquisa);
        return false;
    }
}
