package pitstop.com.br.pitstop.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.cadastro.CadastroMovimentacaoProdutoActivity;
import pitstop.com.br.pitstop.adapter.LstViewTabelaMovimentacaoAdapter;
import pitstop.com.br.pitstop.dao.MovimentacaoProdutoDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.event.AtualizaListaMovimentacaoProdutoEvent;
import pitstop.com.br.pitstop.model.MovimentacaoProduto;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.sic.MovimentacaoProdutoSincronizador;


public class ListarMovimentacaoProdutoFragment extends Fragment {
    MovimentacaoProdutoSincronizador movimentacaoProdutoSincronizador = new MovimentacaoProdutoSincronizador(getContext());
    Context context;
    ArrayList<MovimentacaoProduto> movimentacaoProdutos= new ArrayList<>();
    LstViewTabelaMovimentacaoAdapter adapter;
    LstViewTabelaMovimentacaoAdapter adapterPesquisa;
    Button novoMovimentacao;
    private ListView listaDeMovimentacoes;
    ArrayList<MovimentacaoProduto> pesquisa = new ArrayList<>();
    private SearchView textPesquisaMovimentacao;


    public ListarMovimentacaoProdutoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listar_movimentacao_produto, container, false);





        // Inflate the layout for this fragment
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        movimentacaoProdutoSincronizador.buscaTodos();
        carregaLista();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        textPesquisaMovimentacao= (SearchView) view.findViewById(R.id.text_pesquisa_movimentacao);
        novoMovimentacao = (Button)view.findViewById(R.id.novo_movimentacao);
        listaDeMovimentacoes = (ListView) view.findViewById(R.id.lista_de_movimentacao);
        ViewGroup headerView = (ViewGroup)getLayoutInflater(savedInstanceState).inflate(R.layout.header_movimentacao_produto, listaDeMovimentacoes,false);

        listaDeMovimentacoes.addHeaderView(headerView);

        EventBus eventBus = EventBus.getDefault();
        eventBus.register(this);
        adapter =new LstViewTabelaMovimentacaoAdapter(view.getContext(),R.layout.tabela_movimentacao_produto,R.id.de,movimentacaoProdutos);
        listaDeMovimentacoes.setAdapter(adapter);
        adapterPesquisa =new LstViewTabelaMovimentacaoAdapter(view.getContext(),R.layout.tabela_movimentacao_produto,R.id.de,pesquisa);

        //movimentacaoProdutoSincronizador.buscaTodos();

        textPesquisaMovimentacao.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                pesquisar(searchQuery.toString().trim());


                listaDeMovimentacoes.setAdapter(adapterPesquisa);

//                textPesquisa.invalidate();
                return true;
            }
        });

        novoMovimentacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v)  {

//                auth.signOut();
                //comentar para testar o webservice
                Intent intentVaiProFormulario = new Intent(getActivity(), CadastroMovimentacaoProdutoActivity.class);
                startActivity(intentVaiProFormulario);

            }
        });
        listaDeMovimentacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                MovimentacaoProduto movimentacaoProduto = (MovimentacaoProduto) listaDeMovimentacoes.getItemAtPosition(position);

                ProdutoDAO produtoDAO = new ProdutoDAO(context);
                Toast.makeText(getActivity(), "Produto = " + produtoDAO.procuraPorId(movimentacaoProduto.getIdProduto()), Toast.LENGTH_SHORT).show();
                produtoDAO.close();
            }
        });

        registerForContextMenu(listaDeMovimentacoes);
        carregaLista();


    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                MovimentacaoProduto movimentacaoProdutoproduto = (MovimentacaoProduto) listaDeMovimentacoes.getItemAtPosition(info.position);
                MovimentacaoProdutoDAO movimentacaoProdutoDAO= new MovimentacaoProdutoDAO(getContext());
                movimentacaoProdutoDAO.deleta(movimentacaoProdutoproduto);
                movimentacaoProdutoDAO.close();
                movimentacaoProdutos.remove(movimentacaoProdutoproduto);
                adapter.notifyDataSetChanged();
                ProdutoDAO produtoDAO = new ProdutoDAO(context);
                Toast.makeText(getActivity(), "Produto = " + produtoDAO.procuraPorId(movimentacaoProdutoproduto.getIdProduto()), Toast.LENGTH_SHORT).show();
                produtoDAO.close();
                return false;
            }
        });


    }

    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        pesquisa.clear();

        for (int i = 0; i < movimentacaoProdutos.size(); i++) {
            ProdutoDAO produtoDAO = new ProdutoDAO(context);
            Produto p = produtoDAO.procuraPorId(movimentacaoProdutos.get(i).getIdProduto());
            produtoDAO.close();
//            if (textlength <= p.getNome().length()) {
//                if (txtPesquisa.equalsIgnoreCase((String) p.getNome().subSequence(0, textlength))) {
//                    pesquisa.add(movimentacaoProdutos.get(i));
//                }
//            }
            if (p.getNome().toLowerCase().contains(txtPesquisa.toLowerCase()))
                pesquisa.add(movimentacaoProdutos.get(i));
        }
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void atualizaListaMovimentacaoProdutoEvent(AtualizaListaMovimentacaoProdutoEvent event) {
        carregaLista();
    }

    private void carregaLista() {
        MovimentacaoProdutoDAO movimentacaoProdutoDAO= new MovimentacaoProdutoDAO(context);
        movimentacaoProdutos.clear();
        movimentacaoProdutos.addAll(movimentacaoProdutoDAO.listarMovimentacaoProduto());


        movimentacaoProdutoDAO.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = getContext();
        movimentacaoProdutoSincronizador = new MovimentacaoProdutoSincronizador(getContext());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
