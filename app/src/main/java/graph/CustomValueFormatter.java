package graph;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Trieda, ktora zabezpecuje formatovanie osi x v grafe.
 * <p> x-ove suradnice su reprezentovane ako timestamp datumu a casu kedy meranie prebehlo.
 * <p> Prave tato trieda zabezpecuje spatnu konverziu timestampu spat na String datum a cas.
 */
public class CustomValueFormatter implements IAxisValueFormatter {

    private final DateFormat dfWanted;

    /**
     * Konstruktor.
     */
    public CustomValueFormatter() {
        dfWanted = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());
    }

    /**
     * Konverzia timestamp xovej suradnice na String datum
     *
     * @param value xova hodnota timestamp
     * @param axis  os
     * @return String datum
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return dfWanted.format(new Date((long) value));
    }
}