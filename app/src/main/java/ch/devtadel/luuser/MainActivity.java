package ch.devtadel.luuser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class MainActivity extends AppCompatActivity {

    private CardView newCheckCV;
    private CardView schoolListCV;
    private CardView checkListCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkListCV = findViewById(R.id.cv_search_check);
        checkListCV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
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
}
