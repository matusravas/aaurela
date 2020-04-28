package project.mtf.nsoric.aaurela;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import composites.CompositeDisposableDB;
import composites.CompositeDisposableServer;
import helpers.Constants;
import listeners.LoginListener;
import nsoric.server.objects.LoginManager;

public class LoginActivity extends AppCompatActivity implements LoginListener, View.OnClickListener {

    private CheckBox checkBox;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    private Context context;
    private SharedPreferences sharedPreferences;
    private LoginManager loginManager;
    private CoordinatorLayout coordinatorLayout;
    private String username;
    private String password;
    private String serverName;
    private int serverPort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        this.sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_AAURELA, MODE_PRIVATE);
        this.findViews();
        Intent intent = getIntent();
        this.serverName = intent.getStringExtra(Constants.SP_SERVER_NAME_KEY);
        this.serverPort = intent.getIntExtra(Constants.SP_SERVER_PORT_KEY, 0);
        boolean isSomeoneSigned = sharedPreferences.getBoolean(Constants.SP_IS_REMEMBERED_KEY, false);

        if (isSomeoneSigned) { //ak je niekto prihlaseny
            this.performLogIn();
        }
        //else caka sa na stlacenie tlacidla Log in
    }

    /**
     * Metoda volana metodou onClick (pri kliknuti na tlacidlo Prihlasit sa).
     */
    private void onLoginButtonClicked() {
        this.loginManager = new LoginManager(this, this);
        this.password = passwordEditText.getText().toString().trim();
        this.username = usernameEditText.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            Snackbar.make(coordinatorLayout, R.string.fill_all_fields_snackbar, Snackbar.LENGTH_LONG).show();
        } else loginManager.fetchUsersToDatabase(username, password, serverName, serverPort);
    }

    /**
     * Overenie ci ma prihlaseny pouzivatel vytvoreny zoznam oblubenych.
     *
     * @return true-ak ma  false-nema
     */
    private boolean hasUserImportantView() {
        int userID = this.sharedPreferences.getInt(Constants.SP_USER_ID_KEY, 0);
        return this.sharedPreferences.getBoolean(serverName + "_" + userID, false);
    }

    @Override
    public void onSigningStarted() {
        this.progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage(getString(R.string.dialog_message_signing));
        this.progressDialog.show();
    }


    @Override
    public void onDataForLoginAvailable() {
        CompositeDisposableServer.clear();
        loginManager.verifyUser();
    }

    @Override
    public void onWrongUsernameOrPassword() {
        this.cancelProgressDialog();
        Snackbar.make(coordinatorLayout, R.string.wrong_user_or_pass, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSignedIn() {
        this.cancelProgressDialog();
        CompositeDisposableDB.clear();
        this.saveSharedPref();
        this.performLogIn();
    }

    /**
     * Metoda ktora uklada udaje do systemoveho suboru SharedPreferences ci je niekto prihlaseny.
     */
    private void saveSharedPref() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        if (checkBox.isChecked()) {
            editor.putBoolean(Constants.SP_IS_REMEMBERED_KEY, true);
        }
        editor.apply();
    }

    /**
     * Callback, ktory je zavolany ak pride k neocakavanej chybe
     */
    @Override
    public void onNoServerResponse(int errorCode) {
        this.cancelProgressDialog();
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        if (errorCode == 0) { //ak nie je dostupna siet
            dialog.setTitle(R.string.dialog_no_network_title);
            dialog.setMessage(R.string.dialog_no_network_login_message);
        }
        if (errorCode == 1) {//ak nastala chyba v komunikacii
            dialog.setTitle(R.string.dialog_error_title);
            dialog.setMessage(R.string.server_not_available);
        }
        dialog.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }

            }
        });
        dialog.setPositiveButton(R.string.dialog_button_retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        dialog.cancel();
                        onLoginButtonClicked();
                        break;
                }
            }
        });
        dialog.create();
        dialog.show();
    }

    /**
     * Metoda, ktora spusta novu aktivitu.
     * <p>
     * Ak ma pouzivatel vytvoreny zoznam oblubenych je otvorena aktivita {@link FavouritesActivity},
     * <p>
     * ak nie otvori sa aktivita {@link DashboardActivity}.
     */
    public void performLogIn() {
        Intent intent;
        if (hasUserImportantView()) { //ak ma pouzivatel zoznam oblubenych
            intent = new Intent(this, FavouritesActivity.class);
            intent.putExtra(Constants.ACTIVITY_INTENT_CODE, Constants.LOGIN_ACTIVITY);
            startActivity(intent);
            this.finish();
        } else {
            intent = new Intent(this, DashboardActivity.class);
            intent.putExtra(Constants.ACTIVITY_INTENT_CODE, Constants.LOGIN_ACTIVITY);
            startActivity(intent);
            this.finish();
        }
    }

    private void cancelProgressDialog() {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.cancel();
        }
    }

    /**
     * Kliknutie na tlacidlo Prihlasit sa.
     *
     * @param v komponenta, na ktoru bolo kliknute
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                onLoginButtonClicked();
                break;
            case R.id.checkbox_remember_user:
                if (!checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else checkBox.setChecked(true);
                break;

        }
    }

    /**
     * Callback, zavolany, pri stlaceni tlacidla spat, otvori aktivitu {@link ServerSelectActivity}.
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ServerSelectActivity.class));
        this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (loginManager != null) {
            this.loginManager.removeReferences();
        }
        this.loginManager = null;
        super.onDestroy();
    }


    /**
     * Inicializacia grafickych prvkov a komponentov.
     */
    private void findViews() {
        Button loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(this);
        usernameEditText = findViewById(R.id.edit_text_username);
        username = sharedPreferences.getString(Constants.SP_USERNAME_KEY, "");
        this.passwordEditText = findViewById(R.id.edit_text_password);
        this.coordinatorLayout = findViewById(R.id.coordinator_layout_login);
        checkBox = findViewById(R.id.checkbox_remember_user);
        checkBox.setOnClickListener(this);
        TextView textViewServerName = findViewById(R.id.text_view_login_server);
        textViewServerName.setText(getResources().getString(R.string.login_server_name, sharedPreferences.getString(Constants.SP_SERVER_NAME_KEY, "")));
    }

}
