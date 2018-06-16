package pitstop.com.br.pitstop;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by wilso on 17/02/2018.
 */

public abstract class Util {
    private static final String TAG = Util.class.getSimpleName();

    public static final Locale localeBR = new Locale("pt", "BR");
    public static final TimeZone timeZone = TimeZone.getTimeZone("GMT");

    /* Início conversões e formatações de data */

    public static String horarioFormatado(String hour) {
        return hour.replaceAll("^(\\d+).(\\d+).+$", "$1H$2");
    }

    public static String[] separaData(String data) {
        return data.split("/");
    }


    public static String convertDataParaString(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", localeBR);
        // Conversão no formato GMT (Brasil), padrão UTC. :)
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(new Date(date));
    }

    public static String convertDataParaStringMesEAnoSomente(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy", localeBR);
        return simpleDateFormat.format(new Date(date));
    }

    public static String convertDataParaString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", localeBR);
        return simpleDateFormat.format(date);
    }

    public static String convertDataParaStringNoFormatoDoServidor(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy  HH:mm:ss a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        return sdf.format(date);
    }

    public static String formataDataComDiaMesEAno(int dia, int mes, int ano) {
        return String.format("%02d/%02d/%04d", dia, mes, ano);
    }

    public static String horaOuDataOMaisRecente(Date data) {
        Calendar calendarHoje = Calendar.getInstance();
        String dataHoje = new SimpleDateFormat("dd/MM/yyyy", localeBR).format(calendarHoje.getTime());
        String dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm", localeBR).format(data);
        String[] dataHoraPartes = dataHora.split(" ");
        return dataHoraPartes[0].equals(dataHoje) ? dataHoraPartes[1] : dataHoraPartes[0];
    }

    public static Date converteStringParaData(String ddMMyyyy) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", localeBR);
        try {
            return simpleDateFormat.parse(ddMMyyyy);
        } catch (ParseException e) {
            return null;
        }
    }

    public static long converteStringParaDataTime(String ddMMyyyy) {
        try {
            return converteStringParaData(ddMMyyyy).getTime();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public static String dataComDiaEMes(Date data1) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data1);
        return calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, localeBR);
    }

    public static Date converteDiaMesAnoParaData(int dia, int mes, int ano) {
        LocalDate date = new LocalDate(ano, mes + 1, dia);
        return date.toDate();
    }

    public static int diaDaSemana(int ano, int mes, int dia) {
        GregorianCalendar calendar = new GregorianCalendar();
        DateTime dateTime = new DateTime(ano, mes, dia, 0, 0);
        Date date = new Date(dateTime.getMillis());
        calendar.setTime(date);
        //calendar.set(ano, mes -1, dia);
        return calendar.get(GregorianCalendar.DAY_OF_WEEK);
    }

    public static int diaDaSemana(String ddMMYYYY) {
        try {
            Date date = Util.converteStringParaData(ddMMYYYY);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return diaDaSemana(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        } catch (NullPointerException e) {
            Log.e(TAG, "Erro ao recuperar dia da semana da data: " + ddMMYYYY);
            return 0;
        }
    }

    public static String diaDaSemana(long datetime, boolean completo) {
        Calendar calendar = Calendar.getInstance();
        Date dataDaOcorrencia = new Date(datetime);
        calendar.setTime(dataDaOcorrencia);
        String diaDaSemana = calendar.getDisplayName(Calendar.DAY_OF_WEEK, completo ? Calendar.LONG : Calendar.SHORT, localeBR);
        return toCamelCase(diaDaSemana);
    }

    public static String diaDaSemana(int weekDay, boolean completo) {
        Calendar calendar = Calendar.getInstance();
        Map<String, Integer> displayNamesMap = calendar.getDisplayNames(Calendar.DAY_OF_WEEK, completo ? Calendar.LONG : Calendar.SHORT, localeBR);
        String diaDaSemana = (String) getKeyFromValue(displayNamesMap, weekDay);
        return toCamelCase(diaDaSemana);
    }

    public static String dataComNomeDoDia(long datetime, boolean completo, String separator) {
        String dataFormatada = new SimpleDateFormat("dd/MM/yyyy", localeBR).format(new Date(datetime));
        return diaDaSemana(datetime, completo) + separator + dataFormatada;
    }

    public static String dataComNomeDoDia(long datetime) {
        return dataComNomeDoDia(datetime, true, ", ");
    }

    public static String dataComDiaEHora(long datetime) {
        Calendar calendar = Calendar.getInstance();
        Date dataDaOcorrencia = new Date(datetime);
        calendar.setTime(dataDaOcorrencia);
        return new SimpleDateFormat("dd/MM/yyyy (HH:mm:ss)", localeBR).format(dataDaOcorrencia);
    }

    public static String dataComUnderlines(long datetime) {
        Calendar calendar = Calendar.getInstance();
        Date dataDaOcorrencia = new Date(datetime);
        calendar.setTime(dataDaOcorrencia);
        return new SimpleDateFormat("dd_MM_yyyy_HH-mm-ss", localeBR).format(dataDaOcorrencia);
    }

    public static String dataComDiaEHoraPorExtenso(long datetime) {
        Calendar calendar = Calendar.getInstance();
        Date dataDaOcorrencia = new Date(datetime);
        calendar.setTime(dataDaOcorrencia);
        return new SimpleDateFormat("dd/MM/yyyy' às 'HH:mm", localeBR).format(dataDaOcorrencia);
    }

    public static String dataComDiaMesEHoraPorExtenso(long datetime) {
        Calendar calendar = Calendar.getInstance();
        Date dataDaOcorrencia = new Date(datetime);
        calendar.setTime(dataDaOcorrencia);
        return new SimpleDateFormat("dd' de 'MMMM' às 'HH:mm", localeBR).format(dataDaOcorrencia);
    }

    public static String dataComDiaDoMesEHoraPorExtenso(long datetime) {
        Calendar calendar = Calendar.getInstance();
        Date dataDaOcorrencia = new Date(datetime);
        calendar.setTime(dataDaOcorrencia);
        return new SimpleDateFormat("'Dia 'dd' às 'HH:mm", localeBR).format(dataDaOcorrencia);
    }

    public static AlertDialog alert(Context context, String title, String message, final String positiveButton, final DialogInterface.OnClickListener positiveListener, String negativeButton, final DialogInterface.OnClickListener negativeListener, final String neutralButton, final DialogInterface.OnClickListener neutralListener) {
        AlertDialog.Builder bld = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        bld
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(title)
                .setMessage(Html.fromHtml(message))
                .setCancelable(false)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (positiveListener != null)
                            positiveListener.onClick(dialog, which);
                    }
                });

        if (negativeButton != null) {
            bld.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (negativeListener != null)
                        negativeListener.onClick(dialog, which);
                }
            });
        }

        if (neutralButton != null)
            bld.setNeutralButton(neutralButton, neutralListener);

        AlertDialog alerta = bld.create();
        alerta.show();
        return alerta;
    }

    public static String dataComDiaDaSemanaEHoraPorExtenso(long datetime) {
        Calendar calendar = Calendar.getInstance();
        Date dataDaOcorrencia = new Date(datetime);
        calendar.setTime(dataDaOcorrencia);
        String diaDaSemana = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, localeBR);
        return toCamelCase(diaDaSemana) + new SimpleDateFormat("' às 'HH:mm", localeBR).format(dataDaOcorrencia);
    }

    public static String dataComHoraPorExtenso(long datetime) {
        Calendar calendar = Calendar.getInstance();
        Date dataDaOcorrencia = new Date(datetime);
        calendar.setTime(dataDaOcorrencia);
        return new SimpleDateFormat("'Às 'HH:mm", localeBR).format(dataDaOcorrencia);
    }

    /* Fim conversões e formatações de data */
    /* Início tratamento e manipulação de strings */

    public static String removeAcentuacao(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String formatarAltura(double altura) {
        return String.format("%.0f", altura);
    }

    public static String formatarPesoOuImc(double pesoOuImc) {
        return String.format("%.1f", pesoOuImc);
    }

    public static String toNameCase(String input) {
        return toTitleCase(input, new String[]{"de", "do", "dos", "da", "e"});
    }

    public static String firstNames(String input) {
        String[] words = input.split("\\s+");
        if (words.length == 0) {
            return input;
        }
        if (words.length == 1) {
            return Character.toUpperCase(words[0].charAt(0)) + words[0].substring(1).toLowerCase();
        }
        return Character.toUpperCase(words[0].charAt(0)) + words[0].substring(1).toLowerCase() + " " +
                Character.toUpperCase(words[words.length - 1].charAt(0)) + words[words.length - 1].substring(1).toLowerCase();
    }

    public static String toTitleCase(String input, String[] exceptWords) {
        if (TextUtils.isEmpty(input)) return "";

        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        List<String> exceptWordsList = Arrays.asList(exceptWords);

        if (words[0].length() > 0) {
            for (int i = 0; i < words.length; i++) {
                if (exceptWordsList.contains(words[i].toLowerCase())) {
                    sb.append(words[i].toLowerCase());
                } else {
                    sb.append(Character.toUpperCase(words[i].charAt(0)));
                    sb.append(words[i].subSequence(1, words[i].length()).toString().toLowerCase());
                }

                if (i < words.length - 1) {
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }

    public static String toCamelCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        input = input.toLowerCase();
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }

    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }


    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).toString().equals(value.toString())) {
                return o;
            }
        }
        return null;
    }

    /* Fim tratamento e manipulação de strings */


    public static Animation expand(final View v, Animation.AnimationListener listener) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        a.setAnimationListener(listener);
        return a;
    }

    public static Animation collapse(final View v, Animation.AnimationListener listener) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        a.setAnimationListener(listener);
        return a;
    }

    public static String dataNoformatoDoSQLite(Date data) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dataFormatada = formatter.format(data);
        return dataFormatada;
    }

    public static String dataNoformatoBrasileiro(Date data) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dataFormatada = formatter.format(data);
        return dataFormatada;
    }

    public static Date converteDoFormatoBrasileitoParaDate(String data) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(data);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date converteDoFormatoSQLParaDate(String data) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(data);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String moedaNoFormatoBrasileiro(double valor) {
        NumberFormat formatoBrasileiro = DecimalFormat.getCurrencyInstance(new Locale("pt", "br"));
        return formatoBrasileiro.format(valor).
                replace("R$", "R$ ").
                replace("-R$", "R$ -");
    }
    public static TextWatcher monetario(final EditText ediTxt) {
        return new TextWatcher() {
            // Mascara monetaria para o preço do produto
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    ediTxt.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted.replaceAll("[R$]", "");

                    ediTxt.setText(current);
                    ediTxt.setSelection(current.length());

                    ediTxt.addTextChangedListener(this);
                }
            }

        };
    }
}
