package pitstop.com.br.pitstop.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import dmax.dialog.SpotsDialog;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.adapter.ProdutoRecicleViewAdapter;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;
import pitstop.com.br.pitstop.sic.ObjetosSinkSincronizador;


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
//                        carregaLista();
//                        bus.post(new AtualizaListaProdutoEvent());
//                        bus.post(new AtualizaListaLojasEvent());
//                        spinnerLoja.setSelection(0);
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
        bus.register(this);
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
//        spinnerLoja.setSelection(0);

//        carregaLista();
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
        Intent intent = getActivity().getIntent();
        lojaVindaDaTelaListaLoja = (Loja) intent.getSerializableExtra("loja");
        intent.removeExtra("loja");
        ArrayAdapter<String> spinnerAdapterDe = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, labelsLojas);
        spinnerAdapterDe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoja.setAdapter(spinnerAdapterDe);

        spinnerLoja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (lojaVindaDaTelaListaLoja == null) {
                    if (i == 0) {
                        produtos.clear();
                        produtos.addAll(produtoDAO.listarProdutos());
                        recyclerView.setAdapter(produtoRecicleViewAdapter);
                    } else {
                        produtos.clear();
                        produtos.addAll(produtoDAO.procuraPorLoja(lojas.get(i - 1)));
                        recyclerView.setAdapter(produtoRecicleViewAdapter);
                    }
                } else {
                    int k = 1;
                    for (Loja loja : lojas) {
                        if (loja.getNome().toLowerCase().equals(lojaVindaDaTelaListaLoja.getNome().toLowerCase())) {
                            spinnerLoja.setSelection(k);
                            break;
                        }
                        k++;
                    }
                    produtos.clear();
                    produtos.addAll(produtoDAO.procuraPorLoja(lojaVindaDaTelaListaLoja));
                    lojaVindaDaTelaListaLoja = null;
                }
                produtoDAO.close();
                produtoRecicleViewAdapter.notifyDataSetChanged();
                //recyclerView.getRecycledViewPool().clear();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//


        novoProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                auth.signOut();
                //comentar para testar o webservice
                Intent intentVaiProFormulario = new Intent(getActivity(), CadastroProdutoActivity.class);
                startActivity(intentVaiProFormulario);

            }
        });


    }


    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        pesquisa.clear();

        for (int i = 0; i < produtos.size(); i++) {

            if (produtos.get(i).getNome().toLowerCase().contains(txtPesquisa.toLowerCase()))
                pesquisa.add(produtos.get(i));

        }
    }

    @Override
    public void onDestroy() {
        // Unregister
        bus.unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void atualizaListaProdutoEvent(AtualizaListaProdutoEvent event) {
        carregaLista();
    }

    private void carregaLista() {
        CarregandoListaDeProduto carregandoListaDeProduto = new CarregandoListaDeProduto(context);
        carregandoListaDeProduto.execute();

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
        adapterPesquisa.notifyDataSetChanged();
//        recyclerView.getRecycledViewPool().clear();

        return false;
    }

    public class CarregandoListaDeProduto extends AsyncTask<Void, Void, String> {
        private Context context;
        AlertDialog dialog;

        public CarregandoListaDeProduto(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new SpotsDialog(context, "Carregando Lista de Produtos", R.style.progressDialog);

            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            produtos.clear();
            Usuario user = usuarioPreferences.getUsuario();
            if (usuarioPreferences.getUsuario().getRole().equals("Administrador")) {
                produtos.addAll(produtoDAO.listarProdutos());
            } else {
                produtos.addAll(produtoDAO.procuraPorLoja(usuarioPreferences.getLoja()));
            }
            produtoDAO.close();

            String resposta1 = "Lista de produtos carregado";
            return resposta1;
        }

        @Override
        protected void onPostExecute(String resposta1) {
            dialog.dismiss();
            Toast.makeText(context, resposta1, Toast.LENGTH_LONG).show();
            swipe.setRefreshing(false);
            spinnerLoja.setSelection(0);
            recyclerView.setAdapter(produtoRecicleViewAdapter);
            produtoRecicleViewAdapter.notifyDataSetChanged();
//            recyclerView.getRecycledViewPool().clear();

        }
    }
}
