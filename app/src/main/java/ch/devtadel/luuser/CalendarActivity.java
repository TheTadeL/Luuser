package ch.devtadel.luuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.model.Check;

public class CalendarActivity extends AppCompatActivity {
    public static final String ACTION_STRING_CALENDARDATA_LOADED = "calendardata ready";
    public static final String TAG = "CalendarActivity";
    public static List<Date> data = new ArrayList<>();

    private CalendarView calendarView;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            List<EventDay> events = createEventDays();
//            Log.d(TAG, "EVENTS: "+events.size());
//            calendarView.setEvents(events);
            List<EventDay> events = new ArrayList<>();

            Calendar calendar = Calendar.getInstance();
            events.add(new EventDay(calendar, R.drawable.laus));

            CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
            calendarView.setEvents(events);

            List<EventDay> events2 = new ArrayList<>();

            Calendar calendar2 = Calendar.getInstance();
            events2.add(new EventDay(calendar2, R.drawable.untersuchung));

            calendarView.setEvents(events2);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //Receiver registrieren
        if(broadcastReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_CALENDARDATA_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(broadcastReceiver, intentFilter);
        }

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                dayInfoDialog(eventDay.getCalendar().getTime());
            }
        });

        List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.laus));

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setEvents(events);

        SchoolDao dao = new SchoolDao();
        dao.getChecksToCalendar(getBaseContext(), Calendar.getInstance().getTime());
        Log.d(TAG, Calendar.getInstance().getTime().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Receiver registrieren
        if(broadcastReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_CALENDARDATA_LOADED);
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

    private void dayInfoDialog(Date date){
        LayoutInflater li = LayoutInflater.from(this);
        View promptView = li.inflate(R.layout.prompt_day_info, null);

        //Promptbuilder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        //View dem Dialog zuweisen.
        alertDialogBuilder.setView(promptView);

        String dateString = new SimpleDateFormat("EEEE, dd. MMMM yyyy").format(date);
        final TextView promptTitle = promptView.findViewById(R.id.tv_calendar_date);
        promptTitle.setText(dateString);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setNegativeButton("schliessen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private List<EventDay> createEventDays(){
        List<EventDay> returnList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for(Date date : data){
            cal.setTime(date);
            Log.d(TAG, cal.getTime().toString());
            returnList.add(new EventDay(cal, R.drawable.untersuchung));
        }

        Log.d(TAG, "Grösse returnList: "+returnList.size());

        return returnList;
    }
}
