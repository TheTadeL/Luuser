package ch.devtadel.luuser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import ch.devtadel.luuser.helper.Animator;
import ch.devtadel.luuser.helper.UserHelper;

public class MainActivity extends AppCompatActivity {
//    public static final String TAG = "MainActivity";

    private Animator animator;

    private FirebaseAuth firebaseAuth;
    private boolean loggedIn = false;
    private boolean verified = false;

    private TextView welcomeTV;
    private TextView notVerifiedTV;

    private CardView newCheckCV;
    private CardView schoolListCV;
    private CardView loginCV;
    private CardView logoutCV;
    private CardView profileCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check ob der User authoriziert ist, um Einträge zu machen.
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            //Nicht eingeloggt.
            loggedIn = false;
        } else if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
            //Eingelogt nicht Verifiziert.
            loggedIn = true;
            verified = false;
        } else {
            //Eingeloggt & Verifiziert
            loggedIn = true;
            verified = true;
        }

        //Views initialisieren
        setupContentViews();
        setupMenuCards();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            case R.id.bug_report:
                startActivity(new Intent(MainActivity.this, BugReportActivity.class).putExtra(BugReportActivity.TYPE, BugReportActivity.TYPE_BUG));
                return true;
            case R.id.profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra(ProfileActivity.ME, true));
                return true;
            case R.id.info:
                startActivity(new Intent(MainActivity.this, InfoActivity.class));
                return true;
            case R.id.signout:
                UserHelper.signOutDialog(firebaseAuth, this, getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setupMenuCards() {
        if (loggedIn) {
            newCheckCV.setVisibility(View.VISIBLE);
            schoolListCV.setVisibility(View.VISIBLE);
            logoutCV.setVisibility(View.VISIBLE);
            loginCV.setVisibility(View.GONE);

            //Profil sichtbar wenn eingeloggt;
            profileCV.setVisibility(View.VISIBLE);

            if (!verified) {
                profileCV.setCardBackgroundColor(getResources().getColor(R.color.colorNotVerifiedBG, null));
                notVerifiedTV.setVisibility(View.VISIBLE);
            } else {
                profileCV.setCardBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_default, null));
                notVerifiedTV.setVisibility(View.GONE);
            }
            // Die Willkommen Nachricht setzen (" Willkommen zurück, <Vorname> ")
            String prename = " ";
            if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().getDisplayName() != null)
                prename += firebaseAuth.getCurrentUser().getDisplayName().split(" ")[1];

            welcomeTV.setText(getResources().getString(R.string.welcome_back) + prename);
        } else {
            profileCV.setVisibility(View.GONE);
            newCheckCV.setVisibility(View.GONE);
            schoolListCV.setVisibility(View.GONE);
            loginCV.setVisibility(View.VISIBLE);
            logoutCV.setVisibility(View.INVISIBLE);
            // Die Willkommen Nachricht setzen (" Willkommen bei <AppName> ")
            welcomeTV.setText(getResources().getString(R.string.welcome) + " " + getResources().getString(R.string.app_name));
        }
    }

    public void newCheck(View view) {
        animator.animateCardPress((CardView) view);
        if (loggedIn && verified) {
            startActivity(new Intent(MainActivity.this, NewCheckActivity.class));
        } else if (!loggedIn) {
            Toast.makeText(getBaseContext(), R.string.pls_login, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), R.string.pls_verify, Toast.LENGTH_LONG).show();
        }
    }

    public void schoolList(View view) {
        animator.animateCardPress((CardView) view);
        if (loggedIn && verified) {
            startActivity(new Intent(MainActivity.this, SchoolListActivity.class));
        } else if (!loggedIn) {
            Toast.makeText(getBaseContext(), R.string.pls_login, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), R.string.pls_verify, Toast.LENGTH_LONG).show();
        }
    }

    public void checkList(View view) {
        animator.animateCardPress((CardView) view);
        startActivity(new Intent(MainActivity.this, CheckListActivity.class));
    }

    public void signOut(View view) {
        animator.animateCardPress((CardView) view);
        UserHelper.signOutDialog(firebaseAuth, this, getIntent());
    }

    public void signIn(View view) {
        animator.animateCardPress((CardView) view);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void toInfo(View view) {
        animator.animateCardPress((CardView) view);
        startActivity(new Intent(MainActivity.this, InfoActivity.class));
    }

    public void toProfile(View view) {
        animator.animateCardPress((CardView) view);
        startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra(ProfileActivity.ME, true));
    }


    /**
     * Prozedur um alle Views zu initialisieren.
     * Soll Platz in der OnCreate()-Methode sparen.
     */
    private void setupContentViews() {
        animator = new Animator();

        //TextView
        welcomeTV = findViewById(R.id.tv_welcome_msg);
        notVerifiedTV = findViewById(R.id.tv_main_not_verified);

        //CardView
        schoolListCV = findViewById(R.id.cv_main_school_list);
        newCheckCV = findViewById(R.id.cv_main_new_check);
        profileCV = findViewById(R.id.cv_main_profile);
        loginCV = findViewById(R.id.cv_main_signin);
        logoutCV = findViewById(R.id.cv_main_signout);
    }
}
