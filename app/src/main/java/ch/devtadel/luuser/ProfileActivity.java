package ch.devtadel.luuser;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    public static final String ME = "my Profile";
    public static final String USERMAIL = "email";
    private boolean myProfile;

    FirebaseAuth firebaseAuth;

    private TextView displayNameTV;
    private TextView placeTV;
    private TextView emailTV;
    private TextView verifiedTV;
    private Button verifyBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //UP-Button hinzufügen
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        setupContentViews();

        //
        ConstraintLayout myProfileCL = findViewById(R.id.my_profile);
        ConstraintLayout otherProfileCL = findViewById(R.id.other_profile);
        myProfileCL.setVisibility(View.GONE);
        otherProfileCL.setVisibility(View.GONE);
        //

        //Check wessen Profil das ist.
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            //Check ob eigenes Profil.
            if (bundle.getBoolean(ME)) {
                myProfile = true;
            } else {
                myProfile = false;  //Kann entfernt werden.
            }
            if(bundle.getString(USERMAIL) != null){
                if (bundle.getString(USERMAIL).equals(firebaseAuth.getCurrentUser().getEmail())) {
                    myProfile = true;
                } else {
                    myProfile = false;
                }
            }
        } else {
            myProfile = false;      //Kann entfernt werden.
        }
        //

        //Profil einrichten.
        if(firebaseAuth.getCurrentUser() != null) {
            if (myProfile) {
                setTitle("Mein Profil");
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                displayNameTV.setText(firebaseUser.getDisplayName());
                placeTV.setText("<Placeholder>"); //Todo: Platzhalter
                emailTV.setText(firebaseUser.getEmail());
                myProfileCL.setVisibility(View.VISIBLE);
                if (firebaseUser.isEmailVerified()) {
                    verifiedTV.setText(R.string.verified);
                    verifiedTV.setTextColor(getResources().getColor(R.color.colorVerified, null));
                } else {
                    verifiedTV.setText(R.string.not_verified);
                    verifiedTV.setTextColor(getResources().getColor(R.color.colorNotVerified, null));
                    verifyBTN.setVisibility(View.VISIBLE);
                }
            } else {
                setTitle("Profil");
                otherProfileCL.setVisibility(View.VISIBLE);
            }
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


    /**
     * verifyBTN onClick
     */
    public void verifyEmail(View view){
        verifyEmailDialog();
    }

    private void verifyEmailDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        final View promptView = li.inflate(R.layout.prompt_verify_email, null);

        //Promptbuilder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        //View dem Dialog zuweisen.
        alertDialogBuilder.setView(promptView);

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
        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button newLinkBtn = promptView.findViewById(R.id.btn_prompt_new_link);
        newLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser() != null){
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    Toast.makeText(promptView.getContext(), "Ihnen wurde eine neue Verifizierungsmail zugesendet!", Toast.LENGTH_LONG).show();
                    alertDialog.cancel();
                } else {
                    Toast.makeText(promptView.getContext(), "Sie sind nicht eingeloggt", Toast.LENGTH_LONG).show();
                    alertDialog.cancel();
                }
            }
        });

        Button reportProblemBTN = promptView.findViewById(R.id.btn_prompt_report_problem);
        reportProblemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ContactActivity.class));
                alertDialog.cancel();
            }
        });

        // show it
        alertDialog.show();
    }

    /**
     * Prozedur um alle Views zu initialisieren.
     * Soll Platz in der OnCreate()-Methode sparen.
     */
    private void setupContentViews(){
        displayNameTV = findViewById(R.id.tv_display_name);
        placeTV = findViewById(R.id.tv_profile_place);
        emailTV = findViewById(R.id.tv_profile_email);
        verifiedTV = findViewById(R.id.tv_profile_verified);

        verifyBTN = findViewById(R.id.btn_profile_verify);
        verifyBTN.setVisibility(View.GONE);
    }
}
