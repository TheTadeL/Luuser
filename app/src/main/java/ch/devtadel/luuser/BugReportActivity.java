package ch.devtadel.luuser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BugReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);

        setupContentViews();
        setTitle("Problem melden");
    }

    private void setupContentViews(){

    }
}
