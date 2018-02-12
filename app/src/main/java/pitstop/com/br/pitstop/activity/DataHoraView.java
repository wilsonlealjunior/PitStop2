package pitstop.com.br.pitstop.activity;

import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pitstop.com.br.pitstop.R;
import pitstop.com.br.pitstop.preferences.UsuarioPreferences;

public class DataHoraView {
    static final long TREZE_HORAS_EM_MILISSEGUNDOS = 46800000;


    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Calendar dateTime = Calendar.getInstance();

    private TextView textViewDataInicio;
    private DatePickerDialog.OnDateSetListener DateSetListenerInicio;
    private TimePickerDialog.OnTimeSetListener HoraSetListenerInicio;

    private TextView textViewDataFim;
    private DatePickerDialog.OnDateSetListener DataSetListenerFim;
    private TimePickerDialog.OnTimeSetListener HoraSetListenerFim;

    private ViewGroup viewRoot;
    private Context context;

    private UsuarioPreferences usuarioPreferences;

    DataHoraView(ViewGroup viewRoot, Context context) {
        this.viewRoot = viewRoot;
        this.context = context;
        usuarioPreferences = new UsuarioPreferences(context);
        carregandoView();
        configurandoListenersDaData();
        configurandoTextViewDaData();
        configurandoViewParaFuncionario();
    }

    private void configurandoViewParaFuncionario() {
        if (usuarioPreferences.temUsuario()) {
            if (usuarioPreferences.getUsuario().getRole().equals("Funcionario")) {
                Date ate = new Date();
                long longate = ate.getTime();
                long longDe = longate - TREZE_HORAS_EM_MILISSEGUNDOS;
                Date de = new Date(longDe);
                textViewDataInicio.setText(formatter.format(de));
                textViewDataFim.setText(formatter.format(ate));
                textViewDataInicio.setFocusable(false);
                textViewDataFim.setFocusable(false);
                textViewDataInicio.setClickable(false);
                textViewDataFim.setClickable(false);

            }

        }
    }


    private void configurandoListenersDaData() {
        DateSetListenerInicio = criandoListenerDaData(textViewDataInicio);
        HoraSetListenerInicio = criandoListernerDeHora(textViewDataInicio);
        DataSetListenerFim = criandoListenerDaData(textViewDataFim);
        HoraSetListenerFim = criandoListernerDeHora(textViewDataFim);
    }

    @NonNull
    private TimePickerDialog.OnTimeSetListener criandoListernerDeHora(final TextView textViewData) {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateTime.set(Calendar.MINUTE, minute);
                AtualizandoTextViewDaData(textViewData);
            }
        };
    }

    @NonNull
    private DatePickerDialog.OnDateSetListener criandoListenerDaData(final TextView textViewData) {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTime.set(Calendar.YEAR, year);
                dateTime.set(Calendar.MONTH, monthOfYear);
                dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                AtualizandoTextViewDaData(textViewData);
            }
        };
    }

    private void AtualizandoTextViewDaData(TextView displayDataEHora) {
        displayDataEHora.setText(formatter.format(dateTime.getTime()));
    }

    private void AtualizandoDataDeAcordoComAEscolhaDoUsuario(DatePickerDialog.OnDateSetListener DataListener, TimePickerDialog.OnTimeSetListener HoraListener) {
        new TimePickerDialog(context, HoraListener, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();
        new DatePickerDialog(context, DataListener, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void configurandoTextViewDaData() {
        textViewDataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTime = Calendar.getInstance();
                AtualizandoDataDeAcordoComAEscolhaDoUsuario(DateSetListenerInicio, HoraSetListenerInicio);
            }
        });
        textViewDataFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTime = Calendar.getInstance();
                AtualizandoDataDeAcordoComAEscolhaDoUsuario(DataSetListenerFim, HoraSetListenerFim);
            }
        });
    }


    private void carregandoView() {
        textViewDataInicio = viewRoot.findViewById(R.id.et_data_inicio);
        textViewDataFim = viewRoot.findViewById(R.id.et_data_fim);

    }


    public TextView getTextViewDataInicio() {
        return textViewDataInicio;
    }

    public void setTextViewDataInicio(TextView textViewDataInicio) {
        this.textViewDataInicio = textViewDataInicio;
    }

    public TextView getTextViewDataFim() {
        return textViewDataFim;
    }

    public void setTextViewDataFim(TextView textViewDataFim) {
        this.textViewDataFim = textViewDataFim;
    }
}
