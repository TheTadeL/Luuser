package ch.devtadel.luuser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    public static final String EMAIL = "email";

    private Button loginBTN;
    private EditText emailET;
    private EditText passwordET;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        emailET = findViewById(R.id.tv_login_email);
        passwordET = findViewById(R.id.tv_login_password);
        loginBTN = findViewById(R.id.btn_login);
        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                firebaseAuth.signInWithEmailAndPassword(email, password);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString(EMAIL) != null){
                emailET.setText(bundle.getString(EMAIL));
            }
        }
    }
}
