package ch.devtadel.luuser;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    public static final String ME = "my Profile";
    public static final String USERMAIL = "email";
    private boolean myProfile;

    FirebaseAuth firebaseAuth;

    TextView displayNameTV;
    TextView placeTV;
    TextView emailTV;
    TextView verifiedTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        //
        displayNameTV = findViewById(R.id.tv_display_name);
        placeTV = findViewById(R.id.tv_profile_place);
        emailTV = findViewById(R.id.tv_profile_email);
        verifiedTV = findViewById(R.id.tv_profile_verified);

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
        if(myProfile){
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            displayNameTV.setText(firebaseUser.getDisplayName());
            placeTV.setText("<Placeholder>"); //Todo: Platzhalter
            emailTV.setText(firebaseUser.getEmail());
            myProfileCL.setVisibility(View.VISIBLE);
            if(firebaseUser.isEmailVerified()){
                verifiedTV.setText(R.string.verified);
                verifiedTV.setTextColor(getResources().getColor(R.color.colorVerified, null));
            } else {
                verifiedTV.setText(R.string.not_verified);
                verifiedTV.setTextColor(getResources().getColor(R.color.colorNotVerified, null));
            }
        } else {
            otherProfileCL.setVisibility(View.VISIBLE);
        }
    }
}
