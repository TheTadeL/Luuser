package ch.devtadel.luuser;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.R;

public class DeveloperActivity extends AppCompatActivity {

    private Spinner collectionSP;
    private Spinner datatypeSP;
    private EditText fieldNameET;
    private EditText defaultValueET;
    private Button commitBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        //UP-Button hinzufügen
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupContentViews();
        commitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String collection = collectionSP.getSelectedItem().toString();
                String fieldName = fieldNameET.getText().toString();
                String datatype = datatypeSP.getSelectedItem().toString();
                String defaultValue = defaultValueET.getText().toString();

                SchoolDao dao = new SchoolDao();
                dao.newField(getBaseContext(), collection, fieldName, datatype, defaultValue);
            }
        });
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
     * Prozedur um alle Views zu initialisieren.
     * Soll Platz in der OnCreate()-Methode sparen.
     */
    private void setupContentViews(){
        //Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, SchoolDao.DB_COLLECTIONS);
        collectionSP = findViewById(R.id.sp_dev_collection);
        collectionSP.setAdapter(adapter);

        datatypeSP = findViewById(R.id.sp_dev_datatype);

        //EditText
        fieldNameET = findViewById(R.id.tv_dev_field_name);
        defaultValueET = findViewById(R.id.tv_dev_default_value);

        //Button
        commitBTN = findViewById(R.id.btn_dev_commit);
    }
}
