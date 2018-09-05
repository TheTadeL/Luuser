package ch.devtadel.luuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.adapter.ClassListAdapter;
import ch.devtadel.luuser.model.School;
import ch.devtadel.luuser.model.SchoolClass;

public class SchoolActivity extends AppCompatActivity {
    private static final String TAG = "SchoolActivity";
    public static final String SCHOOL_NAME = "school";
    public static final String ACTION_STRING_SCHOOL_LOADED = "schoolLoaded";
    public static final String ACTION_STRING_CLASSES_LOADED = "classesLoaded";
    public static final String ACTION_STRING_GRAPH_LOADED = "graphLoaded";

    private RecyclerView.Adapter mainRecyclerAdapter;
    public static List<SchoolClass> class_data = new ArrayList<>();
    public static Map<Date, String> graph_data = new TreeMap<>();
    public static School school;

    private ConstraintLayout contentCON;

    private TextView nameTV;
    private TextView placeTV;
    private TextView louseInSchoolTV;
    private TextView noClassesTV;
    private TextView missingDataTV;

    private GraphView graph;

    private ProgressBar schoolPB;
    private ProgressBar classesPB;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_STRING_SCHOOL_LOADED)){
                Log.d(TAG, "SCHOOL LOADED BROADCAST RECEIVED!");
                loadSchoolPage();
            } else if(intent.getAction().equals(ACTION_STRING_CLASSES_LOADED)){
                Log.d(TAG, "CLASSES LOADED BROADCAST RECEIVED!");
                loadClasses();
            } else if(intent.getAction().equals(ACTION_STRING_GRAPH_LOADED)){
                Log.d(TAG, "GRAPH LOADED BROADCAST RECEIVED!");
                graph.addSeries(loadGraph());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        setTitle("Detailansicht Einrichtung");

        //Daten zurücksetzen wenn neue Schule geöffnet wird.
        class_data.clear();
        graph_data.clear();
        school = null;

        contentCON = findViewById(R.id.constraint_content_school);
        contentCON.setVisibility(View.GONE);

        schoolPB = findViewById(R.id.progressBar_school);
        schoolPB.setVisibility(View.VISIBLE);
        classesPB = findViewById(R.id.progressBar_classes);
        classesPB.setVisibility(View.VISIBLE);

        graph = findViewById(R.id.school_graph);
        graph.setVisibility(View.GONE);

        //Receiver registrieren
        if(activityReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_SCHOOL_LOADED);
            intentFilter.addAction(ACTION_STRING_CLASSES_LOADED);
            intentFilter.addAction(ACTION_STRING_GRAPH_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(activityReceiver, intentFilter);
        }

        setupRecyclerView();

        nameTV = findViewById(R.id.tv_school_name);
        placeTV = findViewById(R.id.tv_school_place);
        louseInSchoolTV = findViewById(R.id.tv_cnt_louse_school);
        noClassesTV = findViewById(R.id.tv_no_classes);
        noClassesTV.setVisibility(View.GONE);
        missingDataTV = findViewById(R.id.tv_missing_data);
        missingDataTV.setVisibility(View.GONE);

        Button newClassBTN = findViewById(R.id.btn_new_class);
        newClassBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newClassDialog();
            }
        });

        Button newCheckBTN = findViewById(R.id.btn_new_check);
        newCheckBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolActivity.this, NewCheckActivity.class);
                if(school != null){
                    intent.putExtra(SCHOOL_NAME, school.getName());
                }
                startActivity(intent);
            }
        });

        //Wenn kein Schulname mitgegeben wurde, umleiten auf die SchoolListActivity.
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            if (bundle.getString(SCHOOL_NAME) != null) {
                SchoolDao dao = new SchoolDao();
                dao.getSchoolToPage(bundle.getString(SCHOOL_NAME), this);
            } else {
                startActivity(new Intent(SchoolActivity.this, SchoolListActivity.class));
            }
        } else {
            startActivity(new Intent(SchoolActivity.this, SchoolListActivity.class));
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //Der Receiver wird deaktiviert.
        if(activityReceiver != null){
            unregisterReceiver(activityReceiver);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //Receiver registrieren
        if(activityReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_SCHOOL_LOADED);
            intentFilter.addAction(ACTION_STRING_CLASSES_LOADED);
            intentFilter.addAction(ACTION_STRING_GRAPH_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(activityReceiver, intentFilter);
        }
    }

    /**
     * Prozedur um den RecyclerView für die Userliste vorzubereiten.
     */
    private void setupRecyclerView(){
        RecyclerView mainRecyclerView = findViewById(R.id.rv_klassenliste);

        //Performance verbessern. Möglich, da die Listeneinträge alle die selbe grösse haben sollen.
        mainRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mainLayoutManager = new LinearLayoutManager(SchoolActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mainRecyclerView.setLayoutManager(mainLayoutManager);

        mainRecyclerAdapter = new ClassListAdapter(class_data);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);
    }

    /**
     * Wird aufgerufen wenn die Schulseitendaten geladen haben;
     */
    private void loadSchoolPage(){
        //Recycler wird in der Funktion notifiziert.
        SchoolDao dao = new SchoolDao();
        dao.getClassesToPage(school, mainRecyclerAdapter, this);
        dao.getLouseDataForSchoolGraph(getBaseContext(), school.getName());

        nameTV.setText(school.getName());
        placeTV.setText(school.getPlace());
        contentCON.setVisibility(View.VISIBLE);
        schoolPB.setVisibility(View.GONE);
    }

    /**
     * Wird aufgerufen wenn die Klassendaten geladen haben;
     */
    private void loadClasses(){
        for(int i = 0; i < class_data.size(); i++){
            school.setCntLouse(school.getCntLouse() + class_data.get(i).getCntLouse());
            school.setCntChecks(school.getCntChecks() + class_data.get(i).getCntChecks());
        }
        classesPB.setVisibility(View.GONE);
        if(class_data.size() == 0){
            noClassesTV.setVisibility(View.VISIBLE);
        }
    }

    private LineGraphSeries<DataPoint> loadGraph(){
        DateAsXAxisLabelFormatter dateLabelFormatter = new DateAsXAxisLabelFormatter(getBaseContext(), new SimpleDateFormat("dd.MMM"));
        graph.getGridLabelRenderer().setLabelFormatter(dateLabelFormatter);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(90);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        DataPoint[] points = new DataPoint[graph_data.size()];

        int i = 0;
        int louseCnt = 0;
        for(Object key : graph_data.keySet()){
            points[i] = new DataPoint((Date)key, Integer.valueOf(graph_data.get(key)));
            louseCnt+=Integer.valueOf(graph_data.get(key));
            i++;
        }
        louseInSchoolTV.setText(String.valueOf(louseCnt));

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

        graph.getGridLabelRenderer().setNumHorizontalLabels(10);

        graph.setTitle("Anzahl Kinder mit Läusen");
        graph.setTitleTextSize(48f);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        series.setAnimated(true);

        if(points.length > 1){
            missingDataTV.setVisibility(View.GONE);
            graph.setVisibility(View.VISIBLE);
        } else {
            missingDataTV.setVisibility(View.VISIBLE);
            graph.setVisibility(View.GONE);
        }

        return series;
    }

    private void newClassDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptView = li.inflate(R.layout.prompt_add_class, null);

        //Promptbuilder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        //View dem Dialog zuweisen.
        alertDialogBuilder.setView(promptView);

        final TextView promptTitle = promptView.findViewById(R.id.prompt_title);
        promptTitle.setText(R.string.newClass);
        final EditText userInput = promptView.findViewById(R.id.et_new_class_name);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Speichern",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SchoolDao dao = new SchoolDao();
                                dao.addClass(new SchoolClass(userInput.getText().toString()), school, mainRecyclerAdapter, getBaseContext());
                            }
                        })
                .setNegativeButton("Abbrechen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}
