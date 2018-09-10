package ch.devtadel.luuser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private FirebaseAuth firebaseAuth;
    private boolean loggedIn = false;

    private TextView welcomeTV;

    private CardView newCheckCV;
    private CardView schoolListCV;
    private CardView checkListCV;
    private CardView loginCV;
    private CardView logoutCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views initialisieren
        setupContentViews();

        //Check ob der User authoriziert ist, um Einträge zu machen.
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            loggedIn = false;
        } else if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
            //Todo: wenn eingeloggt aber nicht verifiziert;
            loggedIn = true;
        } else {
            loggedIn = true;
        }

        setupMenuCards();
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
                startActivity(new Intent(MainActivity.this, AddSchoolActivity.class));
                return true;
            case R.id.navigation_test:
                startActivity(new Intent(MainActivity.this, DeveloperActivity.class));
                return true;
            case R.id.navigation_test2:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra(ProfileActivity.ME, true));
                return true;
            case R.id.navigation_test3:
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setupMenuCards(){
        if(loggedIn){
            newCheckCV.setVisibility(View.VISIBLE);
            schoolListCV.setVisibility(View.VISIBLE);
            logoutCV.setVisibility(View.VISIBLE);
            loginCV.setVisibility(View.GONE);
            // Die Willkommen Nachricht setzen (" Willkommen zurück, <Vorname> ")
            String prename = " ";
            try {
                prename += firebaseAuth.getCurrentUser().getDisplayName().split(" ")[1];
            } catch(java.lang.NullPointerException e){
               e.printStackTrace();
            }
            welcomeTV.setText(getResources().getString(R.string.welcome_back) + prename);
        } else {
            newCheckCV.setVisibility(View.GONE);
            schoolListCV.setVisibility(View.GONE);
            loginCV.setVisibility(View.VISIBLE);
            logoutCV.setVisibility(View.INVISIBLE);
            // Die Willkommen Nachricht setzen (" Willkommen bei <AppName> ")
            welcomeTV.setText(getResources().getString(R.string.welcome) + " " + getResources().getString(R.string.app_name));
        }
    }

    public void newCheck(View view){
        startActivity(new Intent(MainActivity.this, NewCheckActivity.class));
    }

    public void schoolList(View view){
        startActivity(new Intent(MainActivity.this, SchoolListActivity.class));
    }

    public void checkList(View view){
        startActivity(new Intent(MainActivity.this, CheckListActivity.class));
    }

    public void signOut(View view){
        //Todo: Dialog zum Bestätigen.
        firebaseAuth.signOut();
        finish();
        Intent intent = getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void signIn(View view){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void toInfo(View view){
        startActivity(new Intent(MainActivity.this, InfoActivity.class));
    }

    public void toProfile(View view){
        startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra(ProfileActivity.ME, true));
    }

    /**
     * Prozedur um alle Views zu initialisieren.
     * Soll Platz in der OnCreate()-Methode sparen.
     */
    private void setupContentViews(){
        //TextView
        welcomeTV = findViewById(R.id.tv_welcome_msg);

        //CardView
        checkListCV = findViewById(R.id.cv_main_search_check);
        schoolListCV = findViewById(R.id.cv_main_school_list);
        newCheckCV = findViewById(R.id.cv_main_new_check);
        loginCV = findViewById(R.id.cv_main_signin);
        logoutCV = findViewById(R.id.cv_main_signout);
    }
}
