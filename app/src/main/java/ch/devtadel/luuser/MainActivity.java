package ch.devtadel.luuser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CardView newCheckCV;
    private CardView schoolListCV;
    private CardView checkListCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkListCV = findViewById(R.id.cv_search_check);
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
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                return true;
            case R.id.navigation_test2:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra(ProfileActivity.ME, true));
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        Toast.makeText(getBaseContext(), "Sign Out clicked!", Toast.LENGTH_LONG).show();
    }

    public void toInfo(View view){
        startActivity(new Intent(MainActivity.this, InfoActivity.class));
    }
}
