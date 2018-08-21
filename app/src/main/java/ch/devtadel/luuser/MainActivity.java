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

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.adapter.SchoolListAdapter;
import ch.devtadel.luuser.model.School;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addSchoolFAB;
    RecyclerView schoolListRV;
    RecyclerView.Adapter mainRecyclerAdapter;

    public static List<School> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                startActivity(new Intent(MainActivity.this, AddSchoolActivity.class));
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
            case R.id.navigation_testeichbuehl:
                startActivity(new Intent(MainActivity.this, SchoolActivity.class).putExtra(SchoolActivity.SCHOOL_NAME, "test"));
                return true;
            case R.id.test_date:
                startActivity(new Intent(MainActivity.this, NewCheckActivity.class));
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

        RecyclerView.LayoutManager mainLayoutManager = new LinearLayoutManager(MainActivity.this);
        mainRecyclerView.setLayoutManager(mainLayoutManager);

        mainRecyclerAdapter = new SchoolListAdapter(data);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);
    }
}
