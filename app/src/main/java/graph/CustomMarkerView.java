package graph;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import project.mtf.nsoric.aaurela.R;

/**
 * Trieda, ktora umoznuje vykreslit label s nameranou hodnotou pri kliknuti na graf.
 */
public class CustomMarkerView extends MarkerView {
    TextView textView;

    /**
     * Konstruktor. Nastavuje MarkerView s vlastnym layoutoum
     *
     * @param context        kontext
     * @param layoutResource layout, ktory ma byt pouzity pre MarkerView
     */
    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        textView = findViewById(R.id.tvContent);
    }

    /**
     * Callback zavolany pri potrebe prekreslit hodnotu pri kliknuti na hodnotu inu.
     *
     * @param e         usporiadana dvojica [x,y]
     * @param highlight
     */
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        textView.setText(String.valueOf(e.getY()));
    }

    @Override
    public MPPointF getOffset() {
        int a = -(getWidth() / 2);
        int b = -2*getHeight();
        return MPPointF.getInstance(a, b);
    }
}
