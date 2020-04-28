package helpers;

import android.app.Application;
import android.content.Context;

/**
 * Trieda, ktora sluzi pre vytvorenie globalneho kontextu celej aplikacie.
 */
public class GlobalContext extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    /**
     * Vracia globalny kontext.
     *
     * @return kontext aplikacie
     */
    public static Context getAppContext() {
        return appContext;
    }

}
