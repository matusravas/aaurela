package listeners;

import database.objects.Repository;

/**
 * Rozhranie, ktore obsahuje callbacky pre pridanie/odobranie konkretneho senzora do zoznamu oblubenych.
 * <p>
 * Jednotlive callbacky su volane pri kliknuti na pridat do oblubenych.
 * Rozhranie je implementovane v triede {@link Repository},
 * <p>
 * kde je riesena logika pridania/odobratia do/zo zoznamu oblubenych.
 */
public interface ImportantViewListener {
    /**
     * Callback, ktory je zavolany ak je senzor pridany do zoznamu oblubenych.
     *
     * @param sensorID id senzora, ktory ma byt pridany do oblubenych
     */
    void onImportantViewAdded(int sensorID);//,int position);

    /**
     * Callback, ktory je zavolany ak je senzor odstraneny zo zoznamu oblubenych.
     *
     * @param sensorID id senzora, ktory ma byt odstraneny z oblubenych
     */
    void onImportantViewRemoved(int sensorID);
}
