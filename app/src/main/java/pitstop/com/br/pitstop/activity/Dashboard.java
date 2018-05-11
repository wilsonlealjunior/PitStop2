package pitstop.com.br.pitstop.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import io.realm.Realm;
import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.grafico.FiltroResumoCardView;
import pitstop.com.br.pitstop.activity.grafico.GraficoDeBarraTotalVendasLojaView;
import pitstop.com.br.pitstop.activity.grafico.GraficoDeBarraTotalVendasUsuarioView;
import pitstop.com.br.pitstop.activity.grafico.GraficoDeLinhaVendaPorDiaLojaView;


public class Dashboard extends AppCompatActivity {
    ViewGroup viewRoot;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard);
        viewRoot = (ViewGroup) findViewById(android.R.id.content);
        GraficoDeBarraTotalVendasLojaView graficoDeBarraTotalVendasLojaView = new GraficoDeBarraTotalVendasLojaView(viewRoot, this);
        GraficoDeBarraTotalVendasUsuarioView graficoDeBarraTotalVendasUsuarioView = new GraficoDeBarraTotalVendasUsuarioView(viewRoot, this);
        GraficoDeLinhaVendaPorDiaLojaView graficoDeLinhaVendaPorDiaLojaView = new GraficoDeLinhaVendaPorDiaLojaView(viewRoot, this);
        FiltroResumoCardView filtroResumoCardView = new FiltroResumoCardView(viewRoot, this);
    }

}
