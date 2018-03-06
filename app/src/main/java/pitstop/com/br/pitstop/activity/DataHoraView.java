package pitstop.com.br.pitstop.activity;

import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import android.widget.DatePicker;
import android.widget.EditText;
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

    private EditText editTextDataInicio;
    private DatePickerDialog.OnDateSetListener DateSetListenerInicio;
    private TimePickerDialog.OnTimeSetListener HoraSetListenerInicio;

    private EditText editTextDataFim;
    private DatePickerDialog.OnDateSetListener DataSetListenerFim;
    private TimePickerDialog.OnTimeSetListener HoraSetListenerFim;

    private ViewGroup viewRoot;
    private Context context;

    private UsuarioPreferences usuarioPreferences;

    public DataHoraView(ViewGroup viewRoot, Context context) {
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
                editTextDataInicio.setText(formatter.format(de));
                editTextDataFim.setText(formatter.format(ate));
                editTextDataInicio.setFocusable(false);
                editTextDataFim.setEnabled(false);
                editTextDataFim.setFocusable(false);
                editTextDataInicio.setClickable(false);
                editTextDataFim.setClickable(false);
                editTextDataInicio.setEnabled(false);
            }

        }
    }


    private void configurandoListenersDaData() {
        DateSetListenerInicio = criandoListenerDaData(editTextDataInicio);
        HoraSetListenerInicio = criandoListernerDeHora(editTextDataInicio);
        DataSetListenerFim = criandoListenerDaData(editTextDataFim);
        HoraSetListenerFim = criandoListernerDeHora(editTextDataFim);
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
        editTextDataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTime = Calendar.getInstance();
                AtualizandoDataDeAcordoComAEscolhaDoUsuario(DateSetListenerInicio, HoraSetListenerInicio);
            }
        });
        editTextDataFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTime = Calendar.getInstance();
                AtualizandoDataDeAcordoComAEscolhaDoUsuario(DataSetListenerFim, HoraSetListenerFim);
            }
        });
    }


    private void carregandoView() {
        editTextDataInicio = viewRoot.findViewById(R.id.et_data_inicio);
        editTextDataFim = viewRoot.findViewById(R.id.et_data_fim);

    }


    public TextView getEditTextDataInicio() {
        return editTextDataInicio;
    }

    public void setEditTextDataInicio(String editTextDataInicio) {
        this.editTextDataInicio.setText(editTextDataInicio);

    }

    public TextView getEditTextDataFim() {
        return editTextDataFim;
    }

    public void setEditTextDataFim(String editTextDataFim) {
        this.editTextDataFim.setText(editTextDataFim);
    }
}
