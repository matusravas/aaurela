package project.mtf.nsoric.aaurela;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import composites.CompositeDisposableServer;
import helpers.Constants;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import nsoric.server.objects.ClientSocket;

/**
 * Aktivita, ktora pouzivatelovi umoznuje vybrat si adresu serveru a portu, na ktory sa chce prihlasit.
 */
public class ServerSelectActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextServerPort;
    private EditText editTextServerName;
    private SharedPreferences sharedPreferences;
    CoordinatorLayout coordinatorLayout;

    private String server, port;
    private String prefServer, prefPort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_select);
        this.findViews();
        this.sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_AAURELA, MODE_PRIVATE);
        prefServer = sharedPreferences.getString(Constants.SP_SERVER_NAME_KEY, "");
        prefPort = sharedPreferences.getString(Constants.SP_SERVER_PORT_KEY, "");
        this.editTextServerName.setText(prefServer);
        this.editTextServerPort.setText(prefPort);
    }

    /**
     * Metoda, ktora sa vykona pri kliknuti na tlacidlo Overit
     *
     * @param v-tlacidlo
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_go_to_login:
                canConnectToServer();
                break;
        }
    }

    /**
     * Metoda overujuca ci bude mozne kontaktovat server.
     * <p>
     * Kontrola sietoveho pripojenia alebo zmeny adresy serveru alebo portu,
     * ktory bol uz predtym ulozeny ako spravny.
     */
    private void canConnectToServer() {
        server = editTextServerName.getText().toString().trim();
        port = editTextServerPort.getText().toString().trim();
        if (server.isEmpty() || port.isEmpty()) {
            Snackbar.make(coordinatorLayout, R.string.fill_all_fields_snackbar, Snackbar.LENGTH_LONG).show();
        } else {
            //ak sa nazov serveru ani portu nezmenil oproti posledne overenemu ulozenemu v sharedPreferences
            if (server.equals(prefServer) && port.equals(prefPort)) {
                this.savePreferences();
                this.startLoginActivity();
            }
            //Ak existuje internetove pripojenie
            else if (ClientSocket.hasNetworkConnection(this)) {
                verifyServer();
            } else {
                onNoNetwork();
            }
        }
    }

    /**
     * Metoda overujuca spravnost zadaneho serveru a portu, skuska otvorenia spojenia.
     */
    private void verifyServer() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                ClientSocket.isServerAvailable(server, Integer.parseInt(port));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                CompositeDisposableServer.add(d);
            }

            @Override
            public void onComplete() {
                CompositeDisposableServer.clear();
                savePreferences();
                startLoginActivity();
            }

            @Override
            public void onError(Throwable e) {
                CompositeDisposableServer.clear();
                onServerDoesNotExist();
            }
        });
    }

    private void savePreferences() {
        SharedPreferences.Editor spEditor = this.sharedPreferences.edit();
        //ak sa zmenil nazov serveru alebo portu odhlasi sa aktualny pouzivatel a otvori sa SigningActivity
        if (!(prefServer.equals(server) && prefPort.equals(port))) {
            spEditor.putBoolean(Constants.SP_IS_REMEMBERED_KEY, false);
            spEditor.putBoolean(Constants.SP_ALL_DATA_SYNCHRONIZED, false);
            spEditor.putInt(Constants.SP_DAYS_TO_PAST_DB,1);
        }
        spEditor.putString(Constants.SP_SERVER_NAME_KEY, server);
        spEditor.putString(Constants.SP_SERVER_PORT_KEY, port);
        spEditor.apply();
    }

    /**
     * Metoda, ktora spusta {@link LoginActivity}.
     */
    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constants.SP_SERVER_NAME_KEY, server);
        intent.putExtra(Constants.SP_SERVER_PORT_KEY, Integer.valueOf(port));
        startActivity(intent);
        this.finish();
    }


    /**
     * Metoda spustena, ak neexistuje zadany server alebo port.
     */
    private void onServerDoesNotExist() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_error_title).setMessage(R.string.dialog_invalid_server_or_port);
        dialog.setCancelable(false);
        dialog.setPositiveButton(R.string.dialog_button_retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        dialog.cancel();
                        break;
                }
            }
        });
        dialog.create();
        dialog.show();
    }

    /**
     * Metoda spustena, ak nie je aktivne sietove pripojenie.
     */
    private void onNoNetwork() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_no_network_title).setMessage(R.string.dialog_no_network_server_auth);
        dialog.setCancelable(false);
        dialog.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        });
        dialog.create();
        dialog.show();
    }

    /**
     * Inicializacia grafickych prvkov a komponentov.
     */
    private void findViews() {
        Button buttonConfirm = findViewById(R.id.button_go_to_login);
        buttonConfirm.setOnClickListener(this);
        this.editTextServerName = findViewById(R.id.edit_text_server);
        this.editTextServerPort = findViewById(R.id.edit_text_port);
        this.coordinatorLayout = findViewById(R.id.coordinator_layout_server_select);
    }
}
