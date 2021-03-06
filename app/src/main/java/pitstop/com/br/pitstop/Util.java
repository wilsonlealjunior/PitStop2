package pitstop.com.br.pitstop;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
        return String.format("%02d/%02d/%04d", dia, mes , ano);
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
    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).toString().equals(value.toString())) {
                return o;
            }
        }
        return null;
    }

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
}
