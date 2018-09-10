package ch.devtadel.luuser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    public static final String EMAIL = "email";

    private Button loginBTN;
    private EditText emailET;
    private EditText passwordET;
    private TextView successTV;
    private ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase Authorization
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        //Views initialisieren
        setupContentViews();

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                if(validateForm()) {
                    String email = emailET.getText().toString();
                    String password = passwordET.getText().toString();
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "falsche Mailadresse oder Passwort", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString(EMAIL) != null){
                successTV.setVisibility(View.VISIBLE);
                emailET.setText(bundle.getString(EMAIL));
            }
        }
    }

    private boolean validateForm(){
        boolean valid = true;

        //Email
        if(TextUtils.isEmpty(emailET.getText())){
            valid = false;
            emailET.setError("Email-Adresse benötigt");
        } else {
            emailET.setError(null);
        }

        //Passwort
        if(TextUtils.isEmpty(passwordET.getText())){
            valid = false;
            passwordET.setError("Passwort benötigt");
        } else {
            passwordET.setError(null);
        }

        return valid;
    }

    /**
     * Prozedur um alle Views zu initialisieren.
     * Soll Platz in der OnCreate()-Methode sparen.
     */
    private void setupContentViews(){
        //ProgressBar
        progressBar = findViewById(R.id.pb_login);
        progressBar.setVisibility(View.GONE);

        //TextView
        successTV = findViewById(R.id.tv_login_success);
        successTV.setVisibility(View.GONE);

        //EditText
        emailET = findViewById(R.id.tv_login_email);
        passwordET = findViewById(R.id.tv_login_password);

        //Button
        loginBTN = findViewById(R.id.btn_login);
    }
}
