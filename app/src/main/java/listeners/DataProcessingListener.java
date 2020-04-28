package listeners;

/**
 * Rozhranie, ktore obsahuje callbacky, ktore zachytavaju rozne fazy stahovania dat z API,
 * vyberania z DB, pripadne umoznuje vyhodit chybu.
 *
 * <p>
 * Rozhranie je implementovane triedami {@link project.mtf.nsoric.aaurela.DashboardActivity},
 * {@link project.mtf.nsoric.aaurela.FavouritesActivity} a {@link project.mtf.nsoric.aaurela.GraphViewActivity}
 */
public interface DataProcessingListener {
    /**
     * Callback, zavolany pri zacati stahovania vsetkych dat zo servera.
     */
    void onServerSynchronizationBegin();

    /**
     * Callback, zavolany pri zacati stahovania len meranych hodnot.
     */
    void onMeasuredDataStartedLoading();

    /**
     * Callback, zavolany po dokonceni stahovania vsetkych dat zo serveru.
     */
    void onAllDataSynchronized();

    /**
     * Callback, ktory je volany po dokonceni stahovania meranych hodnot zo serveru.
     */
    void onMeasuredDataLoadedToDB();

    /**
     * Callback, zavolany pri chybe, ak server neodpoveda.
     * <p>
     * Dovodom moze byt nedostupne pripojenie k sieti errorCode=0
     * <p> nastane chyba v komunikacii medzi aplikaciou a serverom errorCode=1
     *
     * @param errorCode reprezentuje kod chyby<p>
     *                  0 ak nie je k dispozicii internetove pripojenie
     *                  1 ak nastala chyba v komunikacii
     */
    void onNoServerResponse(int errorCode);

    /**
     * Calback zavolany po dokonceni vyberania vsetkych dat pre aktivitu.
     */
    void onDataReady();
}
