package adapters;

import java.util.ArrayList;
import java.util.List;

import database.objects.db.pojo.GraphSensor;
import database.objects.db.pojo.ValuesAndDates;

/**
 * Trieda, sluziaca ako DataHolder pre {@link project.mtf.nsoric.aaurela.GraphViewActivity}.
 */
public class GraphViewData {
    private List<ValuesAndDates> valuesAndDates = new ArrayList<>();

    private GraphSensor graphSensor = new GraphSensor();
    private boolean isImportant = false;

    private static GraphViewData INSTANCE;

    private GraphViewData() {
    }

    /**
     * Vracia instanciu triedy {@link GraphViewData}.
     * <p>
     * Singleton navrhovy vzor
     *
     * @return instancia tiredy {@link GraphViewData}
     */
    public static GraphViewData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GraphViewData();
            return INSTANCE;
        } else return INSTANCE;
    }

    /**
     * Nastavenie hodnot premennej triedy s nameranymi hodntami.
     *
     * @param valuesAndDates zoznam s nameranymi hodnotami
     */
    public void setValuesAndDates(List<ValuesAndDates> valuesAndDates) {
        this.valuesAndDates.clear();
        this.valuesAndDates = valuesAndDates;
    }

    /**
     * Nastavenie ci patri senzor do oblubenych alebo nie.
     *
     * @param important true ak je v oblubenych, false nie je
     */
    public void setIsImportant(boolean important) {
        isImportant = important;
    }

    /**
     * Vracia ci je senzor v oblubenych alebo nie.
     *
     * @return true ak je v oblubenych, false nie je
     */
    public boolean isImportant() {
        return isImportant;
    }

    /**
     * Vracia zoznam nameranych hodnot pre dany senzor.
     *
     * @return zoznam nameranych hodnot
     */
    public List<ValuesAndDates> getValuesAndDates() {

        return valuesAndDates;
    }

    /**
     * Nastavenie hodnoty {@link GraphSensor}, premennej triedy pre konkretny senzor.
     *
     * @param graphSensor objekt triedy {@link GraphSensor}, pre konkretny senzor
     */
    public void setGraphSensor(GraphSensor graphSensor) {
        this.graphSensor = graphSensor;
    }

    /**
     * Vracia premennu triedy {@link GraphSensor}.
     *
     * @return objekt {@link GraphSensor}
     */
    public GraphSensor getGraphSensor() {
        return graphSensor;
    }

    /**
     * Vycistenie dat tiredy {@link GraphViewData}.
     * <p>
     * Data su vycistene pri zatvoreni aktivity {@link project.mtf.nsoric.aaurela.GraphViewActivity}
     */
    public void clearData() {
        if (this.valuesAndDates != null) {
            this.valuesAndDates.clear();
        }
        if (this.graphSensor != null) {
            this.graphSensor = null;
        }
        this.isImportant = false;
    }
}
