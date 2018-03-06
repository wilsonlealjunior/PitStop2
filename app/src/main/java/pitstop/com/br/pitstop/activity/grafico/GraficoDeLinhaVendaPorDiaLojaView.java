package pitstop.com.br.pitstop.activity.grafico;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.activity.DataHoraView;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.VendaDAO;
import pitstop.com.br.pitstop.model.Loja;

public class GraficoDeLinhaVendaPorDiaLojaView extends AppCompatActivity {
    final long UMA_SEMANA_EM_MILISSEGUNDOS = 604800000l;
    VendaDAO vendaDAO;
    List<Loja> lojas;
    Date deUsuario = new Date();
    LineChart lineChart;
    ViewGroup viewRoot;
    Context context;
    DataHoraView dataHoraView;
    Button gerarGrafico;
    private LojaDAO lojaDAO;
    LinearLayout progressBar;


    public GraficoDeLinhaVendaPorDiaLojaView(ViewGroup viewRoot, Context context) {
        this.context = context;
        this.viewRoot = viewRoot;
        loadView();
        configurandoDataHoraView();
        List<String> datas = recuperandoDatasNoFormatoSQLiteDoDataHoraView();
        List<DataSetGraficoDeLinha> dadosParaLineChartTotalVendaLojaPorDia = new ArrayList<>();
        for (Loja loja : lojas) {
            dadosParaLineChartTotalVendaLojaPorDia.add(recuperandoValoresParaOBarChartTotalVendaUsuario(datas, loja));
        }
        initGraphBarChartTotalVendaUsuario(dadosParaLineChartTotalVendaLojaPorDia);
        setupView();

    }

    public String dataNoformatoBrasileiro(Date data) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dataFormatada = formatter.format(data);
        return dataFormatada;
    }

    private void configurandoDataHoraView() {
        Date ate = new Date();
        long longate = ate.getTime();
        long longDe = longate - UMA_SEMANA_EM_MILISSEGUNDOS;
        Date de = new Date(longDe);
        dataHoraView.setEditTextDataInicio(dataNoformatoBrasileiro(de));
        dataHoraView.setEditTextDataFim(dataNoformatoBrasileiro(ate));
    }

    private void setupView() {
        gerarGrafico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarregadorDeGrafico carregaroDeGrafico = new CarregadorDeGrafico();
                carregaroDeGrafico.execute();

            }
        });
    }

    private DataSetGraficoDeLinha recuperandoValoresParaOBarChartTotalVendaUsuario(List<String> datas, Loja loja) {
        List<Double> somaDoTotalDasVendasPorLojaETempo = new ArrayList<>();
        for (int i = 0; i < datas.size(); i = i + 2) {
            somaDoTotalDasVendasPorLojaETempo.add(vendaDAO.somaDoTotalPor(loja, datas.get(i), datas.get(i + 1)));
            vendaDAO.close();
        }


        return new DataSetGraficoDeLinha(loja, somaDoTotalDasVendasPorLojaETempo);
    }

    private void loadView() {
        ViewGroup viewGroupDoGrafico = (ViewGroup) viewRoot.findViewById(R.id.grafico_de_linha_venda_por_dia_loja_view);
        gerarGrafico = (Button) viewGroupDoGrafico.findViewById(R.id.gerar_grafico);
        lineChart = (LineChart) viewGroupDoGrafico.findViewById(R.id.line_chart);
        vendaDAO = new VendaDAO(context);
        lojaDAO = new LojaDAO(context);
        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        progressBar = viewGroupDoGrafico.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        dataHoraView = new DataHoraView((ViewGroup) viewGroupDoGrafico.findViewById(R.id.data_hora_view), context);
    }

    public String dataNoformatoDoSQLite(Date data) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dataFormatada = formatter.format(data);
        return dataFormatada;
    }

    private ArrayList<String> recuperandoDatasNoFormatoSQLiteDoDataHoraView() {
        Date dataInicio = converteParaDate(dataHoraView.getEditTextDataInicio().getText().toString());
        Date dataFinal = converteParaDate(dataHoraView.getEditTextDataFim().getText().toString());
        Calendar calendarAte = Calendar.getInstance();
        calendarAte.setTime(dataFinal);
        calendarAte.add(Calendar.DAY_OF_MONTH, +1);
        dataFinal.setTime(calendarAte.getTimeInMillis());
        deUsuario.setTime(dataInicio.getTime());
        ArrayList<String> datas = new ArrayList<>();
        while (dataInicio.before(dataFinal)) {
            Calendar calendarInicio = Calendar.getInstance();
            calendarInicio.setTime(dataInicio);
            calendarInicio.set(Calendar.HOUR_OF_DAY, calendarInicio.getActualMinimum(Calendar.HOUR_OF_DAY));
            calendarInicio.set(Calendar.MINUTE, calendarInicio.getActualMinimum(Calendar.MINUTE));
            calendarInicio.set(Calendar.SECOND, calendarInicio.getActualMinimum(Calendar.SECOND));
            dataInicio = calendarInicio.getTime();
            String inicio = dataNoformatoDoSQLite(dataInicio);

            Calendar calendarFinal = calendarInicio;
            calendarFinal.set(Calendar.HOUR_OF_DAY, calendarFinal.getActualMaximum(Calendar.HOUR_OF_DAY));
            calendarFinal.set(Calendar.MINUTE, calendarFinal.getActualMaximum(Calendar.MINUTE));
            calendarFinal.set(Calendar.SECOND, calendarFinal.getActualMaximum(Calendar.SECOND));
            dataInicio = calendarInicio.getTime();
            String fim = dataNoformatoDoSQLite(dataInicio);
            datas.add(inicio);
            datas.add(fim);

            calendarFinal.add(Calendar.DAY_OF_MONTH, +1);
            dataInicio = calendarFinal.getTime();
        }
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

    public boolean isValid(DataHoraView dataHoraView) {
        Date dateInicio = converteParaDate(dataHoraView.getEditTextDataInicio().getText().toString());
        Date dateFim = converteParaDate(dataHoraView.getEditTextDataFim().getText().toString());
        if (dateInicio.after(dateFim)) {
            Toast.makeText(context, "A data Inicial deve ser menor do que a data final ", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public class DataSetGraficoDeLinha {
        Loja loja;
        List<Double> dados;

        public DataSetGraficoDeLinha(Loja loja, List<Double> dados) {
            this.loja = loja;
            this.dados = dados;

        }


    }

    public void initGraphBarChartTotalVendaUsuario(List<DataSetGraficoDeLinha> dados) {
        configurandoAsPropriedadesDoBarChart(lineChart);
        LineData data = new LineData();

        ArrayList<Entry> series = new ArrayList<>();
        for (DataSetGraficoDeLinha dataSetGraficoDeLinha : dados) {
            series = new ArrayList<>();

            int i = 0;
            for (Double total : dataSetGraficoDeLinha.dados) {
                String t = String.valueOf(total);
                series.add(new BarEntry(i, Float.valueOf(t)));
                i++;

            }
            LineDataSet lineDataSet = new LineDataSet(series, dataSetGraficoDeLinha.loja.getNome());
            Random gerador = new Random();
            lineDataSet.setColor(Color.rgb(gerador.nextInt(254), gerador.nextInt(254), gerador.nextInt(254)));
            lineDataSet.setCircleColor(Color.BLACK);
            lineDataSet.setHighLightColor(Color.RED);
            data.addDataSet(lineDataSet);


        }

        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);
        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();


    }

    private void configurandoAsPropriedadesDoBarChart(LineChart lineChart) {

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setLabelRotationAngle(-90f);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar c = Calendar.getInstance();
                c.setTime(deUsuario);
                Float f = value;
                int dia = f.intValue();
                c.add(Calendar.DAY_OF_MONTH, +dia);
                Date d = c.getTime();
                return formatter.format(d);
            }
        });
//        Calendar c = Calendar.getInstance();
//        c.setTime(deUsuario);
//        int comeco = c.get(Calendar.DAY_OF_MONTH);
//        c.setTime(ate);
//        int fim = c.get(Calendar.DAY_OF_MONTH);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
//        xAxis.setAxisMinimum(comeco);
//        xAxis.setAxisMaximum(fim);


        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyAxisValueFormatter());
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // set an alternative background color
        lineChart.setBackgroundColor(Color.WHITE);


        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setWordWrapEnabled(true);
        l.setDrawInside(false);
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

    private class CarregadorDeGrafico extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            lineChart.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            List<String> datas = recuperandoDatasNoFormatoSQLiteDoDataHoraView();
            List<DataSetGraficoDeLinha> dadosParaLineChartTotalVendaLojaPorDia = new ArrayList<>();
            for (Loja loja : lojas) {
                dadosParaLineChartTotalVendaLojaPorDia.add(recuperandoValoresParaOBarChartTotalVendaUsuario(datas, loja));
            }
            initGraphBarChartTotalVendaUsuario(dadosParaLineChartTotalVendaLojaPorDia);

            return "";
        }

        @Override
        protected void onPostExecute(String t) {
            progressBar.setVisibility(View.GONE);
            lineChart.setVisibility(View.VISIBLE);
        }


    }
}

