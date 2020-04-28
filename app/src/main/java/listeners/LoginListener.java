
package listeners;


/**
 * Rozhranie, ktore obsahuje callbacky, ktore zachytavaju proces pocas overovania prihlasenia pouzivatela a kontroluje jeho dostupnost na serveri.
 * <p>
 * Rozhranie je implementovane triedou {@link project.mtf.nsoric.aaurela.LoginActivity}
 */
public interface LoginListener {
    /**
     * Callback, zavolany pri zacati stahovania dostupnych pouzivatelov z API.
     */
    void onSigningStarted();

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
     * Callback zavolany ak sa nezhoduje meno, alebo heslo pouzivatela, ktory sa snazi prihlasit.
     */
    void onWrongUsernameOrPassword();

    /**
     * Callback, zavolany v momente ak je pouzivatel, ktory sa snazi prihlasit a zaroven jeho prihlasovacie udaje overene.
     */
    void onSignedIn();

    /**
     * Callback zavolany v momente ak bolo dokoncene stiahnutie userov z API do lokalnej DB.
     */
    void onDataForLoginAvailable();
}
