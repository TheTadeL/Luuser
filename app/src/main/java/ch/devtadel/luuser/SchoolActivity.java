package ch.devtadel.luuser;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.adapter.ClassListAdapter;
import ch.devtadel.luuser.adapter.SchoolCheckListAdapter;
import ch.devtadel.luuser.helper.DateHelper;
import ch.devtadel.luuser.model.Check;
import ch.devtadel.luuser.model.School;
import ch.devtadel.luuser.model.SchoolClass;

//Todo: Umleitung zum Checkerprofil
//Todo: Letzte Kontrolle Datum anzeigen
//Todo: Anzahl Läuse / Anzahl Schüler bei Klassen

public class SchoolActivity extends AppCompatActivity {
    private static final String TAG = "SchoolActivity";
    public static final String SCHOOL_NAME = "school_name";
    public static final String SCHOOL_YEAR = "school_year";
    public static final String TO_NEW_CLASS = "new class";
    public static final String LAST_CHECK_DATE = "last check date";
    public static final String ACTION_STRING_SCHOOL_LOADED = "schoolLoaded";
    public static final String ACTION_STRING_CLASSES_LOADED = "classesLoaded";
    public static final String ACTION_STRING_GRAPH_LOADED = "graphLoaded";
    public static final String ACTION_STRING_CHECKS_LOADED = "checksLoaded";
    public static final String ACTION_STRING_LAST_CHECK_LOADED = "lastCheckLoaded";

    private RecyclerView.Adapter classesRecyclerAdapter;
    private RecyclerView.Adapter checksRecyclerAdapter;
    public static List<SchoolClass> class_data = new ArrayList<>(); //Todo: Klassenliste sortieren (SchoolDao)
    public static List<Check> check_data = new ArrayList<>(); //Todo: Checkliste sortieren (SchoolDao)
    public static Map<Date, String> graph_data = new TreeMap<>();
    public static School school;

    private ConstraintLayout contentCON;

    private TextView nameTV;
    private TextView placeTV;
    private TextView louseInSchoolTV;
    private TextView noClassesTV;
    private TextView missingDataTV;
    private TextView finalSchoolYearTV;
    private TextView lastCheckTV;

    private EditText schoolYearET;

    private GraphView graph;

    private ProgressBar schoolPB;
    private ProgressBar classesPB;

    private boolean toCreateClass = false;
    private boolean addClass = true;

    private int startYear;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() != null && intent.getAction().equals(ACTION_STRING_SCHOOL_LOADED)){
                Log.d(TAG, "SCHOOL LOADED BROADCAST RECEIVED!");
                loadSchoolPage();
            } else if(intent.getAction().equals(ACTION_STRING_CLASSES_LOADED)){
                Log.d(TAG, "CLASSES LOADED BROADCAST RECEIVED!");
                classesRecyclerAdapter.notifyDataSetChanged();
                loadClasses();
                if(toCreateClass){
                    newClassDialog(startYear);
                    toCreateClass = false;
                }
                SchoolDao dao = new SchoolDao();
                dao.getChecksToSchool(getBaseContext(), school.getName());  //Kontrollliste laden wenn Klassen geladen haben    //Todo: Auf Schuljahr beschränken!
                dao.getLastCheckToSchool(getBaseContext(), school.getName());
            } else if(intent.getAction().equals(ACTION_STRING_GRAPH_LOADED)){
                Log.d(TAG, "GRAPH LOADED BROADCAST RECEIVED!");
                graph.addSeries(loadGraph());
            } else if(intent.getAction().equals(ACTION_STRING_CHECKS_LOADED)){
                Log.d(TAG, "CHECKS LOADED BROADCAST RECEIVED!");
                checksRecyclerAdapter.notifyDataSetChanged();
            } else if(intent.getAction().equals(ACTION_STRING_LAST_CHECK_LOADED)){
                Log.d(TAG, "LAST CHECK LOADED BROADCAST RECEIVED!");
                lastCheckTV.setText(Objects.requireNonNull(intent.getExtras()).getString(LAST_CHECK_DATE));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        //UP-Button hinzufügen
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Detailansicht Einrichtung");

        //Daten zurücksetzen wenn neue Schule geöffnet wird.
        class_data.clear();
        graph_data.clear();
        school = null;

        //Receiver registrieren
        if(activityReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_SCHOOL_LOADED);
            intentFilter.addAction(ACTION_STRING_CLASSES_LOADED);
            intentFilter.addAction(ACTION_STRING_GRAPH_LOADED);
            intentFilter.addAction(ACTION_STRING_CHECKS_LOADED);
            intentFilter.addAction(ACTION_STRING_LAST_CHECK_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(activityReceiver, intentFilter);
        }

        setupContentViews();
        setupRecyclerViews();

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
            if (bundle.getBoolean(TO_NEW_CLASS)){
                toCreateClass = true;
                startYear = bundle.getInt(SCHOOL_YEAR);
            }
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
            intentFilter.addAction(ACTION_STRING_CHECKS_LOADED);
            intentFilter.addAction(ACTION_STRING_GRAPH_LOADED);
            intentFilter.addAction(ACTION_STRING_LAST_CHECK_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(activityReceiver, intentFilter);
        }

        //Wenn kein Schulname mitgegeben wurde, umleiten auf die SchoolListActivity.
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            if (bundle.getBoolean(TO_NEW_CLASS)){
                toCreateClass = true;
                startYear = bundle.getInt(SCHOOL_YEAR);
            }
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

    //Actionbar Komponente wird benutzt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Prozedur um den RecyclerView für die Userliste vorzubereiten.
     */
    private void setupRecyclerViews(){
        RecyclerView classesRecyclerView = findViewById(R.id.rv_klassenliste);
        RecyclerView checksRecyclerView = findViewById(R.id.rv_school_checks);

        //Performance verbessern. Möglich, da die Listeneinträge alle die selbe grösse haben sollen.
        classesRecyclerView.setHasFixedSize(true);
        checksRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager classesLayoutManager = new LinearLayoutManager(SchoolActivity.this, LinearLayoutManager.HORIZONTAL, false);
        classesRecyclerView.setLayoutManager(classesLayoutManager);

        RecyclerView.LayoutManager checksLayoutManager = new LinearLayoutManager(SchoolActivity.this);
        checksRecyclerView.setLayoutManager(checksLayoutManager);


        classesRecyclerAdapter = new ClassListAdapter(class_data);
        classesRecyclerView.setAdapter(classesRecyclerAdapter);

        checksRecyclerAdapter = new SchoolCheckListAdapter(check_data);
        checksRecyclerView.setAdapter(checksRecyclerAdapter);
    }

    /**
     * Wird aufgerufen wenn die Schulseitendaten geladen haben;
     */
    private void loadSchoolPage(){
        //Recycler wird in der Funktion notifiziert.
        SchoolDao dao = new SchoolDao();
        dao.getClassesToPage(school, this, Integer.valueOf(schoolYearET.getText().toString()));
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

    @SuppressLint("SimpleDateFormat")
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

//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.YEAR, 2018);
//        cal.set(Calendar.MONTH, 0);
//        cal.set(Calendar.DAY_OF_YEAR, 1);
//        Date start = cal.getTime();
//
//        cal.set(Calendar.YEAR, 2018);
//        cal.set(Calendar.MONTH, 11);
//        cal.set(Calendar.DAY_OF_MONTH, 31);
//        Date end = cal.getTime();
//
//        graph.getViewport().setMinX(start.getTime());
//        graph.getViewport().setMaxX(end.getTime());
//        graph.getViewport().setXAxisBoundsManual(true);

        if(points.length > 0) {
            graph.getViewport().setMinX(points[0].getX());
            graph.getViewport().setMaxX(points[points.length - 1].getX());
            graph.getViewport().setXAxisBoundsManual(true);
        }

//        graph.getGridLabelRenderer().setNumHorizontalLabels(10);

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

    public void toNewClass(View view){
        if(DateHelper.validYear(schoolYearET)){
            newClassDialog(Integer.valueOf(schoolYearET.getText().toString()));
        } else {
            newClassDialog(-1);
        }
    }

    private void newClassDialog(int year){
        LayoutInflater li = LayoutInflater.from(this);
        @SuppressLint("InflateParams")
        View promptView = li.inflate(R.layout.prompt_add_class, null);

        //Promptbuilder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        //View dem Dialog zuweisen.
        alertDialogBuilder.setView(promptView);

        final TextView promptTitle = promptView.findViewById(R.id.prompt_title);
        promptTitle.setText(R.string.newClass);
        final TextView schoolNameTV = promptView.findViewById(R.id.tv_new_class_schol_name);
        schoolNameTV.setText(school.getName());
        final TextView schoolPlaceTV = promptView.findViewById(R.id.tv_new_class_place);
        schoolPlaceTV.setText("("+school.getPlace()+")");

        final EditText startYearET = promptView.findViewById(R.id.prompt_new_class_start_year);

        //Wenn ein StartJahr mitgegeben wurde
        String printString;
        if(year != -1) {
            startYearET.setText(String.valueOf(year));
            printString = DateHelper.getShortYearString(year);
        } else {
            startYearET.setText(String.valueOf(DateHelper.getSchoolYear()));
            printString = DateHelper.getShortYearString(DateHelper.getSchoolYear());
        }
        final TextView finalStartYearTV = promptView.findViewById(R.id.tv_newclass_startyear_final);
        finalStartYearTV.setText(printString);

        startYearET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Festlegen ob die Klasse hinzugefügt werden darf, oder nicht.
                addClass = DateHelper.startYearToFinal(charSequence, getBaseContext(), startYearET, finalStartYearTV);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        final EditText classNameET = promptView.findViewById(R.id.et_new_class_name);

        // Dialognachricht setzen
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Speichern",  //TODO: Wenn das Jahrgangsdatum in der Activity un im Dialog nicht dasselbe ist, muss in der Datenbank geprüft werden, ob die Klasse nicht schon existiert!!
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //Check, ob eine Klasse mit diesem Namen bereits existiert.
                                for(SchoolClass schoolClass : class_data){
                                    if(schoolClass.getName().equals(classNameET.getText().toString())){
                                        addClass = false;
                                    }
                                }
                                if(addClass) {
                                    SchoolDao dao = new SchoolDao();
                                    dao.addClass(new SchoolClass(classNameET.getText().toString(), Integer.valueOf(startYearET.getText().toString())), school, classesRecyclerAdapter, getBaseContext());

                                    //Activity neu laden
                                    Intent intent = getIntent();
                                    intent.putExtra(TO_NEW_CLASS, false);
                                    finish();
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getBaseContext(), "Eine Klasse mit diesem Namen existiert bereits!", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton("Abbrechen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // Dialog erstellen
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Dialog zeigen
        alertDialog.show();
    }

    /**
     * Prozedur um alle Views zu initialisieren.
     * Soll Platz in der OnCreate()-Methode sparen.
     */
    private void setupContentViews(){
        //ConstraintLayout
        contentCON = findViewById(R.id.constraint_content_school);
        contentCON.setVisibility(View.GONE);

        //Progressbar
        schoolPB = findViewById(R.id.progressBar_school);
        schoolPB.setVisibility(View.VISIBLE);
        classesPB = findViewById(R.id.progressBar_classes);
        classesPB.setVisibility(View.VISIBLE);

        //Graph
        graph = findViewById(R.id.school_graph);
        graph.setVisibility(View.GONE);

        //TextView
        nameTV = findViewById(R.id.tv_school_name);
        placeTV = findViewById(R.id.tv_school_place);
        louseInSchoolTV = findViewById(R.id.tv_cnt_louse_school);
        noClassesTV = findViewById(R.id.tv_no_classes);
        noClassesTV.setVisibility(View.GONE);
        missingDataTV = findViewById(R.id.tv_missing_data);
        missingDataTV.setVisibility(View.GONE);
        finalSchoolYearTV = findViewById(R.id.tv_school_final_start_year);
        lastCheckTV = findViewById(R.id.tv_last_check_date);

        //EditText
        schoolYearET = findViewById(R.id.et_school_year);
        int startYear = DateHelper.getSchoolYear();
        schoolYearET.setText(String.valueOf(startYear));
        schoolYearET.clearFocus();
        finalSchoolYearTV.setText(DateHelper.getShortYearString(startYear));
        schoolYearET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                noClassesTV.setVisibility(View.GONE);
                classesPB.setVisibility(View.VISIBLE);
                if(DateHelper.startYearToFinal(charSequence, getBaseContext(), schoolYearET, null)){
                    SchoolDao dao = new SchoolDao();
                    dao.getClassesToPage(school, getBaseContext(), Integer.valueOf(schoolYearET.getText().toString()));
                    schoolYearET.clearFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        schoolYearET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    if(!DateHelper.validYear(schoolYearET)){
                        schoolYearET.setText(String.valueOf(DateHelper.getSchoolYear()));
                        finalSchoolYearTV.setText(DateHelper.getShortYearString(Integer.valueOf(schoolYearET.getText().toString())));
                    }
                }
            }
        });
    }
}
