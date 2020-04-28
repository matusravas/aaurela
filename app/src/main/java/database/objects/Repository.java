package database.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import adapters.DashboardAdapter;
import adapters.FavouritesAdapter;
import adapters.GraphViewData;
import composites.CompositeDisposableDB;
import composites.CompositeDisposableServer;
import database.objects.db.AppDatabase;
import database.objects.db.dao.AreaDao;
import database.objects.db.dao.GroupViewDao;
import database.objects.db.dao.ImportanatViewDao;
import database.objects.db.dao.MeasurementDao;
import database.objects.db.dao.SectorDao;
import database.objects.db.dao.SensorDao;
import database.objects.db.dao.SensorDateDao;
import database.objects.db.dao.SensorPropertiesDao;
import database.objects.db.dao.SensorSectorDao;
import database.objects.db.dao.SensorValueDao;
import database.objects.db.dao.TypeDao;
import database.objects.db.dao.UserDao;
import database.objects.db.entity.ImportantView;
import database.objects.db.pojo.DashboardSensor;
import database.objects.db.pojo.FavouriteSensor;
import database.objects.db.pojo.GraphSensor;
import database.objects.db.pojo.SensorType;
import database.objects.db.pojo.ValuesAndDates;
import helpers.Constants;
import helpers.DateSynchronizeHelper;
import helpers.FixedFifoQueue;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import listeners.DataProcessingListener;
import listeners.ImportantViewListener;
import nsoric.server.objects.ApiDataPresenter;
import nsoric.server.objects.ClientSocket;
import project.mtf.nsoric.aaurela.BuildConfig;
import project.mtf.nsoric.aaurela.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Trieda, ktora zaobstarava pracu s lokalnou databazou.
 * <p>
 * Vkladanie hodnot do DB, vybery z DB a odosielanie tychto hodnot prislusnym dataHolderom.
 */
public class Repository implements ImportantViewListener {
    private int DASHBOARD = 0;
    private int FAVOURITES = 1;
    private int GRAPH_VIEW = 2;
    private int ACTIVITY;

    //Dao
    private UserDao userDao;
    private SensorDao sensorDao;
    private TypeDao typeDao;
    private SensorSectorDao sensorSectorDao;
    private SectorDao sectorDao;
    private AreaDao areaDao;
    private MeasurementDao measurementDao;
    private SensorDateDao sensorDateDao;
    private SensorValueDao sensorValueDao;
    private SensorPropertiesDao sensorPropertiesDao;
    private GroupViewDao groupViewDao;
    private ImportanatViewDao importanatViewDao;

    //SharedPreferences
    private SharedPreferences sharedPreferences;

    //DataHolderi
    private DashboardAdapter dashboardAdapter;
    private FavouritesAdapter favouritesAdapter;
    private GraphViewData graphViewData;


    //Callback objekty
    private DataProcessingListener dataProcessingListener;

    private Context context;
    private AppDatabase database;
    private Resources resources;
    private DateSynchronizeHelper dateSynchronizeHelper;

    //lokalne premenne
    private List<Integer> sensorIDsList = new ArrayList<>();
    private List<SensorType> sensorTypesList = new ArrayList<>();
    private int daysToPastDB = 7;
    private int importantsCount = 0;

    private ApiDataPresenter apiDataPresenter;

    //connect data
    private String login;
    private String server;
    private String port;
    private String pass;
    private String client;
    private int userID;


    private final DateFormat dfWanted = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
    private final DateFormat dfOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    /**
     * Konstruktor
     *
     * @param context                kontext
     * @param dataProcessingListener callback pre notifikovanie aktivit o fazach stahovania/vyberania dat
     */
    public Repository(Context context, DataProcessingListener dataProcessingListener) {
        this.dashboardAdapter = DashboardAdapter.getInstance(null, this);
        this.favouritesAdapter = FavouritesAdapter.getInstance(null, this);
        this.graphViewData = GraphViewData.getInstance();
        this.database = AppDatabase.getInstance(context);
        this.dataProcessingListener = dataProcessingListener;
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(Constants.SHARED_PREF_AAURELA, MODE_PRIVATE);
        this.dateSynchronizeHelper = new DateSynchronizeHelper(this.context);
        this.resources = this.context.getResources();
        this.server = sharedPreferences.getString(Constants.SP_SERVER_NAME_KEY, "");
        createConnectData();
        this.apiDataPresenter = new ApiDataPresenter(login, pass, server, Integer.valueOf(port), client);
        this.sensorDao = database.sensorDao();
        this.sectorDao = database.sectorDao();
        this.sensorSectorDao = database.sensorSectorDao();
        this.areaDao = database.areaDao();
        this.measurementDao = database.measurementDao();
        this.sensorDateDao = database.sensorDateDao();
        this.sensorValueDao = database.sensorValueDao();
        this.sensorPropertiesDao = database.sensorPropertiesDao();
        this.importanatViewDao = database.importanatViewDao();
        this.groupViewDao = database.groupViewDao();
        this.typeDao = database.typeDao();
        this.userDao = database.userDao();
    }


    /**
     * Vyber z DB zoznamu {@link GraphSensor}, pre aktivitu grafu {@link project.mtf.nsoric.aaurela.GraphViewActivity}.
     * <p>
     * Pre dany senzor sa v tejto metode zistuju:
     * <p>
     * maximalna hodnota jeho merania, ako aj datum tohto merania,
     * <p>
     * minimalna hodnota jeho merania, ako aj datum tohto merania,
     * <p>
     * naposledy namerana hodnota, ako aj datum jej namerania
     * <p>
     * priemerna namerana hodnota na senzore.
     *
     * @param valuesAndDates zoznam nameranych hodnot, ktory sa namapuje na prislusny senzor
     */
    private void createGraphViewData(final List<ValuesAndDates> valuesAndDates) {
        CompositeDisposableDB.clear();
        Disposable disposable = sensorDao.getGraphSensor(sensorIDsList).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GraphSensor>() {
                    @Override
                    public void accept(GraphSensor graphSensor) throws Exception {
                        double maxValue = Integer.MIN_VALUE;
                        double minValue = Integer.MAX_VALUE;
                        double avgValue = 0;
                        String maxDate = "N/A";
                        String minDate = "N/A";
                        int size = valuesAndDates.size();
                        double sum = 0;
                        int i = 0;
                        Date date = new Date();
                        try {
                            String uid = String.valueOf(Integer.parseInt
                                    (Integer.toBinaryString(Integer.valueOf(graphSensor.getUid())).
                                            substring(16), 2));
                            graphSensor.setSensorName(uid + ": " + graphSensor.getSensorName());
                        } catch (NumberFormatException e) {
                            //nema uid Integer cislo
                        }
                        for (ValuesAndDates values : valuesAndDates) {
                            /**
                             * Zistovanie minimalnej a maximalnej nameranej hodnoty.
                             * Minimalna a maximalna merana hodnota budu vzdy ulozene na 0-lty index.
                             */
                            if (values.getValue() >= maxValue) {
                                maxValue = values.getValue();
                                maxDate = values.getDate();
                                date = dfOriginal.parse(maxDate);
                                maxDate = dfWanted.format(date);
                                graphSensor.setMaxValue((int) (maxValue * 10) / 10.0);
                                graphSensor.setMaxDate(maxDate);
                            }
                            if (values.getValue() <= minValue) {
                                minValue = values.getValue();
                                minDate = values.getDate();
                                date = dfOriginal.parse(minDate);
                                minDate = dfWanted.format(date);
                                graphSensor.setMinValue((int) (minValue * 10) / 10.0);
                                graphSensor.setMinDate(minDate);
                            }
                            i++;
                            sum += values.getValue();
                            graphSensor.setValue((int) (values.getValue() * 10) / 10.0);
                            date = dfOriginal.parse(values.getDate());
                            graphSensor.setDate(dfWanted.format(date));
                        }
                        if (sensorTypesList.get(0) == null || sensorTypesList.get(0).getUnit().isEmpty()) {
                            graphSensor.setUnit("N/A");
                        } else graphSensor.setUnit(sensorTypesList.get(0).getUnit());

                        //Priemerna hodnota
                        avgValue = ((int) (sum / i * 10) / 10.0);
                        graphSensor.setAverageValue(avgValue);
                        graphViewData.setGraphSensor(graphSensor);
                        loadImportantViewsForUser();
                    }
                });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Vyber z DB zoznamu ID areii, ktore ma pouzivatel pravo prezerat.
     */
    private void loadAreaIDsForUser() {
        CompositeDisposableDB.clear();
        Disposable disposable = groupViewDao.getAllAreaIDsforUser(userID).
                subscribeOn(Schedulers.io()).
                subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        loadSectorIDsForUser(integers);
                    }
                });
        CompositeDisposableDB.add(disposable);
    }


    /**
     * Vyber z DB zoznamu vsetkych sektorov, na ktore ma pouzivatel pravo.
     * <p>
     * Zoznam ID sektorov sa z DB vytahuje na zaklade zoznamu ID areii.
     *
     * @param areaIDsList- zoznam ID areii, na ktore ma pouzivatel prava prezerania
     */
    private void loadSectorIDsForUser(List<Integer> areaIDsList) {
        CompositeDisposableDB.clear();
        Disposable disposable = sectorDao.getAllSectorsInAreas(areaIDsList).
                subscribeOn(Schedulers.io()).
                subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        loadSensorIDsForSectors(integers);
                    }
                });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Vyber z DB zoznamu ID senzorov.
     * <p>
     * Zoznamu ID senzorov vyberanych z DB sa vybera na zaklade zoznamu ID sektorov,
     * na ktore ma pouzivatel prava.
     * <p>
     * Nasledne na zaklade tohto zoznamu ID senzorov, sa budu z DB vyberat ID merani.
     *
     * @param sectorsIDsList zoznam ID sektorov, na ktore ma pouzivatel prava prezerania
     */
    private void loadSensorIDsForSectors(List<Integer> sectorsIDsList) {
        CompositeDisposableDB.clear();
        Disposable disposable = sensorDao.getSensorInSectorIDs(sectorsIDsList).subscribeOn(Schedulers.io()).
                subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        sensorIDsList = integers;
                        loadSensorTypes(integers);
                    }
                });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Vyber udajov z DB tabulky type_group.
     * Vytiahnuty zoznam {@link SensorType} sa ulozi do lokalnej premennej.
     * <p>
     * V metodach:
     * <p>
     * {@link #createDashboardData(List<ValuesAndDates>)}
     * <p>
     * {@link #createFavouritesData(List<ValuesAndDates>)}
     * <p>
     * {@link #createGraphViewData(List<ValuesAndDates>)}
     * <p>
     * sa tento zoznam mapuje, priradovanim fyzikalnej veliciny merani ku konkretnemu senzoru.
     * <p>
     * Metoda nasledne vola metodu
     * {@link #loadMeasurementIDsForSensors(List<Integer>)}, ktorej parameter je prislusny zoznam ID senzorov.
     *
     * @param sensorIDsList zoznam ID senzorov, pre ktore sa bude z DB vyberat
     */
    private void loadSensorTypes(final List<Integer> sensorIDsList) {
        CompositeDisposableDB.clear();
        Disposable disposable = typeDao.getTypesForSensors(sensorIDsList).subscribeOn(Schedulers.io()).
                subscribe(new Consumer<List<SensorType>>() {
                    @Override
                    public void accept(List<SensorType> sensorTypes) throws Exception {
                        sensorTypesList = sensorTypes;
                        loadMeasurementIDsForSensors(Repository.this.sensorIDsList);
                    }
                });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Vyber z DB zoznamu ID merani pre zoznam ID dostupnych senzorov.
     * <p>
     * Ak je ukonceny vyber z DB, zavola sa metoda
     * <p>
     * {@link #loadValuesAndDates(List<Integer>)}, kde paramter je zoznam ID merani.
     *
     * @param sensorIDsList- zoznam ID dostupnych senzorov pre pouzivatela
     */
    private void loadMeasurementIDsForSensors(List<Integer> sensorIDsList) {
        CompositeDisposableDB.clear();
        Disposable disposable = measurementDao.getMeasurementIDs(sensorIDsList).subscribeOn(Schedulers.io()).
                subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        loadValuesAndDates(integers);
                    }
                });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Vyber hodnot z DB {@link ValuesAndDates#value} {@link ValuesAndDates#date} {@link ValuesAndDates#sensorID}.
     * <p>
     * Parametre pre vyber z DB- zoznam measurementIDs, 7dni stary datum (vybera hodnoty novsie ako 7 dni stary datum z DB)
     * <p>
     * Po dokonceni metoda posiela zoznam {@link ValuesAndDates}, prislusnej metode, podla toho,
     * pre aku aktivitu su data vyberane.
     *
     * @param measurementIDsList- zoznam ID merani
     */
    private void loadValuesAndDates(final List<Integer> measurementIDsList) {
        CompositeDisposableDB.clear();
        Disposable disposable =
                sensorValueDao.getAllValuesAndDates(measurementIDsList,
                        dateSynchronizeHelper.getDateFromPastToLoadFrom(daysToPastDB)).
                        subscribeOn(Schedulers.io()).
                        subscribe(new Consumer<List<ValuesAndDates>>() {
                            @Override
                            public void accept(List<ValuesAndDates> valuesAndDates) throws Exception {
                                if (ACTIVITY == DASHBOARD) {
                                    createDashboardData(valuesAndDates);
                                }
                                if (ACTIVITY == FAVOURITES) {
                                    createFavouritesData(valuesAndDates);
                                }
                                if (ACTIVITY == GRAPH_VIEW) {
                                    graphViewData.setValuesAndDates(valuesAndDates);
                                    createGraphViewData(valuesAndDates);
                                }
                            }
                        });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Vyber z DB zoznamu dostupnych senzorov, na zaklade zoznamu ID sektorov.
     * <p>
     * Udaje su vo forme zoznamu, objektov {@link DashboardSensor}.
     *
     * <p>
     * Po vytiahnuti udajov z DB, metoda predpripravuje data pre odoslanie {@link DashboardAdapter}.
     * <p>
     * Ak je dana polozka zoznamu Area- typeForAdapter = -1
     * <p>
     * Ak je dana polozka zoznamu Sektor- typeForAdapter = 0
     * <p>
     * Ak je dana polozka zoznamu Senzor- typeForAdapter = 1, priradi sa jej naposledy merana hodnota,
     * datum merania, fyzikalna jednotka merania takto naparovane
     *
     * <p>
     * Na zaklade hodnoty atributu typeForAdapter sa v {@link DashboardAdapter} vykresli adekvanty layout.
     * <p>
     * Takto pripravene data sa odoslu triede {@link DashboardAdapter}.
     *
     * @param valuesAndDates- zoznam ValuesAndDates, ktoreho hodnoty sa priraduju jednotlivym senzorom
     */
    private void createDashboardData(final List<ValuesAndDates> valuesAndDates) {
        CompositeDisposableDB.clear();
        Disposable disposable = sensorDao.getAllDashboardSensors(sensorIDsList).subscribeOn(Schedulers.io()).
                subscribe(new Consumer<List<DashboardSensor>>() {
                    @Override
                    public void accept(List<DashboardSensor> dashboardSensors) throws Exception {
                        List<DashboardSensor> dashboardSensorList = new ArrayList<>();
                        int curArea = 0;
                        int curSector = 0;
                        int curSensor = 0;
                        Date date;
                        Date dateLastTwoHours = dateSynchronizeHelper.getDateTimeForTrend();
                        double trendValue = Double.MIN_VALUE;
                        double avg;
                        double sum = 0;
                        for (DashboardSensor dSensor : dashboardSensors) {
                            //ak je dalsi senzor z inej arei
                            if (curArea != dSensor.getAreaID()) {
                                curArea = dSensor.getAreaID();
                                DashboardSensor s = new DashboardSensor();
                                s.setAreaName(resources.getString(R.string.area_title, dSensor.getAreaName()));
                                s.setSectorName("");
                                s.setTypeForAdapter(-1);
                                dashboardSensorList.add(s);
                            }
                            //ak je dalsi senzor z ineho sektoru
                            if (curSector != dSensor.getSectorID()) {
                                curSector = dSensor.getSectorID();
                                DashboardSensor s = new DashboardSensor();
                                s.setSectorName(resources.getString(R.string.sector_title, dSensor.getSectorName()));
                                s.setAreaName(dSensor.getAreaName());
                                s.setTypeForAdapter(0);
                                dashboardSensorList.add(s);
                            }

                            //ak je senzor
                            dSensor.setSectorName(dSensor.getSectorName());
                            dSensor.setAreaName(dSensor.getAreaName());
                            dSensor.setTypeForAdapter(1);
                            try {
                                String uid = String.valueOf(Integer.parseInt
                                        (Integer.toBinaryString(Integer.valueOf(dSensor.getUid())).
                                                substring(16), 2));
                                dSensor.setSensorName(uid + ": " + dSensor.getSensorName());
                            } catch (NumberFormatException e) {
                                //nema uid Integer cislo
                            }
                            dSensor.setValue(Double.MIN_VALUE);
                            dSensor.setAvgValue(Double.MIN_VALUE);
                            switch (daysToPastDB) {
                                case 1:
                                    dSensor.setAvgText(resources.getString(R.string.average_text_single, daysToPastDB));
                                    dSensor.setDate(resources.getString(R.string.no_measurement_single, daysToPastDB));
                                    break;
                                case 2:
                                case 3:
                                case 4:
                                    dSensor.setAvgText(resources.getString(R.string.average_text_many1, daysToPastDB));
                                    dSensor.setDate(resources.getString(R.string.no_measurement_many1, daysToPastDB));
                                    break;
                                default:
                                    dSensor.setAvgText(resources.getString(R.string.average_text_many2, daysToPastDB));
                                    dSensor.setDate(resources.getString(R.string.no_measurement_many2, daysToPastDB));
                            }
                            dSensor.setTrend(-1, 0);
                            FixedFifoQueue myLastValues = new FixedFifoQueue(12);
                            if (valuesAndDates.size() != 0) {
                                int i = 0;
                                for (ValuesAndDates values : valuesAndDates) {
                                    if (values.getSensorID() == dSensor.getSensorID()) {
                                        sum += values.getValue();
                                        i++;
                                        date = dfOriginal.parse(values.getDate());
                                        dSensor.setDate(dfWanted.format(date));
                                        dSensor.setValue((int) (values.getValue() * 10) / 10.0);
                                        if ((date.getTime() > dateLastTwoHours.getTime()) &&
                                                TimeUnit.HOURS.convert((date.getTime() - dateLastTwoHours.getTime()),
                                                        TimeUnit.MILLISECONDS) < 2) {
                                            dSensor.setTrend(1, 0);
                                            myLastValues.add(dSensor.getValue());
                                        }
                                    }
                                }
                                trendValue = (Double) myLastValues.getTrend();
                                myLastValues = null;
                                if (dSensor.getTrend() != -1 || trendValue != Double.NaN) {
                                    if (Math.abs(trendValue) < 0.15) {
                                        dSensor.setTrend(1, 0);
                                    } else dSensor.setTrend(trendValue > 0 ? 2 : 0, trendValue);
                                }
                                //Priemerna hodnota
                                if (i != 0) {
                                    avg = ((int) (sum / i * 10) / 10.0);
                                    dSensor.setAvgValue(avg);
                                }
                                sum = i = 0;

                            }
                            //Nastavovanie fyzikalnych jednotiek senzoru
                            for (SensorType sType : sensorTypesList) {
                                if (dSensor.getSensorID() == sType.getSensorID()) {
                                    if (sType.getUnit() == null || sType.getUnit().isEmpty()) {
                                        dSensor.setUnit("N/A");
                                    } else dSensor.setUnit(sType.getUnit());
                                }
                            }
                            dashboardSensorList.add(dSensor);
                        }
                        dashboardAdapter.setDashboardSensors(dashboardSensorList);
                        valuesAndDates.clear();
                        loadImportantViewsForUser();
                    }
                });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Vyber z DB zoznamu oblubenych senzorov pre prihlaseneho pouzivatela.
     * <p>
     * Ak bola metoda volana z {@link project.mtf.nsoric.aaurela.DashboardActivity},
     * zoznam oblubenych sa posle {@link DashboardAdapter}.
     * <p>
     * Ak bola metoda volana z {@link project.mtf.nsoric.aaurela.GraphViewActivity} zisti sa, ci
     * zvoleny senzor je prave v oblubenych.
     * <p>
     * Tato metoda je volana ako posledna pri vyberoch z DB.
     */
    private void loadImportantViewsForUser() {
        CompositeDisposableDB.clear();
        Disposable disposable = importanatViewDao.getAllImportantViewsForUser(userID).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<List<ImportantView>>() {
                    @Override
                    public void accept(List<ImportantView> importantViews) throws Exception {
                        Repository.this.importantsCount = importantViews.size();
                        if (ACTIVITY == DASHBOARD) {
                            dashboardAdapter.setImportantViews(importantViews);
                            dataProcessingListener.onDataReady();
                        }
                        if (ACTIVITY == FAVOURITES) {
                            dataProcessingListener.onDataReady();
                        }
                        if (ACTIVITY == GRAPH_VIEW) {
                            for (int i = 0; i < sensorIDsList.size(); i++) {
                                for (int j = 0; j < importantsCount; j++) {
                                    if ((sensorIDsList.size() != 0 && importantViews.size() != 0) &&
                                            (importantViews.get(j).getSensorID() == sensorIDsList.get(i))) {
                                        graphViewData.setIsImportant(true);
                                    }
                                }
                            }
                            dataProcessingListener.onDataReady();
                        }
                        Repository.this.clearDBWorkerLists();
                    }
                });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Vyber z DB zoznam ID senzorov, ktore patria do zoznam oblubenych.
     * <p>
     * Metoda nasledne vola metodu
     * {@link #loadSensorTypes(List<Integer>)}, ktorej posle zoznam oblubenych senzorov.
     */
    private void loadFavouriteSensorsIDs() {
        CompositeDisposableDB.clear();
        Disposable disposable = sensorDao.getFavouriteSensorIDs(userID).subscribeOn(Schedulers.io()).
                subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        sensorIDsList = integers;
                        loadSensorTypes(Repository.this.sensorIDsList);
                    }
                });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Metoda, ktora z DB vybera zoznam oblubenych senzorov.
     * <p>
     * Po dokonceni vyberu z DB, sa naparuju jednotlive posledne merania a ich datumy,
     * fyzikalne velicicny merani na jednotlive senzory tohto zoznamu.
     * <p>
     * Takto upraveny zoznam sa posiela triede {@link FavouritesAdapter}.
     *
     * @param valuesAndDates zoznam nameranych hodnot, ktory sa mapuje na jednotlive senzory
     */
    private void createFavouritesData(final List<ValuesAndDates> valuesAndDates) {
        CompositeDisposableDB.clear();
        Disposable disposable = sensorDao.getAllFavouriteSensors(sensorIDsList, userID).subscribeOn(Schedulers.io()).
                subscribe(new Consumer<List<FavouriteSensor>>() {
                    @Override
                    public void accept(List<FavouriteSensor> favouriteSensors) throws Exception {
                        List<FavouriteSensor> favouriteSensorList = new ArrayList<>();
                        Date date;
                        Date dateLastTwoHours = dateSynchronizeHelper.getDateTimeForTrend();
                        double trendValue = Double.MIN_VALUE;
                        double sum = 0;
                        int i = 0;
                        double avg;
                        for (FavouriteSensor fSensor : favouriteSensors) {
                            fSensor.setDate(resources.getString(R.string.no_measurement_date, daysToPastDB));
                            fSensor.setValue(Double.MIN_VALUE);
                            fSensor.setAvgValue(Double.MIN_VALUE);
                            switch (daysToPastDB) {
                                case 1:
                                    fSensor.setAvgText(resources.getString(R.string.average_text_single, daysToPastDB));
                                    fSensor.setDate(resources.getString(R.string.no_measurement_single, daysToPastDB));
                                    break;
                                case 2:
                                case 3:
                                case 4:
                                    fSensor.setAvgText(resources.getString(R.string.average_text_many1, daysToPastDB));
                                    fSensor.setDate(resources.getString(R.string.no_measurement_many1, daysToPastDB));
                                    break;
                                default:
                                    fSensor.setAvgText(resources.getString(R.string.average_text_many2, daysToPastDB));
                                    fSensor.setDate(resources.getString(R.string.no_measurement_many2, daysToPastDB));
                            }
                            try {
                                String uid = String.valueOf(Integer.parseInt
                                        (Integer.toBinaryString(Integer.valueOf(fSensor.getUid())).
                                                substring(16), 2));
                                fSensor.setSensorName(uid + ": " + fSensor.getSensorName());
                            } catch (NumberFormatException e) {
                                //nema uid Integer cislo
                            }
                            fSensor.setTrend(-1, 0);
                            FixedFifoQueue myLastValues = new FixedFifoQueue(12);
                            if (valuesAndDates.size() != 0) {
                                for (ValuesAndDates values : valuesAndDates) {
                                    if (values.getSensorID() == fSensor.getSensorID()) {
                                        sum += values.getValue();
                                        i++;
                                        date = dfOriginal.parse(values.getDate());
                                        fSensor.setDate(dfWanted.format(date));
                                        fSensor.setValue((int) (values.getValue() * 10) / 10.0);
                                        if ((date.getTime() > dateLastTwoHours.getTime()) &&
                                                TimeUnit.HOURS.convert((date.getTime() - dateLastTwoHours.getTime()),
                                                        TimeUnit.MILLISECONDS) < 2) {
                                            fSensor.setTrend(1, 0);
                                            myLastValues.add(fSensor.getValue());
                                        }
                                    }
                                }
                                trendValue = (Double) myLastValues.getTrend();
                                myLastValues = null;
                                if (fSensor.getTrend() != -1 || trendValue != Double.NaN) {
                                    if (Math.abs(trendValue) < 0.15) {
                                        fSensor.setTrend(1, 0);
                                    } else fSensor.setTrend(trendValue > 0 ? 2 : 0, trendValue);
                                }
                                //Priemerna hodnota
                                if (i != 0) {
                                    avg = ((int) (sum / i * 10) / 10.0);
                                    fSensor.setAvgValue(avg);
                                }
                                sum = i = 0;

                            }
                            //Nastavovanie fyzikalnych jednotiek senzoru
                            for (SensorType sType : sensorTypesList) {
                                if (sType.getSensorID() == fSensor.getSensorID()) {
                                    if (sType.getUnit() == null || sType.getUnit().isEmpty()) {
                                        fSensor.setUnit("N/A");
                                    } else fSensor.setUnit(sType.getUnit());
                                }
                            }
                            favouriteSensorList.add(fSensor);
                        }
                        favouritesAdapter.setFavouriteSensors(favouriteSensorList);
                        valuesAndDates.clear();

                        loadImportantViewsForUser();
                    }
                });
        CompositeDisposableDB.add(disposable);
    }

    /**
     * Metoda, ktora spusti vyber dat z DB, pre aktivitu {@link project.mtf.nsoric.aaurela.DashboardActivity}
     * <p> Data z DB su vyberane za casovy interval na zaklade hodnoty kluca zo SharedPreferences {@value Constants#SP_DAYS_TO_PAST_DB}.
     * <p>
     * Vybrane data su poslane triede
     * {@link DashboardAdapter}
     * <p>
     * Nasledost volania jednotlivych metod pri vybere z DB:
     * {@link #loadAreaIDsForUser()}
     * {@link #loadSectorIDsForUser(List<Integer>)}<p>
     * {@link #loadSensorIDsForSectors(List<Integer>)}<p>
     * {@link #loadSensorTypes(List<Integer>)}<p>
     * {@link #loadMeasurementIDsForSensors(List<Integer>)}<p>
     * {@link #loadValuesAndDates(List<Integer>)}<p>
     * {@link #createDashboardData(List<ValuesAndDates>)}<p>
     * {@link #loadImportantViewsForUser();}<p>
     */
    public void loadDBDataForDashboard(int daysToPast) {
        this.ACTIVITY = DASHBOARD;
        this.daysToPastDB = daysToPast;
        this.clearDBWorkerLists();
        loadAreaIDsForUser();
    }


    /**
     * Metoda, ktora spusti vyber dat z DB, pre aktivitu {@link project.mtf.nsoric.aaurela.FavouritesActivity}.
     * <p> Data z DB su vyberane za casovy interval na zaklade hodnoty kluca zo SharedPreferences {@value Constants#SP_DAYS_TO_PAST_DB}.
     * <p>
     * * Vybrane data su poslane triede
     * * {@link FavouritesAdapter}
     * * <p>
     * <p>
     * Nasledost volania jednotlivych metod pri vybere z DB:
     * {@link #loadSensorTypes(List<Integer>)} <p>
     * {@link #loadMeasurementIDsForSensors(List<Integer>)}<p>
     * {@link #loadValuesAndDates(List<Integer>)}<p>
     * {@link #createFavouritesData(List<ValuesAndDates>)}<p>
     * {@link #loadImportantViewsForUser()}<p>
     */
    public void loadDBDataForFavourites(int daysToPast) {
        this.ACTIVITY = FAVOURITES;
        this.daysToPastDB = daysToPast;
        this.clearDBWorkerLists();
        loadFavouriteSensorsIDs();
    }

    /**
     * Metoda, ktora spusti vyber dat z DB, pre aktivitu {@link project.mtf.nsoric.aaurela.GraphViewActivity}.
     *
     * <p> Data z DB su vyberane za casovy interval na zaklade hodnoty kluca zo SharedPreferences {@value Constants#SP_DAYS_TO_PAST_DB}.
     * <p>
     * Vybrane data su poslane triede
     * {@link GraphViewData}
     *
     * @param sensorID zoznam ID senzorov pre, ktore sa budu udaje z DB vytahovat
     *                 <p>
     *                 Naslednost volani po spusteni tejto metody: <p>
     *                 {@link #loadSensorTypes(List<Integer>)}
     *                 {@link #loadMeasurementIDsForSensors(List<Integer>)}
     *                 {@link #loadValuesAndDates(List<Integer>)}
     *                 {@link #createGraphViewData(List<ValuesAndDates>)}
     */
    public void loadDBDataForGraphView(int sensorID, int daysToPast) {
        this.ACTIVITY = GRAPH_VIEW;
        this.daysToPastDB = daysToPast;
        this.clearDBWorkerLists();
        this.sensorIDsList.add(sensorID);
        loadSensorTypes(sensorIDsList);
    }


    /**
     * Callback z {@link DashboardAdapter} a {@link FavouritesAdapter}, ktory je volany pri pridani senzoru do zoznamu
     * dolezitych.
     * <p>
     * V tejto metode sa senzor pridny do oblubenych ulozi do DB do tabulky important_view.
     *
     * @param sensorID id senzoru, ktory bol pridany do zoznamu oblubenych
     */
    @Override
    public void onImportantViewAdded(final int sensorID) {
        CompositeDisposableDB.clear();
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                ImportantView importantView = new ImportantView();
                importantView.setSensorID(sensorID);
                importantView.setUserID(userID);
                importanatViewDao.insertImportantView(importantView);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        CompositeDisposableDB.add(d);
                    }

                    @Override
                    public void onComplete() {
                        if (importantsCount == 0) {
                            sharedPreferences.edit().putBoolean(server + "_" + String.valueOf(userID), true).apply();
                        }
                        importantsCount++;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    /**
     * Callback z {@link DashboardAdapter} a {@link FavouritesAdapter}, ktory je volany pri odobrani senzoru zo zoznamu
     * dolezitych.
     * <p>
     * V tejto metode sa senzor odstraneny z oblubenych, odstrani z DB z tabulky important_view.
     *
     * @param sensorID id senzoru, ktory bol odstraneny zo zoznamu oblubenych
     */
    @Override
    public void onImportantViewRemoved(final int sensorID) {
        CompositeDisposableDB.clear();
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                importanatViewDao.deleteImportantView(sensorID, userID);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        CompositeDisposableDB.add(d);
                    }

                    @Override
                    public void onComplete() {
                        importantsCount--;
                        if (importantsCount == 0) {
                            sharedPreferences.edit().putBoolean(server + "_" + String.valueOf(userID), false).apply();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    /**
     * Metoda, ktora stahuje vsetky data zo serveru za {@value Constants#DAYS_TO_FETCH_SERVER} dni (nie Userov) a uklada ich do DB.
     * <p>
     * Po dostahovani a ulozeni hodnot do DB sa premazu hodnoty v DB metodou {@link DateSynchronizeHelper#getDateFromPastToDeleteFrom(int)}+1.
     */
    public void downloadAllDataToDB() {
        final int daysToFetch = Constants.DAYS_TO_FETCH_SERVER;
        CompositeDisposableServer.clear();
        if (!ClientSocket.hasNetworkConnection(context)) {
            dataProcessingListener.onNoServerResponse(0);
        } else {
            dataProcessingListener.onServerSynchronizationBegin();
            Completable.fromRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        userDao.insertAllUsers(apiDataPresenter.getUsers());
                        areaDao.insertAllAreas(apiDataPresenter.getAreas());
                        groupViewDao.insertAllGroupViews(apiDataPresenter.getGroupViews());
                        sectorDao.insertAllSectors(apiDataPresenter.getSectorList());
                        sensorDao.insertAllSensors(apiDataPresenter.getSensorList());
                        sensorSectorDao.insertAllSensorSectors(apiDataPresenter.getSensorSectorList());
                        measurementDao.insertAllMeasurements(apiDataPresenter.getMeasurements());
                        sensorPropertiesDao.insertAllSensorProperties(apiDataPresenter.
                                getSensorProperties());
                        typeDao.insertAllTypeGroups(apiDataPresenter.getTypeGroups());
                        typeDao.insertAllTypes(apiDataPresenter.getTypes());
                        sensorDateDao.insertAllSensorDates(apiDataPresenter.
                                getSensorDates(dateSynchronizeHelper.getDateIntervalToFetch(daysToFetch)));
                        sensorValueDao.insertAllSensorValues(apiDataPresenter.getSensorValues());
                        // Zmazu sa nepotrebne stare data v DB
                        String date = dateSynchronizeHelper.
                                getDateFromPastToDeleteFrom(Constants.DAYS_TO_FETCH_SERVER);
                        sensorDateDao.deleteOldSensorDates(date);
                        sensorValueDao.deleteOldValues(date);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        dataProcessingListener.onNoServerResponse(1);
                    }
                }
            }).subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            CompositeDisposableServer.add(d);
                        }

                        @Override
                        public void onComplete() {
                            dateSynchronizeHelper.setFetchedDays(daysToFetch);
                            apiDataPresenter.clearApiDataLists();
                            dataProcessingListener.onAllDataSynchronized();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            apiDataPresenter.clearApiDataLists();
                            dataProcessingListener.onNoServerResponse(1);
                        }
                    });
        }
    }

    /**
     * Metoda, ktora stahuje zo serveru namerane data za {@value Constants#DAYS_TO_FETCH_SERVER} dni a uklada ich do DB.
     * <p>
     * Po dostahovani a ulozeni hodnot do DB sa premazu hodnoty v DB metodou starsie ako {@link DateSynchronizeHelper#getDateFromPastToDeleteFrom(int)}+1.
     */
    public void downloadMeasuredValuesToDB() {
        final int daysToFetch = Constants.DAYS_TO_FETCH_SERVER;
        CompositeDisposableServer.clear();
        if (!ClientSocket.hasNetworkConnection(context)) {
            dataProcessingListener.onNoServerResponse(0);
        } else {
            dataProcessingListener.onMeasuredDataStartedLoading();
            Completable.fromRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        sensorDateDao.insertAllSensorDates(apiDataPresenter.
                                getSensorDates(dateSynchronizeHelper.getDateIntervalToFetch(daysToFetch)));
                        sensorValueDao.insertAllSensorValues(apiDataPresenter.getSensorValues());

                        String date = dateSynchronizeHelper.
                                getDateFromPastToDeleteFrom(Constants.DAYS_TO_FETCH_SERVER);
                        sensorDateDao.deleteOldSensorDates(date);
                        sensorValueDao.deleteOldValues(date);
                    } catch (JSONException | IOException e) {
                        dataProcessingListener.onNoServerResponse(1);
                        e.printStackTrace();
                    }
                }
            }).subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            CompositeDisposableServer.add(d);
                        }

                        @Override
                        public void onComplete() {
                            dateSynchronizeHelper.setFetchedDays(daysToFetch);
                            apiDataPresenter.clearApiDataLists();
                            dataProcessingListener.onMeasuredDataLoadedToDB();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            apiDataPresenter.clearApiDataLists();
                            dataProcessingListener.onNoServerResponse(1);
                        }
                    });
        }

    }

    /**
     * Pripravi data potrebne pre pripojenie sa na API.
     * <p>
     * Udaje sa ziskavaju zo SP.
     * <p>
     * Udaje su:
     * ID pouzivatela, meno, zahashovane heslo, adresa serveru, cislo portu, klient.
     */
    private void createConnectData() {
        this.login = sharedPreferences.getString(Constants.SP_USERNAME_KEY, "");
        this.server = sharedPreferences.getString(Constants.SP_SERVER_NAME_KEY, "");
        this.port = sharedPreferences.getString(Constants.SP_SERVER_PORT_KEY, "");
        this.pass = sharedPreferences.getString(Constants.SP_PASSWORD_KEY, "");
        this.client = resources.getString(R.string.app_name) + "_" + BuildConfig.VERSION_NAME;
        this.userID = sharedPreferences.getInt(Constants.SP_USER_ID_KEY, 0);
    }

    /**
     * Vycistenie lokalnych premennych, pre GC.
     * <p>
     * Metoda je volana, ak su dovyberane vsetky data z DB, z metody
     * {@link #loadImportantViewsForUser()}
     */
    private void clearDBWorkerLists() {
        if (this.sensorTypesList != null) {
            this.sensorTypesList.clear();
        }
        if (this.sensorIDsList != null) {
            this.sensorIDsList.clear();
        }
    }

    /**
     * Uvolnenie referencii objektov triedy {@link Repository}.
     * <p>
     * Metoda je volana v onDestroy metodach aktivit, ktore vyuzivaju triedu Repository.
     */
    public void removeReferences() {
        this.context = null;
        this.database = null;
        this.resources = null;
        this.dateSynchronizeHelper = null;
        this.dataProcessingListener = null;
        this.dashboardAdapter = null;
        this.favouritesAdapter = null;
        this.graphViewData = null;
        this.apiDataPresenter = null;
    }

    public void closeDatabase() {
        database.closeDatabase();
    }
}
