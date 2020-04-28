package nsoric.server.objects;

import android.content.Context;
import android.content.SharedPreferences;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import composites.CompositeDisposableDB;
import composites.CompositeDisposableServer;
import database.objects.db.AppDatabase;
import database.objects.db.dao.UserDao;
import database.objects.db.entity.User;
import helpers.Constants;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import listeners.LoginListener;
import project.mtf.nsoric.aaurela.BuildConfig;
import project.mtf.nsoric.aaurela.R;

/**
 * Trieda, ktora riesi cely proces prihlasovania.
 */
public class LoginManager {

    private UserDao userDao;
    private SharedPreferences sharedPreferences;

    private Context context;
    private LoginListener loginListener;
    private String client;
    private String hashedPass = "";
    private String username = "";

    private AppDatabase database;

    public LoginManager(Context context, LoginListener loginCallback) {
        this.context = context;
        this.client = context.getResources().getString(R.string.app_name) + "_" + BuildConfig.VERSION_NAME;
        database = AppDatabase.getInstance(context);
        userDao = database.userDao();
        this.sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_AAURELA, Context.MODE_PRIVATE);
        this.loginListener = loginCallback;

    }

    /**
     * Metoda, ktora overuje spravnost zadaneho mena a hesla.
     * <p>
     * Najskor sa vyberu z DB vsetci aktualne dostupni pouzivatelia a overi sa,
     * ci je medzi nimi pouzivatel zo zadanym menom a heslom.
     *
     * <p> Ak je pouzivatel autorizovany {@link project.mtf.nsoric.aaurela.LoginActivity} je notifikovana cez
     * {@link LoginListener} o dokonceni prihlasovania.
     *
     * <p> Ak dany pouzivatel s menom a heslom nie je autorizovany v
     * {@link project.mtf.nsoric.aaurela.LoginActivity} je cez {@link LoginListener} zobrazene
     * oznamenie o neuspenom prihlaseni.
     */
    public void verifyUser() {
        CompositeDisposableDB.clear();
        Disposable disposable = userDao.getAllUsers().subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<User>>() {
            @Override
            public void accept(List<User> users) {
                boolean userIsValid = false;
                for (User user : users) {
                    if (user.getLogin().equals(username) && user.getPassword().equals(hashedPass)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constants.SP_USER_ID_KEY, user.getId());
                        editor.putString(Constants.SP_FULL_NAME, user.getName());
                        editor.putString(Constants.SP_USERNAME_KEY, username);
                        editor.putString(Constants.SP_PASSWORD_KEY, hashedPass);
                        editor.apply();
                        userIsValid = true;
                        break;
                    }
                }
                if (userIsValid) {
                    loginListener.onSignedIn();
                } else loginListener.onWrongUsernameOrPassword();
            }
        });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Metoda, ktora stahuje zo serveru zoznam dostupnych pouzivatelov na serveri a uklada ich do DB.
     *
     * @param username   prihlasovacie meno
     * @param password   heslo
     * @param serverName adresa serveru
     * @param serverPort port serveru
     */
    public void fetchUsersToDatabase(final String username, final String password,
                                     final String serverName, final int serverPort) {
        //najskor sa heslo z editTextu zahasuje
        try {
            this.username = username;
            this.hashedPass = hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            loginListener.onNoServerResponse(1);
            e.printStackTrace();
        }
        CompositeDisposableServer.clear();
        if (!ClientSocket.hasNetworkConnection(context)) {
            loginListener.onNoServerResponse(0);
        } else {
            loginListener.onSigningStarted();
            final ApiDataPresenter apiDataPresenter = new ApiDataPresenter(this.username, this.hashedPass, serverName, serverPort, this.client);
            Completable.fromAction(new Action() {
                @Override
                public void run() throws Exception {
                    userDao.deleteAllUsers();
                    userDao.insertAllUsers(apiDataPresenter.getUsers());
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            CompositeDisposableServer.add(d);
                        }

                        @Override
                        public void onComplete() {
                            apiDataPresenter.clearApiDataLists();
                            loginListener.onDataForLoginAvailable();
                        }

                        @Override
                        public void onError(Throwable e) {
                            apiDataPresenter.clearApiDataLists();
                            loginListener.onNoServerResponse(1);
                        }
                    });
        }
    }


    /**
     * Metoda, ktora hashuje zadane heslo vo formate String do hash formatu MD5.
     *
     * @param pass nezahashovane heslo
     * @return zahashovane heslo
     */
    private String hashPassword(String pass) throws NoSuchAlgorithmException {
        MessageDigest mDigest = null;
        mDigest = MessageDigest.getInstance("MD5");
        mDigest.reset();
        mDigest.update(pass.getBytes(StandardCharsets.UTF_8));
        byte[] digest = mDigest.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        StringBuilder hashedPassword = new StringBuilder(bigInt.toString(16));
        while (hashedPassword.length() < 32) {
            hashedPassword.insert(0, "0");
        }
        return hashedPassword.toString();
    }

    /**
     * Zrusenie referencii
     */
    public void removeReferences() {
        this.context = null;
        this.database = null;
        this.sharedPreferences = null;
        this.loginListener = null;
    }
}
