package ch.devtadel.luuser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.model.School;

public class AddSchoolActivity extends AppCompatActivity {

    private EditText nameET;
    private EditText placeET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);

        //
        nameET = findViewById(R.id.et_new_school_name);
        placeET = findViewById(R.id.et_new_school_place);
        Button saveSchoolBTN = findViewById(R.id.btn_save_new_school);
        saveSchoolBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                School newSchool = new School(nameET.getText().toString(), placeET.getText().toString());

                SchoolDao dao = new SchoolDao();
                dao.createSchoolInFS(newSchool);
            }
        });
    }
}
