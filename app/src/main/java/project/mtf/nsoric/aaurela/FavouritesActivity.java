package project.mtf.nsoric.aaurela;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import adapters.FavouritesAdapter;
import database.objects.Repository;
import helpers.Constants;
import listeners.AdapterItemClickListener;
import listeners.DataProcessingListener;

/**
 * Aktivita, ktora zobrazuje prostrednictvom {@link FavouritesAdapter} zoznam oblubenych senzorov pre prihlaseneho pouzivatela.
 */
public class FavouritesActivity extends AppCompatActivity implements DataProcessingListener,
        AdapterItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener,
        SearchView.OnQueryTextListener {

    private TextView textViewPlaceholder;
    private FloatingActionButton fab;
    private Dialog dialogPeriod;
    private NumberPicker np;
    private ProgressDialog progressDialog;
    private Context context;
    private SwipeRefreshLayout refreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private FavouritesAdapter favouritesAdapter;
    private MenuItem searchMenuItem;
    private Repository repository;
    private SharedPreferences sharedPreferences;
    private boolean previouslySynchronized = false;
    private boolean hasNecessaryData;
    private int daysToPast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        context = this;
        initObjects();
        findViews();
        this.activityDataSetUp();
    }

    @Override
    protected void onResume() {
        daysToPast = sharedPreferences.getInt(Constants.SP_DAYS_TO_PAST_DB, 1);
        if (previouslySynchronized) {
            repository.loadDBDataForFavourites(daysToPast);
        }
        super.onResume();
    }

    /**
     * Metoda, ktora sluzi pre zistenie ci ma aktivita dostupne data, pripadne ci uz prebehla
     * synchronizacia nameranych hodnot v inej aktivite.
     */
    private void activityDataSetUp() {
        this.hasNecessaryData = sharedPreferences.getBoolean(Constants.SP_ALL_DATA_SYNCHRONIZED, false);
        this.previouslySynchronized = getIntent().getBooleanExtra(Constants.PREVIOUSLY_SYNCHRONIZED_KEY, false);
        if (!hasNecessaryData) {
            this.repository.downloadAllDataToDB();
        } else if (!previouslySynchronized) {
            this.repository.downloadMeasuredValuesToDB();
        }
        if (getIntent().getIntExtra(Constants.ACTIVITY_INTENT_CODE, -1) == Constants.LOGIN_ACTIVITY) {
            String message = getResources().getString(R.string.snackbar_signed, sharedPreferences.getString(Constants.SP_USERNAME_KEY, ""));
            Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback volany pri stlaceni plavajuceho tlacidla.
     *
     * @param v komponenta, na ktoru bolo kliknute
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                searchMenuItem.expandActionView();
                break;
            case R.id.button_date_picker:
                onDialogPeriodSet();
                break;
        }
    }

    /**
     * Callback, zavolany, ak bolo potiahnute pre aktualizaciu dat.
     */
    @Override
    public void onRefresh() {
        this.repository.downloadMeasuredValuesToDB();
    }

    /**
     * Callback spusteny pri vytvarani menu v aktivite.
     *
     * @param menu menu
     * @return true menu bolo vytvorene
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favourites_menu, menu);
        this.searchMenuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    /**
     * Callback, spusteny pri vybere polozky z menu.
     *
     * @param item polozka menu, ktora bola vybrana
     * @return true bola vybrata polozka menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_period:
                showDayPeriodDialog();
                break;

            case R.id.menu_synchronize_all_data:
                this.repository.downloadAllDataToDB();
                break;

            case R.id.menu_dashboard:
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.putExtra(Constants.PREVIOUSLY_SYNCHRONIZED_KEY, this.previouslySynchronized);
                intent.putExtra(Constants.ACTIVITY_INTENT_CODE, Constants.FAVOURITES_ACTIVITY);
                startActivity(intent);
                this.finish();
                break;

            case R.id.menu_about:
                Intent activityAbout = new Intent(this, AboutActivity.class);
                startActivity(activityAbout);
                break;
            case R.id.menu_logout:
                logOut();
                break;
        }
        return true;
    }


    @Override
    public void onServerSynchronizationBegin() {
        this.progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setMessage(getString(R.string.dialog_message_server_synchronization));
        this.progressDialog.show();
    }

    @Override
    public void onMeasuredDataStartedLoading() {
        this.refreshLayout.setRefreshing(true);
    }

    @Override
    public void onMeasuredDataLoadedToDB() {
        this.cancelRefreshingDialogs();
        this.repository.loadDBDataForFavourites(daysToPast);
        this.previouslySynchronized = true;
        Snackbar.make(coordinatorLayout, R.string.data_synchronized, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onAllDataSynchronized() {
        this.cancelRefreshingDialogs();
        this.repository.loadDBDataForFavourites(daysToPast);
        this.sharedPreferences.edit().putBoolean(Constants.SP_ALL_DATA_SYNCHRONIZED, true).apply();
        this.previouslySynchronized = true;
        Snackbar.make(coordinatorLayout, R.string.data_synchronized, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onDataReady() {
        favouritesAdapter.notifyDataSetChanged();
        checkFavouritesAvailability(favouritesAdapter.getItemCount());
        if (favouritesAdapter.getItemCount() <= 3) {
            fab.hide();
        } else fab.show();
    }

    @Override
    public void onNoServerResponse(int errorCode) {
        this.cancelRefreshingDialogs();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        if (errorCode == 0) { //ak nie je dostupna siet
            dialog.setTitle(R.string.dialog_no_network_title);
            dialog.setMessage(R.string.dialog_no_network_message);
        }
        if (errorCode == 1) { //ak nastala chyba v komunikacii
            dialog.setTitle(R.string.dialog_error_title);
            dialog.setMessage(R.string.dialog_server_error_message);
        }
        dialog.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == android.app.AlertDialog.BUTTON_NEGATIVE) {
                    dialog.cancel();
                    repository.loadDBDataForFavourites(daysToPast);
                }

            }
        });
        dialog.setPositiveButton(R.string.dialog_button_retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == android.app.AlertDialog.BUTTON_POSITIVE) {
                    dialog.cancel();
                    activityDataSetUp();
                }
            }
        });
        dialog.create();
        dialog.show();
    }

    private void cancelRefreshingDialogs() {
        if (this.refreshLayout.isRefreshing()) {
            this.refreshLayout.setRefreshing(false);
        }
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.cancel();
        }
    }

    @Override
    public void onItemClick(int sensorID) {
        Intent intent = new Intent(this, GraphViewActivity.class);
        intent.putExtra(Constants.SENSOR_ID, sensorID);
        startActivity(intent);
    }

    @Override
    public void importantViewsCountChanged(int importantViewsCount) {
        checkFavouritesAvailability(importantViewsCount);
    }

    /**
     * Calback zavolany v pripade potvrdenia hladaneho retazca.
     *
     * @param query hladany retazec
     * @return false nefiltruj
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Callback zavolany pri zmene hladaneho retazca.
     *
     * @param newText hladany retazec
     * @return true filtruj
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        favouritesAdapter.getFilter().filter(newText);
        return true;
    }

    /**
     * Metoda zavolana pri kliknuti na polozku v menu odhlasit sa.
     */
    private void logOut() {
        this.repository.closeDatabase();
        this.repository.removeReferences();
        this.favouritesAdapter.setFavouriteSensors(null);
        this.sharedPreferences.edit().putBoolean(Constants.SP_IS_REMEMBERED_KEY, false).apply();
        startActivity(new Intent(this, ServerSelectActivity.class));
        this.finish();
    }

    @Override
    protected void onStop() {
        this.cancelRefreshingDialogs();
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        repository.removeReferences();
        this.repository = null;
        this.favouritesAdapter.clearReferences();
        this.favouritesAdapter = null;
        super.onDestroy();
    }


    /**
     * Inicializacia objektov.
     */
    private void initObjects() {
        this.sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_AAURELA, MODE_PRIVATE);
        favouritesAdapter = FavouritesAdapter.getInstance(this, null);
        this.repository = new Repository(this, this);
    }

    /**
     * Inicializacia grafickych prvkov a komponentov.
     */
    private void findViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewPlaceholder = findViewById(R.id.text_view_no_favourites);
        fab = findViewById(R.id.button_search);
        this.coordinatorLayout = findViewById(R.id.coordinator_layout_favourites);
        this.refreshLayout = findViewById(R.id.refresh_layout_favourites);
        this.refreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_favourites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(favouritesAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    fab.show();
                }
                if (dy > 2) {
                    fab.hide();
                }
            }
        });
    }

    /**
     * Zobrazi dialog pre zmenu zobrazovaneho rozsahu.
     */
    private void showDayPeriodDialog() {
        this.cancelRefreshingDialogs();
        dialogPeriod = new Dialog(this);
        dialogPeriod.setContentView(R.layout.dialog_period);
        TextView textView = dialogPeriod.findViewById(R.id.text_view_dialog_period_message);
        textView.setText(R.string.dialog_period_change_message);
        np = dialogPeriod.findViewById(R.id.number_picker);
        np.setWrapSelectorWheel(true);
        np.setMaxValue(Constants.DAYS_TO_FETCH_SERVER);
        np.setMinValue(1);
        np.setValue(daysToPast);
        Button dialogButton = dialogPeriod.findViewById(R.id.button_date_picker);
        dialogButton.setOnClickListener(this);
        dialogPeriod.show();
    }

    /**
     * Metoda zavolana pri kliknuti na tlacidlo "Potvrdit" v dialogu pre zmenu rozshau.
     * Na zaklade zvoleneho poctu dni sa uskutocni vyber udajov z DB pre konkretny senzor.
     */
    private void onDialogPeriodSet() {
        daysToPast = np.getValue();
        this.repository.loadDBDataForFavourites(daysToPast);
        sharedPreferences.edit().putInt(Constants.SP_DAYS_TO_PAST_DB, daysToPast).apply();
        dialogPeriod.cancel();
    }

    /**
     * Metoda overuje ci je pocet oblubenych v {@link FavouritesAdapter} vacsi ako 0.
     * <p> Ak ano zobrazi sa moznost menu otvorit aktivitu {@link FavouritesActivity}
     *
     * @param count pocet oblubenych v {@link FavouritesAdapter}
     */
    private void checkFavouritesAvailability(int count) {
        if (count <= 0) {
            textViewPlaceholder.setVisibility(View.VISIBLE);
        } else textViewPlaceholder.setVisibility(View.GONE);
    }
}
