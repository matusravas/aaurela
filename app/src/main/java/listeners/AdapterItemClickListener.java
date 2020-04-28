package listeners;

/**
 * Rozhranie, ktore obsahuje callbacky, ktore zachytavaju klik na polozku senzoru v {@link adapters.DashboardAdapter} alebo {@link adapters.FavouritesAdapter}.
 * <p>
 * Rozhranie je implementovane triedami {@link project.mtf.nsoric.aaurela.DashboardActivity}, {@link project.mtf.nsoric.aaurela.FavouritesActivity}
 */
public interface AdapterItemClickListener {
    /**
     * Callback, zavolany pri kliknuti na lubovolny senzor v adapteri.
     *
     * @param sensorID id senzora polozky adapteru, na ktory bolo kliknute.
     *                 Sluzi ako parameter v GraphView aktivite kde sa id senzora
     *                 posle do DB pre vyber meranych dat a inych udajov.
     */
    void onItemClick(int sensorID);

    /**
     * Callback, zavolany ked je senzor v adapteri pridany/odobrany z oblubenych.
     *
     * <p>Ak v {@link adapters.DashboardAdapter} nie su ziadne oblubene skryje sa moznost otvorit aktivitu {@link project.mtf.nsoric.aaurela.FavouritesActivity}
     * <p> Ak v {@link adapters.FavouritesAdapter} nie su oblubene zobrazi sa v textview placeholder ziadne oblubene.
     *
     * @param importantViewsCount pocet oblubenych v danom adapteri
     */
    void importantViewsCountChanged(int importantViewsCount);
}
