package helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Trieda pre pracu s datumami.
 */
public class DateSynchronizeHelper {

    private static final String TAG = "DateSynchronizeHelper";

    private SharedPreferences preferences;


    private final DateFormat dfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final DateFormat dfHistory = new SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.getDefault());

    public DateSynchronizeHelper(@NonNull Context context) {
        String NAME = context.getSharedPreferences(Constants.SHARED_PREF_AAURELA, Context.MODE_PRIVATE).getString(Constants.SP_SERVER_NAME_KEY, "default_server");
        this.preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    /**
     * Metoda, ktora vracia zoznam datumov od- do, ktore sa poslu na server pre stiahnutie meranych dat.
     *
     * @param daysToPast pocet dni dozadu
     * @return List dni vo formate String od-do
     */
    public List<String> getDateIntervalToFetch(int daysToPast) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -daysToPast);
        String day = "";
        for (int i = 0; i <= daysToPast; i++) {
            day = dfHistory.format(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            if (preferences.getInt(day, 0) == 0) {
                break;
            }
        }
        cal.clear();
        Calendar calNow = new GregorianCalendar();
        calNow.setTime(calNow.getTime());
        String today = dfNow.format(calNow.getTime());
        List<String> dates = new ArrayList<>();
        dates.add(0, day);
        dates.add(1, today);
        return dates;
    }

    /**
     * Metoda, ktora zapise do SharedPreferences dni, ktorych data boli stiahnute do DB.
     *
     * @param daysToPast pocet dni dozadu
     */
    public void setFetchedDays(int daysToPast) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(cal.getTime());
        preferences.edit().clear().apply();
        for (int i = 1; i <= daysToPast; i++) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            String day = dfHistory.format(cal.getTime());
            this.preferences.edit().putInt(day, 1).apply();
        }
    }

    /**
     * Metoda, ktora vracia datum a cas od ktoreho sa ma vyberat z DB.
     *
     * @param daysToPast pocet dni dozadu, pre ktore sa bude vyberat z DB
     * @return String datum, od ktoreho sa bude vyberat
     */
    public String getDateFromPastToLoadFrom(int daysToPast) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -daysToPast);
        return dfNow.format(cal.getTime());
    }

    /**
     * Metoda sluzi na premazanie starych datumov a merani z DB.
     * <p> Mazu sa zaznamy starsie ako {@param daysToPast}-1
     *
     * @param daysToPast pocet dni dozadu, ktore maju byt zmazane
     * @return String stary datum ktory ma byt zmazany
     */
    public String getDateFromPastToDeleteFrom(int daysToPast) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -daysToPast - 1);
        return dfHistory.format(cal.getTime());
    }

    /**
     * Vracia datum a cas spred 2 hodin.
     * Tento datum je pouzity pre podmienku pridavania hodnot do FIFO zasobniku pre vykreslenie trendu.
     *
     * @return datum
     */
    public Date getDateTimeForTrend() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(cal.getTime());
        cal.add(Calendar.HOUR_OF_DAY, -2);
        return cal.getTime();
    }
}
