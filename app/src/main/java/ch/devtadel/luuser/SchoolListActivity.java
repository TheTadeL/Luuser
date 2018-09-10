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
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.adapter.SchoolListAdapter;
import ch.devtadel.luuser.model.School;

public class SchoolListActivity extends AppCompatActivity {
    FloatingActionButton addSchoolFAB;
    RecyclerView.Adapter mainRecyclerAdapter;

    private FirebaseAuth firebaseAuth;

    public static List<School> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        //Check ob der User authoriziert ist, um Einträge zu machen.
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(SchoolListActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            Toast.makeText(SchoolListActivity.this, R.string.pls_login, Toast.LENGTH_LONG).show();
        } else if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(SchoolListActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            Toast.makeText(SchoolListActivity.this, R.string.pls_verify, Toast.LENGTH_LONG).show();
        }

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
