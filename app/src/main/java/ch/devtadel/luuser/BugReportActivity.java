package ch.devtadel.luuser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.model.School;

public class BugReportActivity extends AppCompatActivity {
    private static String TAG = "BugReportActivity";
    public static String TYPE = "typ";
    public static String TYPE_BUG = "Bug";
    public static String TYPE_SUGGESTION = "Suggestion";
    public static String TYPE_BEER = "Beer";

    private String type = null;
    private FirebaseUser currentUser;

    private TextView TitleTV;
    private EditText descriptionET;
    private TextView descriptionLabel;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);
        Bundle bundle = getIntent().getExtras();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.type = bundle.getString(TYPE);

        TitleTV = findViewById(R.id.title_bug_report);
        descriptionET = findViewById(R.id.et_description);
        descriptionLabel = findViewById(R.id.tv_description_label);
        submitBtn = findViewById(R.id.btn_submit);

        setupActivity();
    }

    private void setupActivity() {
        switch (type) {
            case "Bug":
                TitleTV.setText(R.string.bug_report);
                descriptionLabel.setText(R.string.how_2_describe_error);
                submitBtn.setText(R.string.btn_submit_bug);
                break;
            case "Suggestion":
                TitleTV.setText(R.string.suggestion_report);
                descriptionLabel.setText(R.string.description_suggestion);
                submitBtn.setText(R.string.btn_submit_suggestion);
                break;
            case "Beer":
                TitleTV.setText(R.string.beer_title);
                descriptionET.setVisibility(View.GONE);
                descriptionLabel.setText("Comming Soon");
                submitBtn.setVisibility(View.GONE);
                break;
            default:
        }
    }

    public void submitForm(View view) {
        String DB = type.equals(TYPE_BUG) ? SchoolDao.DB_BUG : SchoolDao.DB_SUGGESTION;
        SchoolDao dao = new SchoolDao();
        dao.commitMessage(descriptionET.getText().toString(), DB, currentUser.getDisplayName());
        Toast.makeText(this, "Vielen Dank für Ihre Rückmeldung", Toast.LENGTH_LONG).show();
        this.startActivity(new Intent(BugReportActivity.this, MainActivity.class));
    }
}
