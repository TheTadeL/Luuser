package ch.devtadel.luuser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    public static final String VERSION_NR = "0.2.0";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //UP-Button hinzufügen
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupContentViews();

        TextView versionTV = findViewById(R.id.tv_version_nr);

        versionTV.setText("Luuser v."+VERSION_NR);
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

    private void setupContentViews(){
        CardView toWebsiteCV = findViewById(R.id.cv_info_to_website);
        toWebsiteCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tadel.ch"));
                startActivity(browserIntent);
            }
        });

        Button toBugReportBTN = findViewById(R.id.btn_info_to_bug_report);
        toBugReportBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InfoActivity.this, ContactActivity.class));
            }
        });
    }
}
