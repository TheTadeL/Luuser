package ch.devtadel.luuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;

public class GraphActivity extends AppCompatActivity {
    private static final String TAG = "GraphActivity";
    public static final String ACTION_STRING_GRAPH_READY = "graph ready";

    public static Map<Date, String> data = new TreeMap<>();

    private GraphView graph;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            graph.addSeries(generateGraph());

            Log.d(TAG, "BROADCAST RECEIVED: " + ACTION_STRING_GRAPH_READY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //Receiver registrieren
        if(broadcastReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_GRAPH_READY);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(broadcastReceiver, intentFilter);
        }

        graph = findViewById(R.id.graphView);


        //TEST
        DateAsXAxisLabelFormatter dateLabelFormatter = new DateAsXAxisLabelFormatter(getBaseContext(), new SimpleDateFormat("dd.MMM"));
        graph.getGridLabelRenderer().setLabelFormatter(dateLabelFormatter);
        graph.getGridLabelRenderer().setNumHorizontalLabels(10);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(90);
        //TEST


        SchoolDao dao = new SchoolDao();
        dao.getLouseDataForGraph(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Receiver registrieren
        if(broadcastReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_GRAPH_READY);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();//Receiver registrieren
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    public LineGraphSeries<DataPoint> generateGraph() {
        DataPoint[] points = new DataPoint[data.size()];

        Log.d(TAG+" Data:", data.toString());

        int i = 0;
        for(Object key : data.keySet()){
            points[i] = new DataPoint((Date)key, Integer.valueOf(data.get(key)));
            i++;

        }

        for(int t = 0; t < points.length; t++){
            Log.d(TAG+" DataPoint:", points[t].toString());
        }


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date start = cal.getTime();

        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        Date end = cal.getTime();

        graph.getViewport().setMinX(start.getTime());
        graph.getViewport().setMaxX(end.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        series.setAnimated(true);

        return series;
    }
}
