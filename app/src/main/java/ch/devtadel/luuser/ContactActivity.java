package ch.devtadel.luuser;

import android.animation.ValueAnimator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;

import ch.devtadel.luuser.helper.Animator;

public class ContactActivity extends AppCompatActivity {
    private Animator animator = new Animator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ActionBar actionBar =  getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setupContentViews();
        setTitle("Kontaktformular");

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

    public void toBeer(final View view){
        animator.animateCardPress((CardView)view);

    }

    public void toBugReport(View view){
        animator.animateCardPress((CardView)view);
    }

    public void toIdeas(View view){
        animator.animateCardPress((CardView)view);

    }

    private void setupContentViews(){

    }
}