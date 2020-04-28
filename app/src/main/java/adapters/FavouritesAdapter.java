package adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import database.objects.db.pojo.FavouriteSensor;
import helpers.GlobalContext;
import listeners.AdapterItemClickListener;
import listeners.ImportantViewListener;
import project.mtf.nsoric.aaurela.R;

/**
 * Adapter, ktory uchovava data, ktore su zobrazovane vo Favourites aktivite.
 */
public class FavouritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private List<FavouriteSensor> filteredList;
    private List<FavouriteSensor> favouriteSensors = new ArrayList<>();
    private ImportantViewListener importantChangeListener;
    private AdapterItemClickListener itemClickListener;
    private static FavouritesAdapter INSTANCE;

    private Resources resources = GlobalContext.getAppContext().getResources();
    private Drawable[] drawables = {resources.getDrawable(R.drawable.ic_trend_arrow_down),
            resources.getDrawable(R.drawable.ic_trend_neutral),
            resources.getDrawable(R.drawable.ic_trend_arrow_up)};

    /**
     * Vracia instanciu triedy {@link FavouritesAdapter}
     * <p>
     * Singleton navrhovy vzor
     *
     * @param onClickListener       importantChangeListener zavolany pri kliknuti na konkretny senzor
     * @param importantViewListener importantChangeListener zavolany pri pridani/odstraneni senzoru do/zo zoznamu oblubenych
     * @return instancia triedy
     */
    public static FavouritesAdapter getInstance(AdapterItemClickListener onClickListener, ImportantViewListener importantViewListener) {
        if (INSTANCE == null) {
            synchronized (FavouritesAdapter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FavouritesAdapter();
                }
            }
        }
        if (onClickListener != null) {
            INSTANCE.itemClickListener = onClickListener;
        }
        if (importantViewListener != null) {
            INSTANCE.importantChangeListener = importantViewListener;
        }
        return INSTANCE;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        v = mInflater.inflate(R.layout.card_view_sensor, viewGroup, false);
        return new FavouritesAdapter.DataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((DataViewHolder) viewHolder).checkBox.setChecked(true);
        ((DataViewHolder) viewHolder).textViewDate.setText(filteredList.get(i).getDate());
        ((DataViewHolder) viewHolder).textViewSensor.setText(filteredList.get(i).getSensorName());
        ((DataViewHolder) viewHolder).imageView.setImageDrawable(null);
        if (filteredList.get(i).getTrend() != -1) {
            ((DataViewHolder) viewHolder).imageView.setImageDrawable(drawables[filteredList.get(i).getTrend()]);
            float rotationValue;
            rotationValue = (float) Math.toDegrees(Math.atan(filteredList.get(i).getTrendValue()));
            rotationValue *= -1.0;
            ((DataViewHolder) viewHolder).imageView.setRotation(rotationValue);
        }
        //ak senzor nemeral, jeho merana hodnota je nastavena na Double.MIN_VALUE
        if (filteredList.get(i).getValue() != Double.MIN_VALUE) {
            if ((filteredList.get(i).getLevelLowest() != Double.MIN_VALUE) &&
                    (filteredList.get(i).getValue() < filteredList.get(i).getLevelLowest())) {
                ((DataViewHolder) viewHolder).textViewSensorValue.setTextColor(Color.parseColor("#0077CC"));
            } else if ((filteredList.get(i).getLevelHighest() != Double.MAX_VALUE) &&
                    (filteredList.get(i).getValue() > filteredList.get(i).getLevelHighest())) {
                ((DataViewHolder) viewHolder).textViewSensorValue.setTextColor(Color.parseColor("#F44336"));
            } else {
                ((DataViewHolder) viewHolder).textViewSensorValue.setTextColor(Color.parseColor("#212121"));
            }
            ((DataViewHolder) viewHolder).textViewSensorValue.setText(String.valueOf(filteredList.get(i).getValue()) + " " +
                    filteredList.get(i).getUnit());
            ((DataViewHolder) viewHolder).textViewAvgText.setText(filteredList.get(i).getAvgText());
            ((DataViewHolder) viewHolder).textViewAvgValue.setText(String.valueOf(filteredList.get(i).getAvgValue()) + " " +
                    filteredList.get(i).getUnit());
        } else {
            ((DataViewHolder) viewHolder).textViewSensorValue.setTextColor(Color.parseColor("#212121"));
            ((DataViewHolder) viewHolder).textViewSensorValue.setText("N/A " + filteredList.get(i).getUnit());
            ((DataViewHolder) viewHolder).textViewAvgText.setText("");
            ((DataViewHolder) viewHolder).textViewAvgValue.setText("");
        }
    }

    public void clearReferences() {
        INSTANCE.itemClickListener = null;
        INSTANCE.importantChangeListener = null;
        this.resources = null;
    }

    /**
     * Vracia pocet poloziek v adapteri.
     *
     * @return pocet poloziek
     */
    @Override
    public int getItemCount() {
        if (filteredList != null) {
            return filteredList.size();
        }
        return 0;
    }

    /**
     * Nastavenie premennej adapteru.
     *
     * @param favouriteSensors zoznam POJO {@link FavouriteSensor}
     */
    public void setFavouriteSensors(List<FavouriteSensor> favouriteSensors) {
        this.favouriteSensors = favouriteSensors;
        this.filteredList = favouriteSensors;
    }

    /**
     * ViewHolder pre vykreslene karty senzoru.
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSensor;
        private TextView textViewDate;
        private TextView textViewSensorValue;
        private TextView textViewAvgValue;
        private TextView textViewAvgText;
        private CheckBox checkBox;
        private RelativeLayout sensorItem;
        private ImageView imageView;

        DataViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_trend);
            textViewSensor = itemView.findViewById(R.id.text_view_sensor_name);
            textViewAvgValue = itemView.findViewById(R.id.text_view_average_value);
            textViewAvgText = itemView.findViewById(R.id.text_view_avg);
            textViewSensorValue = itemView.findViewById(R.id.text_view_sensor_value);
            textViewDate = itemView.findViewById(R.id.text_view_measurement_date_value);
            checkBox = itemView.findViewById(R.id.checkbox);
            sensorItem = itemView.findViewById(R.id.sensor_item);
            sensorItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    INSTANCE.itemClickListener.onItemClick(INSTANCE.filteredList.get(getAdapterPosition()).getSensorID());
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int sensorID = INSTANCE.filteredList.get(getAdapterPosition()).getSensorID();
                    INSTANCE.importantChangeListener.onImportantViewRemoved(sensorID);
                    INSTANCE.filteredList.remove(getAdapterPosition());
                    INSTANCE.itemClickListener.importantViewsCountChanged(INSTANCE.filteredList.size());
                    INSTANCE.notifyDataSetChanged();

                }
            });
        }

    }

    /**
     * Filtrovanie dat v adapteri.
     * <p>
     * Moznost filtrovania na zaklade nazvu senzoru jednotlivo.
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
                    filteredList = favouriteSensors;
                } else {
                    List<FavouriteSensor> mFilteredList = new ArrayList<>();
                    for (FavouriteSensor fSensor : favouriteSensors) {
                        if (fSensor.getSensorName() != null && fSensor.getSensorName().toLowerCase().contains(searchString)) {
                            mFilteredList.add(fSensor);
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
                filteredList = (ArrayList<FavouriteSensor>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
