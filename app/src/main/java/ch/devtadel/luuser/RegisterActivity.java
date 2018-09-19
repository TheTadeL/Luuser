package ch.devtadel.luuser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.model.User;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private FirebaseAuth firebaseAuth;

    private EditText surnameET;
    private EditText prenameET;
    private EditText placeET;
    private EditText emailET;
    private EditText passwordET;
    private EditText confirmET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //UP-Button hinzufügen
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        surnameET = findViewById(R.id.et_surname);
        prenameET = findViewById(R.id.et_prename);
        placeET = findViewById(R.id.et_place);
        emailET = findViewById(R.id.et_email);
        passwordET = findViewById(R.id.et_password);
        confirmET = findViewById(R.id.et_confirm_password);

        Button registerBTN = findViewById(R.id.btn_register);
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
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

    private void createUser(){
        if(validForm()){
            User user = new User();
            user.setPrename(prenameET.getText().toString());
            user.setSurname(surnameET.getText().toString());
            user.setPlace(placeET.getText().toString());
            user.setEmail(emailET.getText().toString());
            user.setPassword(passwordET.getText().toString());

            Log.d(TAG, user.toString());

            createNewUser(user);
        }
    }

    private boolean validForm(){
        boolean valid = true;

        //Vorname
        String prename = prenameET.getText().toString();
        if(!TextUtils.isEmpty(prename)){
            prenameET.setError(null);
        } else {
            valid = false;
            prenameET.setError("Vorname benötigt");
        }

        //Nachname
        String surname = surnameET.getText().toString();
        if(!TextUtils.isEmpty(surname)){
            surnameET.setError(null);
        } else {
            valid = false;
            surnameET.setError("Nachname benötigt");
        }

        //Ort
        String place = placeET.getText().toString();
        if(!TextUtils.isEmpty(place)){
            placeET.setError(null);
        } else {
            valid = false;
            placeET.setError("Ortschaft benötigt");
        }

        //Email
        String mail = emailET.getText().toString();
        //TODO: Check ob die emailadresse bereits registriert ist
        if(!TextUtils.isEmpty(mail)){
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                valid = false;
                emailET.setError("Ungültige Eingabe!");
            } else {
                emailET.setError(null);
            }
        } else {
            valid = false;
            emailET.setError("Email benötigt!");
        }

        //Passwort
        String pw = passwordET.getText().toString();
        String pw2 = confirmET.getText().toString();
        if(TextUtils.isEmpty(pw)){
            passwordET.setError("Bitte Passwort wählen");
            valid = false;
        } else {
            if(!pw.equals(pw2)){
                passwordET.setError("Passwörter stimmen nicht überein");
                valid = false;
            } else {
                passwordET.setError(null);
            }
        }
        return valid;
    }

    private void createNewUser(final User user){
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            SchoolDao dao = new SchoolDao();
                            final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if(firebaseUser != null)
                                dao.createUserInFS(user, firebaseUser.getUid());

                            //Displaynamen setzen
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.getSurname() + " " + user.getPrename())
                                    .build();

                            if(firebaseUser != null) {
                                firebaseUser.updateProfile(request)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Displayname gesetzt: " + firebaseUser.getDisplayName());
                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class).putExtra(LoginActivity.EMAIL, firebaseUser.getEmail()));
                                                } else {
                                                    Log.d(TAG, "Fehler beim Setzen des Displaynamen");
                                                }
                                            }
                                        });
                            }
                            //
                            updateUI(firebaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getBaseContext(), "Fehler beim Erstellen des Benutzerkontos", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if(user == null){
            Log.d(TAG, "Kein User!!!");
        } else {
            //TODO: Dialog anzeigen: Email registrierung an diese Email <mail> [Ok] [Neu anfordern] [Emailadresse ändern]
            if(firebaseAuth.getCurrentUser() != null && !firebaseAuth.getCurrentUser().isEmailVerified()){
                firebaseAuth.getCurrentUser().sendEmailVerification();
            }
        }
    }
}
