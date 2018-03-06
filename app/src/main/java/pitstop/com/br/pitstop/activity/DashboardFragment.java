package pitstop.com.br.pitstop.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.grafico.FiltroResumoCardView;
import pitstop.com.br.pitstop.activity.grafico.GraficoDeBarraTotalVendasLojaView;
import pitstop.com.br.pitstop.activity.grafico.GraficoDeBarraTotalVendasUsuarioView;
import pitstop.com.br.pitstop.activity.grafico.GraficoDeLinhaVendaPorDiaLojaView;
import pitstop.com.br.pitstop.event.AtualizaListaProdutoEvent;
import pitstop.com.br.pitstop.event.AtualizarGraficos;
import pitstop.com.br.pitstop.sic.ObjetosSinkSincronizador;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {
    ViewGroup viewRoot;
    Toolbar toolbar;
    ObjetosSinkSincronizador objetosSinkSincronizador;
    EventBus bus = EventBus.getDefault();

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private void setUpToolbar() {
        toolbar.inflateMenu(R.menu.menu_sinc);
        final Menu menu = toolbar.getMenu();
        MenuItem m1 = menu.findItem(R.id.action_search);
        m1.setVisible(false);
        toolbar.setTitle("Dashboard");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setUpToolbar();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bus.register(this);
        viewRoot = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard, container, false);

        GraficoDeBarraTotalVendasLojaView graficoDeBarraTotalVendasLojaView = new GraficoDeBarraTotalVendasLojaView((ViewGroup) viewRoot, getContext());
        GraficoDeBarraTotalVendasUsuarioView graficoDeBarraTotalVendasUsuarioView = new GraficoDeBarraTotalVendasUsuarioView((ViewGroup) viewRoot, getContext());
        GraficoDeLinhaVendaPorDiaLojaView graficoDeLinhaVendaPorDiaLojaView = new GraficoDeLinhaVendaPorDiaLojaView((ViewGroup) viewRoot, getContext());
        FiltroResumoCardView filtroResumoCardView = new FiltroResumoCardView((ViewGroup) viewRoot, getContext());
        objetosSinkSincronizador = new ObjetosSinkSincronizador(getContext());
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        return viewRoot;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void atualizarGraficos(AtualizarGraficos event) {
        new GraficoDeBarraTotalVendasLojaView((ViewGroup) viewRoot, getContext());
        new GraficoDeBarraTotalVendasUsuarioView((ViewGroup) viewRoot, getContext());
        new GraficoDeLinhaVendaPorDiaLojaView((ViewGroup) viewRoot, getContext());
        new FiltroResumoCardView((ViewGroup) viewRoot, getContext());

    }

    @Override
    public void onDestroy() {
        // Unregister
        bus.unregister(this);
        super.onDestroy();
    }

}
