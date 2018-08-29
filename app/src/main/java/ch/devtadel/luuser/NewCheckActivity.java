package ch.devtadel.luuser;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.model.Check;
import ch.devtadel.luuser.model.School;

public class NewCheckActivity extends AppCompatActivity {
    public final static String TAG = "NewCheckActivity";
    public final static String ACTION_STRING_SCHOOLNAMES_LOADED = "schoolnameSpinnerLoaded";
    public final static String ACTION_STRING_CLASSNAMES_LOADED = "classnameSpinnerLoaded";

    private static int yearCheck;
    private static int monthCheck;
    private static int dayCheck;
    private static Calendar calendar;

    private static TextView dateTV;

    private EditText studentCountET;
    private EditText louseCountET;

    private Spinner schoolSP;
    private Spinner classSP;

    public static List<String> schoolNames = new ArrayList<>();
    public static List<String> classNames = new ArrayList<>();

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
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_check);

        setTitle("Kontrolleneditor");

        //Spinner initialisieren
        schoolSP = findViewById(R.id.spinner_school);
        classSP = findViewById(R.id.spinner_class);

        studentCountET = findViewById(R.id.et_student_count);
        louseCountET = findViewById(R.id.et_louse_count);

        final SchoolDao dao = new SchoolDao();

        schoolSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dao.setupClassSpinner(getApplicationContext(), adapterView.getItemAtPosition(i).toString());
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

        calendar = Calendar.getInstance();
        yearCheck = calendar.get(Calendar.YEAR);
        monthCheck = calendar.get(Calendar.MONTH);
        dayCheck = calendar.get(Calendar.DAY_OF_MONTH);

        dateTV = findViewById(R.id.tv_date);
        dateTV.setText(dayCheck+"."+(monthCheck+1)+"."+yearCheck);

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

                    SchoolDao dao = new SchoolDao();
                    dao.createCheck(check, getBaseContext());
                }
            }
        });
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
}
