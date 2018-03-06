package pitstop.com.br.pitstop.activity.grafico;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.DataHoraView;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.VendaDAO;
import pitstop.com.br.pitstop.model.Loja;

public class GraficoDeBarraTotalVendasLojaView {
    final long VINTE_E_QUATRO_HORAS_EM_MILISSEGUNDOS = 86400000l;
    BarChart barChart;
    LojaDAO lojaDAO;
    VendaDAO vendaDAO;
    List<Loja> lojas;
    Context context;
    ViewGroup viewRoot;
    DataHoraView dataHoraView;
    Button gerarGrafico;
    LinearLayout progressBar;

    public GraficoDeBarraTotalVendasLojaView(ViewGroup viewRoot, Context context) {
        this.viewRoot = viewRoot;
        this.context = context;
        loadView();
        configurandoDataHoraView();
        List<String> datas = recuperandoDatasNoFormatoSQLiteDoDataHoraView();
        List<Double> dadosParaBarChartTotalVendaLoja = recuperandoValoresParaOBarChartTotalVendaLoja(datas.get(0), datas.get(1));
        initGraphBarChartTotalVendaLoja(dadosParaBarChartTotalVendaLoja);
        setupView();

    }

    private void setupView() {
        gerarGrafico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValid(dataHoraView))
                    return;
                progressBar.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
                List<String> datas = recuperandoDatasNoFormatoSQLiteDoDataHoraView();
                List<Double> dadosParaBarChartTotalVendaLoja = recuperandoValoresParaOBarChartTotalVendaLoja(datas.get(0), datas.get(1));
                initGraphBarChartTotalVendaLoja(dadosParaBarChartTotalVendaLoja);
                progressBar.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);

            }
        });
    }

    private List<Double> recuperandoValoresParaOBarChartTotalVendaLoja(String dataInicio, String dataFinal) {
        List<Double> somaDoTotalDasVendasPorLojaETempo = new ArrayList<>();
        for (Loja loja : lojas) {
            somaDoTotalDasVendasPorLojaETempo.add(vendaDAO.somaDoTotalPor(loja, dataInicio, dataFinal));
            vendaDAO.close();
        }
        return somaDoTotalDasVendasPorLojaETempo;
    }

    public boolean isValid(DataHoraView dataHoraView) {
        Date dateInicio = converteParaDate(dataHoraView.getEditTextDataInicio().getText().toString());
        Date dateFim = converteParaDate(dataHoraView.getEditTextDataFim().getText().toString());
        if (dateInicio.after(dateFim)) {
            Toast.makeText(context, "A data Inicial deve ser menor do que a data final ", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private ArrayList<String> recuperandoDatasNoFormatoSQLiteDoDataHoraView() {
        Date dateInicio = converteParaDate(dataHoraView.getEditTextDataInicio().getText().toString());
        Date dateFim = converteParaDate(dataHoraView.getEditTextDataFim().getText().toString());
        String inicio = dataNoformatoDoSQLite(dateInicio);
        String fim = dataNoformatoDoSQLite(dateFim);
        ArrayList<String> datas = new ArrayList<>();
        datas.add(inicio);
        datas.add(fim);
        return datas;

    }

    private Date converteParaDate(String data) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(data);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private void configurandoDataHoraView() {
        Date ate = new Date();
        long longate = ate.getTime();
        long longDe = longate - VINTE_E_QUATRO_HORAS_EM_MILISSEGUNDOS;
        Date de = new Date(longDe);
        dataHoraView.setEditTextDataInicio(dataNoformatoBrasileiro(de));
        dataHoraView.setEditTextDataFim(dataNoformatoBrasileiro(ate));
    }

    public String dataNoformatoDoSQLite(Date data) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dataFormatada = formatter.format(data);
        return dataFormatada;
    }

    public String dataNoformatoBrasileiro(Date data) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dataFormatada = formatter.format(data);
        return dataFormatada;
    }

    private void loadView() {
        ViewGroup viewGroupDoGrafico = (ViewGroup) viewRoot.findViewById(R.id.grafico_barra_total_venda_loja);
        gerarGrafico = (Button) viewGroupDoGrafico.findViewById(R.id.gerar_grafico);
        barChart = (BarChart) viewGroupDoGrafico.findViewById(R.id.bar_chart);
        lojaDAO = new LojaDAO(context);
        vendaDAO = new VendaDAO(context);
        progressBar = viewGroupDoGrafico.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        dataHoraView = new DataHoraView((ViewGroup) viewGroupDoGrafico.findViewById(R.id.data_hora_view), context);
        lojas = lojaDAO.listarLojas();
        lojaDAO.close();

    }

    public void initGraphBarChartTotalVendaLoja(List<Double> SomaDoTotalDasVendasPorLojaETempo) {
        configurandoAsPropriedadesDoBarChart(barChart);
        BarData data = new BarData();
        int i = 0;
        for (Double total : SomaDoTotalDasVendasPorLojaETempo) {
            String t = String.valueOf(total);
            ArrayList<BarEntry> series = new ArrayList<>();
            series.add(new BarEntry(i + 1, Float.valueOf(t)));
            BarDataSet barDataSet = new BarDataSet(series, lojas.get(i).getNome());
            Random gerador = new Random();
            barDataSet.setColor(Color.rgb(gerador.nextInt(254), gerador.nextInt(254), gerador.nextInt(254)));
            data.addDataSet(barDataSet);
            i++;
        }


        barChart.setData(data);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    private void configurandoAsPropriedadesDoBarChart(BarChart barChart) {
        barChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyAxisValueFormatter());
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        barChart.setDrawBarShadow(false);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.setDrawGridBackground(false);
        barChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        barChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        barChart.getLegend().setWordWrapEnabled(true);
        barChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        barChart.getLegend().setDrawInside(false);
        barChart.getLegend().setTextSize(11f);
        barChart.getXAxis().setEnabled(false);
        barChart.setSelected(false);
        barChart.dispatchSetSelected(false);
    }

    public class MyAxisValueFormatter implements IAxisValueFormatter {

        private NumberFormat mFormat;

        public MyAxisValueFormatter() {
            mFormat = DecimalFormat.getCurrencyInstance(new Locale("pt", "br"));
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value);
        }
    }
}
