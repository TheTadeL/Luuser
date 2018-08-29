package ch.devtadel.luuser;

import android.app.ActionBar;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.adapter.SchoolListAdapter;
import ch.devtadel.luuser.model.School;

public class SchoolListActivity extends AppCompatActivity {
    FloatingActionButton addSchoolFAB;
    RecyclerView schoolListRV;
    RecyclerView.Adapter mainRecyclerAdapter;

    public static List<School> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        FirebaseApp.initializeApp(this);
        setupRecyclerView();

        //UP-Button hinzufügen
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Komponente initialisieren
        addSchoolFAB = findViewById(R.id.fab_add_school);
        addSchoolFAB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SchoolListActivity.this, NewCheckActivity.class));
            }
        });

        SchoolDao dao = new SchoolDao();
        dao.loadSchoolList(mainRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Fügt das menu der Actionbar hinzu.
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    //Actionbar Komponente wird benutzt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.navigation_new_school:
                startActivity(new Intent(SchoolListActivity.this, AddSchoolActivity.class));
                return true;
            case R.id.navigation_graph:
                startActivity(new Intent(SchoolListActivity.this, GraphActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Prozedur um den RecyclerView für die Userliste vorzubereiten.
     */
    private void setupRecyclerView(){
        RecyclerView mainRecyclerView = findViewById(R.id.school_list);

        //Performance verbessern. Möglich, da die Listeneinträge alle die selbe grösse haben sollen.
        mainRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mainLayoutManager = new LinearLayoutManager(SchoolListActivity.this);
        mainRecyclerView.setLayoutManager(mainLayoutManager);

        mainRecyclerAdapter = new SchoolListAdapter(data);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);
    }
}
