package ch.devtadel.luuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.adapter.ChecksListAdapter;
import ch.devtadel.luuser.model.Check;

public class CheckListActivity extends AppCompatActivity {
    private static final String TAG = "CheckListActivity";
    public final static String ACTION_STRING_VALUES_LOADED = "values loaded";
    public final static String ACTION_STRING_CHECKS_LOADED = "values 2 loaded";

    private List<String> keyList = new ArrayList<>();
    public static List<String> valueList = new ArrayList<>();
    public static List<Check> data = new ArrayList<>();

    private Spinner keySP;
    private Spinner valueSP;
    private ProgressBar valueSpinnerPB;
    private RecyclerView.Adapter mainRecyclerAdapter;
    private RecyclerView mainRecyclerView;
    private ProgressBar mainProgressBar;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "VALUES LOADED BROADCAST RECEIVED!");

            if(intent.getAction().equals(ACTION_STRING_VALUES_LOADED)) {
                ArrayAdapter<String> valueSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, valueList);
                valueSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
                valueSP.setAdapter(valueSpinnerAdapter);

                valueSpinnerPB.setVisibility(View.GONE);
            } else if(intent.getAction().equals(ACTION_STRING_CHECKS_LOADED)){
                mainRecyclerView.setVisibility(View.VISIBLE);
                mainProgressBar.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        setTitle("Kontrollen einsehen");

        valueSpinnerPB = findViewById(R.id.progressBar_value_spinner);

        setupKeyList();
        setupSpinner();
        setupRecyclerView();

        //Receiver registrieren
        if(broadcastReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_VALUES_LOADED);
            intentFilter.addAction(ACTION_STRING_CHECKS_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Receiver registrieren
        if(broadcastReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_VALUES_LOADED);
            intentFilter.addAction(ACTION_STRING_CHECKS_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Receiver registrieren
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    private void setupKeyList(){
        keyList.add("Alle");
        keyList.add("Schule");
        keyList.add("Klasse");
        keyList.add("Datum");
        keyList.add("Läuse gefunden");
        keyList.add("keine Läuse");
    }

    private void setupSpinner(){
        keySP = findViewById(R.id.spinner_key);
        valueSP = findViewById(R.id.spinner_value);

        //KEYS
        ArrayAdapter<String> keySpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, keyList);
        keySpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        keySP.setAdapter(keySpinnerAdapter);
        keySP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "KEY SPINNER item selected!!!!");

                SchoolDao dao = new SchoolDao();
                String key = adapterView.getItemAtPosition(i).toString();

                valueList.clear();

                switch(key){
                    case "Schule":
                        valueSpinnerPB.setVisibility(View.VISIBLE);
                        valueSP.setVisibility(View.VISIBLE);

                        //Valuespinner Daten laden.
                        dao.keySpinnerSchools(getBaseContext());
                        break;
                    case "Klasse":
                        valueSpinnerPB.setVisibility(View.VISIBLE);
                        valueSP.setVisibility(View.VISIBLE);

                        //Valuespinner Daten laden.
                        dao.keySpinnerClasses(getBaseContext());
                        break;
                    case "Datum":
                        valueSpinnerPB.setVisibility(View.VISIBLE);
                        valueSP.setVisibility(View.VISIBLE);

                        //Valuespinner Daten laden.
                        dao.keySpinnerDate(getBaseContext());
                        break;
                    case "Läuse gefunden":
                        valueSP.setVisibility(View.GONE);

                        break;
                    case "keine Läuse":
                        valueSP.setVisibility(View.GONE);

                        break;
                    default:
                        dao.getAllChecksToPage(mainRecyclerAdapter, getBaseContext());
                        valueSP.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                valueList.clear();
            }
        });
        //END KEYS

        //VALUES
        valueSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "VALUE SPINNER item selected!!!!");

                mainRecyclerView.setVisibility(View.GONE);
                mainProgressBar.setVisibility(View.VISIBLE);

                String key = keySP.getSelectedItem().toString();
                boolean valid = true;
                String finalKey = "";

                switch (key) {
                    case "Schule":
                        finalKey = "Schulname";
                        break;
                    case "Klasse":
                        finalKey = "Klassenname";
                        break;
                    case "Datum":
                        finalKey = "Datum";
                        break;
                    default:
                        valid = false;
                }

                if (valid) {
                    SchoolDao dao = new SchoolDao();
                    dao.getChecksToPage(mainRecyclerAdapter, getBaseContext(), finalKey, valueSP.getSelectedItem().toString());
                }
                Toast.makeText(getBaseContext(), valueSP.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
                //END VALUES
    }

    /**
     * Prozedur um den RecyclerView für die Userliste vorzubereiten.
     */
    private void setupRecyclerView(){
        mainRecyclerView = findViewById(R.id.rv_checks_list);
        mainProgressBar = findViewById(R.id.progressBar_recyclerview);

        //Performance verbessern. Möglich, da die Listeneinträge alle die selbe grösse haben sollen.
        mainRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mainLayoutManager = new LinearLayoutManager(CheckListActivity.this);
        mainRecyclerView.setLayoutManager(mainLayoutManager);

        mainRecyclerAdapter = new ChecksListAdapter(data);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);
    }
}
