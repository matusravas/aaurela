package project.mtf.nsoric.aaurela;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import adapters.GraphViewData;
import composites.CompositeDisposableDB;
import database.objects.Repository;
import graph.CustomMarkerView;
import graph.CustomValueFormatter;
import helpers.Constants;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import listeners.DataProcessingListener;

/**
 * Trieda aktivity vykreslujuca graf zavislosti meranej hodnoty od casu, pre konkretny senzor.
 */
public class GraphViewActivity extends AppCompatActivity implements DataProcessingListener, View.OnClickListener {
    TextView textViewSensorName, textViewAlarmMax, textViewAlarmMin, textViewDate, textViewValue,
            textViewMaxValue, textViewMinValue, textViewMaxDate, textViewMinDate, textViewAverageValue,
            textViewPeriod, textViewMeasurementsCount;
    CheckBox checkBox;
    Toolbar toolbar;
    Button dialogButton;
    Dialog dialogPeriod;
    ProgressDialog progressDialog;
    NumberPicker np;
    SharedPreferences sharedPreferences;
    int size = 0;
    int sensorID;
    Repository repository;
    GraphViewData graphViewData;
    int daysToPast;

    LineChart chart;

    Context context;

    final DateFormat dfOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    //chart objects
    Entry xyVal;  //reprezentuje usporidanu dvojicu [x,y]
    ArrayList<Entry> allXYValues; //reprezentuje zonzam vsetkych usporidanych dvojic [x,y]
    LimitLine averageLine; //reprezentuje ciaru priemernej hodnoty
    CustomMarkerView customMarkerView;
    LineData lineData;
    LineDataSet dataSet;
    CustomValueFormatter valueFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        initObjects();
        findViews();
        this.daysToPast = sharedPreferences.getInt(Constants.SP_DAYS_TO_PAST_DB, 1);
        sensorID = getIntent().getIntExtra(Constants.SENSOR_ID, 0);
        repository.loadDBDataForGraphView(sensorID, daysToPast);
    }

    /**
     * Inicializacia grafickych prvkov a komponentov.
     */
    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chart = findViewById(R.id.graph_view);
        textViewSensorName = findViewById(R.id.text_view_sensor_name_graph);
        textViewAlarmMax = findViewById(R.id.tv_max_alarm_value);
        textViewAlarmMin = findViewById(R.id.tv_min_alarm_value);
        textViewDate = findViewById(R.id.tv_latest_date);
        textViewValue = findViewById(R.id.tv_latest_value);
        textViewMaxValue = findViewById(R.id.tv_max_value);
        textViewMinValue = findViewById(R.id.tv_min_value);
        textViewMaxDate = findViewById(R.id.tv_max_date);
        textViewMinDate = findViewById(R.id.tv_min_date);
        textViewAverageValue = findViewById(R.id.tv_average_value);
        textViewPeriod = findViewById(R.id.text_view_period);
        textViewMeasurementsCount = findViewById(R.id.tv_measurement_count_interval);
        checkBox = findViewById(R.id.checkbox_graph_view);
        checkBox.setOnClickListener(this);
    }

    /**
     * Inicializacia objektov triedy.
     */
    private void initObjects() {
        context = this;
        this.sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_AAURELA, MODE_PRIVATE);
        this.graphViewData = GraphViewData.getInstance();
        this.repository = new Repository(this, this);
        customMarkerView = new CustomMarkerView(context, R.layout.custom_marker_view);
        valueFormatter = new CustomValueFormatter();
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

    }

    @Override
    public void onAllDataSynchronized() {
        this.repository.loadDBDataForGraphView(sensorID, daysToPast);
        this.sharedPreferences.edit().putBoolean(Constants.SP_ALL_DATA_SYNCHRONIZED, true).apply();
    }

    @Override
    public void onMeasuredDataLoadedToDB() {
    }

    @Override
    public void onDataReady() {
        this.closeProgressDialog();
        this.size = graphViewData.getValuesAndDates().size();
        bindDataToTextViews();
        setChartProperties();
        setDataForGraph();
    }

    @Override
    public void onNoServerResponse(int errorCode) {
        this.closeProgressDialog();
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
                }

            }
        });
        dialog.setPositiveButton(R.string.dialog_button_retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == android.app.AlertDialog.BUTTON_POSITIVE) {
                    dialog.cancel();
                    repository.downloadAllDataToDB();
                }
            }
        });
        dialog.create();
        dialog.show();
    }


    /**
     * Vytvaranie vstupnych dat pre vykreslenie v grafe.
     * <p>
     * Vytvaranie je odklonene mimo hlavneho vlakna, kvoli dlhsiemu prechadzaniu zoznamu hodnot
     */
    private void setDataForGraph() {
        if (graphViewData.getValuesAndDates().size() != 0) {
            Completable.fromAction(new Action() {
                @Override
                public void run() {
                    chart.getAxisLeft().removeAllLimitLines();
                    allXYValues = null;
                    dataSet = null;
                    lineData = null;
                    allXYValues = new ArrayList<>();
                    averageLine = new LimitLine((float) graphViewData.getGraphSensor().getAverageValue());
                    for (int i = 0; i < size; i++) {
                        try {
                            xyVal = new Entry(dfOriginal.parse(graphViewData.getValuesAndDates().get(i).getDate()).getTime(),
                                    (float) graphViewData.getValuesAndDates().get(i).getValue());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        allXYValues.add(xyVal);
                        xyVal = null;
                    }
                    dataSet = new LineDataSet(allXYValues, "Values");
                    dataSet.setColor(getResources().getColor(R.color.blue_material));
                    dataSet.setLineWidth(2f);
                    dataSet.setDrawCircles(false);
                    dataSet.setValueTextSize(0);
                    averageLine.setLineColor(getResources().getColor(R.color.orange_material));
                    averageLine.setLineWidth(1f);
                    lineData = new LineData(dataSet);

                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            CompositeDisposableDB.add(d);
                        }

                        @Override
                        public void onComplete() {
                            chart.getAxisLeft().addLimitLine(averageLine);
                            chart.setData(lineData);
                            chart.invalidate();
                            CompositeDisposableDB.clear();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            chart.clear();
            chart.setNoDataTextColor(getResources().getColor(R.color.grey_light));
            chart.setNoDataText(getResources().getString(R.string.graph_no_data));
        }
    }

    /**
     * Zobrazenie udajov do textovych poli.
     */
    private void bindDataToTextViews() {
        checkBox.setChecked(graphViewData.isImportant());
        textViewMeasurementsCount.setText(getString(R.string.measurements_count, size));
        textViewSensorName.setText(graphViewData.getGraphSensor().getSensorName());
        updateTextViewPeriod();
        if (graphViewData.getValuesAndDates().size() != 0) {
            textViewMaxValue.setText(getString(R.string.value_placeholder, graphViewData.getGraphSensor().getMaxValue(), graphViewData.getGraphSensor().getUnit()));
            textViewMinValue.setText(getString(R.string.value_placeholder, graphViewData.getGraphSensor().getMinValue(), graphViewData.getGraphSensor().getUnit()));
            textViewMaxDate.setText(graphViewData.getGraphSensor().getMaxDate());
            textViewMinDate.setText(graphViewData.getGraphSensor().getMinDate());
            textViewValue.setText(getString(R.string.value_placeholder, graphViewData.getGraphSensor().getValue(), graphViewData.getGraphSensor().getUnit()));
            textViewDate.setText(graphViewData.getGraphSensor().getDate());
            textViewAverageValue.setText(getString(R.string.value_placeholder, graphViewData.getGraphSensor().getAverageValue(), graphViewData.getGraphSensor().getUnit()));

        } else {
            textViewMaxValue.setText("");
            textViewMinValue.setText("");
            textViewMaxDate.setText("");
            textViewMinDate.setText("");
            textViewValue.setText("");
            textViewDate.setText("");
            textViewAverageValue.setText("");
        }
        if ((graphViewData.getGraphSensor().getLevelLowest() != Double.MIN_VALUE) &&
                (graphViewData.getGraphSensor().getValue() < graphViewData.getGraphSensor().getLevelLowest())) {
            textViewValue.setTextColor(getResources().getColor(R.color.blue_material));
        } else if (graphViewData.getGraphSensor().getLevelHighest() != Double.MAX_VALUE &&
                (graphViewData.getGraphSensor().getValue() > graphViewData.getGraphSensor().getLevelHighest())) {
            textViewValue.setTextColor(getResources().getColor(R.color.red_material));
        } else
            textViewValue.setTextColor(getResources().getColor(R.color.primary_black_text));
        if (graphViewData.getGraphSensor().getLevelHighest() == Double.MAX_VALUE) {
            textViewAlarmMax.setText(getResources().getString(R.string.not_available));
        } else {
            textViewAlarmMax.setText(getString(R.string.value_placeholder, graphViewData.getGraphSensor().getLevelHighest(), graphViewData.getGraphSensor().getUnit()));
        }
        if (graphViewData.getGraphSensor().getLevelLowest() == Double.MIN_VALUE) {
            textViewAlarmMin.setText(getResources().getString(R.string.not_available));
        } else {
            textViewAlarmMin.setText(getString(R.string.value_placeholder, graphViewData.getGraphSensor().getLevelLowest(), graphViewData.getGraphSensor().getUnit()));
        }
    }

    /**
     * Zaktualizuje hodnotu TextView pre zobrzenie informacie aky interval je zobrazeny.
     * <p>
     * Zavolane vzdy ak sa vyberu data z DB pre Graf.
     */
    private void updateTextViewPeriod() {
        switch (daysToPast) {
            case 1:
                textViewPeriod.setText(getString(R.string.graph_period_info_single, daysToPast));
                break;
            case 2:
            case 3:
            case 4:
                textViewPeriod.setText(getString(R.string.graph_period_info_many1, daysToPast));
                break;
            default:
                textViewPeriod.setText(getString(R.string.graph_period_info_many2, daysToPast));
        }
    }

    /**
     * Nastavenie vlastnosti grafu.
     */
    private void setChartProperties() {
        chart.getXAxis().setValueFormatter(valueFormatter);
        chart.setMarker(customMarkerView);
        chart.setTouchEnabled(true);
        chart.setDoubleTapToZoomEnabled(true);
        chart.getAxisRight().setEnabled(false);
        if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
            chart.getXAxis().setTextSize(14f);
            chart.getXAxis().setGranularity(4f);
            chart.getAxisLeft().setTextSize(14f);
        } else {
            chart.getXAxis().setTextSize(8f);
            chart.getXAxis().setGranularity(1f);
        }
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend legend = chart.getLegend();
        Description description = chart.getDescription();
        description.setEnabled(false);
        legend.setEnabled(false);
        chart.setScaleYEnabled(false);
    }

    /**
     * Zobrazi dialog pre zmenu zobrazovaneho rozsahu.
     */
    private void showDayPeriodDialog() {
        dialogPeriod = new Dialog(this);
        dialogPeriod.setContentView(R.layout.dialog_period);
        TextView textView = dialogPeriod.findViewById(R.id.text_view_dialog_period_message);
        textView.setText(R.string.dialog_period_change_message_graph_view);
        np = dialogPeriod.findViewById(R.id.number_picker);
        np.setWrapSelectorWheel(true);
        np.setMaxValue(Constants.DAYS_TO_FETCH_SERVER);
        np.setMinValue(1);
        np.setValue(daysToPast);
        dialogButton = dialogPeriod.findViewById(R.id.button_date_picker);
        dialogButton.setOnClickListener(this);
        dialogPeriod.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.graph_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_period:
                showDayPeriodDialog();
                break;
            case R.id.menu_synchronize_all_data:
                repository.downloadAllDataToDB();
                break;
            case R.id.menu_about:
                Intent mail = new Intent(this, AboutActivity.class);
                mail.putExtra(Constants.SENSOR_ID, sensorID);
                startActivity(mail);
                break;
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return true;
    }

    private void closeProgressDialog() {
        if (this.dialogPeriod != null && this.dialogPeriod.isShowing()) {
            this.dialogPeriod.cancel();
        }
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.cancel();
        }
    }

    /**
     * Metoda zavolana pri kliknuti na tlacidlo "Potvrdit" v dialogu pre zmenu rozshau.
     * Na zaklade zvoleneho poctu dni sa uskutocni vyber udajov z DB pre konkretny senzor.
     */
    private void onDialogPeriodSet() {
        daysToPast = np.getValue();
        updateTextViewPeriod();
        this.repository.loadDBDataForGraphView(sensorID, daysToPast);
        sharedPreferences.edit().putInt(Constants.SP_DAYS_TO_PAST_DB, daysToPast).apply();
        dialogPeriod.cancel();
    }

    @Override
    public void onBackPressed() {
        CompositeDisposableDB.clear();
        this.finish();
        graphViewData.clearData();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        this.closeProgressDialog();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        this.repository.removeReferences();
        this.repository = null;
        this.customMarkerView = null;
        this.graphViewData = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkbox_graph_view:
                if (v == checkBox) {
                    if (!checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        this.repository.onImportantViewRemoved(sensorID);
                    } else {
                        checkBox.setChecked(true);
                        this.repository.onImportantViewAdded(sensorID);
                    }
                }
                break;
            case R.id.button_date_picker:
                onDialogPeriodSet();
                break;
        }
    }
}
