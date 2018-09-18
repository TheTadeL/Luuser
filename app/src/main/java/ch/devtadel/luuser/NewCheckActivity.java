package ch.devtadel.luuser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.helper.DateHelper;
import ch.devtadel.luuser.model.Check;
import ch.devtadel.luuser.model.School;

public class NewCheckActivity extends AppCompatActivity {
    public final static String TAG = "NewCheckActivity";
    public final static String ACTION_STRING_SCHOOLNAMES_LOADED = "schoolnameSpinnerLoaded";
    public final static String ACTION_STRING_CLASSNAMES_LOADED = "classnameSpinnerLoaded";

    private static int yearCheck;
    private static int monthCheck;
    private static int dayCheck;

    private static TextView dateTV;

    private EditText studentCountET;
    private EditText louseCountET;
    private EditText startYearET;

    private TextView finalStartYearTV;

    private Spinner schoolSP;
    private Spinner classSP;
    private ProgressBar classPB;

    public static List<String> schoolNames = new ArrayList<>();
    public static List<String> classNames = new ArrayList<>();

    private FirebaseAuth firebaseAuth;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_STRING_SCHOOLNAMES_LOADED)){
                Log.d(TAG, "SCHOOL SPINNER LOADED BROADCAST RECEIVED!");

                ArrayAdapter<String> schoolNamesAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, schoolNames);
                schoolNamesAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
                schoolSP.setAdapter(schoolNamesAdapter);

                Bundle bundle = getIntent().getExtras();
                if(bundle != null){
                    if(bundle.getString(SchoolActivity.SCHOOL_NAME) != null){
                        schoolSP.setSelection(schoolNamesAdapter.getPosition(bundle.getString(SchoolActivity.SCHOOL_NAME)));
                    }
                }
            } else if(intent.getAction().equals(ACTION_STRING_CLASSNAMES_LOADED)){
                Log.d(TAG, "CLASSES SPINNER LOADED BROADCAST RECEIVED!");

                ArrayAdapter<String> classNamesAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, classNames);
                classNamesAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
                classSP.setAdapter(classNamesAdapter);
                classSP.setVisibility(View.VISIBLE);
                classPB.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_check);

        //UP-Button hinzufügen
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Check ob der User authoriziert ist, um Einträge zu machen.
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(NewCheckActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            Toast.makeText(NewCheckActivity.this, R.string.pls_login, Toast.LENGTH_LONG).show();
        } else if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(NewCheckActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            Toast.makeText(NewCheckActivity.this, R.string.pls_verify, Toast.LENGTH_LONG).show();
        }

        setupContentViews();
        setTitle("Kontrolleneditor");

        //Das jetzige Jahr setzen(Jahrgang)
        startYearET.setText(String.valueOf(DateHelper.getSchoolYear()));
        finalStartYearTV.setText(DateHelper.getShortYearString(DateHelper.getSchoolYear()));

        startYearET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(DateHelper.startYearToFinal(charSequence, getBaseContext(), startYearET, finalStartYearTV)){
                    loadClassSpinner(new SchoolDao(), Integer.valueOf(startYearET.getText().toString()));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final SchoolDao dao = new SchoolDao();
        schoolSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(DateHelper.validYear(startYearET)) {
                    int startYear = Integer.valueOf(startYearET.getText().toString());
                    loadClassSpinner(dao, startYear);
                } else {
                    Toast.makeText(getBaseContext(), "Ungültigen Jahrgang gewählt!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                classNames.clear();
            }
        });
        dao.setupSchoolSpinner(this);

        //Receiver registrieren
        if(activityReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_SCHOOLNAMES_LOADED);
            intentFilter.addAction(ACTION_STRING_CLASSNAMES_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(activityReceiver, intentFilter);
        }

        ImageButton datePickerBTN = findViewById(R.id.ibtn_pick_date);
        datePickerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        Button newCheckBTN = findViewById(R.id.btn_new_check);
        newCheckBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()) {
                    Check check = new Check();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        check.setDate(sdf.parse(dayCheck+"-"+(monthCheck+1)+"-"+yearCheck));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    check.setClassName(classSP.getSelectedItem().toString());
                    check.setSchoolName(schoolSP.getSelectedItem().toString());
                    check.setLouseCount(Integer.valueOf(louseCountET.getText().toString()));
                    check.setStudentCount(Integer.valueOf(studentCountET.getText().toString()));
                    check.setNoLouse(check.getLouseCount() == 0);
                    check.setClassStartYear(Integer.valueOf(startYearET.getText().toString()));

                    newConfirmNewCheckPrompt(check);
                }
            }
        });
        //TODO: PROMPT EINGABEN ÜBERPRÜFEN
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Receiver registrieren
        if(activityReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_SCHOOLNAMES_LOADED);
            intentFilter.addAction(ACTION_STRING_CLASSNAMES_LOADED);
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, yearCheck, monthCheck, dayCheck);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            yearCheck = year;
            monthCheck = month;
            dayCheck = day;

            dateTV = getActivity().findViewById(R.id.tv_date);
            dateTV.setText(day+"."+(month+1)+"."+year);
        }
    }

    private boolean validateForm(){
        boolean valid = true;

        if(TextUtils.isEmpty(studentCountET.getText().toString()) || !TextUtils.isDigitsOnly(studentCountET.getText().toString())){
            studentCountET.setError("Required.");
            valid = false;
        }
        else{
            studentCountET.setError(null);
        }
        if(TextUtils.isEmpty(louseCountET.getText().toString()) || !TextUtils.isDigitsOnly(louseCountET.getText().toString())){
            louseCountET.setError("Required.");
            valid = false;
        }
        else{
            louseCountET.setError(null);
        }

        if(TextUtils.isEmpty(dateTV.getText())){
            dateTV.setError("Required.");
            valid = false;
        } else {
            dateTV.setError(null);
        }

        return valid;
    }

    /**
     * SchoolActivity wird im "neue Klasse - Modus" gestartet, um eine neue Klasse zu erstellen.
     * @param view
     */
    public void toNewSchoolClass(View view){
        if(schoolSP.getSelectedItem() != null && !TextUtils.isEmpty(schoolSP.getSelectedItem().toString())){
            Intent intent = new Intent(NewCheckActivity.this, SchoolActivity.class)
                    .putExtra(SchoolActivity.SCHOOL_NAME, schoolSP.getSelectedItem().toString())
                    .putExtra(SchoolActivity.TO_NEW_CLASS, true);
            //Wenn ein gültiges Jahr gewählt ist, dieses mitgeben. sonnst aktueller Jahrgang.
            if(DateHelper.validYear(startYearET)){
                intent.putExtra(SchoolActivity.SCHOOL_YEAR, Integer.valueOf(startYearET.getText().toString()));
            } else {
                intent.putExtra(SchoolActivity.SCHOOL_YEAR, DateHelper.getSchoolYear());
            }
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Bitte wählen Sie eine Einrichtung aus!", Toast.LENGTH_LONG).show();
        }
    }

    public void toNewSchool(View view){
        startActivity(new Intent(NewCheckActivity.this, AddSchoolActivity.class));
    }

    private void loadClassSpinner(SchoolDao dao, int startYear){
        dao.setupClassSpinner(getApplicationContext(), schoolSP.getSelectedItem().toString(), startYear);
        classSP.setVisibility(View.INVISIBLE);
        classPB.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void newConfirmNewCheckPrompt(final Check check){
        LayoutInflater li = LayoutInflater.from(this);
        View promptView = li.inflate(R.layout.prompt_new_check, null);

        //Promptbuilder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        //View dem Dialog zuweisen.
        alertDialogBuilder.setView(promptView);

        final TextView promptDateTV = promptView.findViewById(R.id.prompt_new_check_date);
        promptDateTV.setText(dayCheck+"."+(monthCheck+1)+"."+yearCheck);
        final TextView promptClassTV = promptView.findViewById(R.id.prompt_new_check_class);
        promptClassTV.setText(check.getClassName());
        final TextView promptLongYearTV = promptView.findViewById(R.id.prompt_new_check_long_year);
        promptLongYearTV.setText(DateHelper.getLongYearString(check.getClassStartYear()));
        final TextView promptSchoolTV = promptView.findViewById(R.id.prompt_new_check_school);
        promptSchoolTV.setText(check.getSchoolName());
        final TextView promptCntStudentsTV = promptView.findViewById(R.id.prompt_new_check_cnt_students);
        promptCntStudentsTV.setText(String.valueOf(check.getStudentCount()));
        final TextView promptCntLouseTV = promptView.findViewById(R.id.prompt_new_check_cnt_louse);
        promptCntLouseTV.setText(String.valueOf(check.getLouseCount()));

        // Dialognachricht setzen
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Speichern",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //TODO: AUF Speichern gedrückt.
                                SchoolDao dao = new SchoolDao();
                                dao.createCheck(check, getBaseContext(), firebaseAuth.getCurrentUser());
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
    @SuppressLint("SetTextI18n")
    private void setupContentViews(){
        //Datum
        Calendar cal = Calendar.getInstance();
        dayCheck = cal.get(Calendar.DAY_OF_MONTH);
        monthCheck = cal.get(Calendar.MONTH);
        yearCheck = cal.get(Calendar.YEAR);

        //Spinner
        schoolSP = findViewById(R.id.spinner_school);
        classSP = findViewById(R.id.spinner_class);
        //ProgressBar
        classPB = findViewById(R.id.pb_new_check_classes);

        //EditText
        studentCountET = findViewById(R.id.et_student_count);
        louseCountET = findViewById(R.id.et_louse_count);
        startYearET = findViewById(R.id.et_new_check_starting_year);    //TODO: startYear anhand des Datums setzen.
                                                                        //TODO; finalStartYear anhand von startYearET setzen.

        finalStartYearTV = findViewById(R.id.tv_new_check_final_startyear);

        //Date-TextView
        dateTV = findViewById(R.id.tv_date);
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        //das heutige Datum setzen.
        dateTV.setText("Heute (" +dayCheck+"."+(monthCheck+1)+"."+yearCheck + ")");
    }
}
