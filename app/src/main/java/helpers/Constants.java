package helpers;

import project.mtf.nsoric.aaurela.DashboardActivity;
import project.mtf.nsoric.aaurela.FavouritesActivity;
import project.mtf.nsoric.aaurela.GraphViewActivity;
import project.mtf.nsoric.aaurela.LoginActivity;

/**
 * Konsatnty pouzivane v projekte.
 */
public final class Constants {
    public static final String SP_USERNAME_KEY = "username"; //prihlasovacie meno
    public static final String SP_PASSWORD_KEY = "password"; //prihlasovacie heslo
    public static final String SP_SERVER_NAME_KEY = "server"; //server
    public static final String SP_SERVER_PORT_KEY = "port"; //port
    public static final String SP_IS_REMEMBERED_KEY = "remembered"; // true/false podla toho ci je niekto prihlaseny
    public static final String SP_USER_ID_KEY = "user_id";// reprezentuje id usera, true/false ak ma/ nema obluene
    public static final String SHARED_PREF_AAURELA = "aauerela"; //nazov SharedPreferences suboru
    public static final String SP_ALL_DATA_SYNCHRONIZED = "has_all_data"; // true/false ak boli stiahnute senzory,sektory,...
    public static final String SP_DAYS_TO_PAST_DB = "db_period"; //nastavena doba vytahovania nameranych hodnot pre data z DB
    public static final String PREVIOUSLY_SYNCHRONIZED_KEY = "previously_synch"; // true/false pre intent ci boli hondoty synchronizovane v nejakej aktivite skor
    public static final String SP_FULL_NAME = "full_name";

    public static final int DAYS_TO_FETCH_SERVER = 7; //pocet dni, ktore sa stahuju zo serveru

    public static final String SENSOR_ID = "sensor_id";

    /**
     * reprezentuje kod {@link DashboardActivity}
     */
    public static final int DASHBOARD_ACTIVITY = 0;
    /**
     * reprezentuje kod {@link FavouritesActivity}
     */
    public static final int FAVOURITES_ACTIVITY = 1;
    /**
     * reprezentuje kod {@link LoginActivity}
     */
    public static final int LOGIN_ACTIVITY = 2;
    /**
     * reprezentuje kod {@link GraphViewActivity}
     */
    public static final int GRAPH_VIEW_ACTIVITY = 3;
    /**
     * Hodnota klucu intentu k spustajucim aktivitam
     */
    public static final String ACTIVITY_INTENT_CODE = "project.mtf.nsoric.aaurela";

}
