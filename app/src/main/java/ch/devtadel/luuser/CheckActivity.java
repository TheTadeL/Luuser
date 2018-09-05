package ch.devtadel.luuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.model.Check;

public class CheckActivity extends AppCompatActivity {
    public static final String DOCUMENT_ID = "document id";
    public static final String ACTION_STRING_CHECK_LOADED = "check loaded";
    public static Check check;

    private ConstraintLayout contentCL;
    private ConstraintLayout errorCL;
    private ProgressBar progressBar;

    private TextView classTV;
    private TextView schoolTV;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Wenn check "null" ist, fehler anzeigen:
            if(check != null){
                //Todo: Daten abfüllen
                showCheck();
                contentCL.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                showErrorView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        setTitle(R.string.check_title);

        //Receiver registrieren
        if(broadcastReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_CHECK_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(broadcastReceiver, intentFilter);
        }

        progressBar = findViewById(R.id.pb_check);
        progressBar.setVisibility(View.VISIBLE);

        //ErrorView
        errorCL = findViewById(R.id.constraint_check_error);
        errorCL.setVisibility(View.GONE);

        //ContentView
        contentCL = findViewById(R.id.constraint_check_content);
        contentCL.setVisibility(View.GONE);
        classTV = findViewById(R.id.tv_check_class_name);
        schoolTV = findViewById(R.id.tv_check_school_name);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString(DOCUMENT_ID) != null){
                SchoolDao dao = new SchoolDao();
                dao.getCheckById(bundle.getString(DOCUMENT_ID), getBaseContext());
            } else {
                showErrorView();
            }
        } else {
            showErrorView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Receiver registrieren
        if(broadcastReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_CHECK_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    /**
     * Prozedur um die Check-Informationen anzeigen zu lassen
     * Views werden mit den Daten abgefüllt.
     * (wird im broadcastReceiver.onReceive() aufgerufen)
     */
    private void showCheck(){
        classTV.setText(check.getClassName());
        schoolTV.setText(check.getSchoolName());
    }

    private void showErrorView(){
        progressBar.setVisibility(View.GONE);
        contentCL.setVisibility(View.GONE);
        errorCL.setVisibility(View.VISIBLE);
    }
}
