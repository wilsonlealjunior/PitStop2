package pitstop.com.br.pitstop.activity.produto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.ResponseBody;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.dao.EntradaProdutoDAO;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.ProdutoDAO;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.event.CarregaListaDeProduto;
import pitstop.com.br.pitstop.model.EntradaProduto;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Produto;
import pitstop.com.br.pitstop.model.RealmString;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.preferences.ObjetosSinkPreferences;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;
import pitstop.com.br.pitstop.retrofit.RetrofitInializador;
import pitstop.com.br.pitstop.sic.ObjetosSinkSincronizador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListarProdutoFragment extends Fragment implements SearchView.OnQueryTextListener {

    Spinner spinnerLoja;
    CardView cardViewSpinerLoja;
    private RecyclerView recyclerView;
    private ProdutoRecicleViewAdapter produtoRecicleViewAdapter;
    private SwipeRefreshLayout swipe;
    Context context;
    ImageView icLoja;
    List<Produto> produtos = new ArrayList<>();
    ProdutoRecicleViewAdapter adapterPesquisa;
    Button novoProduto;
    List<Produto> pesquisa = new ArrayList<>();
    Loja lojaVindaDaTelaListaLoja;
    UsuarioPreferences usuarioPreferences;
    Toolbar toolbar;
    SearchView searchView;
    ProdutoDAO produtoDAO;
    EntradaProdutoDAO entradaProdutoDAO;
    List<String> labelsLojas = new ArrayList<>();
    List<Loja> lojas = new ArrayList<>();
    LojaDAO lojaDAO;
    ObjetosSinkSincronizador objetosSinkSincronizador;
    EventBus bus = EventBus.getDefault();
    AlertDialog mensagens;
    ObjetosSinkPreferences objetosSinkPreferences;
    TextView tvUltimaSincronizacao;


    public ListarProdutoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {

            File futureStudioIconFile = new File(getActivity().getExternalFilesDir(null) + File.separator + "estoqueAtual.pdf");
            //File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"relatorio.pdf");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    //Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public void views() {
        File pdfFile = new File(getActivity().getExternalFilesDir(null) + File.separator + "estoqueAtual.pdf");  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
//            snackbar.setText("Não existe aplicativo para visualizar o PDF");
//            snackbar.show();
            Toast.makeText(getActivity(), "Não existe aplicativo para visualizar o PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public void gerarPdf() {
        String lojaEscolhida;
        int posicao = spinnerLoja.getSelectedItemPosition();
        if (posicao == 0) {
            lojaEscolhida = "%";
        } else {
            lojaEscolhida = lojas.get(posicao - 1).getId();
        }
        mensagens.setCancelable(false);
        mensagens.show();
        mensagens.setMessage("Gerando PDF");
        Call<ResponseBody> call = new RetrofitInializador().getRelatorioService().estoqueAtual(lojaEscolhida);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mensagens.hide();
                mensagens.dismiss();
                if (response.isSuccessful()) {
                    Log.d("TAG", "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());
//                            snackbar.setText("PDF gerado com sucesso");
//                            snackbar.show();
                    Toast.makeText(context, "PDF gerado com sucesso", Toast.LENGTH_SHORT).show();
                    views();

                    Log.d("TAG", "file download was a success? " + writtenToDisk);
                } else {
                    Log.d("TAG", "server contact failed");
//                            snackbar.setText("Erro ao gerar o pdf");
//                            snackbar.show();
                    Toast.makeText(context, "Erro ao gerar o pdf", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                mensagens.hide();
                mensagens.dismiss();

//                        Toast.makeText(getApplicationContext(), "Verifique a conexao com a internet", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setUpToolbar() {
        toolbar.inflateMenu(R.menu.menu_estoque);
        toolbar.setTitle("Produtos");

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
                    case R.id.gerar_pdf:
                        gerarPdf();
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
        icLoja = (ImageView) rootView.findViewById(R.id.ic_loja);
        tvUltimaSincronizacao = (TextView) rootView.findViewById(R.id.tv_data_ultima_sincronizacao);
        objetosSinkPreferences = new ObjetosSinkPreferences(getContext());
        recyclerView.setHasFixedSize(true);
        produtoRecicleViewAdapter = new ProdutoRecicleViewAdapter(produtos, context);
        adapterPesquisa = new ProdutoRecicleViewAdapter(pesquisa, context);
        recyclerView.setAdapter(produtoRecicleViewAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mensagens = new ProgressDialog(context);
        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_lista_produto);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int posicaoSelecionadaSpinner = spinnerLoja.getSelectedItemPosition();
                if (posicaoSelecionadaSpinner == 0) {
                    carregaLista(null);
                } else {
                    carregaLista(lojas.get(posicaoSelecionadaSpinner - 1));
                }

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
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


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
        entradaProdutoDAO = new EntradaProdutoDAO(context);
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
        lojaDAO.close();
        labelsLojas.add("Todas");
        for (Loja loja : lojas) {
            labelsLojas.add(loja.getNome());
        }
        Intent intent = getActivity().getIntent();
        String lojaId = intent.getStringExtra("lojaId");
        if (lojaId != null) {
            lojaVindaDaTelaListaLoja = lojaDAO.procuraPorId(lojaId);
        }
        intent.removeExtra("lojaId");
        ArrayAdapter<String> spinnerAdapterDe = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, labelsLojas);
        spinnerAdapterDe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoja.setAdapter(spinnerAdapterDe);

        spinnerLoja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (lojaVindaDaTelaListaLoja == null) {
                    if (i == 0) {
                        carregaLista(null);
                    } else {
                        carregaLista(lojas.get(i - 1));
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
                    carregaLista(lojaVindaDaTelaListaLoja);
                    lojaVindaDaTelaListaLoja = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//
        icLoja.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                int quantidadeProdutosIncosistentes = 0;
                for (Produto p : produtos) {
                    Produto produtoPrincipal = new Produto();
                    if (p.vinculado()) {
                        for (Produto produtosDaListView : produtos) {
                            if (p.getIdProdutoPrincipal().equals(produtosDaListView.getId())) {
                                produtoPrincipal = produtosDaListView;
                                break;
                            }
                        }
                    } else {
                        produtoPrincipal = p;
                    }
//                    produtoPrincipal.getEntradaProdutos().addAll(entradaProdutoDAO.procuraTodosDeUmProduto(produtoPrincipal));
                    entradaProdutoDAO.close();
                    int quantidade = 0;
                    for (EntradaProduto entradaProduto : produtoPrincipal.getEntradaProdutos()) {
                        quantidade += (entradaProduto.getQuantidade() - entradaProduto.getQuantidadeVendidaMovimentada());

                    }
                    if (produtoPrincipal.getQuantidade() != quantidade) {
                        quantidadeProdutosIncosistentes++;
                        produtoPrincipal.setQuantidade(quantidade);
                        for (RealmString produtoVinculoId : produtoPrincipal.getIdProdutoVinculado()) {
                            Produto produtoVinculo = produtoDAO.procuraPorId(produtoVinculoId.getValor());
                            produtoDAO.close();
                            produtoVinculo.setQuantidade(quantidade);
                            produtoVinculo.desincroniza();
                            produtoDAO.altera(produtoVinculo);
                            produtoDAO.close();
                        }
                        produtoPrincipal.desincroniza();
                        produtoDAO.altera(produtoPrincipal);
                        produtoDAO.close();

                    }

                }
                Toast.makeText(context, quantidadeProdutosIncosistentes + " Produtos inconsistestes corrigidos com sucesso ", Toast.LENGTH_LONG).show();
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


    }


    private void verificaUltimaSincronizacao() {
        if (objetosSinkPreferences.temVersao()) {
            Date data = Util.converteDoFormatoSQLParaDate(objetosSinkPreferences.getVersao());
            tvUltimaSincronizacao.setText("Última sincronização: " + Util.dataComDiaEHoraPorExtenso(data.getTime()));
        }
    }

    public void pesquisar(String txtPesquisa) {
        int textlength = txtPesquisa.length();
        produtos.clear();

        produtos.addAll(produtoDAO.procuraPorNome(txtPesquisa));
        produtoRecicleViewAdapter.notifyDataSetChanged();
        produtoDAO.close();
        /*pesquisa.clear();

        for (int i = 0; i < produtos.size(); i++) {

            if (produtos.get(i).getNome().toLowerCase().contains(txtPesquisa.toLowerCase()))
                pesquisa.add(produtos.get(i));

        }*/
    }

    @Override
    public void onDestroy() {
        // Unregister
        bus.unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void atualizaListaProdutoEvent(AtualizaListaProdutoEvent event) {
        if (spinnerLoja.getSelectedItemPosition() == 0) {
            carregaLista(null);
        } else {
            spinnerLoja.setSelection(0);
        }
        verificaUltimaSincronizacao();
    }

    private void carregaLista(Loja loja) {
        produtos.clear();
        Usuario user = usuarioPreferences.getUsuario();
        if (usuarioPreferences.getUsuario().getRole().equals("Administrador")) {
            if (loja == null) {
                produtos.addAll(produtoDAO.listarProdutos());
            } else {
                produtos.addAll(produtoDAO.procuraPorLoja(loja));
            }
        } else {
            produtos.addAll(produtoDAO.procuraPorLoja(usuarioPreferences.getLoja()));
        }
        produtoDAO.close();
        swipe.setRefreshing(false);
      /*  recyclerView.setAdapter(produtoRecicleViewAdapter);*/
        produtoRecicleViewAdapter.notifyDataSetChanged();
        verificaUltimaSincronizacao();


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
        /*recyclerView.setAdapter(adapterPesquisa);
        adapterPesquisa.notifyDataSetChanged();*/
//        recyclerView.getRecycledViewPool().clear();

        return false;
    }
/*
    private class CarregandoListaDeProduto extends AsyncTask<Void, Void, String> {
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
            produtoDAO = new ProdutoDAO(context);
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
            verificaUltimaSincronizacao();

        }
    }*/

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void carregaListaDeProduto(CarregaListaDeProduto event) {
        produtoRecicleViewAdapter.notifyDataSetChanged();
    }
}
