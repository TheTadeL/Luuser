package ch.devtadel.luuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.model.School;

public class AddSchoolActivity extends AppCompatActivity {
    private final String TAG = "AddSchoolActivity";
    public static final String ACTION_STRING_SCHOOL_CREATED = "schoolCreated";
    public static final String ACTION_STRING_SCHOOL_CREATE_FAILED = "schoolCreateFailed";

    private EditText nameET;
    private EditText placeET;
    private EditText cantonET;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_STRING_SCHOOL_CREATED)){
                Log.d(TAG, "SCHOOL SUCCESSFULLY CREATED!");
                Toast.makeText(AddSchoolActivity.this, "Schule erstellt", Toast.LENGTH_LONG).show();
                AddSchoolActivity.this.finish();
            } else if(intent.getAction().equals(ACTION_STRING_SCHOOL_CREATE_FAILED)){
                Log.d(TAG, "SCHOOL CREATING FAILED!");
                Toast.makeText(AddSchoolActivity.this, "Schule mit diesem Namen existiert bereits!", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //Receiver registrieren
        if(activityReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_SCHOOL_CREATED);
            intentFilter.addAction(ACTION_STRING_SCHOOL_CREATE_FAILED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(activityReceiver, intentFilter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);

        setTitle("Einrichtung erfassen");

        //
        nameET = findViewById(R.id.et_new_school_name);
        placeET = findViewById(R.id.et_new_school_place);
        cantonET = findViewById(R.id.et_new_school_canton);
        Button saveSchoolBTN = findViewById(R.id.btn_save_new_school);
        saveSchoolBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //TODO: Meldung & Weiterleiten
                School newSchool = new School(nameET.getText().toString(), placeET.getText().toString(), cantonET.getText().toString());

                SchoolDao dao = new SchoolDao();
                dao.createSchoolInFS(newSchool, AddSchoolActivity.this);
            }
        });

        //Receiver registrieren
        if(activityReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_SCHOOL_CREATED);
            intentFilter.addAction(ACTION_STRING_SCHOOL_CREATE_FAILED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(activityReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(activityReceiver != null){
            unregisterReceiver(activityReceiver);
        }
    }
}