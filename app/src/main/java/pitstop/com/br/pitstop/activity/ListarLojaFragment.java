package pitstop.com.br.pitstop.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import dmax.dialog.SpotsDialog;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.cadastro.CadastroLojaActivity;
import pitstop.com.br.pitstop.adapter.LojaRecicleViewAdpater;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.event.AtualizaListaLojasEvent;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.sic.ObjetosSinkSincronizador;


import android.app.Activity;

import android.widget.Button;

import android.support.v7.widget.SearchView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


public class ListarLojaFragment extends Fragment implements SearchView.OnQueryTextListener {

    Context context;
    private RecyclerView recyclerView;
    private LojaRecicleViewAdpater lojaRecicleViewAdpater;
    private SwipeRefreshLayout swipe;
    Toolbar toolbar;
    SearchView searchView;
    ObjetosSinkSincronizador objetosSinkSincronizador;
    EventBus bus = EventBus.getDefault();


    List<Loja> lojas = new ArrayList<>();
    LojaRecicleViewAdpater adapterPesquisa;
    Button novaLoja;
    ArrayList<Loja> pesquisa = new ArrayList<>();


    public ListarLojaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    private void setUpToolbar() {
        toolbar.inflateMenu(R.menu.menu_sinc);
        toolbar.setTitle("Listagem de Lojas");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sincronizar_dados:
                        objetosSinkSincronizador.buscaTodos();
//                        bus.post(new AtualizaListaProdutoEvent());
//                        bus.post(new AtualizaListaLojasEvent());
//                        carregaLista();

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
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);


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
//        carregaLista();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        objetosSinkSincronizador = new ObjetosSinkSincronizador(context);
        novaLoja = (Button) view.findViewById(R.id.nova_loja);


        adapterPesquisa = new LojaRecicleViewAdpater(pesquisa, context);


        novaLoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentVaiProFormulario = new Intent(getActivity(), CadastroLojaActivity.class);
                startActivity(intentVaiProFormulario);

            }
        });


//        carregaLista();


    }

    @Override
    public void onDestroy() {
        // Unregister
        bus.unregister(this);
        super.onDestroy();
    }


    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        pesquisa.clear();

        for (int i = 0; i < lojas.size(); i++) {
            if (lojas.get(i).getNome().toLowerCase().contains(txtPesquisa.toLowerCase()))
                pesquisa.add(lojas.get(i));
//            if (textlength <= lojas.get(i).getNome().length()) {
//                if (txtPesquisa.equalsIgnoreCase((String) lojas.get(i).getNome().subSequence(0, textlength))) {
//                    pesquisa.add(lojas.get(i));
//                }
//            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void atualizaListaLojaEvent(AtualizaListaLojasEvent event) {
        carregaLista();
    }


    private void carregaLista() {
        CarregandoListaDeLoja carregandoListaDeLoja = new CarregandoListaDeLoja(context);
        carregandoListaDeLoja.execute();
//        LojaDAO lojaDAO = new LojaDAO(context);
//        lojas.clear();
//        lojas.addAll(lojaDAO.listarLojas());
//        lojaDAO.close();
//        lojaRecicleViewAdpater.notifyDataSetChanged();
//        swipe.setRefreshing(false);
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

    public class CarregandoListaDeLoja extends AsyncTask<Void, Void, String> {
        private Context context;
        AlertDialog dialog;

        public CarregandoListaDeLoja(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new SpotsDialog(context, "Carregando Lista de Lojas", R.style.progressDialog);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            LojaDAO lojaDAO = new LojaDAO(context);
            lojas.clear();
            lojas.addAll(lojaDAO.listarLojas());
            lojaDAO.close();
            String resposta = "Lista de lojas carregada";
            return resposta;
        }

        @Override
        protected void onPostExecute(String resposta) {
            dialog.dismiss();
            Toast.makeText(context, resposta, Toast.LENGTH_LONG).show();
            swipe.setRefreshing(false);
            lojaRecicleViewAdpater.notifyDataSetChanged();
        }
    }
}
