package pitstop.com.br.pitstop.activity.grafico;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.Util;
import pitstop.com.br.pitstop.activity.DataHoraView;
import pitstop.com.br.pitstop.dao.AvariaDAO;
import pitstop.com.br.pitstop.dao.FuroDAO;
import pitstop.com.br.pitstop.dao.LojaDAO;
import pitstop.com.br.pitstop.dao.UsuarioDAO;
import pitstop.com.br.pitstop.dao.VendaDAO;
import pitstop.com.br.pitstop.model.Loja;
import pitstop.com.br.pitstop.model.Usuario;
import pitstop.com.br.pitstop.model.Venda;

/**
 * Created by wilso on 21/02/2018.
 */

public class FiltroResumoCardView {
    final long VINTE_E_QUATRO_HORAS_EM_MILISSEGUNDOS = 86400000l;
    Usuario funcionarioEscolhido;
    Loja lojaEscolhida;
    String formaDePagamentoEscolhido;

    LinearLayout progressBar;
    CardView resumo;
    Spinner funcionarioSpinner;
    Spinner lojaSpinner;
    Spinner formaDePagamentoSpinnner;
    TextView tvResumeCardLucro;
    TextView tvResumeCardTotal;
    TextView tvResumeCardAvaria;
    TextView tvResumeCardFuro;

    List<Usuario> funcionarios;
    List<Loja> lojas;

    String[] formaDePagamento = new String[]{"Todas", "dinheiro", "cartao"};
    List<String> labelslojas = new ArrayList<>();
    List<String> labelsfuncionario = new ArrayList<>();


    private final ViewGroup viewRoot;
    Context context;

    LojaDAO lojaDAO;
    VendaDAO vendaDAO;
    UsuarioDAO usuarioDAO;
    AvariaDAO avariaDAO;
    FuroDAO furoDAO;
    private Button buttonGerar;

    DataHoraView dataHoraView;
    double total;
    double lucro;
    double avaria;
    double furo;

    public FiltroResumoCardView(ViewGroup viewRoot, Context context) {
        this.viewRoot = viewRoot;
        this.context = context;
        loadView();
        configurandoDataHoraView();
        setupView();
        recuperandoValores();
        configurandoValoresNaView();
    }

    private void configurandoValoresNaView() {
        final NumberFormat formatoBrasileiro = DecimalFormat.getCurrencyInstance(new Locale("pt", "br"));
        tvResumeCardTotal.setText(formatoBrasileiro.format(total).
                replace("R$", "R$ ").
                replace("-R$", "R$ -"));
        tvResumeCardLucro.setText(formatoBrasileiro.format(lucro).
                replace("R$", "R$ ").
                replace("-R$", "R$ -"));
        tvResumeCardAvaria.setText(formatoBrasileiro.format(avaria).
                replace("R$", "R$ ").
                replace("-R$", "R$ -"));
        tvResumeCardFuro.setText(formatoBrasileiro.format(furo).
                replace("R$", "R$ ").
                replace("-R$", "R$ -"));
    }

    private void configurandoDataHoraView() {
        Date ate = new Date();
        long longate = ate.getTime();
        long longDe = longate - VINTE_E_QUATRO_HORAS_EM_MILISSEGUNDOS;
        Date de = new Date(longDe);
        dataHoraView.setEditTextDataInicio(Util.dataNoformatoBrasileiro(de));
        dataHoraView.setEditTextDataFim(Util.dataNoformatoBrasileiro(ate));
    }

    private void loadView() {
        ViewGroup view = (ViewGroup) viewRoot.findViewById(R.id.resumo);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        tvResumeCardTotal = (TextView) view.findViewById(R.id.resumo_card_total);
        tvResumeCardLucro = (TextView) view.findViewById(R.id.resumo_card_lucro);
        tvResumeCardAvaria = (TextView) view.findViewById(R.id.resumo_card_avaria);
        tvResumeCardFuro = (TextView) view.findViewById(R.id.resumo_card_furo);
        dataHoraView = new DataHoraView(view, context);
        lojaDAO = new LojaDAO(context);
        vendaDAO = new VendaDAO(context);
        usuarioDAO = new UsuarioDAO(context);
        avariaDAO = new AvariaDAO(context);
        furoDAO = new FuroDAO(context);
        buttonGerar = (Button) view.findViewById(R.id.gerar_relatorio);
        lojaSpinner = (Spinner) view.findViewById(R.id.spinner_loja);
        formaDePagamentoSpinnner = (Spinner) view.findViewById(R.id.spinner_forma_pagamento);
        funcionarioSpinner = (Spinner) view.findViewById(R.id.spinner_funcionario);

    }

    public void setupView() {
        funcionarios = usuarioDAO.listarUsuarios();
        usuarioDAO.close();
        lojas = lojaDAO.listarLojas();
        lojaDAO.close();
        labelslojas.add("Todas");
        for (Loja l : lojas) {
            labelslojas.add(l.getNome());
        }
        labelsfuncionario.add("Todos");
        for (Usuario u : funcionarios) {
            labelsfuncionario.add(u.getNome());
        }
        ArrayAdapter<String> spinnerAdapterLoja = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, labelslojas);
        spinnerAdapterLoja.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lojaSpinner.setAdapter(spinnerAdapterLoja);


        ArrayAdapter<String> spinnerAdapterFormaPagamento = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, formaDePagamento);
        spinnerAdapterFormaPagamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formaDePagamentoSpinnner.setAdapter(spinnerAdapterFormaPagamento);


        ArrayAdapter<String> spinnerAdapterFuncionario = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, labelsfuncionario);
        spinnerAdapterFuncionario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        funcionarioSpinner.setAdapter(spinnerAdapterFuncionario);


        formaDePagamentoSpinnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    formaDePagamentoEscolhido = formaDePagamento[i];
                    Log.e("Forma de pagamento", formaDePagamentoEscolhido);
                } else {
                    formaDePagamentoEscolhido = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lojaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    lojaEscolhida = lojas.get(i - 1);
                    Log.e("Loja Escolhida", lojaEscolhida.getNome());
                } else {
                    lojaEscolhida = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        funcionarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    funcionarioEscolhido = funcionarios.get(i - 1);
                } else {
                    funcionarioEscolhido = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        buttonGerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperandoValores();
                configurandoValoresNaView();
                /*CarregadorDeGrafico carregador = new CarregadorDeGrafico();
                carregador.execute();*/

            }
        });
    }

    public void recuperandoValores() {
        List<Double> vendas = new ArrayList<>();
        Date de = Util.converteDoFormatoBrasileitoParaDate(dataHoraView.getEditTextDataInicio().getText().toString());
        Date ate = Util.converteDoFormatoBrasileitoParaDate(dataHoraView.getEditTextDataFim().getText().toString());
        String stringDe = Util.dataNoformatoDoSQLite(de);
        String stringAte = Util.dataNoformatoDoSQLite(ate);
        vendas = vendaDAO.relatorioResumo(stringDe, stringAte, formaDePagamentoEscolhido, lojaEscolhida, funcionarioEscolhido);
        vendaDAO.close();

        avaria = avariaDAO.relatorioResumo(lojaEscolhida, stringDe, stringAte);
        avariaDAO.close();
        furo = furoDAO.relatorioResumo(stringDe, stringAte, lojaEscolhida, funcionarioEscolhido);
        furoDAO.close();
        total = vendas.get(0);
        lucro = vendas.get(1);


    }

    private class CarregadorDeGrafico extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            resumo.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            recuperandoValores();
            return "";
        }

        @Override
        protected void onPostExecute(String t) {
            configurandoValoresNaView();
            progressBar.setVisibility(View.GONE);
            resumo.setVisibility(View.VISIBLE);
        }


    }
}
