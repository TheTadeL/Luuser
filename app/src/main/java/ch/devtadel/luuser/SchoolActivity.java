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

import java.util.ArrayList;
import java.util.List;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.adapter.ClassListAdapter;
import ch.devtadel.luuser.model.School;
import ch.devtadel.luuser.model.SchoolClass;

public class SchoolActivity extends AppCompatActivity {
    private static final String TAG = "SchoolActivity";
    public static final String SCHOOL_NAME = "school";
    public static final String ACTION_STRING_SCHOOL_LOADED = "schoolLoaded";
    public static final String ACTION_STRING_CLASSES_LOADED = "classesLoaded";

    private RecyclerView.Adapter mainRecyclerAdapter;
    public static List<SchoolClass> data = new ArrayList<>();
    public static School school;

    private ConstraintLayout contentCON;

    private TextView nameTV;
    private TextView placeTV;
    private TextView louseInSchoolTV;
    private TextView checksInSchoolTV;
    private TextView noClassesTV;

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
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        setTitle("Detailansicht Einrichtung");

        //Daten zurücksetzen wenn neue Schule geöffnet wird.
        data.clear();
        school = null;

        contentCON = findViewById(R.id.constraint_content_school);
        contentCON.setVisibility(View.GONE);

        schoolPB = findViewById(R.id.progressBar_school);
        schoolPB.setVisibility(View.VISIBLE);
        classesPB = findViewById(R.id.progressBar_classes);
        classesPB.setVisibility(View.VISIBLE);

        //Receiver registrieren
        if(activityReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_SCHOOL_LOADED);
            intentFilter.addAction(ACTION_STRING_CLASSES_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(activityReceiver, intentFilter);
        }

        setupRecyclerView();

        nameTV = findViewById(R.id.tv_school_name);
        placeTV = findViewById(R.id.tv_school_place);
        louseInSchoolTV = findViewById(R.id.tv_cnt_louse_school);
        checksInSchoolTV = findViewById(R.id.tv_cnt_checks_school);
        noClassesTV = findViewById(R.id.tv_no_classes);
        noClassesTV.setVisibility(View.GONE);

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

        //Wenn kein Schulname mitgegeben wurde, umleiten auf die MainActivity.
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            if (bundle.getString(SCHOOL_NAME) != null) {
                SchoolDao dao = new SchoolDao();
                dao.getSchoolToPage(bundle.getString(SCHOOL_NAME), this);
            } else {
                startActivity(new Intent(SchoolActivity.this, MainActivity.class));
            }
        } else {
            startActivity(new Intent(SchoolActivity.this, MainActivity.class));
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

        mainRecyclerAdapter = new ClassListAdapter(data);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);
    }

    /**
     * Wird aufgerufen wenn die Schulseitendaten geladen haben;
     */
    private void loadSchoolPage(){
        //Recycler wird in der Funktion notifiziert.
        SchoolDao dao = new SchoolDao();
        dao.getClassesToPage(school, mainRecyclerAdapter, this);

        nameTV.setText(school.getName());
        placeTV.setText(school.getPlace());
        contentCON.setVisibility(View.VISIBLE);
        schoolPB.setVisibility(View.GONE);
    }

    /**
     * Wird aufgerufen wenn die Klassendaten geladen haben;
     */
    private void loadClasses(){
        for(int i = 0; i < data.size(); i++){
            school.setCntLouse(school.getCntLouse() + data.get(i).getCntLouse());
            school.setCntChecks(school.getCntChecks() + data.get(i).getCntChecks());
        }
        louseInSchoolTV.setText(String.valueOf(school.getCntLouse()));
        checksInSchoolTV.setText(String.valueOf(school.getCntChecks()));
        classesPB.setVisibility(View.GONE);
        if(data.size() == 0){
            noClassesTV.setVisibility(View.VISIBLE);
        }
    }

    private void newClassDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptView = li.inflate(R.layout.add_class_prompt, null);

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
