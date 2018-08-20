package ch.devtadel.luuser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.devtadel.luuser.adapter.ClassListAdapter;
import ch.devtadel.luuser.model.Klasse;

public class SchoolActivity extends AppCompatActivity {
    private RecyclerView.Adapter mainRecyclerAdapter;
    public static List<Klasse> data = new ArrayList<>();
    public static int louseInClass;
    public static int checksInClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        TextView louseInClassTV = findViewById(R.id.tv_cnt_louse_school);
        TextView checksInClassTV = findViewById(R.id.tv_cnt_checks_school);

        setupRecyclerView();
        fillTestData();

        for(int i = 0; i < data.size(); i++){
            louseInClass += data.get(i).getAnzLause();
            checksInClass += data.get(i).getAnzKontrollen();
        }

        louseInClassTV.setText(String.valueOf(louseInClass));
        checksInClassTV.setText(String.valueOf(checksInClass));
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

    private void fillTestData(){
        data.add(new Klasse("Klasse 1a", 21, 2, 5));
        data.add(new Klasse("Klasse 1b", 17, 0, 0));
        data.add(new Klasse("Klasse 1c", 19, 2, 1));
        data.add(new Klasse("Klasse 2a", 23, 1, 0));
        data.add(new Klasse("Klasse 2b", 20, 1, 1));
        mainRecyclerAdapter.notifyDataSetChanged();
    }
}
