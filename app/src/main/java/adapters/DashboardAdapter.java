package adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import database.objects.db.entity.ImportantView;
import database.objects.db.pojo.DashboardSensor;
import helpers.GlobalContext;
import listeners.AdapterItemClickListener;
import listeners.ImportantViewListener;
import project.mtf.nsoric.aaurela.R;

/**
 * Adapter, ktory uchovava data, ktore su zobrazovane v Dashboard aktivite.
 */
public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private List<DashboardSensor> filteredList;
    private List<DashboardSensor> dashboardSensors;
    private final int SECTOR = 1;
    private final int DATA = 0;
    private final int AREA = -1;

    private SparseBooleanArray itemStateArray;
    private ImportantViewListener importantChangeListener;
    private AdapterItemClickListener itemClickListener;
    private static DashboardAdapter INSTANCE;

    private Resources resources = GlobalContext.getAppContext().getResources();
    private Drawable[] drawables = {resources.getDrawable(R.drawable.ic_trend_arrow_down),
            resources.getDrawable(R.drawable.ic_trend_neutral),
            resources.getDrawable(R.drawable.ic_trend_arrow_up)};

    /**
     * Vracia instanciu triedy {@link DashboardAdapter}
     * <p>
     * Singleton navrhovy vzor
     *
     * @param onClickListener       importantChangeListener zavolany pri kliknuti na konkretny senzor
     * @param importantViewListener importantChangeListener zavolany pri pridani/odstraneni senzoru do/zo zoznamu oblubenych
     * @return instancia triedy
     */
    public static DashboardAdapter getInstance(AdapterItemClickListener onClickListener, ImportantViewListener importantViewListener) {
        if (INSTANCE == null) {
            synchronized (DashboardAdapter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DashboardAdapter();
                }
            }
        }
        if (importantViewListener != null) {
            INSTANCE.importantChangeListener = importantViewListener;
        }
        if (onClickListener != null) {
            INSTANCE.itemClickListener = onClickListener;
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        //i- itemType Area=-1, Data=0, Sector=1
        switch (i) {
            case DATA:
                LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
                view = mInflater.inflate(R.layout.card_view_sensor, viewGroup, false);
                return new SensorViewHolder(view);
            case SECTOR:
                LayoutInflater mInflater1 = LayoutInflater.from(viewGroup.getContext());
                view = mInflater1.inflate(R.layout.card_view_sector, viewGroup, false);
                return new SectorViewHolder(view);
            case AREA:
                LayoutInflater mInflater2 = LayoutInflater.from(viewGroup.getContext());
                view = mInflater2.inflate(R.layout.card_view_area, viewGroup, false);
                return new AreaViewHolder(view);
        }
        return null;
    }


    /**
     * Vracia aky typ bude vykresleny na zaklade hodnoty parametru {@link DashboardSensor#getTypeForAdapter()}
     *
     * @param position pozicia, ktora ma byt vykreslena v adapteri
     * @return typ, ktory bude vykresleny
     */
    @Override
    public int getItemViewType(int position) {
        if (filteredList.get(position).getTypeForAdapter() == -1) {
            return AREA;
        }
        if (filteredList.get(position).getTypeForAdapter() == 0) {
            return SECTOR;
        } else if (filteredList.get(position).getTypeForAdapter() == 1) {
            return DATA;
        } else return DATA;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof adapters.DashboardAdapter.AreaViewHolder) {
            ((AreaViewHolder) viewHolder).textViewArea.setText(filteredList.get(i).getAreaName());
        } else if (viewHolder instanceof adapters.DashboardAdapter.SectorViewHolder) {
            ((SectorViewHolder) viewHolder).textViewSector.setText(filteredList.get(i).getSectorName());
        } else if (viewHolder instanceof SensorViewHolder) {
            ((SensorViewHolder) viewHolder).bind(filteredList.get(i).getSensorID());
            ((SensorViewHolder) viewHolder).textViewSensor.setText(filteredList.get(i).getSensorName());
            ((SensorViewHolder) viewHolder).textViewDate.setText(filteredList.get(i).getDate());
            ((SensorViewHolder) viewHolder).imageView.setImageDrawable(null);
            if (filteredList.get(i).getTrend() != -1) {
                ((SensorViewHolder) viewHolder).imageView.setImageDrawable(drawables[filteredList.get(i).getTrend()]);
                float rotationValue;
                rotationValue = (float) Math.toDegrees(Math.atan(filteredList.get(i).getTrendValue()));
                rotationValue *= -1.0;
                ((SensorViewHolder) viewHolder).imageView.setRotation(rotationValue);
            }
            //ak senzor nemeral, jeho merana hodnota je nastavena na Double.MIN_VALUE
            if (filteredList.get(i).getValue() != Double.MIN_VALUE) {
                if ((filteredList.get(i).getLevelLowest() != Double.MIN_VALUE) &&
                        (filteredList.get(i).getValue() < filteredList.get(i).getLevelLowest())) {
                    ((SensorViewHolder) viewHolder).textViewSensorValue.setTextColor(Color.parseColor("#0077CC"));
                } else if (filteredList.get(i).getLevelHighest() != Double.MAX_VALUE &&
                        (filteredList.get(i).getValue() > filteredList.get(i).getLevelHighest())) {
                    ((SensorViewHolder) viewHolder).textViewSensorValue.setTextColor(Color.parseColor("#F44336"));
                } else {
                    ((SensorViewHolder) viewHolder).textViewSensorValue.setTextColor(Color.parseColor("#212121"));
                }
                ((SensorViewHolder) viewHolder).textViewSensorValue.setText(String.valueOf(filteredList.get(i).getValue()) + " " +
                        filteredList.get(i).getUnit());
                ((SensorViewHolder) viewHolder).textViewAvgText.setText(filteredList.get(i).getAvgText());
                ((SensorViewHolder) viewHolder).textViewAvgValue.setText(String.valueOf(filteredList.get(i).getAvgValue()) + " " +
                        filteredList.get(i).getUnit());
            } else {
                ((SensorViewHolder) viewHolder).textViewSensorValue.setTextColor(Color.parseColor("#212121"));
                ((SensorViewHolder) viewHolder).textViewSensorValue.setText("N/A " + filteredList.get(i).getUnit());
                ((SensorViewHolder) viewHolder).textViewAvgText.setText("");
                ((SensorViewHolder) viewHolder).textViewAvgValue.setText("");
            }
        }
    }

    /**
     * Vracia pocet poloziek v adapteri.
     *
     * @return pocet poloziek
     */
    @Override
    public int getItemCount() {
        if (filteredList != null && filteredList.size() != 0) {
            return filteredList.size();
        } else return 0;
    }

    /**
     * Nastavenie premennej adapteru.
     *
     * @param dashboardSensors zoznam POJO {@link DashboardSensor}
     */
    public void setDashboardSensors(List<DashboardSensor> dashboardSensors) {
        this.dashboardSensors = dashboardSensors;
        filteredList = dashboardSensors;
    }

    /**
     * Vytvorenie HashMapy, senzoru a tru/false hodnoty, podla toho ci patri do oblubenych alebo nie.
     *
     * @param importantViews zoznam oblubenych senzorov s ID senzorov.
     */
    public void setImportantViews(List<ImportantView> importantViews) {
        itemStateArray = null;
        itemStateArray = new SparseBooleanArray();
        if (filteredList != null && importantViews != null && !importantViews.isEmpty()) {
            for (int i = 0; i < filteredList.size(); i++) {
                for (ImportantView iv : importantViews) {
                    //ak id senzora, kt je v important view==id senzora vo filteredList
                    //tak do arrayu ktory uchovava checked/non-checked sa ulozi id
                    //senzora a nastavi sa na true (tzn. je important)
                    if (iv.getSensorID() == filteredList.get(i).getSensorID()) {
                        itemStateArray.put(filteredList.get(i).getSensorID(), true);
                    }
                }
            }
        }
    }

    public void clearReferences() {
        INSTANCE.itemClickListener = null;
        INSTANCE.importantChangeListener = null;
        this.resources = null;
    }

    public int getImportantsCount() {
        if (itemStateArray != null) {
            return itemStateArray.size();
        }
        return -1;
    }


    /**
     * ViewHolder pre vykreslene karty sektoru.
     */
    public static class SectorViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSector;

        SectorViewHolder(@NonNull final View itemView) {
            super(itemView);
            textViewSector = itemView.findViewById(R.id.text_view_sector_header);
        }
    }

    /**
     * ViewHolder pre vykreslene karty arei.
     */
    public static class AreaViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewArea;

        AreaViewHolder(@NonNull final View itemView) {
            super(itemView);
            textViewArea = itemView.findViewById(R.id.text_view_area_header);
        }
    }

    /**
     * ViewHolder pre vykreslene karty senzoru.
     */
    public static class SensorViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSensor;
        private TextView textViewDate;
        private TextView textViewSensorValue;
        private TextView textViewAvgValue;
        private TextView textViewAvgText;
        private CheckBox checkBox;
        private RelativeLayout sensorItem;
        private ImageView imageView;

        SensorViewHolder(@NonNull final View itemView) {
            super(itemView);
            textViewAvgValue = itemView.findViewById(R.id.text_view_average_value);
            imageView = itemView.findViewById(R.id.image_trend);
            textViewAvgText = itemView.findViewById(R.id.text_view_avg);
            textViewSensor = itemView.findViewById(R.id.text_view_sensor_name);
            textViewSensorValue = itemView.findViewById(R.id.text_view_sensor_value);
            textViewDate = itemView.findViewById(R.id.text_view_measurement_date_value);
            checkBox = itemView.findViewById(R.id.checkbox);
            sensorItem = itemView.findViewById(R.id.sensor_item);
            sensorItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == sensorItem) {
                        INSTANCE.itemClickListener.onItemClick(INSTANCE.filteredList.get(getAdapterPosition()).getSensorID());
                    }
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int sensorID = INSTANCE.filteredList.get(getAdapterPosition()).getSensorID();
                    if (!INSTANCE.itemStateArray.get(sensorID, false)) {
                        checkBox.setChecked(true);
                        INSTANCE.itemStateArray.put(sensorID, true);
                        INSTANCE.importantChangeListener.onImportantViewAdded(sensorID);
                    } else {
                        checkBox.setChecked(false);
                        INSTANCE.itemStateArray.delete(sensorID);
                        INSTANCE.importantChangeListener.onImportantViewRemoved(sensorID);
                    }
                    INSTANCE.itemClickListener.importantViewsCountChanged(INSTANCE.itemStateArray.size());
                }
            });
        }

        /**
         * Nastavovanie hodnoty checkboxu kazdeho itemu ktory bude v onBind pirdany.
         * <p>
         * Nastavovanie je na zaklade HashMapy sensorID = true/false.
         * <p>
         * true ak je senzor v oblubenych
         * false ak nie je v oblubenych
         *
         * @param position pozicia v adapteri, ktora nasleduje
         */
        private void bind(int position) {
            if (INSTANCE.itemStateArray != null) {
                if (!INSTANCE.itemStateArray.get(position, false)) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
            }
        }
    }

    /**
     * Filtrovanie dat v adapteri.
     * <p>
     * Moznost filtrovania na zaklade nazvu senzorov jednotlivo.
     * <p>
     * Moznost filtrovania na zaklade nazvu sektoru a jemu priradenych senzorov.
     * <p>
     * Moznost filtrovania na zaklade nazvu oblasti a jej prislusnych sektorov a im prislusnych senzorov.
     *
     * @return vracia upraveny zoznam, ktoreho polozky vyhovuju zadanemu retazcu
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            /**
             * Implementacia logiky filtrovania dat.
             *
             * Na zaklade hladaneho retazca sa prechadzaju dostupne polozky v adapteri.
             * Tie, ktore vyhovuju hladanemu retazcu sa pridaju do zoznamu vyhovujucich, na zaklade
             * ktoreho je vytvoreny vyfiltrovany zoznam
             * @param constraint retazec pre filtorvanie
             * @return vyfiltorvane data
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchString = constraint.toString().toLowerCase().trim();
                if (searchString.isEmpty()) {
                    filteredList = dashboardSensors;
                } else {
                    List<DashboardSensor> mFilteredList = new ArrayList<>();
                    for (DashboardSensor s : dashboardSensors) {
                        if ((s.getSensorName() != null && s.getSensorName().toLowerCase().contains(searchString)) ||
                                (s.getSectorName().toLowerCase().contains(searchString)) ||
                                (s.getAreaName()).toLowerCase().contains(searchString)) {
                            mFilteredList.add(s);
                        }

                    }
                    filteredList = mFilteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            /**
             * Publikovanie vysledkov, notifikovanie adapteru o zmene datasetu.
             * @param constraint hladany retazec
             * @param results vysledky filtrovania
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<DashboardSensor>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
